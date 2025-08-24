package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.LightningBoltEntity;
import btw.entity.mob.KickingAnimal;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.Difficulty;
import com.itlesports.nightmaremode.AITasks.EntityAIChaseTargetSmart;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.entity.*;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.server.MinecraftServer;
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
    @Unique private boolean areMobsEvolved = NightmareMode.evolvedMobs;

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
        if (bKilledByPlayer && NMUtils.getIsMobEclipsed(this)) {
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
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;

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

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
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


    // dung creeper variant handled in EntityCreeperVariant.class
    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityCreeper;setDead()V"))
    private void manageDungCreeper(CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if(thisObj instanceof EntityDungCreeper){
            for(int i = 0; i < 12; i++){
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



    @ModifyConstant(method = "onUpdate", constant = @Constant(doubleValue = 36.0))
    private double increaseCreeperBreachRange(double constant){
        boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
        if(!isHostile){return constant;}
        int bloodMoonModifier = NMUtils.getIsBloodMoon() || NMUtils.getIsMobEclipsed(this) ? 3 : 1;
        int i = NMUtils.getWorldProgress();

        return switch (i) {
            case 0 ->  36 * bloodMoonModifier * NMUtils.getNiteMultiplier();  // 6b   10.4b
            case 1 ->  81 * bloodMoonModifier * NMUtils.getNiteMultiplier();  // 9b   15.57b
            case 2, 3 -> 121 * bloodMoonModifier * NMUtils.getNiteMultiplier(); // 11b  19.03b
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
        if(world.getDifficulty() != Difficulties.HOSTILE){
            return world.isRemote;
        }
        return true;
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
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        int progress = NMUtils.getWorldProgress();
        boolean isBloodMoon = NMUtils.getIsBloodMoon();
        boolean isEclipse = NMUtils.getIsMobEclipsed(this);

        if(thisObj instanceof EntityLightningCreeper){
            return 1;
        }
        if((progress > 0 || areMobsEvolved) && (thisObj.rand.nextFloat() * NMUtils.getNiteMultiplier()) < 0.15 + (progress - 1) * 0.03){
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

    @Inject(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityCreeper;timeSinceIgnited:I",ordinal = 3, shift = At.Shift.AFTER))
    private void jumpBeforeExploding(CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper) (Object)this;
        if (!(thisObj instanceof EntityMetalCreeper) && !(thisObj instanceof EntitySuperchargedCreeper) && !(thisObj instanceof EntityLightningCreeper)) {
            // 8 ticks before it explodes
            if (this.timeSinceIgnited == (this.getFuseTime() - 8) && thisObj.getCreeperState() == 1 && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE) {
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
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;canCreepersBreachWalls()Z"), remap = false)
    private boolean avoidLightningCreeperExplodingThroughWalls(Difficulty instance){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        if(thisObj instanceof EntityLightningCreeper) return false;
        return instance.canCreepersBreachWalls();
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/src/World;isRemote:Z"))
    private boolean lightningCreeperAvoidsExploding(World instance){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;



        if(!instance.isRemote && thisObj instanceof EntityLightningCreeper){
            EntityPlayer player;
            if(this.getAttackTarget() instanceof EntityPlayer targetPlayer){
                player = targetPlayer;
            } else {
                player = this.worldObj.getClosestPlayerToEntity(this, 16);
            }

            if (player != null) {
                int worldState = NMUtils.getWorldProgress();
                double xOffset = (this.rand.nextFloat() * (3 - worldState)) / 4 * (this.rand.nextBoolean() ? 1 : -1);
                double zOffset = (this.rand.nextFloat() * (3 - worldState)) / 4 * (this.rand.nextBoolean() ? 1 : -1);
                Entity lightningbolt = new LightningBoltEntity(this.worldObj, player.posX + xOffset, player.posY - 0.5, player.posZ + zOffset);
                this.worldObj.addWeatherEffect(lightningbolt);
            }
            this.setDead();
        }

        if (thisObj instanceof EntityLightningCreeper) {
            return true;
        }
        return instance.isRemote;
    }

    @Override
    public boolean isImmuneToHeadCrabDamage() {
        return true;
    }

    @Override
    public boolean isSecondaryTargetForSquid() {
        return NMUtils.getIsBloodMoon() && this.getDataWatcher().getWatchableObjectByte(17) == 0 && !this.inWater;
    }

    @Override
    public Entity getHeadCrabSharedAttackTarget() {
        return this.getAttackTarget();
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,12);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void setFuseTimeDependingOnVariant(World world, CallbackInfo ci){
        EntityCreeper thisObj = (EntityCreeper)(Object)this;

        if (thisObj instanceof EntityMetalCreeper) {
            this.setFuseTime(60);
        } else if (thisObj instanceof EntitySuperchargedCreeper) {
            this.setFuseTime(15);
        } else if (thisObj instanceof EntityLightningCreeper) {
            this.setFuseTime(90 - NMUtils.getWorldProgress() * 10);
        }
    }

    @ModifyArg(method = "onUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;createExplosion(Lnet/minecraft/src/Entity;DDDFZ)Lnet/minecraft/src/Explosion;",
                    ordinal = 1), index = 4)
    private float modifyExplosionSize(float par8) {
        EntityCreeper thisObj = (EntityCreeper)(Object)this;
        float aprilFoolsExplosionModifier = NightmareMode.isAprilFools ? 1.05f + 0.15f * this.rand.nextFloat() : 1f;
        float variantExplosionModifier = thisObj instanceof EntityMetalCreeper ? 1.5f : (thisObj instanceof EntitySuperchargedCreeper ? 1.4f : 1f);
        float bloodmoonModifier = NMUtils.getIsBloodMoon() ? 0.25f : 0;
        float eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 0.15f : 0;
        float niteModifier = (float) NMUtils.getNiteMultiplier();

        if(this.worldObj.getDifficulty() != Difficulties.HOSTILE){
            return (3f + bloodmoonModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        }
        if(NMUtils.getWorldProgress() >= 2){
            return (4.2f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        } else if(NMUtils.getWorldProgress() == 1){
            return (3.6f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
        }
        return (3.375f + bloodmoonModifier + eclipseModifier) * niteModifier * variantExplosionModifier * aprilFoolsExplosionModifier;
    }








    // HELPER METHODS




    @Unique
    private static void spawnItemExplosion(World world, Entity entity, ItemStack itemStack, int amount, Random random) {
        for (int i = 0; i < amount; i++) {
            double theta = random.nextDouble() * 2 * Math.PI; // Horizontal angle
            double phi = random.nextDouble() * Math.PI;       // Vertical angle
            double radius = 0.5 + random.nextDouble() * 0.5; // Sphere size (0.5 - 1 block radius)

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.cos(phi);
            double zOffset = radius * Math.sin(phi) * Math.sin(theta);

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


    @Unique private boolean shouldDetonateOnFire(DamageSource src){
        return (src == DamageSource.inFire ||
                src == DamageSource.onFire ||
                src == DamageSource.lava) && this.dimension != -1 &&
                !NMUtils.getIsBloodMoon() &&
                !NMUtils.getIsMobEclipsed(this) &&
                !this.isPotionActive(Potion.fireResistance.id);
    }
}
