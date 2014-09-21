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
#include <cassert>
#include <cstdio>
#include <cstdlib>
#include <sstream>
#include <string>
#include <vector>
#include <jni.h>
#include <android/log.h>

// Obtains the bit index for a (x, y) position on the board.
inline int GetBitIndexForSquare(int x, int y) {
 return y * 4 + x / 2;
}

// Reverse of the function above.
inline void GetXYForBitIndex(int index, int *x, int *y) {
 *y = index / 4;
 *x = (index % 4) * 2 + (*y) % 2;
}

inline unsigned int GetPositionMaskForIndex(int x, int y) {
 return (1 << GetBitIndexForSquare(x, y));
}

struct Position {
  int x, y;

  unsigned int GetPositionMask() const {
    return GetPositionMaskForIndex(x, y);
  }

  std::string DebugString() const {
    std::stringstream ss;
    ss << "(" << x << "," << y << ")";
    return ss.str();
  }
};

void IncrementPosition(Position *position) {
  position->x += 2;
  if (position->x >= 8) {
    ++position->y;
    position->x = position->y % 2;
  }
}

inline void GetPositionForBitIndex(int index, Position *position) {
  GetXYForBitIndex(index, &position->x, &position->y);
};

struct Move {
  Move() { }
  Move(const Position &st, const Position &en)
      : start(st), end(en) { }

  bool IsJump() const {
    return abs(end.x - start.x) == 2;
  }

  void FillJumpPosition(Position *position) const {
    assert(IsJump());
    position->x = (end.x + start.x) / 2;
    position->y = (end.y + start.y) / 2;
  }

  std::string DebugString() const {
    return "Start: " + start.DebugString() + " end: " + end.DebugString();
  }

