package com.itlesports.nightmaremode.mixin.entity;

import btw.item.BTWItems;
import btw.entity.item.FloatingItemEntity;
import btw.item.items.ArcaneScrollItem;
import com.itlesports.nightmaremode.crafting.manager.WashingRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.WashingRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {
    @Unique private int ticksInDesiredFluid;
    @Unique private int ticksNearLava;
    @Shadow public abstract ItemStack getEntityItem();
    @Shadow public abstract void setEntityItemStack(ItemStack stack);

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

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void fireNetherBricksBesideLava(CallbackInfo ci) {
        if (this.worldObj.isRemote) {
            return;
        }
        ItemStack stack = this.getEntityItem();
        if (stack == null || stack.itemID != BTWItems.unfiredNetherBrick.itemID || !this.hasHorizontalLavaNeighbor()) {
            this.ticksNearLava = 0;
            return;
        }
        if (++this.ticksNearLava < 200) {
            return;
        }
        this.setEntityItemStack(new ItemStack(BTWItems.netherBrick, stack.stackSize));
        this.worldObj.playSoundAtEntity(this, "random.fizz", 0.5F, 1.0F);
        this.ticksNearLava = 0;
    }

    @Unique
    private boolean hasHorizontalLavaNeighbor() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        return this.isLava(x - 1, y, z) || this.isLava(x + 1, y, z)
                || this.isLava(x, y, z - 1) || this.isLava(x, y, z + 1);
    }

    @Unique
    private boolean isLava(int x, int y, int z) {
        int id = this.worldObj.getBlockId(x, y, z);
        return id == Block.lavaStill.blockID || id == Block.lavaMoving.blockID;
    }


    @Inject(method = "attackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void bloodOrbImmunity(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(this.getEntityItem() != null && !this.isItemIndestructible(this.getEntityItem())){
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
    private boolean isItemIndestructible(ItemStack item){
        if(item == null) return false;

        if(getNonFlammableItems().contains(item.itemID)) return false;

        if(item.getItem() instanceof ArcaneScrollItem) return false;

        if(item.getItem() instanceof INetherItem) return false;

        if(item.getItem() instanceof NMItem && ((NMItem) item.getItem()).isIndestructible()) return false;

        return true;
    }
}
