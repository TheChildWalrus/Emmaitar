package emmaitar.common;

import net.minecraftforge.oredict.RecipeSorter;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import emmaitar.common.network.EmmaitarPacketHandler;

@Mod(modid = "emmaitar", name = "Emmaitar", version = "1.1", acceptableRemoteVersions = "*")
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
    	RecipeSorter.register(getModContainer().getModId() + ":painting", RecipePaintings.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
    	GameRegistry.addRecipe(new RecipePaintings());
    	
    	EntityRegistry.registerModEntity(EntityCustomPainting.class, "EMPainting", 0, instance, 160, Integer.MAX_VALUE, false);
    }
    
    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	if (!event.getServer().isDedicatedServer())
    	{
    		event.registerServerCommand(new CommandPrintPaintingInfo());
    	}
    	event.registerServerCommand(new CommandPaintingGive());
    }
    
    public static ModContainer getModContainer()
    {
    	return FMLCommonHandler.instance().findContainerFor(instance);
    }
}
