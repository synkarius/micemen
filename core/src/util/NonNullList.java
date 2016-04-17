package util;

import java.util.ArrayList;

public class NonNullList<E> extends ArrayList<E> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean add(E e) {
        return (e == null) ? false : super.add(e);
    }
    
}
