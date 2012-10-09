//  BitBoard representation
//
//  # 28 # 29 # 30 # 31
//  24 # 25 # 26 # 27 #
//  # 20 # 21 # 22 # 23
//  16 # 17 # 18 # 19 #
//  # 12 # 13 # 14 # 15
//  08 # 09 # 10 # 11 #
//  # 04 # 05 # 06 # 07
//  00 # 01 # 02 # 03 #
//
#include <cstdio>
#include <cstdlib>
#include <vector>
#include <jni.h>
#include <android/log.h>

class BitBoard {
 public:
   BitBoard() : white_piece_set_(0), black_piece_set_(0) { }

   BitBoard(unsigned int white_pieces, unsigned int black_pieces)
     : white_piece_set_(white_pieces),
       black_piece_set_(black_pieces) { }

   void set_white_pieces(unsigned int white_pieces) {
     white_piece_set_ = white_pieces;
   }

   void set_black_pieces(unsigned int black_pieces) {
     black_piece_set_ = black_pieces;
   }

   unsigned int white_piece_set() const {
     return white_piece_set_;
   }

   unsigned int black_piece_set() const {
     return black_piece_set_;
   }

   bool IsWhitePiece(int x, int y) {
     return IsPiecePresent(white_piece_set_, x, y);
   }

   bool IsBlackPiece(int x, int y) {
     return IsPiecePresent(black_piece_set_, x, y);
   }

   void AppendNextWhiteStates(std::vector<BitBoard> *states) {
     const bool is_white_player = true;
     AppendMovesStates(is_white_player, states);
     AppendJumpStates(is_white_player, states);
   }

   void AppendNextBlackStates(std::vector<BitBoard> *states) {
     const bool is_white_player = false;
     AppendMovesStates(is_white_player, states);
     AppendJumpStates(is_white_player, states);
   }

 private:
   static const int kBoardSize = 8;

   static bool IsBitSet(unsigned int piece_set, unsigned int mask) {
     return (piece_set & mask) != 0;
   }

   static void ClearBit(unsigned int mask, unsigned int *piece_set) {
     *piece_set &= ~mask;
   }

   static void SetBit(unsigned int mask, unsigned int *piece_set) {
     *piece_set |= mask;
   }

   static bool IsPiecePresent(unsigned int piece_set, int x, int y) {
     return IsBitSet(piece_set, GetBitMaskForIndex(x, y));
   }

   static bool IsWithinBoard(int x, int y) {
     return x >= 0 && x < kBoardSize && y >= 0 && y < kBoardSize;
   }

   // Returns true if the position specified by the mask is an empty square.
   bool IsEmptySquare(unsigned int position_mask) {
     return !IsBitSet(white_piece_set_, position_mask) &&
       !IsBitSet(black_piece_set_, position_mask);
   }

   // Returns the piece set modified by moving a piece in current_piece_set
   // from a position indicated by current_mask to a position indicated by
   // next_move_mask.
   unsigned int GetNextPieceSetForMove(unsigned int current_mask,
                                       unsigned int next_move_mask,
                                       unsigned int current_piece_set) {
     unsigned int next_piece_set = current_piece_set;
     // Clear the bit for the current position mask.
     ClearBit(current_mask, &next_piece_set);
     // Set it for the next position mask.
     SetBit(next_move_mask, &next_piece_set);
     return next_piece_set;
   }

   // Creates a new move state for (x, y) if there exists a valid move for it
   // and appends it to 'states'.
   void MaybeAppendMoveState(const bool is_white_player,
                             const unsigned int current_mask,
                             const int x, const int y,
                             unsigned int current_piece_set,
                             std::vector<BitBoard> *states) {
     // Return if not within board.
     if (!IsWithinBoard(x, y)) return;
     unsigned int next_move_mask = GetBitMaskForIndex(x, y);

     // If the square is not empty, we can't move there.
     if (!IsEmptySquare(next_move_mask)) return;

     unsigned int next_piece_set =
         GetNextPieceSetForMove(current_mask,
                                next_move_mask,
                                current_piece_set);

     if (current_piece_set == next_piece_set) return;

     if (is_white_player) {
       states->push_back(BitBoard(next_piece_set, black_piece_set_));
     } else {
       states->push_back(BitBoard(white_piece_set_, next_piece_set));
     }
   }

