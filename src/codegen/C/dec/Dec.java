package codegen.C.dec;

import codegen.C.Visitor;

public class Dec extends T
{
  public codegen.C.type.T type;
  public String id;

  public codegen.C.type.T getType() {
	return type;
}

public void setType(codegen.C.type.T type) {
	this.type = type;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public Dec(codegen.C.type.T type, String id)
  {
    this.type = type;
    this.id = id;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
