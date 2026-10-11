package chesscore;

import java.util.ArrayList;

public class Rook extends Piece{
    
    public Rook(Color color) {
        super(PieceType.ROOK, color);
    }

 public int[] getLinearmoves(Square[][] board, String pos) {
    int x = pos.charAt(0) - 'a';
    int y = '8' - pos.charAt(1);
    int lastYabove = 0;
    int lastXright = 7;
    int lastYbelow = 7;
    int lastXleft = 0;

    for (int i = y - 1; i >= 0; i--) {
        if (!board[i][x].isEmpty()) {
            if (board[i][x].getPiece().getColor() != this.color) {
                lastYabove = i;
                break;
            }
        }
        lastYabove = i;
    }

    for (int i = y + 1; i < 8; i++) {
        if (!board[i][x].isEmpty()) {
            if (board[i][x].getPiece().getColor() != this.color) {
                lastYbelow = i;
                break;
            }
        }
        lastYbelow = i;
    }

    for (int i = x - 1; i >= 0; i--) {
        if (!board[y][i].isEmpty()) {
            if (board[y][i].getPiece().getColor() != this.color) {
                lastXleft = i;
                break;
            }
        }
        lastXleft = i;
    }

    for (int i = x + 1; i < 8; i++) {
        if (!board[y][i].isEmpty()) {
            if (board[y][i].getPiece().getColor() != this.color) {
                lastXright = i;
                break;
            }
        }
        lastXright = i;
    }

    int[] occups = { lastYabove, lastYbelow, lastXleft, lastXright };

    return occups;
}

    @Override
    public boolean isValidMove(Board b, String start, String end) {
        ArrayList<String> legalMoves = new ArrayList<String>();
        int x = start.charAt(0) - 'a';
        int y = '8' - start.charAt(1); 
        Square[][] board = b.getSquareArray();
        
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
        return false;
    }

}
