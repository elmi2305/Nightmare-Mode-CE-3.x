package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.RottenArrowEntity;
import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(EntityMob.class)
public abstract class EntityMobMixin extends EntityCreature implements EntityLivingAccessor{
    @Unique private int arrowCooldown;

    public EntityMobMixin(World par1World) {
        super(par1World);
    }

    @Override
    public boolean isPushedByWater() {
        return !this.hasAttackTarget();
    }

    @Inject(method = "isValidLightLevel", at = @At(value = "RETURN", ordinal = 2),cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void ensureSpawnsOnEclipse(CallbackInfoReturnable<Boolean> cir, int x, int y, int z, int blockLightValue, int naturalLightValue){
        if(NightmareUtils.getIsEclipse()){
            cir.setReturnValue(naturalLightValue <= this.rand.nextInt(8) + 8);
        }
    }

    @ModifyArg(method = "isValidLightLevel", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), index = 1)
    private int increaseMobSpawningOnStormChance(int a){
        return NightmareMode.darkStormyNightmare ? 3 : 4;
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    private void manageArrowDeflection(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (par1DamageSource.getSourceOfDamage() instanceof EntityArrow arrow) {
            if (arrow instanceof RottenArrowEntity) {
                arrow.setDead();
                this.worldObj.playSoundAtEntity(this, "random.break", 1.0f, 1.0f);
                cir.setReturnValue(false);
                return;
            }

            if (this.canDeflectArrows() && this.rand.nextInt(8 - NightmareUtils.getWorldProgress() * 2) == 0) {
                this.arrowCooldown = 40;

                EntityArrow reflectedArrow = new EntityArrow(this.worldObj);
                reflectedArrow.copyLocationAndAnglesFrom(arrow);
                reflectedArrow.motionX = -arrow.motionX / 1.5;
                reflectedArrow.motionY = -arrow.motionY;
                reflectedArrow.motionZ = -arrow.motionZ / 1.5;
                arrow.setDead();

                this.worldObj.spawnEntityInWorld(reflectedArrow);
                this.worldObj.playSoundAtEntity(this, "random.break", 1.0f, 7.0f);
                this.swingItem();
                cir.setReturnValue(false);
            }
        }

    }

    @Unique private boolean canDeflectArrows(){
        if (this.arrowCooldown > 0) return false;

        ItemStack heldItem = this.getHeldItem();
        if(heldItem == null) return false;

        return NightmareUtils.LONG_RANGE_ITEMS.contains(heldItem.getItem().itemID);
    }

    @Unique private int timeOfLastAttack;

    @Inject(method = "entityMobOnLivingUpdate", at = @At("TAIL"))
    private void manageHealingOverTime(CallbackInfo ci){
        boolean shouldIncreaseHealth = false;
        if (this.worldObj != null && this.worldObj.isRemote) {
            if(this.ticksExisted % (120 - NightmareUtils.getWorldProgress() * 10) == 0 && this.timeOfLastAttack + 140 < this.ticksExisted){
                shouldIncreaseHealth = true;
            }
        }
        if(shouldIncreaseHealth){
            this.heal(1f);
        }
        this.arrowCooldown = Math.max(this.arrowCooldown - 1, 0);
    }

    @Inject(method = "entityMobAttackEntityFrom", at = @At("TAIL"))
    private void timeEntityWasRecentlyHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (this.worldObj.isRemote) {
            this.timeOfLastAttack = this.ticksExisted;
        }
    }

