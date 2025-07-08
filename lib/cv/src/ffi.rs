use ndarray::{s, Array2, ArrayView2, ArrayView3, ArrayViewMut2, ArrayViewMut3};
use std::os::raw::c_void;

#[repr(C)]
#[derive(Debug, Clone, Copy, PartialEq)]
pub enum BayerPattern {
    RGGB = 0,
    BGGR = 1,
    GRBG = 2,
    GBRG = 3,
}

#[repr(C)]
#[derive(Debug, Clone, Copy, PartialEq)]
pub enum DataType {
    U8 = 0,
    U16 = 1,
    F32 = 2,
}

#[repr(C)]
pub struct CRawArray {
    pub data: *mut c_void,
    pub dims: [u32; 3],
    pub strides: [u32; 3],
    pub dtype: DataType,
    pub pattern: BayerPattern,
    pub owner: u8,
}

#[unsafe(no_mangle)]
pub extern "C" fn bayer_to_bayer_rggb(bayer: *mut CRawArray) -> bool {
    if bayer.is_null() {
        return false;
    }

    unsafe {
        let bayer = &mut *bayer;

        let [height, width, channels] = bayer.dims;
        if bayer.dtype != DataType::U16 || channels != 1 {
            return false;
        }

        let pattern = bayer.pattern;
        let height = height as usize;
        let width = width as usize;
        let input_ptr = bayer.data as *const u16;
        let output_ptr = bayer.data as *mut u16;

        let input = ArrayView2::from_shape_ptr((height, width), input_ptr);
        let mut temp_output = Array2::<u16>::zeros((height, width));

        match pattern {
            BayerPattern::RGGB => temp_output.assign(&input),
            BayerPattern::BGGR => temp_output.assign(&input.slice(s![..;-1, ..;-1])),
            BayerPattern::GRBG => temp_output.assign(&input.slice(s![.., ..;-1])),
            BayerPattern::GBRG => temp_output.assign(&input.slice(s![..;-1, ..])),
        }

        let mut output = ArrayViewMut2::from_shape_ptr((height, width), output_ptr);
        output.assign(&temp_output);

        bayer.pattern = BayerPattern::RGGB;
    }

    true
}

#[unsafe(no_mangle)]
pub extern "C" fn normalize(
    input: *const CRawArray,
    output: *mut CRawArray,
    input_min: f32,
    input_max: f32,
) -> bool {
    if input.is_null() || output.is_null() || input_max <= input_min {
        return false;
    }

    unsafe {
        let input = &*input;
        let output = &mut *output;

        let [height, width, channels] = input.dims;
        if output.dims != input.dims {
            return false;
        }

        let height = height as usize;
        let width = width as usize;
        let channels = channels as usize;
        let scale = 1.0 / (input_max - input_min);

        macro_rules! normalize_impl {
            ($in_ty:ty, $out_ty:ty) => {{
                let input_ptr = input.data as *const $in_ty;
                let output_ptr = output.data as *mut $out_ty;

                let input = ArrayView3::from_shape_ptr((height, width, channels), input_ptr);
                let mut output =
                    ArrayViewMut3::from_shape_ptr((height, width, channels), output_ptr);

                for ((y, x, c), out) in output.indexed_iter_mut() {
                    let val = input[(y, x, c)] as f32;
                    let norm = ((val - input_min) * scale).clamp(0.0, 1.0);
                    *out = match std::any::TypeId::of::<$out_ty>() {
                        id if id == std::any::TypeId::of::<u8>() => {
                            (norm * 255.0).round() as $out_ty
                        }
                        id if id == std::any::TypeId::of::<u16>() => {
                            (norm * 65535.0).round() as $out_ty
                        }
                        _ => norm as $out_ty,
                    };
                }
            }};
        }

        match (input.dtype, output.dtype) {
            (DataType::U8, DataType::U8) => normalize_impl!(u8, u8),
            (DataType::U8, DataType::U16) => normalize_impl!(u8, u16),
            (DataType::U8, DataType::F32) => normalize_impl!(u8, f32),

            (DataType::U16, DataType::U8) => normalize_impl!(u16, u8),
            (DataType::U16, DataType::U16) => normalize_impl!(u16, u16),
            (DataType::U16, DataType::F32) => normalize_impl!(u16, f32),

            (DataType::F32, DataType::U8) => normalize_impl!(f32, u8),
            (DataType::F32, DataType::U16) => normalize_impl!(f32, u16),
            (DataType::F32, DataType::F32) => normalize_impl!(f32, f32),

            _ => return false,
        }
    }

    true
}

