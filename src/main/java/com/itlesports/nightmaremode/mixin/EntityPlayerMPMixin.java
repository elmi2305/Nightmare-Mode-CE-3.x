package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.network.IPlayerDirectionTracker;
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
public abstract class EntityPlayerMPMixin extends EntityPlayer implements IPlayerDirectionTracker {
    @Shadow public MinecraftServer mcServer;
    @Shadow public abstract void sendChatToPlayer(ChatMessageComponent par1ChatMessageComponent);

    @Unique int steelModifier;
    public EntityPlayerMPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }


    @Inject(method = "updateGloomState", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityPlayerMP;inGloomCounter:I",ordinal = 0))
    private void increaseGloomRate(CallbackInfo ci){
        this.inGloomCounter += 5; // gloom goes up 6x faster
    }

    @Unique public boolean isChickenTired(EntityChicken chicken){
        return chicken.isPotionActive(Potion.damageBoost);
        // mirror of isTired() in EntityChickenMixin
    }

    @Unique public boolean isChickenAirborne(EntityChicken chicken){
        return chicken.motionY != 0;
    }
    @Unique private EnumFacing serverHeldDirection;

    @Override
    public EnumFacing nm$getHeldDirection() {
        return serverHeldDirection;
    }

    @Override
    public void nm$setHeldDirectionServer(EnumFacing dir) {
        this.serverHeldDirection = dir;
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void manageChickenRider(CallbackInfo ci){
        if(this.ridingEntity instanceof EntityChicken chicken && NMUtils.getIsMobEclipsed(chicken)) {
            // Allow chicken to fly up if the player is holding jump

            if(chicken.motionY < -0.1){
                chicken.motionY = -0.1;
            }

            if(this.isChickenTired(chicken)) return;
            if (this.isJumping) {
                chicken.motionY = 0.12;
            }

            chicken.rotationYaw = this.rotationYaw;
            chicken.rotationPitch = this.rotationPitch * 0.5F;


            float strafe = this.moveStrafing;
            float forward = this.moveForward;
            float speed = 0.024f;

            // Increase speed when sprinting
            if (this.isSprinting()) {
                speed = 0.035f;
            }

            // Move the chicken
            if (this.isChickenAirborne(chicken)) {
                chicken.moveFlying(strafe, forward, speed);
            } else {
                if (!this.worldObj.isRemote) {
                    chicken.moveEntityWithHeading(strafe / 6, forward / 6);
                }
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



    @Inject(method = "isInGloom", at = @At("HEAD"),cancellable = true)
    private void noGloomIfWearingEnderSpectacles(CallbackInfoReturnable<Boolean> cir){
        if(this.getCurrentItemOrArmor(4) != null && this.getCurrentItemOrArmor(4).itemID == BTWItems.enderSpectacles.itemID){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void manageNetherThreeDayPeriod(CallbackInfo ci){
        if(this.ticksExisted % 20 != 0) return;

        long targetTime = this.worldObj.worldInfo.getNBTTagCompound().getLong("PortalTime");
        if(targetTime != 0 && this.worldObj.getWorldTime() > targetTime && !WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addKey("world.hardmodeBegin");
            text2.setColor(EnumChatFormatting.DARK_RED);
            this.sendChatToPlayer(text2);
            this.playSound("mob.wither.death", 0.9f, 0.905f);
            WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addStat(Lnet/minecraft/src/StatBase;I)V", shift = At.Shift.AFTER))
    private void smitePlayer(DamageSource par1DamageSource, CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE && !MinecraftServer.getIsServer()) {
            Entity lightningbolt = new EntityLightningBolt(this.getEntityWorld(), this.posX, this.posY-0.5, this.posZ);
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
        this.attackEntityFrom(DamageSource.magic, NMUtils.getWorldProgress() * 2 + this.rand.nextInt(2));
        // makes fire resistance not bypass the lightning damage
    }

    @Inject(method = "onStruckByLightning",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void addLightningEffects(EntityLightningBolt boltEntity, CallbackInfo ci){
        EntityPlayerMP thisObj = (EntityPlayerMP)(Object)this;
        this.steelModifier = 0;

        if(isPlayerWearingItem(thisObj, BTWItems.plateBoots,1)){
            this.steelModifier += 1;
        }
        if(isPlayerWearingItem(thisObj, BTWItems.plateLeggings,2)){
            this.steelModifier += 3;
        }
        if(isPlayerWearingItem(thisObj, BTWItems.plateBreastplate,3)) {
            this.steelModifier += 5;
        }
        if(isPlayerWearingItem(thisObj, BTWItems.plateHelmet,4) || isPlayerWearingItem(thisObj, BTWItems.enderSpectacles,4)) {
            this.steelModifier += 1;
        }

        this.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(),110 - this.steelModifier * 10,Math.max(10 - (int)(this.steelModifier / 2) - NMUtils.getWorldProgress(), 0),true));
        this.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(),800 - this.steelModifier * 79,3,true));
        this.addPotionEffect(new PotionEffect(Potion.confusion.getId(),260 - this.steelModifier * 25,0,true));
        this.addPotionEffect(new PotionEffect(Potion.blindness.getId(),260 - this.steelModifier * 25,0,true));
        this.addPotionEffect(new PotionEffect(Potion.weakness.getId(),800 - this.steelModifier * 75,1,true));
    }

    @Unique private boolean isPlayerWearingItem(EntityPlayerMP player, Item itemToCheck, int armorIndex){
        // armor indices: boots 1, legs 2, chest 3, helmet 4, held item 0
        return player.getCurrentItemOrArmor(armorIndex) != null && player.getCurrentItemOrArmor(armorIndex).itemID == itemToCheck.itemID;
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ServerConfigurationManager;sendChatMsg(Lnet/minecraft/src/ChatMessageComponent;)V",shift = At.Shift.AFTER))
    private void manageTauntingChatMessage(DamageSource par1DamageSource, CallbackInfo ci){
        if (NMUtils.getWorldProgress() != 3) {
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
    }
}