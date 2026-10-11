package chesscore;

public class Testmain {
      
   public static void main(String[] args) {
       
   Board b = new Board();
   b.printBoard();
System.out.println("                           ///////////////////////////////////////////////////");
System.out.println("                           ///////////////////////////////////////////////////");   
   ChessGame cg =new ChessGame("ChessGame.txt",b);
   cg.readFromFile();
System.out.println("                           ///////////////////////////////////////////////////");
System.out.println("                           ///////////////////////////////////////////////////");   
   b.printBoard();   
/*   Square a = b.getSquare("e1");
   Square c = b.getSquare("h1"); 
   King k = (King) a.getPiece();
   k.castling(b, a.getPos(), c.getPos());
   
   b.printBoard();      
   */}     
/*   
   Check cm = new Check(b);
System.out.println("                           ///////////////////////////////////////////////////");
System.out.println("                           ///////////////////////////////////////////////////");
   Square a = b.getSquare("b1");
   Square c = b.getSquare("c3");
   Square d = b.getSquare("d5");
   Square e = b.getSquare("f6");  
   Square x = b.getSquare("a2");  
   Square xx = b.getSquare("a3");     
//   Square f = b.getSquare("d1"); 
//   Square g = b.getSquare("h5");
//      Square x = b.getSquare("d7");
//      Square xx = b.getSquare("d8");

//   b.getSquare("b1").getPiece().move(b,a,c);
//   b.getSquare("a2").getPiece().move(b,x,xx);   
//   b.getSquare("c3").getPiece().move(b,c,d);     
//   b.getSquare("d5").getPiece().move(b,d,e); 
// b.getSquare("d3").getPiece().move(b,c,g);  
   b.printBoard();
   cm.checkmate();
System.out.println(cm.stalemate(Color.WHITE));
System.out.println(cm.stalemate(Color.BLACK));
*/  
   } 
    
