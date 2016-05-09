package model;

public class CheeseException extends RuntimeException {
    
    public CheeseException(String string) {
        super(string);
    }
    
    public CheeseException(Exception e) {
        super(e);
    }
    
    public CheeseException(String string, CheeseGrid grid) {
        super(string);
        java.lang.System.out.println(grid.recording().toString());
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
}
