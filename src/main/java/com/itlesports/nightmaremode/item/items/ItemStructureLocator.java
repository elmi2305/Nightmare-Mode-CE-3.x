package com.itlesports.nightmaremode.item.items;
/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */

import btw.entity.LocatorPileEntity;
import net.minecraft.src.*;

import java.util.List;

public class ItemStructureLocator
        extends Item {
    private final int color;
    private final boolean shouldLocateSwamps;
    public ItemStructureLocator(int iItemID, boolean swamp, int color) {
        super(iItemID);
        this.setItemRightClickCooldown(120L);
        this.shouldLocateSwamps = swamp;
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.color = color;
    }

    public String getModId() {
        return "nightmare_mode";
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            List<ChunkPosition> positions;
            boolean bHasTarget = false;
            double dTargetXPos = player.posX;
            double dTargetZPos = player.posZ;
            if (world.provider.dimensionId == 0 && (positions = world.findClosestStructureAll("Temple", (int)player.posX, (int)player.posY, (int)player.posZ, 196, 0)) != null && !positions.isEmpty()) {
                bHasTarget = true;
                for (ChunkPosition pos : positions) {
                    dTargetXPos = pos.x * 16;
                    dTargetZPos = pos.z * 16;
                    if(world.getBiomeGenForCoords((int)dTargetXPos,(int)dTargetZPos) != (this.shouldLocateSwamps ? BiomeGenBase.swampland : BiomeGenBase.desert)) continue;
                    LocatorPileEntity sandEntity = new LocatorPileEntity(world, player.posX, player.posY + 1.7 - (double)player.yOffset, player.posZ, color);
                    sandEntity.moveTowards(dTargetXPos, dTargetZPos);
                    world.spawnEntityInWorld(sandEntity);
                }
            }
            if (!bHasTarget) {
                LocatorPileEntity sandEntity = new LocatorPileEntity(world, player.posX, player.posY + 1.7 - (double)player.yOffset, player.posZ, color);
                sandEntity.moveTowards(dTargetXPos, dTargetZPos);
                world.spawnEntityInWorld(sandEntity);
            }
            if (bHasTarget) {
                world.playAuxSFX(2286, (int)Math.round(player.posX), (int)Math.round(player.posY + 1.7 - (double)player.yOffset), (int)Math.round(player.posZ), 0);
            }
            if (!player.capabilities.isCreativeMode) {
                --stack.stackSize;
            }
        }
        return stack;
    }
}

