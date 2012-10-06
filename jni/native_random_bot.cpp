#include <string.h>
#include <jni.h>

class BitBoard {
  public:
    bool PlayBotMove() {
      return false;
    }
};

extern "C" {
  JNIEXPORT jboolean JNICALL Java_com_android_checkers_NativeRandomBot_playNativeBotMove(
    JNIEnv* environment,
    jobject native_random_bot,
    jint white_pieces,
    jint black_pieces,
    jboolean is_white_player,
    jboolean is_move_again_mode);
}

jboolean Java_com_android_checkers_NativeRandomBot_playNativeBotMove(
    JNIEnv* environment,
    jobject native_random_bot,
    jint white_pieces,
    jint black_pieces,
    jboolean is_white_player,
    jboolean is_move_again_mode) {
  BitBoard board;
  return board.PlayBotMove();
}
