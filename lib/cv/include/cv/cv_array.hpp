#ifndef CV_ARRAY_HPP
#define CV_ARRAY_HPP

#include "cv/cv_carray.h"

class RawArray {
 public:
  RawArray(size_t height, size_t width, size_t channels, DataType dtype,
           BayerPattern pattern);

  RawArray(void* data, size_t height, size_t width, size_t channels,
           DataType dtype, BayerPattern pattern, bool take_ownership = false);

  ~RawArray();

  RawArray(const RawArray&) = delete;
  RawArray& operator=(const RawArray&) = delete;

  const CRawArray* c_ptr() const { return raw_array; }

  CRawArray* c_ptr() { return raw_array; }

  const char* data() const {
    return reinterpret_cast<const char*>(raw_array->data);
  }

  char* data() { return reinterpret_cast<char*>(raw_array->data); }

 private:
  CRawArray* raw_array = nullptr;

  static size_t item_size(DataType dtype) {
    switch (dtype) {
      case DT_U8:
        return 1;
      case DT_U16:
        return 2;
      case DT_F32:
        return 4;
      default:
        return 0;
    }
  }
};

RawArray::RawArray(size_t height, size_t width, size_t channels, DataType dtype,
                   BayerPattern pattern) {
  const size_t sz = item_size(dtype);
  if (sz == 0) return;

  const size_t strides[3] = {width * channels * sz, channels * sz, sz};

  const size_t total_bytes = height * strides[0];
  void* data = std::malloc(total_bytes);
  if (!data) return;

  raw_array = static_cast<CRawArray*>(std::malloc(sizeof(CRawArray)));
  if (!raw_array) {
    std::free(data);
    return;
  }

  raw_array->data = data;
  raw_array->dims[0] = height;
  raw_array->dims[1] = width;
  raw_array->dims[2] = channels;
  std::memcpy(raw_array->strides, strides, sizeof(strides));
  raw_array->dtype = dtype;
  raw_array->pattern = pattern;
  raw_array->is_owner = 1;
}

RawArray::RawArray(void* data, size_t height, size_t width, size_t channels,
                   DataType dtype, BayerPattern pattern, bool take_ownership) {
  const size_t sz = item_size(dtype);
  if (sz == 0) return;

  const size_t strides[3] = {width * channels * sz, channels * sz, sz};

  raw_array = static_cast<CRawArray*>(std::malloc(sizeof(CRawArray)));
  if (!raw_array) {
    if (take_ownership) std::free(data);
    return;
  }

  raw_array->data = data;
  raw_array->dims[0] = height;
  raw_array->dims[1] = width;
  raw_array->dims[2] = channels;
  std::memcpy(raw_array->strides, strides, sizeof(strides));
  raw_array->dtype = dtype;
  raw_array->pattern = pattern;
  raw_array->is_owner = take_ownership ? 1 : 0;
}

RawArray::~RawArray() {
  if (raw_array) {
    if (raw_array->is_owner && raw_array->data) {
      std::free(raw_array->data);
    }
    std::free(raw_array);
  }
}

#endif  // CV_ARRAY_HPP
