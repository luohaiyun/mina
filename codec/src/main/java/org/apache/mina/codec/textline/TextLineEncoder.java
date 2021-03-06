/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.mina.codec.textline;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.codec.ProtocolEncoder;
import org.apache.mina.codec.StatelessProtocolEncoder;

/**
 * A {@link ProtocolEncoder} which encodes a string into a text line
 * which ends with the delimiter.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class TextLineEncoder implements StatelessProtocolEncoder<String, ByteBuffer> {
    private final CharsetEncoder charsetEncoder;

    private final LineDelimiter delimiter;

    private int maxLineLength = Integer.MAX_VALUE;

    /**
     * Creates a new instance with the current default {@link Charset}
     * and {@link LineDelimiter#UNIX} delimiter.
     */
    public TextLineEncoder() {
        this(Charset.defaultCharset(), LineDelimiter.UNIX);
    }

    /**
     * Creates a new instance with the current default {@link Charset}
     * and the specified <tt>delimiter</tt>.
     */
    public TextLineEncoder(String delimiter) {
        this(new LineDelimiter(delimiter));
    }

    /**
     * Creates a new instance with the current default {@link Charset}
     * and the specified <tt>delimiter</tt>.
     */
    public TextLineEncoder(LineDelimiter delimiter) {
        this(Charset.defaultCharset(), delimiter);
    }

    /**
     * Creates a new instance with the spcified <tt>charset</tt>
     * and {@link LineDelimiter#UNIX} delimiter.
     */
    public TextLineEncoder(Charset charset) {
        this(charset, LineDelimiter.UNIX);
    }

    /**
     * Creates a new instance with the spcified <tt>charset</tt>
     * and the specified <tt>delimiter</tt>.
     */
    public TextLineEncoder(Charset charset, String delimiter) {
        this(charset, new LineDelimiter(delimiter));
    }

    /**
     * Creates a new instance with the spcified <tt>charset</tt>
     * and the specified <tt>delimiter</tt>.
     */
    public TextLineEncoder(Charset charset, LineDelimiter delimiter) {
        if (charset == null) {
            throw new IllegalArgumentException("charset");
        }
        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter");
        }
        if (LineDelimiter.AUTO.equals(delimiter)) {
            throw new IllegalArgumentException("AUTO delimiter is not allowed for encoder.");
        }

        this.charsetEncoder = charset.newEncoder();
        this.delimiter = delimiter;
    }

    /**
     * Returns the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public int getMaxLineLength() {
        return maxLineLength;
    }

    /**
     * Sets the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public void setMaxLineLength(int maxLineLength) {
        if (maxLineLength <= 0) {
            throw new IllegalArgumentException("maxLineLength: " + maxLineLength);
        }

        this.maxLineLength = maxLineLength;
    }

    @Override
    public Void createEncoderState() {
        return null;
    }

    @Override
    public ByteBuffer encode(String message, Void context) {
        try {
            String value = (message == null ? "" : message);

            if (value.length() > maxLineLength) {
                throw new IllegalArgumentException("Line length: " + message.length());
            }
            return charsetEncoder.encode(CharBuffer.wrap(value + delimiter.getValue()));
        } catch (CharacterCodingException e) {
            throw new RuntimeException(e);
        }
    }

}