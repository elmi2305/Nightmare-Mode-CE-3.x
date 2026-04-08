package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.EntityBlazeVariantExt;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBlaze.class)
public class EntityBlazeMixin extends EntityMob implements EntityBlazeVariantExt {
    @Unique private int dashTimer = 0;

    public EntityBlazeMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        if(this.worldObj != null) {
            boolean isVariant = false;
            int progress = NMUtils.getWorldProgress();
            int eclipseBonus = NMUtils.getIsMobEclipsed(this) ? (isAquatic() ? 20 : 10) : 0;
            Boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);

            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((16 + progress * (isHostile ? 10 : 4) + eclipseBonus) * NMUtils.getNiteMultiplier());
            // 16 -> 26 -> 36 -> 46
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30);

            if(NMUtils.getIsMobEclipsed(this)){
                if(rand.nextBoolean()){
                    this.nm$setBlazeVariant((byte) NMFields.BLAZE_AQUA);
                    this.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 1000000, 0));
                }
                isVariant = true;
            }
            if(!isVariant){
                if ((progress >= (isHostile ? 0 : 1) || NightmareMode.evolvedMobs) && rand.nextBoolean()) {
                    this.nm$setBlazeVariant((byte) NMFields.BLAZE_SHADOW);
                    this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 1000000, 0));
                }
            }
        }
    }

    @ModifyConstant(method = "attackEntity", constant = @Constant(floatValue = 30.0f))
    private float invisibleBlazePassivity(float constant){
        return isInvisibleBlaze() ? 0f : 30f;
    }

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityBlaze;attackEntityAsMob(Lnet/minecraft/src/Entity;)Z"))
    private void manageInvisibleBlazeAttack(Entity par1Entity, float par2, CallbackInfo ci){
        EntityBlaze blaze = (EntityBlaze)(Object)this;
        if(isInvisibleBlaze()) {

            for (int i = 0; i < 6; i++) {
                double offsetX = (this.rand.nextDouble() - 0.5D) * 3.0D;
                double offsetY = this.rand.nextDouble() * this.height * 1.2D;
                double offsetZ = (this.rand.nextDouble() - 0.5D) * 3.0D;

                this.worldObj.playAuxSFX(2278, (int) (this.posX + offsetX), (int) (this.posY + offsetY), (int) (this.posZ + offsetZ), 0);
            }

            blaze.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4);
            EntityCreeper bomb = new EntityCreeper(blaze.worldObj);
            bomb.copyLocationAndAnglesFrom(blaze);
            blaze.worldObj.spawnEntityInWorld(bomb);
            blaze.setDead();
        }
    }
    @ModifyConstant(method = "attackEntity", constant = @Constant(intValue = 100))
    private int lowerBlazeAttackCooldown(int constant) {
        if (NMUtils.getIsMobEclipsed(this) && this.getActivePotionEffects().isEmpty()){
            return 50; // hacky. should be redone. this is stupid
        }
        return constant;
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageBlazeDash(CallbackInfo ci){
        if(this.entityToAttack instanceof EntityPlayer target){

            double distToPlayerSq = this.getDistanceSqToEntity(target);
            double aggroRange = this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();

            if(distToPlayerSq > (aggroRange * aggroRange) + 124){
                // expected aggro range is 900, +124 makes it 1024, so the aggro resets after 32 blocks
                // set to null because it shouldn't care about the player anymore if it lost aggro. previously it just kept the last seen player
                this.entityToAttack = null;
                return;
            }


            boolean isEclipse = NMUtils.getIsMobEclipsed(this);
            int threshold = isEclipse ? 80 : 200;

            if(isEclipse && distToPlayerSq < 49){
                // it goes vroom when close to the player
                this.dashTimer += 3;
            }

            this.dashTimer = Math.min(this.dashTimer + 1 , threshold);
            if (this.dashTimer == threshold){
                double dx = target.posX - this.posX;
                double dy = target.posY - this.posY;
                double dz = target.posZ - this.posZ;

                if(distToPlayerSq < 49 && isEclipse && !this.isInvisibleBlaze()){
                    dx *= -1.5;
                    dy *= -1;
                    dz *= -1.5;
                }

                Vec3 vector = Vec3.createVectorHelper(dx, dy, dz);
                vector.normalize();
                this.motionX = vector.xCoord * 0.12;
                this.motionY = vector.yCoord * 0.18 + 0.4 + (this.rand.nextFloat() * 0.2);
                this.motionZ = vector.zCoord * 0.12;
                this.dashTimer = 0;
            } else if(this.dashTimer > (threshold - 20)){
                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,8);
    }


    @ModifyArg(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"))
    private Entity manageWaterBlazeAttack(Entity projectile){
        if (this.getEntityToAttack() instanceof EntityPlayer target && !target.capabilities.isCreativeMode && NMUtils.getIsMobEclipsed(this)) {
            if(isAquatic()){
                BTWSquidEntity squid = new BTWSquidEntity(this.worldObj);

                double posX = this.posX + getRandomOffsetFromPosition(this);
                double posY = this.posY + this.getEyeHeight() + (getRandomOffsetFromPosition(this) / 4);
                double posZ = this.posZ + getRandomOffsetFromPosition(this);

                squid.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, Integer.MAX_VALUE));

                squid.setPositionAndUpdate(posX,posY,posZ);

                return squid;
            } else{
                double deltaX = target.posX - this.posX;
                double deltaY = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F)) - 0.5;
                double deltaZ = target.posZ - this.posZ;

                EntityLargeFireball largeFireball = new EntityLargeFireball(this.worldObj, this, deltaX, deltaY, deltaZ);
                this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                largeFireball.posY = this.posY + (double) (this.height / 2.0f) + 0.5;

                return largeFireball;
            }
        }

        return projectile;
    }
    @Inject(method = "entityInit", at = @At("TAIL"))
    private void doBlazeVariantInDataWatcher(CallbackInfo ci){
        this.dataWatcher.addObject(20,(byte) 0);
    }


    @Unique private static double getRandomOffsetFromPosition(EntityLivingBase entity){
        return ((entity.rand.nextBoolean() ? -1 : 1) * entity.rand.nextInt(3)+2);
    }


    @Unique private boolean isInvisibleBlaze() {
        return this.nm$getBlazeVariant() == NMFields.BLAZE_SHADOW;
    }
    @Unique private boolean isAquatic(){
        return this.nm$getBlazeVariant() == NMFields.BLAZE_AQUA;
    }

    @Unique private boolean isValidForEventLoot = false;
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
        return super.attackEntityFrom(par1DamageSource, par2);
    }
    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void manageEclipseShardDrops(boolean bKilledByPlayer, int lootingLevel, CallbackInfo ci){
        if (bKilledByPlayer && NMUtils.getIsMobEclipsed(this) && isValidForEventLoot) {
            for(int i = 0; i < (lootingLevel * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = isAquatic() ? NMItems.waterRod.itemID : isInvisibleBlaze() ? NMItems.shadowRod.itemID : NMItems.fireRod.itemID;

            int var4 = this.rand.nextInt(3);
            if(this.dimension == -1){
                var4 += 4;
            }
            if (lootingLevel > 0) {
                var4 += this.rand.nextInt(lootingLevel + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }
    }

    @Unique
    public byte nm$getBlazeVariant() {
        return this.dataWatcher.getWatchableObjectByte(20);
    }

    @Unique
    public void nm$setBlazeVariant(byte variantType) {
        this.dataWatcher.updateObject(20,variantType);
    }
}
