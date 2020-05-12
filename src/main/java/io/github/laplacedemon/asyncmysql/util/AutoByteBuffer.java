package io.github.laplacedemon.asyncmysql.util;

/**
 * Automatic expansion buffer
 * @author jackie.sjq
 *
 */
public class AutoByteBuffer {
	public static final int INIT_LENGTH = 64;
	private byte[] bs;
	private int index;
	private int length;
	
	public AutoByteBuffer() {
		this.bs = new byte[INIT_LENGTH];
		this.index = 0;
		this.length = INIT_LENGTH;
	}
	
	public AutoByteBuffer(int len) {
		this.bs = new byte[len];
		this.index = 0;
		this.length = len;
	}
	
	public void append(byte b) {
		bs[this.index] = b;
		this.index++;
		if(this.index == this.length) {
			byte[] newbs = new byte[this.length*2];
			System.arraycopy(this.bs, 0, newbs, 0, this.length);
			this.bs = newbs;
		}
	}

	public byte[] getBytes() {
		return bs;
	}
}
