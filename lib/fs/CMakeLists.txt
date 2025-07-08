cmake_minimum_required(VERSION 3.16)

project(fs VERSION 0.1)

set(RUST_TARGET_DIR "${CMAKE_CURRENT_BINARY_DIR}/rust_target")

add_custom_target(fs_rust_lib ALL
    COMMAND ${CARGO} build --release
           --manifest-path ${CMAKE_CURRENT_SOURCE_DIR}/Cargo.toml
           --target-dir ${RUST_TARGET_DIR}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    COMMENT "Compiling Rust library"
    VERBATIM
)

if(UNIX)
    set(RUST_LIB_NAME "libfs.so")
elseif(WIN32)
    set(RUST_LIB_NAME "fs.dll")
endif()
set(RUST_LIB "${RUST_TARGET_DIR}/release/${RUST_LIB_NAME}")

add_library(fs_rust SHARED IMPORTED GLOBAL)
set_target_properties(fs_rust PROPERTIES
    IMPORTED_LOCATION ${RUST_LIB}
    INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_SOURCE_DIR}/include
)

add_dependencies(fs_rust fs_rust_lib)
