use crate::local::LocalFileSystem;

#[repr(C)]
pub struct CFSHandle(*mut std::ffi::c_void);


#[unsafe(no_mangle)]
pub extern "C" fn fs_create_local() -> *mut LocalFileSystem {
    Box::into_raw(Box::new(LocalFileSystem::default()))
}

#[unsafe(no_mangle)]
pub extern "C" fn fs_free(handle: *mut LocalFileSystem) {
    unsafe { let _ = Box::from_raw(handle); };
}