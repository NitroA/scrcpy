#ifndef SC_MEMORY_H
#define SC_MEMORY_H

#include <stddef.h>

/* Like calloc(), but without initialization.
 * Like reallocarray(), but without reallocation.
 */
void *
sc_allocarray(size_t nmemb, size_t size);

#endif
