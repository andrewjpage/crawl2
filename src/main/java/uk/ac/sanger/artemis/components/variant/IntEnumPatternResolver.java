package uk.ac.sanger.artemis.components.variant;

/*
 * Lifted from : 
 * 		http://dhruba.name/2008/12/31/effective-java-item-32-use-enumset-instead-of-bit-fields/
 */
public class IntEnumPatternResolver {

    private int current = 0;
    
    public void set(final int flags) {
    	this.current = flags;
    }

    public boolean isEnabled(final int flag) {
        return (current & flag) == flag;
    }

    public int current() {
        return current;
    }

    public void enable(final int flag) {
        current |= flag;
    }

    public void disable(final int flag) {
        current &= ~flag;
    }

    public void toggle(final int flag) {
        current ^= flag;
    }

    /*
     * bulk operations
     */

    public void enableAll(final int... flags) {
        for (final int flag : flags) {
            enable(flag);
        }
    }

}