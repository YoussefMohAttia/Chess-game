package chesscore;

import static chesscore.Color.BLACK;
import static chesscore.Color.WHITE;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ChessGame {

    boolean ended = false;
    public Board board;
    private String filename;
    private boolean turn = false;
    private Check cm;

    public ChessGame(String f, Board board) {
        this.board = board;
        filename = f;
        cm = new Check(board);
    }

    private boolean isValidMove(String start, String end) {
        int x = start.charAt(0) - 'a';
        int y = '8' - start.charAt(1);
        int x2 = end.charAt(0) - 'a';
        int y2 = '8' - end.charAt(1);

        if (x > 8 || x < 0 || y > 8 || y < 0) {
            return false;
        } else if (x2 > 8 || x2 < 0 || y2 > 8 || y2 < 0) {
            return false;
        } else {
            return true;
        }
    }

    public void readFromFile() {
        try ( Scanner scanner = new Scanner(Paths.get(filename))) {
            while (scanner.hasNextLine()) {
           //        board.printBoard();   
                if (ended) {
                    System.out.println("Game already ended");
                    break;
                }
                int count = 0;
                String row = scanner.nextLine();
                for (int i = 0; i < row.length(); i++) {
                    if (row.charAt(i) == ',') {
                        count++;
                    }
                }
                if (count == 1) {
                    Scanner sc = new Scanner(row);
                    sc.useDelimiter(",");
                    String start = sc.next();
                    String end = sc.next();
                    Square a = board.getSquare(start);
                    Square b = board.getSquare(end);
         //              System.out.println(a.getPos()+","+b.getPos());                   
                    if (a.getPiece() != null) {
                        if (isValidMove(start, end) && turn == false && a.getPiece().getColor() == WHITE) {
                            if (cm.wcheckmate()) {
                                board.getSquare(start).getPiece().move(this.board, a, b);
                                if (cm.wcheckmate()) {
                                    board.getSquare(end).getPiece().move(this.board, b, a);
                                    System.out.println("Invalid move");
                                } else {
                                    turn = true;
                                }
                            } else {
                                board.getSquare(start).getPiece().move(this.board, a, b);
                                turn = true;
                            }
                        } else if (isValidMove(start, end) && turn == true && a.getPiece().getColor() == BLACK) {
                            if (cm.bcheckmate()) {
                                board.getSquare(start).getPiece().move(this.board, a, b);
                                if (cm.bcheckmate()) {
                                    board.getSquare(end).getPiece().move(this.board, b, a);
                                    System.out.println("Invalid move");
                                } else {
                                    turn = false;
                                }
                            } else {
                                board.getSquare(start).getPiece().move(this.board, a, b);
                                turn = false;
                            }
                        } else {
                            System.out.println("Invalid move");
                        }
                    } else {
                        System.out.println("Invalid move");
                    }

                    if (cm.wcheckmate()) {
                        ArrayList<String> Pathh = getTilesBetween(cm.wking.getPos(), cm.checkpos);
                        if (gameover(WHITE, Pathh)) {
                            System.out.println("Black Won");
                            ended = true;
                            continue;
                        }
                        System.out.println("White in check");
                    }
                    if (cm.bcheckmate()) {
                        ArrayList<String> Pathh = getTilesBetween(cm.bking.getPos(), cm.checkpos);
                        if (gameover(BLACK, Pathh)) {
                            System.out.println("White Won");
                            ended = true;
                            continue;
                        }
                        System.out.println("Black in check");
                    }
                    if (cm.isInsufficientMaterial(board)) {
                        System.out.println("Insufficient Material");
                        ended = true;
                    }
                    if (cm.stalemate(BLACK) || cm.stalemate(WHITE)) {
                        System.out.println("Stalemate");
                        ended = true;
                    }
                }
                if (count == 2) {
                    Scanner sc = new Scanner(row);
                    sc.useDelimiter(",");
                    String start = sc.next();
                    String end = sc.next();
                    String promote = sc.next();
                    Square s = board.getSquare(start);                    
                    if (s.getPiece()!= null&&isValidMove(start, end)&&turn == false&& s.getPiece().getColor() == WHITE) {
                        Square a = board.getSquare(start);
                        Square b = board.getSquare(end);
                        Pawn pawn = (Pawn) a.getPiece();
                        pawn.promote(board, a, b, promote);
                        turn = true;
                    }
                     else if (s.getPiece()!= null&&isValidMove(start, end)&&turn == true&& s.getPiece().getColor() == BLACK) {
                        Square a = board.getSquare(start);
                        Square b = board.getSquare(end);
                        Pawn pawn = (Pawn) a.getPiece();
                        pawn.promote(board, a, b, promote);
                        turn = false;
                    }
                    else {
                        System.out.println("Invalid move");
                    }
                    
                   if (cm.wcheckmate()) {
                        ArrayList<String> Pathh = getTilesBetween(cm.wking.getPos(), cm.checkpos);
                        if (gameover(WHITE, Pathh)) {
                            System.out.println("Black Won");
                            ended = true;
                            continue;
                        }
                        System.out.println("White in check");
                    }
                    if (cm.bcheckmate()) {
                        ArrayList<String> Pathh = getTilesBetween(cm.bking.getPos(), cm.checkpos);
                        if (gameover(BLACK, Pathh)) {
                            System.out.println("White Won");
                            ended = true;
                            continue;
                        }
                        System.out.println("Black in check");
                    }
                    if (cm.isInsufficientMaterial(board)) {
                        System.out.println("Insufficient Material");
                        ended = true;
                    }
                    if (cm.stalemate(BLACK) || cm.stalemate(WHITE)) {
                        System.out.println("Stalemate");
                        ended = true;
                    }                    
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public boolean gameover(Color color, ArrayList<String> path) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char c = (char) ('a' + col);
                int r = 8 - row;
                String pos = String.valueOf(c) + r;
                Square tile = board.getSquare(path.get(0));
                if (tile.getPiece().isValidMove(board, tile.getPos(), pos)) {
                    return false;
                }
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char c = (char) ('a' + col);
                int r = 8 - row;
                String pos = String.valueOf(c) + r;
                Square tile = board.getSquare(pos);
                if (tile.getPiece() == null) {
                    continue;
                } else if (tile.getPiece().getColor() != color) {
                    continue;
                } else if (tile.getPiece().getColor() == color) {
                    for (int i = 1; i < path.size(); i++) {
                        if (tile.getPiece().isValidMove(board, tile.getPos(), path.get(i))) {
                            return false;
                        }
                    }

                }
            }
        }
        return true;
    }

    public boolean Draw(Board board, Color color) {
        return cm.stalemate(color) || cm.isInsufficientMaterial(board);
    }

    public ArrayList<String> getTilesBetween(String startTile, String endTile) {
        ArrayList<String> tilesBetween = new ArrayList<>();
        tilesBetween.add(startTile);
        // Extract the file and rank values from the algebraic notation
        char startFile = startTile.charAt(0);
        int startRank = Character.getNumericValue(startTile.charAt(1));
        char endFile = endTile.charAt(0);
        int endRank = Character.getNumericValue(endTile.charAt(1));

        // Check if the tiles are in the same file or rank
        if (startFile == endFile) {
            // Tiles are in the same file, iterate through ranks
            int minRank = Math.min(startRank, endRank);
            int maxRank = Math.max(startRank, endRank);
            for (int rank = minRank + 1; rank < maxRank; rank++) {
                tilesBetween.add(String.valueOf(startFile) + rank);
            }
        } else if (startRank == endRank) {
            // Tiles are in the same rank, iterate through files
            char minFile = (char) Math.min(startFile, endFile);
            char maxFile = (char) Math.max(startFile, endFile);
            for (char file = (char) (minFile + 1); file < maxFile; file++) {
                tilesBetween.add(file + String.valueOf(startRank));
            }
        } else {
            // Tiles are on a diagonal, iterate through the diagonal
            int fileDiff = Math.abs(endFile - startFile);
            int rankDiff = Math.abs(endRank - startRank);
            if (fileDiff == rankDiff) {
                int fileStep = (endFile > startFile) ? 1 : -1;
                int rankStep = (endRank > startRank) ? 1 : -1;
                char file = startFile;
                int rank = startRank;
                while (file != endFile && rank != endRank) {
                    file += fileStep;
                    rank += rankStep;
                    tilesBetween.add(file + String.valueOf(rank));
                }
            } else {
                // Invalid move (not along a file, rank, or diagonal)
                System.out.println("Invalid move: Tiles are not in the same file, rank, or diagonal.");
            }
        }
//        System.out.println("Tiles between " + startTile + " and " + endTile + ": " + tilesBetween);
        return tilesBetween;
    }
}