#[unsafe(no_mangle)]
pub extern "C" fn bayer_rggb_to_rggb(input: *const CRawArray, output: *mut CRawArray) -> bool {
    if input.is_null() || output.is_null() {
        return false;
    }

    unsafe {
        let input = &*input;
        let output = &mut *output;

        if input.dtype != DataType::U16 || output.dtype != DataType::U16 {
            return false;
        }

        let [in_height, in_width, in_channels] = input.dims;
        let [out_height, out_width, out_channels] = output.dims;
        if in_channels != 1 || out_channels != 4 {
            return false;
        }
        if out_height * 2 != in_height || out_width * 2 != in_width {
            return false;
        }

        let in_height = in_height as usize;
        let in_width = in_width as usize;
        let out_height = out_height as usize;
        let out_width = out_width as usize;
        let input_ptr = input.data as *const u16;
        let output_ptr = output.data as *mut u16;

        let input = ArrayView3::from_shape_ptr((in_height, in_width, 1), input_ptr);
        let mut output =
            ArrayViewMut3::from_shape_ptr((out_height / 2, out_width / 2, 4), output_ptr);

        for y in 0..in_height / 2 {
            for x in 0..in_width / 2 {
                let y2 = y * 2;
                let x2 = x * 2;

                output[[y, x, 0]] = input[[y2, x2, 0]];
                output[[y, x, 1]] = input[[y2, x2 + 1, 0]];
                output[[y, x, 2]] = input[[y2 + 1, x2, 0]];
                output[[y, x, 3]] = input[[y2 + 1, x2 + 1, 0]];
            }
        }
    }

    true
}

#[unsafe(no_mangle)]
pub extern "C" fn rggb_to_bayer_rggb(input: *const CRawArray, output: *mut CRawArray) -> bool {
    if input.is_null() || output.is_null() {
        return false;
    }

    unsafe {
        let input = &*input;
        let output = &mut *output;

        if input.dtype != DataType::U16 || output.dtype != DataType::U16 {
            return false;
        }

        let [in_height, in_width, in_channels] = input.dims;
        let [out_height, out_width, out_channels] = output.dims;
        if in_channels != 4 || out_channels != 1 {
            return false;
        }
        if out_height != in_height * 2 || out_width != in_width * 2 {
            return false;
        }

        let in_height = in_height as usize;
        let in_width = in_width as usize;
        let out_height = out_height as usize;
        let out_width = out_width as usize;
        let input_ptr = input.data as *const u16;
        let output_ptr = output.data as *mut u16;

        let input = ArrayView3::from_shape_ptr((in_height / 2, in_width / 2, 4), input_ptr);
        let mut output = ArrayViewMut3::from_shape_ptr((out_height, out_width, 1), output_ptr);

        for y in 0..in_height {
            for x in 0..in_width {
                let r = input[[y, x, 0]];
                let g1 = input[[y, x, 1]];
                let g2 = input[[y, x, 2]];
                let b = input[[y, x, 3]];

                let out_y = y * 2;
                let out_x = x * 2;

                output[[out_y, out_x, 0]] = r;
                output[[out_y, out_x + 1, 0]] = g1;
                output[[out_y + 1, out_x, 0]] = g2;
                output[[out_y + 1, out_x + 1, 0]] = b;
            }
        }
    }

    true
}

