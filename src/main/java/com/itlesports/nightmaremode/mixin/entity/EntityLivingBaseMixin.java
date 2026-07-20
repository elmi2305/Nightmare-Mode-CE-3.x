package com.itlesports.nightmaremode.mixin.entity;

import api.achievement.AchievementEventDispatcher;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.minecraft.src.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity {
    @Unique private int lastTimeWasSnowballed;
    @Unique private EntityPlayer lastSnowBaller;

    @Shadow public abstract boolean isEntityAlive();

    @Shadow public abstract void addPotionEffect(PotionEffect par1PotionEffect);
    @Shadow protected EntityPlayer attackingPlayer;

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @ModifyArg(method = "entityLivingOnDeath", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EntityLivingBase;dropFewItems(ZI)V"), index = 1)
    private int applySkillMobLootChance(int lootingModifier) {
        if (this.attackingPlayer != null
                && this.rand.nextFloat() < SkillHandler.getPlayerData(this.attackingPlayer).mobLootChanceBonus
                        + SkillHandler.getWorldData(this.worldObj).globalMobLootChanceBonus) {
            return lootingModifier + 1;
        }
        return lootingModifier;
    }

    @Redirect(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isChild()Z"))
    private boolean babyZombiesInMvDropTheirItems(EntityLivingBase e){
        if(NightmareMode.moreVariants){
            if(e instanceof EntityZombie) return false; // baby zombies drop items like adult zombies do
        }
        return e.isChild();
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void increaseStepHeightSlightlyForAsphalt(World par1World, CallbackInfo ci){
        this.stepHeight = 0.6f;
    }
    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.85f))
    private float modifyWitherSkeletonSight(float constant){
        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;
        if(this.worldObj == null) return constant;
        if(thisObj instanceof EntitySkeleton skeleton && skeleton.getSkeletonType().id() == 1){
            return 0.6f;
        }
        return constant;
    }
    @ModifyConstant(method = "moveEntityWithHeading", constant = @Constant(doubleValue = 0.2))
    private double modifyLadderClimbRateBasedOnLadder(double constant){
        int blockX = MathHelper.floor_double(this.posX);
        int blockY = MathHelper.floor_double(this.boundingBox.minY);
        int blockZ = MathHelper.floor_double(this.posZ);

        int blockID = this.worldObj.getBlockId(blockX, blockY, blockZ);
        if(blockID == NMBlocks.stoneLadder.blockID){
            return NMBlocks.stoneLadder.getSpeedModifier();
        } else if (blockID == NMBlocks.ironLadder.blockID){
            return NMBlocks.ironLadder.getSpeedModifier();
        }
        return constant;
    }

    @Redirect(method = "updatePotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;spawnParticle(Ljava/lang/String;DDDDDD)V"))
    private void removePotionParticlesConfig(World world, String s, double d, double par2, double par4, double par6, double par8, double par10){
        if(NightmareMode.potionParticles){
            world.spawnParticle(s,d,par2,par4,par6,par8,par10);
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void manageBloodMoonKills(DamageSource source, CallbackInfo ci){
        if(source.getEntity() instanceof EntityPlayer player && Objects.equals(source.damageType, "player")){
            if(NMUtils.isWearingFullBloodArmor(player)){
                int chance = NMUtils.getIsBloodMoon() ? 2 : 3;
                if(this.rand.nextInt(chance) == 0){
                    PotionEffect activeStrengthPotion = player.getActivePotionEffect(Potion.damageBoost);
                    if(activeStrengthPotion != null && activeStrengthPotion.getAmplifier() == 0){

                        if(rand.nextInt(5) == 0 && activeStrengthPotion.getDuration() <= 100){
                            this.addPotion(player,Potion.damageBoost.id, 1);
                        }

                        this.addPotion(player,Potion.regeneration.id, 0);
                    } else {
                        this.addPotion(player, Potion.damageBoost.id, 0);
                    }
                }
                player.heal(rand.nextInt(2)+1);
            }
            if(NMUtils.isHoldingBloodSword(player)){
                player.getHeldItem().setItemDamage(Math.max(player.getHeldItem().getItemDamage() - this.rand.nextInt(4) - 1, 0));
                this.mendTools(player);
                if (this.rand.nextInt(24) == 0) {
                    this.dropItem(NMItems.bloodOrb.itemID,1);
                }
                this.increaseArmorDurabilityRandomly(player);
            }
        }
        EntityLivingBase thisObj = (EntityLivingBase)(Object) this;
        if (thisObj instanceof EntityAnimal && thisObj instanceof CarcassAnimal carcass) {
            carcass.nm$becomeCarcass(source);
            ci.cancel();
        }
    }

    @Inject(method = "entityInit", at = @At("TAIL"))
    private void addCarcassWatchers(CallbackInfo ci) {
        if ((Object)this instanceof EntityAnimal) {
            this.dataWatcher.addObject(27, (byte)0);
            this.dataWatcher.addObject(28, -1);
            this.dataWatcher.addObject(29, 0);
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void tickAnimalCarcass(CallbackInfo ci) {
        if ((Object)this instanceof CarcassAnimal carcass) {
            carcass.nm$tickCarcass();
        }
    }

    @Inject(method = "moveEntityWithHeading", at = @At("HEAD"), cancellable = true)
    private void preventCarcassMovement(float strafe, float forward, CallbackInfo ci) {
        if ((Object)this instanceof CarcassAnimal carcass && carcass.nm$isCarcass()) {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            ci.cancel();
        }
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeCarcassData(NBTTagCompound tag, CallbackInfo ci) {
        if ((Object)this instanceof CarcassAnimal carcass) {
            carcass.nm$writeCarcassToNBT(tag);
        }
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readCarcassData(NBTTagCompound tag, CallbackInfo ci) {
        if ((Object)this instanceof CarcassAnimal carcass) {
            carcass.nm$readCarcassFromNBT(tag);
        }
    }

    @Inject(method = "handleHealthUpdate", at = @At("HEAD"), cancellable = true)
    private void handleCarcassPoof(byte state, CallbackInfo ci) {
        if (state == 30 && (Object)this instanceof CarcassAnimal carcass) {
            carcass.nm$spawnCarcassPoof();
            ci.cancel();
        }
    }

    @Unique private void increaseArmorDurabilityRandomly(EntityLivingBase player){
        int j = rand.nextInt(5);
        for (int a = 0; a < 2; a++) {
            int i = rand.nextInt(4) + 1;
            if(player.getCurrentItemOrArmor(i) == null) continue;
            player.getCurrentItemOrArmor(i).setItemDamage(Math.max(player.getCurrentItemOrArmor(i).getItemDamage() - j,0));
        }
    }


    @Unique private void addPotion(EntityLivingBase entity, int potionID, int amp){
        entity.addPotionEffect(new PotionEffect(potionID, 100, amp));
    }
    @Unique private void mendTools(EntityPlayer player){
        for(ItemStack stack : player.inventory.mainInventory){
            if (stack == null) continue;
            if (stack.getMaxStackSize() != 1) continue;
            if(NMUtils.bloodTools.contains(stack.itemID)){
                if(rand.nextInt(4) == 0) continue;
                this.increaseDurabilityIfPossible(stack);
            }
        }
    }

    @Unique private void increaseDurabilityIfPossible(ItemStack stack){
        int i = rand.nextInt(4) + 1;
        stack.setItemDamage(Math.max(stack.getItemDamage() - i,0));
    }



}