  Position start;
  Position end;
};

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

   unsigned int NumWhitePieces() const {
     return CountBits(white_piece_set());
   }

   unsigned int NumBlackPieces() const {
     return CountBits(black_piece_set());
   }

   bool IsEmptySquare(const Position &position) const {
     return IsEmptySquare(position.GetPositionMask());
   }

   // Returns true if the position specified by the mask is an empty square.
   bool IsEmptySquare(unsigned int position_mask) const {
     return !IsPiecePresent(white_piece_set_, position_mask) &&
       !IsPiecePresent(black_piece_set_, position_mask);
   }

   void AppendNextWhiteMoveStates(std::vector<BitBoard> *states) const {
     const bool move_states = true;
     const bool is_white_player = true;
     AppendGameStatesForPlayer(move_states, is_white_player, states);
   }

   void AppendNextWhiteJumpStates(std::vector<BitBoard> *states) const {
     const bool move_states = false;
     const bool is_white_player = true;
     AppendGameStatesForPlayer(move_states, is_white_player, states);
   }

   void AppendNextBlackMoveStates(std::vector<BitBoard> *states) const {
     const bool move_states = true;
     const bool is_white_player = false;
     AppendGameStatesForPlayer(move_states, is_white_player, states);
   }

   void AppendNextBlackJumpStates(std::vector<BitBoard> *states) const {
     const bool move_states = false;
     const bool is_white_player = false;
     AppendGameStatesForPlayer(move_states, is_white_player, states);
   }

   void ApplyMove(const bool is_white_player, const Move &move) {
     unsigned int *player_piece_set = &white_piece_set_;
     unsigned int *opponent_piece_set = &black_piece_set_;
     if (!is_white_player) {
       player_piece_set = &black_piece_set_;
       opponent_piece_set = &white_piece_set_;
     }

     ClearPiece(move.start.GetPositionMask(), player_piece_set);
     SetPiece(move.end.GetPositionMask(), player_piece_set);
     if (move.IsJump()) {
       Position jump_position;
       move.FillJumpPosition(&jump_position);
       ClearPiece(jump_position.GetPositionMask(), opponent_piece_set);
     }
   }

   static bool IsPiecePresent(
       unsigned int piece_set, const Position &position) {
     return IsPiecePresent(piece_set, position.GetPositionMask());
   }

   static bool IsWithinBoard(int x, int y) {
     return x >= 0 && x < kBoardSize && y >= 0 && y < kBoardSize;
   }

   static bool IsWithinBoard(Position position) {
     return IsWithinBoard(position.x, position.y);
   }

   std::string DebugString() const {
     std::stringstream ss;
     ss << "white_piece_set: " << white_piece_set_
       << "black_piece_set: " << black_piece_set_;
     return ss.str();
   }

 private:
   static const int kBoardSize = 8;

   static bool IsPiecePresent(unsigned int piece_set,
                              unsigned int position_mask) {
     return (piece_set & position_mask) != 0;
   }

   static void ClearPiece(unsigned int position_mask, unsigned int *piece_set) {
     *piece_set &= ~position_mask;
   }

   static void SetPiece(unsigned int position_mask, unsigned int *piece_set) {
     *piece_set |= position_mask;
   }

   static unsigned int CountBits(unsigned int piece_set) {
     int count = 0;
     for (count = 0; piece_set != 0; ++count) {
       piece_set &= (piece_set - 1);
     }
     return count;
   }

   // Returns the piece set modified by moving a piece in current_piece_set
   // from a position indicated by position_mask to a position indicated by
   // next_move_mask.
   unsigned int GetNextPieceSetForMove(unsigned int position_mask,
                                       unsigned int next_move_mask,
                                       unsigned int current_piece_set) const {
     unsigned int next_piece_set = current_piece_set;
     // Clear the bit for the current position mask.
     ClearPiece(position_mask, &next_piece_set);
     // Set it for the next position mask.
     SetPiece(next_move_mask, &next_piece_set);
     return next_piece_set;
   }

   // Creates a new move state for (x, y) if there exists a valid move for it
   // and appends it to 'states'.
   void MaybeAppendMoveState(const bool is_white_player,
                             const unsigned int position_mask,
                             const int x, const int y,
                             unsigned int current_piece_set,
                             std::vector<BitBoard> *states) const {
     // Return if not within board.
     if (!IsWithinBoard(x, y)) return;
     unsigned int next_move_mask = GetPositionMaskForIndex(x, y);

     // If the square is not empty, we can't move there.
     if (!IsEmptySquare(next_move_mask)) return;

     unsigned int next_piece_set =
         GetNextPieceSetForMove(position_mask,
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
                             const unsigned int position_mask,
                             const int x, const int y,
                             const int xdiff, const int ydiff,
                             unsigned int player_piece_set,
                             unsigned int opponent_piece_set,
                             std::vector<BitBoard> *states) const {
     const int kill_x = x + xdiff;
     const int kill_y = y + ydiff;
     const int dest_x = kill_x + xdiff;
     const int dest_y = kill_y + ydiff;
     // Return if the destination is not in the board.
     if (!IsWithinBoard(dest_x, dest_y)) return;

     unsigned int kill_piece_mask = GetPositionMaskForIndex(kill_x, kill_y);
     // We need an opponent piece at kill_piece_mask.
     if (!IsPiecePresent(opponent_piece_set, kill_piece_mask)) return;

     unsigned int next_move_mask = GetPositionMaskForIndex(dest_x, dest_y);

     // If the destination square is not empty, we can't move there.
     if (!IsEmptySquare(next_move_mask)) return;

     unsigned int next_player_piece_set =
         GetNextPieceSetForMove(position_mask,
                                next_move_mask,
                                player_piece_set);

     // We also need to kill the opponent's piece at kill_piece_mask.
     ClearPiece(kill_piece_mask, &opponent_piece_set);

     if (is_white_player) {
       states->push_back(BitBoard(next_player_piece_set, opponent_piece_set));
     } else {
       states->push_back(BitBoard(opponent_piece_set, next_player_piece_set));
     }
   }

   // Appends the next possible game states for the player.
   void AppendGameStatesForPlayer(const bool move_states,
                                  const bool is_white_player,
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

     unsigned int position_mask = 1;
     for (int i = 0; i < 32; ++i, position_mask <<= 1) {
       if (!IsPiecePresent(player_piece_set, position_mask)) continue;

       int x, y;
       GetXYForBitIndex(i, &x, &y);

       if (move_states) {
         MaybeAppendMoveState(is_white_player, position_mask, x + 1, y + ydiff,
             player_piece_set, states);
         MaybeAppendMoveState(is_white_player, position_mask, x - 1, y + ydiff,
             player_piece_set, states);
       } else {
         MaybeAppendJumpState(is_white_player, position_mask, x, y, 1, ydiff,
             player_piece_set, opponent_piece_set, states);
         MaybeAppendJumpState(is_white_player, position_mask, x, y, -1, ydiff,
             player_piece_set, opponent_piece_set, states);
       }
     }
   }

   unsigned int white_piece_set_;
   unsigned int black_piece_set_;
};

