package ast.optimizations;

import java.util.LinkedList;

import ast.exp.Parent;

// Algebraic simplification optimizations on an AST.

public class AlgSimp implements ast.Visitor
{
  private ast.classs.T newClass;
  private ast.mainClass.T mainClass;
  private ast.exp.T newexp;
  private ast.stm.T newstm;
  private ast.method.T newmethod;
  public ast.program.T program;
  @Override
    public void visit(Parent e) {
        // TODO Auto-generated method stub
        e.exp.accept(this);
    }
  public AlgSimp()
  {
    this.newClass = null;
    this.mainClass = null;
    this.program = null;
    this.newexp=null;
    this.newstm=null;
    this.newmethod=null;
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
	  ast.exp.T left=e.left;
	  ast.exp.T right=e.right;
	  String lefttype=left.getClass().getName();
	  String righttype=right.getClass().getName();
	  if(lefttype.equals("ast.exp.Num"))
	  {
		  ast.exp.Num leftNum=(ast.exp.Num)left;
		  if(leftNum.num==0)
		  {
			  this.newexp=right;
			  return;
		  }
	  }
	  if(righttype.equals("ast.exp.Num"))
	  {
		  ast.exp.Num rightNum=(ast.exp.Num)right;
		  if(rightNum.num==0)
		  {
			  this.newexp=left;
		  	  return;
		  }
	  }
	  e.left.accept(this);
	  left=this.newexp;
	  e.right.accept(this);
	  this.newexp=new ast.exp.Add(left, this.newexp);
	  return;
  }

  @Override
  public void visit(ast.exp.And e)
  {
	  ast.exp.T left=e.left;
	  ast.exp.T right=e.right;
	  String lefttype=left.getClass().getName();
	  String righttype=right.getClass().getName();
	  if(lefttype.equals("ast.exp.False")||righttype.equals("ast.exp.False"))
		  this.newexp=new ast.exp.False();
	  else
	  {
		  e.left.accept(this);
		  left=this.newexp;
		  e.right.accept(this);
		  this.newexp=new ast.exp.And(left, this.newexp);
	  }
	  return;
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
	  e.array.accept(this);
	  ast.exp.T array=this.newexp;
	  e.index.accept(this);
	  this.newexp=new ast.exp.ArraySelect(array, this.newexp);
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
	  e.left.accept(this);
	  ast.exp.T left=this.newexp;
	  e.right.accept(this);
	  this.newexp=new ast.exp.Lt(left, this.newexp);
	  return;
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
	  e.exp.accept(this);
	  this.newexp=new ast.exp.Not(this.newexp);
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
	 ast.exp.T right=e.right;
	 String lefttype=left.getClass().getName();
	 String righttype=right.getClass().getName();
	 if(righttype.equals("ast.exp.Num"))
	 {
		 ast.exp.Num rightNum=(ast.exp.Num)right;
		 if(rightNum.num==0)
		 {
			 this.newexp=left;
			 return;
		 }
	 }
	 if(righttype.equals("ast.exp.Id")&&lefttype.equals("ast.exp.Id"))
	 {
		 ast.exp.Id leftId=(ast.exp.Id)left;
		 ast.exp.Id rightId=(ast.exp.Id)right;
		 if(leftId.id.equals(rightId.id))
		 {
			 this.newexp=new ast.exp.Num(0);
			 return;
		 }
	 }
	 
		 e.left.accept(this);
		 left=this.newexp;
		 e.right.accept(this);
		 right=this.newexp;
		 this.newexp=new ast.exp.Sub(left, right);
	 
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
    ast.exp.T right=e.right;
    String lefttype=left.getClass().getName();
    String righttype=right.getClass().getName();
    if(lefttype.equals("ast.exp.Num"))
    {
    	ast.exp.Num lefNum=(ast.exp.Num)left;
    	if(lefNum.num==0)
    		this.newexp=new ast.exp.Num(0);
    	else if(lefNum.num<10)
    	{
    	
    		if(righttype.equals("ast.exp.Id")||righttype.equals("ast.exp.ArraySelect"))
    		{
    			int num=lefNum.num;
    			if(num>2)
    			{
    				num=num-1;
    				ast.exp.T addleft=right;
    				ast.exp.T addright=new ast.exp.Times(new ast.exp.Num(num), right);
    				addright.accept(this);
    				this.newexp=new ast.exp.Add(addleft, this.newexp);
    			}
    			else
    			{
    				this.newexp=new ast.exp.Add(right, right);
    			}
    		}
    	}
    	return;
    }
    if(righttype.equals("ast.exp.Num"))
    {
    	ast.exp.T change=new ast.exp.Times(right, left);
    	change.accept(this);
    	return;
    }
    e.left.accept(this);
    left=this.newexp;
    e.right.accept(this);
    right=this.newexp;
    this.newexp=new ast.exp.Times(left, right);
    
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	  this.newexp=e;
	  return;
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    s.exp.accept(this);
    this.newstm=new ast.stm.Assign(s.id, this.newexp);
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	  s.index.accept(this);
	  ast.exp.T indexs=this.newexp;
	  s.exp.accept(this);
	  ast.exp.T exps=this.newexp;
	  this.newstm=new ast.stm.AssignArray(s.id, indexs, exps);
	  return;
  }

  @Override
  public void visit(ast.stm.Block s)
  {
	  LinkedList<ast.stm.T> newstms=new LinkedList<ast.stm.T>();
	  for(ast.stm.T stm:s.stms)
	  {
		  stm.accept(this);
		  newstms.add(this.newstm);
	  }
	  this.newstm=new ast.stm.Block(newstms);
	  return;
  }

  @Override
  public void visit(ast.stm.If s)
  {
    s.condition.accept(this);
    ast.exp.T conditionif=this.newexp;
    s.thenn.accept(this);
    ast.stm.T thens=this.newstm;
    s.elsee.accept(this);
    ast.stm.T elses=this.newstm;
    this.newstm=new ast.stm.If(conditionif, thens, elses);
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
	  ast.exp.T conditionwhile=this.newexp;
	  s.body.accept(this);
	  this.newstm=new ast.stm.While(conditionwhile, this.newstm);
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
    for(ast.method.T method:c.methods)
    {
    	method.accept(this);
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
    LinkedList<ast.classs.T> newclasses=new LinkedList<ast.classs.T>();
    for(ast.classs.T classs:p.classes)
    {
    	classs.accept(this);
    	newclasses.add(this.newClass);
    }
    this.program=new ast.program.Program(this.mainClass, newclasses);

    if (control.Control.isTracing("ast.AlgSimp")){
      System.out.println("before optimization:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      p.accept(pp);
      System.out.println("after optimization:");
      this.program.accept(pp);
    }
    return;
  }
}
