package com.itlesports.nightmaremode.rendering;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import btw.client.render.tileentity.BasketRenderer;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.CustomBasketTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderManager;
import net.minecraft.src.TileEntity;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class CustomBasketRenderer extends BasketRenderer {

    public CustomBasketRenderer() {
    }

    public void renderTileEntityAt(TileEntity tileEntity, double xCoord, double yCoord, double zCoord, float fPartialTickCount) {
        super.renderTileEntityAt(tileEntity, xCoord, yCoord, zCoord, fPartialTickCount);
        CustomBasketTileEntity basket = (CustomBasketTileEntity)tileEntity;
        this.renderStorageStack(basket, xCoord, yCoord, zCoord, fPartialTickCount);
    }

    private void renderStorageStack(CustomBasketTileEntity basket, double xCoord, double yCoord, double zCoord, float fPartialTickCount) {
        ItemStack stack = basket.getStorageStack();
        if (stack != null && basket.lidOpenRatio > 0.01F) {
            int iMetadata = basket.worldObj.getBlockMetadata(basket.xCoord, basket.yCoord, basket.zCoord);
            EntityItem entity = new EntityItem(basket.worldObj, 0.0F, 0.0F, 0.0F, stack);
            entity.hoverStart = 0.0F;
            GL11.glPushMatrix();
            float fCurrentItemHeight = 0.25F * this.getCurrentOpenRatio(basket, fPartialTickCount);
            GL11.glTranslatef((float)xCoord + 0.5F, (float)yCoord + fCurrentItemHeight, (float)zCoord + 0.5F);
            if (RenderManager.instance.options.fancyGraphics) {
                float fYaw = this.convertFacingToYaw(NMBlocks.customWickerBasket.getFacing(iMetadata));
                GL11.glRotatef(fYaw, 0.0F, 1.0F, 0.0F);
            }

            if (this.doesStackRenderAsBlock(stack) && stack.stackSize > 4) {
                GL11.glTranslatef(0.0F, 0.0F, -0.15F);
            }

            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glPopMatrix();
        }

    }

    private float convertFacingToYaw(int iFacing) {
        float fYaw = 0.0F;
        if (iFacing == 3) {
            fYaw = 180.0F;
        } else if (iFacing == 4) {
            fYaw = 90.0F;
        } else if (iFacing == 5) {
            fYaw = 270.0F;
        }

        return fYaw;
    }

    private boolean doesStackRenderAsBlock(ItemStack stack) {
        return stack.getItemSpriteNumber() == 0 && Block.blocksList[stack.itemID] != null && Block.blocksList[stack.itemID].doesItemRenderAsBlock(stack.getItemDamage());
    }
}
