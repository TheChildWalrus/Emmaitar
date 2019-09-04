package emmaitar.common;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipePaintings implements IRecipe
{
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		return getCraftingResult(inv) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		if (inv.getSizeInventory() == 9)
		{
			ItemStack centre = inv.getStackInSlot(4);
			if (centre != null && centre.getItem() == Items.painting && !CustomPaintingReference.isCustomPaintingItem(centre))
			{
				DyeReference[] dyes = new DyeReference[8];
				int dyeIndex = 0;
				
				for (int i = 0; i <= 8; i++)
				{
					if (i == 4)
					{
						continue;
					}
					
					ItemStack itemstack = inv.getStackInSlot(i);
					if (itemstack != null)
					{
						DyeReference dye = DyeReference.getItemDye(itemstack);
						if (dye != null)
						{
							dyes[dyeIndex] = dye;
						}
					}
					
					dyeIndex++;
				}
				
				CustomPaintingData painting = PaintingCatalogue.lookupByDyes(dyes);
				if (painting != null)
				{
					ItemStack result = new ItemStack(Items.painting);
					CustomPaintingReference.setCustomPainting(result, painting.makeReference());
					return result;
				}
			}
		}

		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return new ItemStack(Items.painting);
	}
}
