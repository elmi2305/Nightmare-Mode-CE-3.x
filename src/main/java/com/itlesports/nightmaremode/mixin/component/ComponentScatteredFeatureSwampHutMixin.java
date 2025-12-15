package com.itlesports.nightmaremode.mixin.component;

import btw.block.BTWBlocks;
import btw.block.tileentity.WickerBasketTileEntity;
import btw.item.util.RandomItemStack;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.NMBlock;
import com.itlesports.nightmaremode.block.tileEntities.CustomBasketTileEntity;
import net.minecraft.src.ComponentScatteredFeature;
import net.minecraft.src.ComponentScatteredFeatureSwampHut;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComponentScatteredFeatureSwampHut.class)
public abstract class ComponentScatteredFeatureSwampHutMixin extends ComponentScatteredFeature {
    @Shadow private static RandomItemStack[] lootBasketContents;
    @Shadow protected abstract void initContentsArray();
    @Shadow private boolean hasLootBasket;

    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentScatteredFeatureSwampHut;addLootBasket(Lnet/minecraft/src/World;Lnet/minecraft/src/StructureBoundingBox;III)V"))
    private void addNightmareLootBasket(ComponentScatteredFeatureSwampHut instance, World world, StructureBoundingBox AABB, int iRelX, int iRelY, int iRelZ){
        this.addLootBasketNightmare(world, AABB, iRelX, iRelY, iRelZ);
    }


    @Unique
    private void addLootBasketNightmare(World world, StructureBoundingBox boundingBox, int iRelX, int iRelY, int iRelZ) {
        // this method is a copy of the BTW one, but it places a NM loot basket instead of a BTW one
        int k;
        int j;
        int i;
        if (lootBasketContents == null) {
            this.initContentsArray();
        }
        if (boundingBox.isVecInside(i = this.getXWithOffset(iRelX, iRelZ), j = this.getYWithOffset(iRelY), k = this.getZWithOffset(iRelX, iRelZ)) && world.getBlockId(i, j, k) != NMBlocks.customWickerBasket.blockID) {
            this.hasLootBasket = true;
            world.setBlock(i, j, k, NMBlocks.customWickerBasket.blockID, world.rand.nextInt(4) | 4, 2);
            CustomBasketTileEntity tileEntity = (CustomBasketTileEntity)world.getBlockTileEntity(i, j, k);
            if (tileEntity != null) {
                tileEntity.setStorageStack(RandomItemStack.getRandomStack(world.rand, lootBasketContents));
            }
        }
    }
}
