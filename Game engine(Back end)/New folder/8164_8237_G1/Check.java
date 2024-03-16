package chesscore;

import static chesscore.Color.BLACK;
import static chesscore.Color.WHITE;
import static chesscore.PieceType.BISHOP;
import static chesscore.PieceType.KING;
import static chesscore.PieceType.KNIGHT;
import static chesscore.PieceType.PAWN;
import static chesscore.PieceType.QUEEN;
import static chesscore.PieceType.ROOK;
import java.util.ArrayList;

public class Check {

    public Square wking;
    public Square bking;
    public String checkpos;
    public Board board;

    Check(Board board) {
        this.board = board;
        findking();
    }

    public boolean wcheckmate() {
        findking();
        if (isValidMove(board, wking.getPos(), wking.getPiece().getColor())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean bcheckmate() {
        findking();
        if (isValidMove(board, bking.getPos(), bking.getPiece().getColor())) {
            return true;
        } else {
            return false;
        }
    }

    public void findking() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char c = (char) ('a' + col);
                int r = 8 - row;
                String pos = String.valueOf(c) + r;
                Square tile = board.getSquare(pos);
                if (tile.getPiece() == null) {
                    continue;
                } else if (tile.getPiece().getType() == KING) {
                    if (tile.getPiece().getColor() == WHITE) {
                        wking = tile;
                    } else {
                        bking = tile;
                    }
                }
            }
        }
    }

    public boolean stalemate(Color color) {
        findking();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char c = (char) ('a' + col);
                int r = 8 - row;
                String pos = String.valueOf(c) + r;
                Square tile = board.getSquare(pos);
                if (tile.getPiece() == null) {
                    continue;
                } else if (tile.getPiece().getColor() == color) {
                    for (int rr = 0; rr < 8; rr++) {
                        for (int cc = 0; cc < 8; cc++) {
                            char x = (char) ('a' + cc);
                            int y = 8 - rr;
                            String p = String.valueOf(x) + y;
                            Square temp = board.getSquare(p);
                            if (tile.getPiece().isValidMove(board, pos, temp.getPos())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
public boolean isInsufficientMaterial(Board board) {
    int whiteKnights = 0, blackKnights = 0, bishopsOnWhite = 0, bishopsOnBlack = 0;
    boolean hasWhiteBishop = false, hasBlackBishop = false, hasWhitePawn = false, hasBlackPawn = false;
    boolean hasWhiteRookOrQueen = false, hasBlackRookOrQueen = false;

    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
                char c = (char) ('a' + col);
                int r = 8 - row;
                String pos = String.valueOf(c) + r;            
            Piece piece = board.getSquare(pos).getPiece();
            if (piece == null) continue;

            switch (piece.getType()) {
                case PAWN:
                    if (piece.getColor() == Color.WHITE) hasWhitePawn = true;
                    else hasBlackPawn = true;
                    break;
                case ROOK:
                case QUEEN:
                    if (piece.getColor() == Color.WHITE) hasWhiteRookOrQueen = true;
                    else hasBlackRookOrQueen = true;
                    break;
                case BISHOP:
                    if (piece.getColor() == Color.WHITE) {
                        hasWhiteBishop = true;
                        if ((row + col) % 2 == 0) bishopsOnWhite++;
                    } else {
                        hasBlackBishop = true;
                        if ((row + col) % 2 == 0) bishopsOnBlack++;
                    }
                    break;
                case KNIGHT:
                    if (piece.getColor() == Color.WHITE) whiteKnights++;
                    else blackKnights++;
                    break;
                default:
                    break;
            }
        }
    }  
    if (!hasWhitePawn && !hasBlackPawn && !hasWhiteRookOrQueen && !hasBlackRookOrQueen) {
        if (!hasWhiteBishop && !hasBlackBishop) {
            if (whiteKnights < 2 && blackKnights < 2) return true;
        } else if (!hasWhiteBishop && blackKnights == 0) {
            if (whiteKnights == 0 && bishopsOnBlack < 2) return true;
        } else if (!hasBlackBishop && whiteKnights == 0) {
            if (blackKnights == 0 && bishopsOnWhite < 2) return true;
        } else if (whiteKnights == 0 && blackKnights == 0) {
            if (bishopsOnWhite < 2 && bishopsOnBlack < 2) return true;
        }
    }
    return false;
}    

    public int[] getLinearsquares(Square[][] board, String pos, Color color) {
        int x = pos.charAt(0) - 'a';
        int y = '8' - pos.charAt(1);
        int lastYabove = 0;
        int lastXright = 7;
        int lastYbelow = 7;
        int lastXleft = 0;

        for (int i = 0; i < y; i++) {
            if (!board[i][x].isEmpty()) {
                if (board[i][x].getPiece().getColor() != color) {
                    lastYabove = i;
                } else {
                    lastYabove = i + 1;
                }
            }
        }

        for (int i = 7; i > y; i--) {
            if (!board[i][x].isEmpty()) {
                if (board[i][x].getPiece().getColor() != color) {
                    lastYbelow = i;
                } else {
                    lastYbelow = i - 1;
                }
            }
        }

        for (int i = 0; i < x; i++) {
            if (!board[y][i].isEmpty()) {
                if (board[y][i].getPiece().getColor() != color) {
                    lastXleft = i;
                } else {
                    lastXleft = i + 1;
                }
            }
        }

        for (int i = 7; i > x; i--) {
            if (!board[y][i].isEmpty()) {
                if (board[y][i].getPiece().getColor() != color) {
                    lastXright = i;
                } else {
                    lastXright = i - 1;
                }
            }
        }

        int[] occups = {lastYabove, lastYbelow, lastXleft, lastXright};

        return occups;
    }

    public ArrayList<Square> getDiagonalsquares(Square[][] board, String pos, Color color) {
        ArrayList<Square> diagsquares = new ArrayList<Square>();

        int x = pos.charAt(0) - 'a';
        int y = '8' - pos.charAt(1);
        int xTL = x - 1;
        int xBL = x - 1;
        int xTR = x + 1;
        int xBR = x + 1;
        int yTL = y - 1;
        int yBL = y + 1;
        int yTR = y - 1;
        int yBR = y + 1;

        while (xTL >= 0 && yTL >= 0) {
            if (!board[yTL][xTL].isEmpty()) {
                if (board[yTL][xTL].getPiece().getColor() == color) {
                    break;
                } else {
                    diagsquares.add(board[yTL][xTL]);
                    break;
                }
            } else {
                diagsquares.add(board[yTL][xTL]);
                yTL--;
                xTL--;
            }
        }

        while (xBL >= 0 && yBL < 8) {
            if (!board[yBL][xBL].isEmpty()) {
                if (board[yBL][xBL].getPiece().getColor() == color) {
                    break;
                } else {
                    diagsquares.add(board[yBL][xBL]);
                    break;
                }
            } else {
                diagsquares.add(board[yBL][xBL]);
                yBL++;
                xBL--;
            }
        }

        while (xBR < 8 && yBR < 8) {
            if (!board[yBR][xBR].isEmpty()) {
                if (board[yBR][xBR].getPiece().getColor() == color) {
                    break;
                } else {
                    diagsquares.add(board[yBR][xBR]);
                    break;
                }
            } else {
                diagsquares.add(board[yBR][xBR]);
                yBR++;
                xBR++;
            }
        }

        while (xTR < 8 && yTR >= 0) {
            if (!board[yTR][xTR].isEmpty()) {
                if (board[yTR][xTR].getPiece().getColor() == color) {
                    break;
                } else {
                    diagsquares.add(board[yTR][xTR]);
                    break;
                }
            } else {
                diagsquares.add(board[yTR][xTR]);
                yTR--;
                xTR++;
            }
        }

        return diagsquares;
    }

    public ArrayList<Square> pawnmove(Board b, String pos, Color color) {
        ArrayList<Square> legalMoves = new ArrayList<Square>();

        Square[][] board = b.getSquareArray();
        int x = pos.charAt(0) - 'a';
        int y = '8' - pos.charAt(1);

        if (color == BLACK) {

            if (x + 1 < 8 && y + 1 < 8) {
                if (!board[y + 1][x + 1].isEmpty()) {
                    legalMoves.add(board[y + 1][x + 1]);
                }
            }

            if (x - 1 >= 0 && y + 1 < 8) {
                if (!board[y + 1][x - 1].isEmpty()) {
                    legalMoves.add(board[y + 1][x - 1]);
                }
            }
        }

        if (color == WHITE) {

            if (x + 1 < 8 && y - 1 >= 0) {
                if (!board[y - 1][x + 1].isEmpty()) {
                    legalMoves.add(board[y - 1][x + 1]);
                }
            }

            if (x - 1 >= 0 && y - 1 >= 0) {
                if (!board[y - 1][x - 1].isEmpty()) {
                    legalMoves.add(board[y - 1][x - 1]);
                }
            }
        }
        return legalMoves;

    }

    public ArrayList<Square> knightmove(Board b, String pos, Color color) {
        ArrayList<Square> legalMoves = new ArrayList<Square>();
        Square[][] board = b.getSquareArray();
        int x = pos.charAt(0) - 'a';
        int y = '8' - pos.charAt(1);

        for (int i = 2; i > -3; i--) {
            for (int k = 2; k > -3; k--) {
                if (Math.abs(i) == 2 ^ Math.abs(k) == 2) {
                    if (k != 0 && i != 0) {
                        try {
                            legalMoves.add(board[y + k][x + i]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    public boolean isValidMove(Board b, String start, Color color) {
        ArrayList<Square> legalMoves = new ArrayList<Square>();
        Square[][] board = b.getSquareArray();
        int x = start.charAt(0) - 'a';
        int y = '8' - start.charAt(1);
        int[] occups = getLinearsquares(board, start, color);

        for (int i = occups[0]; i <= occups[1]; i++) {
            if (i != y) {
                legalMoves.add(board[i][x]);
            }
        }

        for (int i = occups[2]; i <= occups[3]; i++) {
            if (i != x) {
                legalMoves.add(board[y][i]);
            }
        }

        for (int i = 0; i < legalMoves.size(); i++) {
            if (legalMoves.get(i).getPiece() == null) {
                continue;
            } else if (legalMoves.get(i).getPiece().getType() == ROOK || legalMoves.get(i).getPiece().getType() == QUEEN
                    && legalMoves.get(i).getPiece().getColor() != color) {
                checkpos = legalMoves.get(i).getPos();
                return true;
            }
        }

        ArrayList<Square> legalMoves2 = getDiagonalsquares(board, start, color);
        for (int i = 0; i < legalMoves2.size(); i++) {
            if (legalMoves2.get(i).getPiece() == null) {
                continue;
            } else if (legalMoves2.get(i).getPiece().getType() == BISHOP || legalMoves2.get(i).getPiece().getType() == QUEEN
                    && legalMoves2.get(i).getPiece().getColor() != color) {
                checkpos = legalMoves2.get(i).getPos();
                return true;
            }
        }

        ArrayList<Square> legalMoves3 = knightmove(b, start, color);
        for (int i = 0; i < legalMoves3.size(); i++) {
            if (legalMoves3.get(i).getPiece() == null) {
                continue;
            } else if (legalMoves3.get(i).getPiece().getType() == KNIGHT && legalMoves3.get(i).getPiece().getColor() != color) {
                checkpos = legalMoves3.get(i).getPos();
                return true;
            }
        }

        ArrayList<Square> legalMoves4 = pawnmove(b, start, color);
        for (int i = 0; i < legalMoves4.size(); i++) {
            if (legalMoves4.get(i).getPiece() == null) {
                continue;
            } else if (legalMoves4.get(i).getPiece().getType() == PAWN && legalMoves4.get(i).getPiece().getColor() != color) {
                checkpos = legalMoves4.get(i).getPos();
                return true;
            }
        }
        return false;
    }
}
