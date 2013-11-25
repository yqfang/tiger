class Monster
{
	public static void main(String[] args)
	{
		System.out.println(new Foo().foo(new Foo()));
	}
}

class Foo
{
	int i;
	int[] num;
    public int foo(Foo F)
    {
    	
    		int j;
    		j=5;
    		num=new int[2];
    		num[0]=1;
    		num[1]=2;
            while(!(j<0))
            {
            	i=num[1]+num[0];
            	i=i+j;
            	j=j-1;
            }
            return i;
    }
}