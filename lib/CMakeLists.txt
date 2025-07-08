cmake_minimum_required(VERSION 3.16)

find_program(CARGO cargo
    HINTS "$ENV{HOME}/.cargo/bin"
    PATHS "/usr/local/bin"
    REQUIRED
)

add_subdirectory(cv)
# add_subdirectory(fs)
