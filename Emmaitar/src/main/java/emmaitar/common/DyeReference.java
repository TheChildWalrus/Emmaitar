package emmaitar.common;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public enum DyeReference
{
	BLACK("Black", "dyeBlack"),
	RED("Red", "dyeRed"),
	GREEN("Green", "dyeGreen"),
	BROWN("Brown", "dyeBrown"),
	BLUE("Blue", "dyeBlue"),
	PURPLE("Purple", "dyePurple"),
	CYAN("Cyan", "dyeCyan"),
	LIGHT_GREY("LightGray", "dyeLightGray"),
	GREY("Gray", "dyeGray"),
	PINK("Pink", "dyePink"),
	LIME("Lime", "dyeLime"),
	YELLOW("Yellow", "dyeYellow"),
	LIGHT_BLUE("LightBlue", "dyeLightBlue"),
	MAGENTA("Magenta", "dyeMagenta"),
	ORANGE("Orange", "dyeOrange"),
	WHITE("White", "dyeWhite");
	
	public final String configName;
	public final String oreDictName;
	public final int networkID;
	
	private DyeReference(String config, String oredict)
	{
		configName = config;
		oreDictName = oredict;
		networkID = ordinal();
	}
	
	public static DyeReference forConfigName(String s)
	{
		for (DyeReference dye : values())
		{
			if (dye.configName.equals(s))
			{
				return dye;
			}
		}
		return null;
	}
	
	public static DyeReference forID(int id)
	{
		for (DyeReference dye : values())
		{
			if (dye.networkID == id)
			{
				return dye;
			}
		}
		return null;
	}
	
	public static DyeReference getItemDye(ItemStack itemstack)
	{
		if (itemstack != null)
		{
			int[] oreIDs = OreDictionary.getOreIDs(itemstack);
			for (int oreID : oreIDs)
			{
				String oreName = OreDictionary.getOreName(oreID);
				
				for (DyeReference dye : values())
				{
					if (dye.oreDictName.equals(oreName))
					{
						return dye;
					}
				}
			}
		}
		return null;
	}
	
	public ItemStack createBasicDyeItem()
	{
		List<ItemStack> dyeItems = OreDictionary.getOres(oreDictName);
		for (ItemStack item : dyeItems)
		{
			if (item.getItem() == Items.dye)
			{
				return item.copy();
			}
		}
		return null;
	}
}
