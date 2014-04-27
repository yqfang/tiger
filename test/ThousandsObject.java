class Test { 
	public static void main(String[] a) {
        System.out.println(new A().a());
    }
}

class A {
	B b1;
    public int a() {
		B b2;
		int i;
		i = 0;
		while(i < 1000)
		{
			b1 = new B();
			b2 = new B();
			i = i + 1;
		}
		return 0;
    }
}
class B
{
	int a;
	int b;
	int[] c;
}
