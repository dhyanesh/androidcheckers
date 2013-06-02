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

   bool IsWhitePiece(int x, int y) const {
     return IsPiecePresent(white_piece_set_, x, y);
   }

   bool IsBlackPiece(int x, int y) const {
     return IsPiecePresent(black_piece_set_, x, y);
   }

   unsigned int NumWhitePieces() const {
     return CountBits(white_piece_set());
   }

   unsigned int NumBlackPieces() const {
     return CountBits(black_piece_set());
   }

   void AppendNextWhiteStates(std::vector<BitBoard> *states) const {
     const bool is_white_player = true;
     AppendGameStatesForPlayer(is_white_player, states);
   }

   void AppendNextBlackStates(std::vector<BitBoard> *states) const {
     const bool is_white_player = false;
     AppendGameStatesForPlayer(is_white_player, states);
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

   static unsigned int CountBits(unsigned int piece_set) {
     int count = 0;
     for (count = 0; piece_set != 0; ++count) {
     }
     return count;
   }

   static bool IsPiecePresent(unsigned int piece_set, unsigned int mask) {
     return IsBitSet(piece_set, mask);
   }

   static bool IsPiecePresent(unsigned int piece_set, int x, int y) {
     return IsPiecePresent(piece_set, GetBitMaskForIndex(x, y));
   }

   static bool IsWithinBoard(int x, int y) {
     return x >= 0 && x < kBoardSize && y >= 0 && y < kBoardSize;
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

   // Returns true if the position specified by the mask is an empty square.
   bool IsEmptySquare(unsigned int position_mask) const {
     return !IsBitSet(white_piece_set_, position_mask) &&
       !IsBitSet(black_piece_set_, position_mask);
   }

   // Returns the piece set modified by moving a piece in current_piece_set
   // from a position indicated by current_mask to a position indicated by
   // next_move_mask.
   unsigned int GetNextPieceSetForMove(unsigned int current_mask,
                                       unsigned int next_move_mask,
                                       unsigned int current_piece_set) const {
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
                             std::vector<BitBoard> *states) const {
     // Return if not within board.
     if (!IsWithinBoard(x, y)) return;
     unsigned int next_move_mask = GetBitMaskForIndex(x, y);

     // If the square is not empty, we can't move there.
     if (!IsEmptySquare(next_move_mask)) return;

     unsigned int next_piece_set =
         GetNextPieceSetForMove(current_mask,
                                next_move_mask,
                                current_piece_set);

     if (is_white_player) {
       states->push_back(BitBoard(next_piece_set, black_piece_set_));
     } else {
       states->push_back(BitBoard(white_piece_set_, next_piece_set));
     }
   }

   // Creates a new jump state 'start' at (x, y) in the direction indicated by
   // (xdiff, ydiff) if there exists a valid jump for it and appends it to
   // 'states'.
   void MaybeAppendJumpState(const bool is_white_player,
                             const unsigned int current_mask,
                             const int x, const int y,
                             const int xdiff, const int ydiff,
                             unsigned int player_piece_set,
                             unsigned int opponent_piece_set,
                             std::vector<BitBoard> *states) const {
     // Return if the destination is not in the board.
     const int kill_x = x + xdiff;
     const int kill_y = y + ydiff;
     const int dest_x = kill_x + xdiff;
     const int dest_y = kill_y + ydiff;
     if (!IsWithinBoard(dest_x, dest_y)) return;

     unsigned int kill_piece_mask = GetBitMaskForIndex(kill_x, kill_y);
     // We need an opponent piece at kill_piece_mask.
     if (!IsPiecePresent(opponent_piece_set, kill_piece_mask)) return;

     unsigned int next_move_mask = GetBitMaskForIndex(dest_x, dest_y);

     // If the destination square is not empty, we can't move there.
     if (!IsEmptySquare(next_move_mask)) return;

     unsigned int next_player_piece_set =
         GetNextPieceSetForMove(current_mask,
                                next_move_mask,
                                player_piece_set);

     // We also need to kill the opponent's piece at kill_piece_mask.
     ClearBit(kill_piece_mask, &opponent_piece_set);

     if (is_white_player) {
       states->push_back(BitBoard(next_player_piece_set, opponent_piece_set));
     } else {
       states->push_back(BitBoard(opponent_piece_set, next_player_piece_set));
     }
   }

   // Appends the next possible game states for the player.
   void AppendGameStatesForPlayer(const bool is_white_player,
                                  std::vector<BitBoard> *states) const {
     int ydiff;
     unsigned int player_piece_set;
     unsigned int opponent_piece_set;
     if (is_white_player) {
       player_piece_set = white_piece_set_;
       opponent_piece_set = black_piece_set_;
       ydiff = 1;
     } else {
       player_piece_set = black_piece_set_;
       opponent_piece_set = white_piece_set_;
       ydiff = -1;
     }

     unsigned int current_mask = 1;
     for (int i = 0; i < 32; ++i, current_mask <<= 1) {
       if (!IsBitSet(player_piece_set, current_mask)) continue;

       int x, y;
       GetXYForBitIndex(i, &x, &y);

       MaybeAppendMoveState(is_white_player, current_mask, x + 1, y + ydiff,
                            player_piece_set, states);
       MaybeAppendMoveState(is_white_player, current_mask, x - 1, y + ydiff,
                            player_piece_set, states);
       MaybeAppendJumpState(is_white_player, current_mask, x, y, 1, ydiff,
                            player_piece_set, opponent_piece_set, states);
       MaybeAppendJumpState(is_white_player, current_mask, x, y, -1, ydiff,
                            player_piece_set, opponent_piece_set, states);
     }
   }

   unsigned int white_piece_set_;
   unsigned int black_piece_set_;
};

struct GameState {
  BitBoard board;
  bool is_white_player;
  bool is_move_again_mode;
};

class GameCore {
 public:
  GameCore(GameState *game_state) : game_state_(game_state) { }

  void AppendNextGameStates(std::vector<BitBoard> *next_states) const {
    // TODO(dhyanesh): Fix this to take into account is_move_again_mode.
    if (game_state_->is_white_player) {
      game_state_->board.AppendNextWhiteStates(next_states);
    } else {
      game_state_->board.AppendNextBlackStates(next_states);
    }
  }

  void DoMove(BitBoard next_state) {
    game_state_->board = next_state;
    // TODO(dhyanesh): Fix this to take into account is_move_again_mode.
    game_state_->is_white_player = !game_state_->is_white_player;
  }

  const GameState &game_state() const {
    return *game_state_;
  }

 private:
  GameState *game_state_;
};

class BotBase {
 public:
  BotBase(GameCore *game_core) : game_core_(game_core) { }

  // Plays the bot move. Returns false if no more moves are possible.
  virtual bool PlayMove() = 0;

 protected:
  GameCore *game_core() const {
    return game_core_;
  }

 private:
  GameCore *game_core_;
};

class RandomBot : public BotBase {
 public:
  RandomBot(GameCore *game_core) : BotBase(game_core) { }

  virtual bool PlayMove() {
    std::vector<BitBoard> next_states;
    game_core()->AppendNextGameStates(&next_states);

    if (next_states.empty()) return false;

    int index = random() % next_states.size();
    game_core()->DoMove(next_states[index]);

    return true;
  }
};

class MinMaxBot : public BotBase {
 public:
  MinMaxBot(GameCore *game_core) : BotBase(game_core) { }

  virtual bool PlayMove() {
    std::vector<BitBoard> next_states;
    game_core()->AppendNextGameStates(&next_states);

    if (next_states.empty()) return false;

    int index = FindBestMove(next_states);

    game_core()->DoMove(next_states[index]);

    return true;
  }

 private:
  int GameScore() const {
    const BitBoard &board = game_core()->game_state().board;
    return board.NumWhitePieces() - board.NumBlackPieces();
  }

  int FindBestMove(const std::vector<BitBoard> &states) {
  }
};

extern "C" {
  JNIEXPORT jboolean JNICALL Java_com_android_checkers_NativeBot_playNativeBotMove(
    JNIEnv* environment,
    jobject native_random_bot,
    jint white_pieces,
    jint black_pieces,
    jboolean is_white_player,
    jboolean is_move_again_mode);
}

jboolean Java_com_android_checkers_NativeBot_playNativeBotMove(
    JNIEnv* environment,
    jobject native_random_bot,
    jint white_pieces,
    jint black_pieces,
    jboolean is_white_player,
    jboolean is_move_again_mode) {
  GameState game_state;
  game_state.board.set_white_pieces(white_pieces);
  game_state.board.set_black_pieces(black_pieces);
  game_state.is_white_player = is_white_player;
  game_state.is_move_again_mode = is_move_again_mode;

  char buf[100];
  snprintf(buf, sizeof(buf), "(%x, %x)",
           game_state.board.white_piece_set(),
           game_state.board.black_piece_set());
  __android_log_print(ANDROID_LOG_INFO, "native_bot.cc",
      buf);

  GameCore game_core(&game_state);
  BotBase *bot = new RandomBot(&game_core);
  if (!bot->PlayMove()) return false;
   jclass native_random_bot_class_id =
       environment->GetObjectClass(native_random_bot);
   if (native_random_bot_class_id == NULL) {
     return false;
   }

   // Get the Java moveResult object reference.
   jfieldID move_result_field_id = environment->GetFieldID(
       native_random_bot_class_id, "moveResult",
       "Lcom/android/checkers/NativeBot$MoveResult;");
   jobject move_result =
       environment->GetObjectField(native_random_bot, move_result_field_id);

   jclass move_result_class_id = environment->GetObjectClass(move_result);

   jfieldID white_pieces_field_id = environment->GetFieldID(
       move_result_class_id, "whitePieces", "I");
   environment->SetIntField(move_result, white_pieces_field_id,
                            game_state.board.white_piece_set());

   jfieldID black_pieces_field_id = environment->GetFieldID(
       move_result_class_id, "blackPieces", "I");
   environment->SetIntField(move_result, black_pieces_field_id,
                            game_state.board.black_piece_set());

  return true;
}
