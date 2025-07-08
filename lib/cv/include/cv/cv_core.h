#ifndef CV_CORE_H
#define CV_CORE_H

#include <stddef.h>
#include <stdint.h>

#include "cv/cv_array.hpp"

#ifdef __cplusplus
extern "C" {
#endif

bool bayer_to_bayer_rggb(CRawArray* bayer);

bool normalize(const CRawArray* input, CRawArray* output, float input_min, float input_max);

bool bayer_rggb_to_rggb(const CRawArray* input, CRawArray* output);

bool rggb_to_bayer_rggb(const CRawArray* input, CRawArray* output);

bool apply_ccm(CRawArray* rgb, const float ccm[9]);

bool apply_rgb_gain(CRawArray* rgb, const float gain[3]);

bool bayer_rggb_to_rgb(const CRawArray* input, CRawArray* output);

#ifdef __cplusplus
}
#endif

#endif  // CV_CORE_H
