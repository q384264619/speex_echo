#最小版本号
CMAKE_MINIMUM_REQUIRED(VERSION 3.4.1)

add_definitions(-D HAVE_CONFIG_H)
#add_definitions(-D FLOATING_POINT)
#add_definitions(-D USE_KISS_FFT)
#add_definitions(-D EXPORT=JNIEXPORT)

# Specifies a path to native header files.

include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/include
        ${CMAKE_CURRENT_SOURCE_DIR}/include/speex
        ${CMAKE_CURRENT_SOURCE_DIR}/libspeexdsp
        )

#添加指定文件目录下面，所有源文件
AUX_SOURCE_DIRECTORY(${CMAKE_CURRENT_SOURCE_DIR}/libspeexdsp SpeexSourceDir)

message(STATUS "sourceFiles:${SpeexSourceDir}")

#添加自己所需的静态库 库多的话，可以使用下面的方式添加
add_library(
        #special name of lib
        speexdsp
        #set the library as shared library
        SHARED
        #source files
        ${SpeexSourceDir}
)







