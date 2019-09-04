package emmaitar.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import emmaitar.common.Emmaitar;

public class EmmaitarPacketHandler
{
	public static SimpleNetworkWrapper networkWrapper;

	public EmmaitarPacketHandler()
	{
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Emmaitar.getModContainer().getModId() + "_");
		int id = 0;
		
		networkWrapper.registerMessage(PacketPaintingData.Handler.class, PacketPaintingData.class, id++, Side.CLIENT);
		networkWrapper.registerMessage(PacketEmmaitarPing.Handler.class, PacketEmmaitarPing.class, id++, Side.CLIENT);
		
		networkWrapper.registerMessage(PacketPaintingRequest.Handler.class, PacketPaintingRequest.class, id++, Side.SERVER);
		networkWrapper.registerMessage(PacketEmmaitarPong.Handler.class, PacketEmmaitarPong.class, id++, Side.SERVER);
		
		FMLLog.info("Emmaitar: Registered " + id + " packet types");
	}
}
