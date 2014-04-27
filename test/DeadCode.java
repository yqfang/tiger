class DeadCode { 
	public static void main(String[] a) {
        System.out.println(new Doit().doit());
    }
}

class Doit {
    public int doit() {
    	int i;
    	i=2+1;
    	i=4*i;
    	i=i-i;
    	i=5+9;
        if (true)
        {
          i=1;
          System.out.println(1);
          if(!(true&&false)){
        	  i=0;
        	  if(false)
        		  i=3;
        	  else
        		  i=4;
          }
          else
        	  i=1;
          
        }
        else 
          System.out.println(0);
        while(1<0*i)
        {
        	if(false)
        		i=i+1;
        	else
        		i=i+2;
        	i=i+1;
        }
        return 0;
    }
}
