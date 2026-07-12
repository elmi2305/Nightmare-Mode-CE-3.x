package com.itlesports.nightmaremode.mixin.entity;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.entity.variants.EntityCreeperGhast;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.itlesports.nightmaremode.util.NMFields.*;

@Mixin(EntityGhast.class)
public abstract class EntityGhastMixin extends EntityFlying{
    @Shadow public abstract boolean getCanSpawnHereNoPlayerDistanceRestrictions();

    @Shadow
    private Entity entityTargeted;
    @Unique int rageTimer = 0;
    @Unique boolean firstAttack = true;

    public EntityGhastMixin(World world) {
        super(world);
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        int progress = NMUtils.getWorldProgress();
        boolean isEclipse = NMUtils.getIsMobEclipsed(this);
        boolean isBloodMoon = NMUtils.getIsBloodMoon();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute((16.0d + progress * (isBloodMoon ? 2 : 1) + (isEclipse ? 5 : 0)));
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((20 + 8 * progress) * NMUtils.getNiteMultiplier());
        // 20 -> 28 -> 36 -> 44
    }

    @ModifyConstant(method = "fireAtTarget", constant = @Constant(intValue = 1))
    private int increaseExplosionSize(int constant){
        if(NMUtils.getIsBloodMoon()){
            return constant + 2;
        }
        return constant + NMUtils.getWorldProgress() > PREHARDMODE ? 1 : 0;
    }

