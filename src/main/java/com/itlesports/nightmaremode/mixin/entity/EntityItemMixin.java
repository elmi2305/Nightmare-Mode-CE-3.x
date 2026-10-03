package com.itlesports.nightmaremode.mixin.entity;

import btw.item.BTWItems;
import btw.entity.item.FloatingItemEntity;
import com.itlesports.nightmaremode.crafting.manager.WashingRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.WashingRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFireproofItems;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import com.itlesports.nightmaremode.world.SandboxRules;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity implements com.itlesports.nightmaremode.util.interfaces.LoadedItemAge {
    @Unique private int loadedItemAge;
    @Override public int nm$getLoadedItemAge() { return this.loadedItemAge; }
    @Override public void nm$setLoadedItemAge(int ticks) { this.loadedItemAge = Math.max(0, Math.min(18000, ticks)); }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void countLoadedItemTime(CallbackInfo ci) {
        if (!this.worldObj.isRemote) ++this.loadedItemAge;
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void saveLoadedItemTime(NBTTagCompound tag, CallbackInfo ci) { tag.setInteger("NmLoadedAge", this.loadedItemAge); }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readLoadedItemTime(NBTTagCompound tag, CallbackInfo ci) {
        this.nm$setLoadedItemAge(tag.hasKey("NmLoadedAge") ? tag.getInteger("NmLoadedAge") : this.age);
    }

    @Inject(method = "combineItems", at = @At("RETURN"))
    private void preserveOldestLoadedTime(EntityItem other, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        com.itlesports.nightmaremode.util.interfaces.LoadedItemAge merged = (com.itlesports.nightmaremode.util.interfaces.LoadedItemAge)other;
        int oldest = Math.max(this.loadedItemAge, merged.nm$getLoadedItemAge());
        this.nm$setLoadedItemAge(oldest);
        merged.nm$setLoadedItemAge(oldest);
    }
    @Unique private int ticksInDesiredFluid;
    @Unique private int ticksNearLava;
    @Shadow public abstract ItemStack getEntityItem();
    @Shadow public abstract void setEntityItemStack(ItemStack stack);
    @Shadow public int age;
    @Unique private boolean nightmareMode$burned;
    @Unique private boolean nightmareMode$pollutionReported;

    public EntityItemMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onCollideWithPlayer", at = @At("HEAD"), cancellable = true)
    private void nightmareMode$rejectSandboxPickup(EntityPlayer player, CallbackInfo ci) {
        if ((player.capabilities.isCreativeMode || SandboxRules.isSandbox(this.worldObj))
                && !SandboxRules.mayCreate(this.worldObj, this.getEntityItem())) ci.cancel();
    }

    @Inject(method = "onCollideWithPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EntityPlayer;onItemPickup(Lnet/minecraft/src/Entity;I)V"))
    private void nightmareMode$recordSurvivalPickup(EntityPlayer player, CallbackInfo ci) {
        SandboxRules.recordAcquisition(player, this.getEntityItem());
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void destroyRecallEntity(CallbackInfo ci) {
        ItemStack stack = this.getEntityItem();
        if (stack == null || stack.itemID < 0 || stack.itemID >= Item.itemsList.length
                || Item.itemsList[stack.itemID] == null) {
            if (!this.worldObj.isRemote) {
                this.worldObj.getWorldLogAgent().logSevere("Discarding item entity " + this.entityId
                        + " with unregistered item ID " + (stack == null ? "null" : stack.itemID));
            }
            this.setDead();
            ci.cancel();
            return;
        }
        if (!this.worldObj.isRemote && com.itlesports.nightmaremode.util.NetherRecall.isRecall(stack)) {
            this.setDead();
            ci.cancel();
        }
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
        if (this.getEntityItem() != null
                && (NMFireproofItems.isIndestructible(this.getEntityItem())
                    || par1DamageSource.isFireDamage() && NMFireproofItems.isFireproof(this.getEntityItem()))
                && par1DamageSource != DamageSource.lava) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void rememberBurningDamage(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        this.nightmareMode$burned |= source == DamageSource.inFire || source == DamageSource.onFire || source == DamageSource.lava;
    }

    @Inject(method = "attackEntityFrom", at = @At("TAIL"))
    private void polluteBurnedItem(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if(this.worldObj != null && NMUtils.isGracePeriodServer(this.worldObj)) return;
        if (this.nightmareMode$burned && this.isDead) this.nightmareMode$reportItemPollution(0.1F);
    }

    @Inject(method = "checkForItemDespawn", at = @At("HEAD"), cancellable = true)
    private void consistentLoadedTimeDespawn(CallbackInfo ci) {
        if (!this.worldObj.isRemote) {
            int id = this.getEntityItem().itemID;
            boolean finiteTorch = id == btw.block.BTWBlocks.finiteBurningTorch.blockID;
            boolean torch = finiteTorch || id == btw.block.BTWBlocks.infiniteBurningTorch.blockID;
            if (torch && this.isInsideOfMaterial(Material.water)
                    || finiteTorch && this.isBeingRainedOn() && this.rand.nextFloat() <= 0.025F) {
                this.worldObj.playAuxSFX(1004, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
                this.setDead();
            } else if (this.loadedItemAge >= 18000) {
                this.nightmareMode$reportItemPollution(this.nightmareMode$burned || this.isBurning() ? 0.02F : 0.2F);
                this.setDead();
            }
        }
        ci.cancel();
    }

    @Unique
    private void nightmareMode$reportItemPollution(float multiplier) {
        if (this.nightmareMode$pollutionReported) return;
        ItemStack stack = this.getEntityItem();
        if (stack == null) return;
        this.nightmareMode$pollutionReported = true;
        ChunkPollutionManager.pollute(this.worldObj, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), stack.stackSize * 5.0F * multiplier);
    }
}
