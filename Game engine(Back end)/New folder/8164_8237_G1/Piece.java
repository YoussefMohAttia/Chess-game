package chesscore;

public abstract class Piece {
    public PieceType type;
    public Color color;
    protected boolean Moved = false;

    public Piece(PieceType type, Color color) {
        this.type = type;
        this.color = color;
    }

    public PieceType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public void set(PieceType p,Color c) {
    type = p;
    color = c;
    }
    
    
    public boolean move(Board board ,Square startSquare, Square endSquare) {
       
    Piece capturedPiece = endSquare.getPiece();
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
    
    public abstract boolean isValidMove(Board board, String start, String end);
        
    public boolean promote(Board b, Square start,Square end, PieceType promotedPiece) {
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
        case QUEEN:
            newPiece = new Queen(color);
            break;
        case ROOK:
            newPiece = new Rook(color);
            break;
        case BISHOP:
            newPiece = new Bishop(color);
            break;
        case KNIGHT:
            newPiece = new Knight(color);
            break;
        default:
            newPiece = new Queen(color); 
            break;
    }
    start.setPiece(newPiece);
    move(b,start,end);
                      System.out.println("!!Promotion done!!");    
    return true; 
}

}
