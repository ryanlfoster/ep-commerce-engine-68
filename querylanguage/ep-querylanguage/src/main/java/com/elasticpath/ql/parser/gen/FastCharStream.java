package com.elasticpath.ql.parser.gen;

import java.io.IOException;
import java.io.Reader;


/**
 * An efficient implementation of JavaCC's CharStream interface.
 * <p>THIS FILE IS NOT GENERATED. However, it is basically a copy of the same CharStream interface from the Lucene implementation.</p> 
 * <p>
 * Note that this does not do line-number counting, but instead keeps track of the character position of the token in the input, as required by
 * Lucene's {@link org.apache.lucene.analysis.Token} API. This file was copied from org\apache\lucene\queryParser\.</p>
 */
@SuppressWarnings("PMD")
//CHECKSTYLE:OFF
public final class FastCharStream implements CharStream {
	private char[] buffer = null;

	private static final int BUFF_SIZE = 2048;

	private int bufferLength = 0; // end of valid chars

	private int bufferPosition = 0; // next char to read

	private int tokenStart = 0; // offset in buffer

	private int bufferStart = 0; // position in file of buffer

	private final Reader input; // source of chars

	/**
	 * Constructs from a Reader.
	 * 
	 * @param reader the Reader
	 */
	public FastCharStream(final Reader reader) {
		input = reader;
	}

	@Override
	public char readChar() throws IOException {
		if (bufferPosition >= bufferLength) {
			refill();
		}
		return buffer[bufferPosition++];
	}

	private void refill() throws IOException {
		int newPosition = bufferLength - tokenStart;

		if (tokenStart == 0) { // token won't fit in buffer
			if (buffer == null) { // first time: alloc buffer
				buffer = new char[BUFF_SIZE];
			} else if (bufferLength == buffer.length) { // grow buffer
				char[] newBuffer = new char[buffer.length * 2];
				System.arraycopy(buffer, 0, newBuffer, 0, bufferLength);
				buffer = newBuffer;
			}
		} else { // shift token to front
			System.arraycopy(buffer, tokenStart, buffer, 0, newPosition);
		}

		bufferLength = newPosition; // update state
		bufferPosition = newPosition;
		bufferStart += tokenStart;
		tokenStart = 0;

		int charsRead = // fill space in buffer
		input.read(buffer, newPosition, buffer.length - newPosition);
		if (charsRead == -1) {
			throw new IOException("read past eof");
		}
		bufferLength += charsRead;
	}

	@Override
	@SuppressWarnings("PMD.MethodNamingConventions")
	public char BeginToken() throws IOException {
		tokenStart = bufferPosition;
		return readChar();
	}

	@Override
	public void backup(final int amount) {
		bufferPosition -= amount;
	}

	@Override
	@SuppressWarnings("PMD.MethodNamingConventions")
	public String GetImage() {
		return new String(buffer, tokenStart, bufferPosition - tokenStart);
	}

	@Override
	@SuppressWarnings("PMD.MethodNamingConventions")
	public char[] GetSuffix(final int len) {
		char[] value = new char[len];
		System.arraycopy(buffer, bufferPosition - len, value, 0, len);
		return value;
	}

	@Override
	@SuppressWarnings({"PMD.MethodNamingConventions","PMD.SystemPrintln"})
	public void Done() {
		try {
			input.close();
		} catch (IOException e) {
			System.err.println("Caught: " + e + "; ignoring.");
		}
	}

	@Override
	public int getColumn() {
		return bufferStart + bufferPosition;
	}

	@Override
	public int getLine() {
		return 1;
	}

	@Override
	public int getEndColumn() {
		return bufferStart + bufferPosition;
	}

	@Override
	public int getEndLine() {
		return 1;
	}

	@Override
	public int getBeginColumn() {
		return bufferStart + tokenStart;
	}

	@Override
	public int getBeginLine() {
		return 1;
	}
}
