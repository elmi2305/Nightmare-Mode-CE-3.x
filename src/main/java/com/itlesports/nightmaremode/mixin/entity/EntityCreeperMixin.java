package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import api.entity.mob.KickingAnimal;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.AITasks.EntityAIChaseTargetSmart;
import com.itlesports.nightmaremode.entity.creepers.*;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(EntityCreeper.class)
public abstract class EntityCreeperMixin extends EntityMob implements EntityCreeperAccessor{

    @Shadow private int timeSinceIgnited;
    @Shadow public int fuseTime;

    @Shadow public abstract void onKickedByAnimal(KickingAnimal kickingAnimal);

    public EntityCreeperMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "checkForScrollDrop", at = @At("HEAD"),cancellable = true)
    private void noScrollDrops(CallbackInfo ci){
        ci.cancel();
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

            int itemID = NMItems.sulfur.itemID;

            int var4 = this.rand.nextInt(3);
            if (lootingLevel > 0) {
                var4 += this.rand.nextInt(lootingLevel + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addHordeTasks(World par1World, CallbackInfo ci){
        if (NightmareMode.hordeMode) {
            this.tasks.removeAllTasksOfClass(EntityAIAttackOnCollide.class);
            this.tasks.addTask(4, new EntityAIChaseTargetSmart(this, 1.0D));
        }
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void chanceToSpawnWithSpeed(CallbackInfo ci){
        int progress = NMUtils.getWorldProgress();
        double bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.25 : 1;
        int eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 20 : 0;
        boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);

        if (this.rand.nextInt(NMUtils.divByNiteMultiplier(8 - progress * 2, 2)) == 0 && isHostile) {
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10000000,0));
        }
        if (this.rand.nextInt(NMUtils.divByNiteMultiplier(3, 2)) == 0 && eclipseModifier > 1) {
            this.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 10000000,0));
        }
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((20 + progress * 6) * bloodMoonModifier + eclipseModifier) * NMUtils.getNiteMultiplier());
        // 20 -> 26 -> 32 -> 38
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((0.28 + eclipseModifier * 0.005) * ((((NMUtils.getNiteMultiplier() - 1) / 20)) + 1));
    }

    @Unique private boolean isValidForEventLoot = false;
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void storeLastHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
    }
    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0 && bKilledByPlayer && isValidForEventLoot) {
            int var4 = this.rand.nextInt(3);
            // 0 - 2
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }
    }


    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void creeperTeleport(CallbackInfo ci){
        if (NightmareMode.isAprilFools && this.getAttackTarget() instanceof EntityPlayer player && this.getDistanceSqToEntity(player) < 81) {
            Vec3 lookVec = player.getLookVec();

            Vec3 directionToSquid = Vec3.createVectorHelper(
                    this.posX - player.posX,
                    this.posY - (player.posY + player.getEyeHeight()),
                    this.posZ - player.posZ
            ).normalize();

            double dotProduct = lookVec.dotProduct(directionToSquid);

            if (dotProduct < -0.2) {
                this.copyLocationAndAnglesFrom(player);
            }
        }
    }



    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 36.0))
    private double increaseCreeperBreachRange(double constant){
        return getCreeperBreachRange(constant);
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void dropGhastTearsIfCharged(boolean bKilledByPlayer, int iFortuneModifier, CallbackInfo ci){
        if(isCharged()) {
            this.dropItem(Item.ghastTear.itemID, 1);
            if (rand.nextInt(3) == 0) {
                this.dropItem(BTWItems.creeperOysters.itemID, 1);
            }
        }
    }
    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;"))
    private void aprilFoolsFunnyTntBomb(CallbackInfo ci){
        if(NightmareMode.isAprilFools){
            this.worldObj.spawnEntityInWorld(new EntityTNTPrimed(this.worldObj,this.posX,this.posY,this.posZ,this));
        }
    }

    @Inject(method = "interact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/InventoryPlayer;getCurrentItem()Lnet/minecraft/src/ItemStack;",
                    shift = At.Shift.AFTER))
    private void explodeIfShorn(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);
        float bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.25f : 1;
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears && thisObj.getNeuteredState() == 0) {
            if (!thisObj.worldObj.isRemote) {
                if (isHostile || (!isHostile && playersCurrentItem.getItem().itemID == Item.shears.itemID)) {
                    boolean var2 = thisObj.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                    if (thisObj.getPowered()) {
                        thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 8 * bloodMoonModifier, var2);
                    } else {
                        thisObj.worldObj.createExplosion(thisObj, thisObj.posX, thisObj.posY + (double)thisObj.getEyeHeight(), thisObj.posZ, 3 * bloodMoonModifier, var2);
                    }
                    if (!NMUtils.getIsMobEclipsed(this)) {
                        thisObj.setDead();
                    }
                }
            }
        }
    }
    @Redirect(method = "interact", at = @At(value = "FIELD", target = "Lnet/minecraft/src/World;isRemote:Z"))
    private boolean doNotDropCreeperOystersIfShorn(World world){
        if(world.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)){
            return true;
        }
        return world.isRemote;
    }
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void detonateIfFireDamage(DamageSource source, float par2, CallbackInfoReturnable<Boolean> cir){
        if (shouldDetonateOnFire(source)){
            this.onKickedByAnimal(null); // primes the creeper instantly. lightning creeper overrides and skips this
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityCreeper;setDead()V"))
    private void manageNotKillingSelf(EntityCreeper creeper){
        if(NMUtils.getIsMobEclipsed(this)){
            if(creeper.getAttackTarget() instanceof EntityPlayer target){
                double var1 = this.posX - target.posX;
                double var2 = this.posZ - target.posZ;
                Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
                vector.normalize();
                this.motionX = vector.xCoord * 0.2;
                this.motionZ = vector.zCoord * 0.2;
                this.timeSinceIgnited = 0;
                this.fuseTime = 20;
            }
            this.motionY = 0.5f;
        } else{
            this.setDead();
        }
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 2.0f))
    private float creeperImmunityToExplosionDamage(float constant){
        return 5.0f; // explosions deal 1/5 damage to creepers
    }
    @Inject(method = "attackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void immuneToDrowningDuringBloodMoon(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir) {
        if (NMUtils.getIsBloodMoon() && par1DamageSource == DamageSource.drown) {
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "entityInit", constant = @Constant(intValue = 0,ordinal = 0))
    private int chanceToSpawnCharged(int constant){
        return shouldSpawnCharged();
    }

    @Inject(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityCreeper;timeSinceIgnited:I",ordinal = 3, shift = At.Shift.AFTER))
    private void jumpBeforeExploding(CallbackInfo ci){
        EntityCreeper creeper = (EntityCreeper) (Object)this;
        // 8 ticks before it explodes
        if (this.timeSinceIgnited == (this.getFuseTime() - 8) && creeper.getCreeperState() == 1 && creeper.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            EntityPlayer target = creeper.worldObj.getClosestVulnerablePlayerToEntity(creeper,6);
            creeper.motionY = 0.38F;
            if(target != null) {
                double var1 = target.posX - creeper.posX;
                double var2 = target.posZ - creeper.posZ;
                Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
                vector.normalize();
                creeper.motionX = vector.xCoord * 0.18;
                creeper.motionZ = vector.zCoord * 0.18;
                this.faceEntity(target,100f,100f);
            }
        }
    }

    @Override
    public boolean isImmuneToHeadCrabDamage() {
        return true;
    }

    @Override
    public boolean isSecondaryTargetForSquid() {
        return NMUtils.getIsBloodMoon() && isCharged() && !this.inWater;
    }

    @Override
    public Entity getHeadCrabSharedAttackTarget() {
        return this.getAttackTarget();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){NMUtils.manageEclipseChance(this,12);}

    @ModifyArg(method = "onUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;",
                    ordinal = 1), index = 4)
    private float modifyExplosionSize(float par8) {
        return getExplosionSize();
    }




    // HELPER METHODS

    @Unique
    private float getExplosionSize() {
        float aprilFoolsExplosionModifier = NightmareMode.isAprilFools ? 1.05f + 0.15f * this.rand.nextFloat() : 1f;
        float bloodmoonModifier = NMUtils.getIsBloodMoon() ? 0.25f : 0;
        float eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 0.15f : 0;
        float niteModifier = (float) NMUtils.getNiteMultiplier();

        if(!this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)){
            return (3f + bloodmoonModifier) * niteModifier * 1f * aprilFoolsExplosionModifier;
        }
        if(NMUtils.getWorldProgress() >= 2){
            return (4.2f + bloodmoonModifier + eclipseModifier) * niteModifier * 1f * aprilFoolsExplosionModifier;
        } else if(NMUtils.getWorldProgress() == 1){
            return (3.6f + bloodmoonModifier + eclipseModifier) * niteModifier * 1f * aprilFoolsExplosionModifier;
        }
        return (3.375f + bloodmoonModifier + eclipseModifier) * niteModifier * 1f * aprilFoolsExplosionModifier;
    }

    @Unique
    private static void spawnItemExplosion(World world, Entity entity, ItemStack itemStack, int amount, Random random) {
        for (int i = 0; i < amount; i++) {
            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = random.nextDouble() * Math.PI;
            double radius = 0.5 + random.nextDouble() * 0.5;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.cos(phi);
            double zOffset = radius * Math.sin(phi) * Math.sin(theta);

            double spawnX = entity.posX + xOffset;
            double spawnY = entity.posY + yOffset;
            double spawnZ = entity.posZ + zOffset;

            EntityItem itemEntity = new EntityItem(world, spawnX, spawnY, spawnZ, itemStack.copy());

            itemEntity.motionX = xOffset * 0.5;
            itemEntity.motionY = yOffset * 0.5;
            itemEntity.motionZ = zOffset * 0.5;

            world.spawnEntityInWorld(itemEntity);
        }
    }


    @Unique private boolean shouldDetonateOnFire(DamageSource src){
        return (src == DamageSource.inFire ||
                src == DamageSource.onFire ||
                src == DamageSource.lava) && this.dimension != -1 &&
                !NMUtils.getIsBloodMoon() &&
                !NMUtils.getIsMobEclipsed(this) &&
                !this.isPotionActive(Potion.fireResistance.id);
    }
    @Unique
    private double getCreeperBreachRange(double constant) {
        boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);
        if(!isHostile){
            return constant;
        }
        int bloodMoonModifier = NMUtils.getIsBloodMoon() || NMUtils.getIsMobEclipsed(this) ? 3 : 1;
        int i = NMUtils.getWorldProgress();

        return switch (i) {
            case 0 -> 36 * bloodMoonModifier * NMUtils.getNiteMultiplier();  // 6b   10.4b
            case 1 -> 81 * bloodMoonModifier * NMUtils.getNiteMultiplier();  // 9b   15.57b
            case 2, 3 -> 121 * bloodMoonModifier * NMUtils.getNiteMultiplier(); // 11b  19.03b
            default -> constant;
        };
    }
    @Unique
    private boolean isCharged() {
        return this.getDataWatcher().getWatchableObjectByte(17) == 1;
    }
    @Unique
    private int shouldSpawnCharged() {
        EntityCreeper self = (EntityCreeper) (Object) this;
        int progress = NMUtils.getWorldProgress();
        boolean canSpawnCharged = progress > 0 || NightmareMode.evolvedMobs;
        boolean isHostile = self.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);

        if (canSpawnCharged) {
            if (self.dimension == -1 || (self.dimension == 1 && isHostile)) {
                return 1;
            }

            float chargeChance = 0.15f + (progress - 1) * 0.03f;
            if ((self.rand.nextFloat() * NMUtils.getNiteMultiplier()) < chargeChance) {
                if (self.dimension == 0 && self.rand.nextInt(10) == 0) {
                    self.setCustomNameTag("Terrence");
                }
                return 1;
            }
        }

        if (NMUtils.getIsBloodMoon() || NMUtils.getIsMobEclipsed(this)) {
            return self.rand.nextInt(6) == 0 ? 1 : 0;
        }

        return 0;
    }
}
