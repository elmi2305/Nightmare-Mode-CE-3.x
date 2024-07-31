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
        EntityAnimal thisObj = (EntityAnimal)(Object)this;
        EntityPlayer player = (EntityPlayer) this.worldObj.findNearestEntityWithinAABB(EntityPlayer.class, this.boundingBox.expand(4.0, 3.0, 4.0), this);
        if((player != null && (!player.isSneaking() || checkNullAndCompareID(player.getHeldItem()))) && !(thisObj instanceof EntityWolf)){
            this.invokeOnNearbyPlayerStartles(player);
        }
    }

    @Unique
    public boolean checkNullAndCompareID(ItemStack par2ItemStack){
        if(par2ItemStack != null){
            switch(par2ItemStack.itemID){
                case 2,11,19,20,23,27,30,22324:
                    return true;
            }
        }
        return false;
    }
}
