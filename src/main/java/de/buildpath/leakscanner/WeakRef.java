package de.buildpath.leakscanner;

import java.lang.ref.WeakReference;

public class WeakRef<Node> extends WeakReference<Node> {
    
    final int oldHashCode;

    public WeakRef(Node referent) {
        super(referent);
        oldHashCode = referent.hashCode();
    }
    
    @Override
    public String toString() {
        return "WeakRef("+get()+")";
    }
    
    @Override
    public boolean equals(Object e) {
        if (e != null && e instanceof WeakRef<?>) {
            return (this.get() == ((WeakRef<?>) e).get());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (this.get() == null) {
            return oldHashCode;
        }

        return this.get().hashCode();
    }
}

