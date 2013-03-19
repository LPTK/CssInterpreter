package util;

public class Single<T> {
    private T ref;

    public Single(T ref) {
    	super();
    	this.ref = ref;
    }

    public int hashCode() {
    	return  ref != null ? ref.hashCode() : 0;
    }

    public boolean equals(Object other) {
    	if (other instanceof Single) {
    		@SuppressWarnings("rawtypes")
    		Single otherSingle = (Single) other;
    		return 
    		(  this.ref == otherSingle.ref ||
    			( this.ref != null && otherSingle.ref != null &&
    			  this.ref.equals(otherSingle.ref)));
    	}

    	return false;
    }

    public String toString()
    { 
           return "(" + ref + ")"; 
    }

    public T get() {
    	return ref;
    }

    public void set(T ref) {
    	this.ref = ref;
    }
    
}
