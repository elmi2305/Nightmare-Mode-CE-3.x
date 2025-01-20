package com.itlesports.nightmaremode;

import btw.block.BTWBlocks;
import btw.entity.LightningBoltEntity;
import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;

import java.util.List;

public class EntityBloodWither extends EntityWither {
    public EntityBloodWither(World world) {
        super(world);
        this.experienceValue = 4954;
    }
    private final float[] headPitch = new float[2]; // Represents the pitch (up/down angle) of the heads.
    private final float[] headYaw = new float[2]; // Represents the yaw (left/right angle) of the heads
    private final float[] previousHeadPitch = new float[2]; // Previous tick's pitch values for the heads
    private final float[] previousHeadYaw = new float[2]; // Previous tick's yaw values for the heads
    private final int[] headAttackCounts = new int[2]; // Counters for the number of attacks by the heads

    private int attackCycle; // used to be headAttackCooldowns. repurposed to track the cycle an attack is in. eg. the lightning attack has 3 cycles, prep, telegraph and fire
    private int shieldRegenCooldown; // Cooldown timer for regenerating the shield
    private int witherAttack; // controls which AI attack the wither should currently use
    private int passivityDuration = -1; // decides how long the wither should stay passive
    private double verticalOffset = 5.0; // the height difference between the player and the wither
    private boolean activity = false; // whether the wither should go to the sky and be passive or not
    private EntityLiving trackedEntity; // the entity we are tracking. usually a mob summoned by the wither
    private EntityPlayer playerTarget; // the player we are targetting
    private double targetX; // target's X position
    private double targetZ; // target's Z position
    private double deltaX; // target's deltaX movement
    private double deltaZ; // target's deltaZ movement
    private double[] lightningX = {0,0,0,0,0,0,0,0,0,0}; // lightning X target
    private double[] lightningZ = {0,0,0,0,0,0,0,0,0,0}; // lightning Z target
    private int[] origin; // coordinates of the origin, the center of the platform, where the wither spawned
    private boolean shouldFollowPlayer; // determines if a wither attack requires it to follow the player, to ensure aggro at all times
    private static final IEntitySelector attackEntitySelector = new EntityWitherAttackFilter();

    public boolean getActivity(){return this.activity;}

    private void setTargetY(double par1){
        this.verticalOffset = par1;
    }

    private void manageWitherPassivity(boolean isTrackingEntity){

        if (isTrackingEntity) {
            this.activity = false;
            this.setTargetY(10d);
            if(this.trackedEntity.isDead){
                this.engageWither();
            }
            this.setInvisible(true);
        } else {
            if (this.passivityDuration > 0) {
                this.setWitherPassiveFor(this.passivityDuration - 1);
                this.activity = false;
                this.setInvisible(true);
                this.setTargetY(10d);
            } else {
                this.engageWither();
            }
        }
    }

    private void engageWither(){
        this.witherAttack += 1;
        this.passivityDuration = -1;
        this.activity = true;
        this.setInvisible(false);
        this.setTargetY(5d);
    }
    private void setWitherPassiveFor(int par1){
        this.passivityDuration = par1;
    }

    @Override
    protected boolean isAIEnabled() {
        return this.activity;
    }

    private void updatePlayerMovementDeltas(EntityPlayer player){
        if(player.ticksExisted % 2 == 0){
            this.targetX = player.posX;
            this.targetZ = player.posZ;
        } else {
            this.deltaX = player.posX - this.targetX;
            this.deltaZ = player.posZ - this.targetZ;
        }
        // updates this.deltaX and this.deltaZ (which refer to the player's movement deltas) every 2nd tick. the values are reliable and can be used for predictive AI
    }



    public float func_82210_r(int par1) {
        return this.headPitch[par1];
    }
    public float func_82207_a(int par1) {
        return this.headYaw[par1];
    }


