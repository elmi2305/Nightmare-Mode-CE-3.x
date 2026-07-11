package com.itlesports.nightmaremode.mixin.entity;

import btw.item.items.ArcaneScrollItem;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {
    @Shadow public abstract ItemStack getEntityItem();

    public EntityItemMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void doWaterCheck(CallbackInfo ci){
        if(this.getEntityItem() != null && this.getEntityItem().getItem() instanceof NMItem item){
            int id = item.itemID;
            if(id == NMBlocks.blockCrushedIronLayer.blockID){

            }

        }
    }

}
