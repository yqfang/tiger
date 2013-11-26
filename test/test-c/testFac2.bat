java -cp ../../bin Tiger -codegen C Factorial.java
gcc -o Factorial Factorial.java.c 
Factorial  > testFac.txt
testFac.txt
del testFac.txt