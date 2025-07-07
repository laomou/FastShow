cmake_minimum_required(VERSION 3.16)

project(fs VERSION 0.1)

add_custom_target(build_rust ALL
    COMMAND cargo build --release
    WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
    COMMENT "Building Rust project with Cargo"
)
