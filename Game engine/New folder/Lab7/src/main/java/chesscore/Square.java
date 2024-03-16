package chesscore;

public class Square {

    private String Position;
    private Piece piece;

    Square(String pos) {
    Position = pos;
    piece = piece;
    }
    
    public boolean isEmpty() {
        if(piece==null)
	return true;
        else return false;
    }
    
    public Piece getPiece() {
    return piece;
    }
    
    public String getPos() {
    return Position;
    }
    
    public void setPiece(Piece p) {    
    piece = p;
    }
    
}

