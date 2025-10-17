package com.itlesports.nightmaremode.mixin;

import btw.achievement.event.AchievementEventDispatcher;
import btw.achievement.event.BTWAchievementEvents;
import btw.item.items.ChiselItem;
import btw.item.items.PickaxeItem;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInWorldManager.class)
public class ItemInWorldManagerMixin {
    @Shadow
    public World theWorld;

    @Shadow
    public EntityPlayerMP thisPlayerMP;

    @Inject(method = "survivalTryHarvestBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playAuxSFXAtEntity(Lnet/minecraft/src/EntityPlayer;IIIII)V"))
    private void sendNightmareAchievementData(int i, int j, int k, int iFromSide, CallbackInfoReturnable<Boolean> cir) {
        int id = this.theWorld.getBlockId(i, j, k);
        if(this.shouldActivate(id, i, j, k)){
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.LustEvent.class, this.thisPlayerMP, BTWAchievementEvents.none());
        }

    }

    @Unique
    private boolean shouldActivate(int id, int i, int j, int k) {
        ItemStack held = this.thisPlayerMP.getHeldItem();
        boolean isDiamond = id == Block.oreDiamond.blockID;
        boolean isSteel   = id == NMBlocks.steelOre.blockID;

        if (!isDiamond && !isSteel) {
            return false;
        }

        if (isSteel && NMUtils.getWorldProgress() < 2) {
            return true;
        }

        Item item = held != null ? held.getItem() : null;
        if (item instanceof PickaxeItem p) {
            float strength = p.getStrVsBlock(held, this.theWorld, Block.oreDiamond, i, j, k);
            return strength < 3.9f;
        }

        return !(item instanceof ChiselItem);
    }

}
