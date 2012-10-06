LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := native-random-bot
LOCAL_SRC_FILES := native_random_bot.cpp

include $(BUILD_SHARED_LIBRARY)
