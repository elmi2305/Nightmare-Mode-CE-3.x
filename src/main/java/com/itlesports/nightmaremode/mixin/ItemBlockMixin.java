package com.itlesports.nightmaremode.mixin;

import btw.item.items.PlaceAsBlockItem;
import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlock.class)
public class ItemBlockMixin extends PlaceAsBlockItem {

    public ItemBlockMixin(int iItemID, int iBlockID) {
        super(iItemID, iBlockID);
    }

    @Override
    public int getMetadata(int iItemDamage) {
        if(this.itemID == Block.obsidian.blockID){
            return iItemDamage;
        }
        return super.getMetadata(iItemDamage);
    }


    @Inject(method = "getUnlocalizedName(Lnet/minecraft/src/ItemStack;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void manageObsidian(ItemStack stack, CallbackInfoReturnable<String> cir){
        if (stack.itemID == Block.obsidian.blockID) {
            int meta = stack.getItemDamage();
            if(meta == 0){
                cir.setReturnValue("tile.obsidian");
            } else{
                cir.setReturnValue("tile.nmCrudeObsidian");
            }
        }
    }
}