class MoveGenerator {
 public:
   explicit MoveGenerator(const BitBoard &bitboard)
      : bitboard_(bitboard) { }

   void SetPlayer(const bool is_white_player) {
     if (is_white_player) {
       player_piece_set_ = bitboard_.white_piece_set();
       opponent_piece_set_ = bitboard_.black_piece_set();
       ydiff_ = 1;
     } else {
       player_piece_set_ = bitboard_.black_piece_set();
       opponent_piece_set_ = bitboard_.white_piece_set();
       ydiff_ = -1;
     }
   }

   void AddNextMoves(
       std::vector<Move> *moves) const {
     Position position;
     position.x = 0;
     position.y = 0;
     for (; position.y < 8; IncrementPosition(&position)) {
       if (!bitboard_.IsPiecePresent(player_piece_set_, position)) {
         continue;
       }

       AddSimpleMovesFromPosition(position, moves);
       AddJumpMovesFromPosition(position, moves);
     }
   }

 private:
   bool CanMoveToPosition(const Position &position) const {
     return bitboard_.IsWithinBoard(position) &&
         bitboard_.IsEmptySquare(position);
   }

   void MaybeAddSimpleMove(
       const Move &move, std::vector<Move> *moves) const {
     if (CanMoveToPosition(move.end)) {
       moves->push_back(move);
     }
   }

   void AddSimpleMovesFromPosition(const Position &position,
                                   std::vector<Move> *moves) const {
     Move move;
     move.start = position;

     move.end.x = move.start.x + 1;
     move.end.y = move.start.y + ydiff_;
     MaybeAddSimpleMove(move, moves);

     move.end.x = move.start.x - 1;
     move.end.y = move.start.y + ydiff_;
     MaybeAddSimpleMove(move, moves);
   }

   void MaybeAddJump(
       const Position &start, int xdiff, std::vector<Move> *moves) const {
     Position kill;
     kill.x = start.x + xdiff;
     kill.y = start.y + ydiff_;

     Position end;
     end.x = kill.x + xdiff;
     end.y = kill.y + ydiff_;

     if (!CanMoveToPosition(end)) return;

     // We need an opponent piece at the kill position.
     if (!bitboard_.IsPiecePresent(
           opponent_piece_set_, kill)) {
       return;
     }

     moves->push_back(Move(start, end));
   }

   void AddJumpMovesFromPosition(Position position,
                                 std::vector<Move> *moves) const {
     MaybeAddJump(position, 1, moves);
     MaybeAddJump(position, -1, moves);
   }

   const BitBoard &bitboard_;
   unsigned int player_piece_set_;
   unsigned int opponent_piece_set_;
   int ydiff_;
};

struct GameState {
  BitBoard board;
  Position last_jump_position;

  bool is_white_player;
  bool is_jump;

  std::string DebugString() const {
    std::stringstream ss;
    ss << "Board: " << board.DebugString()
       << " is_white_player: " << is_white_player
       << " is_jump: " << is_jump;
      //is_jump ? (" last_jump_position" + last_jump_position.DebugString()) : "";
    return ss.str();
  }
};

