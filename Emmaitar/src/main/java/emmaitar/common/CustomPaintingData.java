package emmaitar.common;

import io.netty.buffer.ByteBuf;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.FMLLog;

public class CustomPaintingData
{
	public BufferedImage paintingIMG;
	
	public String identifier;
	public String authorName;
	public String title;
	public int blockWidth;
	public int blockHeight;
	public DyeReference[] dyes = new DyeReference[8];
	
	public ResourceLocation clientTexture;
	
	public CustomPaintingData()
	{
	}
	
	public CustomPaintingData(String id, String auth, String t, int w, int h)
	{
		identifier = id;
		authorName = auth;
		title = t;
		blockWidth = w;
		blockHeight = h;
	}
	
	public static CustomPaintingData unknown()
	{
		CustomPaintingData painting = new CustomPaintingData();
		painting.identifier = "unknown_id";
		painting.authorName = "UNKNOWN!";
		painting.title = "UNKNOWN!";
		painting.blockWidth = 0;
		painting.blockHeight = 0;
		return painting;
	}
	
	public void loadMetadata(Map<String, String> metaMap)
	{
		authorName = metaMap.get("author");
		title = metaMap.get("title");
		blockWidth = parseInt(metaMap.get("w"));
		blockHeight = parseInt(metaMap.get("h"));
		
		String dyeString = metaMap.get("dyes");
		String[] dyesNames = dyeString.split(",");
		if (dyesNames.length == dyes.length)
		{
			for (int i = 0; i < dyes.length; i++)
			{
				dyes[i] = DyeReference.forConfigName(dyesNames[i]);
			}
		}
	}
	
	private int parseInt(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			FMLLog.warning("Emmaitar ERROR: Invalid number format %s in painting %s meta", s, identifier);
			e.printStackTrace();
			return -1;
		}
	}
	
	public boolean checkMetaComplete()
	{
		if (!StringUtils.isEmpty(authorName) && !StringUtils.isEmpty(title) && blockWidth >= 1 && blockHeight >= 1)
		{
			for (int i = 0; i < dyes.length; i++)
			{
				DyeReference dye = dyes[i];
				if (dye == null)
				{
					FMLLog.warning("Emmaitar ERROR: Painting %s .epm meta file lacks a valid recipe!", identifier);
					FMLLog.warning("A valid recipe is a list of exactly 8 (EIGHT) comma-separated dye names. Dye names are: (case-sensitive)");
					for (DyeReference listedDye : DyeReference.values())
					{
						FMLLog.warning("    " + listedDye.configName);
					}
					return false;
				}
			}
			
			return true;
		}
		
		FMLLog.warning("Emmaitar ERROR: Painting %s .epm meta file is incomplete!", identifier);
		return false;
	}
	
	public boolean matchesDyes(DyeReference[] inputDyes)
	{
		for (int i = 0; i < dyes.length; i++)
		{
			if (i < inputDyes.length)
			{
				DyeReference dye = dyes[i];
				DyeReference inputDye = inputDyes[i];
				if (inputDye == null || dye != inputDye)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public CustomPaintingReference makeReference()
	{
		return new CustomPaintingReference(identifier);
	}
	
	public void writeData(ByteBuf data) throws IOException
	{
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeStringToBuffer(identifier);
		buffer.writeStringToBuffer(authorName);
		buffer.writeStringToBuffer(title);
		buffer.writeByte(blockWidth);
		buffer.writeByte(blockHeight);
		
		for (int i = 0; i < dyes.length; i++)
		{
			DyeReference dye = dyes[i];
			if (dye != null)
			{
				buffer.writeByte(dye.networkID);
			}
			else
			{
				buffer.writeByte(-1);
			}
		}
		
		if (paintingIMG != null)
		{
			buffer.writeInt(paintingIMG.getWidth());
			buffer.writeInt(paintingIMG.getHeight());
			buffer.writeInt(paintingIMG.getType());
			for (int j = 0; j < paintingIMG.getHeight(); j++)
			{
				for (int i = 0; i < paintingIMG.getWidth(); i++)
				{
					int rgb = paintingIMG.getRGB(i, j);
					buffer.writeInt(rgb);
				}
			}
		}
		else
		{
			buffer.writeInt(-1);
		}
	}
	
	public void readData(ByteBuf data) throws IOException
	{
		PacketBuffer buffer = new PacketBuffer(data);
		identifier = buffer.readStringFromBuffer(PaintingCatalogue.STRING_MAX_LENGTH);
		authorName = buffer.readStringFromBuffer(PaintingCatalogue.STRING_MAX_LENGTH);
		title = buffer.readStringFromBuffer(PaintingCatalogue.STRING_MAX_LENGTH);
		blockWidth = buffer.readByte();
		blockHeight = buffer.readByte();
		
		for (int i = 0; i < dyes.length; i++)
		{
			DyeReference dye = DyeReference.forID(buffer.readByte());
			dyes[i] = dye;
		}
		
		int imgWidth = buffer.readInt();
		if (imgWidth >= 0)
		{
			int imgHeight = buffer.readInt();
			int imgType = buffer.readInt();
			paintingIMG = new BufferedImage(imgWidth, imgHeight, imgType);
			for (int j = 0; j < paintingIMG.getHeight(); j++)
			{
				for (int i = 0; i < paintingIMG.getWidth(); i++)
				{
					int rgb = buffer.readInt();
					paintingIMG.setRGB(i, j, rgb);
				}
			}
		}
	}
}
