#最小版本号
CMAKE_MINIMUM_REQUIRED(VERSION 3.4.1)
#工程名字
#语法：project(projectname [cxx] [c] [java])
#可以指定工程采用的语言，选项分别表示：C++, C, java， 如不指定默认支持所有语言
PROJECT(SpeexEcho)
#打印相关消息
MESSAGE(STATUS "Project:${PROJECT_NAME}")
#打印相关消息
MESSAGE(STATUS "Project Dir:${PROJECT_SOURCE_DIR}")

MESSAGE(STATUS "Current source dir:${CMAKE_CURRENT_SOURCE_DIR}")

MESSAGE(STATUS "cross-os:${CMAKE_OSX_SYSROOT}")

MESSAGE(STATUS "compile-c:${CMAKE_C_COMPILER_LOADED}")

SET(CMAKE_BUILD_TYPE DEBUG)

SET(CMAKE_CXX_FLAGS "-Wno-error=format-security -Wno-error=pointer-sign ")



SET(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../../../libs/${ANDROID_ABI})


ADD_SUBDIRECTORY(echo)





