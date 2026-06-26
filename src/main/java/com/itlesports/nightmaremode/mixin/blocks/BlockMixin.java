package com.itlesports.nightmaremode.mixin.blocks;

import api.item.items.PickaxeItem;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.item.FloatingItemEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.itemblock.ObsidianItemBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow public static Block obsidian;
    @Shadow @Final public int blockID;

    @Shadow public abstract boolean doesBlockDropAsItemOnSaw(World world, int i, int j, int k);
    @Shadow public abstract void dropBlockAsItem(World par1World, int par2, int par3, int par4, int par5, int par6);

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void performObsidianRewrite(CallbackInfo ci){
        Item.itemsList[obsidian.blockID] = new ObsidianItemBlock(obsidian.blockID - 256);
    }
//    @Inject(method = "onBlockSawed(Lnet/minecraft/src/World;III)Z", at = @At("HEAD"), cancellable = true)
//    private void addBloodSawRecipeHook(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
//        int metadata = world.getBlockMetadata(i, j, k);
//        SawRecipe recipe = BloodSawCraftingManager.instance.getRecipe((Block)(Object)(this), metadata);
//        if (recipe != null) {
//            for (ItemStack stack : recipe.getOutput()) {
//                ItemUtils.ejectStackWithRandomOffset(world, i, j, k, stack.copy());
//            }
//            world.setBlockToAir(i, j, k);
//            cir.setReturnValue(true);
//            return;
//
//        } else {
//            if (!this.doesBlockDropAsItemOnSaw(world, i, j, k)) {
//                cir.setReturnValue(false);
//                return;
//            }
//            this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
//            world.setBlockToAir(i, j, k);
//            cir.setReturnValue(true);
//            return;
//        }
//    }


    @Inject(method = "harvestBlock", at = @At("HEAD"))
    private void explodeRandomlyOnBlockBreak(World world, EntityPlayer player, int x, int y, int z, int par6, CallbackInfo ci){
        if(NightmareMode.isAprilFools){
            if(world.rand.nextInt(8) == 0){
                world.newExplosion(null,x, y, z, world.rand.nextFloat() + 1.5f, false, true);
            }
        }
    }

    @Inject(method = "harvestBlock", at = @At("HEAD"))
    private void additionalDropsForToolHarvested(World world, EntityPlayer player, int x, int y, int z, int par6, CallbackInfo ci){
        ItemStack item = player.getHeldItem();
        if (item != null && item.getItem() instanceof PickaxeItem pi) {
            int blockID = this.blockID;

            if(EnchantmentHelper.getSilkTouchModifier(player)){return;}

            if(pi.toolMaterial.getHarvestLevel() == 2 && world.rand.nextInt(7) < 4){
                if (blockID == Block.oreIron.blockID) {
                    // 4/7 chance (57%)
                    summonEntity(world,x,y,z,BTWItems.ironOreChunk);
                } else if(blockID == Block.oreGold.blockID){
                    summonEntity(world,x,y,z,BTWItems.goldOreChunk);
                }
            } else if((pi.toolMaterial.getHarvestLevel() >= 3) && world.rand.nextInt(4) != 0) {
                // 3/4 chance (75%)
                if (blockID == Block.oreIron.blockID) {
                    summonEntity(world, x, y, z, BTWItems.ironOreChunk);
                } else if (blockID == Block.oreGold.blockID) {
                    summonEntity(world, x, y, z, BTWItems.goldOreChunk);
                }
            }
        }
    }
    @Inject(method = "canMobsSpawnOn", at = @At("HEAD"),cancellable = true)
    private void mobSpawnOnWood(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsBloodMoon()){
            cir.setReturnValue(true);
        }
    }

    @Unique private static void summonEntity(World world, int x, int y, int z, Item item){
        world.spawnEntityInWorld(new FloatingItemEntity(world, x, y, z, new ItemStack(item)));
    }
}
