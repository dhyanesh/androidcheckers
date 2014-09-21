LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CPP_EXTENSION := .cc
LOCAL_MODULE    := native_bot
LOCAL_SRC_FILES := native_bot.cc
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
