package main.java.gamelogic;

public class IllegalMoveException extends Exception {
    IllegalMoveException(){
        super("Diese Nachricht ist eine Test");
    }

    IllegalMoveException(String error){
        super(error);
    }
}
