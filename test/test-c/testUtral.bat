java -cp ../../bin Tiger -codegen C Utral.java
gcc -o Utral Utral.java.c 
Utral  > test.txt
test.txt
del test.txt