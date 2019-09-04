package emmaitar.common;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityCustomPainting extends EntityHanging implements IEntityAdditionalSpawnData
{
	private CustomPaintingReference paintingReference;
	private CustomPaintingData paintingData;
	
	private int clientBlockWidth;
	private int clientBlockHeight;
	
    public EntityCustomPainting(World world)
    {
        super(world);
    }

    public EntityCustomPainting(World world, int i, int j, int k, int side, CustomPaintingReference reference)
    {
        super(world, i, j, k, side);
        paintingReference = reference;
        if (!worldObj.isRemote)
        {
        	paintingData = PaintingCatalogue.lookup(paintingReference);
        }
        setDirection(side);
    }
    
	@Override
	public void writeSpawnData(ByteBuf data)
	{
		try
		{
			PacketBuffer buffer = new PacketBuffer(data);
			data.writeInt(field_146063_b);
			data.writeInt(field_146064_c);
			data.writeInt(field_146062_d);
			data.writeByte(paintingData.blockWidth);
			data.writeByte(paintingData.blockHeight);
			data.writeByte(hangingDirection);
			buffer.writeStringToBuffer(paintingReference.identifier);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void readSpawnData(ByteBuf data)
	{
		try
		{
			PacketBuffer buffer = new PacketBuffer(data);
			field_146063_b = data.readInt();
			field_146064_c = data.readInt();
			field_146062_d = data.readInt();
			clientBlockWidth = data.readByte();
			clientBlockHeight = data.readByte();
			setDirection(data.readByte());
			paintingReference = new CustomPaintingReference(buffer.readStringFromBuffer(PaintingCatalogue.STRING_MAX_LENGTH));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
    
    public CustomPaintingData getCustomPaintingData()
    {
    	return paintingData;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
    	nbt.setString("CustomPaintingID", paintingReference.identifier);
    	super.writeEntityToNBT(nbt);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
    	paintingReference = new CustomPaintingReference(nbt.getString("CustomPaintingID"));
    	paintingData = PaintingCatalogue.lookup(paintingReference);
        super.readEntityFromNBT(nbt);
    }

    @Override
    public int getWidthPixels()
    {
    	if (worldObj.isRemote)
    	{
    		return clientBlockWidth * 16;
    	}
        return paintingData.blockWidth * 16;
    }

    @Override
    public int getHeightPixels()
    {
    	if (worldObj.isRemote)
    	{
    		return clientBlockHeight * 16;
    	}
        return paintingData.blockHeight * 16;
    }
    
    @Override
    public void onUpdate()
    {
    	super.onUpdate();
    	
    	if (worldObj.isRemote && paintingData == null)
    	{
    		paintingData = Emmaitar.proxy.lookupClientPainting(paintingReference);
    	}
    }

    @Override
    public void onBroken(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entity;
            if (player.capabilities.isCreativeMode)
            {
                return;
            }
        }

        ItemStack paintingItem = new ItemStack(Items.painting);
        CustomPaintingReference.setCustomPainting(paintingItem, paintingReference);
        entityDropItem(paintingItem, 0F);
    }
}