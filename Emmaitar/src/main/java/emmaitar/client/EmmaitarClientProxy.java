package emmaitar.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import emmaitar.common.*;

public class EmmaitarClientProxy extends EmmaitarCommonProxy
{
	private static EmmaitarClientEventHandler eventHandler;
	
	@Override
	public void load()
	{
		eventHandler = new EmmaitarClientEventHandler();
		
		RenderingRegistry.registerEntityRenderingHandler(EntityCustomPainting.class, new RenderCustomPainting());
	}
	
	@Override
	public CustomPaintingData lookupClientPainting(CustomPaintingReference reference)
	{
		return ClientPaintingCatalogue.lookup(reference, true);
	}
	
	@Override
	public CustomPaintingData lookupPaintingByDyes(DyeReference[] dyes)
	{
		return ClientPaintingCatalogue.lookupByDyes(dyes);
	}
}
