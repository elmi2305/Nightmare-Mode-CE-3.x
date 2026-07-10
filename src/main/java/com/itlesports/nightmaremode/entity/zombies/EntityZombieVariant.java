package com.itlesports.nightmaremode.entity.zombies;

import api.achievement.AchievementEventDispatcher;
import api.entity.mob.behavior.SimpleWanderBehavior;
import api.world.difficulty.DifficultyParam;
import btw.achievement.BTWAchievementEvents;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.UrnEntity;
import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.BTWSquidEntity;
import btw.entity.mob.JungleSpiderEntity;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehaviorHostile;
import btw.entity.mob.behavior.ZombieSecondaryAttackBehavior;
import btw.entity.util.ZombieSecondaryTargetFilter;
import btw.item.BTWItems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.itlesports.nightmaremode.AITasks.EntityAILunge;
import com.itlesports.nightmaremode.AITasks.EntityAIZombieSecondaryAttackBehavior;
import com.itlesports.nightmaremode.AITasks.EntityAIZombieSecondaryTargetFilter;
import com.itlesports.nightmaremode.entity.variants.EntityBloodZombie;
import com.itlesports.nightmaremode.entity.variants.EntityShadowZombie;
import com.itlesports.nightmaremode.entity.variants.EntitySkeletonDrowned;
import com.itlesports.nightmaremode.entity.variants.EntitySkeletonMelted;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

import static com.itlesports.nightmaremode.util.NMFields.HARDMODE;
import static com.itlesports.nightmaremode.util.NMFields.POSTWITHER;

