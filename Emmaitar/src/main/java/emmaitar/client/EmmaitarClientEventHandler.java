package emmaitar.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import emmaitar.common.*;

public class EmmaitarClientEventHandler
{
	public EmmaitarClientEventHandler()
	{
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.theWorld;
		
		if (event.phase == Phase.END)
		{
			if (world == null)
			{
				ClientPaintingCatalogue.clearAll();
			}
			else
			{
				ClientPaintingCatalogue.onTick();
			}
		}
	}
	
	@SubscribeEvent
	public void getItemTooltip(ItemTooltipEvent event)
	{
		ItemStack itemstack = event.itemStack;
		List<String> tooltip = event.toolTip;
		EntityPlayer entityplayer = event.entityPlayer;
		
		if (itemstack.getItem() == Items.painting && CustomPaintingReference.isCustomPaintingItem(itemstack))
		{
			CustomPaintingReference ref = CustomPaintingReference.getCustomPainting(itemstack);
			CustomPaintingData painting = ClientPaintingCatalogue.lookup(ref, true);
			if (painting != null)
			{
				tooltip.add("");
				tooltip.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("emmaitar.tooltip.header"));
				tooltip.add(EnumChatFormatting.WHITE + "" + EnumChatFormatting.ITALIC + StatCollector.translateToLocalFormatted("emmaitar.tooltip.title", painting.title));
				tooltip.add(StatCollector.translateToLocalFormatted("emmaitar.tooltip.author", painting.authorName));
				tooltip.add(StatCollector.translateToLocalFormatted("emmaitar.tooltip.dimensions", painting.blockWidth, painting.blockHeight));
			}
		}
	}
}