    private void executeAttack(int index, int delayBetweenAttacks){
        EntityPlayer player = this.playerTarget;
        if (player != null) {
            this.updatePlayerMovementDeltas(player);
            double x,y,z;
            boolean shouldCheck = this.ticksExisted % delayBetweenAttacks == 0 || delayBetweenAttacks == -1;
            if (shouldCheck) {
                Vec3 deltaMovement = Vec3.createVectorHelper(this.deltaX, 0, this.deltaZ);
                deltaMovement.normalize();

                switch (index){
                    case 0: // suffocation attack
                        for (int i = -1; i < 2; i++) {
                            for (int j = -1; j < 2; j++) {
                                x = player.posX + i + this.rand.nextInt(4);
                                y = player.posY + 20;
                                z = player.posZ + j + this.rand.nextInt(4);
                                // time to fall (in ticks) is sqrt(2 * 400 * y / 16), which is approximately 5.31.62277660168379 in this case

                                this.worldObj.setBlock((int)(x + deltaMovement.xCoord * 31.62277660168379), (int)y, (int)(z + deltaMovement.zCoord * 31.62277660168379), this.rand.nextBoolean() ? Block.sand.blockID : Block.gravel.blockID);
                                this.worldObj.setBlock((int)(x + deltaMovement.xCoord * 31.62277660168379), (int)y + 1, (int)(z + deltaMovement.zCoord * 31.62277660168379), this.rand.nextBoolean() ? Block.sand.blockID : Block.gravel.blockID);
                            }
                        }
                        break;
                    case 1: // tnt rain
                        x = player.posX + rand.nextInt(6);
                        y =  8 + rand.nextInt(10);
                        z = player.posZ + rand.nextInt(6);
                        int fuse = (int) Math.sqrt(800 * y / 16);

                        EntityTNTPrimed missile = new EntityTNTPrimed(this.worldObj,x + deltaMovement.xCoord * fuse,player.posY + y, z + deltaMovement.zCoord * fuse);
                        missile.fuse = fuse;
                        this.worldObj.spawnEntityInWorld(missile);
                        break;
                    case 2:
                        int lightningSpread = 5;
                        this.attackCycle += 1;
                        // lightning strikes
                        for (int i = 0; i < 10; i++) {
                            if(this.lightningX[i] == 0 || this.lightningZ[i] == 0){

                                this.lightningX[i] = this.origin[0] + (this.rand.nextBoolean() ? -1 : 1) * (3 + this.rand.nextInt(25));
                                this.lightningZ[i] = this.origin[2] + (this.rand.nextBoolean() ? -1 : 1) * (3 + this.rand.nextInt(25));
                            } else {
                                if (this.attackCycle % 4 == 3) {
                                    Entity lightningbolt = new LightningBoltEntity(this.worldObj, this.lightningX[i], 200, this.lightningZ[i]);
                                    this.worldObj.addWeatherEffect(lightningbolt);
                                } else if(this.attackCycle % 4 == 0) {
                                    Entity scatteredBolt0 = new LightningBoltEntity(this.worldObj, this.lightningX[i] + lightningSpread, 200, this.lightningZ[i] + lightningSpread);
                                    Entity scatteredBolt1 = new LightningBoltEntity(this.worldObj, this.lightningX[i] + lightningSpread, 200, this.lightningZ[i] - lightningSpread);
                                    Entity scatteredBolt2 = new LightningBoltEntity(this.worldObj, this.lightningX[i] - lightningSpread, 200, this.lightningZ[i] + lightningSpread);
                                    Entity scatteredBolt3 = new LightningBoltEntity(this.worldObj, this.lightningX[i] - lightningSpread, 200, this.lightningZ[i] - lightningSpread);
                                    this.worldObj.addWeatherEffect(scatteredBolt0);
                                    this.worldObj.addWeatherEffect(scatteredBolt1);
                                    this.worldObj.addWeatherEffect(scatteredBolt2);
                                    this.worldObj.addWeatherEffect(scatteredBolt3);
                                    this.lightningX[i] = this.lightningZ[i] = 0;
                                } else {
                                    for (int j = 2; j < 10; j++) {
                                        this.worldObj.newExplosion(this, this.lightningX[i], 205 + (j * 2), this.lightningZ[i], 1.75f, false, false);
                                    }
                                }
                            }
                        }
                        break;
                    case 8:
                        if (this.trackedEntity == null) {
                            EntityDragon dragon = new EntityDragon(this.worldObj);
                            dragon.setPositionAndUpdate(this.origin[0],this.origin[1] + 30, this.origin[2]);
                            this.trackedEntity = dragon;
                            this.worldObj.spawnEntityInWorld(dragon);
                        }
                        break;
                }
            }
        }
    }


