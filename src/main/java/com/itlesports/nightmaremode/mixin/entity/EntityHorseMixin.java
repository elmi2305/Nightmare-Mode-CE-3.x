package com.itlesports.nightmaremode.mixin.entity;

import api.entity.mob.KickingAnimal;
import api.world.WorldUtils;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.block.blocks.BlockRoad;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import com.itlesports.nightmaremode.util.interfaces.IHorseTamingClient;
import com.itlesports.nightmaremode.util.interfaces.IPlayerDirectionTracker;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(EntityHorse.class)
public abstract class EntityHorseMixin extends KickingAnimal implements IHorseTamingClient, IPlayerDirectionTracker {


    @Shadow
    private AnimalChest horseChest;


    @Shadow
    public abstract boolean isTame();

    @Shadow
    public abstract boolean isHorseJumping();

    @Shadow
    protected abstract void func_110237_h(EntityPlayer par1EntityPlayer);

    @Unique private int swimmingTicks;
    @Unique private int kickCooldown = 20;


    public EntityHorseMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 20.0d))
    private double increaseHP(double constant){
        return (24.0 + NMUtils.getWorldProgress() * 6) * NMUtils.getNiteMultiplier();
    }


    @Unique
    private float getInternalSpeedModifier() {
        float fMoveSpeed = 1.0f;
        if (this.onGround && this.isAffectedByMovementModifiers()) {
            Block blockOn;
            int iGroundK;
            int iGroundJ;
            int iGroundI = MathHelper.floor_double(this.posX);
            if (WorldUtils.isGroundCoverOnBlock(this.worldObj, iGroundI, iGroundJ = MathHelper.floor_double(this.posY - 0.03 - (double)this.yOffset), iGroundK = MathHelper.floor_double(this.posZ))) {
                fMoveSpeed *= 0.8f;
            }
            if ((blockOn = Block.blocksList[this.worldObj.getBlockId(iGroundI, iGroundJ, iGroundK)]) == null || blockOn.getCollisionBoundingBoxFromPool(this.worldObj, iGroundI, iGroundJ, iGroundK) == null) {
                float fHalfWidth = this.width / 2.0f;
                int iCenterGroundI = iGroundI;
                iGroundI = MathHelper.floor_double(this.posX + (double)fHalfWidth);
                blockOn = Block.blocksList[this.worldObj.getBlockId(iGroundI, iGroundJ, iGroundK)];
                if (!(blockOn != null && blockOn.getCollisionBoundingBoxFromPool(this.worldObj, iGroundI, iGroundJ, iGroundK) != null || (blockOn = Block.blocksList[this.worldObj.getBlockId(iGroundI = MathHelper.floor_double(this.posX - (double)fHalfWidth), iGroundJ, iGroundK)]) != null && blockOn.getCollisionBoundingBoxFromPool(this.worldObj, iGroundI, iGroundJ, iGroundK) != null || (blockOn = Block.blocksList[this.worldObj.getBlockId(iGroundI = iCenterGroundI, iGroundJ, iGroundK = MathHelper.floor_double(this.posZ + (double)fHalfWidth))]) != null && blockOn.getCollisionBoundingBoxFromPool(this.worldObj, iGroundI, iGroundJ, iGroundK) != null)) {
                    iGroundK = MathHelper.floor_double(this.posZ - (double)fHalfWidth);
                    blockOn = Block.blocksList[this.worldObj.getBlockId(iGroundI, iGroundJ, iGroundK)];
                }
            }
            if (blockOn != null) {
                fMoveSpeed *= blockOn.getMovementModifier(this.worldObj, iGroundI, iGroundJ, iGroundK);
                if(blockOn instanceof BlockRoad){
                    fMoveSpeed *= 1.25f;
                }
            }
            fMoveSpeed *= this.getLandMovementModifier();
        }
        if (fMoveSpeed < 0.0f) {
            fMoveSpeed = 0.0f;
        }

        
        return fMoveSpeed;
    }

    @Override
    public float getAIMoveSpeed() {
        float modifier = this.getWeightFromArmor();
        float speedModifier = this.isPotionActive(Potion.moveSpeed) ? (float) (1.2f + (0.2 * (this.getActivePotionEffect(Potion.moveSpeed).getAmplifier() - 1))) : 1.0f;
        float internalSpeedModifier = this.getInternalSpeedModifier();
        return super.getAIMoveSpeed() * modifier * speedModifier * internalSpeedModifier;
    }

    @Unique private float getWeightFromArmor(){
        float f = 1.0f;
        if(!this.isWearingArmor()) return f;
        if(this.horseChest.getStackInSlot(1).getItem() instanceof ItemAdvancedHorseArmor armor){
            f -= (armor.getArmorTier().exhaustion - 1) * 0.2f;
        }
        // gold / iron: 0.9f
        // diamond: 0.8f
        return f;
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,4);
    }

    @Inject(method = "isSubjectToHunger", at = @At("HEAD"),cancellable = true)
    private void manageEclipseHunger(CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsMobEclipsed(this)){
            cir.setReturnValue(false);
        }
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

            int itemID = NMItems.speedCoil.itemID;

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

    @Override
    public boolean isPossessed() {
        return false;
    }

    @Override
    public boolean getCanCreatureBePossessedFromDistance(boolean bPersistentSpirit) {
        return false;
    }
    @Unique private int applyArmorMod(int a){
        if(!this.isWearingArmor()) return a;

        return MathHelper.ceiling_float_int(a * (this.getWeightFromArmor() * 2 - 0.1f));
        // gold/iron: 1.7x drain
        // diamond: 1.5x drain
    }
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void horseOnUpdate(CallbackInfo ci){

        // manage horse rider
        if (this.isTame()) {
            boolean client = this.worldObj.isRemote;
            if(this.riddenByEntity instanceof EntityPlayer p && !client){
                EntityHorse horseHost = (EntityHorse)(Object)this;
//                    System.out.println(this.hungerCountdown + " | " + this.getHungerLevel());

                this.hungerCountdown -= 3;
                if(this.isSprinting()){
                    this.hungerCountdown -= 2;
                }
                if(this.isHorseJumping()){
                    this.hungerCountdown -= 3;
                }

                if (this.ticksExisted % 4 == 0) {
                    boolean isFoodTick = this.ticksExisted % 2 == 0;
                    // every 8th tick. controls how fast the horse is allowed to eat from your inventory. otherwise it will instantly drain your food as its hungry
                    if (isFoodTick) {
                        if(this.isWearingArmor()){
                            ItemAdvancedHorseArmor horseArmorItem = (ItemAdvancedHorseArmor) this.horseChest.getStackInSlot(1).getItem();
                            if (this.eatFromArmor()) {
                                // ticks fuel down by 1
                                horseArmorItem.tickFuel(this.horseChest.getStackInSlot(1),horseHost);
                            }
                        }

                        if(this.eatFoodItem(p.getHeldItem())){
                            --p.getHeldItem().stackSize;
                            if (p.getHeldItem().stackSize <= 0) {
                                p.inventory.setInventorySlotContents(p.inventory.currentItem, null);
                            }
                        }
                    }


                    EntityMob mobTarget;
                    if((mobTarget = this.getClosestMob(this,2.0d)) != null && this.kickCooldown == 0){
                        ((KickingAnimalAccessor)this).invokeLaunchKickAttack();
                        mobTarget.onKickedByAnimal(this);
                        mobTarget.attackEntityFrom(DamageSource.generic, 6.0f);
                        this.kickCooldown = 25 + this.rand.nextInt(10) * 5;
                    }
                    if (this.kickCooldown > 0) {
                        this.kickCooldown--;
                    }

                }


                if(this.isFamished()){
                    if(this.rand.nextInt(200) == 0){
                        p.mountEntity(null);
                        this.onNearbyPlayerStartles(p);
                    }
                }
            }
        }
        // done horse riding



        if(this.ticksExisted % 120 != 0) return;
        int originalHealth = 24;
        double eclipseModifier = NMUtils.getIsEclipse() ? 1.5 : 1;
        if(this.getMaxHealth() != originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier);
        }

        float speed = (float) ((NMUtils.getIsMobEclipsed(this) ? 0.4f : 0.225f) * NMUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(speed);
    }


    @Unique
    private EntityMob getClosestMob(KickingAnimal horse, double radius) {
        List nearby = horse.worldObj.getEntitiesWithinAABB(
                EntityMob.class,
                horse.boundingBox.expand(radius, radius, radius)
        );

        EntityMob closest = null;
        double closestDistSq = Double.MAX_VALUE;

        for (Object obj : nearby) {
            EntityMob mob = (EntityMob) obj;
            double distSq = horse.getDistanceSqToEntity(mob);

            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = mob;
            }
        }

        return closest;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if (this.isWearingArmor()) {
            this.hungerCountdown -= (int) ((par2 + 2) * 4);
        }
        if(this.isTame() && this.riddenByEntity instanceof EntityPlayer p){
            if(p.rand.nextInt(16) == 0){
                p.mountEntity(null);
                this.onNearbyPlayerStartles(p);
            }
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Unique private boolean isWearingArmor(){
        return this.horseChest.getStackInSlot(1) != null;
    }

    @Unique private void eatFood(int amount){
        // increases hunger and does client animation for eating
        this.addToHungerCount(amount);
        this.worldObj.setEntityState(this, (byte)10);
        this.worldObj.playAuxSFX(2283, MathHelper.floor_double(this.posX), (int)(this.posY + (double)this.height), MathHelper.floor_double(this.posZ), 0);
    }

    @Unique private boolean eatFromArmor(){
        // checks null for armor, checks if armor has wheat, if conditions are right it eats the food in the armor
        ItemStack armor = this.horseChest.getStackInSlot(1);
        if(armor == null) return false;
        if(armor.getItemDamage() == armor.getMaxDamage()) return false;

        // eats wheat - default item
        return this.eatFoodItem(new ItemStack(BTWItems.wheat));
    }

    private static Map<Integer,Integer> FOOD_TICKS = new HashMap<>();
    private Map<Integer,Integer> getFoodItemMap(){
        if(FOOD_TICKS.isEmpty()) {
            FOOD_TICKS.put(BTWItems.wheat.itemID, 2200);
            FOOD_TICKS.put(BTWItems.straw.itemID, 2200);
            FOOD_TICKS.put(Item.appleRed.itemID, 1800);
            FOOD_TICKS.put(BTWItems.carrot.itemID, 2000);
            FOOD_TICKS.put(Item.goldenCarrot.itemID, 8000);
            FOOD_TICKS.put(Item.sugar.itemID, 1400);
            FOOD_TICKS.put(Item.netherStalkSeeds.itemID, 2000);
            FOOD_TICKS.put(BTWItems.mysteriousGland.itemID, 1400);
        }
        return FOOD_TICKS;
    }

    @Unique
    private boolean eatFoodItem(ItemStack itemStack) {
        if (itemStack == null) return false;

        Item item = itemStack.getItem();
        if (item == null) return false;

        int id = item.itemID;
        int foodValue = getFoodValue(id);

        if (foodValue == 0) return false;
        if (this.hungerCountdown > (24000 - foodValue)) return false;

        this.eatFood(foodValue);

        if (id == Item.appleRed.itemID) {
            addPotion(Potion.regeneration, 600);
        }
        else if (id == BTWItems.carrot.itemID) {
            if (this.rand.nextInt(4) == 0) {
                this.heal(1f);
            }
        }
        else if (id == Item.goldenCarrot.itemID) {
            this.heal(8f);
        }
        else if (id == Item.sugar.itemID) {
            addPotion(Potion.moveSpeed, 700);
        }
        else if (id == Item.netherStalkSeeds.itemID) {
            addPotion(Potion.fireResistance, 900);
        }
        else if (id == BTWItems.mysteriousGland.itemID) {
            addPotion(Potion.waterBreathing, 1200);
            this.setSwimmingTicks(1200);
        }

        return true;
    }

    @Unique
    private int getFoodValue(int id) {
        Integer value = this.getFoodItemMap().get(id);
        return value != null ? value : 0;
    }


    @Unique private void setSwimmingTicks(int i) {
        this.swimmingTicks = i;
    }

    @Unique private void addPotion(Potion type, int duration){
        if(this.isPotionActive(type.id)){
            this.removePotionEffect(type.id);
        }
        this.addPotionEffect(new PotionEffect(type.id, duration, 0));
    }

    @Inject(method = "interact", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityHorse;fleeingTick:I"), cancellable = true)
    private void allowHorseMounting(EntityPlayer player, CallbackInfoReturnable<Boolean> cir){
        ItemStack item = player.inventory.getCurrentItem();

        if(item != null) {
            this.eatFoodItem(item);
            // custom hand feeding code
        }

        // set mount and begin taming
        if(this.isChild()) return;
        this.func_110237_h(player);
        this.resetTamingFields();
        this.beginTamingProcess();
        cir.setReturnValue(true);
    }

    @Unique private void beginTamingProcess(){
        if(this.isTame()) return;
        EnumFacing dir = EnumFacing.values()[requiredDirection];
        // send updated direction to the client
        EntityHorse horseHost = (EntityHorse)(Object)this;
        if (!this.worldObj.isRemote) {
//            System.out.println("ran first direction send");
            NightmareMode.sendHorseDirectionToAll(horseHost, dir);
        }
    }


    @Override
    public void nm$setRequiredDirection(byte ordinal) { this.requiredDirection = ordinal; }
    @Override
    public byte nm$getRequiredDirection() { return this.requiredDirection; }
    @Override
    public void nm$setTamingProgress(int progress) { this.tamingProgress = progress; }
    @Override
    public int nm$getTamingProgress() { return tamingProgress; }





    @Unique private int tamingProgress;
    @Unique private byte requiredDirection = 2;
    @Unique private int rideTimeTicks = 0;
    @Unique private static final int TAME_THRESHOLD = 1000;

    @Override
    public boolean canSwim() {
        return super.canSwim() || this.swimmingTicks > 0;
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageTaming(CallbackInfo ci){
        if(this.swimmingTicks > 0){
            if (this.isInWater() || this.isInsideOfMaterial(Material.water) || this.worldObj.getBlockMaterial((int) this.posX, (int) (this.posY), (int) this.posZ) == Material.water) {
                    this.motionY = 0.15f;
                    this.fallDistance = 0;
            }
            this.swimmingTicks--;
        }

        // manage angry at rider
        if(this.entityToAttack == this.riddenByEntity){
            if(this.riddenByEntity != null){
                this.riddenByEntity.mountEntity(null);
            }
        }
        if(this.isTame()){
            this.uncomfortableTick = 0;
        }


        if(this.isTame()) return;
        if (!(this.riddenByEntity instanceof EntityPlayer)) return;
        EntityHorse horseHost = (EntityHorse) (Object)this;
        EntityPlayer player = (EntityPlayer) horseHost.riddenByEntity;

        // timeout mechanic
        rideTimeTicks++;
        if (rideTimeTicks >= 700) {
            player.mountEntity(null);
            this.onNearbyPlayerStartles(player);
            this.resetTamingFields();

            return;
        }
//        System.out.println(requiredDirection + (this.worldObj.isRemote ? " client" : " server"));

//        if(this.worldObj.isRemote) return;

        // server side packet-synced direction tracker
        EnumFacing held = ((IPlayerDirectionTracker) player).nm$getHeldDirection();

        // held is null on server, but accurate on client

        if(this.worldObj.isRemote) return;
//        System.out.println(held + " : " + EnumFacing.values()[requiredDirection] + " : " + requiredDirection + " : " + tamingProgress + " : " + player);


        double horseYawRad = Math.toRadians(horseHost.rotationYawHead);
        Vec3 horseForward = Vec3.createVectorHelper(
                -Math.sin(horseYawRad), // x
                0.0,
                Math.cos(horseYawRad)  // z
        ).normalize();

        double playerYawRad = Math.toRadians(player.rotationYawHead);
        Vec3 playerForward = Vec3.createVectorHelper(
                -Math.sin(playerYawRad), // x
                0.0,
                Math.cos(playerYawRad)  // z
        ).normalize();


        double dotProduct = horseForward.dotProduct(playerForward);

        int scoreGain = getScoreGain(dotProduct);
        // score gain: 12 per tick at >= 95%. 0 when dot product is negative. everything else is mapped 1:1

        // update taming progress
        if (held != null && held == EnumFacing.values()[requiredDirection]) {
            if (scoreGain > 0) {
                tamingProgress += scoreGain;
            }

            if (
                    horseHost.ticksExisted % 4 == 0 &&
                    horseHost.rand.nextInt(600) < tamingProgress
            ) {
                requiredDirection = (byte) (horseHost.getRNG().nextInt(4) + 2);

                EnumFacing dir = EnumFacing.values()[requiredDirection];
                // send new direction to the client
                NightmareMode.sendHorseDirectionToAll(horseHost, dir);
                horseHost.worldObj.setEntityState(horseHost, (byte)8);
            }

            // check for completion
            if (tamingProgress >= TAME_THRESHOLD) {
                horseHost.setTamedBy(player);
                horseHost.worldObj.setEntityState(horseHost, (byte)7);
                // Consider resetting rideTimeTicks or task
                this.resetTamingFields();
                return;
            }

        } else if (held != null) {
            // penalty for pressing wrong key
            tamingProgress = Math.max(0, tamingProgress - 3);
        } else{
            // penalty for not even trying
            tamingProgress = Math.max(0, tamingProgress - 5);
        }

        // packet spam. syncs progress to players for the GUI
        if (horseHost.ticksExisted % 2 == 0) {
            NightmareMode.sendHorseProgressToAll(horseHost, tamingProgress);
        }
    }


    @Unique private static int getScoreGain(double dotProduct) {
        double multiplier;

        // 18 degrees within 100% is fine
        if (dotProduct >= 0.95) {
            multiplier = 12.0;
        }

        else if (dotProduct < 0.0) {
            multiplier = 0.0;
        }
        else {
            // map [0.0, 0.95] to [0.0, 1.0], then multiply by 10
            multiplier = dotProduct * 10 / 0.95;
        }

        int baseGain = 1;
        int scoreGain = (int) Math.round(baseGain * multiplier);
        return scoreGain;
    }

    @Unique private void resetTamingFields(){
        this.rideTimeTicks = 0;
        this.tamingProgress = 0;
        this.requiredDirection = 2;
    }
}
