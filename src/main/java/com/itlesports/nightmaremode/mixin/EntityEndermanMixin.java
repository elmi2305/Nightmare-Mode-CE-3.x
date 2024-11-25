package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityEnderman.class)
public abstract class EntityEndermanMixin extends EntityMob {

    public EntityEndermanMixin(World par1World) {
        super(par1World);
    }
    @Shadow protected abstract void angerNearbyEndermen(EntityPlayer targetPlayer);
    @Shadow private int teleportDelay;
    @Shadow protected abstract boolean teleportToEntity(Entity par1Entity);

    @Unique int patience = 30;

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        int progress = NightmareUtils.getWorldProgress(this.worldObj);
        double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.5 : 1;
        boolean isBloodMoon = bloodMoonModifier > 1;

        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((progress > 0 ? 0.38f : 0.35f) + (isBloodMoon ? 0.04 : 0));
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0 + progress * 2 + bloodMoonModifier);
        // 7 -> 9 -> 11 -> 13
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(40 + progress*20);
        // 40 -> 60 -> 80 -> 100
    }


    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(intValue = 4))
    private int increaseAttemptsToTeleportPlayer(int constant){
        if(this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            if (NightmareUtils.getIsBloodMoon()) {
                return 9;
            }
            return 6;
        }
        return constant;
    }

    @Inject(method = "findPlayerToAttack", at = @At("TAIL"),locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void hostileInEnd(CallbackInfoReturnable<Entity> cir, EntityPlayer target){
        if (target != null){
            ItemStack var2 = target.inventory.armorInventory[3];
            if (target.dimension == 1 && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                if (var2 == null) {
                    this.angerNearbyEndermen(target);
                    cir.setReturnValue(target);
                } else if(var2.itemID != BTWItems.enderSpectacles.itemID){
                    this.angerNearbyEndermen(target);
                    cir.setReturnValue(target);
                }
            } else {
                if(var2 != null && var2.itemID == 86){ // carved pumpkin aggros all endermen
                    this.angerNearbyEndermen(target);
                    cir.setReturnValue(target);
                }
            }
        }
    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 256))
    private double endermanRangeToTeleport(double constant){
        return 128;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderman;setAIMoveSpeed(F)V"))
    private void teleportToNearestPlayerOnAggro(CallbackInfo ci){
        if(this.entityToAttack instanceof EntityPlayer){
            this.teleportToEntity(this.entityToAttack);
        }
    }

    @Inject(method = "onLivingUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityEnderman;entityMobOnLivingUpdate()V"))
    private void updateEnemyTeleport(CallbackInfo ci) {
        if (!this.worldObj.isRemote && this.isEntityAlive() && this.entityToAttack != null) {
            if (entityToAttack.getDistanceSqToEntity(this) > 6.0D && entityToAttack.getDistanceSqToEntity(this) < 64D) {
                if (this.teleportDelay++ >= 120 && this.teleportEnemy()) {
                    this.teleportDelay = 0;
                }
            } else if (entityToAttack.getDistanceSqToEntity(this) < 256D) {
                this.teleportDelay = 0;
            }
        }
    }

    @Inject(method = "findPlayerToAttack", at = @At("TAIL"), cancellable = true)
    private void attackClosePlayers(CallbackInfoReturnable<Entity> cir){
        EntityPlayer target = null;
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            target = this.worldObj.getClosestVulnerablePlayerToEntity(this, 3.5);
        }
        EntityPlayer effectTarget = this.worldObj.getClosestVulnerablePlayerToEntity(this, 7);
        if(effectTarget != null && this.dimension != 1){
            effectTarget.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60,0));
            effectTarget.addPotionEffect(new PotionEffect(Potion.weakness.id, 60,0));
            effectTarget.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 60,0));
            effectTarget.addPotionEffect(new PotionEffect(Potion.blindness.id, 60,0));
            if (target != null) {
                this.patience--;
                if(this.patience <= 0) {
                    this.patience = 0;
                    this.angerNearbyEndermen(target);
                    cir.setReturnValue(target);
                }
            } else{
                this.patience = Math.min(++this.patience,30);
            }
        }
    }
    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean returnFalse(World instance){
        return false; // this just makes endermen not lose aggro in the day.
    }


    @Unique
    protected boolean teleportEnemy() {
        if (this.entityToAttack != null) {
            Entity target = this.entityToAttack;
            Vec3 vec = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX - target.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - target.posY + (double)target.getEyeHeight(), this.posZ - target.posZ);
            vec = vec.normalize();
            double x0 = target.posX;
            double y0 = target.posY;
            double z0 = target.posZ;
            target.posX = x0 + (this.rand.nextDouble() - 0.5D) * 8.0D + vec.xCoord * 0.8D;
            target.posY = y0 + (double)(this.rand.nextInt(16) - 8) + vec.yCoord * 0.8D;
            target.posZ = z0 + (this.rand.nextDouble() - 0.5D) * 8.0D + vec.zCoord * 0.8D;
            int xb = MathHelper.floor_double(target.posX);
            int yb = MathHelper.floor_double(target.posY);
            int zb = MathHelper.floor_double(target.posZ);
            int blockId;

            if (this.worldObj.blockExists(xb, yb, zb)) {
                boolean canTeleport = false;

                while (!canTeleport && yb > 0) {
                    blockId = this.worldObj.getBlockId(xb, yb, zb);
                    if (blockId != 0 && Block.blocksList[blockId].blockMaterial.blocksMovement()) {
                        canTeleport = true;
                        for (int i = 1; i <= MathHelper.ceiling_float_int(target.height); i++) {
                            blockId = this.worldObj.getBlockId(xb, yb + i, zb);
                            if (blockId != 0 && Block.blocksList[blockId].blockMaterial.blocksMovement()) {
                                canTeleport = false;
                                target.posY -= MathHelper.ceiling_float_int(target.height) + 1;
                                yb -= MathHelper.ceiling_float_int(target.height) + 1;
                                break;
                            }
                        }
                        ++target.posY;
                    }
                    else {
                        --target.posY;
                        --yb;
                    }
                }

                if (canTeleport) {
                    if (target instanceof EntityPlayer) {
                        ((EntityPlayer) target).setPositionAndUpdate(target.posX, target.posY, target.posZ);
                    } else {
                        target.setPosition(target.posX, target.posY, target.posZ);
                    }

                    short var30 = 128;
                    for (blockId = 0; blockId < var30; ++blockId) {
                        double var19 = (double)blockId / ((double)var30 - 1.0D);
                        float var21 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                        float var22 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                        float var23 = (this.rand.nextFloat() - 0.5F) * 0.2F;
                        double var24 = x0 + (target.posX - x0) * var19 + (this.rand.nextDouble() - 0.5D) * (double)target.width * 2.0D;
                        double var26 = y0 + (target.posY - y0) * var19 + this.rand.nextDouble() * (double)target.height;
                        double var28 = z0 + (target.posZ - z0) * var19 + (this.rand.nextDouble() - 0.5D) * (double)target.width * 2.0D;
                        this.worldObj.spawnParticle("portal", var24, var26, var28, var21, var22, var23);
                    }
                    this.worldObj.playSoundEffect(x0, y0, z0, "mob.endermen.portal", 1.0F, 1.0F);
                    target.playSound("mob.endermen.portal", 1.0F, 1.0F);

                    return true;
                } else {
                    target.setPosition(x0, y0, z0);
                }
            }
        }
        return false;
    }
}
