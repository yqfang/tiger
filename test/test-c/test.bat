@echo ==================================================
@echo test starting
@echo =============
for %%i in (*.java) do java -cp ../../bin Tiger -codegen C %%i
@echo =============
@echo test finished
@echo ==================================================

gcc -o BubbleSort BubbleSort.java.c
gcc -o BinarySearch BinarySearch.java.c

gcc -o Factorial Factorial.java.c
gcc -o LinearSearch LinearSearch.java.c
gcc -o Sum Sum.java.c
gcc -o QuickSort QuickSort.java.c


@echo result-of-BinarySearch ==================================================> result.txt
BinarySearch >> result.txt



@echo result-of-Sum ==================================================>> result.txt
Sum >> result.txt


@echo result-of-LinearSearch ==================================================>> result.txt
LinearSearch >> result.txt

@echo result-of-BubbleSort ==================================================>> result.txt
BubbleSort >> result.txt

@echo result-of-Factorial ==================================================>> result.txt
Factorial >> result.txt

@echo result-of-QuickSort ==================================================>> result.txt
QuickSort >> result.txt


@echo result  complete!!! ==================================================>> result.txt
result.txt
for %%i in (*.c) do del  %%i
for %%i in (*.exe) do del  %%i
for %%i in (*.bak) do del  %%i
del result.txt
