package emmaitar.common.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.*;

public class PacketEmmaitarPing implements IMessage
{
	public PacketEmmaitarPing() {}

	@Override
	public void toBytes(ByteBuf data) {}

	@Override
	public void fromBytes(ByteBuf data) {}
	
	public static class Handler implements IMessageHandler<PacketEmmaitarPing, IMessage>
	{
		public Handler() {}
		
		@Override
		public IMessage onMessage(PacketEmmaitarPing packet, MessageContext context)
		{
			return new PacketEmmaitarPong();
		}
	}
}
