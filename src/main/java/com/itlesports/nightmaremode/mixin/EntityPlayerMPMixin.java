package com.itlesports.nightmaremode.mixin;

import btw.entity.LightningBoltEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityPlayerMP.class)

public abstract class EntityPlayerMPMixin extends EntityPlayer {

    @Shadow public MinecraftServer mcServer;
    @Shadow public boolean playerConqueredTheEnd;
    @Unique int steelModifier;
    public EntityPlayerMPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }
    @Inject(method="updateGloomState", at = @At("HEAD"))
    public void incrementInGloomCounter(CallbackInfo info) {
        if (this.getGloomLevel() > 0) {
            this.inGloomCounter+=5; // gloom goes up 6x faster
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;addStat(Lnet/minecraft/src/StatBase;I)V", shift = At.Shift.AFTER))
    private void smitePlayer(DamageSource par1DamageSource, CallbackInfo ci){
        Entity lightningbolt = new LightningBoltEntity(this.getEntityWorld(), this.posX, this.posY-0.5, this.posZ);
        getEntityWorld().addWeatherEffect(lightningbolt);

        // SUMMONS EXPLOSION. explosion does tile and entity damage. effectively kills all dropped items.
        double par2 = this.posX;
        double par4 = this.posY;
        double par6 = this.posZ;
        float par8 = 3.0f;
        this.worldObj.createExplosion(null, par2, par4, par6, par8, true);
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
        if (NightmareUtils.getGameProgressMobsLevel(this.worldObj) != 3) {
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText(getDeathMessages().get(this.rand.nextInt(getDeathMessages().size())));
            text2.setColor(getDeathColors().get(this.rand.nextInt(getDeathColors().size())));
            this.mcServer.getConfigurationManager().sendChatMsg(text2);
        }
    }

    @Unique
    private static @NotNull List<String> getDeathMessages() {
        List<String> messageList = new ArrayList<>();
        messageList.add("<???> Pathetic.");
        messageList.add("<???> Really?");
        messageList.add("<???> Too easy.");
        messageList.add("<???> Have you tried not dying?");
        messageList.add("<???> Skill issue.");
        messageList.add("<???> Dead again?");
        messageList.add("<???> Nice one.");
        messageList.add("<???> Easy.");
        messageList.add("<???> Not even close.");
        messageList.add("<???> Is someone having a hard time?");
        messageList.add("<???> Don't bother trying.");
        messageList.add("<???> Perfect.");
        messageList.add("<???> :)");
        return messageList;
    }

    @Unique
    private static @NotNull List<EnumChatFormatting> getDeathColors() {
        List<EnumChatFormatting> colorList = new ArrayList<>();
        colorList.add(EnumChatFormatting.RED);
        colorList.add(EnumChatFormatting.BLUE);
        return colorList;
    }

    @Inject(method = "travelToDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;triggerAchievement(Lnet/minecraft/src/StatBase;)V",ordinal = 1))
    private void manageEndDialogue(int par1, CallbackInfo ci){
        ChatMessageComponent text2 = new ChatMessageComponent();
        text2.addText("<The Twins> Your journey ends here.");
        text2.setColor(EnumChatFormatting.LIGHT_PURPLE);
        this.mcServer.getConfigurationManager().sendChatMsg(text2);
        // need to figure out how to make this not happen every time the player goes to the end
    }
}
