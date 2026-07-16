package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.item.items.ItemHammer;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.itemblock.ObsidianItemBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow public static Block obsidian;
    @Shadow @Final public int blockID;

    @Shadow protected abstract void dropBlockAsItem_do(World world, int x, int y, int z, ItemStack stack);

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void performObsidianRewrite(CallbackInfo ci){
        Item.itemsList[obsidian.blockID] = new ObsidianItemBlock(obsidian.blockID - 256);
    }

    @Inject(method = "harvestBlock", at = @At("HEAD"), cancellable = true)
    private void applyHammerRecipeDrops(World world, EntityPlayer player, int x, int y, int z, int meta, CallbackInfo ci){
        ItemStack heldStack = player.getHeldItem();
        if (heldStack == null || !(heldStack.getItem() instanceof ItemHammer)) {
            return;
        }

        HammerRecipe recipe = HammerCraftingManager.instance.getRecipe((Block)(Object)this, meta);
        if (recipe == null) {
            return;
        }
        if (!recipe.canPlayerUseHammer(heldStack, player)) {
            world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, meta);
            ci.cancel();
            return;
        }

        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        player.addHarvestBlockExhaustion(this.blockID, x, y, z, meta);

        if (recipe.isFinalHit(meta)) {
            recipe.chargePlayerExperience(player);
            for (ItemStack output : recipe.getOutput()) {
                if (output != null) {
                    this.dropBlockAsItem_do(world, x, y, z, output.copy());
                }
            }
        } else {
            world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, recipe.getNextHitMetadata(meta));
        }

        ci.cancel();
    }
    @Inject(method = "canMobsSpawnOn", at = @At("HEAD"),cancellable = true)
    private void mobSpawnOnWood(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsBloodMoon()){
            cir.setReturnValue(true);
        }
    }
}
