package com.itlesports.nightmaremode.mixin;

import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.block.blocks.BedrollBlock;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.LightningBoltEntity;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

import static com.itlesports.nightmaremode.NightmareUtils.chainArmor;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements EntityAccessor {
    @Unique private boolean hasUpdated = false;
    @Shadow public abstract ItemStack getHeldItem();
    @Shadow protected abstract boolean isPlayer();
    @Shadow public PlayerCapabilities capabilities;
    @Shadow protected abstract boolean isInBed();
    @Shadow public FoodStats foodStats;
    @Shadow public abstract boolean attackEntityFrom(DamageSource par1DamageSource, float par2);
    @Shadow public abstract void playSound(String par1Str, float par2, float par3);

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

                    // can't jump if you have slowness
    @Inject(method = "canJump", at = @At("RETURN"), cancellable = true)
    private void cantJumpIfSlowness(CallbackInfoReturnable<Boolean> cir){
        if(this.isPotionActive(Potion.moveSlowdown) && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isImmuneToHeadCrabDamage", at = @At("HEAD"),cancellable = true)
    private void notImmuneToSquidsEclipse(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsEclipse()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
    private void manageLifeSteal(Entity entity, CallbackInfo ci){
        if(entity instanceof EntityLiving && NightmareUtils.isHoldingBloodSword(this) && entity.hurtResistantTime == 0 && !this.isPotionActive(Potion.weakness) && !(entity instanceof EntityWither)){
            int chance = 20 - NightmareUtils.getBloodArmorWornCount(this) * 3;
            // 20, 16, 12, 8, 4

            if(rand.nextInt(chance) == 0){
                this.heal(rand.nextInt(chance) == 0 ? 2 : 1);
            }

            if(rand.nextInt((int) (chance / 1.5)) == 0 && this.foodStats.getFoodLevel() < 57){
                this.foodStats.setFoodLevel(this.foodStats.getFoodLevel() + 3);
            }

            this.increaseArmorDurabilityRandomly(this);

            if(NightmareUtils.isWearingFullBloodArmor(this)){
                if((this.rand.nextInt(3) == 0) && this.fallDistance > 0.0F){
                    this.heal(1f);
                }
            }
        }
    }

//    @Inject(method = "onUpdate", at = @At("HEAD"))
//    private void freelook(CallbackInfo ci) {
//        if(AddonHandler.modList.keySet().toString().contains("FreeLook")){
//            if(!this.isPotionActive(Potion.blindness)){
//                ChatMessageComponent text2 = new ChatMessageComponent();
//                text2.addText("<???> Using FreeLook? Pathetic. Seeing clearer won’t save you. You can’t cheat your way out of the darkness.");
//                text2.setColor(EnumChatFormatting.RED);
//                MinecraftServer.getServer().getConfigurationManager().sendChatMsg(text2);
//            }
//            this.addPotionEffect(new PotionEffect(Potion.blindness.id, 100));
//            if(this.rand.nextInt(40) == 0){
//                this.worldObj.playSoundEffect(this.posX,this.posY,this.posZ,"mob.wither.death",2.0F,0.905F);
//            }
//            if(this.worldObj.getWorldTime() % 100 == 99){
//                Entity lightningbolt = new LightningBoltEntity(this.worldObj, this.posX, this.posY, this.posZ);
//                this.worldObj.addWeatherEffect(lightningbolt);
//            }
//            if(this.worldObj.getWorldTime() % 400 == 399){
//                this.attackEntityFrom(DamageSource.outOfWorld, 200f);
//            }
//        }
//    }

    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
    private void punishPlayerForHittingChicken(Entity par1Entity, CallbackInfo ci){
        if(par1Entity instanceof EntityChicken chicken && NightmareUtils.getIsMobEclipsed(chicken)){
            Entity lightningbolt = new LightningBoltEntity(this.worldObj, this.posX, this.posY, this.posZ);
            this.worldObj.addWeatherEffect(lightningbolt);
        }
    }
    @Unique private void increaseArmorDurabilityRandomly(EntityLivingBase player){
        int j = rand.nextInt(4);
        for (int a = 0; a < 3; a++) {
            int i = rand.nextInt(5);
            if(player.getCurrentItemOrArmor(i) == null) continue;
            player.getCurrentItemOrArmor(i).setItemDamage(Math.max(player.getCurrentItemOrArmor(i).getItemDamage() - j,0));
        }
    }

    @ModifyConstant(method = "addExhaustionForJump", constant = @Constant(floatValue = 0.2f))
    private float reduceExhaustion(float constant){
        if(NightmareMode.bloodmare){
            return 0.15f;
        }
        return 0.17f; // jump
    }
    @Inject(method = "jump", at = @At("HEAD"))
    private void a(CallbackInfo ci){
        NightmareUtils.updateItemStackSizes();
        if (!this.hasUpdated) {
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText("Potions and food now have double stack size!");
            ((EntityPlayer)(Object)this).sendChatToPlayer(text2);
        }
        this.hasUpdated = true;
    }
    @ModifyConstant(method = "addExhaustionForJump", constant = @Constant(floatValue = 1.0f))
    private float reduceExhaustion1(float constant){
        if(NightmareMode.bloodmare){
            return 0.5f;
        }
        return 0.75f; // sprint jump
    }
    @ModifyConstant(method = "attackTargetEntityWithCurrentItem", constant = @Constant(floatValue = 0.3f))
    private float reduceExhaustion2(float constant){
        if(NightmareMode.bloodmare){
            return 0.15f;
        }
        return 0.2f; // punch
    }

    @Inject(method = "onItemUseFinish", at = @At("HEAD"))
    private void manageWaterDrinking(CallbackInfo ci){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;
        if(thisObj.getItemInUse() != null && (thisObj.getItemInUse().itemID == Item.potion.itemID && thisObj.getItemInUse().getItemDamage() == 0)){
            if (this.isBurning()) {
                this.extinguish();
            }
            if(this.isPotionActive(Potion.confusion.id)){
                this.removePotionEffect(Potion.confusion.id);
            }
            if(this.isPotionActive(Potion.blindness.id)){
                this.removePotionEffect(Potion.blindness.id);
            }
            if(this.isPotionActive(Potion.weakness.id)){
                this.removePotionEffect(Potion.weakness.id);
            }
            if(this.isPotionActive(Potion.moveSlowdown.id)){
                this.removePotionEffect(Potion.moveSlowdown.id);
            }
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageBlightMovement(CallbackInfo ci){
        if (this.worldObj.getBlockId(MathHelper.floor_double(this.posX),MathHelper.floor_double(this.posY-1),MathHelper.floor_double(this.posZ)) == BTWBlocks.aestheticEarth.blockID && !this.capabilities.isCreativeMode){
            EntityPlayer thisObj = (EntityPlayer)(Object)this;

            int i = MathHelper.floor_double(this.posX);
            int j = MathHelper.floor_double(this.posY-1);
            int k = MathHelper.floor_double(this.posZ);

            if(this.worldObj.getBlockMetadata(i,j,k) == 0){
                this.addPlayerPotionEffect(thisObj,Potion.weakness.id);
            } else if (this.worldObj.getBlockMetadata(i,j,k) == 1){
                this.addPlayerPotionEffect(thisObj,Potion.poison.id);
            } else if (this.worldObj.getBlockMetadata(i,j,k) == 2){
                this.addPlayerPotionEffect(thisObj,Potion.wither.id);
                this.addPlayerPotionEffect(thisObj,Potion.moveSlowdown.id);
            } else if (this.worldObj.getBlockMetadata(i,j,k) == 4){
                this.addPlayerPotionEffect(thisObj,Potion.wither.id);
                this.addPlayerPotionEffect(thisObj,Potion.moveSlowdown.id);
                this.addPlayerPotionEffect(thisObj,Potion.blindness.id);
                this.addPlayerPotionEffect(thisObj,Potion.weakness.id);
            }
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void managePotionsDuringBloodArmor(CallbackInfo ci){
        Collection activePotions = this.getActivePotionEffects();
        if (NightmareUtils.isWearingFullBloodArmorWithoutSword(this)) {
            for(Object activePotion : activePotions){
                if(activePotion == null) continue;
                PotionEffect tempPotion = (PotionEffect) activePotion;
                tempPotion.duration = Math.max(tempPotion.duration - 1, 0);
            }
        }
    }
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageChainArmor(CallbackInfo ci){
        if(isWearingFullChainArmor(this) && !areChainPotionsActive(this)){
            this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 110,0));
            if(this.rand.nextInt(16) == 0){
                this.addPotionEffect(new PotionEffect(BTWMod.potionFortune.id, 600, 0));
            }
            if(this.rand.nextInt(16) == 0){
                this.addPotionEffect(new PotionEffect(BTWMod.potionLooting.id, 600, 0));
            }
            if(this.rand.nextInt(16) == 0){
                this.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 110,1));
            } else{
                this.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 110,0));
            }
        }
    }

    @Unique private static boolean areChainPotionsActive(EntityLivingBase player){
        return player.isPotionActive(Potion.digSpeed) || player.isPotionActive(Potion.moveSpeed);
    }
    @Unique private static boolean isWearingFullChainArmor(EntityLivingBase entity){
        for(int i = 1; i < 5; i++){
            if(entity.getCurrentItemOrArmor(i) == null){return false;}
            if(entity.getCurrentItemOrArmor(i).itemID == chainArmor.get(i - 1)) continue;
            return false;
        }
        return true;
    }

    @Unique private void addPlayerPotionEffect(EntityPlayer player, int potionID){
        if(!player.isPotionActive(potionID) || potionID == Potion.blindness.id){
            player.addPotionEffect(new PotionEffect(potionID,81,0));
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageRunningFromPlayer(CallbackInfo ci){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;
        if (thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            double range = NightmareUtils.getIsEclipse() ? 3 : 5;

            List list = thisObj.worldObj.getEntitiesWithinAABBExcludingEntity(thisObj, thisObj.boundingBox.expand(range, range, range));
            for (Object tempEntity : list) {
                if (!(tempEntity instanceof EntityAnimal tempAnimal)) continue;
                if (tempAnimal instanceof EntityWolf) continue;
                if (NightmareUtils.getIsMobEclipsed(tempAnimal)) {
                    if(tempAnimal instanceof EntityChicken) continue;
                    if(tempAnimal instanceof EntityPig && tempAnimal.worldObj.isRemote){
                        tempAnimal.setDead();
                        this.worldObj.newExplosion(tempAnimal,tempAnimal.posX,tempAnimal.posY,tempAnimal.posZ,5f,false,false);
                        break;
                    }
                }
                if(!((!thisObj.isSneaking() || checkNullAndCompareID(thisObj.getHeldItem())) && !tempAnimal.getLeashed())) continue;
                ((EntityAnimalInvoker) tempAnimal).invokeOnNearbyPlayerStartles(thisObj);

                break;
            }
        }
    }



    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void slowIfInWeb(CallbackInfo ci){
        if(this.isInWeb) {
            this.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 10, 3));
            this.addPotionEffect(new PotionEffect(Potion.weakness.id, 10, 1));
        }
    }

    // removes the check for daytime and kicking the player out of the bed if it turns day. this enables infinite sleeping
    @Redirect(method = "sleepInBedAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean doNotCareIfDay(World instance){
        if (!(Block.blocksList[this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))] instanceof BedrollBlock)) {
            return false;
        } else {
            return this.worldObj.skylightSubtracted < 4;
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean doNotCareIfDay1(World instance) {
        if (!(Block.blocksList[this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))] instanceof BedrollBlock)) {
            return false;
        } else {
            return this.worldObj.skylightSubtracted < 4;
        }
    }

    @Redirect(method = "sleepInBedAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldProvider;isSurfaceWorld()Z"))
    private boolean canSleepInNether(WorldProvider instance){
        return true;
    }
    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;setTimerSpeedModifier(F)V"))
    private void manageInvisibilityWhileSleeping(CallbackInfo ci){
        if (this.isInBed()) {
            this.addPotionEffect(new PotionEffect(Potion.invisibility.id, 10,0));
        }
    }

    @ModifyConstant(method = "movementModifierWhenRidingBoat", constant = @Constant(doubleValue = 0.35))
    private double windmillSpeedBoat(double constant){
        EntityPlayer thisObj = (EntityPlayer)(Object)this;
        if(isPlayerHoldingWindmill(thisObj)){
            return 5.0;
        }
        return constant;
    }

    @Unique private boolean isPlayerHoldingWindmill(EntityPlayer player) {
        ItemStack currentItemStack = player.inventory.mainInventory[player.inventory.currentItem];
        if (currentItemStack != null) {
            return currentItemStack.itemID == BTWItems.windMill.itemID;
        }
        return false;
    }

    @Unique
    public boolean checkNullAndCompareID(ItemStack par2ItemStack){
        if(par2ItemStack != null){
            switch(par2ItemStack.itemID){
                case 2,11,19,20,23,27,30,267,276,22580:
                    return true;
            }
        }
        return false;
    }
}
