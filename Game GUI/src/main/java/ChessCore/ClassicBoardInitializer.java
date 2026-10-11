package ChessCore;

import ChessCore.Pieces.*;

public final class ClassicBoardInitializer implements BoardInitializer {
    private static final BoardInitializer instance = new ClassicBoardInitializer();
    private static final PieceFactory pieceFactory = new PieceFactory();

    private ClassicBoardInitializer() {
    }

    public static BoardInitializer getInstance() {
        return instance;
    }

    @Override
    public Piece[][] initialize() {
        Piece[][] initialState = {
            {pieceFactory.createPiece("ROOK", Player.WHITE), pieceFactory.createPiece("KNIGHT", Player.WHITE), pieceFactory.createPiece("BISHOP", Player.WHITE), pieceFactory.createPiece("QUEEN", Player.WHITE), pieceFactory.createPiece("KING", Player.WHITE), pieceFactory.createPiece("BISHOP", Player.WHITE), pieceFactory.createPiece("KNIGHT", Player.WHITE), pieceFactory.createPiece("ROOK", Player.WHITE)},
            {pieceFactory.createPiece("PAWN", Player.WHITE), pieceFactory.createPiece("PAWN", Player.WHITE), pieceFactory.createPiece("PAWN", Player.WHITE), pieceFactory.createPiece("PAWN", Player.WHITE), pieceFactory.createPiece("PAWN", Player.WHITE), pieceFactory.createPiece("PAWN", Player.WHITE), pieceFactory.createPiece("PAWN", Player.WHITE), pieceFactory.createPiece("PAWN", Player.WHITE)},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {pieceFactory.createPiece("PAWN", Player.BLACK), pieceFactory.createPiece("PAWN", Player.BLACK), pieceFactory.createPiece("PAWN", Player.BLACK), pieceFactory.createPiece("PAWN", Player.BLACK), pieceFactory.createPiece("PAWN", Player.BLACK), pieceFactory.createPiece("PAWN", Player.BLACK), pieceFactory.createPiece("PAWN", Player.BLACK), pieceFactory.createPiece("PAWN", Player.BLACK)},
            {pieceFactory.createPiece("ROOK", Player.BLACK), pieceFactory.createPiece("KNIGHT", Player.BLACK), pieceFactory.createPiece("BISHOP", Player.BLACK), pieceFactory.createPiece("QUEEN", Player.BLACK), pieceFactory.createPiece("KING", Player.BLACK), pieceFactory.createPiece("BISHOP", Player.BLACK), pieceFactory.createPiece("KNIGHT", Player.BLACK), pieceFactory.createPiece("ROOK", Player.BLACK)}
        };
        return initialState;
    }
}
