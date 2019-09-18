package emmaitar.client;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import emmaitar.common.*;
import emmaitar.common.network.EmmaitarPacketHandler;
import emmaitar.common.network.PacketPaintingRequest;

public class ClientPaintingCatalogue
{
	private static List<CustomPaintingData> allPaintings = new ArrayList();
	private static Map<String, CustomPaintingData> idLookup = new HashMap();
	private static Map<String, Integer> idRequestTicks = new HashMap();
	private static final int clientRequestInterval = 200;
	
	public static void clearAll()
	{
		allPaintings.clear();
		idLookup.clear();
		idRequestTicks.clear();
	}
	
	public static void onTick()
	{
		Set<String> removes = new HashSet();
		
		for (Entry<String, Integer> e : idRequestTicks.entrySet())
		{
			String id = e.getKey();
			int tick = e.getValue();
			
			tick--;
			e.setValue(tick);
			if (tick <= 0)
			{
				removes.add(id);
			}
		}
		
		for (String id : removes)
		{
			idRequestTicks.remove(id);
		}
	}
	
	public static void addPainting(CustomPaintingData painting)
	{
		allPaintings.add(painting);
		idLookup.put(painting.identifier, painting);
	}
	
	private static void requestImageFromServer(String id)
	{
		PacketPaintingRequest pkt = new PacketPaintingRequest(id);
		EmmaitarPacketHandler.networkWrapper.sendToServer(pkt);
	}
	
	public static void addPaintingImage(String identifier, BufferedImage image)
	{
		CustomPaintingData painting = idLookup.get(identifier);
		painting.paintingIMG = image;
		
		TextureManager texManager = Minecraft.getMinecraft().getTextureManager();
		String texPath = Emmaitar.getModContainer().getModId() + ":painting_" + painting.identifier;
		painting.clientTexture = texManager.getDynamicTextureLocation(texPath, new DynamicTexture(painting.paintingIMG));
	}
	
	public static CustomPaintingData lookup(CustomPaintingReference reference, boolean request)
	{
		return lookup(reference.identifier, request);
	}
	
	public static CustomPaintingData lookup(String id, boolean request)
	{
		CustomPaintingData painting = idLookup.get(id);
		if (request && (painting == null || painting.clientTexture == null))
		{
			int tick = idRequestTicks.containsKey(id) ? idRequestTicks.get(id) : 0;
			if (tick <= 0)
			{
				requestImageFromServer(id);
				idRequestTicks.put(id, clientRequestInterval);
			}
		}
		return painting;
	}

	public static CustomPaintingData lookupByDyes(DyeReference[] dyes)
	{
		for (CustomPaintingData painting : allPaintings)
		{
			if (painting.matchesDyes(dyes))
			{
				return painting;
			}
		}
		return null;
	}
}
