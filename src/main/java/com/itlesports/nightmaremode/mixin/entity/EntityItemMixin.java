package com.itlesports.nightmaremode.mixin.entity;

import btw.item.items.ArcaneScrollItem;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItemIndestructible;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {
    @Shadow public abstract ItemStack getEntityItem();

    public EntityItemMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void bloodOrbImmunity(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(this.getEntityItem() != null && !this.getIsItemBurnable(this.getEntityItem())){
            cir.setReturnValue(false);
        }
    }
    @Unique private static Set<Integer> nonFlammableItems = null;

    @Unique private Set<Integer> getNonFlammableItems() {
        if(nonFlammableItems != null) return nonFlammableItems;
        nonFlammableItems = new HashSet<>(Arrays.asList(
                NMItems.bloodOrb.itemID,
                Item.netherStar.itemID,
                NMItems.starOfTheBloodGod.itemID,
                Item.blazeRod.itemID,
                Item.blazePowder.itemID,
                Block.obsidian.blockID,
                NMItems.obsidianShard.itemID
        ));
        return nonFlammableItems;
    }

    @Unique
    private boolean getIsItemBurnable(ItemStack item){
        if(item == null) return false;

        if(getNonFlammableItems().contains(item.itemID)) return false;

        if(item.getItem() instanceof ArcaneScrollItem) return false;

        if(item.getItem() instanceof NMItemIndestructible) return false;

        return true;
    }
}
