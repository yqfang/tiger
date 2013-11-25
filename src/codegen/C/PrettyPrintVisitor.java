package codegen.C;

import control.Control;

public class PrettyPrintVisitor implements Visitor
{
  private int indentLevel;
  private java.io.BufferedWriter writer;

  public PrettyPrintVisitor()
  {
    this.indentLevel = 2;
  }

  private void indent()
  {
    this.indentLevel += 2;
  }

  private void unIndent()
  {
    this.indentLevel -= 2;
  }

  private void printSpaces()
  {
    int i = this.indentLevel;
    while (i-- != 0)
      this.say(" ");
  }

  private void sayln(String s)
  {
    say(s);
    try {
      this.writer.write("\n");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void say(String s)
  {
    try {
      this.writer.write(s);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(codegen.C.exp.Add e)
  {
	  e.left.accept(this);
	  this.say("+");
	  e.right.accept(this);
	  return;
  }

  @Override
  public void visit(codegen.C.exp.And e)
  {
	  e.left.accept(this);
	  this.say("&&");
	  e.right.accept(this);
	  return;
  }

  @Override
  public void visit(codegen.C.exp.ArraySelect e)
  {
	  this.say("*(");
	  e.array.accept(this);
	  this.say("->array+(");
	  e.index.accept(this);
	  this.say("))");
	  return;
  }

  @Override
  public void visit(codegen.C.exp.Call e)
  {
    this.say("(" + e.assign + "=");
    e.exp.accept(this);
    this.say(", ");
    this.say(e.assign + "->vptr->" + e.id + "(" + e.assign);
    int size = e.args.size();
    if (size == 0) {
      this.say("))");
      return;
    }
    for (codegen.C.exp.T x : e.args) {
      this.say(", ");
      x.accept(this);
    }
    this.say("))");
    return;
  }

  @Override
  public void visit(codegen.C.exp.Id e)
  {
    this.say(e.id);
  }

  @Override
  public void visit(codegen.C.exp.Length e)
  {
	  e.array.accept(this);
	  this.say("->length");
	  return;
  }

  @Override
  public void visit(codegen.C.exp.Lt e)
  {
    e.left.accept(this);
    this.say(" < ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(codegen.C.exp.NewIntArray e)
  {
	  this.say("(struct IntArray *)(Tiger_new_array(");
	  e.exp.accept(this);
	  this.say("))");
	  return;
  }

  @Override
  public void visit(codegen.C.exp.NewObject e)
  {
    this.say("((struct " + e.id + "*)(Tiger_new (&" + e.id
        + "_vtable_, sizeof(struct " + e.id + "))))");
    return;
  }

  @Override
  public void visit(codegen.C.exp.Not e)
  {
	  this.say("!");
	  this.say("(");
	  e.exp.accept(this);
	  this.say(")");
	  return;
  }

  @Override
  public void visit(codegen.C.exp.Num e)
  {
    this.say(Integer.toString(e.num));
    return;
  }

  @Override
  public void visit(codegen.C.exp.Sub e)
  {
    e.left.accept(this);
    this.say(" - ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(codegen.C.exp.This e)
  {
    this.say("thiss");
  }

  @Override
  public void visit(codegen.C.exp.Times e)
  {
    e.left.accept(this);
    this.say(" * ");
    e.right.accept(this);
    return;
  }

  // statements
  @Override
  public void visit(codegen.C.stm.Assign s)
  {
    this.printSpaces();
    this.say(s.id + " = ");
    s.exp.accept(this);
    this.say(";\n");
    return;
  }

  @Override
  public void visit(codegen.C.stm.AssignArray s)
  {
	  this.printSpaces();
	  this.say("*("+s.id+"->array+(");
	  s.index.accept(this);
	  this.say(")) = ");
	  s.exp.accept(this);
	  this.printSpaces();
	  this.sayln(";");
	  return;
  }

  @Override
  public void visit(codegen.C.stm.Block s)
  {
	  this.printSpaces();
	  this.say("{");
	  this.say("\n");
	  for(codegen.C.stm.T as:s.stms)
		  as.accept(this);
	  this.printSpaces();
	  this.sayln("}");
	  return;
  }

  @Override
  public void visit(codegen.C.stm.If s)
  {
    this.printSpaces();
    this.say("if (");
    s.condition.accept(this);
    this.sayln(")");
    this.indent();
    s.thenn.accept(this);
    this.unIndent();
    this.sayln("");
    this.printSpaces();
    this.sayln("else");
    this.indent();
    s.elsee.accept(this);
    this.sayln("");
    this.unIndent();
    return;
  }

  @Override
  public void visit(codegen.C.stm.Print s)
  {
	  this.printSpaces();
	  this.say("System_out_println (");
	  s.exp.accept(this);
	  this.sayln(");");
	  return;
  }

  @Override
  public void visit(codegen.C.stm.While s)
  {
	  this.printSpaces();
	  this.say("while(");
	  s.condition.accept(this);
	  this.sayln(")");
	  this.indent();
	  s.body.accept(this);
	  this.unIndent();
	  this.sayln("");
	  return;
  }

  // type
  @Override
  public void visit(codegen.C.type.Class t)
  {
    this.say("struct " + t.id + " *");
  }

  @Override
  public void visit(codegen.C.type.Int t)
  {
    this.say("int");
  }

  @Override
  public void visit(codegen.C.type.IntArray t)
  {
	  this.say("struct IntArray\n");
	  this.say("{\n");
	  this.indent();
	  this.say("int length;\n");
	  this.say("int *array;\n");
	  this.unIndent();
	  this.say("} *");
	  return;
  }

  // dec
  @Override
  public void visit(codegen.C.dec.Dec d)
  {
	  d.type.accept(this);
	  this.say(" "+d.id+";\n");
	  return;
  }

  // method
  @Override
  public void visit(codegen.C.method.Method m)
  {
    m.retType.accept(this);
    this.say(" " + m.classId + "_" + m.id + "(");
    int size = m.formals.size();
    for (codegen.C.dec.T d : m.formals) {
      codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
      size--;
      dec.type.accept(this);
      this.say(" " + dec.id);
      if (size > 0)
        this.say(", ");
    }
    this.sayln(")");
    this.sayln("{");

    for (codegen.C.dec.T d : m.locals) {
      codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
      this.say("  ");
      dec.type.accept(this);
      this.say(" " + dec.id + ";\n");
    }
    this.sayln("");
    for (codegen.C.stm.T s : m.stms)
      s.accept(this);
    this.say("  return ");
    m.retExp.accept(this);
    this.sayln(";");
    this.sayln("}");
    return;
  }

  @Override
  public void visit(codegen.C.mainMethod.MainMethod m)
  {
    this.sayln("int Tiger_main ()");
    this.sayln("{");
    for (codegen.C.dec.T dec : m.locals) {
      this.say("  ");
      codegen.C.dec.Dec d = (codegen.C.dec.Dec) dec;
      d.type.accept(this);
      this.say(" ");
      this.sayln(d.id + ";");
    }
    m.stm.accept(this);
    this.sayln("}\n");
    return;
  }

  // vtables
  @Override
  public void visit(codegen.C.vtable.Vtable v)
  {
    this.sayln("struct " + v.id + "_vtable");
    this.sayln("{");
    for (codegen.C.Ftuple t : v.ms) {
      this.say("  ");
      t.ret.accept(this);
      this.say(" (*" + t.id + ")("+"struct "+v.id+" * thiss");
      if(t.args.isEmpty())
    	  ;
      else
    	  this.say(",");
      for(codegen.C.dec.T decal:t.args)
      {
    	  codegen.C.dec.Dec formal=(codegen.C.dec.Dec)decal;
    	  formal.type.accept(this);
    	  this.say(" "+formal.id);
    	  if(decal!=t.args.getLast())
    		  this.say(",");
      }
      this.sayln(");");
    }
    this.sayln("};\n");
    return;
  }

  private void outputVtable(codegen.C.vtable.Vtable v)
  {
    this.sayln("struct " + v.id + "_vtable " + v.id + "_vtable_ = ");
    this.sayln("{");
    int size=v.ms.size();
    for (codegen.C.Ftuple t : v.ms) {
      --size;
      this.say("  ");
      this.say(t.classs + "_" + t.id);
      if(size!=0)
    	  this.say(",");
      this.say("\n");
    }
    this.sayln("};\n");
    return;
  }

  // class
  @Override
  public void visit(codegen.C.classs.Class c)
  {
    this.sayln("struct " + c.id);
    this.sayln("{");
    this.sayln("  struct " + c.id + "_vtable *vptr;");
    for (codegen.C.Tuple t : c.decs) {
      this.say("  ");
      t.type.accept(this);
      this.say(" ");
      this.sayln(t.id + ";");
    }
    this.sayln("};");
    return;
  }

  // program
  @Override
  public void visit(codegen.C.program.Program p)
  {
    // we'd like to output to a file, rather than the "stdout".
    try {
      String outputName = null;
      if (Control.outputName != null)
        outputName = Control.outputName;
      else if (Control.fileName != null)
        outputName = Control.fileName + ".c";
      else
        outputName = "a.c";

      this.writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
          new java.io.FileOutputStream(outputName)));
      this.writer.write("#include \"include/lib.c\" \n#include \"include/gc.c\"\n");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    this.sayln("// This is automatically generated by the Tiger compiler.");
    this.sayln("// Do NOT modify!\n");

    this.sayln("// structures");
    for (codegen.C.classs.T c : p.classes) {
      c.accept(this);
    }

    this.sayln("// vtables structures");
    for (codegen.C.vtable.T v : p.vtables) {
      v.accept(this);
    }
    this.sayln("");

    this.sayln("// methods");
    for (codegen.C.method.T m : p.methods) {
      m.accept(this);
    }
    this.sayln("");

    this.sayln("// vtables");
    for (codegen.C.vtable.T v : p.vtables) {
      outputVtable((codegen.C.vtable.Vtable) v);
    }
    this.sayln("");

    this.sayln("// main method");
    p.mainMethod.accept(this);
    this.sayln("");

    this.say("\n\n");
    this.say("int main (int argc, char **argv)" + 
"{\nTiger_main ();\nreturn 0;}");
    try {
      this.writer.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

  }

}