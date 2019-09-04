package emmaitar.common.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import cpw.mods.fml.common.network.simpleimpl.*;
import emmaitar.client.ClientPaintingCatalogue;
import emmaitar.common.CustomPaintingData;
import emmaitar.common.PaintingCatalogue;

public class PacketPaintingData implements IMessage
{
	private CustomPaintingData painting;
	
	public PacketPaintingData() {}
	
	public PacketPaintingData(CustomPaintingData p)
	{
		painting = p;
	}
	
	@Override
	public void toBytes(ByteBuf data)
	{
		try
		{
			painting.writeData(data);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void fromBytes(ByteBuf data)
	{
		painting = new CustomPaintingData();
		try
		{
			painting.readData(data);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static class Handler implements IMessageHandler<PacketPaintingData, IMessage>
	{
		public Handler() {}
		
		@Override
		public IMessage onMessage(PacketPaintingData packet, MessageContext context)
		{
			CustomPaintingData painting = packet.painting;
			if (painting.checkMetaComplete())
			{
				ClientPaintingCatalogue.addPainting(painting);
			}
			
			return null;
		}
	}
}
