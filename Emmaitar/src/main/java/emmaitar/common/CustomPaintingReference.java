package emmaitar.common;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CustomPaintingReference
{
	public String identifier;
	
	public CustomPaintingReference()
	{
	}
	
	public CustomPaintingReference(String s)
	{
		identifier = s;
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("PaintingID", identifier);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		identifier = nbt.getString("PaintingID");
	}
	
	public static boolean isCustomPaintingItem(ItemStack itemstack)
	{
		return getCustomPainting(itemstack) != null;
	}
	
	public static CustomPaintingReference getCustomPainting(ItemStack itemstack)
	{
		if (itemstack.hasTagCompound())
		{
			NBTTagCompound data = itemstack.getTagCompound().getCompoundTag("Emmaitar");
			if (!data.hasNoTags())
			{
				CustomPaintingReference reference = new CustomPaintingReference();
				reference.readFromNBT(data);
				return reference;
			}
		}
		return null;
	}
	
	public static void setCustomPainting(ItemStack itemstack, CustomPaintingReference reference)
	{
		NBTTagCompound data = new NBTTagCompound();
		reference.writeToNBT(data);
		itemstack.setTagInfo("Emmaitar", data);
	}
}