// Requires that move is a valid Move that can be applied to input_state.
void ApplyMove(const GameState &input_state,
               const Move &move,
               GameState *output_state) {
  assert(!input_state.board.is_jump || !move.IsJump());

  *output_state = input_state;
  output_state->board.ApplyMove(input_state.is_white_player, move);

  if (move.IsJump()) {
    output_state->is_jump = true;
    output_state->last_jump_position = move.end;
    output_state->is_white_player = input_state.is_white_player;
  } else {
    output_state->is_jump = false;
    output_state->is_white_player = !input_state.is_white_player;
  }
}

void AppendNextMoveStates(const std::vector<BitBoard> &next_boards,
                          const bool is_white_player,
                          std::vector<GameState> *next_states) {
  next_states->reserve(next_states->size() + next_boards.size());
  for (int i = 0; i < next_boards.size(); ++i) {
    next_states->push_back(GameState());

    GameState &state = next_states->back();
    state.board = next_boards[i];
    state.is_jump = false;
    state.is_white_player = !is_white_player;
  }
}

bool HasJumpStates(BitBoard board, bool is_white_player) {
  // TODO(dhyanesh): This is not entirely accurate. We need to check jump
  // states only from the last jump move.
  std::vector<BitBoard> states;
  if (is_white_player) {
    board.AppendNextWhiteJumpStates(&states);
  } else {
    board.AppendNextBlackJumpStates(&states);
  }
  return states.size() > 0;
}

void AppendNextJumpStates(const std::vector<BitBoard> &next_boards,
                          const bool is_white_player,
                          std::vector<GameState> *next_states) {
  next_states->reserve(next_states->size() + next_boards.size());
  for (int i = 0; i < next_boards.size(); ++i) {
    next_states->push_back(GameState());

    GameState &state = next_states->back();
    state.board = next_boards[i];
    state.is_jump = HasJumpStates(state.board,
                                             is_white_player);
    state.is_white_player = state.is_jump ?
      is_white_player : !is_white_player;
  }
}

void AppendNextGameStates(const GameState &game_state,
                          std::vector<GameState> *next_states) {
  std::vector<BitBoard> next_boards;
  if (game_state.is_white_player) {
    if (!game_state.is_jump) {
      game_state.board.AppendNextWhiteMoveStates(&next_boards);
      AppendNextMoveStates(
          next_boards, game_state.is_white_player,next_states);
    }
    next_boards.clear();

    game_state.board.AppendNextWhiteJumpStates(&next_boards);
    AppendNextJumpStates(
        next_boards, game_state.is_white_player, next_states);
  } else {
    if (!game_state.is_jump) {
      game_state.board.AppendNextBlackMoveStates(&next_boards);
      AppendNextMoveStates(
          next_boards, game_state.is_white_player, next_states);
    }

    next_boards.clear();
    game_state.board.AppendNextBlackJumpStates(&next_boards);
    AppendNextJumpStates(
        next_boards, game_state.is_white_player, next_states);
  }
}

class BotBase {
 public:
  BotBase(GameState *game_state) : game_state_(game_state) { }

  // Plays the bot move. Returns false if no more moves are possible.
  virtual bool PlayMove() = 0;

 protected:
  const GameState &game_state() const {
    return *game_state_;
  }

  void UpdateGameState(const GameState &game_state) {
    *game_state_ = game_state;
  }

 private:
  GameState *game_state_;
};

class RandomBot : public BotBase {
 public:
  RandomBot(GameState *game_state) : BotBase(game_state) { }

  virtual bool PlayMove() {
    // std::vector<GameState> next_states;
    // AppendNextGameStates(game_state(), &next_states);

    // if (next_states.empty()) return false;

    // int index = random() % next_states.size();
    // UpdateGameState(next_states[index]);
    std::vector<Move> moves;
    MoveGenerator generator(game_state().board);
    generator.SetPlayer(game_state().is_white_player);
    generator.AddNextMoves(&moves);

    if (moves.empty()) return false;
    int index = random() % moves.size();

    __android_log_print(ANDROID_LOG_VERBOSE, "native_bot.cc",
        "Selected move: %s", moves[index].DebugString().c_str());

    GameState next_game_state;
    ApplyMove(game_state(), moves[index], &next_game_state);
    UpdateGameState(next_game_state);

    return true;
  }
};

