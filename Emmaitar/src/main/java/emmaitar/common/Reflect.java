package emmaitar.common;

import net.minecraft.entity.*;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class Reflect
{
	public static EntityTrackerEntry getTrackerEntry(WorldServer world, Entity entity)
	{
		EntityTracker tracker = world.getEntityTracker();
		IntHashMap entryMap = ReflectionHelper.getPrivateValue(EntityTracker.class, tracker, "trackedEntityIDs", "field_72794_c");
		return (EntityTrackerEntry)entryMap.lookup(entity.getEntityId());
	}
}
