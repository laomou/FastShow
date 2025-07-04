cmake_minimum_required(VERSION 3.16)

project(viewer VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(Qt6 REQUIRED COMPONENTS Quick Core Qml)

qt_standard_project_setup(REQUIRES 6.8)

qt_add_executable(viewer
    cpp/main.cc
)

qt_add_qml_module(viewer
    URI ui
    VERSION 1.0
    QML_FILES
        qml/ImageTile.qml
        qml/ImageViewer.qml
        SOURCES cpp/rgba_image_provider.h cpp/rgba_image_provider.cpp
)

# Qt for iOS sets MACOSX_BUNDLE_GUI_IDENTIFIER automatically since Qt 6.1.
# If you are developing for iOS or macOS you should consider setting an
# explicit, fixed bundle identifier manually though.
set_target_properties(viewer PROPERTIES
#    MACOSX_BUNDLE_GUI_IDENTIFIER com.laomou.viewer
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

target_link_libraries(viewer
    PRIVATE Qt6::Quick
)

include(GNUInstallDirs)
install(TARGETS viewer
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)
