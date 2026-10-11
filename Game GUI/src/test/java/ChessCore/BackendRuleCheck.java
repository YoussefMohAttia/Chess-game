package ChessCore;

import ChessCore.Pieces.King;
import ChessCore.Pieces.Pawn;

public final class BackendRuleCheck {
    private BackendRuleCheck() {
    }

    public static void main(String[] args) {
        rejectsPromotionWithoutSelectedPiece();
        detectsBlackPromotion();
        rejectsCastlingWithoutRook();
    }

    private static void rejectsPromotionWithoutSelectedPiece() {
        ClassicChessGame game = emptyGame();
        game.getBoard().setPieceAtSquare(new Square(BoardFile.E, BoardRank.FIRST), new King(Player.WHITE));
        game.getBoard().setPieceAtSquare(new Square(BoardFile.E, BoardRank.EIGHTH), new King(Player.BLACK));
        game.getBoard().setPieceAtSquare(new Square(BoardFile.A, BoardRank.SEVENTH), new Pawn(Player.WHITE));

        boolean moved = game.makeMove(new Move(
                new Square(BoardFile.A, BoardRank.SEVENTH),
                new Square(BoardFile.A, BoardRank.EIGHTH)));

        check(!moved, "White pawn promotion without a selected piece must be rejected.");
    }

    private static void detectsBlackPromotion() {
        ClassicChessGame game = emptyGame();
        game.getBoard().setPieceAtSquare(new Square(BoardFile.E, BoardRank.FIRST), new King(Player.WHITE));
        game.getBoard().setPieceAtSquare(new Square(BoardFile.E, BoardRank.EIGHTH), new King(Player.BLACK));
        game.getBoard().setPieceAtSquare(new Square(BoardFile.A, BoardRank.SECOND), new Pawn(Player.BLACK));

        boolean promotion = game.isPromotionMove(new Move(
                new Square(BoardFile.A, BoardRank.SECOND),
                new Square(BoardFile.A, BoardRank.FIRST)));

        check(promotion, "Black pawn moving from second to first rank must be detected as promotion.");
    }

    private static void rejectsCastlingWithoutRook() {
        ClassicChessGame game = new ClassicChessGame();
        game.getBoard().setPieceAtSquare(new Square(BoardFile.F, BoardRank.FIRST), null);
        game.getBoard().setPieceAtSquare(new Square(BoardFile.G, BoardRank.FIRST), null);
        game.getBoard().setPieceAtSquare(new Square(BoardFile.H, BoardRank.FIRST), null);

        boolean moved = game.makeMove(new Move(
                new Square(BoardFile.E, BoardRank.FIRST),
                new Square(BoardFile.G, BoardRank.FIRST)));

        check(!moved, "Castling must be rejected when the rook is missing.");
    }

    private static ClassicChessGame emptyGame() {
        ClassicChessGame game = new ClassicChessGame();
        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                game.getBoard().setPieceAtSquare(new Square(file, rank), null);
            }
        }
        return game;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
