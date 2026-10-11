package ChessCore;

import ChessCore.Pieces.*;

import ChessCore.Pieces.King;
import Frontend.Board;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class ChessGame implements Observer {

    private ChessBoard board;
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;
    private Player whoseTurn = Player.WHITE;
    private Move lastMove;
    private boolean canWhiteCastleKingSide = true;
    private boolean canWhiteCastleQueenSide = true;
    private boolean canBlackCastleKingSide = true;
    private boolean canBlackCastleQueenSide = true;

    private boolean ucanWhiteCastleKingSide = false;
    private boolean ucanWhiteCastleQueenSide = false;
    private boolean ucanBlackCastleKingSide = false;
    private boolean ucanBlackCastleQueenSide = false;

    private final List<Observer> observers = new ArrayList<>();
    private boolean undoflag = false;
    private Board guiBoard;
    StateCaretaker caretaker = new StateCaretaker();

    protected ChessGame(BoardInitializer boardInitializer) {
        this.board = new ChessBoard(boardInitializer.initialize());
        this.guiBoard = guiBoard;
//        addObserver((Observer) this.guiBoard);
        caretaker.saveMemento(new ChessBoard(this.board));
    }

     @Override
    public void update() {
        this.board = board.getState();
    }

    public boolean isCanWhiteCastleKingSide() {
        return canWhiteCastleKingSide;
    }

    public boolean isCanWhiteCastleQueenSide() {
        return canWhiteCastleQueenSide;
    }

    public boolean isCanBlackCastleKingSide() {
        return canBlackCastleKingSide;
    }

    public boolean isCanBlackCastleQueenSide() {
        return canBlackCastleQueenSide;
    }

    protected boolean isValidMove(Move move) {
        if (isGameEnded()) {
            return false;
        }

        Piece pieceAtFrom = board.getPieceAtSquare(move.getFromSquare());
        if (pieceAtFrom == null || pieceAtFrom.getOwner() != whoseTurn || !pieceAtFrom.isValidMove(move, this)) {
            return false;
        }

        Piece pieceAtTo = board.getPieceAtSquare(move.getToSquare());
        // A player can't capture his own piece.
        if (pieceAtTo != null && pieceAtTo.getOwner() == whoseTurn) {
            return false;
        }

        return isValidMoveCore(move);
    }

    public Move getLastMove() {
        return lastMove;
    }

    public Player getWhoseTurn() {
        return whoseTurn;
    }

    ChessBoard getBoard() {
        return board;
    }

    public void flipBoardPieces() {
        Square tile = null;
        Square tilef = null;
        BoardFile[] files = BoardFile.values();
        BoardRank[] ranks = BoardRank.values();

        for (BoardFile file : files) {
            for (BoardRank rank : ranks) {
                String pos = file.name() + rank.ordinal();
                BoardFile f = BoardFile.valueOf(Character.toString(pos.charAt(0)));
                BoardRank r = BoardRank.values()[Integer.parseInt(pos.substring(1))];
                BoardFile flippedFile = BoardFile.values()[7 - f.ordinal()];
                BoardRank flippedRank = BoardRank.values()[7 - r.ordinal()];
                tile = new Square(f, r);
                tilef = new Square(flippedFile, flippedRank);

                Piece piece = board.getPieceAtSquare(tile);
                Piece piecef = board.getPieceAtSquare(tilef);
                board.setPieceAtSquare(tile, piecef);
                board.setPieceAtSquare(tilef, piece);

            }
        }
        int boardSize = 8;
        for (int rank = 0; rank < boardSize / 2; rank++) {
            for (int file = 0; file < boardSize; file++) {
                Square rtile = new Square(BoardFile.values()[file], BoardRank.values()[rank]);
                Square oppositeTile = new Square(BoardFile.values()[file], BoardRank.values()[boardSize - 1 - rank]);

                Piece piece = board.getPieceAtSquare(rtile);
                Piece oppositePiece = board.getPieceAtSquare(oppositeTile);

                board.setPieceAtSquare(rtile, oppositePiece);
                board.setPieceAtSquare(oppositeTile, piece);
            }
        }

    }

    public boolean isPromotionMove(Move move) {
        Piece piece = board.getPieceAtSquare(move.getFromSquare());
        BoardRank toRank = move.getToSquare().getRank();
        BoardRank fromRank = move.getFromSquare().getRank();
        if (piece instanceof Pawn) {
            if ((piece.getOwner() == Player.WHITE && toRank == BoardRank.EIGHTH && fromRank == BoardRank.SEVENTH)
                    || (piece.getOwner() == Player.BLACK && toRank == BoardRank.EIGHTH && fromRank == BoardRank.SEVENTH)) {
                return true;
            }
        }
        return false;
    }

    protected abstract boolean isValidMoveCore(Move move);

    public boolean isTherePieceInBetween(Move move) {
        return board.isTherePieceInBetween(move);
    }

    public boolean hasPieceIn(Square square) {
        return board.getPieceAtSquare(square) != null;
    }

    public boolean hasPieceInSquareForPlayer(Square square, Player player) {
        Piece piece = board.getPieceAtSquare(square);
        return piece != null && piece.getOwner() == player;
    }

    public boolean makeMove(Move move) {
        if (!isValidMove(move)) {
            return false;
        }

        Square fromSquare = move.getFromSquare();
        Piece fromPiece = board.getPieceAtSquare(fromSquare);

        // If the king has moved, castle is not allowed.
        if (fromPiece instanceof King) {
            if (fromPiece.getOwner() == Player.WHITE) {
                canWhiteCastleKingSide = false;
                canWhiteCastleQueenSide = false;
                ucanWhiteCastleKingSide = true;
                ucanWhiteCastleQueenSide = true;
            } else {
                canBlackCastleKingSide = false;
                canBlackCastleQueenSide = false;
                ucanBlackCastleKingSide = true;
                ucanBlackCastleQueenSide = true;
            }
        }

        // If the rook has moved, castle is not allowed on that specific side..
        if (fromPiece instanceof Rook) {
            if (fromPiece.getOwner() == Player.WHITE) {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.FIRST) {
                    canWhiteCastleKingSide = false;
                }
            } else {
                if (fromSquare.getFile() == BoardFile.A && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleQueenSide = false;
                } else if (fromSquare.getFile() == BoardFile.H && fromSquare.getRank() == BoardRank.EIGHTH) {
                    canBlackCastleKingSide = false;
                }
            }
        }

        // En-passant.
        if (fromPiece instanceof Pawn
                && move.getAbsDeltaX() == 1
                && !hasPieceIn(move.getToSquare())) {
            board.setPieceAtSquare(lastMove.getToSquare(), null);
        }

        // Promotion
        if (fromPiece instanceof Pawn) {
            BoardRank toSquareRank = move.getToSquare().getRank();
            if (toSquareRank == BoardRank.FIRST || toSquareRank == BoardRank.EIGHTH) {
                Player playerPromoting = fromPiece.getOwner() == Player.WHITE ? Player.WHITE : Player.BLACK;
                PawnPromotion promotion = move.getPawnPromotion();
                PieceFactory pieceFactory = new PieceFactory();
                switch (promotion) {
                    case Queen:
                        fromPiece = pieceFactory.createPiece("QUEEN", playerPromoting);
                        break;
                    case Rook:
                        fromPiece = pieceFactory.createPiece("ROOK", playerPromoting);
                        break;
                    case Knight:
                        fromPiece = pieceFactory.createPiece("KNIGHT", playerPromoting);
                        break;
                    case Bishop:
                        fromPiece = pieceFactory.createPiece("BISHOP", playerPromoting);
                        break;
                    case None:
                        throw new RuntimeException("Pawn moving to last rank without promotion being set. This should NEVER happen!");
                }
            }
        }

        // Castle
        if (fromPiece instanceof King
                && move.getAbsDeltaX() == 2) {

            Square toSquare = move.getToSquare();
            if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.FIRST) {
                // White king-side castle.
                // Rook moves from H1 to F1
                Square h1 = new Square(BoardFile.H, BoardRank.FIRST);
                Square f1 = new Square(BoardFile.F, BoardRank.FIRST);
                Piece rook = board.getPieceAtSquare(h1);
                board.setPieceAtSquare(h1, null);
                board.setPieceAtSquare(f1, rook);
                ucanWhiteCastleKingSide = true;
            } else if (toSquare.getFile() == BoardFile.G && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black king-side castle.
                // Rook moves from H8 to F8
                Square h8 = new Square(BoardFile.H, BoardRank.EIGHTH);
                Square f8 = new Square(BoardFile.F, BoardRank.EIGHTH);
                Piece rook = board.getPieceAtSquare(h8);
                board.setPieceAtSquare(h8, null);
                board.setPieceAtSquare(f8, rook);
                ucanBlackCastleKingSide = true;
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.FIRST) {
                // White queen-side castle.
                // Rook moves from A1 to D1
                Square a1 = new Square(BoardFile.A, BoardRank.FIRST);
                Square d1 = new Square(BoardFile.D, BoardRank.FIRST);
                Piece rook = board.getPieceAtSquare(a1);
                board.setPieceAtSquare(a1, null);
                board.setPieceAtSquare(d1, rook);
                ucanWhiteCastleQueenSide = true;
            } else if (toSquare.getFile() == BoardFile.C && toSquare.getRank() == BoardRank.EIGHTH) {
                // Black queen-side castle.
                // Rook moves from A8 to D8
                Square a8 = new Square(BoardFile.A, BoardRank.EIGHTH);
                Square d8 = new Square(BoardFile.D, BoardRank.EIGHTH);
                Piece rook = board.getPieceAtSquare(a8);
                board.setPieceAtSquare(a8, null);
                board.setPieceAtSquare(d8, rook);
                ucanBlackCastleQueenSide = true;
            }
        }

        board.setPieceAtSquare(fromSquare, null);
        board.setPieceAtSquare(move.getToSquare(), fromPiece);

        whoseTurn = Utilities.revertPlayer(whoseTurn);
        lastMove = move;
        updateGameStatus();

        caretaker.saveMemento(new ChessBoard(board));
        undoflag = true;

        return true;
    }

    public void undoMove() {
        if (ucanWhiteCastleKingSide) {
            ucanWhiteCastleKingSide = false;
            canWhiteCastleKingSide = true;
        }
        if (ucanWhiteCastleQueenSide) {
            ucanWhiteCastleQueenSide = false;
            canWhiteCastleQueenSide = true;
        }
        if (ucanBlackCastleKingSide) {
            ucanBlackCastleKingSide = false;
            canBlackCastleKingSide = true;
        }
        if (ucanBlackCastleQueenSide) {
            ucanBlackCastleQueenSide = false;
            canBlackCastleQueenSide = true;
        }

        if (undoflag) {
            caretaker.throwMemento();
            undoflag = false;
        }
        if (caretaker.Mementocount() > 1) {
            whoseTurn = Utilities.revertPlayer(whoseTurn);
            ChessBoard temp = caretaker.getMemento();
            ChessBoard copiedBoard = new ChessBoard(temp);
            this.board = copiedBoard;
        } else {
            ChessBoard temp = new ChessBoard(caretaker.checkMemento());
            this.board = temp;
            whoseTurn = Utilities.revertPlayer(Player.BLACK);
        }
    }

    private void updateGameStatus() {
        Player whoseTurn = getWhoseTurn();
        boolean isInCheck = Utilities.isInCheck(whoseTurn, getBoard());
        boolean hasAnyValidMoves = hasAnyValidMoves();
        if (isInCheck) {
            if (!hasAnyValidMoves && whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.BLACK_WON;
            } else if (!hasAnyValidMoves && whoseTurn == Player.BLACK) {
                gameStatus = GameStatus.WHITE_WON;
            } else if (whoseTurn == Player.WHITE) {
                gameStatus = GameStatus.WHITE_UNDER_CHECK;
            } else {
                gameStatus = GameStatus.BLACK_UNDER_CHECK;
            }
        } else if (!hasAnyValidMoves) {
            gameStatus = GameStatus.STALEMATE;
        } else {
            gameStatus = GameStatus.IN_PROGRESS;
        }

        // Note: Insufficient material can happen while a player is in check. Consider this scenario:
        // Board with two kings and a lone pawn. The pawn is promoted to a Knight with a check.
        // In this game, a player will be in check but the game also ends as insufficient material.
        // For this case, we just mark the game as insufficient material.
        // It might be better to use some sort of a "Flags" enum.
        // Or, alternatively, don't represent "check" in gameStatus
        // Instead, have a separate isWhiteInCheck/isBlackInCheck methods.
        if (isInsufficientMaterial()) {
            gameStatus = GameStatus.INSUFFICIENT_MATERIAL;
        }

    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean isGameEnded() {
        return gameStatus == GameStatus.WHITE_WON
                || gameStatus == GameStatus.BLACK_WON
                || gameStatus == GameStatus.STALEMATE
                || gameStatus == GameStatus.INSUFFICIENT_MATERIAL;
    }

    private boolean isInsufficientMaterial() {
        /*
        If both sides have any one of the following, and there are no pawns on the board:

        A lone king
        a king and bishop
        a king and knight
         */
        int whiteBishopCount = 0;
        int blackBishopCount = 0;
        int whiteKnightCount = 0;
        int blackKnightCount = 0;

        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                Piece p = getPieceAtSquare(new Square(file, rank));
                if (p == null || p instanceof King) {
                    continue;
                }

                if (p instanceof Bishop) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteBishopCount++;
                    } else {
                        blackBishopCount++;
                    }
                } else if (p instanceof Knight) {
                    if (p.getOwner() == Player.WHITE) {
                        whiteKnightCount++;
                    } else {
                        blackKnightCount++;
                    }
                } else {
                    // There is a non-null piece that is not a King, Knight, or Bishop.
                    // This can't be insufficient material.
                    return false;
                }
            }
        }

        boolean insufficientForWhite = whiteKnightCount + whiteBishopCount <= 1;
        boolean insufficientForBlack = blackKnightCount + blackBishopCount <= 1;
        return insufficientForWhite && insufficientForBlack;
    }

    private boolean hasAnyValidMoves() {
        for (BoardFile file : BoardFile.values()) {
            for (BoardRank rank : BoardRank.values()) {
                if (!getAllValidMovesFromSquare(new Square(file, rank)).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public Square findking(Player Player) {
        Square tile = null;
        BoardFile[] files = BoardFile.values();
        BoardRank[] ranks = BoardRank.values();
        for (BoardFile file : files) {
            for (BoardRank rank : ranks) {
                String pos = file.name() + rank.ordinal();
                BoardFile f = BoardFile.valueOf(Character.toString(pos.charAt(0)));
                BoardRank r = BoardRank.values()[Integer.parseInt(pos.substring(1))];
                tile = new Square(f, r);
                if (board.getPieceAtSquare(tile) == null) {
                    continue;
                } else if (board.getPieceAtSquare(tile) instanceof King) {
                    if (board.getPieceAtSquare(tile).getOwner() == Player) {
                        return tile;
                    } else {
                        continue;
                    }
                }
            }
        }
        return tile;
    }

    public List<Square> getAllValidMovesFromSquare(Square square) {
        ArrayList<Square> validMoves = new ArrayList<>();
        for (var i : BoardFile.values()) {
            for (var j : BoardRank.values()) {
                var sq = new Square(i, j);
                if (isValidMove(new Move(square, sq, PawnPromotion.Queen))) {
                    validMoves.add(sq);
                }
            }
        }

        return validMoves;
    }

    public Piece getPieceAtSquare(Square square) {
        return board.getPieceAtSquare(square);
    }
}
