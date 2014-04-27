#define HEAPSIZE 512
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <windows.h>


// The Gimple Garbage Collector.

//===============================================================//
// The Java Heap data structure.

/*   
 ----------------------------------------------------
 |                        |                         |
 ----------------------------------------------------
 ^\                      /^
 | \<~~~~~~~ size ~~~~~>/ |
 from                       to
 */
// structures


struct JavaHeap {
	int size;         // in bytes, note that this if for semi-heap size
	char *from;       // the "from" space pointer
	char *fromFree;   // the next "free" space in the from space
	char *to;         // the "to" space pointer
	char *toStart;    // "start" address in the "to" space
	char *toNext;     // "next" free space pointer in the to space
};

// The Java heap, which is initialized by the following
// "heap_init" function.
struct JavaHeap heap;
static void Tiger_gc();

void printCurrentHeap();

void move(int *p);
void removeLocalObject(int *p);
void removeFromalObject(int* p);
int TestTime(LARGE_INTEGER linkTableStart, LARGE_INTEGER linkTableNow, LARGE_INTEGER m_liPerfFreq);
int ssss = 0;
	char *temp;
	float bt;

// Lab 4, exercise 10:
// Given the heap size (in bytes), allocate a Java heap
// in the C heap, initialize the relevant fields.
void Tiger_heap_init(int heapSize) {
	// You should write 7 statement here:
	// #1: allocate a chunk of memory of size "heapSize" using "malloc"
	char *p = malloc(heapSize);
	// #2: initialize the "size" field, note that "size" field
	// is for semi-heap, but "heapSize" is for the whole heap.
	heap.size = heapSize / 2;
	// #3: initialize the "from" field (with what value?)
	heap.from = p;
	// #4: initialize the "fromFree" field (with what value?)
	heap.fromFree = p;
	// #5: initialize the "to" field (with what value?)
	heap.to = p + heap.size;
	// #6: initizlize the "toStart" field with NULL;
	heap.toStart = NULL;
	// #7: initialize the "toNext" field with NULL;
	heap.toNext = heap.to;
	return;
}

// The "prev" pointer, pointing to the top frame on the GC stack.
// (see part A of Lab 4)
void *prev = 0;
//===============================================================//
// Object Model And allocation

// Lab 4: exercise 11:
// "new" a new object, do necessary initializations, and
// return the pointer (reference).
/*    ----------------
 | vptr      ---|----> (points to the virtual method table)
 |--------------|
 | isObjOrArray | (0: for normal objects)
 |--------------|
 | length       | (this field should be empty for normal objects)
 |--------------|
 | forwarding   |
 |--------------|\
p---->| v_0          | \
 |--------------|  s
 | ...          |  i
 |--------------|  z
 | v_{size-1}   | /e
 ----------------/
 */
// Try to allocate an object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.0p
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1;
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
void *Tiger_new(void *vtable, int size) {
	// Your code here:

	char *p;
	int i;
	if (heap.toStart == NULL) {

		p = heap.fromFree;
		if (size > heap.to - heap.fromFree) {
		printf("\t\tBeforce Heap_From=%dbyte Heap_To=%dbyte\n",(heap.fromFree-heap.from),(heap.toNext - heap.to));
			(heap.toStart == heap.to)?(bt = ((float)(heap.fromFree-heap.from) / (float)heap.size) *100):
			(bt = ((float)(heap.toNext-heap.to) / (float)heap.size) *100);
	
			heap.toNext = heap.toStart = heap.to;
			Tiger_gc();		
			p = heap.toNext;
			printf("\n\t\tLast Heap_From=%dbyte Heap_To=%dbyte\n",(heap.fromFree-heap.from),(heap.toNext - heap.to));
			bt = ((float)(heap.toNext-heap.to) / (float)heap.size) *100;
			printf("\t\tTO Busy = %0.3f%%\n\t\tFrom->->->->->->GC...->->->->->->To\n",bt);
		}
		if (size > heap.to + heap.size - heap.toNext) {
			printf("right OBJECT OutOfMemory!\n");
			exit(1);
		}
	} else if (heap.toStart == heap.to) {

		p = heap.toNext;
		if (size > heap.to + heap.size - heap.toNext) {
		printf("\t\tBeforce Heap_From=%dbyte Heap_To=%dbyte\n",(heap.fromFree-heap.from),(heap.toNext - heap.to));
		
			
			heap.toStart = NULL;
			heap.fromFree = heap.from;
			Tiger_gc();
		
			
			p = heap.fromFree;
				printf("\n\t\tLast Heap_From=%dbyte Heap_TO=%dbyte\n",(heap.fromFree-heap.from),(heap.toNext - heap.to));
			(bt = ((float)(heap.fromFree-heap.from) / (float)heap.size) *100);
			printf("\t\tFrom Busy = %0.3f%%\n\t\tTo->->->->->->->GC...->->->->->From\n",bt);
		}
		if (size > heap.to - heap.fromFree) {
			printf("left OBJECT OutOfMemory!\n");
			exit(1);
		}
	}
	//memset(p, 0, size);
	((int*) p)[0] = (int) vtable;
	((int*) p)[1] = 0;
	((int*) p)[2] = 0;
	((int*) p)[3] = NULL;

	for(i = 0; i < (size / 4 - 4); i++)
	{
		((int*) p)[4 + i] = NULL;
	}
	(heap.toStart == NULL) ? (heap.fromFree += size) : (heap.toNext += size);
	return p;

}

