package chesscore;

import java.util.ArrayList;

public class King extends Piece {
    public King(Color color) {
        super(PieceType.KING, color);
    }
    
   @Override
    public boolean move(Board board ,Square startSquare, Square endSquare) {       
    Piece capturedPiece = endSquare.getPiece();
       Square[][] b = board.getSquareArray();        
        int x = startSquare.getPos().charAt(0) - 'a';
        int y = '8' - startSquare.getPos().charAt(1);
        int x2 = endSquare.getPos().charAt(0) - 'a';
        int y2 = '8' - endSquare.getPos().charAt(1);   
        
        if (y==0){
            if(y2==0&&x2==2){
            if(castling(board,startSquare.getPos(),b[y2][0].getPos()))
                return true;}    
            else if(y2==0&&x2==6){    
            if(castling(board,startSquare.getPos(),b[y2][7].getPos()))
                return true;
            }}
        else if (y==7){ 
            if(y2==7&&x2==2){
            if(castling(board,startSquare.getPos(),b[y2][0].getPos()))
                return true;
            }else if(y2==7&&x2==6)    
            if(castling(board,startSquare.getPos(),b[y2][7].getPos()))
                return true;
            }     
        
    if(!this.isValidMove(board, startSquare.getPos(), endSquare.getPos()))
    {
        System.out.println("Invalid move!!!");        
        return false;
    } 
        
        if (capturedPiece != null) {
            if (capturedPiece.getColor() == this.color) return false;
        }                   
          endSquare.setPiece(startSquare.getPiece()); 
          startSquare.setPiece(null); 
          if(capturedPiece != null)
          System.out.println("Captured "+capturedPiece.getType());
           Moved = true;
          return true; 
    }    
    
    
    @Override
    public boolean isValidMove(Board b, String start, String end) {
         ArrayList<String> legalMoves = new ArrayList<String>();
        Check cm = new Check(b);
        Square[][] board = b.getSquareArray();
        int x = start.charAt(0) - 'a';
        int y = '8' - start.charAt(1);
        int x2 = end.charAt(0) - 'a';
        int y2 = '8' - end.charAt(1);   
        if (!board[y2][x2].isEmpty()) {
            if (board[y2][x2].getPiece().getColor() == this.color)
            return false;
        }                
          for (int i = 1; i > -2; i--) {
            for (int k = 1; k > -2; k--) {
                if(!(i == 0 && k == 0)) {
                    try {
                        if(cm.isValidMove(b,board[y + k][x + i].getPos(),this.color))
                            continue;
                        if(board[y + k][x + i].isEmpty() || board[y + k][x + i].getPiece().getColor()!= this.getColor()) 
                        {
                            legalMoves.add(board[y + k][x + i].getPos());
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
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
    
    public boolean castling(Board board,String kingPos ,String rookPos) {
        Square kingSquare = board.getSquare(kingPos);
        Square rookSquare = board.getSquare(rookPos);
        Rook rook = (Rook) rookSquare.getPiece();

         if (this.Moved || rook.Moved) {
        return false;
    }

        int start = Math.min(kingSquare.getPos().charAt(0), rookSquare.getPos().charAt(0)) - 'a' + 1;
        int end = Math.max(kingSquare.getPos().charAt(0), rookSquare.getPos().charAt(0)) - 'a';
        for (int i = start; i < end; i++) {
            if (!board.getSquare((char) ('a' + i) + kingSquare.getPos().substring(1)).isEmpty()) {
                return false;
            }
        }


        if (rookSquare.getPos().charAt(0) > kingSquare.getPos().charAt(0)) {
        kingSquare.setPiece(null);            
        rook.move(board, rookSquare, board.getSquare((char) (rookSquare.getPos().charAt(0) - 2) + rookSquare.getPos().substring(1)));
        board.getSquare((char) (kingSquare.getPos().charAt(0) + 2) + kingSquare.getPos().substring(1)).setPiece(new King (this.color));      
        } 
        else {
        kingSquare.setPiece(null);
        rook.move(board, rookSquare, board.getSquare((char) (rookSquare.getPos().charAt(0) + 3) + rookSquare.getPos().substring(1)));;
        board.getSquare((char) (kingSquare.getPos().charAt(0) - 2) + kingSquare.getPos().substring(1)).setPiece(new King (this.color));        
        }
        System.out.println("Castle");
        return true;
    }
    
}
