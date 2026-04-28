package com.itlesports.nightmaremode.block.tileEntities;

import api.block.TileEntityDataPacketHandler;
import btw.entity.mob.BTWSquidEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.EntityBloodAltar;
import com.itlesports.nightmaremode.entity.variants.EntityShadowZombie;
import com.itlesports.nightmaremode.entity.creepers.EntityFireCreeper;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class TileEntityBloodBone extends TileEntity implements TileEntityDataPacketHandler {
    private boolean activityState; // whether the ritual is going on or nah
    private byte ritualTicks; // counter that goes up once every 64 ticks. tends to overflow.
    private Set<EntityLivingBase> livingEntities; // entities it summoned
    private boolean isAngry; // server and client anger state
    public float xRot; // client only
    public float yRot; // client only
    public float zRot; // client only
    private boolean dead; // used because of tile entity nonsense that lets it remain active after deactivating. don't ask me how
    private int successfulIncrements; // differs from ritual ticks because it only increments when conditions are valid
    private final Random rand;
    private float spinSpeed = 1.0F; // how fast to vroom
    private ItemStack trackedItemStack; // the nether star item stack it's tracking
    private EntityBloodAltar markerEntity; // displays the boss bar, also tracks corpses for sacrifice
    private static final int threshHold = 128; // just for debug. how many successful ticks before finishing
    private int sacrifices = 0; // how many sacrifices it wants right now

    public TileEntityBloodBone() {
        super();
        this.livingEntities = new HashSet<>();
        this.rand = new Random();
    }
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        System.out.println("Reading from NBT");

        if(tag.hasKey("activityState")) {
            this.activityState = tag.getBoolean("activityState");
        }

        if(tag.hasKey("ritualTicks")) {
            this.ritualTicks = tag.getByte("ritualTicks");
        }

        if(tag.hasKey("angry")) {
            this.isAngry = tag.getBoolean("angry");
        }

        if(tag.hasKey("successfulIncrements")) {
            this.successfulIncrements = tag.getInteger("successfulIncrements");
        }
        if(tag.hasKey("trackedItemStack")) {
            NBTTagCompound tagTrackedItemStack = tag.getCompoundTag("trackedItemStack");
            this.trackedItemStack = new ItemStack(tagTrackedItemStack.getInteger("id"), 1, tagTrackedItemStack.getInteger("damage"));
        }

//        if(tag.hasKey("xRot")) {
//            this.xRot = tag.getFloat("xRot");
//        }
//        if(tag.hasKey("zRot")) {
//            this.zRot = tag.getFloat("zRot");
//        }
//        if(tag.hasKey("yRot")) {
//            this.yRot = tag.getFloat("yRot");
//        }

        if(this.livingEntities == null) {
            this.livingEntities = new HashSet<>();
        }

        NBTTagList list = tag.getTagList("entities");
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound currentTag = (NBTTagCompound) list.tagAt(i);
            int entityID = currentTag.getInteger("entityID");

            if(this.worldObj != null) {
                Entity entity = this.worldObj.getEntityByID(entityID);
                if (entity instanceof EntityLivingBase && entity.isEntityAlive()) {
                    this.livingEntities.add((EntityLivingBase) entity);
                }
            }
        }
        readCustomNBT(tag);
    }

    private Set<EntityLivingBase> validateSet(Set<EntityLivingBase> set) {
        Set<EntityLivingBase> validatedSet = new HashSet<>();
        for (EntityLivingBase entity : set) {
            if (entity != null && entity.isEntityAlive() && this.isActive()) {
                validatedSet.add(entity);
            } else {
                if(entity == null) {
                    System.out.println("Found Null entity");
                } else {
                    System.out.println("Found dead entry: " + entity.getEntityName());
                }
            }
        }
        if(validatedSet.isEmpty()) {
            System.out.println("Set was trashed");
            return null;
        }
        return validatedSet;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        System.out.println("Wrote to NBT");
        tag.setBoolean("activityState", this.activityState);
        tag.setByte("ritualTicks", this.ritualTicks);
        tag.setBoolean("angry", this.isAngry);
        tag.setInteger("successfulIncrements", this.successfulIncrements);
//        tag.setFloat("xRot", this.xRot);
//        tag.setFloat("zRot", this.zRot);
//        tag.setFloat("yRot", this.yRot);
        // rotation only set on client, doesn't save on server lol

        if (this.trackedItemStack != null) {
            NBTTagCompound tagTrackedItemStack = new NBTTagCompound();
            tagTrackedItemStack.setInteger("id", this.trackedItemStack.itemID);
            tagTrackedItemStack.setInteger("damage", this.trackedItemStack.getItemDamage());
        }


        if(this.livingEntities != null) {
            NBTTagList list = new NBTTagList();

            for (EntityLivingBase entity : this.livingEntities) {
                if (entity == null || !entity.isEntityAlive()) continue;
                NBTTagCompound currentTag = new NBTTagCompound();
                currentTag.setInteger("entityID", entity.entityId);
                list.appendTag(currentTag);
            }

            tag.setTag("entities", list);
        }
        writeCustomNBT(tag);
    }

    @Override
    public Block getBlockType() {
        return NMBlocks.bloodBonesUpgraded;
    }

    @Override
    public void updateEntity() {
        if(this.dead) return;
        super.updateEntity();

        World world = this.worldObj;
        boolean client = world.isRemote;

        if(!NMUtils.getIsBloodMoon()){
            this.cancelRitual();
            return;
        }
        if(!this.isActive()) return;

        if (client) {
            boolean mobsActive = !canIncrementRitual();

            // Smoothly adjust spin speed
            if(mobsActive) {
                this.spinSpeed = Math.max(0.0F, this.spinSpeed - 0.05F);
            } else {
                this.spinSpeed = Math.min(1.0F, this.spinSpeed + 0.05F);
            }

            float multiplier = this.getRotationSpeedMultiplier();

            this.xRot += 0.3F * this.spinSpeed * multiplier;
            this.yRot += 0.5F * this.spinSpeed * multiplier;
            this.zRot += 0.2F * this.spinSpeed * multiplier;

            if(this.isAngry && world.rand.nextInt(3) == 0) {
                this.spawnAngryParticles();
            }
        }

        if (!client && this.successfulIncrements >= threshHold) {
            this.completeRitual();
        }

        if ((world.getWorldTime() & 63) == 0) {
            this.checkForConditionsAndIncrement();
        }




    }

    public void readCustomNBT(NBTTagCompound tag) {
        System.out.println("Reading from custom NBT");

        if(tag.hasKey("activityState")) {
            this.activityState = tag.getBoolean("activityState");
        }

        if(tag.hasKey("ritualTicks")) {
            this.ritualTicks = tag.getByte("ritualTicks");
        }

        if(tag.hasKey("angry")) {
            this.isAngry = tag.getBoolean("angry");
        }

        if(tag.hasKey("successfulIncrements")) {
            this.successfulIncrements = tag.getInteger("successfulIncrements");
        }

        if(tag.hasKey("spinSpeed")) {
            this.spinSpeed = tag.getFloat("spinSpeed");
        }

        if(this.livingEntities == null) {
            this.livingEntities = new HashSet<>();
        }

        NBTTagList list = tag.getTagList("entities");
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound currentTag = (NBTTagCompound) list.tagAt(i);
            int entityID = currentTag.getInteger("entityID");

            if(this.worldObj != null) {
                Entity entity = this.worldObj.getEntityByID(entityID);
                if (entity instanceof EntityLivingBase && entity.isEntityAlive()) {
                    this.livingEntities.add((EntityLivingBase) entity);
                }
            }
        }
    }

    public void writeCustomNBT(NBTTagCompound tag) {
        System.out.println("Wrote custom NBT");
        tag.setBoolean("activityState", this.activityState);
        tag.setByte("ritualTicks", this.ritualTicks);
        tag.setBoolean("angry", this.isAngry);
        tag.setInteger("successfulIncrements", this.successfulIncrements);
        tag.setFloat("spinSpeed", this.spinSpeed);

        if(this.livingEntities != null) {
            NBTTagList list = new NBTTagList();

            for (EntityLivingBase entity : this.livingEntities) {
                if (entity == null || !entity.isEntityAlive()) continue;
                NBTTagCompound currentTag = new NBTTagCompound();
                currentTag.setInteger("entityID", entity.entityId);
                list.appendTag(currentTag);
            }

            tag.setTag("entities", list);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeCustomNBT(nbttagcompound);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, nbttagcompound);
    }

    private void syncToClients() {
        if (!this.worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }


    private void spawnAngryParticles() {
        for(int i = 0; i < 6; i++) {
            double px = this.xCoord + 0.5 + (this.rand.nextDouble() - 0.5) * 2.2;
            double py = this.yCoord + 0.5 + (this.rand.nextDouble() - 0.5) * 2.2;
            double pz = this.zCoord + 0.5 + (this.rand.nextDouble() - 0.5) * 2.2;

            this.worldObj.spawnParticle("reddust", px, py, pz, 0.0, 0.0, 0.0);
        }
    }

    private void spawnAngryParticlesAtPos(double x, double y, double z) {
        for(int i = 0; i < 16; i++) {
            double px = x + 0.5 + (this.rand.nextDouble() - 0.5) * 2.2;
            double py = y + 0.5 + (this.rand.nextDouble()) * 2.2;
            double pz = z + 0.5 + (this.rand.nextDouble() - 0.5) * 2.2;

            this.worldObj.spawnParticle("reddust", px, py, pz, 0.0, 0.0, 0.0);
        }
    }

    private void checkForConditionsAndIncrement() {
        EntityPlayer player = this.worldObj.getClosestPlayer(this.xCoord, this.yCoord, this.zCoord, 16);

        if(player == null){
            if(this.isAngry){
                this.cancelRitualAndExplode(5f);
            } else{
                this.setAngry(true);
                System.out.println("DEBUG: Player left area - block is now angry");
            }
            return;
        }

        if(this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) != NMBlocks.bloodBonesUpgraded.blockID){
            killSelf();
            return;
        }
        if(this.getSacrifices() > 5){
            this.setAngry(true);
        }
        if(this.getSacrifices() > 10){
            cancelRitualAndExplode(2);
            return;
        }

        if(isBeingObstructedByNeighbors(this.worldObj, this.xCoord, this.yCoord, this.zCoord)){
            if(this.isAngry){
                this.cancelRitualAndExplode(3f);
                System.out.println("DEBUG: Obstructed while angry - exploding");
                return;
            }
            this.worldObj.newExplosion(null, this.xCoord, this.yCoord + 0.5, this.zCoord, 2f, false, false);
            this.setAngry(true);
            System.out.println("DEBUG: Obstructed - block is now angry");
            return;
        }

        if(!canIncrementRitual()) {
            for(EntityLivingBase e : this.livingEntities) {
                spawnAngryParticlesAtPos(e.posX, e.posY, e.posZ);
            }

            System.out.println("DEBUG: Cannot increment - tracked entities still alive");
            return;
        }

        this.ritualTicks++;
        this.successfulIncrements++;
        syncToClients();
        System.out.println("DEBUG: Ritual ticks: " + this.getRitualTicks() + " | Successful increments: " + this.successfulIncrements);

        if(this.isAngry && this.successfulIncrements >= 8) {
            this.setAngry(false);
            this.successfulIncrements = 0;
            System.out.println("DEBUG: Anger state reverted after 10 successful increments");
        }

        if(this.getRitualTicks() % 4 == 0 && this.getRitualTicks() > 0){
            sacrifices++; // hungry
            System.out.println("DEBUG: Hungry! Need " + sacrifices + " sacrifices");
        }
        if(this.getRitualTicks() % 8 == 0 && this.getRitualTicks() > 0) {
            this.summonRitualMobs();
        }

        this.setActive(true);
    }

    private boolean canIncrementRitual() {
        if(this.livingEntities == null || this.livingEntities.isEmpty()) {
            return true;
        }

        Iterator<EntityLivingBase> iterator = this.livingEntities.iterator();
        while(iterator.hasNext()) {
            EntityLivingBase entity = iterator.next();
            if(entity == null || !entity.isEntityAlive()) {
                iterator.remove();
                System.out.println("DEBUG: Removed dead tracked entity");
            }
        }

        boolean canIncrement = this.livingEntities.isEmpty();
        if(!canIncrement) {
            System.out.println("DEBUG: " + this.livingEntities.size() + " tracked entities still alive");
        }
        return canIncrement;
    }
    private static final Class<? extends EntityLiving>[] summonableMobs = new Class[] {
            EntityZombie.class,
            EntityShadowZombie.class,
            EntitySkeleton.class,
            EntitySpider.class,
            EntityCreeper.class,
            EntityFireCreeper.class,
            EntityBlaze.class,
            BTWSquidEntity.class,
            EntityPigZombie.class
    };
    private EntityLiving createRandomMob() {
        try {
            Class<? extends EntityLiving> mobClass = summonableMobs[this.rand.nextInt(summonableMobs.length)];
            return mobClass.getConstructor(World.class).newInstance(this.worldObj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void summonRitualMobs() {
        if(this.worldObj.isRemote) return;

        System.out.println("DEBUG: Ritual milestone reached at tick " + this.getRitualTicks());

        this.worldObj.addWeatherEffect(new EntityLightningBolt(this.worldObj, this.xCoord, this.yCoord, this.zCoord));
        System.out.println("DEBUG: Lightning strike summoned");

        for(int i = 0; i < 3; i++) {
            double angle = this.rand.nextDouble() * Math.PI * 2;
            double distance = 3.0 + this.rand.nextDouble() * 3.0;

            double spawnX = this.xCoord + Math.cos(angle) * distance;
            double spawnZ = this.zCoord + Math.sin(angle) * distance;
            double spawnY = this.worldObj.getTopSolidOrLiquidBlock((int)spawnX, (int)spawnZ);

            EntityLiving mob = createRandomMob();
            if(mob == null) continue; // will ignore. not silently, because it will print a stacktrace
            NMUtils.setMobEclipsed(mob);
            mob.setPosition(spawnX, spawnY, spawnZ);
            this.worldObj.spawnEntityInWorld(mob);
            this.livingEntities.add(mob);

            System.out.println("DEBUG: Summoned zombie " + (i+1) + " at " + (int)spawnX + ", " + (int)spawnY + ", " + (int)spawnZ);
        }

        System.out.println("DEBUG: Now tracking " + this.livingEntities.size() + " entities");
    }


    private void completeRitual() {
        System.out.println("DEBUG: Ritual completed! Spawning item and removing block");

        float f = this.rand.nextFloat() * 0.8F + 0.1F;
        float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
        float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityitem = new EntityItem(
                this.worldObj,
                this.xCoord + f,
                this.yCoord + f1,
                this.zCoord + f2,
                new ItemStack(Item.swordIron)
        );


        float f3 = 0.05F;
        entityitem.motionX = (float)this.rand.nextGaussian() * f3;
        entityitem.motionY = (float)this.rand.nextGaussian() * f3 + 0.2F;
        entityitem.motionZ = (float)this.rand.nextGaussian() * f3;

        this.worldObj.spawnEntityInWorld(entityitem);
        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "mob.wither.death", 0.5f, 0.905F);

        this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0);
        this.killSelf();
    }

    private void killSelf(){
        this.cancelRitual();
        this.dead = true;
    }

    public static boolean isBeingObstructedByNeighbors(World world, int x, int y, int z) {
        if(!world.canBlockSeeTheSky(x,y,z)) return true;
        if(y > 80 || y < 60) return false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue; // doesn't gaf about itself
                    if (dx == 0 && dy == -1 && dz == 0) continue; // or the block below it

                    int neighborId = world.getBlockId(x + dx, y + dy, z + dz);
                    if (neighborId != 0) {
                        return true;
                    }
                }
            }
        }

        int fx = x + (world.rand.nextInt(8) * (world.rand.nextBoolean() ? 1 : -1));
        int fz = z + (world.rand.nextInt(8) * (world.rand.nextBoolean() ? 1 : -1));
        if(!world.canBlockSeeTheSky(fx, y, fz)) {
            System.out.println("Nearby Blocks cannot see the sky: x" + fx + ", y" +y + ", z" + fz);
            return true;
        }
        // randomly checks nearby blocks within a 8x8 square centered on itself (16 blocks wide, 16 blocks long) to see if they have blocked skylight. if they do, it will deem itself underground and get angry
        return false;
    }

    /**
     * Gets the ritual ticks
     * @return Returns the ritual ticks + 1, a number that is between 1 and 128 inclusive
     */
    private int getRitualTicks(){
        return this.ritualTicks + 1;
    }

    public int getSacrifices(){
        return this.sacrifices;
    }
    public void sacrifice(){
        this.sacrifices--;
    }

    public boolean isActive(){
        return this.activityState;
    }

    public boolean isAngry(){
        return this.isAngry;
    }

    public float getRotationSpeedMultiplier() {
        float anger = this.isAngry ? 2.0f : 1.0f;
        float progress = (float) this.getRitualTicks() / threshHold + 1.5f;
        return anger * progress;
    }

    public float getTwitchAmount() {
        return (this.isAngry ? 0.12F : 0.0F) * (((float) this.getRitualTicks() / threshHold) + 1);
    }

    public void setActive(boolean active){
        this.activityState = active;
        // DEBUG: spawning on client too
        if(active  && markerEntity == null) {
            markerEntity = new EntityBloodAltar(worldObj);
            markerEntity.bindToAltar(this);
            worldObj.spawnEntityInWorld(markerEntity);
            System.out.println("DEBUG: Spawned marker entity");
        }
    }
    @Override
    public void invalidate() {
        super.invalidate();
        if(markerEntity != null) {
            markerEntity.setDead();
        }
    }

    public void setItemStack(ItemStack stack){
        this.trackedItemStack = stack;
    }
    public void ejectItemStack(){
        if(this.trackedItemStack == null) return;
        if(this.worldObj.isRemote) return;
        if (this.trackedItemStack.getItemDamage() != this.trackedItemStack.getMaxDamage()) {
            float f = this.rand.nextFloat() * 0.8F + 0.1F;
            float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
            float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

            EntityItem entityitem = new EntityItem(
                    this.worldObj,
                    this.xCoord + f,
                    this.yCoord + f1,
                    this.zCoord + f2,
                    this.trackedItemStack
            );


            float f3 = 0.05F;
            entityitem.motionX = (float)this.rand.nextGaussian() * f3;
            entityitem.motionY = (float)this.rand.nextGaussian() * f3 + 0.2F;
            entityitem.motionZ = (float)this.rand.nextGaussian() * f3;

            this.worldObj.spawnEntityInWorld(entityitem);
        }

        this.worldObj.playAuxSFX(1000, MathHelper.floor_double(this.xCoord), MathHelper.floor_double(this.yCoord), MathHelper.floor_double(this.zCoord), 0);
    }

    private void setAngry(boolean angry){
        this.isAngry = angry;
        if(angry) {
            this.successfulIncrements = 0;
        }
        syncToClients();

    }

    private void setRitualTicks(int ritualTicks){
        this.ritualTicks = (byte) ritualTicks;
    }

    public void cancelRitual(){
        this.setRitualTicks(0);
        this.setActive(false);
        this.isAngry = false;
        this.successfulIncrements = 0;
        this.sacrifices = 0;
        if(this.livingEntities != null) {
            this.livingEntities.clear();
        }
        if(markerEntity != null) {
            markerEntity.setDead();
        }
        ejectItemStack();
        this.syncToClients();
    }

    private void cancelRitualAndExplode(float explosionStrength){
        this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0);
        this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, explosionStrength, true);
        this.cancelRitual();
    }
    public int getSuccessfulIncrements(){
        return this.successfulIncrements;
    }

    @Override
    public void readNBTFromPacket(NBTTagCompound tag) {
        readCustomNBT(tag);
        System.out.println("DEBUG: Client received sync packet - ticks: " + this.getRitualTicks() + ", angry: " + this.isAngry);
    }
}