package chesscore;

import java.util.ArrayList;

public class Bishop extends Piece{
    
    public Bishop(Color color) {
        super(PieceType.BISHOP, color);
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
        Square[][] board = b.getSquareArray();
        int x = start.charAt(0) - 'a';
        int y = '8' - start.charAt(1);
        ArrayList<String> legalMoves = getDiagonalmoves(board,start);    
        for(int i=0;i<legalMoves.size();i++){
        if(legalMoves.get(i).equals(end))
            return true;
        }
        return false;    
    
    }

}