// "new" an array of size "length", do necessary
// initializations. And each array cosmes with an
// extra "header" storing the array length and other inFromation.
/*    ----------------
 | vptr         | (this field should be empty for an array)
 |--------------|
 | isObjOrArray | (1: for array)
 |--------------|
 | length       |
 |--------------|
 | forwarding   |
 |--------------|\
p---->| e_0          | \
 |--------------|  s
 | ...          |  i
 |--------------|  z
 | e_{length-1} | /e
 ----------------/
 */
// Try to allocate an array object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this array object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1;
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
void *Tiger_new_array(int length) {

	char *p;
	if (heap.toStart == NULL) {

		p = heap.fromFree;
		if (20 + sizeof(int) * length > heap.to - heap.fromFree) {
		printf("\t\tBeforce Heap_From=%dbyte Heap_To=%dbyte\n",(heap.fromFree-heap.from),(heap.toNext - heap.to));
			(heap.toStart == heap.to)?(bt = ((float)(heap.fromFree-heap.from) / (float)heap.size) *100):
			(bt = ((float)(heap.toNext-heap.to) / (float)heap.size) *100);

		heap.toNext = heap.toStart = heap.to;
			Tiger_gc();
			
			
			p = heap.toNext;
			printf("\n\t\tLast Heap_From=%dbyte Heap_To=%dbyte\n",(heap.fromFree-heap.from),(heap.toNext - heap.to));
			bt = ((float)(heap.toNext-heap.to) / (float)heap.size) *100;
			printf("\t\tTO Busy = %0.3f%%\n\t\tFrom->->->->->->GC...->->->->->->To\n",bt);
		}
		if (20 + sizeof(int) * length > heap.to + heap.size - heap.toNext) {
			printf("right ARRAY OutOfMemory!\n");
		
			exit(1);
		}
	} else if (heap.toStart == heap.to) {

		p = heap.toNext;
		if (20 + sizeof(int) * length > heap.to + heap.size - heap.toNext) {
		printf("\t\tBeforce Heap_From=%dbyte Heap_To=%dbyte\n",(heap.fromFree-heap.from),(heap.toNext - heap.to));
			(heap.toStart == heap.to)?(bt = ((float)(heap.fromFree-heap.from) / (float)heap.size) *100):
			(bt = ((float)(heap.toNext-heap.to) / (float)heap.size) *100);
		
			heap.toStart = NULL;
			heap.fromFree = heap.from;
			Tiger_gc();
	
			
			p = heap.fromFree;
				printf("\n\t\tLast Heap_From=%dbyte Heap_TO=%dbyte\n",(heap.fromFree-heap.from),(heap.toNext - heap.to));
			(bt = ((float)(heap.fromFree-heap.from) / (float)heap.size) *100);
			printf("\t\tFrom Busy = %0.3f%%\n\t\tTo->->->->->->->GC...->->->->->From\n",bt);
		}
		if (20 + sizeof(int) * length > heap.to - heap.fromFree) {
			printf("left ARRAY OutOfMemory!\n");
			exit(1);
		}
	}

	//memset(p, 0, 20 + sizeof(int) * length);
	((int*) p)[0] = NULL;
	((int*) p)[1] = 1;
	((int*) p)[2] = length;
	((int*) p)[3] = NULL;
	((int*) p)[4] = (heap.toStart == heap.to)?(heap.toNext+20):(heap.fromFree+20);

	(heap.toStart == NULL) ?
			(heap.fromFree += 20 + sizeof(int) * length) :
			(heap.toNext += 20 + sizeof(int) * length);
	return p;
}

