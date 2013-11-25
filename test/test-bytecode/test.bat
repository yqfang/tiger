@echo ==================================================
@echo test starting
@echo =============
for %%i in (*.java) do java -cp ../../bin Tiger -codegen bytecode %%i
@echo =============
@echo test finished
@echo ==================================================
for %%i in (*.j) do java -jar jasmin.jar %%i
@echo =============
@echo test finished
@echo ==================================================
@echo result-of-BinaryTree ==================================================> result.txt
java BinaryTree >> result.txt

@echo result-of-TreeVisitor ==================================================>> result.txt
java TreeVisitor >> result.txt

@echo result-of-BinarySearch ==================================================>> result.txt
java BinarySearch >> result.txt

@echo result-of-Sum ==================================================>> result.txt
java Monster >> result.txt

@echo result-of-BinaryTree ==================================================>> result.txt
java BinaryTree >> result.txt

@echo result-of-LinearSearch ==================================================>> result.txt
java LinearSearch >> result.txt

@echo result-of-BubbleSort ==================================================>> result.txt
java BubbleSort >> result.txt

@echo result-of-Factorial ==================================================>> result.txt
java Factorial >> result.txt

@echo result-of-QuickSort ==================================================>> result.txt
java QuickSort >> result.txt

@echo result-of-LinkedList ==================================================>> result.txt
java LinkedList >> result.txt

@echo result  complete!!! ==================================================>> result.txt
result.txt
for %%i in (*.class) do del  %%i
for %%i in (*.j) do del  %%i
for %%i in (*.bak) do del  %%i
del result.txt