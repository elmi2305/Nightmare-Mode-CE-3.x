package com.itlesports.nightmaremode.item.items;
/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */

import btw.entity.EmeraldPileEntity;
import net.minecraft.src.*;

import java.util.List;

public class ItemWitchLocator
        extends Item {
    public ItemWitchLocator(int iItemID) {
        super(iItemID);
        this.setBellowsBlowDistance(1);
        this.setFilterableProperties(8);
        this.setUnlocalizedName("nmItemWitchLocator");
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            List<ChunkPosition> positions;
            boolean bHasTarget = false;
            double dTargetXPos = player.posX;
            double dTargetZPos = player.posZ;
            if (world.provider.dimensionId == 0 && (positions = world.findClosestStructureAll("Temple", (int)player.posX, (int)player.posY, (int)player.posZ, 128, 0)) != null && !positions.isEmpty()) {
                bHasTarget = true;
                for (ChunkPosition pos : positions) {
                    dTargetXPos = pos.x * 16;
                    dTargetZPos = pos.z * 16;
                    if(world.getBiomeGenForCoords((int)dTargetXPos,(int)dTargetZPos) != BiomeGenBase.swampland) continue;
                    EmeraldPileEntity sandEntity = new EmeraldPileEntity(world, player.posX, player.posY + 1.7 - (double)player.yOffset, player.posZ);
                    sandEntity.moveTowards(dTargetXPos, dTargetZPos);
                    world.spawnEntityInWorld(sandEntity);
                }
            }
            if (!bHasTarget) {
                EmeraldPileEntity sandEntity = new EmeraldPileEntity(world, player.posX, player.posY + 1.7 - (double)player.yOffset, player.posZ);
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

