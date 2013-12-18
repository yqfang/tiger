java -cp ../bin Tiger -codegen C ArrayGC.java
gcc -o ArrayGC ArrayGC.java.c
ArrayGC > ArrayGC.txt
ArrayGC.txt
del ArrayGC.txt
del ArrayGC.exe
del *.bak
