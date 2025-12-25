package com.itlesports.nightmaremode.mixin;

import api.world.WorldUtils;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.RottenArrowEntity;
import com.itlesports.nightmaremode.NMUtils;
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
        if(NMUtils.getIsEclipse()){
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

            if (this.canDeflectArrows() && this.rand.nextInt(8 - NMUtils.getWorldProgress() * 2) == 0) {
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

        return NMUtils.LONG_RANGE_ITEMS.contains(heldItem.getItem().itemID);
    }

    @Unique private int timeOfLastAttack;

    @Inject(method = "entityMobOnLivingUpdate", at = @At("TAIL"))
    private void manageHealingOverTime(CallbackInfo ci){
        boolean shouldIncreaseHealth = false;
        if (this.worldObj != null && this.worldObj.isRemote) {
            if(this.ticksExisted % (120 - NMUtils.getWorldProgress() * 10) == 0 && this.timeOfLastAttack + 140 < this.ticksExisted){
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
            if(NMUtils.getIsBloodMoon()){
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(method = "canSpawnOnBlockBelow", at = @At("HEAD"),cancellable = true)
    private void manageBloodmareSpawning(CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsBloodMoon() || NMUtils.getIsMobEclipsed(this)){
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
                int mobY = (int) this.posY;
                int pY = mobY;

                // get the closest player and get distance to it
                EntityPlayer closestPlayer = null;
                double dDistSq = Double.MAX_VALUE;
                for(Object p : this.worldObj.playerEntities){
                    double newDist = this.getDistanceSqToEntity((Entity) p);
                    if (newDist < dDistSq) {
                        dDistSq = newDist;
                        closestPlayer = (EntityPlayer) p;
                        pY = (int) ((EntityPlayer) p).posY;
                    }
                }


                int randomVariance = getRandomVariance(this.worldObj,pY, mobY);
                double minDist = this.minDistFromPlayerForDespawn();
                if (closestPlayer != null && dDistSq > minDist * minDist) {
                    // player is far enough away to check despawn
                    // found player is true by default
                    // distance from mob to player is > 40 blocks

                    // reset age when a player is nearby (so mobs do not despawn while the player is close)
                    if (this.entityAge > 800 && this.rand.nextInt(randomVariance) == 0) {
//                    if (this.rand.nextInt(randomVariance) == 0) {
//                        System.out.println("despawned " + this.getEntityName() + " at y" + mobY + " with variance " + randomVariance + " with pY: " + (int)pY);
                        this.setDead();
                    }
                } else{
                    // closest player doesn't exist (no players online)
                    // OR player exists but distance to player is less than 40 blocks
                    this.entityAge = 0;
                }

            }

        } else {
            // cannot despawn
            this.entityAge = 0;
        }
    }

    @Inject(method = "isValidLightLevel", at = @At("HEAD"), cancellable = true)
    private void ensureEndDimensionSpawns(CallbackInfoReturnable<Boolean> cir){
        if(this.dimension == 1){
            cir.setReturnValue(true);
        }
    }
    @Unique
    private static double getWorldProgressFactor(World world) {
        long totalTicks = world.getWorldTime();
        double weekTicks = 24000.0 * 8.0;

        // from 0.1 to 1.0 through the week
        double progress = Math.min(1.0, totalTicks / weekTicks);

        return 0.5 + (0.5 * progress);
    }

    @Unique
    private static int getRandomVariance(World world, int pY, int mobY) {
        double diff = Math.abs(pY - mobY);
        double capped = Math.min(diff, 60);

        double logPart = Math.log1p(capped) / Math.log1p(20.0);
        double linearPart = capped > 20.0 ? (capped - 20.0) / 40.0 : 0.0;
        double despawnFactor = getDespawnFactor(mobY, logPart, linearPart);

        // scale with world time - [0, 1.0] at 8 days
        double progression = getWorldProgressFactor(world);
        despawnFactor *= progression;

        int baseVariance = 900;
        int randomVariance = (int)(baseVariance * (1.2 - 0.75 * despawnFactor));
        randomVariance = Math.max(1, randomVariance);
        return randomVariance;
    }


    @Unique
    private static double getDespawnFactor(int mobY, double logPart, double linearPart) {
        double despawnFactor = 0.5 * logPart + 0.3 * linearPart;
//        double despawnFactor = 0.6 * logPart + 0.4 * linearPart;

        double caveFactor = 1.0;
        if (mobY < 40) {
            caveFactor += (40.0 - mobY) / 40.0 * 0.25; // up to +25% penalty for very deep mobs
        }
        despawnFactor *= caveFactor;
        despawnFactor = Math.min(1.5, despawnFactor); // hard cap

        // for surface mobs
        if (mobY > 40) {
            final double SURFACE_PROTECT = 0.2;
            final double SURFACE_CAP = 0.3;
            // putting the numbers here so they can easily be changed
            despawnFactor = Math.min(despawnFactor * SURFACE_PROTECT, SURFACE_CAP);
        }
        return despawnFactor;
    }

    @Override
    protected double minDistFromPlayerForDespawn() {
        return super.minDistFromPlayerForDespawn() * 1.25f;
    }

    @Inject(method = "attackEntityFrom", at = @At("TAIL"))
    private void ensureExperienceGain(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsBloodMoon()){
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