//===============================================================//
// The Gimple Garbage Collector

// Lab 4, exercise 12:
// A copying collector based-on Cheney's algorithm.
static void Tiger_gc() {
		
	// Your code here:
	int * p = prev;//p is the top pointer of the stack
	LARGE_INTEGER m_liPerfFreq={0};	//获取每秒多少CPU PerFromance Tick 
	QueryPerformanceFrequency(&m_liPerfFreq); 
	LARGE_INTEGER linkTableStart={0};
	QueryPerformanceCounter(&linkTableStart);
      while(p != 0)
	{
		removeLocalObject(p);//try to move all local references
		removeFromalObject(p);//try to move all Fromal reference
		//printCurrentHeap(); //watch the current heap state after collection
		p = p[0];
	}
		LARGE_INTEGER linkTableNow={0};		
	QueryPerformanceCounter(&linkTableNow);
	int timer=TestTime(linkTableStart, linkTableNow, m_liPerfFreq);
	printf("\t\t\tTime : %d ms",timer);
	if(heap.toStart == heap.to)
	{
		heap.fromFree = heap.from; //after gc,clean up the other hemiHeap
	}
	else
	{
		heap.toNext = heap.to;//as above
	}
}

void removeLocalObject(int *p)//try to move all local references
{
	int i;
	for(i = 0; i < atoi(p[3]); i++)
	{
		move(p[4 + i]);//move local references according to the counts of local_gc_map
	}
}

void removeFromalObject(int* p)//try to move all Fromal reference
{

	char* c = p[1];
	int i;
	for(i = 0; i < strlen(c); i++)
	{
		if( c[i] == '1')
		{
			int *q = p[2] + 4 * i;
			int *a = *q;
			move(a);//move all Fromal references according to the '1' element of Fromal_gc_map vector
		}
	}


}




void printCurrentHeap()//watch the current heap state after collection
{
	if(heap.toStart == NULL)
	printf("leftHeap% = %.2f\n", 100 * (float)(heap.fromFree - heap.from)/(float)heap.size);
	else
	printf("rightHeap% = %.2f\n",  100 * (float)(heap.toNext - heap.to)/(float)heap.size);
}



void move(int *p)
{ 
	int i;
	if(p == NULL || p[3] != NULL || (p[1] != 0 && p[1] != 1))
		return;
	
	int *temp = p[0];//vtable_map
	char* classmap;
	if(temp != NULL)
{
	classmap = temp[0];
}

	int basicsize = 4;
	int realsize = 0;
	if(p[1] == 1)
	{
		realsize = (basicsize + 1 + p[3]) * 4;//array object
	}
	if(p[1] == 0)
	{
		if(classmap[0] == '2')
		{
		realsize = basicsize * 4;
		}		
		else
		{
		realsize = (strlen(temp[0]) + basicsize) * 4;//normal object
		}
		
	}

	if(heap.toStart == NULL)
	{
	
		memcpy(heap.fromFree, p, realsize);
		p = p[3] = heap.fromFree;
		heap.fromFree += realsize;
		//heap.toStart = NULL;
		if(temp == NULL) return;
		for(i = 0; i < strlen(classmap); i++) //to the current object ,move its referents tuple
	{
		if( classmap[i] == '1')
		{
			int *q = p[4 + i];
			move(q);
		}
	}

	}
	if(heap.toStart == heap.to)
	{
	int i;
		memcpy(heap.toNext, p, realsize);
		p = p[3] = heap.toNext;//forward is not null now!
		heap.toNext += realsize;
		if(temp ==NULL)return;
		for(i = 0; i < strlen(classmap); i++) //to the current object ,move its referents tuple
	{
		if( classmap[i] == '1')
		{
			int *q = p[4 + i];
			move(q);
		}
	}
	
	}
		
	return;
	
}
int TestTime(LARGE_INTEGER linkTableStart, LARGE_INTEGER linkTableNow, LARGE_INTEGER m_liPerfFreq)
{
	return ( ((linkTableNow.QuadPart - linkTableStart.QuadPart) * 1000)/m_liPerfFreq.QuadPart);
}




