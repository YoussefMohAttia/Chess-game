package chesscore;

import static chesscore.Color.BLACK;
import static chesscore.Color.WHITE;
import static chesscore.PieceType.PAWN;
import static chesscore.PieceType.QUEEN;
import java.util.ArrayList;

public class Pawn extends Piece {   
    private Square lastMove;
    public Pawn(Color color) {
        super(PieceType.PAWN, color);
    }
    
    @Override
    public boolean move(Board board ,Square startSquare, Square endSquare) {
        boolean b = super.move(board,startSquare,endSquare);
        Moved = true;
        lastMove = endSquare;                       
        return b;
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
        
        if (this.color == BLACK ) {
            if (!Moved) {
                if (board[y+2][x].isEmpty() && board[y+1][x].isEmpty()) {
                    legalMoves.add(board[y+2][x].getPos());
                }
            }
            
            if (y+1 < 8) {
                if (board[y+1][x].isEmpty()) {
                    legalMoves.add(board[y+1][x].getPos());
                }
            }
            
            if (x+1 < 8 && y+1 < 8) {
                if (!board[y+1][x+1].isEmpty()) {
                    legalMoves.add(board[y+1][x+1].getPos());
                }
            }
                
            if (x-1 >= 0 && y+1 < 8) {
                if (!board[y+1][x-1].isEmpty()) {
                    legalMoves.add(board[y+1][x-1].getPos());
                }
            }
        }
        
        if (this.color == WHITE) {
            if (!Moved &&(y-2)>0) {
                if (board[y-2][x].isEmpty() && board[y-1][x].isEmpty()) {
                    legalMoves.add(board[y-2][x].getPos());
                }
            }
            
            if (y-1 >= 0) {
                if (board[y-1][x].isEmpty()) {
                    legalMoves.add(board[y-1][x].getPos());
                }
            }
            
            if (x+1 < 8 && y-1 >= 0) {
                if (!board[y-1][x+1].isEmpty()) {
                    legalMoves.add(board[y-1][x+1].getPos());
                }
            }
                
            if (x-1 >= 0 && y-1 >= 0) {
                if (!board[y-1][x-1].isEmpty()) {
                    legalMoves.add(board[y-1][x-1].getPos());
                }
            }
        }
        // enpassant
        if (Math.abs(y2 - y) == 1 && x2 != x && board[y2][x2].isEmpty() && !board[y][x2].isEmpty()) {  
            if (board[y][x2].getPiece().getType() == PAWN) {              
                Pawn adjacentPawn = (Pawn) board[y][x2].getPiece();
                if (adjacentPawn.lastMove == board[y][x2] && adjacentPawn.getColor() != this.color) {
                    if(end.equals(board[y2][x2].getPos()))
                    {
                      board[y][x2].setPiece(null);
                      System.out.println("Enpassant");
                      System.out.println("Captured PAWN");                      
                      return true;                     
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
    
    public boolean promote(Board b, Square start,Square end,String promotedPiece) {
    if (start == null || start.getPiece() == null || start.getPiece() != this) {        
        return false;
    }
        if ((this.getColor() == Color.WHITE && end.getPos().charAt(1) != '8') ||
        (this.getColor() == Color.BLACK && end.getPos().charAt(1) != '1')) 
        {
        return false;
        }

    Color color = this.getColor();
    Piece newPiece;
    switch (promotedPiece) {
        case "Q":
            newPiece = new Queen(color);
            break;
        case "R":
            newPiece = new Rook(color);
            break;
        case "B":
            newPiece = new Bishop(color);
            break;
        case "K":
            newPiece = new Knight(color);
            break;
        default:
            newPiece = new Queen(color); 
            break;
    }
    move(b,start,end);
    end.setPiece(newPiece);    
    return true; 
}
}
