package parser;

import java.util.LinkedList;

import ast.stm.T;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser
{
  Lexer lexer;
  Token current;
  LinkedList<Token> queuekindbuffer=new LinkedList<Token>();
  
  
 
  
  
 
  static ast.classs.Class fac = new ast.classs.Class("F", null,
	      new util.Flist<ast.dec.T>().addAll(),
	      new util.Flist<ast.method.T>().addAll(new ast.method.Method(
	          new ast.type.Int(), "ComputeFac", new util.Flist<ast.dec.T>()
	              .addAll(new ast.dec.Dec(new ast.type.Int(), "num")),
	          new util.Flist<ast.dec.T>().addAll(new ast.dec.Dec(
	              new ast.type.Int(), "num_aux")), new util.Flist<ast.stm.T>()
	              .addAll(new ast.stm.If(new ast.exp.Lt(new ast.exp.Id("num"),
	                  new ast.exp.Num(1)), new ast.stm.Assign("num_aux",
	                  new ast.exp.Num(1)), new ast.stm.Assign("num_aux",
	                  new ast.exp.Times(new ast.exp.Id("num"), new ast.exp.Call(
	                      new ast.exp.This(), "ComputeFac",
	                      new util.Flist<ast.exp.T>().addAll(new ast.exp.Sub(
	                          new ast.exp.Id("num"), new ast.exp.Num(1)))))))),
	          new ast.exp.Id("num_aux"))));
  public Parser(String fname, java.io.BufferedInputStream fstream)
  {
    lexer = new Lexer(fname, fstream);
    current=lexer.nextToken();
    while(current.kind == Kind.TOKEN_NOTE)
    {
    	current = lexer.nextToken();
    }
  }

  // /////////////////////////////////////////////
  // utility methods to connect the lexer
  // and the parser.

  private void advance()
  {
	  if(queuekindbuffer.isEmpty())
	    	current = lexer.nextToken();
	  else
	    	current=queuekindbuffer.removeFirst();
    while(current.kind == Kind.TOKEN_NOTE)
    {
    	current = lexer.nextToken();
    }
  }

  private Token eatToken(Kind kind)
  {
	Token tempstore=current;
    if (kind == current.kind)
      advance();
    else {
      System.out.println("Expects: " + kind.toString());
      System.out.println("But got: " + current.kind.toString());
      System.exit(1);
    }
    return tempstore;
  }

  private void error()
  {
    System.out.println("Syntax error: compilation aborting...\n");
    System.exit(1);
    return;
  }

  // ////////////////////////////////////////////////////////////
  // below are method for parsing.

  // A bunch of parsing methods to parse expressions. The messy
  // parts are to deal with precedence and associativity.

  // ExpList -> Exp ExpRest*
  // ->
  // ExpRest -> , Exp
  private java.util.LinkedList<ast.exp.T> parseExpList()
  {
	LinkedList<ast.exp.T> queueexp=new LinkedList<ast.exp.T>();
    if (current.kind == Kind.TOKEN_RPAREN)
      return queueexp;
    queueexp.add(parseExp());
    while (current.kind == Kind.TOKEN_COMMER) {
      advance();
    queueexp.add(parseExp());
    }
    return queueexp;
  }

  // AtomExp -> (exp)
  // -> INTEGER_LITERAL
  // -> true
  // -> false
  // -> this
  // -> id
  // -> new int [exp]
  // -> new id ()
  private ast.exp.T parseAtomExp()
  {
	Token temp;
    switch (current.kind) {
    case TOKEN_LPAREN:
      advance();
      ast.exp.T exp=parseExp();
      eatToken(Kind.TOKEN_RPAREN);
      return new ast.exp.Parent(exp);
    case TOKEN_NUM:
      temp=current;
      advance();
      return new ast.exp.Num(Integer.valueOf(temp.lexeme));
    case TOKEN_TRUE:
      advance();
      return new ast.exp.True();
    case TOKEN_FALSE:
    	advance();
    	return new ast.exp.False();
    case TOKEN_THIS:
      advance();
      return new ast.exp.This();
    case TOKEN_ID:
      temp=current;
      advance();
      return new ast.exp.Id(temp.lexeme);
    case TOKEN_NEW: {
      advance();
      switch (current.kind) {
      case TOKEN_INT:
        advance();
        eatToken(Kind.TOKEN_LBRACK);
        ast.exp.T Exp=parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        return new ast.exp.NewIntArray(Exp);
      case TOKEN_ID:
    	temp=current;
        advance();
        eatToken(Kind.TOKEN_LPAREN);
        eatToken(Kind.TOKEN_RPAREN);
        return new ast.exp.NewObject(temp.lexeme);
      default:
        error();
        return null;
      }
    }
    default:
      error();
      return null;
    }
  }

  // NotExp -> AtomExp
  // -> AtomExp .id (expList)
  // -> AtomExp [exp]
  // -> AtomExp .length
  private ast.exp.T parseNotExp()
  {
	Token temp;
    ast.exp.T AtomExp=parseAtomExp();
    while (current.kind == Kind.TOKEN_DOT || current.kind == Kind.TOKEN_LBRACK) {
      if (current.kind == Kind.TOKEN_DOT) {
        advance();
        if (current.kind == Kind.TOKEN_LENGTH) {
          advance();
          return new ast.exp.Length(AtomExp);
        }
        temp=current;
        eatToken(Kind.TOKEN_ID);
        eatToken(Kind.TOKEN_LPAREN);
        java.util.LinkedList<ast.exp.T> args=parseExpList();
        eatToken(Kind.TOKEN_RPAREN);
        return new ast.exp.Call(AtomExp, temp.lexeme, args);
      } else {
        advance();
        ast.exp.T index=parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        return new ast.exp.ArraySelect(AtomExp, index);
      }
    }
    return AtomExp;
  }

  // TimesExp -> ! TimesExp
  // -> NotExp
  private ast.exp.T parseTimesExp()
  {
    while (current.kind == Kind.TOKEN_NOT) {
      advance();
      ast.exp.T exp=parseTimesExp();
      return new ast.exp.Not(exp);
    }
    ast.exp.T notexp=parseNotExp();
    return notexp;
  }

  // AddSubExp -> TimesExp * TimesExp
  // -> TimesExp
  private ast.exp.T parseAddSubExp()
  {
	ast.exp.T left,right;
    left=parseTimesExp();
    right=null;
    while (current.kind == Kind.TOKEN_TIMES) {
      advance();
      right=parseTimesExp();
      return new ast.exp.Times(left, right);
    }
    return left;
  }

  // LtExp -> AddSubExp + AddSubExp
  // -> AddSubExp - AddSubExp
  // -> AddSubExp
  private ast.exp.T parseLtExp()
  {
	ast.exp.T left,right;
	left=parseAddSubExp();
	right=null;
	while(current.kind==Kind.TOKEN_ADD||current.kind==Kind.TOKEN_SUB)
	{
		if(current.kind==Kind.TOKEN_ADD)
		{
			advance();
			right=parseAddSubExp();
			return new ast.exp.Add(left, right);
		}
		else if(current.kind==Kind.TOKEN_SUB)
		{
			advance();
			right=parseAddSubExp();
			return new ast.exp.Sub(left, right);
		}
	}
    return left;
  }

  // AndExp -> LtExp < LtExp
  // -> LtExp
  private ast.exp.T parseAndExp()
  {
    ast.exp.T left,right;
    left=parseLtExp();
    right=null;
    while (current.kind == Kind.TOKEN_LT) {
      advance();
      right=parseLtExp();
      return new ast.exp.Lt(left, right);
    }
    return left;
  }

  // Exp -> AndExp && AndExp
  // -> AndExp
  private ast.exp.T parseExp()
  {
    ast.exp.T left,right;
    left=parseAndExp();
    right=null;
    while (current.kind == Kind.TOKEN_AND) {
      advance();
      right=parseAndExp();
      return new ast.exp.And(left, right);
    }
    return left;
  }

  // Statement -> { Statement* }
  // -> if ( Exp ) Statement else Statement
  // -> while ( Exp ) Statement
  // -> System.out.println ( Exp ) ;
  // -> id = Exp ;
  // -> id [ Exp ]= Exp ;
  private ast.stm.T parseStatement()
  {
    switch(current.kind){
    case TOKEN_LBRACE:
    	advance();
    	LinkedList<ast.stm.T> block=parseStatements();
    	eatToken(Kind.TOKEN_RBRACE);
    	return new ast.stm.Block(block);
    case TOKEN_IF:
    	advance();
    	eatToken(Kind.TOKEN_LPAREN);
    	ast.exp.T exp=parseExp();
    	eatToken(Kind.TOKEN_RPAREN);
    	ast.stm.T statement1=parseStatement();
    	eatToken(Kind.TOKEN_ELSE);
    	ast.stm.T statement2=parseStatement();
    	return new ast.stm.If(exp, statement1, statement2);
    case TOKEN_WHILE:
    	advance();
    	eatToken(Kind.TOKEN_LPAREN);
    	ast.exp.T exp1=parseExp();
    	eatToken(Kind.TOKEN_RPAREN);
    	ast.stm.T statement11=parseStatement();
    	return new ast.stm.While(exp1, statement11);
    case TOKEN_SYSTEM:
    	advance();
    	eatToken(Kind.TOKEN_DOT);
    	eatToken(Kind.TOKEN_OUT);
    	eatToken(Kind.TOKEN_DOT);
    	eatToken(Kind.TOKEN_PRINTLN);
    	eatToken(Kind.TOKEN_LPAREN);
    	ast.exp.T exp11=parseExp();
    	eatToken(Kind.TOKEN_RPAREN);
    	eatToken(Kind.TOKEN_SEMI);
    	return new ast.stm.Print(exp11);
    case TOKEN_ID:
    	String id=current.lexeme;
    	advance();
    	switch(current.kind){
    	case TOKEN_ASSIGN:
    		advance();
    		ast.exp.T exp111=parseExp();
    		eatToken(Kind.TOKEN_SEMI);
    		return new ast.stm.Assign(id, exp111);
    	case TOKEN_LBRACK:
    		advance();
    		ast.exp.T index=parseExp();
    		eatToken(Kind.TOKEN_RBRACK);
    		eatToken(Kind.TOKEN_ASSIGN);
    		ast.exp.T exp1111=parseExp();
    		eatToken(Kind.TOKEN_SEMI);
    		return new ast.stm.AssignArray(id, index, exp1111);
    	default:
    		error();
    		return null;
    	}
    default:
    	error();
    	return null;
    }
   }

  // Statements -> Statement Statements
  // ->
  private java.util.LinkedList<ast.stm.T> parseStatements()
  {
	LinkedList<ast.stm.T> queuestm=new LinkedList<ast.stm.T>();
    while (current.kind == Kind.TOKEN_LBRACE || current.kind == Kind.TOKEN_IF
        || current.kind == Kind.TOKEN_WHILE
        || current.kind == Kind.TOKEN_SYSTEM || current.kind == Kind.TOKEN_ID) {
      queuestm.add(parseStatement());
    }
    return queuestm;
  }

  // Type -> int []
  // -> boolean
  // -> int
  // -> id
  private ast.type.T parseType()
  {
    switch(current.kind){
    case TOKEN_INT:
    	advance();
    	if(current.kind==Kind.TOKEN_LBRACK){
    		advance();
    		eatToken(Kind.TOKEN_RBRACK);
    		return new ast.type.IntArray();
    	}
    	return new ast.type.Int();
    case TOKEN_BOOLEAN:
    	advance();
    	return new ast.type.Boolean();
    case TOKEN_ID:
    	String temp=current.lexeme;
    	advance();
    	return new ast.type.Class(temp);
    default:
    	error();
    	return null;
    }
  }

  // VarDecl -> Type id ;
  private ast.dec.T parseVarDecl()
  {
    // to parse the "Type" nonterminal in this method, instead of writing
    // a fresh one.
	Token temp;
    ast.type.T type=parseType();
    temp=current;
    eatToken(Kind.TOKEN_ID);
    eatToken(Kind.TOKEN_SEMI);
    return new ast.dec.Dec(type, temp.lexeme);
  }

  // VarDecls -> VarDecl VarDecls
  // ->
  private java.util.LinkedList<ast.dec.T> parseVarDecls()
  {
	LinkedList<ast.dec.T> queuedec=new LinkedList<ast.dec.T>();
    while (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
        || current.kind == Kind.TOKEN_ID) {
    	queuekindbuffer.addLast(current);
    	current=lexer.nextToken();
    	while(current.kind==Kind.TOKEN_NOTE){
    		current=lexer.nextToken();
    	}
    	queuekindbuffer.addLast(current);
    	if(current.kind==Kind.TOKEN_ASSIGN||current.kind==Kind.TOKEN_LBRACK){
    		current=queuekindbuffer.removeFirst();
    		if(current.kind==Kind.TOKEN_INT)
    			queuedec.add(parseVarDecl());
    		else
    			return queuedec;
    	}
    	else{
    		current=queuekindbuffer.removeFirst();
    		queuedec.add(parseVarDecl());
    	}
    }
    return queuedec;
  }

  // FormalList -> Type id FormalRest*
  // ->
  // FormalRest -> , Type id
  private LinkedList<ast.dec.T> parseFormalList()
  {
	LinkedList<ast.dec.T> queueformlist=new LinkedList<ast.dec.T>();
	ast.dec.T formallist;
	Token temp;
    while(current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
        || current.kind == Kind.TOKEN_ID) {
      ast.type.T type=parseType();
      temp=current;
      eatToken(Kind.TOKEN_ID);
      formallist=new ast.dec.Dec(type, temp.lexeme);
      queueformlist.add(formallist);
      while (current.kind == Kind.TOKEN_COMMER) {
        advance();
      }
    }
    return queueformlist;
  }

  // Method -> public Type id ( FormalList )
  // { VarDecl* Statement* return Exp ;}
  private ast.method.T parseMethod()
  {
    eatToken(Kind.TOKEN_PUBLIC);
    ast.type.T type=parseType();
    Token temp=current;
    eatToken(Kind.TOKEN_ID);
    eatToken(Kind.TOKEN_LPAREN);
    LinkedList<ast.dec.T> formallist=parseFormalList();
    eatToken(Kind.TOKEN_RPAREN);
    eatToken(Kind.TOKEN_LBRACE);
    LinkedList<ast.dec.T> vardecls=parseVarDecls();
    LinkedList<ast.stm.T> statements=parseStatements();
    eatToken(Kind.TOKEN_RETURN);
    ast.exp.T exp=parseExp();
    eatToken(Kind.TOKEN_SEMI);
    eatToken(Kind.TOKEN_RBRACE);
    return new ast.method.Method(type, temp.lexeme, formallist, vardecls, statements, exp);
  }

  // MethodDecls -> MethodDecl MethodDecls
  // ->
  private java.util.LinkedList<ast.method.T> parseMethodDecls()
  {
	LinkedList<ast.method.T> queuemethod=new LinkedList<ast.method.T>();
    while (current.kind == Kind.TOKEN_PUBLIC) {
      queuemethod.add(parseMethod());
    }
    return queuemethod;
  }

  // ClassDecl -> class id { VarDecl* MethodDecl* }
  // -> class id extends id { VarDecl* MethodDecl* }
  private ast.classs.T parseClassDecl()
  {
	Token temp;
	String extendss=null;
    eatToken(Kind.TOKEN_CLASS);
    temp=current;
    eatToken(Kind.TOKEN_ID);
    if (current.kind == Kind.TOKEN_EXTENDS) {
      eatToken(Kind.TOKEN_EXTENDS);
      extendss=current.lexeme;
      eatToken(Kind.TOKEN_ID);
    }
    eatToken(Kind.TOKEN_LBRACE);
    LinkedList<ast.dec.T> decs=parseVarDecls();
    LinkedList<ast.method.T> methods=parseMethodDecls();
    eatToken(Kind.TOKEN_RBRACE);
    return new ast.classs.Class(temp.lexeme, extendss, decs, methods);
  }

  // ClassDecls -> ClassDecl ClassDecls
  // ->
  private java.util.LinkedList<ast.classs.T> parseClassDecls()
  {
	LinkedList<ast.classs.T> queueclass=new LinkedList<ast.classs.T>();
    while (current.kind == Kind.TOKEN_CLASS) {
      queueclass.add(parseClassDecl());
    }
    return queueclass;
  }

  // MainClass -> class id
  // {
  // public static void main ( String [] id )
  // {
  // Statement
  // }
  // }
  private ast.mainClass.T parseMainClass()
  {
    eatToken(Kind.TOKEN_CLASS);
    String id=eatToken(Kind.TOKEN_ID).lexeme;
    System.out.println(id);
    eatToken(Kind.TOKEN_LBRACE);
    eatToken(Kind.TOKEN_PUBLIC);
    eatToken(Kind.TOKEN_STATIC);
    eatToken(Kind.TOKEN_VOID);
    eatToken(Kind.TOKEN_MAIN);
    eatToken(Kind.TOKEN_LPAREN);
    eatToken(Kind.TOKEN_STRING);
    eatToken(Kind.TOKEN_LBRACK);
    eatToken(Kind.TOKEN_RBRACK);
    String arg=eatToken(Kind.TOKEN_ID).lexeme;
    System.out.println(arg);
    eatToken(Kind.TOKEN_RPAREN);
    eatToken(Kind.TOKEN_LBRACE);
    ast.stm.T statement=parseStatement();
    eatToken(Kind.TOKEN_RBRACE);
    eatToken(Kind.TOKEN_RBRACE);
    return new ast.mainClass.MainClass(id, arg, statement);
  }

  // Program -> MainClass ClassDecl*
  private ast.program.T parseProgram()
  {
   // eatToken(Kind.TOKEN_EOF);
    return new ast.program.Program(parseMainClass(),parseClassDecls());
  }

  public ast.program.T parse()
  {
    return parseProgram();
  }
}
