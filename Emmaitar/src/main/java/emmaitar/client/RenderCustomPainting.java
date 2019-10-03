package emmaitar.client;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import emmaitar.common.CustomPaintingData;
import emmaitar.common.EntityCustomPainting;

@SideOnly(Side.CLIENT)
public class RenderCustomPainting extends Render
{
    private static final ResourceLocation defaultPaintingTexture = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    @Override
    public void doRender(Entity entity, double x, double y, double z, float r, float tick)
    {
    	EntityCustomPainting painting = (EntityCustomPainting)entity;
    	
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(r, 0F, 1F, 0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        float scale = 0.0625F;
        GL11.glScalef(scale, scale, scale);
        renderPainting(painting);
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return defaultPaintingTexture;
    }

    private void renderPainting(EntityCustomPainting painting)
    {
    	CustomPaintingData paintingData = painting.getCustomPaintingData();
    	if (paintingData == null || paintingData.clientTexture == null)
    	{
    		return;
    	}
    	
    	int width = paintingData.blockWidth * 16;
    	int height = paintingData.blockHeight * 16;
    	
        float halfW = (float)(-width) / 2F;
        float halfH = (float)(-height) / 2F;
        float depth = 0.5F;
        
        float backUMin = 192F / 256F;
        float backUMax = 208F / 256F;
        float backVMin = 0F / 256F;
        float backVMax = 16F / 256F;
        
        float f7 = 192F / 256F;
        float f8 = 208F / 256F;
        float f9 = 0.5F / 256F;
        float f10 = 0.5F / 256F;
        float f11 = 193F / 256F;
        float f12 = 193F / 256F;
        float f13 = 0F / 256F;
        float f14 = 16F / 256F;
        
        long lightTotalSky = 0;
       	long lightTotalBlock = 0;
       	int lightCount = 0;
       	int averagedLight = 0;
        
        for (int pass = 0; pass <= 1; pass++)
        {
	        for (int i = 0; i < paintingData.blockWidth; i++)
	        {
	            for (int j = 0; j < paintingData.blockHeight; j++)
	            {
	                float xMin = halfW + (i * 16);
	                float xMax = halfW + ((i + 1) * 16);
	                float yMin = halfH + (j * 16);
	                float yMax = halfH + ((j + 1) * 16);
	                
	                if (pass == 0)
	                {
	                	int light = calcBlockLighting(painting, (xMax + xMin) / 2F, (yMax + yMin) / 2F);
	                	int lightSky = (light >> 20) & 15;
	                	int lightBlock = (light >> 4) & 15;
	                	lightTotalSky += lightSky;
	                	lightTotalBlock += lightBlock;
	                	lightCount++;
	                }
	                else if (pass == 1)
	                {
	    	            int lightX = averagedLight % 65536;
	    	            int lightY = averagedLight / 65536;
	    	            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
	    	            GL11.glColor3f(1F, 1F, 1F);
	    	            
		                float uMin = (float)(paintingData.blockWidth - i - 1) / (float)paintingData.blockWidth;
		                float uMax = (float)(paintingData.blockWidth - i) / (float)paintingData.blockWidth;
		                float vMin = (float)(paintingData.blockHeight - j - 1) / (float)paintingData.blockHeight;
		                float vMax = (float)(paintingData.blockHeight - j) / (float)paintingData.blockHeight;
		                
		                Tessellator tessellator = Tessellator.instance;
		                
		                bindTexture(paintingData.clientTexture);
		                tessellator.startDrawingQuads();
		                tessellator.setNormal(0F, 0F, -1F);
		                tessellator.addVertexWithUV(xMax, yMin, -depth, uMin, vMax);
		                tessellator.addVertexWithUV(xMin, yMin, -depth, uMax, vMax);
		                tessellator.addVertexWithUV(xMin, yMax, -depth, uMax, vMin);
		                tessellator.addVertexWithUV(xMax, yMax, -depth, uMin, vMin);
		                tessellator.draw();
		                
		                bindTexture(defaultPaintingTexture);
		                tessellator.startDrawingQuads();
		                tessellator.setNormal(0F, 0F, 1F);
		                tessellator.addVertexWithUV(xMax, yMax, depth, backUMin, backVMin);
		                tessellator.addVertexWithUV(xMin, yMax, depth, backUMax, backVMin);
		                tessellator.addVertexWithUV(xMin, yMin, depth, backUMax, backVMax);
		                tessellator.addVertexWithUV(xMax, yMin, depth, backUMin, backVMax);
		                tessellator.setNormal(0F, 1F, 0F);
		                tessellator.addVertexWithUV(xMax, yMax, -depth, f7, f9);
		                tessellator.addVertexWithUV(xMin, yMax, -depth, f8, f9);
		                tessellator.addVertexWithUV(xMin, yMax, depth, f8, f10);
		                tessellator.addVertexWithUV(xMax, yMax, depth, f7, f10);
		                tessellator.setNormal(0F, -1F, 0F);
		                tessellator.addVertexWithUV(xMax, yMin, depth, f7, f9);
		                tessellator.addVertexWithUV(xMin, yMin, depth, f8, f9);
		                tessellator.addVertexWithUV(xMin, yMin, -depth, f8, f10);
		                tessellator.addVertexWithUV(xMax, yMin, -depth, f7, f10);
		                tessellator.setNormal(-1F, 0F, 0F);
		                tessellator.addVertexWithUV(xMax, yMax, depth, f12, f13);
		                tessellator.addVertexWithUV(xMax, yMin, depth, f12, f14);
		                tessellator.addVertexWithUV(xMax, yMin, -depth, f11, f14);
		                tessellator.addVertexWithUV(xMax, yMax, -depth, f11, f13);
		                tessellator.setNormal(1F, 0F, 0F);
		                tessellator.addVertexWithUV(xMin, yMax, -depth, f12, f13);
		                tessellator.addVertexWithUV(xMin, yMin, -depth, f12, f14);
		                tessellator.addVertexWithUV(xMin, yMin, depth, f11, f14);
		                tessellator.addVertexWithUV(xMin, yMax, depth, f11, f13);
		                tessellator.draw();
	                }
	            }
	        }
	        
	        if (pass == 0)
	        {
	        	int avgSky = (int)Math.round((double)lightTotalSky / (double)lightCount);
	        	int avgBlock = (int)Math.round((double)lightTotalBlock / (double)lightCount);
	        	averagedLight = (avgSky << 20) | (avgBlock << 4);
	        }
        }
    }

    private int calcBlockLighting(EntityCustomPainting painting, float midX, float midY)
    {
        int i = MathHelper.floor_double(painting.posX);
        int j = MathHelper.floor_double(painting.posY + (midY / 16F));
        int k = MathHelper.floor_double(painting.posZ);

        if (painting.hangingDirection == 2)
        {
            i = MathHelper.floor_double(painting.posX + (midX / 16F));
        }
        else if (painting.hangingDirection == 1)
        {
            k = MathHelper.floor_double(painting.posZ - (midX / 16F));
        }
        else if (painting.hangingDirection == 0)
        {
            i = MathHelper.floor_double(painting.posX - (midX / 16F));
        }
        else if (painting.hangingDirection == 3)
        {
            k = MathHelper.floor_double(painting.posZ + (midX / 16F));
        }

        int light = renderManager.worldObj.getLightBrightnessForSkyBlocks(i, j, k, 0);
        return light;
    }
}
