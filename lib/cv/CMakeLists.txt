cmake_minimum_required(VERSION 3.16)

project(cv VERSION 0.1)

set(RUST_TARGET_DIR "${CMAKE_CURRENT_BINARY_DIR}/rust_target")

add_custom_target(cv_rust_lib ALL
    COMMAND ${CARGO} build --release
           --manifest-path ${CMAKE_CURRENT_SOURCE_DIR}/Cargo.toml
           --target-dir ${RUST_TARGET_DIR}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    COMMENT "Compiling Rust library"
    VERBATIM
)

if(UNIX)
    set(RUST_LIB_NAME "libcv.so")
elseif(WIN32)
    set(RUST_LIB_NAME "cv.dll")
    if(MINGW)
        set(RUST_IMPLIB_NAME "libcv.dll.a")
        set(RUST_IMPLIB "${RUST_TARGET_DIR}/release/${RUST_IMPLIB_NAME}")
    endif()
endif()
set(RUST_LIB "${RUST_TARGET_DIR}/release/${RUST_LIB_NAME}")

add_library(cv_rust SHARED IMPORTED GLOBAL)

set_target_properties(cv_rust PROPERTIES
    IMPORTED_LOCATION ${RUST_LIB}
    INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_SOURCE_DIR}/include
)

if(MINGW AND RUST_IMPLIB)
    set_target_properties(cv_rust PROPERTIES
        IMPORTED_IMPLIB ${RUST_IMPLIB}
    )
endif()

add_dependencies(cv_rust cv_rust_lib)
