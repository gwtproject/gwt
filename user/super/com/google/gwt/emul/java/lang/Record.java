package java.lang;

public abstract class Record {

    protected Record(){

    }

    public abstract int hashCode();

    public abstract boolean equals(Object other);

    public abstract String toString();
}