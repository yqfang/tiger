package cfg.block;

import cfg.Visitor;

public class Block extends T
{
  public util.Label label;
  public java.util.LinkedList<cfg.stm.T> stms;
  public cfg.transfer.T transfer;
  public int count;
  
  public Block(util.Label label,
      java.util.LinkedList<cfg.stm.T> stms,
      cfg.transfer.T transfer)
  {
    this.label = label;
    this.stms = stms;
    this.transfer = transfer;
    this.count=0;
  }

  @Override
  public boolean equals(Object o)
  {
    if (o==null)
      return false;
    
    if (!(o instanceof Block))
      return false;
    
    Block ob = (Block)o;
    return this.label.equals(ob.label);
  }
  
  @Override
  public String toString()
  {
    StringBuffer strb = new StringBuffer();
    strb.append(this.label.toString()+":\\n");
    // Lab5. Your code here:
    //strb.append("Your code here:\\n");
    for(cfg.stm.T stm:this.stms)
    {
    	String type=stm.getClass().getName();
    	switch(type)
    	{
    		case "cfg.stm.Sub":
	    	{
	    		cfg.stm.Sub temp = (cfg.stm.Sub)(stm);
	        	strb.append(temp.dst+" = ");
	        	String l = temp.left.getClass().getName();
	        	String r = temp.right.getClass().getName();
	        	if(l == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var le = (cfg.operand.Var)(temp.left);
	        		strb.append(le.id+" - ");
	        	}
	        	if(l == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int le = (cfg.operand.Int)(temp.left);
	        		strb.append(le.i+" - ");
	        	}
	        	if(r == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var ri = (cfg.operand.Var)(temp.right);
	        		strb.append(ri.id+";\\n");
	        	}
	        	if(r == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int ri = (cfg.operand.Int)(temp.right);
	        		strb.append(ri.i+";\\n");
	        	}
	    	}break;
    		case "cfg.stm.Add":
	    	{
	    		cfg.stm.Add temp = (cfg.stm.Add)(stm);
	        	strb.append(temp.dst+" = ");
	        	String l = temp.left.getClass().getName();
	        	String r = temp.right.getClass().getName();
	        	if(l == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var le = (cfg.operand.Var)(temp.left);
	        		strb.append(le.id+" + ");
	        	}
	        	if(l == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int le = (cfg.operand.Int)(temp.left);
	        		strb.append(le.i+" + ");
	        	}
	        	if(r == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var ri = (cfg.operand.Var)(temp.right);
	        		strb.append(ri.id+";\\n");
	        	}
	        	if(r == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int ri = (cfg.operand.Int)(temp.right);
	        		strb.append(ri.i+";\\n");
	        	}
	    	}break;
    		case "cfg.stm.Times":
	    	{
	    		cfg.stm.Times temp = (cfg.stm.Times)(stm);
	        	strb.append(temp.dst+" = ");
	        	String l = temp.left.getClass().getName();
	        	String r = temp.right.getClass().getName();
	        	if(l == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var le = (cfg.operand.Var)(temp.left);
	        		strb.append(le.id+" * ");
	        	}
	        	if(l == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int le = (cfg.operand.Int)(temp.left);
	        		strb.append(le.i+" * ");
	        	}
	        	if(r == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var ri = (cfg.operand.Var)(temp.right);
	        		strb.append(ri.id+";\\n");
	        	}
	        	if(r == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int ri = (cfg.operand.Int)(temp.right);
	        		strb.append(ri.i+";\\n");
	        	}
	    	}break;
    		case "cfg.stm.Lt":
	    	{
	    		cfg.stm.Lt temp = (cfg.stm.Lt)(stm);
	        	strb.append(temp.dst+" = ");
	        	String l = temp.left.getClass().getName();
	        	String r = temp.right.getClass().getName();
	        	if(l == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var le = (cfg.operand.Var)(temp.left);
	        		strb.append(le.id+" < ");
	        	}
	        	if(l == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int le = (cfg.operand.Int)(temp.left);
	        		strb.append(le.i+" < ");
	        	}
	        	if(r == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var ri = (cfg.operand.Var)(temp.right);
	        		strb.append(ri.id+";\\n");
	        	}
	        	if(r == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int ri = (cfg.operand.Int)(temp.right);
	        		strb.append(ri.i+";\\n");
	        	}
	    	}break;
    		case "cfg.stm.Move":
	    	{
	    		cfg.stm.Move temp = (cfg.stm.Move)(stm);
	        	strb.append(temp.dst+" = ");
	        	String s = temp.src.getClass().getName();
	        	if(s == "cfg.operand.Var")
	        	{
	        		cfg.operand.Var le = (cfg.operand.Var)(temp.src);
	        		strb.append(le.id+";\\n");
	        	}
	        	if(s == "cfg.operand.Int")
	        	{
	        		cfg.operand.Int le = (cfg.operand.Int)(temp.src);
	        		strb.append(le.i+";\\n");
	        	}
	    	}break;
    		case "cfg.stm.NewObject":
	    	{
	    		cfg.stm.NewObject temp = (cfg.stm.NewObject)(stm);
	        	strb.append(temp.dst+" = New ");
	        	strb.append(temp.c+"( );\\n");
	    	}break;
    		case "cfg.stm.NewIntArray":
    		{
    			cfg.stm.NewIntArray temp=(cfg.stm.NewIntArray)stm;
    			strb.append(temp.dst+"=(struct IntArray*)(Tiger_new_array(");
    			cfg.operand.T exp=temp.exp;
    			if(exp.getClass().getName().equals("cfg.operand.Int"))
    			{
    				cfg.operand.Int expInt=(cfg.operand.Int)exp;
    				strb.append(expInt.i);
    			}
    			else if(exp.getClass().getName().equals("cfg.operand.Var"))
    			{
    				cfg.operand.Var expVar=(cfg.operand.Var)exp;
    				strb.append(expVar.id);
    			}
    			strb.append("));\\n");
    		}break;
    		case "cfg.stm.InvokeVirtual":
	    	{
	    		cfg.stm.InvokeVirtual temp = (cfg.stm.InvokeVirtual)(stm);
	        	strb.append(temp.dst+" = "+temp.obj+"->vptr->"+temp.f+"( "+temp.obj+", ");
	            int Nar  =  temp.args.size();
	            cfg.operand.T temp_args = null;
	            for( int j = 0; j < Nar; j++)
	            {
	            	temp_args = temp.args.get(j);
	            	String s = temp_args.getClass().getName();
		        	if(s == "cfg.operand.Var")
		        	{
		        		cfg.operand.Var le = (cfg.operand.Var)(temp_args);
		        		strb.append(le.id);
		        	}
		        	if(s == "cfg.operand.Int")
		        	{
		        		cfg.operand.Int le = (cfg.operand.Int)(temp_args);
		        		strb.append(le.i);
		        	}
		        	if(j != Nar-1)
		        	{
		        		strb.append(", ");
		        	}
	            }
	        	strb.append(" );\\n");
	        }break;
    		case "cfg.stm.Print":
    			strb.append("System_out_println (");
    			cfg.stm.Print stmPrint=(cfg.stm.Print)stm;
    			String operandtype=stmPrint.arg.getClass().getName();
    			if(operandtype.equals("cfg.operand.Int"))
    			{
    				cfg.operand.Int int1Int=(cfg.operand.Int)stmPrint.arg;
    				strb.append(int1Int.i);
    			}
    			else
    			{
    				cfg.operand.Var var=(cfg.operand.Var)stmPrint.arg;
    				strb.append(var.id);
    			}
    			break;
    		default:
    			break;
    	}
    }
    
    
    return strb.toString();
  }
  
  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
