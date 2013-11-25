package elaborator;


public class MethodTable
{
  private java.util.Hashtable<String, ast.type.T> table;

  public MethodTable()
  {
    this.table = new java.util.Hashtable<String, ast.type.T>();
  }

  public java.util.Hashtable<String, ast.type.T> getTable() {
	return table;
}

public void setTable(java.util.Hashtable<String, ast.type.T> table) {
	this.table = table;
}

// Duplication is not allowed
  public void put(java.util.LinkedList<ast.dec.T> formals,
      java.util.LinkedList<ast.dec.T> locals)
  {
    for (ast.dec.T dec : formals) {
      ast.dec.Dec decc = (ast.dec.Dec) dec;
      if (this.table.get(decc.id) != null) {
        System.out.println("duplicated parameter: " + decc.id);
        System.exit(1);
      }
      this.table.put(decc.id, decc.type);
    }

    for (ast.dec.T dec : locals) {
      ast.dec.Dec decc = (ast.dec.Dec) dec;
      if (this.table.get(decc.id) != null) {
        System.out.println("duplicated variable: " + decc.id);
        System.exit(1);
      }
      this.table.put(decc.id, decc.type);
    }

  }

  // return null for non-existing keys
  public ast.type.T get(String id)
  {
    return this.table.get(id);
  }

  public void dump()
  {
	//System.out.println("--------------- MethodTable dump -- begin ----");		  
	  System.out.println("-------------------------------------");		  
//		System.out.println(this.table.toString());
		  String key;
		  java.util.Enumeration<String> keys = this.table.keys();
		  while(keys.hasMoreElements()){
			  key = keys.nextElement();
			  System.out.println(key + ": " + this.table.get(key));  
		  }
	System.out.println("=============== MethodTable dump end ===========");	
  }

  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
