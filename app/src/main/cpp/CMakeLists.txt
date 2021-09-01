cmake_minimum_required(VERSION 3.10.2)

project("dlibandroidfacelandmark")

add_subdirectory(./dlib_android dlib_android_build)

find_library(
        android-lib
        android
)

find_library(
        log-lib
        log
)

add_library(
        dlib_face_result
        SHARED
        dlib_face_result.cpp
)

target_link_libraries(dlib_face_result dlib_utils)
target_link_libraries(dlib_face_result ${log-lib})
target_link_libraries(dlib_face_result ${android-lib})
target_link_libraries(dlib_face_result -ljnigraphics)