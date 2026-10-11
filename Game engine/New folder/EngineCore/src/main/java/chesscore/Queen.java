package chesscore;

import java.util.ArrayList;

public class Queen extends Piece{
    
    public Queen(Color color) {
        super(PieceType.QUEEN, color);
    }
     public int[] getLinearmoves(Square[][] board,String pos) {
        int x = pos.charAt(0) - 'a';
        int y = '8' - pos.charAt(1);
        int lastYabove = 0;
        int lastXright = 7;
        int lastYbelow = 7;
        int lastXleft = 0;
        
        for (int i = 0; i < y; i++) {
            if (!board[i][(x)].isEmpty()) {
                if (board[i][x].getPiece().getColor() != this.color) {
                    lastYabove = i;
                } else lastYabove = i + 1;
            }
        }

        for (int i = 7; i > y; i--) {
            if (!board[i][x].isEmpty()) {
                if (board[i][x].getPiece().getColor() != this.color) {
                    lastYbelow = i;
                } else lastYbelow = i - 1;
            }
        }

        for (int i = 0; i < x; i++) {
            if (!board[y][i].isEmpty()) {
                if (board[y][i].getPiece().getColor() != this.color) {
                    lastXleft = i;
                } else lastXleft = i + 1;
            }
        }

        for (int i = 7; i > x; i--) {
            if (!board[y][i].isEmpty()) {
                if (board[y][i].getPiece().getColor() != this.color) {
                    lastXright = i;
                } else lastXright = i - 1;
            }
        }
        
        int[] occups = {lastYabove, lastYbelow, lastXleft, lastXright};
        
        return occups;
    }
    
    public ArrayList<String> getDiagonalmoves(Square[][] board,String pos) {
        ArrayList<String> diagmoves = new ArrayList<String>();
        
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
                if (board[yTL][xTL].getPiece().getColor() == this.color) {
                    break;
                } else {
                    diagmoves.add(board[yTL][xTL].getPos());
                    break;
                }
            } else {
                diagmoves.add(board[yTL][xTL].getPos());
                yTL--;
                xTL--;
            }
        }
        
        while (xBL >= 0 && yBL < 8) {
            if (!board[yBL][xBL].isEmpty()) {
                if (board[yBL][xBL].getPiece().getColor() == this.color) {
                    break;
                } else {
                    diagmoves.add(board[yBL][xBL].getPos());
                    break;
                }
            } else {
                diagmoves.add(board[yBL][xBL].getPos());
                yBL++;
                xBL--;
            }
        }
        
        while (xBR < 8 && yBR < 8) {
            if (!board[yBR][xBR].isEmpty()) {
                if (board[yBR][xBR].getPiece().getColor() == this.color) {
                    break;
                } else {
                    diagmoves.add(board[yBR][xBR].getPos());
                    break;
                }
            } else {
                diagmoves.add(board[yBR][xBR].getPos());
                yBR++;
                xBR++;
            }
        }
        
        while (xTR < 8 && yTR >= 0) {
            if (!board[yTR][xTR].isEmpty()) {
                if (board[yTR][xTR].getPiece().getColor() == this.color) {
                    break;
                } else {
                    diagmoves.add(board[yTR][xTR].getPos());
                    break;
                }
            } else {
                diagmoves.add(board[yTR][xTR].getPos());
                yTR--;
                xTR++;
            }
        }
        
        return diagmoves;
    }   
    @Override
    public boolean isValidMove(Board b, String start, String end) {
        ArrayList<String> legalMoves = new ArrayList<String>();
        Square[][] board = b.getSquareArray();
        
        int x = start.charAt(0) - 'a';
        int y = '8' - start.charAt(1);
        
        int[] occups = getLinearmoves(board,start);
        
        for (int i = occups[0]; i <= occups[1]; i++) {
            if (i != y) legalMoves.add(board[i][x].getPos());
        }
        
        for (int i = occups[2]; i <= occups[3]; i++) {
            if (i != x) legalMoves.add(board[y][i].getPos());
        }
        
        for(int i=0;i<legalMoves.size();i++){
        if(legalMoves.get(i).equals(end))
            return true;
        }
        
        ArrayList<String> legalMoves2 = getDiagonalmoves(board,start);
        
        for(int i=0;i<legalMoves2.size();i++){
        if(legalMoves2.get(i).equals(end))
            return true;
        }
        
        return false; 
        
    }
}
