package meshditor;

import java.io.*;

/** This is a class for capturing Java error messages. */
class MyFilterOutputStream extends OutputStream {
    public MyFilterOutputStream() {
    }
    
    public MyFilterOutputStream(OutputStream out) {
	this.out= out;
    }
    
    protected OutputStream out;
    
    
    public void write(int b) throws IOException {
	out.write(b);
    }
    
    public void write(byte[] b) throws IOException {
	write(b, 0, b.length);
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
	for (int i= 0; i < len; i++) 
	    write(b[off+i]);
    }
    
    public void flush() throws IOException {
	out.flush();
    }
}
