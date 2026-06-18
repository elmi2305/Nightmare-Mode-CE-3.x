package com.itlesports.nightmaremode.entity.underworld;

import api.world.WorldUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.EntityWitherSkullExt;
import com.itlesports.nightmaremode.util.underworld.FearSource;
import com.itlesports.nightmaremode.util.underworld.postprocessing.MonoInvertPostProcessor;
import net.minecraft.src.EntityWither;
import net.minecraft.src.World;
import btw.entity.attribute.BTWAttributes;
import com.itlesports.nightmaremode.entity.variants.EntityShadowZombie;
import net.minecraft.src.*;

import java.util.*;

public class EntityAwakenedWither extends EntityWither implements FearSource {

    private final float[] sideHeadPitch = new float[2];
    private final float[] sideHeadYaw = new float[2];
    private final float[] prevSideHeadPitch = new float[2];
    private final float[] prevSideHeadYaw = new float[2];
    private final int[] headAttackCounts = new int[2];
    private final int[] headFireTimers = new int[2]; // tick timestamps, not angles

    // 0=awakening, 1=phase1, 2=transitioning, 3=phase2
    private int awakenedPhase = 0;
    private int transitionTimer = -1;

    private boolean activity = false;
    private int passivityDuration = -1;
    private double verticalOffset = 5.0;

    // attackTimer increments in the else-branch and is broken by engageWither()
    private int attackTimer = 0;
    private int baseAttackInterval = 420; // USED TO BE 580
    private int currentAttackIndex = -1;
    private int currentFireRate = -1;
    private boolean isCurrentAttackSummoning = false;
    private int currentPassivityLength   = 0;
    private int attackCycle = 0;

    private int launchWindupTimer = -1;
    private boolean launchFired  = false;

    private final List<Entity> trackedEntities = new ArrayList<>(20);

    private EntityPlayer playerTarget;
    private boolean hasLoS = false;

    private int healthDrainTimer  = 0;
    public int healthDrainStacks = 0;

    private int[] spawnOrigin;

    private static boolean bossActive = false;

    private static final Set<Integer> GOOD_EFFECTS = new HashSet<>(Arrays.asList(
            Potion.regeneration.id, Potion.fireResistance.id, Potion.digSpeed.id,
            Potion.resistance.id,   Potion.damageBoost.id,    Potion.moveSpeed.id,
            Potion.field_76434_w.id, Potion.field_76444_x.id, Potion.field_76443_y.id
    ));

    public static final int ATK_SKULL_RAIN = 1;
    public static final int ATK_DIRECT_BARRAGE = 2;
    public static final int ATK_TELEPORT_STRIKE = 3;
    public static final int ATK_SUMMON_HORDE  = 4;
    public static final int ATK_LAUNCH = 5;
    public static final int ATK_SHADOW_BARRAGE = 6;
    public static final int TICKS_DRAIN = 300;


    public int getCurrentAttack(){
        return this.currentAttackIndex;
    }
    public EntityAwakenedWither(World world) {
        super(world);
        this.awakenedPhase = 0;
    }

