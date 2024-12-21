package com.itlesports.nightmaremode.mixin;

import btw.entity.item.FloatingItemEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "harvestBlock", at = @At("HEAD"))
    private void additionalDropsForToolHarvested(World world, EntityPlayer player, int x, int y, int z, int par6, CallbackInfo ci){
        Block thisObj = (Block)(Object)this;
        ItemStack item = player.getHeldItem();
        if (item != null) {
//            if(item.itemID == Item.shovelStone.itemID){
//                if(thisObj.blockID == Block.blockClay.blockID && world.rand.nextInt(10)==0){
//                    summonEntity(world,x,y,z,Item.clay);
//                }
//            } else if(item.itemID == Item.shovelIron.itemID){
//                if(thisObj.blockID == Block.blockClay.blockID && world.rand.nextInt(6)==0){
//                    summonEntity(world,x,y,z,Item.clay);
//                }
//            }
// TODO: decide if shovels should give double clay lol

            int blockID = thisObj.blockID;

            if(item.itemID == Item.pickaxeIron.itemID && world.rand.nextInt(7) < 4){
                if (blockID == Block.oreIron.blockID) {
                    // 4/7 chance (57%)
                    summonEntity(world,x,y,z,BTWItems.ironOreChunk);
                } else if(blockID == Block.oreGold.blockID){
                    summonEntity(world,x,y,z,BTWItems.goldOreChunk);
                }
            } else if((item.itemID == Item.pickaxeDiamond.itemID || item.itemID == BTWItems.steelPickaxe.itemID || item.itemID == NMItems.bloodPickaxe.itemID) && world.rand.nextInt(4) < 3) {
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
        if(NightmareUtils.getIsBloodMoon()){
            cir.setReturnValue(true);
        }
    }

    @Unique private static void summonEntity(World world, int x, int y, int z, Item item){
        world.spawnEntityInWorld(new FloatingItemEntity(world, x, y, z, new ItemStack(item)));
    }
}
