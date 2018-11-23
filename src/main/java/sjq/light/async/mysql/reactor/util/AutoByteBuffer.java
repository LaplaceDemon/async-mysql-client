package sjq.light.async.mysql.reactor.util;

public class AutoByteBuffer {
	private byte[] bs;
	private int index;
	private int length;
	
	public AutoByteBuffer() {
		int len = 64;
		this.bs = new byte[len];
		this.index = 0;
		this.length = len;
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
