package io.github.laplacedemon.asyncmysql.util;

/**
 * 自动扩容的ByteBuffer
 * @author jackie.sjq
 *
 */
public class AutoByteBuffer {
	private byte[] bs;
	private int index;
	private int length;
	private static final int InitLen = 64;
	
	public AutoByteBuffer() {
		this.bs = new byte[InitLen];
		this.index = 0;
		this.length = InitLen;
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
