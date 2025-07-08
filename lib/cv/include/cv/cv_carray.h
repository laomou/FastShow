#ifndef CV_CARRAY_H
#define CV_CARRAY_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef enum {
  DT_U8 = 0,
  DT_U16 = 1,
  DT_F32 = 2,
} DataType;

typedef enum {
  BP_RGGB = 0,
  BP_BGGR = 1,
  BP_GRBG = 2,
  BP_GBRG = 3,
  BP_RGB = 4,
} BayerPattern;

typedef struct {
  void* data;
  uint32_t dims[3];
  uint32_t strides[3];
  DataType dtype;
  BayerPattern pattern;
  uint8_t is_owner;
} CRawArray;

#ifdef __cplusplus
}
#endif

#endif  // CV_CARRAY_H
