package ast.optimizations;

import java.util.LinkedList;

import ast.exp.Parent;

// Constant folding optimizations on an AST.

public class ConstFold implements ast.Visitor
{
  private ast.classs.T newClass;
  private ast.mainClass.T mainClass;
  private ast.method.T newmethod;
  private ast.stm.T newstm;
  private ast.exp.T newexp;
  public ast.program.T program;
  
  public ConstFold()
  {
    this.newClass = null;
    this.mainClass = null;
    this.program = null;
    this.newmethod=null;
    this.newexp=null;
    this.newstm=null;
    
  }
@Override
    public void visit(Parent e) {
        // TODO Auto-generated method stub
        e.accept(this);
    }
  // //////////////////////////////////////////////////////
  // 
  public String genId()
  {
    return util.Temp.next();
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.exp.Add e)
  {
	  ast.exp.T left,right;
	  left=e.left;
	  right=e.right;
	  String lefttype=left.getClass().getName();
	  String righttype=right.getClass().getName();
	  if(lefttype.equals("ast.exp.Num")&&righttype.equals("ast.exp.Num"))
	  {
		  ast.exp.Num leftnum=(ast.exp.Num)left;
		  ast.exp.Num rightnum=(ast.exp.Num)right;
		  int numl,mumr;
		  numl=leftnum.num;
		  mumr=rightnum.num;
		  int result=numl+mumr;
		  this.newexp=new ast.exp.Num(result);
	  }
	  else
	  {
		  e.left.accept(this);
		  left=this.newexp;
		  e.right.accept(this);
		  right=this.newexp;
		  this.newexp=new ast.exp.Add(left, right);
	  }
  }

  @Override
  public void visit(ast.exp.And e)
  {
	  ast.exp.T left=e.left;
	  ast.exp.T right=e.right;
	  String lefttype=left.getClass().getName();
	  String righttype=right.getClass().getName();
	  if((lefttype.equals("ast.exp.False")||lefttype.equals("ast.exp.True"))
			  &&right.equals("ast.exp.False")||righttype.equals("ast.exp.True"))
	  {
		  int count=0;
		  if(lefttype.equals("ast.exp.True"))
		  {
			  ++count;
			  if(righttype.equals("ast.exp.True"))
				  ++count;
			  
		  }
		  else if(righttype.equals("ast.exp.True"))
			  		++count;
		  if(count!=2)
			  this.newexp=new ast.exp.False();
		  else
			  this.newexp=new ast.exp.True();
	  }
	  else
	  {
		  e.left.accept(this);
		  left=this.newexp;
		  e.right.accept(this);
		  right=this.newexp;
		  this.newexp=new ast.exp.And(left, right);
	  }
	  return;
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
	  e.array.accept(this);
	  ast.exp.T array=this.newexp;
	  e.index.accept(this);
	  ast.exp.T index=this.newexp;
	  this.newexp=new ast.exp.ArraySelect(array, index);
	  return;
  }

  @Override
  public void visit(ast.exp.Call e)
  {
	this.newexp=e;
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
	  this.newexp=e;
  }

  @Override
  public void visit(ast.exp.Id e)
  {
	  this.newexp=e;
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
	  this.newexp=e;
	  return;
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
	ast.exp.T left=e.left;
	ast.exp.T right=e.right;
	String lefttype=left.getClass().getName();
	String righttype=right.getClass().getName();
	if(lefttype.equals("ast.exp.Num")&&righttype.equals("ast.exp.Num"))
	{
		ast.exp.Num leftNum=(ast.exp.Num)left;
		int numl=leftNum.num;
		ast.exp.Num rightNum=(ast.exp.Num)right;
		int numr=rightNum.num;
		if(numl<numr)
			this.newexp=new ast.exp.True();
		else
			this.newexp=new ast.exp.False();
	}
	else
	{
		e.left.accept(this);
		left=this.newexp;
		e.right.accept(this);
		right=this.newexp;
		this.newexp=new ast.exp.Lt(left, right);
		return;
	}
   
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
	  this.newexp=e;
	  return;
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
	  this.newexp=e;
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
	  ast.exp.T notexp=e.exp;
	  String noteexptype=notexp.getClass().getName();
	  if(noteexptype.equals("ast.exp.True"))
		  this.newexp=new ast.exp.False();
	  else if(noteexptype.equals("ast.exp.False"))
		  this.newexp=new ast.exp.True();
	  else
	  {
		  e.exp.accept(this);
		  notexp=this.newexp;
		  this.newexp=new ast.exp.Not(notexp);
	  }
	  return;
  }

