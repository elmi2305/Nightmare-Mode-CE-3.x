package com.itlesports.nightmaremode.mixin.entity;

import btw.item.items.ArcaneScrollItem;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
@Unique
    private static final List<Integer> nonFlammableItems = new ArrayList<>(Arrays.asList(
            NMItems.bloodOrb.itemID,
            Item.netherStar.itemID,
            NMItems.starOfTheBloodGod.itemID,
            Item.blazeRod.itemID,
            Item.blazePowder.itemID,
            Block.obsidian.blockID,
            NMItems.obsidianShard.itemID,
            2317 // this is crude obsidian. I'm too lazy to set up the blocks so that they're loaded before this mixin runs. this is a crude solution, but I'm okay with it
    ));

    @Unique
    private boolean getIsItemBurnable(ItemStack item){
        if(item == null) return false;

        if(nonFlammableItems.contains(item.itemID)) return false;

        if(item.getItem() instanceof ArcaneScrollItem) return false;

        return true;
    }
}
