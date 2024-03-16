package ChessCore;

import java.util.Stack;

public class StateCaretaker {

    private Stack<ChessBoard> states = new Stack<>();

    public void saveMemento(ChessBoard memento) {
        states.push(memento);
    }

    public ChessBoard getMemento() {
        return states.pop();
    }

    public ChessBoard checkMemento() {
        return states.peek();
    }

    public int Mementocount() {
        return states.size();
    }

    public void throwMemento() {
        states.pop();
    }

}