package emmaitar.common;

import java.util.List;

import net.minecraft.command.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandPaintingGive extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "emmaitar_give";
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "commands.emmaitar_give.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length >= 2)
		{
			EntityPlayerMP player = getPlayer(sender, args[0]);
			String id = args[1];
			CustomPaintingData painting = PaintingCatalogue.lookup(id);
			if (painting != null && painting.paintingIMG != null)
			{
				ItemStack itemstack = new ItemStack(Items.painting);
				CustomPaintingReference.setCustomPainting(itemstack, painting.makeReference());
				
	            EntityItem entityitem = player.dropPlayerItemWithRandomChoice(itemstack, false);
	            entityitem.delayBeforeCanPickup = 0;
	            entityitem.func_145797_a(player.getCommandSenderName());
				func_152373_a(sender, this, "commands.emmaitar_give.success", player.getCommandSenderName(), painting.identifier);
				return;
			}
			else
			{
				throw new WrongUsageException("commands.emmaitar_give.noPainting", id);
			}
		}
		
		throw new WrongUsageException(getCommandUsage(sender));
	}
	
	@Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
        if (args.length == 2)
        {
        	List<String> options = PaintingCatalogue.listAllPaintingIDs();
        	return getListOfStringsMatchingLastWord(args, options.toArray(new String[0]));
        }
        return null;
    }
	
	@Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }
}
