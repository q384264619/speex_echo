#最小版本号
CMAKE_MINIMUM_REQUIRED(VERSION 3.4.1)


add_definitions(-D HAVE_CONFIG_H)

#speex dir
set(libSpeexSrcDir ../speexdsp )
#
include_directories(
        ./include
        ./../logs
        )

#include speex header
include_directories(
        ${libSpeexSrcDir}/include
        ${libSpeexSrcDir}/include/speex
        ${libSpeexSrcDir}/libspeexdsp
)


#添加指定文件目录下面，所有源文件 speex
AUX_SOURCE_DIRECTORY(${libSpeexSrcDir}/libspeexdsp SpeexSourceFiles)

message(STATUS "sourceFiles:${SpeexSourceFiles}")

# add myself source file
AUX_SOURCE_DIRECTORY(
        ${CMAKE_CURRENT_SOURCE_DIR} SpeexEcho)

message(STATUS "SpeexEcho sourceFiles:${SpeexEcho}")

add_library(
        #special name of lib
        speexecho
        #set the library as shared library
        SHARED
        #source files
        ${SpeexSourceFiles}
        ${SpeexEcho}
)

target_link_libraries(speexecho

        log)