    @Override
    public void onLivingUpdate() {
        int particleIndex;
        int headIndex;
        double targetDistanceSquared;
        double deltaX;
        double deltaZ;
        EntityPlayer primaryTarget;

        this.motionY *= 0.6f;

        // Adjust motion towards the primary target
        if(this.playerTarget == null){
            this.playerTarget = this.worldObj.getClosestVulnerablePlayerToEntity(this,40);
        }
        if (!this.worldObj.isRemote && this.playerTarget != null) {
            primaryTarget = this.playerTarget;
            if(this.witherAttack == 600) {

                this.executeAttack(8,-1);
//                if (this.trackedEntity == null) {
//                    EntityDragon dragon = new EntityDragon(this.worldObj);
//                    dragon.setPositionAndUpdate(this.origin[0],this.origin[1] + 30, this.origin[2]);
//                    this.trackedEntity = dragon;
//                    this.worldObj.spawnEntityInWorld(dragon);
//                }
                this.manageWitherPassivity(true);

                // suffocation attack
//                primaryTarget.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200,0));
//                this.executeAttack(0, 8, false);
            }
            else if(this.witherAttack == 900){
                // tnt rain

                if (this.passivityDuration == -1) {
                    this.setWitherPassiveFor(300);
                }
                this.manageWitherPassivity(false);
                this.executeAttack(1, 12);
            }
            else if(this.witherAttack == 1400){
                if (this.passivityDuration == -1) {
                    this.setWitherPassiveFor(600);
                }
                this.manageWitherPassivity(false);
                this.executeAttack(2, 20 + 1);
            }
            else if(this.witherAttack == 2300){
                // dragon summon
                this.executeAttack(8,60);
//                if (this.trackedEntity == null) {
//                    EntityDragon dragon = new EntityDragon(this.worldObj);
//                    dragon.setPositionAndUpdate(this.origin[0],this.origin[1] + 30, this.origin[2]);
//                    this.trackedEntity = dragon;
//                    this.worldObj.spawnEntityInWorld(dragon);
//                }
                this.manageWitherPassivity(true);
            }
            else if(this.witherAttack == 2500){

            }
            else{
                this.witherAttack = Math.min(this.witherAttack + 1, 2000); // counter goes up from 0 to 2000
                this.activity = true;
                this.attackCycle = 0;
            }


            if (this.getHealthTimer() <= 0) {
                if (this.posY < primaryTarget.posY || (!this.isArmored() && this.posY < primaryTarget.posY + this.verticalOffset)) {
                    if (this.motionY < 0.0) {
                        this.motionY = 0.0;
                    }
                    this.motionY += (0.5 - this.motionY) * 0.6f;
                }
                deltaX = primaryTarget.posX - this.posX;
                deltaZ = primaryTarget.posZ - this.posZ;
                targetDistanceSquared = deltaX * deltaX + deltaZ * deltaZ;
                if (targetDistanceSquared > 9.0 && this.activity) {
                    double distance = MathHelper.sqrt_double(targetDistanceSquared);
                    this.motionX += (deltaX / distance * 0.5 - this.motionX) * 0.6f;
                    this.motionZ += (deltaZ / distance * 0.5 - this.motionZ) * 0.6f;
                }
                if(targetDistanceSquared > 1000 && !this.activity && this.trackedEntity == null){
                    this.setPositionAndUpdate(primaryTarget.posX,210, primaryTarget.posZ);
                }
                if(this.trackedEntity != null){
                    deltaX = this.origin[0] - this.posX;
                    deltaZ = this.origin[2] - this.posZ;
                    targetDistanceSquared = deltaX * deltaX + deltaZ * deltaZ;
                    if (targetDistanceSquared > 4.0) {
                        double distance = MathHelper.sqrt_double(targetDistanceSquared);
                        this.motionX += (deltaX / distance * 0.5 - this.motionX) * 0.2f;
                        this.motionZ += (deltaZ / distance * 0.5 - this.motionZ) * 0.2f;
                    }
                }
            }
        }

        // Adjust rotation based on movement
        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05f) {
            this.rotationYaw = (float) Math.atan2(this.motionZ, this.motionX) * 57.295776f - 90.0f;
        }