    @Inject(method = "entityMobAttackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void mobMagicImmunity(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        EntityMob thisObj = (EntityMob)(Object)this;
        if((par1DamageSource == DamageSource.magic || par1DamageSource == DamageSource.wither || par1DamageSource == DamageSource.fallingBlock) && (thisObj instanceof EntityWitch || thisObj instanceof EntitySpider || thisObj instanceof EntitySilverfish)){
            cir.setReturnValue(false);
        }
        if (par1DamageSource == DamageSource.fall && (thisObj instanceof EntityCreeper || thisObj instanceof EntitySkeleton) && thisObj.dimension == 1){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void avoidAttackingWitches(CallbackInfo ci){
        EntityMob thisObj = (EntityMob)(Object)this;
        if(thisObj.getAttackTarget() instanceof EntityWitch || thisObj.getAttackTarget() instanceof EntityWither || (thisObj.getAttackTarget() instanceof EntitySpider && thisObj instanceof EntitySkeleton)){
            thisObj.setAttackTarget(null);
        }
    }
    @Inject(method = "isValidLightLevel", at = @At("HEAD"), cancellable = true)
    private void allowBloodMoonSpawnsInLight(CallbackInfoReturnable<Boolean> cir){
        EntityMob thisObj = (EntityMob)(Object)this;
        if (thisObj.worldObj != null) {
            if(NightmareUtils.getIsBloodMoon()){
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(method = "canSpawnOnBlockBelow", at = @At("HEAD"),cancellable = true)
    private void manageBloodmareSpawning(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsBloodMoon() || NightmareUtils.getIsMobEclipsed(this)){
            int i = MathHelper.floor_double(this.posX);
            int j = (int)this.boundingBox.minY - 1;
            int k = MathHelper.floor_double(this.posZ);
            Material blockMaterial = this.worldObj.getBlockMaterial(i,j,k);
            if(this.worldObj != null && this.worldObj.getBlockId(i,j,k) != 0 && blockMaterial != Material.water && blockMaterial != Material.lava){
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(method = "getCanSpawnHere", at = @At("RETURN"), cancellable = true)
    private void restrictSpawningFarFromPlayer(CallbackInfoReturnable<Boolean> cir) {
        EntityMob mob = (EntityMob)(Object)this;

        // Only apply when relevant
        if (!NightmareMode.hordeMode && !(mob instanceof EntityBloodZombie)) return;

        EntityPlayer player = worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, -1);
        if (player == null) return;
        if(player.capabilities.isCreativeMode) return;

        double mobY = this.posY;
        double playerY = player.posY;
        double heightDifference = Math.abs(mobY - playerY);

        boolean playerIsHigh = playerY > 70;
        boolean mobIsHigh = mobY > 60;


        if (heightDifference > 20) {
            if(playerIsHigh && mobIsHigh){
                return;
            }

            cir.setReturnValue(false);
        }
    }
    @Inject(method = "isValidLightLevel", at = @At("HEAD"), cancellable = true)
    private void ensureEndDimensionSpawns(CallbackInfoReturnable<Boolean> cir){
        if(this.dimension == 1){
            cir.setReturnValue(true);
        }
    }

    @Override
    protected void despawnEntity() {
        boolean isHorde = NightmareMode.hordeMode;
        if (!this.getPersistence() && this.canDespawn()) {
            int chunkX = MathHelper.floor_double(this.posX / 16.0);
            int chunkZ = MathHelper.floor_double(this.posZ / 16.0);

            if (!this.worldObj.isChunkActive(chunkX, chunkZ)) {
                this.setDead();
                return;
            }

            if (isHorde) {
                // Horde mode: prioritize target-based despawning
                EntityLivingBase target = this.getAttackTarget();

                if (target != null && target.isEntityAlive()) {
                    double distSq = this.getDistanceSqToEntity(target);

                    if (distSq > 48.0 * 48.0) {
                        this.setDead();
                        return;
                    }

                    // Still chasing target → stay alive
                    this.entityAge = 0;
                } else {
                    // No target: fall back to player proximity
                    EntityPlayer closestPlayer = this.worldObj.getClosestPlayerToEntity(this, this.minDistFromPlayerForDespawn());
                    if (closestPlayer != null) {
                        this.entityAge = 0;
                    } else if (this.entityAge > 600 && this.rand.nextInt(800) == 0) {
                        this.setDead();
                    }
                }

            } else {
                // Vanilla behavior
                EntityPlayer closestPlayer = this.worldObj.getClosestPlayerToEntity(this, this.minDistFromPlayerForDespawn());
                if (closestPlayer != null) {
                    this.entityAge = 0;
                } else if (this.entityAge > 600 && this.rand.nextInt(800) == 0) {
                    this.setDead();
                }
            }

        } else {
            this.entityAge = 0;
        }
    }


    @Inject(method = "attackEntityFrom", at = @At("TAIL"))
    private void ensureExperienceGain(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsBloodMoon()){
            boolean bIsPostWither = WorldUtils.gameProgressHasWitherBeenSummonedServerOnly();
            this.experienceValue = bIsPostWither ? 40 : 20;
        } else{
            this.experienceValue = NightmareMode.nite ? 10 : 5;
        }
    }

    @Inject(method = "entityMobOnLivingUpdate", at = @At("TAIL"))
    private void manageBlightPowerUp(CallbackInfo ci){
        EntityMob thisObj = (EntityMob)(Object)this;
        if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
            if(thisObj.worldObj.getBlockId(MathHelper.floor_double(thisObj.posX),MathHelper.floor_double(thisObj.posY-1),MathHelper.floor_double(thisObj.posZ)) == BTWBlocks.aestheticEarth.blockID){
                int i = MathHelper.floor_double(thisObj.posX);
                int j = MathHelper.floor_double(thisObj.posY-1);
                int k = MathHelper.floor_double(thisObj.posZ);

                if(thisObj.worldObj.getBlockMetadata(i,j,k) == 0){
                    this.addBlightPotionEffect(thisObj,Potion.regeneration.id);
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 1){
                    this.addBlightPotionEffect(thisObj,Potion.regeneration.id);
                    this.addBlightPotionEffect(thisObj,Potion.resistance.id);
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 2){
                    this.addBlightPotionEffect(thisObj,Potion.moveSpeed.id);
                    this.addBlightPotionEffect(thisObj,Potion.damageBoost.id);
                    this.addBlightPotionEffect(thisObj,Potion.resistance.id);
                } else if (thisObj.worldObj.getBlockMetadata(i,j,k) == 3){
                    this.addBlightPotionEffect(thisObj,Potion.moveSpeed.id);
                    this.addBlightPotionEffect(thisObj,Potion.damageBoost.id);
                    this.addBlightPotionEffect(thisObj,Potion.resistance.id);
                    this.addBlightPotionEffect(thisObj,Potion.invisibility.id);
                }
            }
        }
    }

    @Unique private void addBlightPotionEffect(EntityMob mob, int potionID){
        if(!mob.isPotionActive(potionID)){
            mob.addPotionEffect(new PotionEffect(potionID,100,0));
        }
    }
}
