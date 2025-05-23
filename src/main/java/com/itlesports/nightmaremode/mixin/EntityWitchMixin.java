package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.AITasks.EntityAIWitchLightningStrike;
import com.itlesports.nightmaremode.NightmareUtils;
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
        NightmareUtils.manageEclipseChance(this,10);
    }

    @Unique private void summonMinion(EntityWitch witch, EntityPlayer player){
        for(int i = 0; i < 3; i++){
            if(NightmareUtils.getIsMobEclipsed(this)){
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
                if (NightmareUtils.getIsBloodMoon()) {
                    EntityCreeper tempMinion = new EntityCreeper(this.worldObj);
                    tempMinion.copyLocationAndAnglesFrom(witch);
                    tempMinion.entityToAttack = player;
                    this.worldObj.spawnEntityInWorld(tempMinion);
                    break;
                }
                if (!WorldUtils.gameProgressHasNetherBeenAccessedServerOnly() || this.dimension == 1) {
                    EntitySilverfish tempMinion = new EntitySilverfish(this.worldObj);
                    tempMinion.copyLocationAndAnglesFrom(witch);
                    tempMinion.entityToAttack = player;
                    this.worldObj.spawnEntityInWorld(tempMinion);
                    // silverfish pre nether and in the end
                } else {
                    EntitySpider tempMinion = new EntitySpider(this.worldObj);
                    tempMinion.entityToAttack = player;
                    tempMinion.copyLocationAndAnglesFrom(witch);
                    this.worldObj.spawnEntityInWorld(tempMinion);
                    break;
                }
            }
        }
    }


    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if (bKilledByPlayer) {
            if (NightmareMode.magicMonsters) {
                Item itemToDrop = null;
                for (int i = 0; i < 3; i++) {
                    int j = this.rand.nextInt(163);
                    if (this.dimension == 0) {
                        itemToDrop = switch (j) {
                            case  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12                  -> BTWItems.nitre;           // 18
                            case 13, 14, 15, 16, 17, 18, 19, 20, 21, 22                              -> Item.rottenFlesh;         // 20
                            case 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34                      -> Item.spiderEye;           // 12
                            case 35, 36, 37, 38                                                      -> Item.fireballCharge;      // 4
                            case 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51                  -> Item.clay;                // 13
                            case 52, 53, 54, 55                                                      -> Item.enderPearl;          // 4
                            case 56, 57, 58, 59, 60, 61, 62, 63                                      -> BTWItems.witchWart;       // 8
                            case 64, 65, 66, 67, 68, 69, 70                                          -> BTWItems.mysteriousGland; // 7
                            case 71, 72, 73, 74, 75, 76, 77, 78, 79                                  -> NMItems.calamari;         // 9
                            case 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94          -> Item.bone;                // 15
                            case 95, 96, 97, 98, 99, 100, 101                                        -> Item.slimeBall;           // 7
                            case 102, 103, 104, 105, 106, 107, 108, 109, 110                         -> Item.potion;              // 9
                            case 111, 112, 113, 114, 115, 116                                        -> Item.fermentedSpiderEye;  // 6
                            case 117, 118, 119, 120, 121, 122, 123, 124, 125                         -> Item.dyePowder;           // 9
                            case 126, 127, 128                                                       -> Item.skull;               // 3
                            case 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140          -> Item.arrow;               // 12
                            case 141, 142, 143, 144                                                  -> Item.bow;                 // 4
                            case 145                                                                 -> Item.plateIron;           // 2
                            case 146                                                                 -> Item.bootsIron;           // 3
                            case 147                                                                 -> Item.legsIron;            // 2
                            case 148                                                                 -> Item.helmetIron;          // 4
                            case 149, 150, 151, 152, 153                                             -> BTWItems.creeperOysters;  // 14
                            case 154, 155, 156, 157, 158, 159, 160, 161, 162                         -> Item.silk;                // 14
                            default -> BTWItems.witchWart;
                        };
                    } else if(this.dimension == -1){
                        j = this.rand.nextInt(37);
                        itemToDrop = switch (j) {
                            case 0, 1, 2, 3, 4, 5, 6, 29, 30, 31, 32, 33     -> Item.blazeRod;
                            case 7, 8, 9, 10, 11, 12, 13, 24, 25, 26, 27, 28 -> Item.magmaCream;
                            case 14, 15, 16, 17, 18                          -> Item.ghastTear;
                            case 19, 20, 21, 22, 23                          -> Item.goldNugget;
                            case 34                                          -> Item.swordGold;
                            case 35                                          -> Item.plateGold;
                            case 36                                          -> Item.legsGold;
                            default -> BTWItems.witchWart;
                        };
                    }
                    if(NightmareUtils.getIsMobEclipsed(this)){
                        j = this.rand.nextInt(80);
                        itemToDrop = switch (j) {
                            case  0,  1,  2,  3,  4  -> NMItems.magicFeather;
                            case  5,  6,  7,  8,  9  -> NMItems.creeperChop;
                            case 10, 11, 12, 13, 14  -> NMItems.magicArrow;
                            case 15, 16               -> NMItems.bloodMilk;
                            case 17, 18, 19, 20, 21, 22 -> NMItems.calamari;
                            case 23, 24, 25, 26, 27, 28, 29 -> NMItems.silverLump;
                            case 30, 31, 32, 33, 34, 35, 36, 37, 38 -> BTWItems.soulFlux;
                            case 39, 40, 41, 42      -> NMItems.voidMembrane;
                            case 43, 44, 45, 46, 47  -> NMItems.voidSack;
                            case 48, 49, 50          -> NMItems.charredFlesh;
                            case 51, 52, 53, 54      -> NMItems.ghastTentacle;
                            case 55, 56, 57          -> NMItems.creeperTear;
                            case 58, 59, 60, 61      -> NMItems.spiderFangs;
                            case 62, 63, 64          -> NMItems.greg;
                            case 65, 66, 67, 68, 69, 70, 71, 72, 73 -> NMItems.waterRod;
                            case 74, 75              -> NMItems.elementalRod;
                            case 76, 77, 78, 79      -> NMItems.decayedFlesh;
                            default -> Item.fishRaw;  // Fallback in case of unexpected input
                        };
                    }


                    if (itemToDrop != null) {
                        this.dropItem(itemToDrop.itemID, 1);
                    }
                }
            }



            int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
            if (bloodOrbID > 0) {
                int var4 = this.rand.nextInt(9)+4;
                // 4 - 12
                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    this.dropItem(bloodOrbID, 1);
                }
            }
            if (NightmareUtils.getIsMobEclipsed(this)) {
                for(int i = 0; i < (iLootingModifier * 2) + 1; i++) {
                    if (this.rand.nextInt(8) == 0) {
                        this.dropItem(NMItems.darksunFragment.itemID, 1);
                        if (this.rand.nextBoolean()) {
                            break;
                        }
                    }
                }

                int itemID = NMItems.voidMembrane.itemID;

                int var4 = this.rand.nextInt(3);
                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    if(this.rand.nextInt(3) == 0) continue;
                    this.dropItem(itemID, 1);
                }
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addWitchSpecificAITasks(World par1World, CallbackInfo ci){
        this.targetTasks.addTask(1, new EntityAIWitchLightningStrike(this));
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.5 : 1;
            int eclipseModifier = NightmareUtils.getIsMobEclipsed(this) ? 30 : 0;

            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((20.0 + progress * 4) * bloodMoonModifier + eclipseModifier) * NightmareUtils.getNiteMultiplier());
            // 20 -> 24 -> 28 -> 32
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.4 * (1 + (NightmareUtils.getNiteMultiplier() - 1) / 20));
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityWitch;setAggressive(Z)V",ordinal = 1,shift = At.Shift.AFTER))
    private void healFast(CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            this.witchAttackTimer = NightmareUtils.getIsMobEclipsed(this) ? 5 : 10;
        }
    }
    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void chanceToDropSpecialItems(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if(this.rand.nextInt(Math.max(24 - iLootingModifier, 2)) == 0){
            this.dropItem(Item.expBottle.itemID, 1);
        }

        if(this.rand.nextInt(NightmareUtils.getIsBloodMoon() ? 2 : 6) == 0){
            this.dropItem(BTWItems.witchWart.itemID, this.rand.nextInt(2));
        }
    }

    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 8.0f))
    private float increaseThrowingRange(float constant){
        return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 16f : constant;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 8.0f))
    private float lookAtPlayer(float constant){
        return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 16f : constant;
    }

    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 10.0f))
    private float increaseThrowingRange1(float constant){
        return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 20f : constant;
    }

    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 0.75f))
    private float modifyPotionVelocity(float constant){
        if(this.worldObj.getDifficulty() != Difficulties.HOSTILE){
            return constant;
        }
        if (this.getAttackTarget() != null) {
            double dist = this.getDistanceSqToEntity(this.getAttackTarget());
            float velocityTuning = 0;
            if(Math.sqrt(dist) > 15){
                velocityTuning = (float)Math.sqrt(dist)/27;
                return 0.6f+velocityTuning;
            }
            if (Math.sqrt(dist) > 8) {
                velocityTuning = (float)Math.sqrt(dist)/30;
                return 0.6f+velocityTuning;
            }
            if (Math.sqrt(dist) < 5){
                return 0.6f;
            }
            return 0.75f +velocityTuning;
        }
        return constant;
    }

    @Inject(method = "attackEntityWithRangedAttack", at = @At("TAIL"))
    private void chanceToTeleport(EntityLivingBase par1EntityLivingBase, float par2, CallbackInfo ci){
        if(this.worldObj.getDifficulty() == Difficulties.HOSTILE && this.getAttackTarget() instanceof EntityPlayer targetPlayer && getDistanceSqToEntity(targetPlayer)>256){
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

        this.minionCountdown += thisObj.rand.nextInt(3 + NightmareUtils.getWorldProgress(this.worldObj));
        if(this.minionCountdown > (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 600 : 1600) - (NightmareUtils.getIsMobEclipsed(this) ? 300 : 0)){
            if(thisObj.getAttackTarget() instanceof EntityPlayer player && !player.capabilities.isCreativeMode){
                this.summonMinion(thisObj, player);
                this.minionCountdown = this.rand.nextInt(15) * (10 - (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 10));
                // this formula produces 3 silverfish around every 300 ticks spent targeting the player
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(this.rand.nextInt(4) == 0 && NightmareUtils.getIsMobEclipsed(this)){
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
        return super.attackEntityFrom(par1DamageSource, par2);
    }
}
