package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Putfield extends T
{
	public String classname;
	public String field;
	public codegen.bytecode.type.T type;
	public Putfield(String classname,String field,codegen.bytecode.type.T type)
	{
		this.classname=classname;
		this.field=field;
		this.type=type;
	}
	
	@Override 
	public void accept(Visitor v)
	{
		v.visit(this);
	}

}
