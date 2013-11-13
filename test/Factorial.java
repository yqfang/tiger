<<<<<<< HEAD:test/Factorial.java
class Factorial { 
=======
//12222
class Factorial { //12222
>>>>>>> Lab1:test/Factorial.java
	public static void main(String[] a) {
        System.out.println(new Fac().ComputeFac(10));
    }
}
class Fac {
    public int ComputeFac(int num) {
        int num_aux;
        if (num < 1)
            num_aux = 1;
        else
            num_aux = num * (this.ComputeFac(num-1));
        return num_aux;
    }
}
