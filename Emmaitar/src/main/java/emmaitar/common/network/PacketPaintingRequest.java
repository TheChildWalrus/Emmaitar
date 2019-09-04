package emmaitar.common.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.*;
import emmaitar.common.CustomPaintingData;
import emmaitar.common.PaintingCatalogue;

public class PacketPaintingRequest implements IMessage
{
	private String identifier;
	
	public PacketPaintingRequest() {}
	
	public PacketPaintingRequest(String id)
	{
		identifier = id;
	}
	
	@Override
	public void toBytes(ByteBuf data)
	{
		PacketBuffer buffer = new PacketBuffer(data);
		try
		{
			buffer.writeStringToBuffer(identifier);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void fromBytes(ByteBuf data)
	{
		PacketBuffer buffer = new PacketBuffer(data);
		try
		{
			identifier = buffer.readStringFromBuffer(PaintingCatalogue.STRING_MAX_LENGTH);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static class Handler implements IMessageHandler<PacketPaintingRequest, IMessage>
	{
		public Handler() {}
		
		@Override
		public IMessage onMessage(PacketPaintingRequest packet, MessageContext context)
		{
			EntityPlayerMP player = context.getServerHandler().playerEntity;
			String id = packet.identifier;
			
			CustomPaintingData painting = PaintingCatalogue.lookup(id);
			if (painting != null)
			{
				PacketPaintingData response = new PacketPaintingData(painting);
				EmmaitarPacketHandler.networkWrapper.sendTo(response, player);
			}
			
			return null;
		}
	}
}
