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
import net.minecraft.util.Direction;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
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
    
    // Override this from EntityHanging to fix issues if width/height is an EVEN number > 4.
    // (EntityHanging.func_70517_b must be extended to work for all even numbers)
    @Override
    public void setDirection(int dir)
    {
        hangingDirection = dir;

        float sizeX = (float)getWidthPixels();
        float sizeY = (float)getHeightPixels();
        float sizeZ = (float)getWidthPixels();
        if (dir != 2 && dir != 0)
        {
            sizeX = 1F;
            rotationYaw = prevRotationYaw = (float)(dir * 90);
        }
        else
        {
            sizeZ = 1F;
            rotationYaw = prevRotationYaw = (float)(Direction.rotateOpposite[dir] * 90);
        }
        sizeX /= 16F;
        sizeY /= 16F;
        sizeZ /= 16F;
        
        float x = (float)field_146063_b + 0.5F;
        float y = (float)field_146064_c + 0.5F;
        float z = (float)field_146062_d + 0.5F;
        float depth = 0.5625F;

        int blockW = getWidthBlocks();
        int blockH = getHeightBlocks();
        
        if (dir == 2)
        {
            z -= depth;
            x -= getOffset(blockW);
        }
        else if (dir == 1)
        {
            x -= depth;
            z += getOffset(blockW);
        }
        else if (dir == 0)
        {
            z += depth;
            x += getOffset(blockW);
        }
        else if (dir == 3)
        {
            x += depth;
            z -= getOffset(blockW);
        }

        y += getOffset(blockH);
        setPosition(x, y, z);
        
        float halfX = sizeX / 2F;
        float halfY = sizeY / 2F;
        float halfZ = sizeZ / 2F;
        float bbEdge = -0.03125F;
        boundingBox.setBounds(x - halfX - bbEdge, y - halfY - bbEdge, z - halfZ - bbEdge, x + halfX + bbEdge, y + halfY + bbEdge, z + halfZ + bbEdge);
    }

    // Extended version of EntityHanging.func_70517_b
    private float getOffset(int blockSize)
    {
        return (blockSize % 2) == 0 ? 0.5F : 0F;
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
        if (paintingData != null)
        {
        	super.readEntityFromNBT(nbt);
        }
        else
        {
        	setDead();
        	FMLLog.warning("Emmaitar ERROR: Painting %s does not exist! Removing placed painting entity from world", paintingReference.identifier);
        }
    }
    
    private int getWidthBlocks()
    {
    	if (worldObj.isRemote)
    	{
    		return clientBlockWidth;
    	}
    	return paintingData.blockWidth;
    }
    
    private int getHeightBlocks()
    {
    	if (worldObj.isRemote)
    	{
    		return clientBlockHeight;
    	}
        return paintingData.blockHeight;
    }

    @Override
    public final int getWidthPixels()
    {
    	return getWidthBlocks() * 16;
    }

    @Override
    public final int getHeightPixels()
    {
    	return getHeightBlocks() * 16;
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
    
    @Override
    public ItemStack getPickedResult(MovingObjectPosition target)
    {
        ItemStack paintingItem = new ItemStack(Items.painting);
        CustomPaintingReference.setCustomPainting(paintingItem, paintingReference);
        return paintingItem;
    }
}