package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Getfield extends T
{
	public String classname;
	public String fieldname;
	public codegen.bytecode.type.T type;
	
	public Getfield(String classname,String fieldname,codegen.bytecode.type.T type)
	{
		this.classname=classname;
		this.fieldname=fieldname;
		this.type=type;
	}
	
	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}

}
