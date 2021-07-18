package com.explosionduck.micemen.domain;

public class CheeseException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CheeseException(String string) {
        super(string);
    }

    public CheeseException(Exception e) {
        super(e);
    }

    public CheeseException(String string, CheeseGrid grid) {
        super(string);
    }

}
