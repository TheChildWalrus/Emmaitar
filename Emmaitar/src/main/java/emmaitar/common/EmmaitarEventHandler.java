package emmaitar.common;

import java.util.*;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import emmaitar.common.network.EmmaitarPacketHandler;
import emmaitar.common.network.PacketEmmaitarPing;

public class EmmaitarEventHandler
{
	public EmmaitarEventHandler()
	{
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		EntityPlayer player = event.entityPlayer;
		World world = event.world;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		int face = event.face;
		Action action = event.action;
		ItemStack itemstack = player.getCurrentEquippedItem();
		
		if (!world.isRemote)
		{
			if (action == Action.RIGHT_CLICK_BLOCK)
			{
				if (itemstack != null && itemstack.getItem() == Items.painting && CustomPaintingReference.isCustomPaintingItem(itemstack))
				{
					tryPlacePainting(player, itemstack, world, x, y, z, face);
					
					UUID playerID = player.getUniqueID();
					if (playersWithoutMod.contains(playerID))
					{
						IChatComponent msg = new ChatComponentText("This item is an Emmaitar custom painting."
								+ " For these paintings to show up in your game, you must download and install the Emmaitar mod on your client.");
						msg.getChatStyle().setColor(EnumChatFormatting.GOLD);
						player.addChatMessage(msg);
					}
					
					event.setCanceled(true);
					return;
				}
			}
		}
	}

	private boolean tryPlacePainting(EntityPlayer player, ItemStack itemstack, World world, int x, int y, int z, int face)
	{
		if (face == 0)
        {
            return false;
        }
        else if (face == 1)
        {
            return false;
        }
        else
        {
            int dir = Direction.facingToDirection[face];
            CustomPaintingReference reference = CustomPaintingReference.getCustomPainting(itemstack);
            CustomPaintingData paintingData = PaintingCatalogue.lookup(reference);
            EntityCustomPainting painting = new EntityCustomPainting(world, x, y, z, dir, reference);

            if (!player.canPlayerEdit(x, y, z, face, itemstack))
            {
                return false;
            }
            else
            {
            	if (!painting.onValidSurface())
            	{
            		int paintingW = paintingData.blockWidth;
            		int paintingH = paintingData.blockHeight;
            		
            		checkOtherPos:
            		for (int i = -(paintingW - 1); i <= paintingW - 1; i++)
            		{
                		for (int j = -(paintingH - 1); j <= paintingH - 1; j++)
                		{
                			painting.field_146063_b = x;
                			painting.field_146064_c = y + j;
                			painting.field_146062_d = z;
                			if (dir == 0 || dir == 2)
                			{
                				painting.field_146063_b += i;
                			}
                			else if (dir == 1 || dir == 3)
                			{
                				painting.field_146062_d += i;
                			}
                			painting.setDirection(dir);
                			
                			if (painting.onValidSurface())
                			{
                				break checkOtherPos;
                			}
                		}
            		}
            	}
            	
                if (painting.onValidSurface())
                {
                    if (!world.isRemote)
                    {
                    	world.spawnEntityInWorld(painting);
                    }
                    
                    if (!player.capabilities.isCreativeMode)
                    {
                    	itemstack.stackSize--;
                    }
                }

                return true;
            }
        }
	}
	
	private Map<UUID, Integer> playersAwaitingPong = new HashMap();
	private static final int PINGPONG_WAIT = 200;
	private Set<UUID> playersWithoutMod = new HashSet();
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		EntityPlayer player = event.player;
		UUID playerID = player.getUniqueID();
		
		PacketEmmaitarPing pkt = new PacketEmmaitarPing();
		EmmaitarPacketHandler.networkWrapper.sendTo(pkt, (EntityPlayerMP)player);
		playersAwaitingPong.put(playerID, PINGPONG_WAIT);
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
	{
		EntityPlayer player = event.player;
		UUID playerID = player.getUniqueID();
		
		playersAwaitingPong.remove(playerID);
		playersWithoutMod.remove(playerID);
	}
	
	public void receivePong(EntityPlayer player)
	{
		UUID playerID = player.getUniqueID();
		if (playersAwaitingPong.containsKey(playerID))
		{
			playersAwaitingPong.remove(playerID);
		}
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event)
	{
		MinecraftServer server = MinecraftServer.getServer();
		
		if (event.phase == Phase.END)
		{
			Set<UUID> removes = new HashSet();
			
			for (Entry<UUID, Integer> e : playersAwaitingPong.entrySet())
			{
				UUID playerID = e.getKey();
				int tick = e.getValue();
				
				tick--;
				e.setValue(tick);
				if (tick <= 0)
				{
					removes.add(playerID);
					
					playersWithoutMod.add(playerID);
					
					ServerConfigurationManager scm = server.getConfigurationManager();
					EntityPlayer foundPlayer = null;
					for (Object obj : scm.playerEntityList)
					{
						EntityPlayer player = (EntityPlayer)obj;
						if (player.getUniqueID().equals(playerID))
						{
							foundPlayer = player; 
							break;
						}
					}
					
					if (foundPlayer != null)
					{
						IChatComponent msg = new ChatComponentText("This server is running Emmaitar, the craftable custom paintings mod!"
								+ " For these paintings to show up in your game, you must download and install Emmaitar.");
						msg.getChatStyle().setColor(EnumChatFormatting.GOLD);
						foundPlayer.addChatMessage(msg);
					}
				}
			}
			
			for (UUID playerID : removes)
			{
				playersAwaitingPong.remove(playerID);
			}
		}
	}
}
