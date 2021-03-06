package emmaitar.common;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.FMLLog;
import emmaitar.common.network.EmmaitarPacketHandler;
import emmaitar.common.network.PacketPaintingData;

public class PaintingCatalogue
{
	private static List<CustomPaintingData> allPaintings = new ArrayList();
	private static Map<String, CustomPaintingData> idLookup = new HashMap();
	private static Map<Pair<String, String>, CustomPaintingData> authorTitleLookup = new HashMap();
	
	private static List<CustomPaintingData> recipeConflicting = new ArrayList();
	
	public static final int STRING_MAX_LENGTH = 128;
	
	public static void loadAll()
	{
		File dir = getPaintingDir();
		if (!dir.exists())
		{
			dir.mkdirs();
		}
			
		File[] subfiles = dir.listFiles();
		subLoop:
		for (File sub : subfiles)
		{
			if (sub.getName().endsWith(".png"))
			{
				String filename = sub.getName();
				filename = filename.substring(0, filename.indexOf(".png"));
				if (filename.length() > STRING_MAX_LENGTH)
				{
					FMLLog.warning("Emmaitar ERROR: Painting %s needs a shorter ID! Max filename length is %d", filename, STRING_MAX_LENGTH);
					continue subLoop;
				}
				
				BufferedImage img = null;
				try
				{
					img = readPaintingImage(sub);
				}
				catch (IOException e)
				{
					FMLLog.warning("Emmaitar ERROR: Failed to load painting image from file %s", sub.getName());
					e.printStackTrace();
				}
				
				if (img != null)
				{
					if (img.getWidth() % 16 != 0)
					{
						FMLLog.warning("Emmaitar ERROR: Painting %s width is not a multiple of 16!", filename);
						continue subLoop;
					}
					else if (img.getHeight() % 16 != 0)
					{
						FMLLog.warning("Emmaitar ERROR: Painting %s height is not a multiple of 16!", filename);
						continue subLoop;
					}
					
					File metaFile = new File(dir, filename + ".epm");
					if (!metaFile.exists())
					{
						FMLLog.warning("Emmaitar ERROR: Painting %s has no .epm meta file!", filename);
						continue subLoop;
					}
					
					CustomPaintingData painting = new CustomPaintingData();
					painting.paintingIMG = img;
					painting.identifier = filename;
					if (loadPaintingMeta(painting, metaFile))
					{
						registerPainting(painting);
					}
				}
			}
		}
		
		FMLLog.info("Emmaitar: Successfully loaded %d paintings", allPaintings.size());
	}
	
	public static File getPaintingDir()
	{
		return new File("emmaitar-paintings");
	}
	
	private static BufferedImage readPaintingImage(File file) throws IOException
	{
		BufferedImage loaded = ImageIO.read(file);
		BufferedImage converted = new BufferedImage(loaded.getWidth(), loaded.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		converted.createGraphics().drawImage(loaded, null, 0, 0);
		return converted;
	}
	
	private static boolean loadPaintingMeta(CustomPaintingData painting, File metaFile)
	{
		try
		{
			Map<String, String> metaValues = new HashMap();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(new FileInputStream(metaFile)), Charsets.UTF_8.name()));
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("#"))
				{
					continue;
				}
				
				int i = line.indexOf("=");
				if (i >= 0)
				{
					String key = line.substring(0, i);
					String val = line.substring(i + 1);
					metaValues.put(key, val);
				}
			}
			reader.close();
			
			painting.loadMetadata(metaValues);
			if (painting.checkMetaComplete())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (IOException e)
		{
			FMLLog.warning("Emmaitar ERROR: Failed to load .epm meta file for painting %s", painting.identifier);
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean registerPainting(CustomPaintingData painting)
	{
		if (idLookup.containsKey(painting.identifier))
		{
			FMLLog.severe("Emmaitar ERROR: A custom painting with the ID %s already exists! You need to rename the painting file.", painting.identifier);
			return false;
		}
		
		Pair<String, String> authorTitleKey = Pair.of(painting.authorName, painting.title);
		if (authorTitleLookup.containsKey(authorTitleKey))
		{
			FMLLog.severe("Emmaitar ERROR: A custom painting with (author,title) = (%s,%s) already exists! Change the author and/or title in the painting's meta file.", painting.authorName, painting.title);
			return false;
		}
		
		CustomPaintingData recipeConflict = lookupByDyes(painting.dyes);
		if (recipeConflict != null)
		{
			FMLLog.severe("Emmaitar ERROR: Recipe conflict! Painting %s already has the same recipe as painting %s! Change one of these recipes.", recipeConflict.identifier, painting.identifier);
			
			if (!recipeConflicting.contains(painting))
			{
				recipeConflicting.add(painting);
			}
			if (!recipeConflicting.contains(recipeConflict))
			{
				recipeConflicting.add(recipeConflict);
			}
		}
		
		allPaintings.add(painting);
		idLookup.put(painting.identifier, painting);
		authorTitleLookup.put(authorTitleKey, painting);
		//FMLLog.info("Emmaitar: Successfully loaded painting: %s", painting.identifier); // No need to log the successful loads
		return true;
	}
	
	public static CustomPaintingData lookup(CustomPaintingReference reference)
	{
		return lookup(reference.identifier);
	}
	
	public static CustomPaintingData lookup(String identifier)
	{
		CustomPaintingData found = idLookup.get(identifier);
		if (found != null)
		{
			return found;
		}
		return null;
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
	
	public static List<CustomPaintingData> listAllPaintings()
	{
		return new ArrayList(allPaintings);
	}
	
	public static List<String> listAllPaintingIDs()
	{
		List<String> list = new ArrayList();
		for (CustomPaintingData painting : allPaintings)
		{
			list.add(painting.identifier);
		}
		return list;
	}

	public static CustomPaintingData getRandomPainting(Random rand)
	{
		return allPaintings.get(rand.nextInt(allPaintings.size()));
	}
	
	public static void sendLoginToPlayer(EntityPlayerMP player)
	{
		for (CustomPaintingData painting : allPaintings)
		{
			PacketPaintingData pkt = new PacketPaintingData(painting);
			EmmaitarPacketHandler.networkWrapper.sendTo(pkt, player);
		}
	}
	
	public static List<String> listConflictingPaintingIDs()
	{
		List<String> list = new ArrayList();
		for (CustomPaintingData painting : recipeConflicting)
		{
			list.add(painting.identifier);
		}
		return list;
	}
}
