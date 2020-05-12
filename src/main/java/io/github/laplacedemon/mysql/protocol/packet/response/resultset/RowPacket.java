package io.github.laplacedemon.mysql.protocol.packet.response.resultset;

import java.io.IOException;

import io.github.laplacedemon.mysql.protocol.buffer.InputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.buffer.MySQLMessage;
import io.github.laplacedemon.mysql.protocol.buffer.OutputMySQLBuffer;
import io.github.laplacedemon.mysql.protocol.packet.MySQLPacket;

/**
 * https://segmentfault.com/a/1190000012166738
 * @author jackie.sjq
 */
public class RowPacket extends MySQLPacket {
    /**
         * 多个lenenc_string。
     */
    private String[] values;
    
	public RowPacket(int fieldCount) {
		super();
		this.values = new String[fieldCount];
	}
	
	public String[] values() {
		return values;
	}

	@Override
	public void read(InputMySQLBuffer buffer) throws IOException {
		for(int i = 0; i < this.values.length;i++) {
			String value = buffer.readLenencString();
			values[i] = value;
		}
	}

	@Override
	public void write(MySQLMessage message, OutputMySQLBuffer output) {
		super.write(message, null);
		
		for(int i = 0; i < this.values.length;i++) {
			String value = this.values[i];
			message.writeLenencString(value);
		}
		
		output.write(message);
	}

//    @Override
//    public void write(ChannelHandlerContext ctx) {
//        ByteBuf buffer = ctx.alloc().buffer();
//        buffer.writeByte(super.sequenceId);
//        NettyByteBufUtils.writeLenencString(buffer, text);
//        ctx.channel().write(buffer);
//    }

}
