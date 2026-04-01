package com.itlesports.nightmaremode.mixin.entity;

import api.world.WorldUtils;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import btw.entity.mob.JungleSpiderEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.AITasks.EntityAIWitchLightningStrike;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;


@Mixin(EntityWitch.class)
public abstract class EntityWitchMixin extends EntityMob {
    @Shadow private int witchAttackTimer;

    @Unique private int minionCountdown = 0;
    public EntityWitchMixin(World par1World) {
        super(par1World);
    }

    @Override
    public boolean getCanSpawnHere() {
        if (NightmareMode.magicMonsters) {
            return super.getCanSpawnHere();
        } else{
            return (int) this.posY >= this.worldObj.provider.getAverageGroundLevel() - 5 && super.getCanSpawnHere();
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,10);
    }

    @Unique private void summonMinion(EntityWitch witch, EntityPlayer player){
        if(this.timesSummoned > 3) return;
        this.timesSummoned++;
        for(int i = 0; i < 3; i++){
            if(NMUtils.getIsMobEclipsed(this)){
                int xValue = MathHelper.floor_double(this.posX) + this.rand.nextInt(-7, 8);
                int zValue = MathHelper.floor_double(this.posZ) + this.rand.nextInt(-7, 8);
                int yValue = this.worldObj.getPrecipitationHeight(MathHelper.floor_double(xValue), MathHelper.floor_double(zValue));

                if(this.posY + 5 < yValue){
                    yValue = (int) (this.posY + 2);
                    xValue = (int) this.posX;
                    zValue = (int) this.posZ;
                }

                int minionIndex = rand.nextInt(4);
                EntityLiving minion = null;
                switch(minionIndex){
                    case 0:
                        minion = new EntitySkeleton(this.worldObj);
                        ((EntitySkeleton)minion).setSkeletonType(1);
                        minion.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        minion.setCurrentItemOrArmor(0, new ItemStack(Item.bow));
                        this.worldObj.spawnEntityInWorld(minion);
                        break;
                    case 1:
                        minion = new EntitySlime(this.worldObj);
                        minion.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        ((EntitySlime)minion).setSlimeSize(this.rand.nextInt(4) * 2 + 2);
                        this.worldObj.spawnEntityInWorld(minion);
                        break;
                    case 2:
                        minion = new EntitySpider(this.worldObj);
                        EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
                        skeleton.setSkeletonType(1);
                        skeleton.setCurrentItemOrArmor(0,new ItemStack(Item.swordStone));
                        BTWSquidEntity squid = new BTWSquidEntity(this.worldObj);

                        minion.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        skeleton.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        squid.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);

                        this.worldObj.spawnEntityInWorld(squid);
                        this.worldObj.spawnEntityInWorld(skeleton);
                        this.worldObj.spawnEntityInWorld(minion);

                        squid.mountEntity(skeleton);
                        skeleton.mountEntity(minion);
                        break;
                    case 3:
                        minion = new EntityBlaze(this.worldObj);
                        minion.setLocationAndAngles(xValue, yValue, zValue, this.rotationYaw, this.rotationPitch);
                        minion.motionY = (rand.nextFloat() + 0.5) / 4;
                        this.worldObj.spawnEntityInWorld(minion);
                        break;
                }

                if (this.getAttackTarget() != null) {
                    minion.setAttackTarget(this.getAttackTarget());
                }
                minion.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, Integer.MAX_VALUE));
            } else {
                if (!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() || this.dimension == 1) {
                    EntitySilverfish tempMinion = new EntitySilverfish(this.worldObj);
                    tempMinion.copyLocationAndAnglesFrom(witch);
                    tempMinion.entityToAttack = player;
                    this.worldObj.spawnEntityInWorld(tempMinion);
                    // silverfish pre nether and in the end
                } else {
                    boolean summonCreeper = false;
                    EntitySpider tempMinion = new EntitySpider(this.worldObj);
                    if(this.rand.nextInt(4) == 0){
                        tempMinion = new JungleSpiderEntity(this.worldObj);
                    } else{
                        if(this.rand.nextInt(16) == 0){
                            summonCreeper = true;
                        }
                    }
                    tempMinion.entityToAttack = player;
                    tempMinion.copyLocationAndAnglesFrom(witch);
                    this.worldObj.spawnEntityInWorld(tempMinion);
                    if(summonCreeper){
                        EntityCreeper creeper = new EntityCreeper(this.worldObj);
                        creeper.entityToAttack = player;
                        creeper.copyLocationAndAnglesFrom(witch);
                        this.worldObj.spawnEntityInWorld(creeper);
                    }
                    break;
                }
            }
        }
    }
    @Unique private int timesSummoned = 0;


    @Unique private boolean isValidForEventLoot = false;

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean killedByPlayer, int looting, CallbackInfo ci) {
        if (!killedByPlayer) return;

        Random rand = this.rand;

        boolean eclipsed = NMUtils.getIsMobEclipsed(this);
        boolean validLoot = isValidForEventLoot;

        // 🔮 MAGIC DROPS
        if (NightmareMode.magicMonsters) {
            for (int i = 0; i < 3; i++) {
                Item drop;

                if (eclipsed) {
                    drop = getRandomWeighted(rand, ECLIPSE_ITEMS, ECLIPSE_WEIGHTS);
                } else if (this.dimension == -1) {
                    drop = getRandomWeighted(rand, NETHER_ITEMS, NETHER_WEIGHTS);
                } else {
                    drop = getRandomWeighted(rand, OVERWORLD_ITEMS, OVERWORLD_WEIGHTS);
                }

                this.dropItem(drop.itemID, 1);
            }
        }

        // 🌕 BLOOD MOON ORBS
        if (validLoot && NMUtils.getIsBloodMoon()) {
            int count = 6 + rand.nextInt(9); // 6–14
            if (looting > 0) count += rand.nextInt(looting + 1);

            for (int i = 0; i < count; i++) {
                this.dropItem(NMItems.bloodOrb.itemID, 1);
            }
        }

        // 🌑 ECLIPSE BONUS DROPS
        if (eclipsed && validLoot) {

            // Darksun fragments (improved chance logic)
            int attempts = looting * 2 + 1;
            for (int i = 0; i < attempts; i++) {
                if (rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (rand.nextBoolean()) break;
                }
            }

            // Void membrane drops
            int count = 1 + rand.nextInt(5);
            if (looting > 0) count += rand.nextInt(looting + 1);

            for (int i = 0; i < count; i++) {
                if (rand.nextInt(3) != 0) { // cleaner than continue
                    this.dropItem(NMItems.voidMembrane.itemID, 1);
                }
            }
        }
    }


    @Unique
    private static Item getRandomWeighted(Random rand, Item[] items, int[] weights) {
        int total = 0;
        for (int w : weights) total += w;

        int r = rand.nextInt(total);

        for (int i = 0; i < items.length; i++) {
            r -= weights[i];
            if (r < 0) return items[i];
        }

        return items[0]; // fallback
    }
    @Unique
    private static final Item[] OVERWORLD_ITEMS = {
            BTWItems.nitre, Item.rottenFlesh, Item.spiderEye, Item.fireballCharge,
            Item.clay, Item.enderPearl, BTWItems.witchWart, BTWItems.mysteriousGland,
            NMItems.calamari, Item.bone, Item.slimeBall, Item.potion,
            Item.fermentedSpiderEye, Item.dyePowder, Item.skull, Item.arrow,
            Item.bow, Item.plateIron, Item.bootsIron, Item.legsIron,
            Item.helmetIron, BTWItems.creeperOysters, Item.silk
    };

    @Unique
    private static final int[] OVERWORLD_WEIGHTS = {
            18, 20, 12, 4,
            13, 4, 8, 7,
            9, 15, 7, 9,
            6, 9, 3, 12,
            4, 2, 3, 2,
            4, 14, 14
    };

    @Unique
    private static final Item[] NETHER_ITEMS = {
            Item.blazeRod, Item.magmaCream, Item.ghastTear,
            Item.goldNugget, Item.swordGold, Item.plateGold, Item.legsGold
    };

    @Unique
    private static final int[] NETHER_WEIGHTS = {
            12, 12, 5,
            5, 1, 1, 1
    };
    @Unique
    private static final Item[] ECLIPSE_ITEMS = {
            NMItems.magicFeather, NMItems.creeperChop, NMItems.magicArrow,
            NMItems.bloodMilk, NMItems.calamari, NMItems.silverLump,
            BTWItems.soulFlux, NMItems.voidMembrane, NMItems.voidSack,
            NMItems.charredFlesh, NMItems.ghastTentacle, NMItems.creeperTear,
            NMItems.spiderFangs, NMItems.speedCoil, NMItems.waterRod,
            NMItems.elementalRod, NMItems.decayedFlesh
    };

    @Unique
    private static final int[] ECLIPSE_WEIGHTS = {
            5, 5, 5,
            2, 6, 7,
            9, 4, 5,
            3, 4, 3,
            4, 3, 9,
            2, 4
    };

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addWitchSpecificAITasks(World par1World, CallbackInfo ci){
        this.targetTasks.addTask(1, new EntityAIWitchLightningStrike(this));
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        if (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            int progress = NMUtils.getWorldProgress();
            double bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.5 : 1;
            int eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 30 : 0;

            double niteMultiplier = NMUtils.getNiteMultiplier();
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((20.0 + progress * 4) * bloodMoonModifier + eclipseModifier) * niteMultiplier);
            // 20 -> 24 -> 28 -> 32
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.4 * (1 + (niteMultiplier - 1) / 20));
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWitch;setAggressive(Z)V",ordinal = 1,shift = At.Shift.AFTER))
    private void healFast(CallbackInfo ci){
        if (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            this.witchAttackTimer = NMUtils.getIsMobEclipsed(this) ? 5 : 10;
        }
    }
    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void chanceToDropSpecialItems(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        for(int i = 0; i < 1 + iLootingModifier * 2; i++){
            if(this.rand.nextInt(Math.max(6 - iLootingModifier, 2)) == 0){
                this.dropItem(Item.expBottle.itemID, 1);
            }
        }

        if(this.rand.nextInt(NMUtils.getIsBloodMoon() ? 2 : 6) == 0){
            this.dropItem(BTWItems.witchWart.itemID, this.rand.nextInt(2));
        }
    }

    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 8.0f))
    private float increaseThrowingRange(float constant){
        return this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 16f : constant;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 8.0f))
    private float lookAtPlayer(float constant){
        return this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 16f : constant;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 10.0f))
    private float increaseThrowingRange1(float constant){
        return this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 20f : constant;
    }

    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 0.75f))
    private float modifyPotionVelocity(float constant){
        if(!this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)){
            return constant;
        }
        Entity target = this.getAttackTarget();
        if (target != null) {
            double distSq = this.getDistanceSqToEntity(target);

            if (distSq > 225) {
                float dist = (float)Math.sqrt(distSq);
                return 0.6f + dist / 27f;
            }

            if (distSq > 64) {
                float dist = (float)Math.sqrt(distSq);
                return 0.6f + dist / 30f;
            }

            if (distSq < 25) {
                return 0.6f;
            }

            return 0.75f;
        }
        return constant;
    }

    @Inject(method = "attackEntityWithRangedAttack", at = @At("TAIL"))
    private void chanceToTeleport(EntityLivingBase par1EntityLivingBase, float par2, CallbackInfo ci){
        if(this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) && this.getAttackTarget() instanceof EntityPlayer targetPlayer && getDistanceSqToEntity(targetPlayer)>256){
            EntityEnderPearl pearl = new EntityEnderPearl(this.worldObj, this);
            this.worldObj.spawnEntityInWorld(pearl);
            double var1 = targetPlayer.posX - this.posX;
            double var2 = targetPlayer.posZ - this.posZ;
            Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
            vector.normalize();
            pearl.motionX = vector.xCoord * 0.1;
            pearl.motionZ = vector.zCoord * 0.1;
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageMinionSummons(CallbackInfo ci){
        EntityWitch thisObj = (EntityWitch)(Object)this;

        if(NMUtils.getIsBloodMoon()) return;

        this.minionCountdown += thisObj.rand.nextInt(3 + NMUtils.getWorldProgress());
        if(this.minionCountdown > (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 600 : 1600) - (NMUtils.getIsMobEclipsed(this) ? 300 : 0)){
            if(thisObj.getAttackTarget() instanceof EntityPlayer player && !player.capabilities.isCreativeMode){
                this.summonMinion(thisObj, player);
                this.minionCountdown = this.rand.nextInt(15) * (10 - (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 0 : 10));
                // this formula produces 3 silverfish around every 300 ticks spent targeting the player
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(this.rand.nextInt(4) == 0 && NMUtils.getIsMobEclipsed(this)){
            int xOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(3)+3);
            int zOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(3)+3);

            int xValue = MathHelper.floor_double(this.posX) + xOffset;
            int zValue = MathHelper.floor_double(this.posZ) + zOffset;
            int yValue = MathHelper.floor_double(this.posY) + this.rand.nextInt(-2,2);
            if(this.worldObj.getBlockId(xValue,yValue,zValue) != 0){
                yValue = this.worldObj.getPrecipitationHeight(xValue,zValue);
                if (Math.abs(yValue - this.posY) > 5){
                    yValue = (int) this.posY;
                }
            }
            this.setPositionAndUpdate(xValue,yValue,zValue);
        }
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
        return super.attackEntityFrom(par1DamageSource, par2);
    }
}
