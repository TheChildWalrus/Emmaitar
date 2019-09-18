package emmaitar.common.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import cpw.mods.fml.common.network.simpleimpl.*;
import emmaitar.client.ClientPaintingCatalogue;
import emmaitar.common.CustomPaintingData;

public class PacketPaintingImage implements IMessage
{
	private CustomPaintingData painting;
	
	public PacketPaintingImage() {}
	
	public PacketPaintingImage(CustomPaintingData p)
	{
		painting = p;
	}
	
	@Override
	public void toBytes(ByteBuf data)
	{
		try
		{
			painting.writeIDAndImage(data);
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
			painting.readIDAndImage(data);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static class Handler implements IMessageHandler<PacketPaintingImage, IMessage>
	{
		public Handler() {}
		
		@Override
		public IMessage onMessage(PacketPaintingImage packet, MessageContext context)
		{
			ClientPaintingCatalogue.addPaintingImage(packet.painting.identifier, packet.painting.paintingIMG);
			
			return null;
		}
	}
}