class MinMaxBot : public BotBase {
 public:
  MinMaxBot(GameState *game_core) : BotBase(game_core) { }

  virtual bool PlayMove() {
    Result result = FindBestMove(game_state(), 0);
    if (result.final_score == 50 || result.final_score == -50) return false;

    UpdateGameState(result.game_state);

    return true;
  }

 private:
  struct Result {
    GameState game_state;
    int final_score;
  };

  // Returns the absolute score of the passed in board.
  static int GameScore(const BitBoard &board) {
    return board.NumWhitePieces() - board.NumBlackPieces();
  }

  Result FindBestMove(const GameState &game_state, int depth) {
    static const int kMaxDepth = 3;

    std::vector<GameState> next_states;
    AppendNextGameStates(game_state, &next_states);

    Result best_result;
    best_result.final_score = game_state.is_white_player ? -50 : 50;

    char buf[200];
    snprintf(buf, sizeof(buf),
        "Evaluating state at depth: %d state:(%x, %x)), states to eval: %d",
        depth,
        game_state.board.white_piece_set(),
        game_state.board.black_piece_set(),
        next_states.size());
  __android_log_print(ANDROID_LOG_VERBOSE, "native_bot.cc",
      buf);

    for (int i = 0; i < next_states.size(); ++i) {
      GameState &state = next_states[i];
      if (depth == kMaxDepth) {
        int score = GameScore(state.board);
        if (game_state.is_white_player) {
          if (score > best_result.final_score) {
            best_result.game_state = state;
            best_result.final_score = score;
          }
        } else {
          if (score < best_result.final_score) {
            best_result.game_state = state;
            best_result.final_score = score;
          }
        }
      } else {
        Result next_result = FindBestMove(state, depth + 1);
        if (game_state.is_white_player) {
          if (next_result.final_score > best_result.final_score) {
            best_result.final_score = next_result.final_score;
            best_result.game_state = state;
          }
        } else {
          if (next_result.final_score < best_result.final_score) {
            best_result.final_score = next_result.final_score;
            best_result.game_state = state;
          }
        }
      }
    }

    snprintf(buf, sizeof(buf),
        "Returning result at depth: %d state:(%x, %x))",
        depth,
        best_result.game_state.board.white_piece_set(),
        best_result.game_state.board.black_piece_set());
    __android_log_print(ANDROID_LOG_VERBOSE, "native_bot.cc",
        buf);
    return best_result;
  }
};

extern "C" {
  JNIEXPORT jboolean JNICALL Java_com_android_checkers_NativeBot_playNativeBotMove(
    JNIEnv* environment,
    jobject native_random_bot,
    jint white_pieces,
    jint black_pieces,
    jboolean is_white_player,
    jboolean is_jump);
}

jboolean Java_com_android_checkers_NativeBot_playNativeBotMove(
    JNIEnv* environment,
    jobject native_random_bot,
    jint white_pieces,
    jint black_pieces,
    jboolean is_white_player,
    jboolean is_jump) {
  GameState game_state;
  game_state.board.set_white_pieces(white_pieces);
  game_state.board.set_black_pieces(black_pieces);
  game_state.is_white_player = is_white_player;
  game_state.is_jump = is_jump;

  __android_log_print(ANDROID_LOG_VERBOSE, "native_bot.cc",
      "GameState before: %s", game_state.DebugString().c_str());

  BotBase *bot = new RandomBot(&game_state);
  if (!bot->PlayMove()) {
    __android_log_print(ANDROID_LOG_VERBOSE, "native_bot.cc",
        "No possible bot moves.");
    return false;
  }

  __android_log_print(ANDROID_LOG_VERBOSE, "native_bot.cc",
      "GameState after: %s", game_state.DebugString().c_str());

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

  jfieldID move_again_mode_field_id = environment->GetFieldID(
      move_result_class_id, "isMoveAgainMode", "Z");
  environment->SetBooleanField(move_result, move_again_mode_field_id,
      game_state.is_jump);

  return true;
}
