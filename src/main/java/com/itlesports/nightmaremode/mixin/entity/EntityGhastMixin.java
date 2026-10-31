package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.GhastFlightNavigator;
import com.itlesports.nightmaremode.entity.variants.EntityCreeperGhast;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
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
    @Unique boolean firstAttack = true;
    @Unique private final GhastFlightNavigator flightNavigator = new GhastFlightNavigator();

    public EntityGhastMixin(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void tickGhastCarcass(CallbackInfo ci) {
        if ((Object)this instanceof CarcassAnimal carcass && carcass.nm$isCarcass()) {
            carcass.nm$tickCarcass();
            ci.cancel();
        }
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
            return constant + (this.dimension == -1 ? 1 : 2);
        }
        return constant + NMUtils.getWorldProgress() > PREHARDMODE ? 1 : 0;
    }

    @ModifyConstant(method = {"updateAttackStateClient", "updateEntityActionState"}, constant = @Constant(doubleValue = 4096.0D))
    private double limitAttackRange(double constant){
        double range = com.itlesports.nightmaremode.util.NMGhastRules.attackRange();
        return range * range;
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



    @Inject(method = "updateEntityActionState", at = @At("HEAD"))
    private void approachPlayerBeforeShooting(CallbackInfo ci) {
        if (this.isCreeperVariant()) return;
        double preferred = com.itlesports.nightmaremode.util.NMGhastRules.attackRange() * 0.75D;
        this.flightNavigator.update((EntityGhast)(Object)this, this.entityTargeted, preferred);
    }

    @Unique private boolean isCreeperVariant(){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        return thisObj instanceof EntityCreeperGhast;
    }


    @Inject(method = "getCanSpawnHere", at = @At("HEAD"),cancellable = true)
    private void manageOverworldSpawn(CallbackInfoReturnable<Boolean> cir){
        if (this.dimension == -1 && this.rand.nextInt(4) == 0) {
            cir.setReturnValue(false);
            return;
        }
        if(this.dimension == 0){
            if (OverworldTierHelper.getRegion(this.worldObj, this.posX, this.posZ) == OverworldTierHelper.Region.DEADZONE) {
                cir.setReturnValue(this.getCanSpawnHereNoPlayerDistanceRestrictions()
                        && this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 24.0D) == null);
                return;
            }
            if ((NMUtils.hasEmpoweredBloodMoonMobs() || NMUtils.getIsMobEclipsed(this) || NMEvents.SimpleEvent.HELL.isActive())) {
                if (this.getCanSpawnHereNoPlayerDistanceRestrictions() && this.posY >= 63 && this.rand.nextInt(8) == 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
                    cir.setReturnValue(true);
                }
            } else{
                cir.setReturnValue(false);
            }
        }
    }
    @ModifyArg(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getClosestPlayer(DDDD)Lnet/minecraft/src/EntityPlayer;"),index = 3)
    private double limitBloodMoonSpawnProximity(double par1){
        if(NMUtils.hasEmpoweredBloodMoonMobs() && this.dimension == -1){return 32.0;}
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