    @ModifyArg(method = "updateAttackStateClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getClosestVulnerablePlayerToEntity(Lnet/minecraft/src/Entity;D)Lnet/minecraft/src/EntityPlayer;"), index = 1)
    private double increaseHordeAttackRange(double par2){
        return (this.isTargetPlayerAndPumpkin(this.entityTargeted) || (NMUtils.getIsBloodMoon() && this.dimension == -1)) ? 140d : par2;
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
        return (float) ((this.isTargetPlayerAndPumpkin(this.entityTargeted) || (NMUtils.getIsBloodMoon() && this.dimension == -1)) ? constant * 4d : constant);
    }
    @Redirect(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityGhast;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean allowShootingThroughWallsBloodMoon(EntityGhast instance, Entity entity){
        if(this.isTargetPlayerAndPumpkin(this.entityTargeted) ||(NMUtils.getIsBloodMoon() && instance.dimension == -1)){return true;}
        return instance.canEntityBeSeen(entity);
    }
    @Redirect(method = "updateAttackStateClient", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityGhast;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean seePlayerThroughWallsHorde(EntityGhast instance, Entity entity){
        return this.isTargetPlayerAndPumpkin(this.entityTargeted) || (NMUtils.getIsBloodMoon() && this.dimension == -1) || instance.canEntityBeSeen(entity);
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
            else if (NMUtils.getIsMobEclipsed(this) && (NightmareMode.totalEclipse || NMUtils.getWorldProgress() > POSTWITHER)) {
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
        if (!this.isCreeperVariant()) {
            this.rageTimer = 1;
        }
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

    @Inject(method = "updateEntityActionState", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityGhast;attackCounter:I", ordinal = 2, opcode = Opcodes.GETFIELD),cancellable = true)
    private void manageCreeperEclipseVariant(CallbackInfo ci){
        if(this.isCreeperVariant()){

            if (NMUtils.getIsMobEclipsed(this)) {
                this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 40,0));
                Entity target = this.entityTargeted;
                if(this.firstAttack){
                    this.worldObj.playSoundEffect(target.posX,target.posY,target.posZ, "mob.ghast.scream",1f,0.8f);
                    this.firstAttack = false;
                }

                Vec3 playerPos = Vec3.createVectorHelper(target.posX,target.posY,target.posZ);
                Vec3 entityPos = Vec3.createVectorHelper(this.posX,this.posY,this.posZ);

                // direction vector
                Vec3 velocity = entityPos.subtract(playerPos);
                velocity.normalize();

                // velocity scale
                velocity.scale(0.1d);
                if (this.getDistanceSqToEntity(target) > 4) {
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
            } else{
                this.setDead();
            }
        }
    }

    @ModifyConstant(method = "updateEntityActionState", constant = @Constant(intValue = 20))
    private int modifyAttackThreshold(int constant){
        EntityGhast g = (EntityGhast)(Object)this;
        int progress = NMUtils.getWorldProgress();

        if(g.dimension == 0 && !NMUtils.getIsMobEclipsed(this)){
            return NMUtils.divByNiteMultiplier(constant * 2, 10);
        }

        if(g.dimension != -1) return constant;

        // nether
        boolean postWither = NMUtils.getWorldProgress() > HARDMODE;
        boolean belowHalfHealth = this.getHealth() <= this.getMaxHealth() * 0.5f;

        if(belowHalfHealth || postWither){
            return NMUtils.divByNiteMultiplier((int) (constant - progress * 1.5 - 2), 8);
            // 18 -> 16 -> 15 -> 13
        }
        return (int)(constant * 1.2);
        // 24 by default, pre hm and hm
    }



    @Inject(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityGhast;isCourseTraversable(DDDD)Z", shift = At.Shift.AFTER))
    private void avoidGettingTooCloseToPlayer(CallbackInfo ci){
        EntityGhast g = (EntityGhast)(Object)this;
        Entity target = this.entityTargeted;
        if(target != null && target.isEntityAlive()){
            double distSq = g.getDistanceSqToEntity(target);
            if(distSq < 256.0){
                // calculate direction away from player
                double awayX = g.posX - target.posX;
                double awayY = g.posY - target.posY;
                double awayZ = g.posZ - target.posZ;
                double length = MathHelper.sqrt_double(awayX * awayX + awayY * awayY + awayZ * awayZ);

                if(length > 0){
                    // set new waypoint further away from player
                    double targetDist = 24.0; // try to maintain 24 blocks distance
                    g.waypointX = g.posX + (awayX / length) * targetDist;
                    g.waypointY = g.posY + (awayY / length) * targetDist + (g.rand.nextDouble() * 8.0 - 4.0); // add some vertical variation
                    g.waypointZ = g.posZ + (awayZ / length) * targetDist;

                    // clamp waypoint to reasonable bounds
                    g.waypointY = Math.max(g.posY - 16.0, Math.min(g.posY + 16.0, g.waypointY));
                }
            }
        }
    }

    @Unique private boolean isCreeperVariant(){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        return thisObj instanceof EntityCreeperGhast;
    }


    @Inject(method = "getCanSpawnHere", at = @At("HEAD"),cancellable = true)
    private void manageOverworldSpawn(CallbackInfoReturnable<Boolean> cir){
        if(this.dimension == 0){
            if (NMUtils.getIsBloodMoon() || NMUtils.getIsMobEclipsed(this) || NMEvents.SimpleEvent.HELL.isActive()) {
                if (this.getCanSpawnHereNoPlayerDistanceRestrictions() && this.posY >= 63 && this.rand.nextInt(8) == 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
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
        if(thisObj.worldObj != null && NMUtils.getWorldProgress() > PREHARDMODE){
            return constant - NMUtils.getWorldProgress() * 2 -1;
            // 9 -> 7 -> 4 -> 2
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
            Entity target = this.entityTargeted;
            Vec3 playerPos = Vec3.createVectorHelper(target.posX,target.posY,target.posZ);
            Vec3 entityPos = Vec3.createVectorHelper(this.posX,this.posY,this.posZ);
            // calculate the direction vector
            Vec3 velocity = entityPos.subtract(playerPos);
            velocity.normalize();

            // calculate the velocity vector
            velocity.scale(0.02d);
            if (this.getDistanceSqToEntity(target) > 256) {
                this.motionX = velocity.xCoord;
                this.motionY = velocity.yCoord;
                this.motionZ = velocity.zCoord;
            }
        }
    }

    @Override
    public void checkForScrollDrop() {}

    @ModifyConstant(method = "fireAtTarget", constant = @Constant(intValue = -40))
    private int lowerAttackCooldownOnFire(int constant){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        int baseCooldown = (int) ((- 10 - thisObj.rand.nextInt(21)) * NMUtils.getNiteMultiplier());

        if(thisObj.dimension != -1) return baseCooldown;

        boolean postWither = NMUtils.getWorldProgress() > HARDMODE;
        boolean belowHalfHealth = this.getHealth() <= this.getMaxHealth() * 0.5f;

        if(postWither){
            return (int)(baseCooldown * 0.7); // faster cooldown by default
        }
        if(belowHalfHealth){
            return (int)(baseCooldown * 0.8); // faster cooldown when below half health
        }
        return (int)(baseCooldown * 1.2); // slower cooldown by default
    }
}
