package com.itlesports.nightmaremode.rendering;


import com.itlesports.nightmaremode.block.tileEntities.TileEntityDisenchantmentTable;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class RenderDisenchantmentTable extends TileEntitySpecialRenderer
{
    private static final ResourceLocation enchantingTableBookTextures = new ResourceLocation("textures/entity/disenchanterBook.png");
    private ModelBook enchantmentBook = new ModelBook();

    public void renderTileEntityEnchantmentTableAt(TileEntityDisenchantmentTable te, double x, double y, double z, float partialTick)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
        float var9 = (float)te.tickCount + partialTick;
        GL11.glTranslatef(0.0F, 0.1F + MathHelper.sin(var9 * 0.1F) * 0.01F, 0.0F);
        float var10;

        for (var10 = te.bookRotation2 - te.bookRotationPrev; var10 >= (float)Math.PI; var10 -= ((float)Math.PI * 2F))
        {
            ;
        }

        while (var10 < -(float)Math.PI)
        {
            var10 += ((float)Math.PI * 2F);
        }

        float var11 = te.bookRotationPrev + var10 * partialTick;
        GL11.glRotatef(-var11 * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(enchantingTableBookTextures);
        float var12 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTick + 0.25F;
        float var13 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTick + 0.75F;
        var12 = (var12 - (float)MathHelper.truncateDoubleToInt((double)var12)) * 1.6F - 0.3F;
        var13 = (var13 - (float)MathHelper.truncateDoubleToInt((double)var13)) * 1.6F - 0.3F;

        if (var12 < 0.0F)
        {
            var12 = 0.0F;
        }

        if (var13 < 0.0F)
        {
            var13 = 0.0F;
        }

        if (var12 > 1.0F)
        {
            var12 = 1.0F;
        }

        if (var13 > 1.0F)
        {
            var13 = 1.0F;
        }

        float var14 = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTick;
        GL11.glEnable(GL11.GL_CULL_FACE);
        this.enchantmentBook.render((Entity)null, var9, var12, var13, var14, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderTileEntityEnchantmentTableAt((TileEntityDisenchantmentTable)par1TileEntity, par2, par4, par6, par8);
    }
}
