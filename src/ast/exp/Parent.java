package ast.exp;

public class Parent extends T
{
	public T exp;
	
	public Parent(T exp)
	{
		this.exp=exp;
	}
	
 @Override
 public void accept(ast.Visitor v)
 {
	v.visit(this);
	return;
 }
}