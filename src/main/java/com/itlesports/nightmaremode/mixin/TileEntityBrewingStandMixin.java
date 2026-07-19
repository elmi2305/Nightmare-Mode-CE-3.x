package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityBrewingStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityBrewingStand.class)
public class TileEntityBrewingStandMixin {
    @Shadow private ItemStack[] brewingItemStacks;

    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 400))
    private int reduceBrewTime(int constant){
        TileEntity tile = (TileEntity)(Object)this;
        EntityPlayer player = tile.worldObj == null ? null : tile.worldObj.getClosestPlayer(
                tile.xCoord + 0.5D, tile.yCoord + 0.5D, tile.zCoord + 0.5D, 8.0D);
        float speedBonus = player == null ? 0.0F : SkillHandler.getPlayerData(player).brewingSpeedBonus;
        return Math.max(1, Math.round(12000.0F / (1.0F + speedBonus)));
    }

    @Inject(method = "brewPotions", at = @At("TAIL"))
    private void trackSkillPotionBrewing(CallbackInfo ci) {
        TileEntity tile = (TileEntity)(Object)this;
        if (tile.worldObj == null || tile.worldObj.isRemote) {
            return;
        }

        int brewed = 0;
        for (int i = 0; i < 3; ++i) {
            ItemStack stack = this.brewingItemStacks[i];
            if (stack != null && stack.itemID == Item.potion.itemID) {
                brewed++;
            }
        }
        if (brewed <= 0) {
            return;
        }

        EntityPlayer player = tile.worldObj.getClosestPlayer(
                (double)tile.xCoord + 0.5D,
                (double)tile.yCoord + 0.5D,
                (double)tile.zCoord + 0.5D,
                8.0D
        );
        SkillHandler.incrementPotionsBrewed(player, brewed);
    }
}
