package com.itlesports.nightmaremode.entity.underworld;

import btw.item.BTWItems;
import net.minecraft.src.*;

public class EntitySlimeVariant extends EntityLiving implements IMob {
    private static final float[] spawnChances = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;
    private int slimeJumpDelay;

    protected float soundPitchLarge = 0.75F;
    protected float soundPitchMedium = 1.0F;
    protected float soundPitchSmall = 1.25F;
    protected int slimeType = 0;
    protected EntityPlayer targetPlayer;

    public EntitySlimeVariant(World par1World) {
        super(par1World);
        this.yOffset = 0.0F;
        this.slimeJumpDelay = this.rand.nextInt(20) + 10;
        this.setSlimeSize(this.getInitialSize());
    }

    public int getSlimeType(){
        return this.slimeType;
    }
    public void setSlimeType(int type){
        this.slimeType = type;
    }

    protected int getInitialSize() {
        return 1 << (this.rand.nextInt(3));
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)1);
    }

    public int getSlimeSize() {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Size", this.getSlimeSize() - 1);
    }

    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setSlimeSize(par1NBTTagCompound.getInteger("Size") + 1);
    }

    protected String getSlimeParticle() {
        return "slime";
    }

    protected String getJumpSound() {
        int var10000 = this.getSlimeSize();
        return "mob.slime." + (var10000 > 1 ? "big" : "small");
    }

    // returns the block ID to place beneath this entity as it walks, or 0 for none.
    protected int getTrailBlockId() {
        return 0;
    }

    protected boolean canPlaceTrailAt(int x, int y, int z) {
        if (!this.worldObj.isAirBlock(x, y, z)) {
            return false;
        }
        int belowId = this.worldObj.getBlockId(x, y - 1, z);
        Block below = Block.blocksList[belowId];
        return below != null && below.isOpaqueCube();
    }

    private void tryPlaceTrail() {
        int trailId = this.getTrailBlockId();
        if (trailId == 0) {
            return;
        }
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);

        int slimeSize = this.getSlimeSize();

        int radius = Math.max(0, MathHelper.floor_float((0.20F * slimeSize) / 2.0F));
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (this.canPlaceTrailAt(x + dx, y, z + dz)) {
                    this.worldObj.setBlock(x + dx, y, z + dz, trailId);
                }
            }
        }
    }

    public void onUpdate() {
        if (!this.worldObj.isRemote && this.worldObj.difficultySetting == 0 && this.getSlimeSize() > 0) {
            this.isDead = true;
        }

        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;
        boolean var1 = this.onGround;
        super.onUpdate();
        this.variantUpdate();
        if (this.onGround && !var1) {
            int var2 = this.getSlimeSize();

            for (int var3 = 0; var3 < var2 * 8; ++var3) {
                float var4 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                float var5 = this.rand.nextFloat() * 0.5F + 0.5F;
                float var6 = MathHelper.sin(var4) * (float)var2 * 0.5F * var5;
                float var7 = MathHelper.cos(var4) * (float)var2 * 0.5F * var5;
                this.worldObj.spawnParticle(this.getSlimeParticle(), this.posX + (double)var6, this.boundingBox.minY, this.posZ + (double)var7, 0.0D, 0.0D, 0.0D);
            }

            if (this.makesSoundOnLand()) {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.squishAmount = -0.5F;
            this.onLand();
        } else if (!this.onGround && var1) {
            this.squishAmount = 1.0F;
        }

        this.alterSquishAmount();

        if (this.worldObj.isRemote) {
            int var2 = this.getSlimeSize();
            this.setSize(0.6F * (float)var2, 0.6F * (float)var2);
        } else if (this.onGround) {
            this.tryPlaceTrail();
        }
    }

    protected void variantUpdate() {}

    protected void onLand() {}

    protected void alterSquishAmount() {
        this.squishAmount *= 0.6F;
    }

    protected int getJumpDelay() {
        return this.rand.nextInt(20) + 10;
    }

    protected EntitySlimeVariant createInstance() {
        return new EntitySlimeVariant(this.worldObj);
    }

    protected int getMinSplitCount() {
        return 2;
    }

    protected int getMaxSplitCount() {
        return 4;
    }

    protected void onSplit() {}

    public void setDead() {
        int var1 = this.getSlimeSize();
        if (!this.worldObj.isRemote && var1 > 1 && this.getHealth() <= 0.0F) {
            this.onSplit();
            int var2 = this.getMinSplitCount() + this.rand.nextInt(this.getMaxSplitCount() - this.getMinSplitCount() + 1);

//            for (int var3 = 0; var3 < var2; ++var3) {
//                float var4 = ((float)(var3 % 2) - 0.5F) * (float)var1 / 40.0F;
//                float var5 = ((float)(var3 / 2) - 0.5F) * (float)var1 / 40.0F;
//                EntitySlimeVariant var6 = this.createInstance();
//                var6.setSlimeType(this.slimeType);
//                var6.setSlimeSize(var1 / 2);
//                var6.setLocationAndAngles(this.posX + (double)var4, this.posY + 0.5D, this.posZ + (double)var5, this.rand.nextFloat() * 360.0F, 0.0F);
//                this.worldObj.spawnEntityInWorld(var6);
//            }
        }

        super.setDead();
    }

    protected int getAttackStrength() {
        return this.getSlimeSize();
    }

    protected String getHurtSound() {
        int var10000 = this.getSlimeSize();
        return "mob.slime." + (var10000 > 1 ? "big" : "small");
    }

    protected String getDeathSound() {
        int var10000 = this.getSlimeSize();
        return "mob.slime." + (var10000 > 1 ? "big" : "small");
    }

    protected int getDropItemId() {
        return this.getSlimeSize() == 1 ? Item.slimeBall.itemID : 0;
    }

    protected boolean isValidLightLevel() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) > this.rand.nextInt(32)) {
            return false;
        } else {
            int blockLightValue = this.worldObj.getBlockLightValueNoSky(x, y, z);
            if (blockLightValue > 0) {
                return false;
            } else {
                int naturalLightValue = this.worldObj.getBlockNaturalLightValue(x, y, z);
                if (this.worldObj.isThundering()) {
                    naturalLightValue = Math.min(naturalLightValue, 5);
                }

                return naturalLightValue <= this.rand.nextInt(8);
            }
        }
    }

    public int getVerticalFaceSpeed() {
        return 0;
    }

    protected boolean makesSoundOnJump() {
        return this.getSlimeSize() > 0;
    }

    public void setSlimeSize(int iSize) {
        this.dataWatcher.updateObject(16, (byte)iSize);
        if (iSize == 1) {
            this.setSize(0.6F, 0.4F);
        } else {
            this.setSize(0.4F * (float)iSize, 0.4F * (float)iSize);
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((double)(iSize * iSize));
        this.setHealth(this.getMaxHealth());
        this.experienceValue = iSize;
    }

    protected boolean makesSoundOnLand() {
        if (this.getSlimeSize() > 2 && !this.inWater) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), this.getSoundPitch() * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F));
        }

        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0D);
    }

    protected void updateEntityActionState() {
        ++this.entityAge;
        this.despawnEntity();
        double range = this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
        EntityPlayer targetPlayer = this.worldObj.getClosestVulnerablePlayerToEntity(this, range);
        if (targetPlayer != null) {
            this.targetPlayer = targetPlayer;
            this.faceEntity(targetPlayer, 10.0F, 20.0F);
        } else{
            this.targetPlayer = null;
        }

        this.isJumping = false;
        if (this.onGround) {
            --this.slimeJumpDelay;
            if (this.slimeJumpDelay <= 0) {
                this.playJumpSound();
                this.isJumping = true;
                this.slimeJumpDelay = this.getJumpDelay();
                this.moveForward = (float)this.getSlimeSize();
                if (targetPlayer != null) {
                    this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
                    this.slimeJumpDelay /= 6;
                } else {
                    this.moveStrafing = 0.0F;
                    if (this.rand.nextInt(4) == 0) {
                        this.rotationYaw = MathHelper.wrapAngleTo180_float((float)this.rand.nextInt(4) * 90.0F);
                    }
                }
            } else {
                this.moveStrafing = this.moveForward = 0.0F;
            }
        }
    }

    protected double minDistFromPlayerForDespawn() {
        return 64.0D;
    }

    public void onCollideWithPlayer(EntityPlayer player) {
        if (this.canDamagePlayer() && this.canEntityBeSeen(player) && player.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getAttackStrength())) {
            this.attackTime = 20;
            this.playSound("mob.slime.attack", 1.0F, this.getSoundPitch() * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F));
        }
    }

    protected boolean canDamagePlayer() {
        return this.isEntityAlive() && this.attackTime <= 0;
    }

    public boolean getCanSpawnHere() {
        Chunk var1 = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
        if ((this.worldObj.getWorldInfo().getTerrainType() != WorldType.FLAT || this.rand.nextInt(4) == 1) && (this.getSlimeSize() == 1 || this.worldObj.difficultySetting > 0)) {
            BiomeGenBase var2 = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
            if (var2 == BiomeGenBase.swampland && this.posY > 50.0D && this.posY < 70.0D && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor() && this.isValidLightLevel()) {
                return super.getCanSpawnHere();
            }

            if (this.rand.nextInt(10) == 0 && var1.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0D) {
                return super.getCanSpawnHere() && this.canSpawnOnBlockInSlimeChunk(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
            }
        }

        return false;
    }

    protected float getSoundVolume() {
        return 0.1F * (float)this.getSlimeSize();
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public void checkForScrollDrop() {
        if (this.getSlimeSize() == 1 && this.rand.nextInt(1000) == 0) {
            ItemStack itemstack = new ItemStack(BTWItems.arcaneScroll, 1, Enchantment.protection.effectId);
            this.entityDropItem(itemstack, 0.0F);
        }
    }

    public boolean isAffectedByMovementModifiers() {
        return false;
    }

    public void jump() {
        this.onJump();
        this.motionY = 0.525D;
        this.isAirBorne = true;
    }

    protected void onJump() {}

    public boolean canSwim() {
        return false;
    }

    public float getDefaultSlipperinessOnGround() {
        return 0.819F;
    }

    public float getSlipperinessRelativeToBlock(int iBlockID) {
        return this.getDefaultSlipperinessOnGround();
    }

    protected void fall(float fFallDistance) {}

    protected float getSoundPitch() {
        int iSize = this.getSlimeSize();
        if (iSize == 4) {
            return this.soundPitchLarge;
        } else {
            return iSize == 2 ? this.soundPitchMedium : this.soundPitchSmall;
        }
    }

    private boolean canSpawnOnBlockInSlimeChunk(int i, int j, int k) {
        int iBlockID = this.worldObj.getBlockId(i, j, k);
        return iBlockID == Block.dirt.blockID || iBlockID == Block.stone.blockID || iBlockID == Block.grass.blockID || iBlockID == Block.gravel.blockID || iBlockID == Block.sand.blockID;
    }

    private void playJumpSound() {
        if (this.makesSoundOnJump()) {
            if (!this.inWater) {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), this.getSoundPitch() * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F));
            } else {
                this.playSound("liquid.swim", 0.25F, this.getSoundPitch() * (1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F));
            }
        }
    }
}