public class EntityZombieVariant
        extends EntityMob {
    private static final UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.5, 1);
    private int conversionTime;
    public int villagerClass = -1;
    private IEntitySelector targetEntitySelector;


    public EntityZombieVariant(World par1World) {
        super(par1World);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
        this.tasks.addTask(5, new EntityAIMoveThroughVillage(this, 1.0, false));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(3, new EntityAIZombieSecondaryAttackBehavior(this));
        this.tasks.addTask(6, new SimpleWanderBehavior(this, 1.0f));
        boolean hostileMode = par1World != null && (Boolean)par1World.getDifficultyParameter(DifficultyParam.CanZombiesBreakBlocks.class) != false;
        this.targetEntitySelector = new EntityAIZombieSecondaryTargetFilter(this);
        this.setZombiesAdvancedAI(hostileMode);
        NMUtils.manageEclipseChance(this,12);
        this.targetTasks.addTask(3, new EntityAILunge(this, true));
        if(this.isChild()){
            this.tasks.removeAllTasksOfClass(ZombieBreakBarricadeBehaviorHostile.class);
            this.tasks.removeAllTasksOfClass(ZombieBreakBarricadeBehavior.class);
        }
    }

    private void setZombiesAdvancedAI(boolean setAdvanced) {
        this.getNavigator().setBreakDoors(false);
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.tasks.removeAllTasksOfClass(ZombieBreakBarricadeBehaviorHostile.class);
        this.tasks.removeAllTasksOfClass(ZombieBreakBarricadeBehavior.class);
        if (setAdvanced) {
            this.tasks.addTask(1, new ZombieBreakBarricadeBehaviorHostile(this));
            this.getNavigator().setBreakDoors(true);
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false, false, null, false));
        } else {
            this.tasks.addTask(1, new ZombieBreakBarricadeBehavior(this));
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, false, null, false));
        }
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityCreature.class, 0, false, false, this.targetEntitySelector));
    }

    public int nightmareMode$getLungedAgo() {
        return lungedAgo;
    }

    public void nightmareMode$setLungedAgo(int lungedAgo) {
        this.lungedAgo = lungedAgo;
    }

    public int nightmareMode$getTimeSpentBreaking() {
        return this.timeSpentBreaking;
    }

    public void nightmareMode$setTimeSpentBreaking(int timeSpentBreaking) {
        this.timeSpentBreaking = timeSpentBreaking;
    }


    @Unique private int lungedAgo;
    @Unique private int timeSpentBreaking;

    @Unique public void onKilledBySun() {
        if (!this.worldObj.isRemote) {
            final int SKULL_SLOT = 4;

            double niteMultiplier = NMUtils.getNiteMultiplier();
            float witherSkeletonChanceModifier = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)
                    ? 0f
                    : (float) (0.2f * niteMultiplier);

            boolean isEclipse = NMUtils.getIsMobEclipsed(this);
            int progress = NMUtils.getWorldProgress();

            if (this.rand.nextInt((this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)
                    ? 2
                    : 6)) < 2 - niteMultiplier) {
                EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
                skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                int health = (int) Math.min((skeleton.getMaxHealth() - this.rand.nextInt(7) - 2 + progress * 2) * niteMultiplier,
                        skeleton.getMaxHealth() * niteMultiplier);
                skeleton.setHealth((float) health);

                // Set equipment drop chance to -1f to prevent dropping items
                for (int i = 0; i < 5; i++) {
                    skeleton.setCurrentItemOrArmor(i, this.getCurrentItemOrArmor(i));
                    skeleton.setEquipmentDropChance(i, -1f);
                }
                skeleton.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30d);

                if (this.getAttackTarget() != null) {
                    skeleton.setAttackTarget(this.getAttackTarget());
                    skeleton.entityToAttack = this.getEntityToAttack();
                }

                // Set random equipment
                if (skeleton.getCurrentItemOrArmor(SKULL_SLOT) == null && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
                    if (rand.nextInt(Math.max((int) (25 / niteMultiplier), 1)) == 0) {
                        skeleton.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    }
                }

                // Set random skull equipment
                if (skeleton.getCurrentItemOrArmor(SKULL_SLOT) == null) {
                    if (rand.nextInt(Math.max((int) (10 / niteMultiplier), 1)) == 0) {
                        ItemStack var2 = new ItemStack(Item.skull, 1, 2);
                        skeleton.setCurrentItemOrArmor(SKULL_SLOT, var2);
                    }
                }

                // Set skeleton type based on progress and areMobsEvolved
                if (progress >= 1 || NightmareMode.evolvedMobs && rand.nextFloat() <= (0.3 - witherSkeletonChanceModifier)) {
                    skeleton.setSkeletonType(1);
                }

                // Set random skeleton type for eclipse
                if (isEclipse) {
                    skeleton.setSkeletonType(this.rand.nextInt(5));
                }

                this.worldObj.spawnEntityInWorld(skeleton);
                this.setDead();
            }
        }
    }

    @Unique private boolean canEntitySeeSun(){
        if(this.worldObj.isDaytime() && !this.worldObj.isRainingAtPos((int)this.posX, (int)this.posY, (int)this.posZ) && !this.isChild() && !this.inWater){
            boolean canSeeSky = this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double) this.getEyeHeight() + 0.5f), MathHelper.floor_double(this.posZ));
            return this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? canSeeSky : canSeeSky && this.rand.nextBoolean();
        }
        return false;
    }

    @Unique private void transformToVariant(boolean wasFireDamage){
        int progress = NMUtils.getWorldProgress();
        EntitySkeleton skeleton;
        if (wasFireDamage) {
            skeleton = new EntitySkeletonMelted(this.worldObj);
            for (int i = 0; i < 10; i++) {
                double offsetX = (this.rand.nextDouble() - 0.5D) * 2.5D;
                double offsetY = this.rand.nextDouble() * this.height * 1.5D;
                double offsetZ = (this.rand.nextDouble() - 0.5D) * 2.5D;

                this.worldObj.playAuxSFX(2278, (int) (this.posX + offsetX), (int) (this.posY + offsetY), (int) (this.posZ + offsetZ), 0);
            }
        } else{
            skeleton = new EntitySkeletonDrowned(this.worldObj);
        }

        skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        double niteMultiplier = NMUtils.getNiteMultiplier();
        skeleton.setHealth((float) Math.min((skeleton.getMaxHealth() - this.rand.nextInt(7) - 2 + progress * 2) * niteMultiplier, skeleton.getMaxHealth() * niteMultiplier));
        for (int i = 0; i < 5; i++) {
            skeleton.setCurrentItemOrArmor(i, this.getCurrentItemOrArmor(i));
            skeleton.setEquipmentDropChance(i,this.equipmentDropChances[i]);
        }
        skeleton.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30d);

        if (this.hasAttackTarget()) {
            skeleton.setAttackTarget(this.getAttackTarget());
            skeleton.entityToAttack = this.getEntityToAttack();
        }
        if (!this.worldObj.isRemote) {
            this.worldObj.spawnEntityInWorld(skeleton);
        }
        this.setDead();
    }

    @Unique private static void summonSilverfish(EntityMob zombie){
        if (!(zombie instanceof EntityPigZombie) && !(zombie instanceof EntityShadowZombie)) {
            int i = zombie.rand.nextInt(5)+1;
            while(i > 0){
                if(i >= 2 && zombie.rand.nextBoolean()){
                    JungleSpiderEntity spider = new JungleSpiderEntity(zombie.worldObj);
                    spider.copyLocationAndAnglesFrom(zombie);
                    zombie.worldObj.spawnEntityInWorld(spider);
                    i -= 2;
                } else{
                    EntitySilverfish fish = new EntitySilverfish(zombie.worldObj);
                    fish.setLocationAndAngles(zombie.posX,zombie.posY,zombie.posZ, zombie.rand.nextFloat()*90, zombie.rotationPitch);
                    fish.copyLocationAndAnglesFrom(zombie);
                    zombie.worldObj.spawnEntityInWorld(fish);
                    i--;
                }
            }
            zombie.onDeath(DamageSource.generic);
            zombie.setDead();
        }
    }

    @Unique private boolean isValidForEventLoot = false;

    @Unique private static List<Integer> leatherArmorList = new ArrayList<>(4);

    @Unique private static @NotNull List<Integer> getLeatherArmor() {
        if (leatherArmorList.isEmpty()) {
            leatherArmorList.add(Item.bootsLeather.itemID);
            leatherArmorList.add(Item.legsLeather.itemID);
            leatherArmorList.add(Item.plateLeather.itemID);
            leatherArmorList.add(Item.helmetLeather.itemID);
        }
        return leatherArmorList;
    }

    @Unique
    private void summonCrystalHeadAtPos(){
        EntityZombie crystalhead = new EntityZombie(this.worldObj);
        Entity crystal = new EntityEnderCrystal(this.worldObj);
        crystal.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        crystalhead.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

        if (!this.worldObj.isRemote) {
            this.worldObj.spawnEntityInWorld(crystal);
        }
        for (int i = 0; i < 5; i++) {
            crystalhead.setEquipmentDropChance(i,-1f);
        }
        if (!this.worldObj.isRemote) {
            this.worldObj.spawnEntityInWorld(crystalhead);
        }
        crystal.mountEntity(crystalhead);

        ItemStack var1 = new ItemStack(Item.skull,1,1);
        crystalhead.setCurrentItemOrArmor(4, var1);
        crystalhead.setCurrentItemOrArmor(0, null);
        crystalhead.setCurrentItemOrArmor(1, setItemColor(new ItemStack(BTWItems.woolBoots))); // black
        crystalhead.setCurrentItemOrArmor(2, setItemColor(new ItemStack(BTWItems.woolLeggings))); // black
        crystalhead.setCurrentItemOrArmor(3, setItemColor(new ItemStack(BTWItems.woolChest))); // black

        crystalhead.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.35f);
        EntityPlayer nearestPlayer = this.worldObj.getClosestVulnerablePlayerToEntity(crystalhead, 100);
        if (nearestPlayer != null) {
            crystalhead.setAttackTarget(nearestPlayer);
            crystalhead.getMoveHelper().setMoveTo(nearestPlayer.posX,nearestPlayer.posY,nearestPlayer.posZ, 1.2f);
        }
        this.setDead();
    }




    @Unique
    private void summonShadowZombieAtPos(EntityZombie zombie){
        EntityShadowZombie shadowZombie = new EntityShadowZombie(this.worldObj);
        shadowZombie.setLocationAndAngles(zombie.posX, zombie.posY, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch);

        shadowZombie.setCurrentItemOrArmor(1, null);
        shadowZombie.setCurrentItemOrArmor(2, null);
        shadowZombie.setCurrentItemOrArmor(3, null);
        shadowZombie.setCurrentItemOrArmor(4, null);
        zombie.setDead();
        zombie.worldObj.spawnEntityInWorld(shadowZombie);
    }

    @Unique
    private ItemStack setItemColor(ItemStack item){
        NBTTagCompound var3 = item.getTagCompound();
        if (var3 == null) {
            var3 = new NBTTagCompound();
            item.setTagCompound(var3);
        }
        NBTTagCompound var4 = var3.getCompoundTag("display");
        if (!var3.hasKey("display")) {
            var3.setCompoundTag("display", var4);
        }

        var4.setInteger("color", 1052688);
        item.setTagCompound(var3);
        return item;
    }

    @Unique
    public boolean isCrystalHead(Entity par1Entity){
        return par1Entity.riddenByEntity instanceof EntityEnderCrystal;
    }

    @Unique private static List<Integer> ARMOR_DIAMOND = new ArrayList<>(4);
    @Unique private static @NotNull List<Integer> getDiamondArmor() {
        if (ARMOR_DIAMOND.isEmpty()) {
            ARMOR_DIAMOND.add(Item.bootsDiamond.itemID);
            ARMOR_DIAMOND.add(Item.legsDiamond.itemID);
            ARMOR_DIAMOND.add(Item.plateDiamond.itemID);
            ARMOR_DIAMOND.add(Item.helmetDiamond.itemID);
        }
        return ARMOR_DIAMOND;
    }



    @Unique private boolean isWearingAnyDiamondArmor(EntityMob mob){
        for(int i = 1; i < 5; i++){
            ItemStack armorStack = mob.getCurrentItemOrArmor(i);
            if(armorStack != null && getDiamondArmor().contains(armorStack.itemID)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23f);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(2.0);
        double followDistance = 16.0;
        if (this.worldObj != null) {
            followDistance *= (double)((Float)this.worldObj.getDifficultyParameter(DifficultyParam.ZombieFollowDistanceMultiplier.class)).floatValue();
        }
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(followDistance);
        if (this.worldObj != null) {
            int progress = 0;
            boolean isEclipse = NMUtils.getIsMobEclipsed(this);
            boolean isBloodMoon = NMUtils.getIsBloodMoon();

            try {
                progress = NMUtils.getWorldProgress();
            } catch (RuntimeException ignored) {}


            double niteMultiplier = NMUtils.getNiteMultiplier();
            this.getEntityAttribute(BTWAttributes.armor).setAttribute((2.0d + progress * (isBloodMoon ? 1.5 : 1) + (isEclipse ? rand.nextInt(3)+2 : 0)) * niteMultiplier);
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((isBloodMoon ? 24 : 20) + progress * (isBloodMoon ? 8 : 6) + (isEclipse ? 20 : 0)) * niteMultiplier);
            // 40 -> 46 -> 52 -> 58 eclipse
            // 24 -> 32 -> 40 -> 48 bm
            // 20 -> 26 -> 32 -> 38 normal
            this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(((double) progress / (isBloodMoon ? 5 : 10)) * niteMultiplier);

            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((3.0d + (progress > 0 ? 1 : 0)) * niteMultiplier);

            if(this.isChild()){
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.34f);
            }
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataWatcher().addObject(12, (byte)0);
        this.getDataWatcher().addObject(13, (byte)0);
        this.getDataWatcher().addObject(14, (byte)0);
    }

    @Override
    public int getTotalArmorValue() {
        int var1 = super.getTotalArmorValue();
        if (var1 > 20) {
            var1 = 20;
        }
        return var1;
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public boolean isChild() {
        return this.getDataWatcher().getWatchableObjectByte(12) == 1;
    }

    public void setChild(boolean par1) {
        this.getDataWatcher().updateObject(12, (byte)(par1 ? 1 : 0));
        if (this.worldObj != null && !this.worldObj.isRemote) {
            AttributeInstance var2 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            var2.removeModifier(babySpeedBoostModifier);
            if (par1) {
                var2.applyModifier(babySpeedBoostModifier);
            }
        }
    }

    public boolean isVillager() {
        return this.getDataWatcher().getWatchableObjectByte(13) == 1;
    }

    public void setVillager(boolean par1) {
        this.getDataWatcher().updateObject(13, (byte)(par1 ? 1 : 0));
        boolean canZombiesBreakBlocks = (Boolean)this.worldObj.getDifficultyParameter(DifficultyParam.CanZombiesBreakBlocks.class);
        boolean canVillagersBreakBlocks = (Boolean)this.worldObj.getDifficultyParameter(DifficultyParam.CanZombieVillagersBreakBlocks.class);
        this.setZombiesAdvancedAI(par1 && canVillagersBreakBlocks || canZombiesBreakBlocks);
        this.getNavigator().setBreakDoors(par1);
    }

    @Override
    public void onUpdate() {
        if(this.lungedAgo > 0){
            this.lungedAgo--;
        }
        if((this.ticksExisted & 3) == 0 && this.timeSpentBreaking > 0){
            this.timeSpentBreaking--;
        }
        if (!this.worldObj.isRemote && this.isConverting()) {
            --this.conversionTime;
            if (this.conversionTime <= 0) {
                this.convertToVillager();
            }
        }
        EntityLivingBase target = this.getAttackTarget();
        if(target != null && !(target instanceof EntityPlayer)){
            // specifically if they're targetting an animal or something else that isn't the player
            if((this.ticksExisted & 63) == 0){
                EntityPlayer p;
                if((p = this.worldObj.getClosestVulnerablePlayerToEntity(this, 20)) != null){
                    this.setAttackTarget(p);
                }
            }
        }

        if (this.worldObj != null) {
            if(isCrystalHead(this)){
                if(this.getHealth() <= 0.5f){
                    this.riddenByEntity.setDead();
                }
            }
        }
        super.onUpdate();
    }

    @Override
    protected String getLivingSound() {
        return "mob.zombie.say";
    }

    @Override
    protected String getHurtSound() {
        return "mob.zombie.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.zombie.death";
    }

    @Override
    protected void playStepSound(int par1, int par2, int par3, int par4) {
        this.playSound("mob.zombie.step", 0.15f, 1.0f);
    }

    @Override
    protected int getDropItemId() {
        if (this.isVillager()) {
            return BTWItems.rawMysteryMeat.itemID;
        }
        return Item.rottenFlesh.itemID;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        if (this.isChild()) {
            par1NBTTagCompound.setBoolean("IsBaby", true);
        }
        if (this.isVillager()) {
            par1NBTTagCompound.setBoolean("IsVillager", true);
        }
        par1NBTTagCompound.setInteger("ConversionTime", this.isConverting() ? this.conversionTime : -1);
        par1NBTTagCompound.setInteger("fcVillagerClass", this.villagerClass);
        if (this.isVillager() && !this.getHomePosition().equals(new ChunkCoordinates(0, 0, 0))) {
            par1NBTTagCompound.setIntArray("zillagerHome", new int[]{this.getHomePosition().posX, this.getHomePosition().posY, this.getHomePosition().posZ});
            par1NBTTagCompound.setInteger("zillagerHomeRange", (int)this.maximumHomeDistance);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.getBoolean("IsBaby")) {
            this.setChild(true);
        }
        if (par1NBTTagCompound.getBoolean("IsVillager")) {
            this.setVillager(true);
        }
        if (par1NBTTagCompound.hasKey("ConversionTime") && par1NBTTagCompound.getInteger("ConversionTime") > -1) {
            this.startConversion(par1NBTTagCompound.getInteger("ConversionTime"));
        }
        if (par1NBTTagCompound.hasKey("fcVillagerClass")) {
            this.villagerClass = par1NBTTagCompound.getInteger("fcVillagerClass");
        }
        if (par1NBTTagCompound.hasKey("zillagerHome")) {
            int[] homeLoc = par1NBTTagCompound.getIntArray("zillagerHome");
            int homeRange = -1;
            if (par1NBTTagCompound.hasKey("zillagerHomeRange")) {
                homeRange = par1NBTTagCompound.getInteger("zillagerHomeRange");
            }
            this.setHomeArea(homeLoc[0], homeLoc[1], homeLoc[2], homeRange);
        }
    }

    protected void startConversion(int par1) {
        this.conversionTime = par1;
        this.getDataWatcher().updateObject(14, (byte)1);
        this.removePotionEffect(Potion.weakness.id);
        this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, par1, Math.min(this.worldObj.difficultySetting - 1, 0)));
        this.worldObj.setEntityState(this, (byte)16);
    }

    @Override
    protected boolean canDespawn() {
        return !this.isConverting();
    }

    public boolean isConverting() {
        return this.getDataWatcher().getWatchableObjectByte(14) == 1;
    }

    @Override
    public float getSpeedModifier() {
        if (this.isVillager()) {
            return 1.5f;
        }
        return super.getSpeedModifier();
    }

    @Override
    public void onLivingUpdate() {
        if(this.worldObj.isRemote){
            if(this.isChild()){
                this.setSize(this.width, 1.0f);
            }
        }
        if (!this.isVillager()) {
            this.checkForCatchFireInSun();
        }
        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityAsMob(Entity attackedEntity) {
        if(NMUtils.getIsMobEclipsed(this)){
            if(rand.nextInt(3) == 0 && attackedEntity instanceof EntityLivingBase){
                ((EntityLivingBase) attackedEntity).addPotionEffect(new PotionEffect(Potion.poison.id, 40,0));
            }
        }
        return this.meleeAttack(attackedEntity);
    }

    private float modifyChanceToHaveIronTool(){
        if (this.worldObj != null) {
            if((EntityZombie)(Object)this instanceof EntityShadowZombie){
                return 0;
            }
            int worldProgress = NMUtils.getWorldProgress();
            if(worldProgress ==3){return 0.3f;}
            else {
                int bloodMoonToolBonus = NMUtils.getIsBloodMoon() ? 2 : 1;
                return (float) ((0.05F + (worldProgress * (0.03 * bloodMoonToolBonus))) * NMUtils.getNiteMultiplier());
            }
        }
        return 0.05F;
        // 0.05f -> 0.08f -> 0.11f -> 0.30f
    }

    @Override
    protected void addRandomArmor() {
        this.entityLivingAddRandomArmor();
        EntityZombie thisObj =(EntityZombie)(Object)this;
        if(thisObj instanceof EntityBloodZombie) {return;}
        if (this.rand.nextFloat() < modifyChanceToHaveIronTool()) {
            int iHeldType = this.rand.nextInt(3);
            if (iHeldType == 0) {
                if (this.worldObj!= null) {
                    int worldProgress = NMUtils.getWorldProgress();
                    double niteMultiplier = NMUtils.getNiteMultiplier();
                    if(NMUtils.getIsBloodMoon()){
                        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((Math.floor(4.0 + worldProgress * 2.5)) * niteMultiplier);
                        // 4.0 -> 6.0 -> 9.0 -> 11.0
                    } else {
                        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((Math.floor(4.0 + worldProgress * 1.75)) * niteMultiplier);
                        // 4.0 -> 5.0 -> 7.0 -> 9.0
                    }
                }
                this.setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));
            } else {
                this.setCurrentItemOrArmor(0, new ItemStack(Item.shovelIron));
            }
            this.equipmentDropChances[0] = 0.99f;
        }
        if (this.worldObj != null && !this.isVillager()) {
            boolean wasVariantSelected = false;

            int progress = NMUtils.getWorldProgress();
            boolean isBloodMoon = NMUtils.getIsBloodMoon();
            double bloodMoonModifier = isBloodMoon ? 0.5 : 1;
            boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);
            boolean isEclipse = NMUtils.getIsMobEclipsed(this);

            double niteMultiplier = NMUtils.getNiteMultiplier();

            if (!isEclipse) {
                // NON ECLIPSE
                if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 50 : 100) * bloodMoonModifier * (1 / niteMultiplier)), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    this.equipmentDropChances[0] = -1f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(3.0 + progress*1.5));
                    // 3.0 -> 4.0 -> 6.0 -> 7.0
                    wasVariantSelected = true;
                } else if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 18 : 20) * bloodMoonModifier * (1 / niteMultiplier)), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordWood));
                    this.equipmentDropChances[0] = -1f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0 + progress);
                    // 2.0 -> 3.0 -> 4.0 -> 5.0
                    wasVariantSelected = true;
                }

                if(rand.nextInt(Math.max(MathHelper.floor_double(16 * bloodMoonModifier * (1 / niteMultiplier)), 2)) == 0 && isHostile && this.posY <= 50){
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.pickaxeStone));
                    this.equipmentDropChances[0] = -1f;
                    List<Integer> leatherArmor = getLeatherArmor();
                    for (int i = 1; i <= 4; i++) {
                        if(this.getCurrentItemOrArmor(i) == null){
                            this.setCurrentItemOrArmor(i, new ItemStack(leatherArmor.get(i - 1), 1 ,rand.nextInt(EnumArmorMaterial.CLOTH.getDurability(i - 1))));
                            this.equipmentDropChances[i] = -1f;
                        }
                    }
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0d);
                    wasVariantSelected = true;
                }



                if (progress == HARDMODE || NightmareMode.evolvedMobs) {
                    if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 18 : 68) * bloodMoonModifier * (1 / niteMultiplier)), 2)) == 0) {
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.axeGold));
                        this.setCurrentItemOrArmor(4, new ItemStack(Item.helmetGold));
                        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
                        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((isHostile ? 0.34f : 0.29f) * ((niteMultiplier - 1) / 20 + 1));
                        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0d);
                        this.equipmentDropChances[0] = -1f;
                        this.equipmentDropChances[4] = -1f;
                        wasVariantSelected = true;
                    }
                } else if (rand.nextInt((int) Math.max(MathHelper.floor_double ((isHostile ? 22 : 50) * bloodMoonModifier) * (1 / niteMultiplier), 2)) == 0 && (progress > 1 || NightmareMode.evolvedMobs)) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordDiamond));
                    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(36.0);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (progress-2)*3) - (isHostile ? 0 : 4));
                    this.equipmentDropChances[0] = -1f;
                    // 12.0 -> 15.0
                    wasVariantSelected = true;
                }

                if(isHostile && (isBloodMoon || NightmareMode.evolvedMobs)){
                    if(rand.nextInt((int) Math.max(50 * (1 / niteMultiplier), 2)) == 0){
                        this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.steelSword));
                        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (progress-2)*3));
                        this.equipmentDropChances[0] = -1f;
                        wasVariantSelected = true;
                    }
                    float streakModifier = 0.0f;
                    for (int i = 1; i <= 4; i++) {
                        if(this.getCurrentItemOrArmor(i) == null){ // starts at index 1, index 0 is held item
                            if(rand.nextFloat() < 0.07f + streakModifier){
                                streakModifier += 0.01f;
                                streakModifier += (float) (niteMultiplier - 1);
                                List<Integer> diamondArmorID = getDiamondArmor();
                                this.setCurrentItemOrArmor(i, new ItemStack(diamondArmorID.get(i - 1), 1, 0));
                                this.equipmentDropChances[i] = -1;
                            }
                        }
                    }
                }
                if(isHostile && !wasVariantSelected && this.getCurrentItemOrArmor(4) == null && this.rand.nextInt(24) == 0){
                    this.setCurrentItemOrArmor(4, new ItemStack(BTWBlocks.carvedPumpkin));
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.26f);
                    this.getEntityAttribute(BTWAttributes.armor).setAttribute(2f);
                    this.equipmentDropChances[4] = -1f;
                }
            }
            else{
                // ECLIPSE
                if (rand.nextInt((int) Math.max(8 * (1 / niteMultiplier), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    this.equipmentDropChances[0] = -1f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(3.0 + progress*1.5));
                    // 3.0 -> 4.0 -> 6.0 -> 7.0
                } else if (rand.nextInt((int) Math.max(8 * (1 / niteMultiplier), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordWood));
                    this.equipmentDropChances[0] = -1f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0 + progress);
                    // 2.0 -> 3.0 -> 4.0 -> 5.0
                }

                if(rand.nextInt((int) Math.max(8 * (1 / niteMultiplier), 2)) == 0 && isHostile){
                    if (this.posY <= 45) {
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.pickaxeStone));
                        this.equipmentDropChances[0] = -1f;
                        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0d);
                    } else{
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
                        this.equipmentDropChances[0] = -1f;
                    }
                }

                if (rand.nextInt((int) Math.max(10 * (1 / niteMultiplier), 2)) == 0) {
                    ItemStack var1 = new ItemStack(Item.axeGold);
                    ItemStack var2 = new ItemStack(Item.helmetGold);
                    this.setCurrentItemOrArmor(0, var1);
                    this.setCurrentItemOrArmor(4, var2);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.34f);
                    this.equipmentDropChances[0] = -1f;
                    this.equipmentDropChances[4] = -1f;
                }
            }

            // iron tool zombies have a 99% chance to spawn with a leather helmet
            if(this.getHeldItem() != null && (this.getHeldItem().itemID == Item.swordIron.itemID || this.getHeldItem().itemID == Item.shovelIron.itemID) && this.rand.nextInt(100) != 0 && this.getCurrentItemOrArmor(4) == null){
                this.setCurrentItemOrArmor(4, new ItemStack(Item.helmetLeather));
                this.equipmentDropChances[4] = -1f;
            }
            if (this.worldObj != null) {
                int chance = 60;
                if(this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)){
                    chance = 32;
                }
                if(NMUtils.getIsBloodMoon()){chance /= 2;}
                if(NMUtils.getIsMobEclipsed(this)){chance /= 4;}

                int worldProgress = NMUtils.getWorldProgress();

                if((worldProgress >= 2 || NightmareMode.evolvedMobs) && rand.nextInt((int) Math.max(chance * (1 / niteMultiplier), 4)) == 0){
                    summonCrystalHeadAtPos();
                } else if((worldProgress >= 1 || NightmareMode.evolvedMobs) && rand.nextInt((int) Math.max(4 * (1 / niteMultiplier), 2)) == 0){
                    summonShadowZombieAtPos((EntityZombie)(Object)this);
                }
            }
        }
    }

    @Override
    public float knockbackMagnitude() {
        return this.isWeighted() ? 0.2f : super.knockbackMagnitude();
    }

    @Override
    public void onKillEntity(EntityLivingBase entityKilled) {
        if (entityKilled instanceof EntityVillager) {
            net.minecraft.src.EntityZombie newZombie = new net.minecraft.src.EntityZombie(this.worldObj);
            newZombie.copyLocationAndAnglesFrom(entityKilled);
            this.worldObj.removeEntity(entityKilled);
            newZombie.setPersistent(true);
            newZombie.setVillager(true);
            newZombie.villagerClass = ((EntityVillager)entityKilled).getProfession();
            if (entityKilled.isChild()) {
                newZombie.setChild(true);
            }
            this.worldObj.spawnEntityInWorld(newZombie);
            this.worldObj.playAuxSFXAtEntity(null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        }
    }

    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData data) {
        data = this.entityLivingOnSpawnWithEgg(data);
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.15f);
        this.addRandomArmor();
        this.enchantEquipment();
        boolean willBeBaby = this.rand.nextInt(32) == 0 && NightmareMode.moreVariants;
        if(willBeBaby && !this.worldObj.isRemote){
            this.setChild(true);
            this.setSize(this.width, 1.0f);
        }

        else if(NMEvents.SimpleEvent.SPIDER_RAIN.isActive()){
            if(this.rand.nextInt(3) != 0){
                EntitySpider spider = NMUtils.getSpiderToInitialize(this.worldObj,this);
                this.setDead();
                this.worldObj.spawnEntityInWorld(spider);
            }
        }
        else if(NMEvents.SimpleEvent.HELL.isActive()){
            if(NMUtils.initializeAndSummonHellMob(this.worldObj,this)){
                this.setDead();
            }
        }
        return data;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        return false;
    }

    protected void convertToVillager() {
        EntityVillager newVillager = EntityVillager.createVillagerFromProfession(this.worldObj, this.villagerClass);
        newVillager.copyLocationAndAnglesFrom(this);
        newVillager.onSpawnWithEgg(null);
        if (this.villagerClass == 0) {
            newVillager.setDirtyPeasant(1);
        }
        newVillager.func_82187_q();
        if (this.isChild()) {
            newVillager.setGrowingAge(-newVillager.getTicksForChildToGrow());
        }
        this.worldObj.removeEntity(this);
        this.worldObj.spawnEntityInWorld(newVillager);
        newVillager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
        this.worldObj.playAuxSFXAtEntity(null, 1017, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
    }

    @Override
    protected void modSpecificOnLivingUpdate() {
        super.modSpecificOnLivingUpdate();
        this.checkForLooseFood();
        this.checkLostFromHome();
        if (!this.worldObj.isRemote && this.isVillager() && !this.isDead && this.villagerClass < 0) {
            this.setVillager(false);
        }
    }

    public void checkLostFromHome() {
        if (!this.worldObj.isRemote && this.hasHome() && (this.dimension != this.homeDimension || this.getHomePosition().getDistanceSquared((int)this.posX, (int)this.posY, (int)this.posZ) > this.maximumHomeDistance * this.maximumHomeDistance * 16.0f)) {
            this.detachHome();
        }
    }

    @Override
    public void checkForScrollDrop() {
    }

    @Override
    protected void attackEntity(Entity attackedEntity, float fDistanceToTarget) {
        if (attackedEntity instanceof EntityAnimal) {
            if (this.attackTime <= 0 && fDistanceToTarget < 4.0f) {
                this.attackTime = 20;
                this.attackEntityAsMob(attackedEntity);
            }
        } else {
            super.attackEntity(attackedEntity, fDistanceToTarget);
        }
    }

    @Override
    protected void entityLivingDropFewItems(boolean par1, int par2) {
        if (par1 && NMUtils.getIsMobEclipsed(this) && isValidForEventLoot && (NightmareMode.totalEclipse || NMUtils.getWorldProgress() > POSTWITHER)) {
            for(int i = 0; i < (par2 * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.decayedFlesh.itemID;
            int var4 = this.rand.nextInt(3);
            if (par2 > 0) {
                var4 += this.rand.nextInt(par2 + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }

        }
        int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;

        if (bloodOrbID > 0 && par1 && isValidForEventLoot) {
            int dropCount = this.rand.nextInt(2); // 0 - 1
            if(((EntityZombie)(Object)this) instanceof EntityPigZombie){
                dropCount = this.rand.nextInt(6) == 0 ? 1 : 0;
            }

            if (this.isCrystalHead(this)) {
                dropCount += 3;
            } else if(this.getHeldItem() != null && (this.getHeldItem().itemID == Item.axeGold.itemID || this.getHeldItem().itemID == BTWItems.steelSword.itemID)){
                dropCount += 1;
            }

            for (int i = 0; i < dropCount; ++i) {
                this.dropItem(bloodOrbID, 1);
            }
        }
        if(this.isWearingAnyDiamondArmor(this)){
            this.dropItem(Item.diamond.itemID, 1);
        }
        super.entityLivingDropFewItems(par1, par2);
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
        if (!this.isEntityInvulnerable() && !this.isVillager() && this.getHealth() <= par2) {
            if (canEntitySeeSun()) {
                boolean shouldBurn =
                        !isCrystalHead(this) &&
                                !this.isImmuneToFire &&
                                this.getCurrentItemOrArmor(4) == null &&
                                !this.isInWater();

                if (shouldBurn) {
                    this.onKilledBySun();
                }
            } else if(NMUtils.getIsMobEclipsed(this) && !this.worldObj.isRemote && this.isValidForEventLoot){
                summonSilverfish(this);
            } else if(par1DamageSource == DamageSource.drown || par1DamageSource == DamageSource.lava){
                this.transformToVariant(par1DamageSource == DamageSource.lava);
                return true;
            }
        }
        if (((Boolean)this.worldObj.getDifficultyParameter(DifficultyParam.CanCreepersBreachWalls.class)).booleanValue() && par1DamageSource.isExplosion()) {
            par2 /= 2.0f;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    protected void dropHead() {
        this.entityDropItem(new ItemStack(Item.skull.itemID, 1, 2), 0.0f);
    }

    @Override
    public void spawnerInitCreature() {
        this.entityLivingOnSpawnWithEgg(null);
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.15f);
        this.enchantEquipment();
    }

    @Override
    protected float getSoundPitch() {
        if (!this.isChild() && this.isVillager()) {
            return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 0.7f;
        }
        return super.getSoundPitch();
    }

    @Override
    public boolean getCanBeHeadCrabbed(boolean bSquidInWater) {
        return !bSquidInWater && this.riddenByEntity == null && this.isEntityAlive() && !this.isChild();
    }

    @Override
    public void onHeadCrabbedBySquid(BTWSquidEntity squid) {
        this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    public double getMountedYOffset() {
        return this.height;
    }

    @Override
    protected boolean isWeightedByHeadCrab() {
        return false;
    }

    @Override
    public Entity getHeadCrabSharedAttackTarget() {
        return this.getAttackTarget();
    }

    @Override
    public boolean isImmuneToHeadCrabDamage() {
        return true;
    }

    private void checkForLooseFood() {
        EntityZombie thisObj =(EntityZombie)(Object)this;
        if(thisObj instanceof EntityBloodZombie) return;
        if (!this.worldObj.isRemote && !this.isLivingDead) {
            boolean bAte = false;
            List itemList = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(2.5, 1.0, 2.5));
            for (Object itemEntity : itemList) {
                if (itemEntity instanceof EntityItem name) {
                    ItemStack itemStack;
                    Item item;
                    if (name.delayBeforeCanPickup != 0 || name.isDead || !(item = (itemStack = name.getEntityItem()).getItem()).doZombiesConsume())
                        continue;
                    name.setDead();
                    bAte = true;
                }
            }
            if (bAte) {
                this.heal(4.0F);
                this.addPotionEffect(new PotionEffect(Potion.damageBoost.id,80,0));
                this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id,160,1));
                this.worldObj.playAuxSFX(2226, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
            }
        }
    }

    public boolean attemptToStartCure() {
        if (!this.isLivingDead && this.isVillager() && !this.isConverting()) {
            this.startConversion(this.rand.nextInt(2401) + 3600);
            return true;
        }
        return false;
    }

    @Override
    public boolean canSoulAffectEntity(UrnEntity soulEntity) {
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleHealthUpdate(byte var1) {
        super.handleHealthUpdate(var1);
        if (var1 == 16) {
            this.worldObj.playSound(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "mob.zombie.remedy", 1.0f + this.rand.nextFloat(), this.rand.nextFloat() * 0.7f + 0.3f, false);
            this.worldObj.playSound(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "mob.zombie.say", 1.0f + this.rand.nextFloat(), this.rand.nextFloat() * 0.2f + 0.5f, false);
        }
    }
}


