package emmaitar.common;

import java.util.Map;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import emmaitar.common.network.EmmaitarPacketHandler;

@Mod(modid = "emmaitar", name = "Emmaitar", version = "1.0", acceptableRemoteVersions = "*")
public class Emmaitar
{
	@Mod.Instance
	private static Emmaitar instance;
	
	@SidedProxy(serverSide = "emmaitar.common.EmmaitarCommonProxy", clientSide = "emmaitar.client.EmmaitarClientProxy")
	public static EmmaitarCommonProxy proxy;
	
	public static EmmaitarEventHandler modEventHandler;
    private static EmmaitarPacketHandler packetHandler;
    
    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
    	proxy.load();
    	modEventHandler = new EmmaitarEventHandler();
    	packetHandler = new EmmaitarPacketHandler();
    	
    	PaintingCatalogue.loadAll();
    	GameRegistry.addRecipe(new RecipePaintings());
    	
    	EntityRegistry.registerModEntity(EntityCustomPainting.class, "EMPainting", 0, instance, 160, Integer.MAX_VALUE, false);
    }
    
    public static ModContainer getModContainer()
    {
    	return FMLCommonHandler.instance().findContainerFor(instance);
    }
}
