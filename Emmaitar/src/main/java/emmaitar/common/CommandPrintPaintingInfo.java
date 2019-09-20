package emmaitar.common;

import java.io.IOException;
import java.util.List;

import net.minecraft.command.*;

public class CommandPrintPaintingInfo extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "emmaitar_print";
	}
	
	@Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "commands.emmaitar_print.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			String id = args[0];
			if (id.equals("all"))
			{
				int i = 0;
				for (CustomPaintingData painting : PaintingCatalogue.listAllPaintings())
				{
					printPainting(sender, painting);
					i++;
				}
				func_152373_a(sender, this, "commands.emmaitar_print.successAll", i);
				return;
			}
			else
			{
				CustomPaintingData painting = PaintingCatalogue.lookup(id);
				if (painting != null)
				{
					printPainting(sender, painting);
					func_152373_a(sender, this, "commands.emmaitar_print.success", painting.identifier);
					return;
				}
				else
				{
					throw new WrongUsageException("commands.emmaitar_print.noPainting", id);
				}
			}
		}
		
		throw new WrongUsageException(getCommandUsage(sender));
	}
	
	private void printPainting(ICommandSender sender, CustomPaintingData painting)
	{
		try
		{
			Emmaitar.proxy.printPaintingInfo(painting);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new WrongUsageException(e.getMessage());
		}
	}
	
	@Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
        	List<String> options = PaintingCatalogue.listAllPaintingIDs();
        	options.add("all");
        	return getListOfStringsMatchingLastWord(args, options.toArray(new String[0]));
        }
        return null;
    }
}
