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
#include <vector>
#include <jni.h>

class BitBoard {
 public:
   BitBoard() : white_pieces_(0), black_pieces_(0) { }

   BitBoard(unsigned int white_pieces, unsigned int black_pieces)
     : white_pieces_(white_pieces),
       black_pieces_(black_pieces) { }

   void set_white_pieces(unsigned int white_pieces) {
     white_pieces_ = white_pieces;
   }

   void set_black_pieces(unsigned int black_pieces) {
     black_pieces_ = black_pieces;
   }

   bool IsWhitePiece(int x, int y) {
     return IsPiecePresent(white_pieces_, x, y);
   }

   bool IsBlackPiece(int x, int y) {
     return IsPiecePresent(black_pieces_, x, y);
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

   // Returns the next piece set if it is possible to move to the position
   // indicated by next_move_mask. Otherwise returns piece_set as is.
   unsigned int GetNextPieceSetForMove(unsigned int current_mask,
                                       unsigned int next_move_mask,
                                       unsigned int piece_set) {
     if (IsBitSet(white_pieces_, next_move_mask) ||
         IsBitSet(black_pieces_, next_move_mask)) {
       return piece_set;
     }

     unsigned int next_piece_set = piece_set;
     // Clear the bit for the current position mask.
     ClearBit(current_mask, &next_piece_set);
     // Set it for the next position mask.
     SetBit(next_move_mask, &next_piece_set);
     return next_piece_set;
   }

   void AppendMovesStates(const bool is_white_player,
                          std::vector<BitBoard> *states) {
     if (is_white_player) {
       const int ydiff = -1;
       unsigned int current_mask = 1;
       for (int i = 0; i < 32; ++i) {
         if (!IsBitSet(white_pieces_, current_mask)) continue;

         int x, y;
         GetXYForBitIndex(i, &x, &y);

         unsigned int next_move_mask = GetBitMaskForIndex(x + 1, y + ydiff);
         unsigned int next_white_pieces = GetNextPieceSetForMove(current_mask,
                                                                 next_move_mask,
                                                                 white_pieces_);
         if (next_white_pieces != white_pieces_) {
           states->push_back(BitBoard(next_white_pieces, black_pieces_));
         }
         next_move_mask = GetBitMaskForIndex(x - 1, y + ydiff);
         next_white_pieces = GetNextPieceSetForMove(current_mask,
                                                    next_move_mask,
                                                    white_pieces_);
         if (next_white_pieces != white_pieces_) {
           states->push_back(BitBoard(next_white_pieces, black_pieces_));
         }

         current_mask <<= 1;
       }
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

   unsigned int white_pieces_;
   unsigned int black_pieces_;
};

struct GameState {
  BitBoard board;
  bool is_white_player;
  bool is_move_again_mode;
};

class BotBase {
 public:
  BotBase(const GameState &game_state) : game_state_(game_state) { }

  virtual bool PlayMove() = 0;

 protected:
  const GameState &game_state() const {
    return game_state_;
  }

 private:
  GameState game_state_;
};

class RandomBot : public BotBase {
 public:
  RandomBot(const GameState &game_state) : BotBase(game_state) { }

  virtual bool PlayMove() {
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
  GameState state;
  state.board.set_white_pieces(white_pieces);
  state.board.set_black_pieces(black_pieces);
  state.is_white_player = is_white_player;
  state.is_move_again_mode = is_move_again_mode;

  RandomBot bot(state);
  return bot.PlayMove();
}