        super.onLivingUpdate();

        // Update head tracking for each head
        for (headIndex = 0; headIndex < 2; ++headIndex) {
            this.previousHeadYaw[headIndex] = this.headYaw[headIndex];
            this.previousHeadPitch[headIndex] = this.headPitch[headIndex];
            // cato
        }

        for (headIndex = 0; headIndex < 2; ++headIndex) {
            int targetId = this.getWatchedTargetId(headIndex + 1);
            Entity headTarget = targetId > 0 ? this.worldObj.getEntityByID(targetId) : null;

            if (headTarget != null) {
                double headTargetX = this.getHeadX(headIndex + 1);
                double headTargetY = this.getHeadY(headIndex + 1);
                double headTargetZ = this.getHeadZ(headIndex + 1);

                double deltaTargetX = headTarget.posX - headTargetX;
                double deltaTargetY = headTarget.posY + headTarget.getEyeHeight() - headTargetY;
                double deltaTargetZ = headTarget.posZ - headTargetZ;

                double horizontalDistance = MathHelper.sqrt_double(deltaTargetX * deltaTargetX + deltaTargetZ * deltaTargetZ);
                float yaw = (float) (Math.atan2(deltaTargetZ, deltaTargetX) * 180.0 / Math.PI) - 90.0f;
                float pitch = (float) -(Math.atan2(deltaTargetY, horizontalDistance) * 180.0 / Math.PI);

                this.headPitch[headIndex] = this.clampAngle(this.headPitch[headIndex], pitch, 40.0f);
                this.headYaw[headIndex] = this.clampAngle(this.headYaw[headIndex], yaw, 10.0f);
            } else {
                this.headYaw[headIndex] = this.clampAngle(this.headYaw[headIndex], this.renderYawOffset, 10.0f);
            }
        }

        // Generate particles based on state
        boolean isShielded = this.isArmored();
        for (particleIndex = 0; particleIndex < 3; ++particleIndex) {
            double headX = this.getHeadX(particleIndex);
            double headY = this.getHeadY(particleIndex);
            double headZ = this.getHeadZ(particleIndex);

            this.worldObj.spawnParticle("smoke", headX + this.rand.nextGaussian() * 0.3, headY + this.rand.nextGaussian() * 0.3, headZ + this.rand.nextGaussian() * 0.3, 0.0, 0.0, 0.0);

            if (isShielded && this.rand.nextInt(4) == 0) {
                this.worldObj.spawnParticle("mobSpell", headX + this.rand.nextGaussian() * 0.3, headY + this.rand.nextGaussian() * 0.3, headZ + this.rand.nextGaussian() * 0.3, 0.7f, 0.7f, 0.5);
            }
        }

