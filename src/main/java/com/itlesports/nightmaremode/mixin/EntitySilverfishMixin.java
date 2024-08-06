package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySilverfish.class)
public class EntitySilverfishMixin {
    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySilverfish;attackEntityAsMob(Lnet/minecraft/src/Entity;)Z"))
    private void infectPlayer(Entity par1Entity, float par2, CallbackInfo ci){
        EntitySilverfish thisObj = (EntitySilverfish)(Object)this;
        if(par1Entity instanceof EntityPlayer target && thisObj.worldObj != null){
            if (thisObj.rand.nextFloat()<0.05 && NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)>1) {
                thisObj.setDead();
                target.addPotionEffect(new PotionEffect(Potion.wither.id,1000000,0));
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("You are infected. Find milk or you will die.");
                text2.setColor(EnumChatFormatting.DARK_GRAY);
                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(text2);
            } else {
                if (thisObj.rand.nextInt(2) == 0) {
                    target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 0));
                } else target.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 60, 0));
            }
        }
    }
    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void dropClay(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        EntitySilverfish thisObj = (EntitySilverfish)(Object)this;
        thisObj.dropItem(Item.clay.itemID, thisObj.rand.nextInt(3)+1); // drops clay regardless of dimension, dropping more in the end
    }
}
