package chesscore;

import java.util.ArrayList;

public class Knight extends Piece{
    
    public Knight(Color color) {
        super(PieceType.KNIGHT, color);
    }

    @Override
    public boolean isValidMove(Board b, String start, String end) {
        ArrayList<String> legalMoves = new ArrayList<String>();
        Square[][] board = b.getSquareArray();
        int x = start.charAt(0) - 'a';
        int y = '8' - start.charAt(1);
        
        int x2 = end.charAt(0) - 'a';
        int y2 = '8' - end.charAt(1);    
        
        
        if (!board[y2][x2].isEmpty()) {
            if (board[y2][x2].getPiece().getColor() == this.color)
            return false;
        }
        
        for (int i = 2; i > -3; i--) {
            for (int k = 2; k > -3; k--) {
                if(Math.abs(i) == 2 ^ Math.abs(k) == 2) {
                    if (k != 0 && i != 0) {
                        try {
                            legalMoves.add(board[y + k][x + i].getPos());
                        } catch (ArrayIndexOutOfBoundsException e) {                         
                            continue;
                        }
                    }
                }
            }
        }
        
        
        for(int i=0;i<legalMoves.size();i++){
        if(legalMoves.get(i).equals(end))
            return true;
        }
        return false;   
    }
    }
