package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NMDifficultyParam;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.entity.EntityCreeperGhast;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityGhast.class)
public abstract class EntityGhastMixin extends EntityFlying{
    @Shadow public abstract boolean getCanSpawnHereNoPlayerDistanceRestrictions();

    @Shadow private Entity entityTargeted;
    @Unique int rageTimer = 0;
    @Unique boolean firstAttack = true;

    public EntityGhastMixin(World world) {
        super(world);
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        int progress = NMUtils.getWorldProgress();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((20 + 8 * progress) * NMUtils.getNiteMultiplier());
        // 20 -> 28 -> 36 -> 44
    }

    @ModifyConstant(method = "fireAtTarget", constant = @Constant(intValue = 1))
    private int increaseExplosionSize(int constant){
        if(NMUtils.getIsBloodMoon()){
            return constant + 2;
        }
        return constant + 1;
    }

    @ModifyArg(method = "updateAttackStateClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getClosestVulnerablePlayerToEntity(Lnet/minecraft/src/Entity;D)Lnet/minecraft/src/EntityPlayer;"), index = 1)
    private double increaseHordeAttackRange(double par2){
        return (NightmareMode.hordeMode || this.isTargetPlayerAndPumpkin(this.entityTargeted) || (NMUtils.getIsBloodMoon() && this.dimension == -1)) ? 140d : par2;
    }
    @Unique private boolean isTargetPlayerAndPumpkin(Entity target){
        if(target instanceof EntityPlayer p){
            ItemStack headStack = p.getCurrentItemOrArmor(4);
            return headStack != null && headStack.getItem().itemID == BTWBlocks.carvedPumpkin.blockID;
        }
        return false;
    }
    @ModifyConstant(method = "updateAttackStateClient", constant = @Constant(doubleValue = 4096.0F))
    private double increaseDetectionRangeHorde(double constant){
        return (float) ((NightmareMode.hordeMode || this.isTargetPlayerAndPumpkin(this.entityTargeted) || (NMUtils.getIsBloodMoon() && this.dimension == -1)) ? constant * 4d : constant);
    }
    @Redirect(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityGhast;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean allowShootingThroughWallsBloodMoon(EntityGhast instance, Entity entity){
        if(this.isTargetPlayerAndPumpkin(this.entityTargeted) ||(NMUtils.getIsBloodMoon() && instance.dimension == -1)){return true;}
        return instance.canEntityBeSeen(entity);
    }
    @Redirect(method = "updateAttackStateClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityGhast;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean seePlayerThroughWallsHorde(EntityGhast instance, Entity entity){
        return NightmareMode.hordeMode || this.isTargetPlayerAndPumpkin(this.entityTargeted) || (NMUtils.getIsBloodMoon() && this.dimension == -1) || instance.canEntityBeSeen(entity);
    }

    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData par1EntityLivingData) {
        return super.onSpawnWithEgg(par1EntityLivingData);
    }

    @Unique private boolean isValidForEventLoot = false;
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void storeLastHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
    }

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if (bKilledByPlayer && isValidForEventLoot) {
            int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
            if (bloodOrbID > 0) {
                int var4 = this.rand.nextInt(3)+1;
                // 1 - 3
                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    this.dropItem(bloodOrbID, 1);
                }
            }

            if(this.isCreeperVariant()){
                int itemID = NMItems.creeperTear.itemID;

                int var4 = this.rand.nextInt(3);

                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }

                for (int var5 = 0; var5 < var4; ++var5) {
                    if(this.rand.nextInt(3) == 0) continue;
                    this.dropItem(itemID, 1);
                }
            }
            else if (NMUtils.getIsMobEclipsed(this)) {
                for(int i = 0; i < (iLootingModifier * 2) + 1; i++) {
                    if (this.rand.nextInt(8) == 0) {
                        this.dropItem(NMItems.darksunFragment.itemID, 1);
                        if (this.rand.nextBoolean()) {
                            break;
                        }
                    }
                }

                int itemID = NMItems.ghastTentacle.itemID;

                int var4 = this.rand.nextInt(6);
                if(this.dimension == -1){
                    var4 += 4;
                }
                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    if(this.rand.nextBoolean()) continue;
                    this.dropItem(itemID, 1);
                }
            }

        }
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 1000.0f))
    private float ghastEnrageOnSelfHit(float constant){
        this.rageTimer = 1;
        return 5f;
    }
    @Inject(method = "updateEntityActionState", at =@At("HEAD"))
    private void manageRageState(CallbackInfo ci){
        if(this.rageTimer > 0){
            this.rageTimer++;
            EntityPlayer entityTargeted = this.worldObj.getClosestVulnerablePlayerToEntity(this, 100.0);
            if (entityTargeted != null && this.rageTimer % (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 4 : 8) == 0) {
                Vec3 ghastLookVec = this.getLook(1.0f);
                double dFireballX = this.posX + ghastLookVec.xCoord;
                double dFireballY = this.posY + (double)(this.height / 2.0f);
                double dFireballZ = this.posZ + ghastLookVec.zCoord;
                double dDeltaX = entityTargeted.posX - dFireballX;
                double dDeltaY = entityTargeted.posY + (double)entityTargeted.getEyeHeight() - dFireballY;
                double dDeltaZ = entityTargeted.posZ - dFireballZ;
                EntityLargeFireball fireball = new EntityLargeFireball(this.worldObj, this, dDeltaX, dDeltaY, dDeltaZ);
                fireball.field_92057_e = 1;
                double dDeltaLength = MathHelper.sqrt_double(dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ);
                double dUnitDeltaX = dDeltaX / dDeltaLength;
                double dUnitDeltaY = dDeltaY / dDeltaLength;
                double dUnitDeltaZ = dDeltaZ / dDeltaLength;
                fireball.posX = dFireballX + dUnitDeltaX * 4.0;
                fireball.posY = dFireballY + dUnitDeltaY * 4.0 - (double)fireball.height / 2.0;
                fireball.posZ = dFireballZ + dUnitDeltaZ * 4.0;
                this.worldObj.spawnEntityInWorld(fireball);
            }
        }
        if(this.rageTimer > 100){
            this.rageTimer = 0;
        }
    }

    @Inject(method = "updateEntityActionState", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityGhast;attackCounter:I",ordinal = 2),cancellable = true)
    private void manageCreeperEclipseVariant(CallbackInfo ci){
        if(NMUtils.getIsMobEclipsed(this) && this.isCreeperVariant()){
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 40,0));
            if(this.firstAttack){
                this.worldObj.playSoundEffect(this.entityTargeted.posX,this.entityTargeted.posY,this.entityTargeted.posZ, "mob.ghast.scream",1f,0.8f);
                this.firstAttack = false;
            }

            // Get the positions of the entity and the player
            Vec3 playerPos = Vec3.createVectorHelper(this.entityTargeted.posX,this.entityTargeted.posY,this.entityTargeted.posZ);
            Vec3 entityPos = Vec3.createVectorHelper(this.posX,this.posY,this.posZ);
            // Calculate the direction vector
            Vec3 velocity = entityPos.subtract(playerPos);
            velocity.normalize();

            // Calculate the velocity vector
            velocity.scale(0.1d);
            if (this.getDistanceSqToEntity(this.entityTargeted) > 4) {
                if (this.hurtResistantTime <= 8) {
                    this.motionX = velocity.xCoord;
                    this.motionY = velocity.yCoord;
                    this.motionZ = velocity.zCoord;
                }
            } else{
                this.worldObj.newExplosion(this,this.posX,this.posY,this.posZ,6f, false, true);
                this.setDead();
            }
            ci.cancel();
        }
    }

    @Unique private boolean isCreeperVariant(){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        return thisObj instanceof EntityCreeperGhast;
    }


    @Inject(method = "getCanSpawnHere", at = @At("HEAD"),cancellable = true)
    private void manageOverworldSpawn(CallbackInfoReturnable<Boolean> cir){
        if(this.dimension == 0){
            if (NMUtils.getIsBloodMoon() || NMUtils.getIsMobEclipsed(this)) {
                if (this.getCanSpawnHereNoPlayerDistanceRestrictions() && this.posY >= 63) {
                    cir.setReturnValue(true);
                }
            } else{
                cir.setReturnValue(false);
            }
        }
    }
    @ModifyArg(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getClosestPlayer(DDDD)Lnet/minecraft/src/EntityPlayer;"),index = 3)
    private double increaseGhastSpawnrateOnBloodMoon(double par1){
        if(NMUtils.getIsBloodMoon() && this.dimension == -1){return 16.0;}
        return par1;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void killIfTooHigh(CallbackInfo ci){
        if(this.dimension == 0 && this.posY >= 200){
            this.setDead();
        }
    }

    @ModifyConstant(method = "updateEntityActionState",constant = @Constant(intValue = 10,ordinal = 0))
    private int lowerSoundThreshold(int constant){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        if(thisObj.dimension == 0 && !NMUtils.getIsMobEclipsed(this)){
            return constant * 2;
        }
        if(thisObj.worldObj != null && NMUtils.getWorldProgress()>0){
            return constant - NMUtils.getWorldProgress()*2 -1;
            // 9 -> 7 -> 4 -> 2
        }
        return constant;
    }
    @ModifyConstant(method = "updateEntityActionState",constant = @Constant(intValue = 20,ordinal = 1))
    private int lowerAttackThreshold(int constant){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        if(thisObj.dimension == 0 && !NMUtils.getIsMobEclipsed(this)){
            return NMUtils.divByNiteMultiplier(constant * 2, 10);
        }
        if(thisObj.worldObj != null && NMUtils.getWorldProgress()>0){
            return NMUtils.divByNiteMultiplier((int) (constant - NMUtils.getWorldProgress() * 1.5 - 5), 8);
            // 15 -> 13 -> 12 -> 10
        }
        return constant;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,12);
    }

    @Inject(method = "fireAtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private void boostGhastForwardOnEclipse(CallbackInfo ci){
        if (NMUtils.getIsMobEclipsed(this) && this.rand.nextInt(3) == 0) {
            Vec3 playerPos = Vec3.createVectorHelper(this.entityTargeted.posX,this.entityTargeted.posY,this.entityTargeted.posZ);
            Vec3 entityPos = Vec3.createVectorHelper(this.posX,this.posY,this.posZ);
            // Calculate the direction vector
            Vec3 velocity = entityPos.subtract(playerPos);
            velocity.normalize();

            // Calculate the velocity vector
            velocity.scale(0.02d);
            if (this.getDistanceSqToEntity(this.entityTargeted) > 256) {
                this.motionX = velocity.xCoord;
                this.motionY = velocity.yCoord;
                this.motionZ = velocity.zCoord;
            }
        }
    }

    @ModifyConstant(method = "fireAtTarget", constant = @Constant(intValue = -40))
    private int lowerAttackCooldownOnFire(int constant){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        return (int) ((- 10 - thisObj.rand.nextInt(21)) * NMUtils.getNiteMultiplier());
        // from -10 to -30
    }
}
