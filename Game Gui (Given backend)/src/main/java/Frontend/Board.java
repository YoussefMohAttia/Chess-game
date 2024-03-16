package Frontend;

import ChessCore.BoardFile;
import ChessCore.BoardRank;
import ChessCore.ClassicChessGame;
import ChessCore.GameStatus;
import ChessCore.Move;
import ChessCore.PawnPromotion;
import ChessCore.Pieces.Bishop;
import ChessCore.Pieces.King;
import ChessCore.Pieces.Knight;
import ChessCore.Pieces.Pawn;
import ChessCore.Pieces.Piece;
import ChessCore.Pieces.Queen;
import ChessCore.Pieces.Rook;
import ChessCore.Player;
import ChessCore.Square;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Board extends javax.swing.JFrame {

    private List<JButton> lastHighlightedButtons = new ArrayList<>();
    private List<Color> lastHighlightedColors = new ArrayList<>();
    private Square sourceSquare;
    private Square destinationSquare;
    private JButton lastHighlightedButton = null;
    private Color lastHighlightedColor = null;

    public Board() {
        initComponents();
        initBoard();
    }

    ClassicChessGame object = new ClassicChessGame();
    
    public void set(JButton JB, Square square) {
        Piece piece = object.getPieceAtSquare(square);
        if (piece == null) {
            JB.setIcon(null);
        }
        String color = "";
        if (piece != null) {
            color = (piece.getOwner() == Player.WHITE) ? "White" : "Black";
        }
        String pieceType = "";
        if (piece instanceof Rook) {
            pieceType = "Rook";
        } else if (piece instanceof Pawn) {
            pieceType = "Pawn";
        } else if (piece instanceof Bishop) {
            pieceType = "Bishop";
        } else if (piece instanceof Knight) {
            pieceType = "Knight";
        } else if (piece instanceof Queen) {
            pieceType = "Queen";
        } else if (piece instanceof King) {
            pieceType = "King";
        }

        if (!pieceType.isEmpty()) {
            JB.setIcon(new ImageIcon("C:\\Users\\ADMIN\\OneDrive\\Desktop\\Term 5\\programming 2\\Lab8\\src\\main\\java\\Frontend\\" + color + pieceType + ".png"));
        }
    }

    public void initBoard() {
        BoardFile[] files = BoardFile.values();
        BoardRank[] ranks = BoardRank.values();
        for (BoardFile file : files) {
            for (BoardRank rank : ranks) {
                String buttonName = file.name() + rank.ordinal();

                JButton button = getButtonByName(buttonName);

                set(button, new Square(file, rank));
            }
        }
    }

    public JButton getButtonByName(String name) {

        Component[] components = this.getContentPane().getComponents();

        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                button.addActionListener(this::squareButtonClicked);
                if (name.equals(button.getName())) {
                    return button;
                }
            }
        }
        return null;
    }

    private void squareButtonClicked(ActionEvent evt) {
        JButton clickedButton = (JButton) evt.getSource();

        String buttonName = clickedButton.getName();
        BoardFile file = BoardFile.valueOf(Character.toString(buttonName.charAt(0)));
        BoardRank rank = BoardRank.values()[Integer.parseInt(buttonName.substring(1))];

        Square clickedSquare = new Square(file, rank);

        if (sourceSquare == null) {
            sourceSquare = clickedSquare;
            List<Square> validMoves = object.getAllValidMovesFromSquare(sourceSquare);
            highlightValidMoveSquares(validMoves);

        } else {
            destinationSquare = clickedSquare;
            if (object.isPromotionMove(new Move(sourceSquare, destinationSquare))) {

                String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                String input = (String) JOptionPane.showInputDialog(null, "Choose promotion", "Pawn reached the end of the board", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                PawnPromotion promotion = PawnPromotion.valueOf(input);

                object.makeMove(new Move(sourceSquare, destinationSquare, promotion));
//                object.flipBoardPieces();
                initBoard();

            } else if (object.makeMove(new Move(sourceSquare, destinationSquare))) {
                if (object.getWhoseTurn() == Player.WHITE) {
//                  object.flipBoardPieces();
                    initBoard();
                }

                if (object.getWhoseTurn() == Player.BLACK) {
//                    object.flipBoardPieces();
                    initBoard();
                }
                if (lastHighlightedButton != null) {
                    lastHighlightedButton.setBackground(lastHighlightedColor);
                    lastHighlightedButton = null;
                    lastHighlightedColor = null;
                }

                if (object.getGameStatus() == GameStatus.WHITE_UNDER_CHECK) {
                    Square tile = object.findking(Player.WHITE);
                    highlightKingSquare(tile);
                }
                if (object.getGameStatus() == GameStatus.BLACK_UNDER_CHECK) {
                    Square tile = object.findking(Player.BLACK);
                    highlightKingSquare(tile);
                }
                if (object.getGameStatus() == GameStatus.WHITE_WON) {
                    JOptionPane.showMessageDialog(this, "White Won", "Status", JOptionPane.ERROR_MESSAGE);
                }
                if (object.getGameStatus() == GameStatus.BLACK_WON) {
                    JOptionPane.showMessageDialog(this, "Black Won", "Status", JOptionPane.ERROR_MESSAGE);
                }
            }
            for (int i = 0; i < lastHighlightedButtons.size(); i++) {
                lastHighlightedButtons.get(i).setBackground(lastHighlightedColors.get(i));
            }
            lastHighlightedButtons.clear();
            lastHighlightedColors.clear();
            sourceSquare = null;
            destinationSquare = null;
        }
    }

    public void flipBoard() {
        BoardFile[] files = BoardFile.values();
        BoardRank[] ranks = BoardRank.values();

        for (BoardFile file : files) {
            for (BoardRank rank : ranks) {
                String buttonName = file.name() + rank.ordinal();

                JButton button = getFlippedButtonByName(buttonName);

                set(button, new Square(file, rank));
            }
        }
    }

    public JButton getFlippedButtonByName(String name) {
        BoardFile[] files = BoardFile.values();
        BoardRank[] ranks = BoardRank.values();

        int fileIndex = Math.abs(files.length - 1 - (name.charAt(0) - 'A'));
        int rankIndex = Math.abs(ranks.length - 1 - (Integer.parseInt(name.substring(1))));

        String flippedButtonName = files[fileIndex].name() + rankIndex;

        return getButtonByName(flippedButtonName);
    }

    private void highlightValidMoveSquares(List<Square> validMoves) {
        for (int i = 0; i < lastHighlightedButtons.size(); i++) {
            lastHighlightedButtons.get(i).setBackground(lastHighlightedColors.get(i));
        }
        lastHighlightedButtons.clear();
        lastHighlightedColors.clear();

        for (Square move : validMoves) {
            try {
                char file = move.getFile().toString().charAt(0);
                int rank = move.getRank().ordinal();

                String buttonName = String.valueOf(file) + rank;
                JButton squareButton = (JButton) this.getClass().getDeclaredField(buttonName).get(this);

                lastHighlightedColors.add(squareButton.getBackground());

                squareButton.setBackground(Color.GREEN);

                lastHighlightedButtons.add(squareButton);
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void highlightKingSquare(Square square) {
        try {
            char file = square.getFile().toString().charAt(0);
            int rank = square.getRank().ordinal();
            String buttonName = String.valueOf(file) + rank;
            JButton squareButton = (JButton) this.getClass().getDeclaredField(buttonName).get(this);
            lastHighlightedColor = squareButton.getBackground();
            squareButton.setBackground(Color.RED);
            lastHighlightedButton = squareButton;
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        B7 = new javax.swing.JButton();
        A7 = new javax.swing.JButton();
        D7 = new javax.swing.JButton();
        C7 = new javax.swing.JButton();
        F7 = new javax.swing.JButton();
        E7 = new javax.swing.JButton();
        H7 = new javax.swing.JButton();
        G7 = new javax.swing.JButton();
        B6 = new javax.swing.JButton();
        A6 = new javax.swing.JButton();
        D6 = new javax.swing.JButton();
        C6 = new javax.swing.JButton();
        F6 = new javax.swing.JButton();
        E6 = new javax.swing.JButton();
        H6 = new javax.swing.JButton();
        G6 = new javax.swing.JButton();
        B5 = new javax.swing.JButton();
        A5 = new javax.swing.JButton();
        D5 = new javax.swing.JButton();
        C5 = new javax.swing.JButton();
        F5 = new javax.swing.JButton();
        E5 = new javax.swing.JButton();
        H5 = new javax.swing.JButton();
        G5 = new javax.swing.JButton();
        B4 = new javax.swing.JButton();
        A4 = new javax.swing.JButton();
        D4 = new javax.swing.JButton();
        C4 = new javax.swing.JButton();
        F4 = new javax.swing.JButton();
        E4 = new javax.swing.JButton();
        H4 = new javax.swing.JButton();
        G4 = new javax.swing.JButton();
        E1 = new javax.swing.JButton();
        H1 = new javax.swing.JButton();
        G1 = new javax.swing.JButton();
        B0 = new javax.swing.JButton();
        A0 = new javax.swing.JButton();
        D0 = new javax.swing.JButton();
        C0 = new javax.swing.JButton();
        F0 = new javax.swing.JButton();
        E0 = new javax.swing.JButton();
        H0 = new javax.swing.JButton();
        G0 = new javax.swing.JButton();
        B3 = new javax.swing.JButton();
        A3 = new javax.swing.JButton();
        D3 = new javax.swing.JButton();
        C3 = new javax.swing.JButton();
        F3 = new javax.swing.JButton();
        E3 = new javax.swing.JButton();
        H3 = new javax.swing.JButton();
        G3 = new javax.swing.JButton();
        B2 = new javax.swing.JButton();
        A2 = new javax.swing.JButton();
        D2 = new javax.swing.JButton();
        C2 = new javax.swing.JButton();
        F2 = new javax.swing.JButton();
        E2 = new javax.swing.JButton();
        H2 = new javax.swing.JButton();
        G2 = new javax.swing.JButton();
        B1 = new javax.swing.JButton();
        A1 = new javax.swing.JButton();
        D1 = new javax.swing.JButton();
        C1 = new javax.swing.JButton();
        F1 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chess Game");

        B7.setBackground(new java.awt.Color(118, 150, 86));
        B7.setName("B7"); // NOI18N
        B7.setPreferredSize(new java.awt.Dimension(75, 75));

        A7.setBackground(new java.awt.Color(238, 238, 210));
        A7.setName("A7"); // NOI18N
        A7.setPreferredSize(new java.awt.Dimension(75, 75));

        D7.setBackground(new java.awt.Color(118, 150, 86));
        D7.setName("D7"); // NOI18N
        D7.setPreferredSize(new java.awt.Dimension(75, 75));

        C7.setBackground(new java.awt.Color(238, 238, 210));
        C7.setName("C7"); // NOI18N
        C7.setPreferredSize(new java.awt.Dimension(75, 75));

        F7.setBackground(new java.awt.Color(118, 150, 86));
        F7.setName("F7"); // NOI18N
        F7.setPreferredSize(new java.awt.Dimension(75, 75));

        E7.setBackground(new java.awt.Color(238, 238, 210));
        E7.setName("E7"); // NOI18N
        E7.setPreferredSize(new java.awt.Dimension(75, 75));

        H7.setBackground(new java.awt.Color(118, 150, 86));
        H7.setName("H7"); // NOI18N
        H7.setPreferredSize(new java.awt.Dimension(75, 75));

        G7.setBackground(new java.awt.Color(238, 238, 210));
        G7.setName("G7"); // NOI18N
        G7.setPreferredSize(new java.awt.Dimension(75, 75));

        B6.setBackground(new java.awt.Color(238, 238, 210));
        B6.setName("B6"); // NOI18N
        B6.setPreferredSize(new java.awt.Dimension(75, 75));

        A6.setBackground(new java.awt.Color(118, 150, 86));
        A6.setName("A6"); // NOI18N
        A6.setPreferredSize(new java.awt.Dimension(75, 75));

        D6.setBackground(new java.awt.Color(238, 238, 210));
        D6.setName("D6"); // NOI18N
        D6.setPreferredSize(new java.awt.Dimension(75, 75));

        C6.setBackground(new java.awt.Color(118, 150, 86));
        C6.setName("C6"); // NOI18N
        C6.setPreferredSize(new java.awt.Dimension(75, 75));

        F6.setBackground(new java.awt.Color(238, 238, 210));
        F6.setName("F6"); // NOI18N
        F6.setPreferredSize(new java.awt.Dimension(75, 75));

        E6.setBackground(new java.awt.Color(118, 150, 86));
        E6.setName("E6"); // NOI18N
        E6.setPreferredSize(new java.awt.Dimension(75, 75));

        H6.setBackground(new java.awt.Color(238, 238, 210));
        H6.setName("H6"); // NOI18N
        H6.setPreferredSize(new java.awt.Dimension(75, 75));

        G6.setBackground(new java.awt.Color(118, 150, 86));
        G6.setName("G6"); // NOI18N
        G6.setPreferredSize(new java.awt.Dimension(75, 75));

        B5.setBackground(new java.awt.Color(118, 150, 86));
        B5.setName("B5"); // NOI18N
        B5.setPreferredSize(new java.awt.Dimension(75, 75));

        A5.setBackground(new java.awt.Color(238, 238, 210));
        A5.setName("A5"); // NOI18N
        A5.setPreferredSize(new java.awt.Dimension(75, 75));

        D5.setBackground(new java.awt.Color(118, 150, 86));
        D5.setName("D5"); // NOI18N
        D5.setPreferredSize(new java.awt.Dimension(75, 75));

        C5.setBackground(new java.awt.Color(238, 238, 210));
        C5.setName("C5"); // NOI18N
        C5.setPreferredSize(new java.awt.Dimension(75, 75));

        F5.setBackground(new java.awt.Color(118, 150, 86));
        F5.setName("F5"); // NOI18N
        F5.setPreferredSize(new java.awt.Dimension(75, 75));

        E5.setBackground(new java.awt.Color(238, 238, 210));
        E5.setName("E5"); // NOI18N
        E5.setPreferredSize(new java.awt.Dimension(75, 75));

        H5.setBackground(new java.awt.Color(118, 150, 86));
        H5.setName("H5"); // NOI18N
        H5.setPreferredSize(new java.awt.Dimension(75, 75));

        G5.setBackground(new java.awt.Color(238, 238, 210));
        G5.setName("G5"); // NOI18N
        G5.setPreferredSize(new java.awt.Dimension(75, 75));

        B4.setBackground(new java.awt.Color(238, 238, 210));
        B4.setName("B4"); // NOI18N
        B4.setPreferredSize(new java.awt.Dimension(75, 75));

        A4.setBackground(new java.awt.Color(118, 150, 86));
        A4.setName("A4"); // NOI18N
        A4.setPreferredSize(new java.awt.Dimension(75, 75));

        D4.setBackground(new java.awt.Color(238, 238, 210));
        D4.setName("D4"); // NOI18N
        D4.setPreferredSize(new java.awt.Dimension(75, 75));

        C4.setBackground(new java.awt.Color(118, 150, 86));
        C4.setName("C4"); // NOI18N
        C4.setPreferredSize(new java.awt.Dimension(75, 75));

        F4.setBackground(new java.awt.Color(238, 238, 210));
        F4.setName("F4"); // NOI18N
        F4.setPreferredSize(new java.awt.Dimension(75, 75));

        E4.setBackground(new java.awt.Color(118, 150, 86));
        E4.setName("E4"); // NOI18N
        E4.setPreferredSize(new java.awt.Dimension(75, 75));

        H4.setBackground(new java.awt.Color(238, 238, 210));
        H4.setName("H4"); // NOI18N
        H4.setPreferredSize(new java.awt.Dimension(75, 75));

        G4.setBackground(new java.awt.Color(118, 150, 86));
        G4.setName("G4"); // NOI18N
        G4.setPreferredSize(new java.awt.Dimension(75, 75));

        E1.setBackground(new java.awt.Color(238, 238, 210));
        E1.setName("E1"); // NOI18N
        E1.setPreferredSize(new java.awt.Dimension(75, 75));

        H1.setBackground(new java.awt.Color(118, 150, 86));
        H1.setName("H1"); // NOI18N
        H1.setPreferredSize(new java.awt.Dimension(75, 75));

        G1.setBackground(new java.awt.Color(238, 238, 210));
        G1.setName("G1"); // NOI18N
        G1.setPreferredSize(new java.awt.Dimension(75, 75));

        B0.setBackground(new java.awt.Color(238, 238, 210));
        B0.setName("B0"); // NOI18N
        B0.setPreferredSize(new java.awt.Dimension(75, 75));

        A0.setBackground(new java.awt.Color(118, 150, 86));
        A0.setName("A0"); // NOI18N
        A0.setPreferredSize(new java.awt.Dimension(75, 75));

        D0.setBackground(new java.awt.Color(238, 238, 210));
        D0.setName("D0"); // NOI18N
        D0.setPreferredSize(new java.awt.Dimension(75, 75));

        C0.setBackground(new java.awt.Color(118, 150, 86));
        C0.setName("C0"); // NOI18N
        C0.setPreferredSize(new java.awt.Dimension(75, 75));

        F0.setBackground(new java.awt.Color(238, 238, 210));
        F0.setName("F0"); // NOI18N
        F0.setPreferredSize(new java.awt.Dimension(75, 75));

        E0.setBackground(new java.awt.Color(118, 150, 86));
        E0.setName("E0"); // NOI18N
        E0.setPreferredSize(new java.awt.Dimension(75, 75));

        H0.setBackground(new java.awt.Color(238, 238, 210));
        H0.setName("H0"); // NOI18N
        H0.setPreferredSize(new java.awt.Dimension(75, 75));

        G0.setBackground(new java.awt.Color(118, 150, 86));
        G0.setName("G0"); // NOI18N
        G0.setPreferredSize(new java.awt.Dimension(75, 75));

        B3.setBackground(new java.awt.Color(118, 150, 86));
        B3.setName("B3"); // NOI18N
        B3.setPreferredSize(new java.awt.Dimension(75, 75));

        A3.setBackground(new java.awt.Color(238, 238, 210));
        A3.setName("A3"); // NOI18N
        A3.setPreferredSize(new java.awt.Dimension(75, 75));

        D3.setBackground(new java.awt.Color(118, 150, 86));
        D3.setName("D3"); // NOI18N
        D3.setPreferredSize(new java.awt.Dimension(75, 75));

        C3.setBackground(new java.awt.Color(238, 238, 210));
        C3.setName("C3"); // NOI18N
        C3.setPreferredSize(new java.awt.Dimension(75, 75));

        F3.setBackground(new java.awt.Color(118, 150, 86));
        F3.setName("F3"); // NOI18N
        F3.setPreferredSize(new java.awt.Dimension(75, 75));

        E3.setBackground(new java.awt.Color(238, 238, 210));
        E3.setName("E3"); // NOI18N
        E3.setPreferredSize(new java.awt.Dimension(75, 75));

        H3.setBackground(new java.awt.Color(118, 150, 86));
        H3.setName("H3"); // NOI18N
        H3.setPreferredSize(new java.awt.Dimension(75, 75));

        G3.setBackground(new java.awt.Color(238, 238, 210));
        G3.setName("G3"); // NOI18N
        G3.setPreferredSize(new java.awt.Dimension(75, 75));

        B2.setBackground(new java.awt.Color(238, 238, 210));
        B2.setName("B2"); // NOI18N
        B2.setPreferredSize(new java.awt.Dimension(75, 75));

        A2.setBackground(new java.awt.Color(118, 150, 86));
        A2.setName("A2"); // NOI18N
        A2.setPreferredSize(new java.awt.Dimension(75, 75));

        D2.setBackground(new java.awt.Color(238, 238, 210));
        D2.setName("D2"); // NOI18N
        D2.setPreferredSize(new java.awt.Dimension(75, 75));

        C2.setBackground(new java.awt.Color(118, 150, 86));
        C2.setName("C2"); // NOI18N
        C2.setPreferredSize(new java.awt.Dimension(75, 75));

        F2.setBackground(new java.awt.Color(238, 238, 210));
        F2.setName("F2"); // NOI18N
        F2.setPreferredSize(new java.awt.Dimension(75, 75));

        E2.setBackground(new java.awt.Color(118, 150, 86));
        E2.setName("E2"); // NOI18N
        E2.setPreferredSize(new java.awt.Dimension(75, 75));

        H2.setBackground(new java.awt.Color(238, 238, 210));
        H2.setName("H2"); // NOI18N
        H2.setPreferredSize(new java.awt.Dimension(75, 75));

        G2.setBackground(new java.awt.Color(118, 150, 86));
        G2.setName("G2"); // NOI18N
        G2.setPreferredSize(new java.awt.Dimension(75, 75));

        B1.setBackground(new java.awt.Color(118, 150, 86));
        B1.setName("B1"); // NOI18N
        B1.setPreferredSize(new java.awt.Dimension(75, 75));

        A1.setBackground(new java.awt.Color(238, 238, 210));
        A1.setName("A1"); // NOI18N
        A1.setPreferredSize(new java.awt.Dimension(75, 75));

        D1.setBackground(new java.awt.Color(118, 150, 86));
        D1.setName("D1"); // NOI18N
        D1.setPreferredSize(new java.awt.Dimension(75, 75));

        C1.setBackground(new java.awt.Color(238, 238, 210));
        C1.setName("C1"); // NOI18N
        C1.setPreferredSize(new java.awt.Dimension(75, 75));

        F1.setBackground(new java.awt.Color(118, 150, 86));
        F1.setName("F1"); // NOI18N
        F1.setPreferredSize(new java.awt.Dimension(75, 75));

        jButton1.setText("UNDO");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(A6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(B6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(C6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(D6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(E6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(F6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(G6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(H6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(A7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(B7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(C7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(D7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(E7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(F7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(G7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(H7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(A4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(B4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(C4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(D4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(E4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(F4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(G4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(H4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(A5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(B5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(C5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(D5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(E5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(F5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(G5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(H5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(A2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(B2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(C2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(D2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(E2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(F2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(G2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(H2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(A3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(B3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(C3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(D3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(E3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(F3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(G3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(H3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(A1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(B1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(C1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(D1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(E1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(F1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(G1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(H1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(A0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(B0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(C0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(D0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(E0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(F0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(G0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(H0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(D7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(E7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(H7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(G7, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(D6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(E6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(H6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(G6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(D5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(E5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(H5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(G5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(D4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(E4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(H4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(G4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(D3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(E3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(H3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(G3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(D2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(E2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(H2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(G2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(D1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(E1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(H1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(G1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(D0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(C0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(E0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(H0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(G0, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        object.undoMove();
        initBoard();        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Board().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton A0;
    private javax.swing.JButton A1;
    private javax.swing.JButton A2;
    private javax.swing.JButton A3;
    private javax.swing.JButton A4;
    private javax.swing.JButton A5;
    private javax.swing.JButton A6;
    private javax.swing.JButton A7;
    private javax.swing.JButton B0;
    private javax.swing.JButton B1;
    private javax.swing.JButton B2;
    private javax.swing.JButton B3;
    private javax.swing.JButton B4;
    private javax.swing.JButton B5;
    private javax.swing.JButton B6;
    private javax.swing.JButton B7;
    private javax.swing.JButton C0;
    private javax.swing.JButton C1;
    private javax.swing.JButton C2;
    private javax.swing.JButton C3;
    private javax.swing.JButton C4;
    private javax.swing.JButton C5;
    private javax.swing.JButton C6;
    private javax.swing.JButton C7;
    private javax.swing.JButton D0;
    private javax.swing.JButton D1;
    private javax.swing.JButton D2;
    private javax.swing.JButton D3;
    private javax.swing.JButton D4;
    private javax.swing.JButton D5;
    private javax.swing.JButton D6;
    private javax.swing.JButton D7;
    private javax.swing.JButton E0;
    private javax.swing.JButton E1;
    private javax.swing.JButton E2;
    private javax.swing.JButton E3;
    private javax.swing.JButton E4;
    private javax.swing.JButton E5;
    private javax.swing.JButton E6;
    private javax.swing.JButton E7;
    private javax.swing.JButton F0;
    private javax.swing.JButton F1;
    private javax.swing.JButton F2;
    private javax.swing.JButton F3;
    private javax.swing.JButton F4;
    private javax.swing.JButton F5;
    private javax.swing.JButton F6;
    private javax.swing.JButton F7;
    private javax.swing.JButton G0;
    private javax.swing.JButton G1;
    private javax.swing.JButton G2;
    private javax.swing.JButton G3;
    private javax.swing.JButton G4;
    private javax.swing.JButton G5;
    private javax.swing.JButton G6;
    private javax.swing.JButton G7;
    private javax.swing.JButton H0;
    private javax.swing.JButton H1;
    private javax.swing.JButton H2;
    private javax.swing.JButton H3;
    private javax.swing.JButton H4;
    private javax.swing.JButton H5;
    private javax.swing.JButton H6;
    private javax.swing.JButton H7;
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}
