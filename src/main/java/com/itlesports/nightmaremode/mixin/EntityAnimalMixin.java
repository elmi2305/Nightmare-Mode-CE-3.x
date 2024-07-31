package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityAnimal.class)
public abstract class EntityAnimalMixin extends EntityAgeable implements EntityAnimalInvoker{
    public EntityAnimalMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageRunningFromPlayer(CallbackInfo ci){
        EntityPlayer player = (EntityPlayer) this.worldObj.findNearestEntityWithinAABB(EntityPlayer.class, this.boundingBox.expand(4.0, 3.0, 4.0), this);
        if(player != null && (!player.isSneaking() || checkNullAndCompareID(player.getHeldItem()))){
            this.invokeOnNearbyPlayerStartles(player);
        }
    }

    @Unique
    private static @NotNull List<Integer> getIllegalItemIDs() {
        List<Integer> illegalItemList = new ArrayList<>();
        illegalItemList.add(Item.swordDiamond.itemID);
        illegalItemList.add(Item.axeDiamond.itemID);
        illegalItemList.add(Item.swordIron.itemID);
        illegalItemList.add(Item.axeIron.itemID);
        illegalItemList.add(Item.swordGold.itemID);
        illegalItemList.add(Item.axeGold.itemID);
        illegalItemList.add(Item.axeStone.itemID);
        illegalItemList.add(BTWItems.boneClub.itemID);
        return illegalItemList;
    }

    @Unique
    public boolean checkNullAndCompareID(ItemStack par2ItemStack){
        if(par2ItemStack != null){
            return getIllegalItemIDs().contains(par2ItemStack.itemID);
        } else return false;
    }
}
