package com.schneider.mstt.cost.center.mirror.exceptions;

public class SciformaException extends Exception {
    
    public SciformaException() {
        super();
    }
    
    public SciformaException(String message) {
        super(message);
    }
    
    public SciformaException(String message, Exception e) {
        super(message, e);
    }
}
