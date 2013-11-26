java -cp ../../bin Tiger -codegen C Factorial2.java
gcc -o Factorial2 Factorial2.java.c 
Factorial2  > testFac.txt
testFac.txt
del testFac.txt