#ifndef FS_CORE_H
#define FS_CORE_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef void* FS_Handle;

FS_Handle fs_create_local();

void fs_free(FS_Handle handle);

#ifdef __cplusplus
}
#endif

#endif // FS_CORE_H