  @Override
  public void visit(ast.exp.Num e)
  {
	  this.newexp=e;
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {
	  ast.exp.T left=e.left;
	  String lefttype=left.getClass().getName();
	  ast.exp.T right=e.right;
	  String righttype=right.getClass().getName();
	  if(lefttype.equals("ast.exp.Num")&&righttype.equals("ast.exp.Num"))
	  {
		  ast.exp.Num leftNum=(ast.exp.Num)left;
		  int numl=leftNum.num;
		  ast.exp.Num rightNum=(ast.exp.Num)right;
		  int numr=rightNum.num;
		  int result=numl-numr;
		  this.newexp=new ast.exp.Num(result);
	  }
	  else
	  {
		  e.left.accept(this);
		  left=this.newexp;
		  e.right.accept(this);
		  right=this.newexp;
		  this.newexp=new ast.exp.Sub(left, right);
	  }
    return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
	  this.newexp=e;
    return;
  }

  @Override
  public void visit(ast.exp.Times e)
  {
	  ast.exp.T left=e.left;
	  String lefttype=left.getClass().getName();
	  ast.exp.T right=e.right;
	  String righttype=right.getClass().getName();
	  if(lefttype.equals("ast.exp.Num")&&righttype.equals("ast.exp.Num"))
	  {
		  ast.exp.Num leftNum=(ast.exp.Num)left;
		  int numl=leftNum.num;
		  ast.exp.Num rightNum=(ast.exp.Num)right;
		  int numr=rightNum.num;
		  int result=numl*numr;
		  this.newexp=new ast.exp.Num(result);
	  }
	  else
	  {
		  e.left.accept(this);
		  left=this.newexp;
		  e.right.accept(this);
		  right=this.newexp;
		  this.newexp=new ast.exp.Sub(left, right);
	  }
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	  this.newexp=e;
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    s.exp.accept(this);
    ast.exp.T expassign=this.newexp;
    this.newstm=new ast.stm.Assign(s.id, expassign);
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	  s.exp.accept(this);
	  ast.exp.T expassignarray=this.newexp;
	  s.index.accept(this);
	  ast.exp.T indexassignarray=this.newexp;
	  this.newstm=new ast.stm.AssignArray(s.id,indexassignarray, expassignarray);
	  return;
  }

  @Override
  public void visit(ast.stm.Block s)
  {
	  LinkedList<ast.stm.T> stmsblock=new LinkedList<ast.stm.T>();
	  for(ast.stm.T stm:s.stms)
	  {
		  stm.accept(this);
		  stmsblock.add(this.newstm);
	  }
	  this.newstm=new ast.stm.Block(stmsblock);
	  return;
  }

  @Override
  public void visit(ast.stm.If s)
  {
    s.condition.accept(this);
    ast.exp.T conditionif=this.newexp;
    s.thenn.accept(this);
    ast.stm.T thennif=this.newstm;
    s.elsee.accept(this);
    ast.stm.T elseif=this.newstm;
    this.newstm=new ast.stm.If(conditionif, thennif, elseif);
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
	  s.exp.accept(this);
	  this.newstm=new ast.stm.Print(this.newexp);
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
	  s.condition.accept(this);
	  ast.exp.T conditonwhile=this.newexp;
	  s.body.accept(this);
	  this.newstm=new ast.stm.While(conditonwhile, this.newstm);
	  return;
  }

  // type
  @Override
  public void visit(ast.type.Boolean t)
  {
  }

  @Override
  public void visit(ast.type.Class t)
  {
  }

  @Override
  public void visit(ast.type.Int t)
  {
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
    return;
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
	LinkedList<ast.stm.T>newstms=new LinkedList<ast.stm.T>();
	for(ast.stm.T stm:m.stms)
	{
		ast.stm.T current=stm;
		current.accept(this);
		newstms.add(this.newstm);
	}
	m.retExp.accept(this);
	ast.exp.T ret=this.newexp;
	this.newmethod=new ast.method.Method(m.retType, m.id, m.formals, m.locals, newstms, ret);
	
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
    LinkedList<ast.method.T> newmethods=new LinkedList<ast.method.T>();
    for(ast.method.T newmethod:c.methods)
    {
    	newmethod.accept(this);
    	newmethods.add(this.newmethod);
    }
    this.newClass=new ast.classs.Class(c.id, c.extendss, c.decs, newmethods);
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass c)
  {
    c.stm.accept(this);
    this.mainClass=new ast.mainClass.MainClass(c.id, c.arg, this.newstm);
    return;
  }

  // program
  @Override
  public void visit(ast.program.Program p)
  {
    
 // You should comment out this line of code:
    this.program = p;
    p.mainClass.accept(this);
    
    LinkedList<ast.classs.T> newclasss=new LinkedList<ast.classs.T>();
    for(ast.classs.T classs:p.classes)
    {
    	classs.accept(this);
    	newclasss.add(this.newClass);
    }
    
    this.program=new ast.program.Program(this.mainClass, newclasss);

    if (control.Control.isTracing("ast.ConstFold")){
      System.out.println("before optimization:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      p.accept(pp);
      System.out.println("after optimization:");
      this.program.accept(pp);
    }
    return;
  }
}
