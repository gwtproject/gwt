package java.io;

import static javaemul.internal.InternalPreconditions.checkNotNull;

/**
 * See <a
 * href="https://docs.oracle.com/javase/8/docs/api/java/io/UncheckedIOException.html">the
 * official Java API doc</a> for details.
 */
public class UncheckedIOException extends RuntimeException {
    public UncheckedIOException(String message, IOException cause) {
        super(message, checkNotNull(cause));
    }

    public UncheckedIOException(IOException cause) {
        super(checkNotNull(cause));
    }

    @Override
    public IOException getCause() {
        return (IOException) super.getCause();
    }
}
