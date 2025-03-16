package com.itlesports.nightmaremode.mixin;

import btw.entity.LightningBoltEntity;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityPlayerMP.class)

public abstract class EntityPlayerMPMixin extends EntityPlayer {
    @Unique boolean isTryingToEscapeBloodMoon = true;
    @Shadow public MinecraftServer mcServer;
    @Shadow public abstract void sendChatToPlayer(ChatMessageComponent par1ChatMessageComponent);

    @Unique int steelModifier;
    public EntityPlayerMPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }
    @Inject(method="updateGloomState", at = @At("HEAD"))
    public void incrementInGloomCounter(CallbackInfo info) {
        if (this.getGloomLevel() > 0) {
            this.inGloomCounter += 5; // gloom goes up 6x faster
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void manageChickenRider(CallbackInfo ci){
        if(this.ridingEntity instanceof EntityChicken && NightmareUtils.getIsMobEclipsed((EntityChicken)this.ridingEntity)) {
            this.ridingEntity.rotationYaw = this.rotationYaw;
            this.ridingEntity.rotationPitch = this.rotationPitch * 0.5F;

            float strafe = this.moveStrafing;
            float forward = this.moveForward;
            float speed = 0.018f;

            // Increase speed when sprinting
            if (this.isSprinting()) {
                speed = 0.023f;
            }

            // Allow horse to fly up if the player is holding jump
            if (this.isJumping) {
                this.ridingEntity.motionY = 0.12;
            }

            // Move the horse
            this.ridingEntity.moveFlying(strafe, forward, speed);

            if (!this.worldObj.isRemote) {
                super.moveEntityWithHeading(strafe, forward);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double var8 = this.posX - this.prevPosX;
            double var5 = this.posZ - this.prevPosZ;
            float var7 = MathHelper.sqrt_double(var8 * var8 + var5 * var5) * 4.0F;

            if (var7 > 1.0F) {
                var7 = 1.0F;
            }

            this.limbSwingAmount += (var7 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
    }

    @Redirect(method = "isInGloom", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;isPotionActive(Lnet/minecraft/src/Potion;)Z"))
    private boolean manageGloomDuringBloodMoon(EntityPlayerMP player, Potion potion){
        if(NightmareUtils.getIsBloodMoon()){
            return false;
        }
        return player.isPotionActive(potion);
    }


    @Inject(method = "isInGloom", at = @At("HEAD"),cancellable = true)
    private void noGloomIfWearingEnderSpectacles(CallbackInfoReturnable<Boolean> cir){
        if(this.getCurrentItemOrArmor(4) != null && this.getCurrentItemOrArmor(4).itemID == BTWItems.enderSpectacles.itemID){
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "travelToDimension",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityPlayerMP;triggerAchievement(Lnet/minecraft/src/StatBase;)V",
                    ordinal = 2),cancellable = true)
    private void initialiseNetherThreeDayPeriod(int par1, CallbackInfo ci){
        if (NightmareUtils.getWorldProgress(this.worldObj) > 0 && this.dimension == 0) {
            int dayCount = ((int)Math.ceil((double) this.worldObj.getWorldTime() / 24000)) + (this.worldObj.getWorldTime() % 24000 >= 23459 ? 1 : 0);
            if(NightmareUtils.getIsBloodMoon() || (dayCount % 16 >= 8 && dayCount % 16 <= 9)){
                if(this.isTryingToEscapeBloodMoon){
                    ChatMessageComponent text1 = new ChatMessageComponent();
                    text1.addKey("player.attemptEscapeBloodmoon");
                    text1.setColor(EnumChatFormatting.DARK_RED);
                    this.sendChatToPlayer(text1);
                    this.isTryingToEscapeBloodMoon = false;
                }
                ci.cancel();
            }
        }
    }
    @Inject(method = "getEyeHeight", at = @At(value = "HEAD"),cancellable = true)
    private void increaseEyeHeightWhileSleeping(CallbackInfoReturnable<Float> cir){
        if(this.sleeping){
            cir.setReturnValue(0.8f);
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageNetherThreeDayPeriod(CallbackInfo ci){
//        NightmareMode.getInstance().setCanLeaveGame(((this.worldObj.getWorldTime() <= this.targetTime - 72000) && this.dimension != -1) || NightmareUtils.getWorldProgress(this.worldObj) != 0);

        if(this.ticksExisted % 20 != 0) return;
        long targetTime = this.worldObj.worldInfo.getNBTTagCompound().getLong("PortalTime");
        if(targetTime != 0 && this.worldObj.getWorldTime() > targetTime && !WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()){
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addKey("world.hardmodeBegin");
            text2.setColor(EnumChatFormatting.DARK_RED);
            this.sendChatToPlayer(text2);
            this.playSound("mob.wither.death",0.9f,0.905f);
            WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
        }
        if(this.worldObj.getWorldTime() % 24000 == 0){
            this.isTryingToEscapeBloodMoon = true;
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addStat(Lnet/minecraft/src/StatBase;I)V", shift = At.Shift.AFTER))
    private void smitePlayer(DamageSource par1DamageSource, CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE && !MinecraftServer.getIsServer()) {
            Entity lightningbolt = new LightningBoltEntity(this.getEntityWorld(), this.posX, this.posY-0.5, this.posZ);
            getEntityWorld().addWeatherEffect(lightningbolt);

            // SUMMONS EXPLOSION. explosion does tile and entity damage. effectively kills all dropped items.
            double par2 = this.posX;
            double par4 = this.posY;
            double par6 = this.posZ;
            float par8 = 3.0f;
            this.worldObj.createExplosion(null, par2, par4, par6, par8, true);
        }
    }

    @Redirect(method = "onStruckByLightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;dealFireDamage(I)V"))
    private void dealMagicDamage(EntityPlayerMP instance, int i){
        this.attackEntityFrom(DamageSource.magic, 5f+this.rand.nextInt(3));
        // makes fire resistance not bypass the lightning damage
    }

        // makes lightning give a few other effects with higher amplifier
    @Inject(method = "onStruckByLightning",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void givePlayerSlowness(LightningBoltEntity boltEntity, CallbackInfo ci){
        EntityPlayerMP thisObj = (EntityPlayerMP)(Object)this;
        steelModifier = 0;
        if(isPlayerWearingItem(thisObj, BTWItems.plateBoots,1)){
            steelModifier += 1;
        }
        if(isPlayerWearingItem(thisObj, BTWItems.plateLeggings,2)){
            steelModifier += 3;
        }
        if(isPlayerWearingItem(thisObj, BTWItems.plateBreastplate,3)) {
            steelModifier += 5;
        }
        if(isPlayerWearingItem(thisObj, BTWItems.plateHelmet,4) || isPlayerWearingItem(thisObj, BTWItems.enderSpectacles,4)) {
            steelModifier += 1;
        }

        this.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),120 - steelModifier * 10,10 - steelModifier,true));
        this.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(),800 - steelModifier * 79,3,true));
        this.addPotionEffect(new PotionEffect(Potion.confusion.getId(),300 - steelModifier * 28,0,true));
        this.addPotionEffect(new PotionEffect(Potion.blindness.getId(),300 - steelModifier * 28,0,true));
        this.addPotionEffect(new PotionEffect(Potion.weakness.getId(),800 - steelModifier * 75,1,true));
    }

    @Unique private boolean isPlayerWearingItem(EntityPlayerMP player, Item itemToCheck, int armorIndex){
        // armor indices: boots 1, legs 2, chest 3, helmet 4, held item 0
        return player.getCurrentItemOrArmor(armorIndex) != null && player.getCurrentItemOrArmor(armorIndex).itemID == itemToCheck.itemID;
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ServerConfigurationManager;sendChatMsg(Lnet/minecraft/src/ChatMessageComponent;)V",shift = At.Shift.AFTER))
    private void manageTauntingChatMessage(DamageSource par1DamageSource, CallbackInfo ci){
        if (NightmareUtils.getWorldProgress(this.worldObj) != 3) {
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addKey(getDeathMessages().get(this.rand.nextInt(getDeathMessages().size())));
            text2.setColor(EnumChatFormatting.RED);
            this.mcServer.getConfigurationManager().sendChatMsg(text2);
        }
    }

    @Unique
    private static @NotNull List<String> getDeathMessages() {
        List<String> messageList = new ArrayList<>();
        for (int i = 1; i <= 19; i++){
            messageList.add("deathScreen.deathTauntMessage"+i);
        }
        return messageList;
    }

    @Inject(method = "travelToDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;triggerAchievement(Lnet/minecraft/src/StatBase;)V",ordinal = 1))
    private void manageEndDialogue(int par1, CallbackInfo ci){
        ChatMessageComponent text2 = new ChatMessageComponent();
        text2.addKey("bosses.dragons.journeyEnd");
        text2.setColor(EnumChatFormatting.LIGHT_PURPLE);
        this.mcServer.getConfigurationManager().sendChatMsg(text2);
        // need to figure out how to make this not happen every time the player goes to the end
    }
}
