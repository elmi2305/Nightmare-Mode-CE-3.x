package com.itlesports.nightmaremode.mixin;

import api.item.items.PlaceAsBlockItem;
import btw.block.blocks.SaplingBlock;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlaceAsBlockItem.class)
public abstract class PlaceAsBlockItemMixin {
    @Shadow public abstract int getBlockIDToPlace(World world, int damage, int facing, float clickX, float clickY, float clickZ);

    @Inject(method = "onItemUse", at = @At("RETURN"))
    private void trackSaplingPlanting(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                                      int facing, float clickX, float clickY, float clickZ,
                                      CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || player == null || world.isRemote) {
            return;
        }
        Block placed = Block.blocksList[this.getBlockIDToPlace(world, stack.getItemDamage(), facing, clickX, clickY, clickZ)];
        if (placed == Block.sapling || placed instanceof SaplingBlock) {
            SkillHandler.incrementSaplingsPlanted(player);
        }
    }
}
