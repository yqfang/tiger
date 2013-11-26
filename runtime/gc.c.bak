#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void *Tiger_new (void *vtable, int size)
{
	int *p;
  // You should write 4 statements for this function.
  // #1: "malloc" a chunk of memory (be careful of the size) :
	p = malloc(sizeof(int)*(size));


  // #2: clear this chunk of memory (zero off it):
	memset(p, 0, size);

	// #3: set up the "vptr" pointer to the value of "vtable":
	p[0] = (int)vtable;
	//vp = (int *)(p[0]);
	// #4: return the pointer
	return p;
}
// "new" an array of size "length", do necessary
// initializations. And each array comes with an
// extra "header" storing the array length.
// This function should return the starting address
// of the array elements, but not the starting address of
// the array chunk.
/*    ---------------------------------------------
      | length | e0 | e1 | ...      | e_{length-1}|
      ---------------------------------------------
               ^
               |
               p (returned address)
*/
void *Tiger_new_array (int length)
{
  // You can use the C "malloc" facilities, as above.
  // Your code here:
	int *p;
	  // You should write 4 statements for this function.
	  // #1: "malloc" a chunk of memory (be careful of the size) :
		p = malloc(sizeof(int)*(2));
		//memset(p, 0, length + 1);
		p[0] = length;
		p[1] = malloc(sizeof(int)*(length));
		return p;

}

