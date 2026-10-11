package chesscore;

public class Board {
    
    private Square[][] board;

    public Board() {
        board = new Square[8][8];
        initializeBoard();
        setup();
    }
    
    public Square[][] getSquareArray() {
        return this.board;
    }  
  
    private void initializeBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                char col = (char) ('a' + c);
                int row = 8 - r;
                String position = String.valueOf(col) + row;
                board[r][c] = new Square(position);
            }
        }
    }
    
    
     public Square getSquare(String position) {
        int col = position.charAt(0) - 'a';
        int row = '8' - position.charAt(1);

        if (col < 0 || col >= 8 || row < 0 || row >= 8) {
            System.out.println("Invalid position!");
            return null;
        }

        return board[row][col];
    }

    private void setup() {
        Piece p;
      for (int i = 0; i < 8; i++)
            board[1][i].setPiece(new Pawn(Color.BLACK));
        board[0][0].setPiece(new Rook (Color.BLACK));
        board[0][1].setPiece(new Knight(Color.BLACK)); 
        board[0][2].setPiece(new Bishop(Color.BLACK));
        board[0][3].setPiece(new Queen(Color.BLACK));        
        board[0][4].setPiece(new King(Color.BLACK));         
        board[0][5].setPiece(new Bishop(Color.BLACK));         
        board[0][6].setPiece(new Knight(Color.BLACK));       
        board[0][7].setPiece(new Rook (Color.BLACK));      

        for (int i = 0; i < 8; i++)
            board[6][i].setPiece(new Pawn(Color.WHITE));        
        board[7][0].setPiece(new Rook (Color.WHITE));
        board[7][1].setPiece(new Knight(Color.WHITE)); 
        board[7][2].setPiece(new Bishop(Color.WHITE));
        board[7][3].setPiece(new Queen(Color.WHITE));        
        board[7][4].setPiece(new King(Color.WHITE));         
        board[7][5].setPiece(new Bishop(Color.WHITE));         
        board[7][6].setPiece(new Knight(Color.WHITE));
        board[7][7].setPiece(new Rook (Color.WHITE));                     
        
        
    }
    
    public void printBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square tile = board[row][col];
                if(tile.getPiece()==null)
                //System.out.print(tile.getPos() +"  ");
                System.out.print("Emptyyyyyy" +"   ");
                else
                //System.out.print(tile.getPos() +"  ");    
                System.out.print(tile.getPiece().getType() +","+ tile.getPiece().getColor() +"  ");                    
            }
            System.out.println();
    }
     
    }
} 

