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

#include <sstream>
#include <string>

struct Square {
  int x, y;

  unsigned int GetMask() const {
    return (1 << GetBitIndexForSquare(x, y));
  }

  std::string DebugString() const {
    std::stringstream ss;
    ss << "(" << x << "," << y << ")";
    return ss.str();
  }

 private:
  // Obtains the bit index for a (x, y) square on the board.
  int GetBitIndexForSquare(int x, int y) const {
   return y * 4 + x / 2;
  }

  // Reverse of the function above.
  void GetXYForBitIndex(int index, int *x, int *y) const {
   *y = index / 4;
   *x = (index % 4) * 2 + (*y) % 2;
  }
};

inline void IncrementSquare(Square *square) {
  square->x += 2;
  if (square->x >= 8) {
    ++square->y;
    square->x = square->y % 2;
  }
}
