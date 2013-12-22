package codegen.C;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import codegen.C.dec.Dec;
import control.Control;


public class PrettyPrintVisitor implements Visitor {
	private int indentLevel;
	private java.io.BufferedWriter writer;
	private String methodName = "";
	private String locals = "";
	private Map<String, String> TupleGCMap = new HashMap<String, String>();
	public PrettyPrintVisitor() {
		this.indentLevel = 2;
	}

	private void indent() {
		this.indentLevel += 2;
	}

	private void unIndent() {
		this.indentLevel -= 2;
	}

	private void printSpaces() {
		int i = this.indentLevel;
		while (i-- != 0)
			this.say(" ");
	}

	private void sayln(String s) {
		say(s);
		try {
			this.writer.write("\n");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void say(String s) {
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
	public void visit(codegen.C.exp.Add e) {
		e.left.accept(this);
		this.say("+");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.And e) {
		e.left.accept(this);
		this.say("&&");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.ArraySelect e) {
		this.say("*(");
		e.array.accept(this);
		this.say("->array+(");
		e.index.accept(this);
		this.say("))");
		return;
	}

	@Override
	public void visit(codegen.C.exp.Call e) {
		String str = "";
		String[] strs = this.locals.split(",");
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < strs.length; i++) {
			set.add(strs[i]);
		}

		if (!methodName.equals("")) {
			if (e.assign.startsWith("thiss") || !set.contains(e.assign)) {
				str = e.assign;
			} else {
				str = "frame." + e.assign;
			}

		} else {
			str = e.assign;
		}

		this.say("(" + str + "=");
		e.exp.accept(this);
		this.say(", ");
		this.say(str + "->vptr->" + e.id + "(" + str);
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
	public void visit(codegen.C.exp.Id e) {
		String str = "";
		String[] strs = this.locals.split(",");
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < strs.length; i++) {
			set.add(strs[i]);
		}

		if (!methodName.equals("")) {
			if (e.id.startsWith("thiss") || !set.contains(e.id)) {
				str = e.id;
			} else {
				str = "frame." + e.id;
			}

		} else {
			str = e.id;
		}
		this.say(str);
	}

	@Override
	public void visit(codegen.C.exp.Length e) {
		e.array.accept(this);
		this.say("->length");
		return;
	}

	@Override
	public void visit(codegen.C.exp.Lt e) {
		e.left.accept(this);
		this.say(" < ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.NewIntArray e) {
		this.say("(struct IntArray *)(Tiger_new_array(");
		e.exp.accept(this);
		this.say("))");
		return;
	}

	@Override
	public void visit(codegen.C.exp.NewObject e) {
		this.say("((struct " + e.id + "*)(Tiger_new (&" + e.id
				+ "_vtable_, sizeof(struct " + e.id + "))))");
		return;
	}

	@Override
	public void visit(codegen.C.exp.Not e) {
		this.say("!");
		this.say("(");
		e.exp.accept(this);
		this.say(")");
		return;
	}

	@Override
	public void visit(codegen.C.exp.Num e) {
		this.say(Integer.toString(e.num));
		return;
	}

	@Override
	public void visit(codegen.C.exp.Sub e) {
		e.left.accept(this);
		this.say(" - ");
		e.right.accept(this);
		return;
	}

	@Override
	public void visit(codegen.C.exp.This e) {
		this.say("thiss");
	}

	@Override
	public void visit(codegen.C.exp.Times e) {
		e.left.accept(this);
		this.say(" * ");
		e.right.accept(this);
		return;
	}

	// statements
	@Override
	public void visit(codegen.C.stm.Assign s) {
		String str = "";
		String[] strs = this.locals.split(",");
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < strs.length; i++) {
			set.add(strs[i]);
		}

		if (!methodName.equals("")) {
			if (s.id.startsWith("thiss") || !set.contains(s.id)) {
				str = s.id;
			} else {
				str = "frame." + s.id;
			}

		} else {
			str = s.id;
		}

		this.printSpaces();
		this.say(str + " = ");
		s.exp.accept(this);
		this.say(";\n");
		return;
	}

	@Override
	public void visit(codegen.C.stm.AssignArray s) {
		this.printSpaces();
		this.say("*(" + s.id + "->array+(");
		s.index.accept(this);
		this.say(")) = ");
		s.exp.accept(this);
		this.printSpaces();
		this.sayln(";");
		return;
	}

	@Override
	public void visit(codegen.C.stm.Block s) {
		this.printSpaces();
		this.say("{");
		this.say("\n");
		for (codegen.C.stm.T as : s.stms)
			as.accept(this);
		this.printSpaces();
		this.sayln("}");
		return;
	}

	@Override
	public void visit(codegen.C.stm.If s) {
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
	public void visit(codegen.C.stm.Print s) {
		this.printSpaces();
		this.say("System_out_println (");
		s.exp.accept(this);
		this.sayln(");");
		return;
	}

	@Override
	public void visit(codegen.C.stm.While s) {
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
	public void visit(codegen.C.type.Class t) {
		this.say("struct " + t.id + " *");
	}

	@Override
	public void visit(codegen.C.type.Int t) {
		this.say("int");
	}

	@Override
	public void visit(codegen.C.type.IntArray t) {
		this.say("IntArray ");
		
	}
	private void printIntArrayDef()
	{
		this.say("typedef struct IntArray");
		this.say("\n");
		this.say("{\n");
		this.indent();
		this.sayln("struct IntArray" + "_vtable *vptr;\n");
		this.sayln("\t " + "\n\tint isObjOrArray;\n\tunsigned length;\n\tvoid *forwarding;\n\tint *array;\n");
		this.unIndent();
		this.say("} * IntArray;\n\t");
		return;
	}

	// dec
	@Override
	public void visit(codegen.C.dec.Dec d) {
		d.type.accept(this);
		this.say(" " + d.id + ";\n");
		return;
	}

	// struct f_gc_frame{
	// void *prev; // dynamic chain, pointing to f's caller's GC frame
	// char *arguments_gc_map; // should be assigned the value of
	// "f_arguments_gc_map"
	// int *arguments_base_address; // address of the first argument
	// char *locals_gc_map; // should be assigned the value of "f_locals_gc_map"
	// struct A *local1; // remaining fields are method locals
	// int local2;
	// struct C *local3;
	// };
	// method

	@Override
	public void visit(codegen.C.method.Method m) {

		int i = printGcStackFrame(m);

		printGcMap(m);

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
		// struct f_gc_frame frame;
		// //push this frame onto the GC stack by setting up "prev"
		// frame.prev = prev;
		// prev = &frame;
		// //setting up memory GC maps and corresponding base addresses
		// frame.arguments_gc_map = f_arguments_gc_map;
		// frame.arguments_base_address = &this;
		// frame.locals_gc_map = f_locals_gc_map;
		String str0 = "";
		while(i > 0)
		{
			str0 += "0,";
			--i;
		}
		this.say("\tstruct "
				+ m.classId
				+ "_"
				+ m.id
				+ "_gc_frame frame = {0,0,0,0," + str0) ;
				
				
				this.say("};\n\t"
				+ "frame.prev = prev;\n\tprev = &frame;\n\tframe.arguments_gc_map = "
				+ m.classId + "_" + m.id + "_arguments_gc_map;\n\t"
				+ "frame.arguements_base_address = &thiss;\n\t"
				+ "frame.locals_gc_map = " + m.classId + "_" + m.id
				+ "_locals_gc_map;\n\t");


		this.sayln("");

		for (codegen.C.dec.T d : m.locals) {
			String strtmp = " ";
			strtmp = ((Dec) d).getType().toString();
			if (strtmp.equals("@int")) {
				d.accept(this);
				this.say("\t");
			}
		}
		this.methodName = "yes";
		for (codegen.C.stm.T s : m.stms)
		{
			s.accept(this);	
			//this.sayln("printCurrentHeap();\n\t");
		}
			
		this.say("prev = frame.prev;");
		this.say("  return ");
		m.retExp.accept(this);
		this.sayln(";");
		this.sayln("}");
		this.methodName = "";
		this.locals = "";
		return;
	}

	private int printGcStackFrame(codegen.C.method.Method m) {
		int i = 0;
		String str = "struct " + m.classId + "_" + m.id
				+ "_gc_frame{\n\tvoid* prev;\n\tchar *"
				+ "arguments_gc_map;\n\tint *arguements_base_address;\n\t"
				+ "char *" + "locals_gc_map;\n\t";
		this.say(str);

		for (codegen.C.dec.T d : m.locals) {
			String strtmp = " ";
			strtmp = ((Dec) d).getType().toString();
			if (!strtmp.equals("@int")) {
				i++;
				this.locals += ((Dec) d).getId() + ",";
				d.accept(this);
				this.say("\t");
			}
		}
		this.say("};\n");
		return i;
	}

	private void printGcMap(codegen.C.method.Method m) {
		String str = "";
		for (codegen.C.dec.T d : m.formals) {
			String strtmp = " ";
			strtmp = ((Dec) d).getType().toString();
			if (!strtmp.equals("@int")) {
				str += "1";
			} else
				str += "0";
		}
		if (str.equals("")) {
			str = "2";
		}
		this.say("char *" + m.classId + "_" + m.id + "_arguments_gc_map = \""
				+ str + "\";\n");
		int numlocalrefs = 0;
		for (codegen.C.dec.T d : m.locals) {
			String strtmp = " ";
			strtmp = ((Dec) d).getType().toString();
			if (!strtmp.equals("@int")) {
				numlocalrefs++;
			}
		}
		this.say("char *" + m.classId + "_" + m.id + "_locals_gc_map = \""
				+ numlocalrefs + "\";\n");
	}

	@Override
	public void visit(codegen.C.mainMethod.MainMethod m) {
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
	public void visit(codegen.C.vtable.Vtable v) {

		this.sayln("struct " + v.id + "_vtable");
		this.sayln("{");
		this.sayln("\tchar *" + v.id + "_gc_map; ");
		for (codegen.C.Ftuple t : v.ms) {
			this.say("  ");
			t.ret.accept(this);
			this.say(" (*" + t.id + ")(" + "struct " + v.id + " * thiss");
			if (t.args.isEmpty())
				;
			else
				this.say(",");
			for (codegen.C.dec.T decal : t.args) {
				codegen.C.dec.Dec formal = (codegen.C.dec.Dec) decal;
				formal.type.accept(this);
				this.say(" " + formal.id);
				if (decal != t.args.getLast())
					this.say(",");
			}
			this.sayln(");");
		}
		this.sayln("};\n");
		return;
	}

	private void outputVtable(codegen.C.vtable.Vtable v) {
		this.sayln("struct " + v.id + "_vtable " + v.id + "_vtable_ = ");
		this.sayln("{");
		String str = this.TupleGCMap.get(v.id + "_vtable");
		this.sayln("\"" + str + "\",");
		int size = v.ms.size();
		for (codegen.C.Ftuple t : v.ms) {
			--size;
			this.say("  ");
			this.say(t.classs + "_" + t.id);
			if (size != 0)
				this.say(",");
			this.say("\n");
		}
		this.sayln("};\n");
		return;
	}

	// class
//	struct A_class{
//		void *vptr; // virtual method table pointer
//		int isObjOrArray; // is this a normal object or an (integer) array object?
//		unsigned length; // array length
//		void *forwarding; // forwarding pointer, will be used by your Gimple GC
//		...; // remainings are normal class or array fields
//		};
	@Override
	public void visit(codegen.C.classs.Class c) {
		this.sayln("struct " + c.id);
		this.sayln("{");
		this.sayln("  struct " + c.id + "_vtable *vptr;");
		this.sayln("\tint isObjOrArray;\n\tunsigned length;\n\tvoid *forwarding;\n\t");
		String str = "";
		for (codegen.C.Tuple t : c.decs) {
			String strtmp = t.type.toString();
			if (!strtmp.equals("@int")) {
				str += "1";
			} else {
				str += "0";
			}
			this.say("  ");
			t.type.accept(this);
			this.say(" ");
			this.sayln(t.id + ";");
		}
		this.sayln("};");
		if (str.isEmpty()) {
			str = "2";
		}
		this.TupleGCMap.put(c.id + "_vtable", str);
		return;
	}

	// program
	@Override
	public void visit(codegen.C.program.Program p) {
		// we'd like to output to a file, rather than the "stdout".
		try {
			String outputName = null;
			if (Control.outputName != null)
				outputName = Control.outputName;
			else if (Control.fileName != null)
				outputName = Control.fileName + ".c";
			else
				outputName = "a.c";

			this.writer = new java.io.BufferedWriter(
					new java.io.OutputStreamWriter(
							new java.io.FileOutputStream(outputName)));
			this.writer
					.write("#include \"include/lib.c\" \n#include \"include/gc.c\"\nextern void Tiger_heap_init (int);");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		this.sayln("// This is automatically generated by the Tiger compiler.");
		this.sayln("// Do NOT modify!\n");

		this.sayln("// structures");
		printIntArrayDef();
		for (codegen.C.classs.T c : p.classes) {
			c.accept(this);
		}

		this.sayln("// vtables structures");
		for (codegen.C.vtable.T v : p.vtables) {
			v.accept(this);
		}
		this.sayln("");

		this.sayln("//method declar");
		for (codegen.C.method.T m : p.methods) {
			printMethodDec(m);
		}

		this.sayln("// vtables");
		for (codegen.C.vtable.T v : p.vtables) {
			outputVtable((codegen.C.vtable.Vtable) v);
		}
		this.sayln("");

		this.sayln("// methods");
		for (codegen.C.method.T m : p.methods) {
			m.accept(this);
		}
		this.sayln("");

		this.sayln("// main method");
		p.mainMethod.accept(this);
		this.sayln("");

		this.say("\n\n");
		this.say("int main (int argc, char **argv)"
				+ "{\nTiger_heap_init (HEAPSIZE);\n\tTiger_main ();\nreturn 0;}");
		try {
			this.writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	private void printMethodDec(codegen.C.method.T m) 
{
		codegen.C.method.Method methoddeclar = (codegen.C.method.Method) m;
		methoddeclar.retType.accept(this);
		this.say(" " + methoddeclar.classId + "_" + methoddeclar.id + "(");
		int size = methoddeclar.formals.size();
		for (codegen.C.dec.T d : methoddeclar.formals) {
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec) d;
			size--;
			dec.type.accept(this);
			this.say(" " + dec.id);
			if (size > 0)
				this.say(", ");
		}
		this.sayln(");");
	}
}
