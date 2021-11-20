
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("androidcameracontrol")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(ANDROID_OPENCV_COMPONENTS "opencv_java" CACHE STRING "")
message(STATUS "ANDROID_ABI=${ANDROID_ABI}")
find_package(OpenCV REQUIRED COMPONENTS ${ANDROID_OPENCV_COMPONENTS})


# ------------------------------------------------------------------
# -- DLIB
# ------------------------------------------------------------------
set(DLIB_PATH ${CMAKE_SOURCE_DIR}/dlib)


add_library(dlib SHARED IMPORTED)

# sets the location of the prebuilt dlib .so
set_target_properties(dlib
        PROPERTIES IMPORTED_LOCATION
        ${DLIB_PATH}/lib/${ANDROID_ABI}/libdlib.so)

include_directories(${DLIB_PATH}/include
        ${CMAKE_SOURCE_DIR}/dlib_natives)
# ------------------------------------------------------------------


add_library(dlib_natives

        SHARED

        dlib_natives/dlib_utility.cpp

        facedetectors/dlib_FaceDetectionHOG.cpp
        facedetectors/dlib_FaceDetectionMMOD.cpp
        facemarks/dlib_FacemarksKazemi.cpp
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries(dlib_natives

        dlib
        ${ANDROID_OPENCV_COMPONENTS}
        )
