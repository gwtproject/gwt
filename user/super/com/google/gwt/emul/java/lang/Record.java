package java.lang;

public abstract class Record {

    protected Record(){

    }

    public abstract int hashCode();

    public abstract boolean equals(Object other);

    public abstract String toString();


    /**
     * Internal and non-standard helper to generate a readable tostring as required by
     * the Record spec.
     *
     * This method probably shouldn't exist, but be inlined into the generated toString
     * itself.
     *
     * @param typeName the name of the type, may be obfuscated
     * @param labeledComponents labeled values, already converted to a string
     * @return a string with the typename and labeled componenents
     */
    protected static String __toString(String typeName, String... labeledComponents) {
        StringBuilder sb = new StringBuilder(typeName);
        sb.append(" { ");

        for (int i = 0; i < labeledComponents.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(labeledComponents);
        }

        return sb.append(" }").toString();
    }
}