class A { 
	public static void main(String[] a) {
        System.out.println(new B().B());
    }
}
class B {
    public int B() {
		C c;
		c = new C();
		return 10;
    }
}
class C {
    public int C() {
        return 10;
    }
}