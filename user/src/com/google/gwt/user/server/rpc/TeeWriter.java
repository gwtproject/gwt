package com.google.gwt.user.server.rpc;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A writer that wraps one writer and copies all output written to it not only to that one
 * writer but also to another writer ("tee").
 * 
 * @author Axel Uhl
 *
 */
public class TeeWriter extends FilterWriter {
    private final Writer tee;
    
    protected TeeWriter(Writer out, Writer tee) {
        super(out);
        this.tee = tee;
    }

    @Override
    public void write(int c) throws IOException {
        super.write(c);
        tee.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        super.write(cbuf, off, len);
        tee.write(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        super.write(str, off, len);
        tee.write(str, off, len);
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        tee.flush();
    }

    @Override
    public void close() throws IOException {
        super.close();
        tee.close();
    }
}
