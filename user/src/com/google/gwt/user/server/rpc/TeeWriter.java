/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.server.rpc;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A writer that wraps one writer and copies all output written to it not only to that one
 * writer but also to another writer ("tee").
 */
public class TeeWriter extends FilterWriter {
    private final Writer tee;
    
    public TeeWriter(Writer out, Writer tee) {
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
