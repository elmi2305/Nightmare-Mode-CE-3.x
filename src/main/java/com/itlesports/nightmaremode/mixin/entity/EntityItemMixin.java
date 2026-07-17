package com.itlesports.nightmaremode.mixin.entity;

import btw.entity.item.FloatingItemEntity;
import com.itlesports.nightmaremode.crafting.manager.WashingRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.WashingRecipe;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {
    @Unique private int ticksInDesiredFluid;
    @Shadow public abstract ItemStack getEntityItem();

    public EntityItemMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void doWaterCheck(CallbackInfo ci) {
        if (this.worldObj.isRemote) {
            return;
        }

        ItemStack input = this.getEntityItem();
        WashingRecipe recipe = WashingRecipeManager.instance.getWaterRecipe(input);
        if (recipe == null || !this.isInsideOfMaterial(Material.water)) {
            this.ticksInDesiredFluid = 0;
            return;
        }

        if (++this.ticksInDesiredFluid < recipe.getDuration()) {
            return;
        }

        ItemStack required = recipe.getInput();
        int batches = input.stackSize / required.stackSize;
        ItemStack output = recipe.getOutput();
        output.stackSize *= batches;
        this.worldObj.spawnEntityInWorld(
                new FloatingItemEntity(this.worldObj, this.posX, this.posY, this.posZ, output));
        this.worldObj.playAuxSFX(2222,
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posY),
                MathHelper.floor_double(this.posZ),
                0);

        input.stackSize -= required.stackSize * batches;
        this.ticksInDesiredFluid = 0;
        if (input.stackSize <= 0) {
            this.setDead();
        }
    }
}
