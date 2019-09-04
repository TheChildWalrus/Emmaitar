package emmaitar.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.simpleimpl.*;
import emmaitar.common.Emmaitar;

public class PacketEmmaitarPong implements IMessage
{
	public PacketEmmaitarPong() {}

	@Override
	public void toBytes(ByteBuf data) {}

	@Override
	public void fromBytes(ByteBuf data) {}
	
	public static class Handler implements IMessageHandler<PacketEmmaitarPong, IMessage>
	{
		public Handler() {}
		
		@Override
		public IMessage onMessage(PacketEmmaitarPong packet, MessageContext context)
		{
			EntityPlayerMP player = context.getServerHandler().playerEntity;
			Emmaitar.modEventHandler.receivePong(player);
			
			return null;
		}
	}
}
