package com.github.karllevik.qmorph.viewer;

import java.io.IOException;
import java.io.OutputStream;

/** This is a class for capturing Java error messages. */
class MyFilterOutputStream extends OutputStream {
	public MyFilterOutputStream() {
	}

	public MyFilterOutputStream(OutputStream out) {
		this.out = out;
	}

	protected OutputStream out;

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			write(b[off + i]);
		}
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}
}
