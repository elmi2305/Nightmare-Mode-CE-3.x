package com.itlesports.nightmaremode.mixin;

import btw.entity.item.FloatingItemEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "harvestBlock", at = @At("HEAD"))
    private void additionalDropsForToolHarvested(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6, CallbackInfo ci){
        Block thisObj = (Block)(Object)this;
        if (par2EntityPlayer.getHeldItem() != null) {
            if(par2EntityPlayer.getHeldItem().itemID == Item.pickaxeIron.itemID && par1World.rand.nextInt(7) <= 3){
                if (thisObj.blockID == Block.oreIron.blockID) {
                    // 4/7 chance (57%)
                    par1World.spawnEntityInWorld(new FloatingItemEntity(par1World, par3, par4, par5, new ItemStack(BTWItems.ironOreChunk)));
                } else if(thisObj.blockID == Block.oreGold.blockID){
                    par1World.spawnEntityInWorld(new FloatingItemEntity(par1World, par3, par4, par5, new ItemStack(BTWItems.goldOreChunk)));
                }
            } else if((par2EntityPlayer.getHeldItem().itemID == Item.pickaxeDiamond.itemID || par2EntityPlayer.getHeldItem().itemID == BTWItems.steelPickaxe.itemID) && par1World.rand.nextInt(4) <= 2){
                // 3/4 chance (75%)
                if (thisObj.blockID == Block.oreIron.blockID) {
                    par1World.spawnEntityInWorld(new FloatingItemEntity(par1World, par3, par4, par5, new ItemStack(BTWItems.ironOreChunk)));
                } else if(thisObj.blockID == Block.oreGold.blockID){
                    par1World.spawnEntityInWorld(new FloatingItemEntity(par1World, par3, par4, par5, new ItemStack(BTWItems.goldOreChunk)));
                }
            }
        }
    }
    @Inject(method = "canMobsSpawnOn", at = @At("HEAD"),cancellable = true)
    private void mobSpawnOnWood(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsBloodMoon()){
            cir.setReturnValue(true);
        }
    }
}