#[unsafe(no_mangle)]
pub extern "C" fn bayer_rggb_to_rgb(input: *const CRawArray, output: *mut CRawArray) -> bool {
    if input.is_null() || output.is_null() {
        return false;
    }

    unsafe {
        let input = &*input;
        let output = &mut *output;

        if input.dtype != DataType::U16 || output.dtype != DataType::U16 {
            return false;
        }
        let [in_height, in_width, in_channels] = input.dims;
        let [out_height, out_width, out_channels] = output.dims;
        if in_channels != 1 || out_channels != 3 {
            return false;
        }
        let in_height = in_height as usize;
        let in_width = in_width as usize;
        let out_height = out_height as usize;
        let out_width = out_width as usize;
        let input_ptr = input.data as *const u16;
        let output_ptr = output.data as *mut u16;

        let input = ArrayView3::from_shape_ptr((in_height, in_width, 1), input_ptr);
        let mut output = ArrayViewMut3::from_shape_ptr((out_height, out_width, 3), output_ptr);

        for y in 1..in_height - 1 {
            for x in 1..in_width - 1 {
                let r;
                let g;
                let b;

                if y % 2 == 0 {
                    if x % 2 == 0 {
                        r = input[[y, x, 0]];
                        g = ((input[[y, x - 1, 0]] + input[[y, x + 1, 0]] +
                              input[[y - 1, x, 0]] + input[[y + 1, x, 0]]) / 4) as u16;
                        b = ((input[[y - 1, x - 1, 0]] + input[[y - 1, x + 1, 0]] +
                              input[[y + 1, x - 1, 0]] + input[[y + 1, x + 1, 0]]) / 4) as u16;
                    } else {
                        g = input[[y, x, 0]];
                        r = ((input[[y, x - 1, 0]] + input[[y, x + 1, 0]]) / 2) as u16;
                        b = ((input[[y - 1, x, 0]] + input[[y + 1, x, 0]]) / 2) as u16;
                    }
                } else {
                    if x % 2 == 0 {
                        g = input[[y, x, 0]];
                        r = ((input[[y - 1, x, 0]] + input[[y + 1, x, 0]]) / 2) as u16;
                        b = ((input[[y, x - 1, 0]] + input[[y, x + 1, 0]]) / 2) as u16;
                    } else {
                        b = input[[y, x, 0]];
                        g = ((input[[y, x - 1, 0]] + input[[y, x + 1, 0]] +
                              input[[y - 1, x, 0]] + input[[y + 1, x, 0]]) / 4) as u16;
                        r = ((input[[y - 1, x - 1, 0]] + input[[y - 1, x + 1, 0]] +
                              input[[y + 1, x - 1, 0]] + input[[y + 1, x + 1, 0]]) / 4) as u16;
                    }
                }

                output[[y, x, 0]] = r;
                output[[y, x, 1]] = g;
                output[[y, x, 2]] = b;
            }
        }
    }

    true
}

#[unsafe(no_mangle)]
pub extern "C" fn apply_ccm(rgb: *mut CRawArray, ccm: *const [f32; 9]) -> bool {
    if rgb.is_null() {
        return false;
    }

    unsafe {
        let rgb = &mut *rgb;
        let [height, width, channels] = rgb.dims;
        if rgb.dtype != DataType::F32 || channels != 3 {
            return false;
        }

        let height = height as usize;
        let width = width as usize;
        let data_ptr = rgb.data as *mut f32;

        let ccm = ArrayView2::from_shape_ptr((3, 3), ccm as *const f32);
        let mut img = ArrayViewMut3::from_shape_ptr((height, width, 3), data_ptr);

        for y in 0..height {
            for x in 0..width {
                let r = img[[y, x, 0]];
                let g = img[[y, x, 1]];
                let b = img[[y, x, 2]];

                img[[y, x, 0]] =
                    (ccm[[0, 0]] * r + ccm[[0, 1]] * g + ccm[[0, 2]] * b).clamp(0.0, 1.0);
                img[[y, x, 1]] =
                    (ccm[[1, 0]] * r + ccm[[1, 1]] * g + ccm[[1, 2]] * b).clamp(0.0, 1.0);
                img[[y, x, 2]] =
                    (ccm[[2, 0]] * r + ccm[[2, 1]] * g + ccm[[2, 2]] * b).clamp(0.0, 1.0);
            }
        }
    }

    true
}

#[unsafe(no_mangle)]
pub extern "C" fn apply_rgb_gain(rgb: *mut CRawArray, gains: *const [f32; 3]) -> bool {
    if rgb.is_null() {
        return false;
    }

    unsafe {
        let rgb = &mut *rgb;
        let [height, width, channels] = rgb.dims;
        if rgb.dtype != DataType::F32 || channels != 3 {
            return false;
        }


        let height = height as usize;
        let width = width as usize;
        let data_ptr = rgb.data as *mut f32;

        let gains = &*gains;
        if gains[0] < 0.0 || gains[1] < 0.0 || gains[2] < 0.0 {
            return false;
        }

        let mut img = ArrayViewMut3::from_shape_ptr((height, width, 3), data_ptr);

        for y in 0..height {
            for x in 0..width {
                img[[y, x, 0]] = (img[[y, x, 0]] * gains[0]).clamp(0.0, 1.0);
                img[[y, x, 1]] = (img[[y, x, 1]] * gains[1]).clamp(0.0, 1.0);
                img[[y, x, 2]] = (img[[y, x, 2]] * gains[2]).clamp(0.0, 1.0);
            }
        }
    }

    true
}