    public static void summonWitherAtLocation(World world, int x, int y, int z) {
        EntityAwakenedWither wither = new EntityAwakenedWither(world);
        wither.setLocationAndAngles((double)x + 0.5, (double)y - 1.45, (double)z + 0.5, 0.0f, 0.0f);
        wither.spawnOrigin = new int[]{x, 64, z};

        wither.func_82206_m();
        world.spawnEntityInWorld(wither);
        world.playAuxSFX(2279, x, y, z, 0);
    }
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(400);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(10.0);
    }

    public float getSideHeadPitch(int idx) { return this.sideHeadPitch[idx]; }
    public float getSideHeadYaw(int idx) { return this.sideHeadYaw[idx]; }
    public float getPrevSideHeadPitch(int idx){ return this.prevSideHeadPitch[idx]; }
    public float getPrevSideHeadYaw(int idx) { return this.prevSideHeadYaw[idx]; }
    public int getAwakenedPhase() { return this.awakenedPhase; }

    public static boolean isBossActive() { return bossActive; }
    public static void setBossActive(boolean v){ bossActive = v; }

    public int getHealthTimer() { return this.dataWatcher.getWatchableObjectInt(20); }
    public void setHealthTimer(int v) { this.dataWatcher.updateObject(20, v); }
    public void setTargetId(int i, int id){ this.dataWatcher.updateObject(17 + i, id); }
    public int getInvulnerabilityTime() { return this.dataWatcher.getWatchableObjectInt(20); }

    private void engageWither() {
        if (!this.activity) {
            this.attackTimer++;
        }
        this.passivityDuration = -1;
        this.activity = true;
        this.setInvisible(false);
        this.verticalOffset = this.awakenedPhase == 3 ? 4.0 : 5.0;
    }

    private void disengageWither() {
        this.activity = false;
        this.setInvisible(true);
        this.verticalOffset = 12.0;
    }

    private void setWitherPassiveFor(int ticks) { this.passivityDuration = ticks; }

    @Override protected boolean isAIEnabled() { return this.activity || this.getHealthTimer() > 0; }
    @Override public boolean canBeCollidedWith() { return this.activity && this.getHealthTimer() <= 0; }
    @Override public boolean canBePushed() { return false; }
    @Override public boolean isArmored() { return this.awakenedPhase == 3; }

    @Override
    public void func_82206_m() {
        // NMUtils.forcePlayMusic(NightmareModeAddon.NM_AWAKENEDWITHER.sound(), true);
        this.setHealthTimer(200);
        this.setHealth(this.getMaxHealth() / 20.0F);
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        this.dropItem(NMItems.awakenedStar.itemID, 1);
    }

    @Override public void checkForScrollDrop() {}

    // nbt stuff
    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (this.spawnOrigin != null) tag.setIntArray("spawnOrigin", this.spawnOrigin);
        tag.setInteger("awakenedPhase",  this.awakenedPhase);
        tag.setInteger("drainStacks",    this.healthDrainStacks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("spawnOrigin")) this.spawnOrigin = tag.getIntArray("spawnOrigin");
        this.awakenedPhase = tag.getInteger("awakenedPhase");
        this.healthDrainStacks = tag.getInteger("drainStacks");
    }

    @Override
    public boolean attackEntityFrom(DamageSource src, float amount) {
        if (!this.activity || this.awakenedPhase == 0 || this.awakenedPhase == 2) return false;
        amount = Math.min(amount, 20.0f);
        if (this.awakenedPhase == 1 && this.getHealth() <= amount + 1) {
            this.beginPhaseTransition();
            return false;
        }
        return super.entityMobAttackEntityFrom(src, amount);
    }

    @Override
    public void onDeath(DamageSource source) {
        setBossActive(false);
        if (this.playerTarget != null) this.playerTarget.capabilities.allowEdit = true;
        NMUtils.shushMusic();
        MonoInvertPostProcessor.INSTANCE.setEnabled(false);
        super.onDeath(source);
    }

    // chat helper methods
    private void sendChatKey(String key, EnumChatFormatting color) {
        if (this.playerTarget == null) return;
        ChatMessageComponent msg = new ChatMessageComponent();
        msg.addKey(key);
        msg.setColor(color);
        this.playerTarget.sendChatToPlayer(msg);
    }

    private void sendChatRaw(String text, EnumChatFormatting color) {
        if (this.playerTarget == null) return;
        ChatMessageComponent msg = new ChatMessageComponent();
        msg.addText(text);
        msg.setColor(color);
        this.playerTarget.sendChatToPlayer(msg);
    }

    private void removePlayerPotions() {
        if (this.playerTarget == null) return;
        List<Integer> toRemove = new ArrayList<>();
        for (PotionEffect eff : (Collection<PotionEffect>) this.playerTarget.getActivePotionEffects()) {
            if (GOOD_EFFECTS.contains(eff.getPotionID())) toRemove.add(eff.getPotionID());
        }
        toRemove.forEach(this.playerTarget::removePotionEffect);
    }

    // transition
    private void beginPhaseTransition() {
        this.awakenedPhase = 2;
        this.transitionTimer = 180;
        this.heal(this.getMaxHealth() * 0.55f);
        this.trackedEntities.forEach(Entity::setDead);
        this.trackedEntities.clear();
        this.passivityDuration = -1;
        this.currentAttackIndex = -1;
        this.disengageWither();
        this.worldObj.playAuxSFX(2279,
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posY),
                MathHelper.floor_double(this.posZ), 0);

        MonoInvertPostProcessor.INSTANCE.setEnabled(true);

        sendChatKey("bosses.awakenedwither.phase_shift", EnumChatFormatting.DARK_PURPLE);
    }

    private void tickTransition() {
        this.transitionTimer--;

        if (this.motionY < 0.28) this.motionY += 0.036;

        if (this.ticksExisted % 3 == 0) {
            double fraction = this.transitionTimer / 180.0;
            double radius   = 5 + 12 * Math.sin(fraction * Math.PI);
            for (int i = 0; i < 12; i++) {
                double angle = (i / 12.0) * Math.PI * 2 + this.transitionTimer * 0.08;
                double px = this.posX + Math.cos(angle) * radius;
                double pz = this.posZ + Math.sin(angle) * radius;
                this.worldObj.spawnParticle("portal", px, this.posY + 2 + this.rand.nextDouble() * 3, pz,
                        (this.posX - px) * 0.07, 0.05, (this.posZ - pz) * 0.07);
            }
            this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 2, this.posZ, 0, 0.05, 0);
        }

        if (this.transitionTimer == 140) sendChatKey("bosses.awakenedwither.evolving", EnumChatFormatting.DARK_PURPLE);
        if (this.transitionTimer == 90)  sendChatKey("bosses.awakenedwither.purge_warning", EnumChatFormatting.RED);

        if (this.transitionTimer == 60) {
            this.removePlayerPotions();
            this.worldObj.addWeatherEffect(new EntityLightningBolt(this.worldObj, this.posX, this.posY, this.posZ));
            this.worldObj.playAuxSFX(2279,
                    MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.posY),
                    MathHelper.floor_double(this.posZ), 0);
        }

        if (this.transitionTimer <= 0) {
            this.awakenedPhase = 3;
            this.transitionTimer = -1;
            this.baseAttackInterval = 300;
            this.attackTimer = 0;
            this.currentAttackIndex = -1;
            this.activity = false;
            this.engageWither();
            this.worldObj.newExplosion(this, this.posX, this.posY, this.posZ, 5.5f, false, false);
            sendChatKey("bosses.awakenedwither.reborn", EnumChatFormatting.DARK_RED);
        }
    }

    private void tickHealthDrain() {
        this.healthDrainStacks++;


        for(Object po : this.worldObj.playerEntities){
            if(po instanceof EntityPlayerExt p){
                p.nightmareMode$incrementHealth(-1);
            }
        }

        sendChatRaw("The realm erodes your life force. [-1 Max Health]", EnumChatFormatting.DARK_RED);
    }

    private void setAttackDetails(int index, boolean isInit) {
        System.out.println("doing attack: " + index);
        switch (index) {
            case ATK_SKULL_RAIN:
                this.currentFireRate = 2;
                this.isCurrentAttackSummoning = false;
                this.currentPassivityLength  = this.awakenedPhase == 3 ? 320 : 400;
                break;
            case ATK_DIRECT_BARRAGE:
                this.currentFireRate = 3;
                this.isCurrentAttackSummoning = false;
                this.currentPassivityLength  = this.awakenedPhase == 3 ? 280 : 100;
                break;
            case ATK_TELEPORT_STRIKE:
                this.currentFireRate = 6;
                this.isCurrentAttackSummoning = false;
                this.currentPassivityLength = 300;
                break;
            case ATK_SUMMON_HORDE:
                this.currentFireRate = -1;
                this.isCurrentAttackSummoning = true;
                this.currentPassivityLength = this.awakenedPhase == 3 ? 520 : 440;
                break;
            case ATK_LAUNCH:
                if (isInit) {
                    this.launchWindupTimer = -1;
                    this.launchFired = false;
                }
                this.currentFireRate = -1;
                this.isCurrentAttackSummoning = false;
                this.currentPassivityLength  = 200;
                break;
            case ATK_SHADOW_BARRAGE:
                this.currentFireRate = 8;
                this.isCurrentAttackSummoning = false;
                this.currentPassivityLength = 300;
                break;
        }
    }

    private void updateAntiCheese() {
        this.hasLoS = this.playerTarget != null && this.canEntityBeSeen(this.playerTarget);
        if (!this.hasLoS && this.activity) {
            this.verticalOffset = 1.0;
        }
    }

    private void manageWitherPassivity(boolean isTrackingEntities) {
        if (isTrackingEntities) {
            this.disengageWither();
            this.trackedEntities.removeIf(e -> e.isDead || !this.worldObj.getLoadedEntityList().contains(e));
            if (this.passivityDuration > 0) {
                this.setWitherPassiveFor(this.passivityDuration - 1);
                this.disengageWither();
            } else {
                this.trackedEntities.forEach(Entity::setDead);
                this.trackedEntities.clear();
                this.engageWither();
            }
            if (this.trackedEntities.isEmpty()) {
                this.engageWither();
            }
        } else {
            if (this.passivityDuration > 0) {
                this.setWitherPassiveFor(this.passivityDuration - 1);
                this.disengageWither();
            } else {
                this.engageWither();
            }
        }
    }

    private void executeAttack(int index, int fireRate) {
        this.setAttackDetails(index, false);
        if (this.playerTarget == null) return;
        boolean shouldFire = fireRate == -1 || this.ticksExisted % fireRate == 0;
        if (!shouldFire) return;
        switch (index) {
            case ATK_SKULL_RAIN:      doSkullRain();      break;
            case ATK_DIRECT_BARRAGE:  doDirectBarrage();  break;
            case ATK_TELEPORT_STRIKE: doTeleportStrike(); break;
            case ATK_SUMMON_HORDE:    doSummonHorde();    break;
            case ATK_LAUNCH:          doLaunchAttack();   break;
            case ATK_SHADOW_BARRAGE:  doShadowBarrage();  break;
        }
    }

    private void doSkullRain() {
        EntityPlayer p = this.playerTarget;
        int count = this.awakenedPhase == 3 ? 4 : 3;
        for (int i = 0; i < count; i++) {
            double spawnX = p.posX + (this.rand.nextDouble() - 0.5) * 22.0;
            double spawnY = p.posY + 24 + this.rand.nextInt(8);
            double spawnZ = p.posZ + (this.rand.nextDouble() - 0.5) * 22.0;
            double dx = (this.rand.nextDouble() - 0.5) * 0.5;
            double dy = -(0.75 + this.rand.nextDouble() * 0.35);
            double dz = (this.rand.nextDouble() - 0.5) * 0.5;
            double invLen = 1.0 / Math.sqrt(dx * dx + dy * dy + dz * dz);
            dx *= invLen; dy *= invLen; dz *= invLen;
            EntityWitherSkull skull = new EntityWitherSkull(this.worldObj, this, dx, dy, dz);
            if (this.awakenedPhase == 3 && this.rand.nextInt(4) == 0){ skull.setInvulnerable(true);}
            else if(this.rand.nextInt(32) == 0){
                ((EntityWitherSkullExt)skull).nightmareMode$setLifeStealing(true);
            }
            skull.posX = spawnX; skull.posY = spawnY; skull.posZ = spawnZ;
            this.worldObj.spawnEntityInWorld(skull);
        }
        this.worldObj.playAuxSFXAtEntity(null, 1014, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
    }

    private void doDirectBarrage() {
        EntityPlayer p = this.playerTarget;
        int head = this.rand.nextInt(3);
        spawnSkull(head, p.posX, p.posY + p.getEyeHeight() * 0.5, p.posZ, false);
        if (this.awakenedPhase == 3 && this.rand.nextInt(4) == 0) {
            spawnSkull((head + 1) % 3, p.posX, p.posY + p.getEyeHeight() * 0.5, p.posZ, false);
        }
    }

    private void doTeleportStrike() {
        EntityPlayer p  = this.playerTarget;
        double ox = (this.rand.nextDouble() - 0.5) * 14;
        double oz = (this.rand.nextDouble() - 0.5) * 14;
        this.setPositionAndUpdate(p.posX + ox, p.posY + 3 + this.rand.nextInt(4), p.posZ + oz);
        this.worldObj.playAuxSFXAtEntity(null, 1014, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
        spawnSkull(0, p.posX, p.posY + p.getEyeHeight() * 0.5, p.posZ, false);
    }

    private void doSummonHorde() {
        if (!this.trackedEntities.isEmpty()) return;
        EntityPlayer p = this.playerTarget;
        int count = this.awakenedPhase == 3 ? 5 : 3;
        for (int i = 0; i < count; i++) {
            EntityLiving mob = buildSummonMob();
            double ox = (this.rand.nextBoolean() ? 1 : -1) * (4 + this.rand.nextInt(10));
            double oz = (this.rand.nextBoolean() ? 1 : -1) * (4 + this.rand.nextInt(10));
            mob.setPositionAndUpdate(p.posX + ox, this.worldObj.getPrecipitationHeight((int) (p.posX + ox), (int) (p.posZ + oz)), p.posZ + oz);
            mob.setAttackTarget(p);
            this.trackedEntities.add(mob);
            this.worldObj.spawnEntityInWorld(mob);
        }
        if (this.awakenedPhase == 3) {
            EntityBlaze blaze = new EntityBlaze(this.worldObj);
            blaze.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 100000, 1));
            double bx = (this.rand.nextBoolean() ? 1 : -1) * (5 + this.rand.nextInt(5));
            double bz = (this.rand.nextBoolean() ? 1 : -1) * (5 + this.rand.nextInt(5));
            blaze.setPositionAndUpdate(p.posX + bx, this.worldObj.getPrecipitationHeight((int) (p.posX + bx), (int) (p.posZ + bz)) + this.rand.nextInt(4) + 1, p.posZ + bz);
            blaze.entityToAttack = p;
            this.trackedEntities.add(blaze);
            this.worldObj.spawnEntityInWorld(blaze);
        }
    }

    private EntityLiving buildSummonMob() {
        int roll = this.rand.nextInt(this.awakenedPhase == 3 ? 5 : 3);
        EntityLiving mob;
        switch (roll) {
            case 0: case 1: {
                EntitySkeleton skel = new EntitySkeleton(this.worldObj);
                skel.setSkeletonType(1);
                skel.addPotionEffect(new PotionEffect(Potion.moveSpeed.id,  100000, 1));
                skel.addPotionEffect(new PotionEffect(Potion.resistance.id, 100000, 0));
                mob = skel;
                break;
            }
            case 2: {
                EntityShadowZombie sz = new EntityShadowZombie(this.worldObj);
                sz.addPotionEffect(new PotionEffect(Potion.resistance.id, 100000, 1));
                mob = sz;
                break;
            }
            case 3: {
                EntityEnderman em = new EntityEnderman(this.worldObj);
                em.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 100000, 2));
                mob = em;
                break;
            }
            case 4: {
                EntityWitch wt = new EntityWitch(this.worldObj);
                wt.addPotionEffect(new PotionEffect(Potion.resistance.id, 100000, 1));
                mob = wt;
                break;
            }
            default:
                mob = new EntityShadowZombie(this.worldObj);
        }
        for (int i = 0; i < 5; i++) mob.setEquipmentDropChance(i, 0f);
        return mob;
    }

    private void doLaunchAttack() {
        EntityPlayer p = this.playerTarget;
        if (p == null) return;
        if (this.launchWindupTimer < 0 && !this.launchFired) {
            this.launchWindupTimer = 80;
            sendChatKey("bosses.awakenedwither.launch_warning", EnumChatFormatting.GOLD);
            return;
        }
        if (!this.launchFired && this.launchWindupTimer > 0) {
            this.launchWindupTimer--;
            if (this.launchWindupTimer % 4 == 0) {
                for (int i = 0; i < 8; i++) {
                    double rx = (this.rand.nextDouble() - 0.5) * 18;
                    double rz = (this.rand.nextDouble() - 0.5) * 18;
                    this.worldObj.spawnParticle("portal",
                            p.posX + rx, p.posY + this.rand.nextDouble() * 2, p.posZ + rz,
                            -rx * 0.07, 0.06, -rz * 0.07);
                }
            }
            if (this.launchWindupTimer == 30) sendChatRaw("(this attack may not work, it's in development!)", EnumChatFormatting.RED);
            if (this.launchWindupTimer <= 0) {
                this.launchFired = true;
                if (this.getDistanceSqToEntity(p) < 1024.0) {
                    p.motionY = 2.4;
                    p.motionX *= 0.2;
                    p.motionZ *= 0.2;
                    p.fallDistance = 0;
                    p.onGround= false;
                    p.isAirBorne = true;
                    this.worldObj.playAuxSFX(2279, MathHelper.floor_double(p.posX), MathHelper.floor_double(p.posY), MathHelper.floor_double(p.posZ), 0);
//                    sendChatRaw("\u00a7c[Think fast!]", EnumChatFormatting.RED);
                }
            }
        }
    }

    private void doShadowBarrage() {
        doDirectBarrage();
        if (this.ticksExisted % 40 == 0) {
            this.playerTarget.addPotionEffect(new PotionEffect(Potion.blindness.id,    80, 0));
            this.playerTarget.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 1));
        }
    }


    private void spawnSkull(int headIndex, double targetX, double targetY, double targetZ, boolean charged) {
        this.worldObj.playAuxSFXAtEntity(null, 1014, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
        double ox = getHeadX(headIndex), oy = getHeadY(headIndex), oz = getHeadZ(headIndex);
        EntityWitherSkull skull = new EntityWitherSkull(
                this.worldObj, this, targetX - ox, targetY - oy, targetZ - oz);
        if (charged) skull.setInvulnerable(true);
        skull.posX = ox; skull.posY = oy; skull.posZ = oz;
        this.worldObj.spawnEntityInWorld(skull);
    }

    private void spawnSkullWithRandomOffset(int headIndex, double targetX, double targetY, double targetZ, boolean charged) {
        this.worldObj.playAuxSFXAtEntity(null, 1014, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
        double ox = getHeadX(headIndex), oy = getHeadY(headIndex), oz = getHeadZ(headIndex);
        EntityWitherSkull skull = new EntityWitherSkull(
                this.worldObj, this, targetX - ox, targetY - oy, targetZ - oz);
        if (charged) skull.setInvulnerable(true);
        skull.posX = ox; skull.posY = oy; skull.posZ = oz;
        this.worldObj.spawnEntityInWorld(skull);
    }




    private double getHeadX(int idx) {
        if (idx <= 0) return this.posX;
        float a = (this.renderYawOffset + 180f * (idx - 1)) / 180f * (float) Math.PI;
        return this.posX + MathHelper.cos(a) * 1.3;
    }

    private double getHeadY(int idx) { return idx <= 0 ? this.posY + 3.0 : this.posY + 2.2; }

    private double getHeadZ(int idx) {
        if (idx <= 0) return this.posZ;
        float a = (this.renderYawOffset + 180f * (idx - 1)) / 180f * (float) Math.PI;
        return this.posZ + MathHelper.sin(a) * 1.3;
    }

    private float clampAngle(float current, float target, float maxDelta) {
        float d = MathHelper.wrapAngleTo180_float(target - current);
        if (d >  maxDelta) d =  maxDelta;
        if (d < -maxDelta) d = -maxDelta;
        return current + d;
    }

    @Override
    protected void updateAITasks() {
        if (this.getHealthTimer() > 0) {
            int t = this.getHealthTimer() - 1;
            this.setHealthTimer(t);

            if (this.motionY < 0.22) this.motionY += 0.025;

            if (t % 2 == 0) {
                double radius = 10 + t * 0.04;
                for (int i = 0; i < 10; i++) {
                    double angle = (i / 10.0) * Math.PI * 2 + (200 - t) * 0.05;
                    double px    = this.posX + Math.cos(angle) * radius;
                    double pz    = this.posZ + Math.sin(angle) * radius;
                    this.worldObj.spawnParticle("portal",
                            px, this.posY + (this.rand.nextDouble() - 0.5) * 8, pz,
                            (this.posX - px) * 0.04, 0.04, (this.posZ - pz) * 0.04);
                }
            }

            if (t == 170) {
                this.worldObj.addWeatherEffect(
                        new EntityLightningBolt(this.worldObj, this.posX, this.posY, this.posZ));
            }
            if (t == 90) {
                sendChatKey("bosses.awakenedwither.awakening", EnumChatFormatting.DARK_PURPLE);
                this.worldObj.addWeatherEffect(
                        new EntityLightningBolt(this.worldObj, this.posX + 3, this.posY, this.posZ));
                this.worldObj.addWeatherEffect(
                        new EntityLightningBolt(this.worldObj, this.posX - 3, this.posY, this.posZ));
            }
            if (t == 40) {
                this.worldObj.addWeatherEffect(
                        new EntityLightningBolt(this.worldObj, this.posX, this.posY, this.posZ + 3));
                this.worldObj.addWeatherEffect(
                        new EntityLightningBolt(this.worldObj, this.posX, this.posY, this.posZ - 3));
            }
            if (t <= 0) {
                this.worldObj.newExplosion(this, this.posX, this.posY + this.getEyeHeight(), this.posZ,
                        6.0f, false, false);
                this.worldObj.func_82739_e(1013, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
                this.awakenedPhase = 1;
                this.activity = true;
                this.setInvisible(false);
            }
            if (this.ticksExisted % 8 == 0) this.heal(25f);
            return;
        }

        super.updateAITasks();

        for (int i = 1; i < 3; i++) {
            if (this.ticksExisted < this.headFireTimers[i - 1]) continue;

            this.headFireTimers[i - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);
            this.headAttackCounts[i - 1]++;

            if (this.headAttackCounts[i - 1] > 15) {
                float  r  = 10.0f;
                double px = this.posX + (this.rand.nextDouble() - 0.5) * 2 * r;
                double py = this.posY + (this.rand.nextDouble() - 0.5) * 10;
                double pz = this.posZ + (this.rand.nextDouble() - 0.5) * 2 * r;
                spawnSkull(i, px, py, pz, true);
                this.headAttackCounts[i - 1] = 0;
            }

            int targetId = this.getWatchedTargetId(i);
            if (targetId > 0) {
                Entity target = this.worldObj.getEntityByID(targetId);
                if (target == null) target = this.worldObj.getClosestVulnerablePlayerToEntity(this, 30);
                if (target != null && target.isEntityAlive()
                        && this.getDistanceSqToEntity(target) <= 900.0
                        && this.canEntityBeSeen(target)) {
                    EntityLivingBase lb = (EntityLivingBase) target;
                    spawnSkull(i, target.posX, target.posY + lb.getEyeHeight(), target.posZ, false);
                    this.headFireTimers[i - 1]   = this.ticksExisted + 40 + this.rand.nextInt(20);
                    this.headAttackCounts[i - 1] = 0;
                    continue;
                }
                this.setTargetId(i, 0);
                continue;
            }

            List<EntityPlayer> nearby = this.worldObj.selectEntitiesWithinAABB(
                    EntityPlayer.class, this.boundingBox.expand(20, 8, 20), IEntitySelector.selectAnything);
            for (int j = 0; j < 10 && !nearby.isEmpty(); j++) {
                EntityPlayer candidate = nearby.get(this.rand.nextInt(nearby.size()));
                if (candidate.isEntityAlive() && this.canEntityBeSeen(candidate)
                        && !candidate.capabilities.disableDamage) {
                    this.entityToAttack = candidate;
                    this.setTargetId(i, candidate.entityId);
                    break;
                }
                nearby.remove(candidate);
            }
        }

        if (this.entityToAttack != null) this.setTargetId(0, this.entityToAttack.entityId);
        else                              this.setTargetId(0, 0);
    }

    @Override
    public void onLivingUpdate() {
        this.motionY *= 0.6f;

        if (this.playerTarget == null || !this.playerTarget.isEntityAlive()) {
            this.playerTarget = this.worldObj.getClosestVulnerablePlayerToEntity(this, 64);
        }
        setBossActive(this.playerTarget != null && this.playerTarget.isEntityAlive() && this.isEntityAlive());
        if(!isBossActive() && this.getAwakenedPhase() == 3){
            MonoInvertPostProcessor.INSTANCE.setEnabled(false);
        }

        if ((this.playerTarget == null || !this.playerTarget.isEntityAlive()) && this.getInvulnerabilityTime() == 0) {
            NMUtils.shushMusic();
        }

        if (this.awakenedPhase == 2) {
            if (!this.worldObj.isRemote) tickTransition();
            super.entityMobOnLivingUpdate();
            spawnHeadParticles();
            return;
        }

        if (!this.worldObj.isRemote && this.playerTarget != null) {

            if(this.ticksExisted % 4 == 0){
                this.destroyBlocksInAABB(this.boundingBox.expand(0.5d,0,0.5d));
            }

            if(this.playerTarget instanceof EntityPlayerExt pExt && this.ticksExisted % 6 == 0) {
//                float t = (16 - this.getDistanceToEntity(this.playerTarget)) / 15;
//                System.out.println(t + " | " + this.getDistanceToEntity(this.playerTarget) + " | " + pExt.nightmareMode$getFear());
//                if (t > 0) {
//                    pExt.nightmareMode$setFear(t);
//                }
                pExt.nightmareMode$setFear(Math.max(pExt.nightmareMode$getFear(), 0.6f));
            }

            EntityPlayer p = this.playerTarget;
            p.capabilities.allowEdit = this.activity || this.isDead;

            if (this.awakenedPhase >= 1
                    && this.ticksExisted % this.baseAttackInterval == this.baseAttackInterval - 1
                    && this.passivityDuration == -1
                    && this.trackedEntities.isEmpty()) {

                int pool = (this.awakenedPhase == 3 ? 6 : 4) + 1;
                int next = 0;
                int tries = 0;
                do {
                    next = this.rand.nextInt(pool);
                    tries++;
                    if (next == this.currentAttackIndex) continue;
                    if (next == ATK_LAUNCH && this.rand.nextInt(5) != 0) continue;
                    break;
                } while (tries < 30);
//                next = ATK_SKULL_RAIN;
                this.currentAttackIndex = next;
                this.setAttackDetails(this.currentAttackIndex, true);
            }

            // attackTimer modulo: fires every (baseAttackInterval+200) ticks.
            // engageWither() increments attackTimer by 1 to break the modulo, resuming normal counting.
            if (this.awakenedPhase >= 1
                    && (this.attackTimer % (this.baseAttackInterval + 200)) == (this.baseAttackInterval + 199)) {

                if (this.passivityDuration == -1) {
                    this.setWitherPassiveFor(this.currentPassivityLength);
                }
                if (this.currentAttackIndex == ATK_SKULL_RAIN) {
                    this.manageWitherPassivity(false);
                    this.executeAttack(this.currentAttackIndex, this.currentFireRate);
                } else if (this.isCurrentAttackSummoning) {
                    this.executeAttack(this.currentAttackIndex, this.currentFireRate);
                    this.manageWitherPassivity(true);
                } else {
                    this.executeAttack(this.currentAttackIndex, this.currentFireRate);
                    this.manageWitherPassivity(false);
                }
            } else {
                this.attackTimer = (this.attackTimer + 1) % 3000;
                if (this.awakenedPhase >= 1) {
                    this.activity = true;
                    this.attackCycle = 0;
                }
            }

            if (this.awakenedPhase == 3) {
                this.healthDrainTimer++;
                if (this.healthDrainTimer >= TICKS_DRAIN) {
                    this.healthDrainTimer = 0;
                    this.tickHealthDrain();
                }
            }

            this.updateAntiCheese();

            if (this.getHealthTimer() <= 0) {
                double targetPosY = p.posY + this.verticalOffset;
                if (this.posY < targetPosY) {
                    if (this.motionY < 0) this.motionY = 0;
                    this.motionY += (0.5 - this.motionY) * 0.6f;
                }

                double dx     = p.posX - this.posX;
                double dz     = p.posZ - this.posZ;
                double distSq = dx * dx + dz * dz;

                if (distSq > 9.0 && this.activity) {
                    double dist      = MathHelper.sqrt_double(distSq);
                    float  aggression = this.hasLoS ? 0.6f : 0.85f;
                    this.motionX += (dx / dist * 0.5 - this.motionX) * aggression;
                    this.motionZ += (dz / dist * 0.5 - this.motionZ) * aggression;
                }

                if (distSq > 1024 && !this.activity && this.trackedEntities.isEmpty()) {
                    this.setPositionAndUpdate(p.posX, p.posY + 8, p.posZ);
                }

                if (!this.hasLoS && this.activity && this.ticksExisted % 180 == 0) {
                    double ox = (this.rand.nextDouble() - 0.5) * 8;
                    double oz = (this.rand.nextDouble() - 0.5) * 8;
                    this.setPositionAndUpdate(p.posX + ox, p.posY + 1, p.posZ + oz);
                }
            }
        }

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05f) {
            this.rotationYaw = (float) Math.atan2(this.motionZ, this.motionX) * 57.295776f - 90.0f;
        }

        super.entityMobOnLivingUpdate();

        for (int i = 0; i < 2; i++) {
            this.prevSideHeadYaw[i]   = this.sideHeadYaw[i];
            this.prevSideHeadPitch[i] = this.sideHeadPitch[i];
        }
        for (int i = 0; i < 2; i++) {
            int    targetId   = this.getWatchedTargetId(i + 1);
            Entity headTarget = targetId > 0 ? this.worldObj.getEntityByID(targetId) : null;
            if (headTarget instanceof EntityLivingBase) {
                double hx    = getHeadX(i + 1), hy = getHeadY(i + 1), hz = getHeadZ(i + 1);
                double ddx   = headTarget.posX - hx;
                double ddy   = headTarget.posY + ((EntityLivingBase) headTarget).getEyeHeight() - hy;
                double ddz   = headTarget.posZ - hz;
                double hDist = MathHelper.sqrt_double(ddx * ddx + ddz * ddz);
                float  yaw   = (float) (Math.atan2(ddz, ddx) * 180.0 / Math.PI) - 90.0f;
                float  pitch = (float) -(Math.atan2(ddy, hDist) * 180.0 / Math.PI);
                this.sideHeadPitch[i] = clampAngle(this.sideHeadPitch[i], pitch, 40.0f);
                this.sideHeadYaw[i]   = clampAngle(this.sideHeadYaw[i],   yaw,   10.0f);
            } else {
                this.sideHeadYaw[i] = clampAngle(this.sideHeadYaw[i], this.renderYawOffset, 10.0f);
            }
        }

        spawnHeadParticles();
    }


    private void destroyBlocksInAABB(AxisAlignedBB aabb) {
        // copied from EntityWitherMixin
        int minX = MathHelper.floor_double(aabb.minX);
        int minY = MathHelper.floor_double(aabb.minY);
        int minZ = MathHelper.floor_double(aabb.minZ);
        int maxX = MathHelper.floor_double(aabb.maxX);
        int maxY = MathHelper.floor_double(aabb.maxY);
        int maxZ = MathHelper.floor_double(aabb.maxZ);
        boolean setToAir = false;
        for (int dx = minX; dx <= maxX; ++dx) {
            for (int dy = minY; dy <= maxY; ++dy) {
                for (int dz = minZ; dz <= maxZ; ++dz) {
                    int blockID = this.worldObj.getBlockId(dx, dy, dz);
                    if (blockID == 0) continue;
                    if (
                        blockID != Block.bedrock.blockID
                        && blockID != NMBlocks.cryingObsidian.blockID
                        && blockID != NMBlocks.specialObsidian.blockID
                        && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
                    {
                        setToAir = this.worldObj.setBlockToAir(dx, dy, dz);
                    }
                }
            }
        }
        if (setToAir) {
            double fxX = aabb.minX + (aabb.maxX - aabb.minX) * (double)this.rand.nextFloat();
            double fxY = aabb.minY + (aabb.maxY - aabb.minY) * (double)this.rand.nextFloat();
            double fxZ = aabb.minZ + (aabb.maxZ - aabb.minZ) * (double)this.rand.nextFloat();
            this.worldObj.spawnParticle("largeexplode", fxX, fxY, fxZ, 0.0, 0.0, 0.0);
        }
    }

    private void spawnHeadParticles() {
        boolean armored = this.isArmored();
        for (int i = 0; i < 3; i++) {
            double hx = getHeadX(i), hy = getHeadY(i), hz = getHeadZ(i);
            this.worldObj.spawnParticle("smoke",
                    hx + this.rand.nextGaussian() * 0.3,
                    hy + this.rand.nextGaussian() * 0.3,
                    hz + this.rand.nextGaussian() * 0.3, 0, 0, 0);
            if (armored && this.rand.nextInt(4) == 0) {
                this.worldObj.spawnParticle("mobSpell",
                        hx + this.rand.nextGaussian() * 0.3,
                        hy + this.rand.nextGaussian() * 0.3,
                        hz + this.rand.nextGaussian() * 0.3, 0.5, 0.0, 0.8);
            }
        }
        if (this.getInvulnerabilityTime() > 0) {
            for (int i = 0; i < 3; i++) {
                this.worldObj.spawnParticle("mobSpell",
                        this.posX + this.rand.nextGaussian(),
                        this.posY + this.rand.nextFloat() * 3.3,
                        this.posZ + this.rand.nextGaussian(), 0.5, 0.0, 0.8);
            }
        }
    }

    @Override
    public double getFearForThisEntity(double x, double y, double z) {
        // dead code dw about it
        double dist = this.getDistance(x,y,z);
        if(dist > 8) return 0;
        return 0.5f - 0.5f * (dist / 16);
    }
}