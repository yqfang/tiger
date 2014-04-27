package cfg.type;

import cfg.Visitor;

public class IntStar extends T
{
	public IntStar()
	{
		
	}
	
	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}
}