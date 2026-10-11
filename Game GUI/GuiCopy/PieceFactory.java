/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChessCore.Pieces;
import ChessCore.Player;

/**
 *
 * @author karim
 */
public class PieceFactory {
     public Piece createPiece(String type,Player owner) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        switch (type) {
            case "KING":
                return new King(owner);   
            case "QUEEN":
                return new Queen(owner);
            case "ROOK":
                return new Rook(owner);
            case "PAWN":
                return new Pawn(owner);
            case "KNIGHT":
                return new Knight(owner);
            case "BISHOP":
                return new Bishop(owner);                
            default:
                return null;
        }
    }
    
}