package ast.optimizations;

import java.util.LinkedList;

import ast.exp.Parent;

// Dead code elimination optimizations on an AST.

public class DeadCode implements ast.Visitor
{
  private ast.method.T newmethod;
  private ast.classs.T newClass;
  private ast.stm.T newstm;
  private ast.mainClass.T mainClass;
  public ast.program.T program;
  
  public ast.stm.T resultBlock;
  @Override
    public void visit(Parent e) {
        // TODO Auto-generated method stub
        
    }
  public DeadCode()
  {
	this.newmethod=null;
    this.newClass = null;
    this.mainClass = null;
    this.program = null;
    this.newstm=null;
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
  }

  @Override
  public void visit(ast.exp.And e)
  {
	
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
  }

  @Override
  public void visit(ast.exp.Call e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
	 
	  return;
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
	  
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	   
	  return;
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
	    
    this.newstm=s;
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	this.newstm=s;    
  }

  @Override
  public void visit(ast.stm.Block s)
  {
	  LinkedList<ast.stm.T>newstms=new LinkedList<ast.stm.T>();   
	  for(ast.stm.T stm:s.stms)
	  {
		  stm.accept(this);
		  if(this.newstm==null)
			  ;
		  else
			  newstms.add(this.newstm);
	  }
	  this.newstm=new ast.stm.Block(newstms);
	  return;
  }

  @Override
  public void visit(ast.stm.If s)
  {
    ast.exp.T conditonif=s.condition;
    String conditontype=conditonif.getClass().getName();
    if(conditontype.equals("ast.exp.True"))
    {
    	s.thenn.accept(this);
    }
    else if(conditontype.equals("ast.exp.False"))
    {
    	s.elsee.accept(this);
    }
    else
    {
    	s.thenn.accept(this);
    	ast.stm.T thennn=this.newstm;
    	s.elsee.accept(this);
    	ast.stm.T elseee=this.newstm;
    	this.newstm=new ast.stm.If(s.condition, thennn, elseee);
    }
    
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
	this.newstm=s;  
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
	  ast.exp.T conditionwhile=s.condition;
	  String conditiontype=conditionwhile.getClass().getName();
	  if(conditiontype.equals("ast.exp.False"))
		  this.newstm=null;
	  else
	  {
		  s.body.accept(this);
		  this.newstm=new ast.stm.While(s.condition, this.newstm);
	  }
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
	LinkedList<ast.stm.T> newstms=new LinkedList<ast.stm.T>();
	for(ast.stm.T stm:m.stms)
	{
		stm.accept(this);
		if(this.newstm==null)
			;
		else
			newstms.add(this.newstm);
	}
	this.newmethod=new ast.method.Method(m.retType, m.id, m.formals, m.locals, newstms, m.retExp);
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
	LinkedList<ast.method.T> newmethod=new LinkedList<ast.method.T>();  
    for(ast.method.T method:c.methods)
    {
    	method.accept(this);
    	newmethod.add(this.newmethod);
    }
    this.newClass=new ast.classs.Class(c.id, c.extendss, c.decs, newmethod);
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
	  this.program=p;
	
	  ast.mainClass.MainClass mainclass=(ast.mainClass.MainClass)p.mainClass;
	    
	  mainclass.accept(this);
	    
	  LinkedList<ast.classs.T> newclasses=new LinkedList<ast.classs.T>();
	  for(ast.classs.T classs:p.classes)
	    {
	    	classs.accept(this);
	    	newclasses.add(this.newClass);
	    }
	    
	 this.program=new ast.program.Program(this.mainClass, newclasses);

    if (control.Control.isTracing("ast.DeadCode")){
      System.out.println("before optimization:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      p.accept(pp);
      System.out.println("after optimization:");
      this.program.accept(pp);
    }
    return;
  }
}