        if (this.getInvulnerabilityTime() > 0) {
            for (particleIndex = 0; particleIndex < 3; ++particleIndex) {
                this.worldObj.spawnParticle("mobSpell", this.posX + this.rand.nextGaussian(), this.posY + this.rand.nextFloat() * 3.3, this.posZ + this.rand.nextGaussian(), 0.7f, 0.7f, 0.9f);
            }
        }
    }

    public int getInvulnerabilityTime() {
        return this.dataWatcher.getWatchableObjectInt(20);
    }

    private float clampAngle(float par1, float par2, float par3) {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);
        if (var4 > par3) {
            var4 = par3;
        }
        if (var4 < -par3) {
            var4 = -par3;
        }
        return par1 + var4;
    }

    private double getHeadX(int headIndex) {
        if (headIndex <= 0) {
            return this.posX; // Return the central position for the main body
        }
        float angleOffset = (this.renderYawOffset + (180 * (headIndex - 1))) / 180.0f * (float) Math.PI;
        float offsetX = MathHelper.cos(angleOffset); // Calculate X offset
        return this.posX + offsetX * 1.3; // Add offset to the X position
    }

    private double getHeadY(int headIndex) {
        return headIndex <= 0 ? this.posY + 3.0 : this.posY + 2.2; // Return different heights based on index
    }

    private double getHeadZ(int headIndex) {
        if (headIndex <= 0) {
            return this.posZ; // Return the central position for the main body
        }
        float angleOffset = (this.renderYawOffset + (180 * (headIndex - 1))) / 180.0f * (float) Math.PI;
        float offsetZ = MathHelper.sin(angleOffset); // Calculate Z offset
        return this.posZ + offsetZ * 1.3; // Add offset to the Z position
    }
    public int getHealthTimer() {
        return this.dataWatcher.getWatchableObjectInt(20);
    }
    public void setHealthTimer(int par1) {
        this.dataWatcher.updateObject(20, par1);
    }
    private double getHeadXPositionOffset(int par1) {
        if (par1 <= 0) {
            return this.posX;
        }
        float var2 = (this.renderYawOffset + (float)(180 * (par1 - 1))) / 180.0f * (float)Math.PI;
        float var3 = MathHelper.cos(var2);
        return this.posX + (double)var3 * 1.3;
    }

    private double getHeadYPositionOffset(int headIndex) {
        return headIndex <= 0 ? this.posY + 3.0 : this.posY + 2.2;
    }

    private double getHeadZPositionOffset(int headIndex) {
        if (headIndex <= 0) {
            return this.posZ;
        }
        float angle = (this.renderYawOffset + (float)(180 * (headIndex - 1))) / 180.0f * (float)Math.PI;
        float sinAngle = MathHelper.sin(angle);
        return this.posZ + (double)sinAngle * 1.3;
    }

    private void spawnEntity(int headIndex, double targetX, double targetY, double targetZ, boolean isInvulnerable) {
        this.worldObj.playAuxSFXAtEntity(null, 1014, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        double offsetX = this.getHeadXPositionOffset(headIndex);
        double offsetY = this.getHeadYPositionOffset(headIndex);
        double offsetZ = this.getHeadZPositionOffset(headIndex);
        double deltaX = targetX - offsetX;
        double deltaY = targetY - offsetY;
        double deltaZ = targetZ - offsetZ;
        EntityWitherSkull witherSkull = new EntityWitherSkull(this.worldObj, this, deltaX, deltaY, deltaZ);
        if (isInvulnerable) {
            witherSkull.setInvulnerable(true);
        }
        witherSkull.posY = offsetY;
        witherSkull.posX = offsetX;
        witherSkull.posZ = offsetZ;
        this.worldObj.spawnEntityInWorld(witherSkull);
    }

    private void setTargetButInsteadItJustShoots(int par1, EntityLivingBase par2EntityLivingBase) {
        this.spawnEntity(par1, par2EntityLivingBase.posX, par2EntityLivingBase.posY + (double)par2EntityLivingBase.getEyeHeight() * 0.5, par2EntityLivingBase.posZ, par1 == 0 && this.rand.nextFloat() < 0.001f);
    }

    public void setTargetId(int par1, int par2) {
        this.dataWatcher.updateObject(17 + par1, par2);
    }

    private static void placeBlocksAtLine(World world, int x, int y, int z, int line){
        int bonus = (line % 5) * 12;
        for(int i = bonus; i < 12 + bonus; i++){
            int blockID = world.rand.nextBoolean() ? NMBlocks.specialObsidian.blockID : NMBlocks.cryingObsidian.blockID;
            world.setBlock(x + i, y, z - MathHelper.floor_double((double) line / 5),blockID);
        }
    }

    private static void placeBlocksAtLinePartial(World world, int x, int y, int z, int line){
        int bonus = (line % 3) * 20;
        for(int i = bonus; i < 20 + bonus; i++) {
            int blockID = world.rand.nextBoolean() ? NMBlocks.specialObsidian.blockID : NMBlocks.cryingObsidian.blockID;
            world.setBlock(x + i, y, z - MathHelper.floor_double((double) line / 3), blockID);
        }
    }

    @Override
    protected void updateAITasks() {
        if (this.getHealthTimer() > 0) {
            int healthTimer = this.getHealthTimer() - 1;
            // builds the platform of the wither arena, in a lag efficient way

            // places 1 line every tick
//            if(isBetween(healthTimer,80,400)){
//                int line = healthTimer - 80;
//                // healthTimer holds a value between 0 and 300
//                placeBlocksAtLine(this.worldObj, (int) this.posX - 30, (int) this.posY - 1, (int) this.posZ + 30, line);
//            }

            // places one line every 2 ticks
            if(isBetween(healthTimer,40,400)){
                int line = healthTimer - 40;
                // healthTimer holds a value between 0 and 360
                if (line % 2 == 0) {
                    placeBlocksAtLinePartial(this.worldObj, (int) this.posX - 30, (int) this.posY - 1, (int) this.posZ + 30, MathHelper.floor_double((double) line / 2));
                }
            }

            if(healthTimer == 30){
                Entity lightningbolt = new LightningBoltEntity(this.worldObj, this.posX, this.posY + 1, this.posZ);
                this.worldObj.addWeatherEffect(lightningbolt);
            } else if(healthTimer == 15){
                LightningBoltEntity lightningbolt1 = new LightningBoltEntity(this.worldObj, this.posX + 3, this.posY + 1, this.posZ);
                LightningBoltEntity lightningbolt2 = new LightningBoltEntity(this.worldObj, this.posX - 3, this.posY + 1, this.posZ);
                LightningBoltEntity lightningbolt3 = new LightningBoltEntity(this.worldObj, this.posX, this.posY + 1, this.posZ + 3);
                LightningBoltEntity lightningbolt4 = new LightningBoltEntity(this.worldObj, this.posX, this.posY + 1, this.posZ - 3);

                this.worldObj.addWeatherEffect(lightningbolt1);
                this.worldObj.addWeatherEffect(lightningbolt2);
                this.worldObj.addWeatherEffect(lightningbolt3);
                this.worldObj.addWeatherEffect(lightningbolt4);
            }
            if (healthTimer <= 0) {
                this.worldObj.newExplosion(this, this.posX, this.posY + this.getEyeHeight(), this.posZ, 7.0f, true, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
                this.worldObj.func_82739_e(1013, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                this.activity = true;
            }
            this.setHealthTimer(healthTimer);
            if (this.ticksExisted % 10 == 0) {
                this.heal(10.0f);
            }
        } else {
            int targetId;
            super.updateAITasks();
            // technically one of these fields should be replaced with field_82223_h which is headAttackCooldowns, but I'm keeping it like this
            for (int i = 1; i < 3; ++i) {
                if (this.ticksExisted < this.headPitch[i - 1]) continue;
                this.headPitch[i - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
                int previousAttackCount = this.headAttackCounts[i - 1];
                this.headAttackCounts[i - 1] = previousAttackCount + 1;
                if (previousAttackCount > 15) {
                    float rangeX = 10.0f;
                    float rangeY = 5.0f;
                    double posX = MathHelper.getRandomDoubleInRange(this.rand, this.posX - rangeX, this.posX + rangeX);
                    double posY = MathHelper.getRandomDoubleInRange(this.rand, this.posY - rangeY, this.posY + rangeY);
                    double posZ = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - rangeX, this.posZ + rangeX);
                    this.spawnEntity(i + 1, posX, posY, posZ, true);
                    this.headAttackCounts[i - 1] = 0;
                }
                if ((targetId = this.getWatchedTargetId(i)) > 0) {
                    Entity targetEntity = this.worldObj.getEntityByID(targetId) == null ? this.worldObj.getClosestVulnerablePlayerToEntity(this,30) : this.worldObj.getEntityByID(targetId);
                    if (targetEntity != null && targetEntity.isEntityAlive() && this.getDistanceSqToEntity(targetEntity) <= 900.0 && this.canEntityBeSeen(targetEntity)) {
                        this.setTargetButInsteadItJustShoots(i + 1, (EntityLivingBase)targetEntity);
                        this.headPitch[i - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                        this.headAttackCounts[i - 1] = 0;
                        continue;
                    }
                    this.setTargetId(i, 0);
                    continue;
                }
                List<EntityPlayer> nearbyEntities = this.worldObj.selectEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(20.0, 8.0, 20.0), attackEntitySelector);
                for (int j = 0; j < 10 && !nearbyEntities.isEmpty(); ++j) {
                    EntityPlayer entity = nearbyEntities.get(this.rand.nextInt(nearbyEntities.size()));
                    if (entity.isEntityAlive() && this.canEntityBeSeen(entity)) {
                        if (entity.capabilities.disableDamage) continue;
                        this.entityToAttack = entity;
                        this.setTargetId(i, entity.entityId);
                        continue;
                    }
                    nearbyEntities.remove(entity);
                }
            }

            if (this.entityToAttack != null) {
                this.setTargetId(0, this.entityToAttack.entityId);
            } else {
                this.setTargetId(0, 0);
            }

            if (this.shieldRegenCooldown > 0) {
                --this.shieldRegenCooldown;
                if (this.shieldRegenCooldown == 0 && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
                    int floorPosY = MathHelper.floor_double(this.posY);
                    int floorPosX = MathHelper.floor_double(this.posX);
                    int floorPosZ = MathHelper.floor_double(this.posZ);
                    boolean blockDestroyed = false;
                    for (int xOffset = -1; xOffset <= 1; ++xOffset) {
                        for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                            for (int yOffset = 0; yOffset <= 3; ++yOffset) {
                                int blockX = floorPosX + xOffset;
                                int blockY = floorPosY + yOffset;
                                int blockZ = floorPosZ + zOffset;
                                int blockId = this.worldObj.getBlockId(blockX, blockY, blockZ);
                                if (blockId <= 0 || blockId == Block.bedrock.blockID || blockId == Block.endPortal.blockID || blockId == Block.endPortalFrame.blockID || blockId == BTWBlocks.soulforgedSteelBlock.blockID) continue;
                                blockDestroyed = this.worldObj.destroyBlock(blockX, blockY, blockZ, true) || blockDestroyed;
                            }
                        }
                    }
                    if (blockDestroyed) {
                        this.worldObj.playAuxSFXAtEntity(null, 1012, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
                    }
                }
            }
            if (this.ticksExisted % 20 == 0) {
                this.heal(1.0f);
            }
        }
    }

    private static boolean isBetween(int num, int min, int max){
        return num >= min && num <= max;
    }

    public static void summonWitherAtLocation(World world, int x, int z) {
        EntityBloodWither wither = new EntityBloodWither(world);
        wither.func_82206_m();
        world.playAuxSFX(2279, x, 200, z, 0);
        WorldUtils.gameProgressSetWitherHasBeenSummonedServerOnly();
        wither.setLocationAndAngles(x + 0.25, 200, (double)z + 0.5, 0.0f, 0.0f);
        wither.origin = new int[]{x, 200, z};
        world.spawnEntityInWorld(wither);
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        return this.activity && super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    public void func_82206_m() {
        this.setHealthTimer(400);
        this.setHealth(this.getMaxHealth() / 16.0F);
    }
}
