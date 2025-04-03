package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.entity.EntityDungCreeper;
import com.itlesports.nightmaremode.entity.EntityFireCreeper;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.entity.EntityMetalCreeper;
import com.itlesports.nightmaremode.entity.EntitySuperchargedCreeper;
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
    @Unique private static boolean areMobsEvolved = NightmareMode.evolvedMobs;

    @Shadow private int timeSinceIgnited;
    @Shadow public int fuseTime;

    public EntityCreeperMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "checkForScrollDrop", at = @At("HEAD"),cancellable = true)
    private void noScrollDrops(CallbackInfo ci){
        ci.cancel();
    }
    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void manageEclipseShardDrops(boolean bKilledByPlayer, int lootingLevel, CallbackInfo ci){
        if (bKilledByPlayer && NightmareUtils.getIsMobEclipsed(this)) {
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

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void chanceToSpawnWithSpeed(CallbackInfo ci){
        int progress = NightmareUtils.getWorldProgress(this.worldObj);
        double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.25 : 1;
        int eclipseModifier = NightmareUtils.getIsMobEclipsed(this) ? 20 : 0;
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;

        if (this.rand.nextInt(NightmareUtils.divByNiteMultiplier(8 - progress * 2, 2)) == 0 && isHostile) {
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10000000,0));
        }
        if (this.rand.nextInt(NightmareUtils.divByNiteMultiplier(3, 2)) == 0 && eclipseModifier > 1) {
            this.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 10000000,0));
        }
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((20 + progress * 6) * bloodMoonModifier + eclipseModifier) * NightmareUtils.getNiteMultiplier());
        // 20 -> 26 -> 32 -> 38
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((0.28 + eclipseModifier * 0.005) * ((((NightmareUtils.getNiteMultiplier() - 1) / 20)) + 1));
    }

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0 && bKilledByPlayer) {
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

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityCreeper;setDead()V"))
    private void manageDungCreeper(CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if(thisObj instanceof EntityDungCreeper){
            for(int i = 0; i < 24; i++){
                spawnItemExplosion(this.worldObj,this, new ItemStack(BTWItems.dung),3,this.rand);
            }
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void creeperTeleport(CallbackInfo ci){
        if (NightmareMode.isAprilFools && this.getAttackTarget() instanceof EntityPlayer player && this.getDistanceSqToEntity(player) < 81) {
            Vec3 lookVec = player.getLookVec(); // Player's look direction

            Vec3 directionToSquid = Vec3.createVectorHelper(
                    this.posX - player.posX,
                    this.posY - (player.posY + player.getEyeHeight()), // From eye level
                    this.posZ - player.posZ
            ).normalize(); // Normalize to get direction

            double dotProduct = lookVec.dotProduct(directionToSquid); // How aligned the vectors are

            if (dotProduct < -0.2) {
                this.copyLocationAndAnglesFrom(player);
            }
        }

    }
    @Unique
    private static void spawnItemExplosion(World world, Entity entity, ItemStack itemStack, int amount, Random random) {
        for (int i = 0; i < amount; i++) {
            // Random spherical coordinates
            double theta = random.nextDouble() * 2 * Math.PI; // Horizontal angle
            double phi = random.nextDouble() * Math.PI;       // Vertical angle
            double radius = 0.5 + random.nextDouble() * 0.5; // Sphere size (0.5 - 1 block radius)

            // Convert spherical coordinates to Cartesian
            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.cos(phi);
            double zOffset = radius * Math.sin(phi) * Math.sin(theta);

            // Spawn position relative to the entity
            double spawnX = entity.posX + xOffset;
            double spawnY = entity.posY + yOffset;
            double spawnZ = entity.posZ + zOffset;

            // Create item entity
            EntityItem itemEntity = new EntityItem(world, spawnX, spawnY, spawnZ, itemStack.copy());

            // Outward momentum (normalized direction vector)
            itemEntity.motionX = xOffset * 0.5;
            itemEntity.motionY = yOffset * 0.5;
            itemEntity.motionZ = zOffset * 0.5;

            // Spawn the item entity
            world.spawnEntityInWorld(itemEntity);
        }
    }

    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 36.0))
    private double increaseCreeperBreachRange(double constant){
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
        if(!isHostile){return constant;}
        int bloodMoonModifier = NightmareUtils.getIsBloodMoon() || NightmareUtils.getIsMobEclipsed(this) ? 3 : 1;
        int i = NightmareUtils.getWorldProgress(this.worldObj);

        return switch (i) {
            case 0 ->  36 * bloodMoonModifier * NightmareUtils.getNiteMultiplier();  // 6b   10.4b
            case 1 ->  81 * bloodMoonModifier * NightmareUtils.getNiteMultiplier();  // 9b   15.57b
            case 2 -> 121 * bloodMoonModifier * NightmareUtils.getNiteMultiplier(); // 11b  19.03b
            case 3 -> 196 * bloodMoonModifier * NightmareUtils.getNiteMultiplier(); // 14b  24.2b
            default -> constant;
        };
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void dropGhastTearsIfCharged(boolean bKilledByPlayer, int iFortuneModifier, CallbackInfo ci){
        if(this.getDataWatcher().getWatchableObjectByte(17) == 1) {
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
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
        float bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.25f : 1;
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
                    if (!NightmareUtils.getIsMobEclipsed(this)) {
                        thisObj.setDead();
                    }
                }
            }
        }
    }
    @Redirect(method = "interact", at = @At(value = "FIELD", target = "Lnet/minecraft/src/World;isRemote:Z"))
    private boolean doNotDropCreeperOystersIfShorn(World world){
        if(world.getDifficulty() != Difficulties.HOSTILE){
            return world.isRemote;
        }
        return true;
    }
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void detonateIfFireDamage(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if ((par1DamageSource == DamageSource.inFire || par1DamageSource == DamageSource.onFire || par1DamageSource == DamageSource.lava) && this.dimension != -1 && !NightmareUtils.getIsBloodMoon() && !NightmareUtils.getIsMobEclipsed(this) && !this.isPotionActive(Potion.fireResistance.id)){
            this.onKickedByAnimal(null); // primes the creeper instantly
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityCreeper;setDead()V"))
    private void manageNotKillingSelf(EntityCreeper creeper){
        if(NightmareUtils.getIsMobEclipsed(this)){
            if(creeper.getAttackTarget() instanceof EntityPlayer target){
                double var1 = this.posX - target.posX;
                double var2 = this.posZ - target.posZ;
                Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
                vector.normalize();
                this.motionX = vector.xCoord * 0.2;
                this.motionY = 0.5f;
                this.motionZ = vector.zCoord * 0.2;
                this.timeSinceIgnited = 0;
                this.fuseTime = 20;
            }
        } else{
            this.setDead();
        }
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 2.0f))
    private float creeperImmunityToExplosionDamage(float constant){
        return 5.0f; // explosions deal 1/5 damage to creepers
    }
    @Inject(method = "attackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void immuneToDrowningDuringBloodMoon(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsBloodMoon() && par1DamageSource == DamageSource.drown){
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "entityInit", constant = @Constant(intValue = 0,ordinal = 0))
    private int chanceToSpawnCharged(int constant){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        int progress = NightmareUtils.getWorldProgress(thisObj.worldObj);
        boolean isBloodMoon = NightmareUtils.getIsBloodMoon();
        boolean isEclipse = NightmareUtils.getIsMobEclipsed(this);

        if((progress > 0 || areMobsEvolved) && (thisObj.rand.nextFloat() * NightmareUtils.getNiteMultiplier()) < 0.15 + (progress - 1) * 0.03){
            if(thisObj.rand.nextInt(10) == 0 && thisObj.dimension == 0) {
                thisObj.setCustomNameTag("Terrence");
            }
            return 1;   // set to charged if conditions met
        } else if((thisObj.dimension == -1 && !(thisObj instanceof EntityFireCreeper)) && (progress > 0 || areMobsEvolved)){
            return 1;
        } else if(thisObj.dimension == 1 && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE){
            return 1;
        }
        if(isBloodMoon || isEclipse){
            return rand.nextInt(6) == 0 ? 1 : 0;
        }
        return 0;
    }
    @Unique private int creeperTimeSinceIgnited = 0;

    @Inject(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityCreeper;timeSinceIgnited:I",ordinal = 3, shift = At.Shift.AFTER))
    private void jumpBeforeExploding(CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper) (Object)this;

        if (!(thisObj instanceof EntityMetalCreeper) && !(thisObj instanceof EntitySuperchargedCreeper)) {
            if (thisObj.getCreeperState() == 1) {
                this.creeperTimeSinceIgnited++;
            } else {this.creeperTimeSinceIgnited = 0;}

            // 8 ticks before it explodes
            if (this.creeperTimeSinceIgnited == (this.getFuseTime() - 8) && thisObj.getCreeperState() == 1 && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                EntityPlayer target = thisObj.worldObj.getClosestVulnerablePlayerToEntity(thisObj,6);
                thisObj.motionY = 0.38F;
                if(target != null) {
                    double var1 = target.posX - thisObj.posX;
                    double var2 = target.posZ - thisObj.posZ;
                    Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
                    vector.normalize();
                    thisObj.motionX = vector.xCoord * 0.18;
                    thisObj.motionZ = vector.zCoord * 0.18;
                    this.faceEntity(target,100f,100f);
                }
            }
        }
    }

    @Override
    public boolean isImmuneToHeadCrabDamage() {
        return true;
    }

    @Override
    public boolean isSecondaryTargetForSquid() {
        return NightmareUtils.getIsBloodMoon() && this.getDataWatcher().getWatchableObjectByte(17) == 0 && !this.inWater;
    }

    @Override
    public Entity getHeadCrabSharedAttackTarget() {
        return this.getAttackTarget();
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,12);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setFuseTimeDependingOnVariant(World world, CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if (thisObj instanceof EntityMetalCreeper) {
            this.setFuseTime(60);
        } else if (thisObj instanceof EntitySuperchargedCreeper) {
            this.setFuseTime(15);
        }
    }

    @ModifyArg(method = "onUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;",
                    ordinal = 1), index = 4)
    private float modifyExplosionSize(float par8) {
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        float aprilFoolsExplosionModifier = NightmareMode.isAprilFools ? 1.05f + 0.15f * this.rand.nextFloat() : 1f;
        float variantExplosionModifier = thisObj instanceof EntityMetalCreeper ? 1.25f : (thisObj instanceof EntitySuperchargedCreeper ? 1.4f : 1f);
        float bloodmoonModifier = NightmareUtils.getIsBloodMoon() ? 0.25f : 0;
        float eclipseModifier = NightmareUtils.getIsMobEclipsed(this) ? 0.15f : 0;
        float niteModifier = (float) NightmareUtils.getNiteMultiplier();

        if(this.worldObj.getDifficulty() != Difficulties.HOSTILE){
            return (3f + bloodmoonModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        }
        if(NightmareUtils.getWorldProgress(this.worldObj) >= 2){
            return (4.2f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        } else if(NightmareUtils.getWorldProgress(this.worldObj) == 1){
            return (3.6f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        }
        return (3.375f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
    }
}
