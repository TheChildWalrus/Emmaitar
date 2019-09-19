package emmaitar.client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import emmaitar.common.CustomPaintingData;
import emmaitar.common.PaintingCatalogue;

public class PaintingInfoPrinter
{
	public static void printRecipe(CustomPaintingData painting) throws IOException
	{
		File dir = new File(PaintingCatalogue.getPaintingDir(), "info-printouts");
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		File printoutFile = new File(dir, painting.identifier + ".png");
		
		int width = 600;
		int height = 400;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		int border = 10;
		
		g2d.setColor(new Color(0xFFB7B7B7));
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(new Color(0xFF261C09));
		g2d.fillRect(1, 1, width - 2, height - 2);
		
		int textScale = 2;
		BufferedImage title = loadText(StatCollector.translateToLocalFormatted("emmaitar.tooltip.title", painting.title));
		BufferedImage author = loadText(StatCollector.translateToLocalFormatted("emmaitar.tooltip.author", painting.authorName));
		BufferedImage dims = loadText(StatCollector.translateToLocalFormatted("emmaitar.tooltip.dimensions", painting.blockWidth, painting.blockHeight));
		title = rescale(title, textScale);
		author = rescale(author, textScale);
		dims = rescale(dims, textScale);
		g2d.drawImage(title, null, border, border);
		g2d.drawImage(author, null, border, border + 25);
		g2d.drawImage(dims, null, border, border + 50);
		
		int belowTextY = border + 80;
		
		final int craftingScale = 3;
		BufferedImage crafting = loadMCTexture(new ResourceLocation("textures/gui/container/crafting_table.png"));
		BufferedImage craftingGrid = crafting.getSubimage(24, 11, 64, 64);
		craftingGrid = rescale(craftingGrid, craftingScale);
		g2d.drawImage(craftingGrid, null, border, belowTextY);
		
		ItemStack[] recipeItems = new ItemStack[9];
		int dyeIndex = 0;
		for (int i = 0; i < recipeItems.length; i++)
		{
			if (i == 4)
			{
				recipeItems[i] = new ItemStack(Items.painting);
			}
			else
			{
				recipeItems[i] = painting.dyes[dyeIndex].createBasicDyeItem();
				dyeIndex++;
			}
		}
		for (int i = 0; i < recipeItems.length; i++)
		{
			ItemStack item = recipeItems[i];
			int slotX = i % 3;
			int slotY = i / 3;
			
			BufferedImage itemIcon = loadItemIcon(item.getIconIndex());
			itemIcon = rescale(itemIcon, craftingScale);
			
			g2d.drawImage(itemIcon, null, border + (6 + slotX * 18) * craftingScale, belowTextY + (6 + slotY * 18) * craftingScale);
		}
		
		int paintingX = 240;
		int paintingY = belowTextY;
		int paintingScale = 6;
		BufferedImage paintingImage = null;
		while (paintingScale > 1)
		{
			paintingImage = rescale(painting.paintingIMG, paintingScale);
			if (paintingX + paintingImage.getWidth() > width - border || paintingY + paintingImage.getHeight() > height - border)
			{
				paintingScale--;
			}
			else
			{
				break;
			}
		}
		g2d.drawImage(paintingImage, null, paintingX, paintingY);
		g2d.dispose();

		ImageIO.write(image, "png", printoutFile);
	}
	
	private static BufferedImage loadMCTexture(ResourceLocation res) throws IOException
	{
		BufferedImage image = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream());
		return image;
	}
	
	private static BufferedImage loadItemIcon(IIcon icon) throws IOException
	{
		ResourceLocation iconPath = new ResourceLocation(icon.getIconName());
		ResourceLocation res = new ResourceLocation(iconPath.getResourceDomain(), "textures/items/" + iconPath.getResourcePath() + ".png");
		return loadMCTexture(res);
	}
	
	private static BufferedImage loadText(String s) throws IOException
	{
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		BufferedImage fontImage = loadMCTexture(new ResourceLocation("textures/font/ascii.png"));
		
		int width = fr.getStringWidth(s);
		int height = fr.FONT_HEIGHT;
		BufferedImage textImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = textImage.createGraphics();
		
		int posX = 0;
		int posY = 0;
		for (int i = 0; i < s.length(); ++i)
        {
            char c = s.charAt(i);
            // The list of font characters, from the font texture
            int charIndex = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(c);
            if (charIndex != -1)
            {
            	int charWidth = fr.getCharWidth(c);
            	
                int x = (charIndex % 16) * 8;
                int y = (charIndex / 16) * 8;
                
                g2d.drawImage(fontImage.getSubimage(x, y, 8, 8), null, posX, posY);

                posX += charWidth;
            }
        }
		
		g2d.dispose();
		return textImage;
	}
	
	private static BufferedImage rescale(BufferedImage orig, int scale)
	{
		BufferedImage rescaled = new BufferedImage(orig.getWidth() * scale, orig.getHeight() * scale, orig.getType());
		Graphics2D g2d = rescaled.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2d.drawImage(orig, 0, 0, rescaled.getWidth(), rescaled.getHeight(), null);
		g2d.dispose();
		return rescaled;
	}
}
