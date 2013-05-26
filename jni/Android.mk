LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := native-bot
LOCAL_SRC_FILES := native_bot.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
