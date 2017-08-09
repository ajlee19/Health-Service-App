cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS}  -Wall -Werror")
add_definitions("-DANDROID_NDK -DDISABLE_IMPORTGL")

add_library(BPAlgorithm SHARED
            com_samsung_sds_mhs_android_BPAlgorithm.c
            )

# Include libraries needed for sanangeles lib
target_link_libraries(BPAlgorithm
                      bp_estimate
                      android
                      dl
                      GLESv1_CM
                      log
                      m)