   // Appends the next possible move states for the pieces in
   // 'current_piece_set'.
   void AppendMoveStatesForPieceSet(const bool is_white_player,
                                    unsigned int current_piece_set,
                                    std::vector<BitBoard> *states) {
     const int ydiff = is_white_player ? 1 : -1;
     unsigned int current_mask = 1;
     for (int i = 0; i < 32; ++i, current_mask <<= 1) {
       if (!IsBitSet(current_piece_set, current_mask)) continue;

       int x, y;
       GetXYForBitIndex(i, &x, &y);
       char buf[100];
       snprintf(buf, sizeof(buf), "(%d, %d)", x, y);
       __android_log_print(ANDROID_LOG_INFO, "NativeRandomBot.cc",
           buf);

       MaybeAppendMoveState(is_white_player, current_mask, x + 1, y + ydiff,
                            current_piece_set, states);
       MaybeAppendMoveState(is_white_player, current_mask, x - 1, y + ydiff,
                            current_piece_set, states);
     }
   }

   // Appends the next 'move' states into 'states'. This does not include
   // jumps. Those are filled in by AppendJumpStates.
   void AppendMovesStates(const bool is_white_player,
                          std::vector<BitBoard> *states) {
     if (is_white_player) {
       AppendMoveStatesForPieceSet(is_white_player, white_piece_set_, states);
     } else {
       AppendMoveStatesForPieceSet(is_white_player, black_piece_set_, states);
     }
   }

   void AppendJumpStates(const bool is_white_player,
                         std::vector<BitBoard> *states) {
   }

   static unsigned int GetBitMaskForIndex(int x, int y) {
     return (1 << GetBitIndexForSquare(x, y));
   }

   // Obtains the bit index for a (x, y) position on the board.
   static int GetBitIndexForSquare(int x, int y) {
     return y * 4 + x / 2;
   }

   // Reverse of the function above.
   static void GetXYForBitIndex(int index, int *x, int *y) {
     *y = index / 4;
     *x = (index % 4) * 2 + (*y) % 2;
   }

   unsigned int white_piece_set_;
   unsigned int black_piece_set_;
};

struct GameState {
  BitBoard board;
  bool is_white_player;
  bool is_move_again_mode;
};

class BotBase {
 public:
  BotBase(GameState *game_state) : game_state_(game_state) { }

  virtual bool PlayMove() = 0;

 protected:
  GameState *game_state() {
    return game_state_;
  }

 private:
  GameState *game_state_;
};

class RandomBot : public BotBase {
 public:
  RandomBot(GameState *game_state) : BotBase(game_state) { }

  virtual bool PlayMove() {
    std::vector<BitBoard> next_states;
    if (game_state()->is_white_player) {
      game_state()->board.AppendNextWhiteStates(&next_states);
      __android_log_print(ANDROID_LOG_INFO, "NativeRandomBot.cc",
                          "White player.");
    } else {
      game_state()->board.AppendNextBlackStates(&next_states);
      __android_log_print(ANDROID_LOG_INFO, "NativeRandomBot.cc",
                          "Black player.");
    }

    if (next_states.empty()) return false;

    int index = random() % next_states.size();
    game_state()->board = next_states[index];

    return true;
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
  GameState state;
  state.board.set_white_pieces(white_pieces);
  state.board.set_black_pieces(black_pieces);
  state.is_white_player = is_white_player;
  state.is_move_again_mode = is_move_again_mode;

  char buf[100];
  snprintf(buf, sizeof(buf), "(%x, %x)", state.board.white_piece_set(), state.board.black_piece_set());
  __android_log_print(ANDROID_LOG_INFO, "NativeRandomBot.cc",
      buf);

  RandomBot bot(&state);
  if (!bot.PlayMove()) return false;

   jclass native_random_bot_class_id =
       environment->GetObjectClass(native_random_bot);
   if (native_random_bot_class_id == NULL) {
      // printf("GetObjectClass returned NULL\n");
      return false;
   }

   // Get the Java moveResult object reference.
   jfieldID move_result_field_id = environment->GetFieldID(
       native_random_bot_class_id, "moveResult",
       "Lcom/android/checkers/NativeRandomBot$MoveResult;");
   jobject move_result =
       environment->GetObjectField(native_random_bot, move_result_field_id);

   jclass move_result_class_id = environment->GetObjectClass(move_result);

   jfieldID white_pieces_field_id = environment->GetFieldID(
       move_result_class_id, "whitePieces", "I");
   environment->SetIntField(move_result, white_pieces_field_id,
                            state.board.white_piece_set());

   jfieldID black_pieces_field_id = environment->GetFieldID(
       move_result_class_id, "blackPieces", "I");
   environment->SetIntField(move_result, black_pieces_field_id,
                            state.board.black_piece_set());

  return true;
}
