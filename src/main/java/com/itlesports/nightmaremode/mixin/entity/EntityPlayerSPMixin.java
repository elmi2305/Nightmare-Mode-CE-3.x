package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.NightmareModeAddon;
import com.itlesports.nightmaremode.util.interfaces.IPlayerDirectionTracker;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends EntityPlayer implements IPlayerDirectionTracker {
    @Unique private boolean oldBloodMoon = false;

    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void playMusicInTheEnd(Minecraft mc, World w, Session session, int par4, CallbackInfo ci) {
        if (w.provider.dimensionId == 1) {
            NMUtils.forcePlayMusic(NightmareModeAddon.NM_BOSS_MUSIC.sound(), true);
        }
        else {
            NMUtils.shushMusic();
        }
    }

    @Override
    public void onDeath(DamageSource src) {
        NMUtils.shushMusic();
        super.onDeath(src);
    }

    private EnumFacing currentDirection = null;

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void captureInput(CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.keyBindLeft.pressed) currentDirection = EnumFacing.WEST;
        else if (mc.gameSettings.keyBindRight.pressed) currentDirection = EnumFacing.EAST;
        else if (mc.gameSettings.keyBindForward.pressed) currentDirection = EnumFacing.NORTH;
        else if (mc.gameSettings.keyBindBack.pressed) currentDirection = EnumFacing.SOUTH;
        else currentDirection = null;
        // send packet to server if riding horse
        if (this.ridingEntity instanceof EntityHorse) {
            // gets sent from the client which makes sense
            NightmareMode.sendDirectionUpdate(currentDirection);
        }

        if(this.ticksExisted % 20 != 0) return;
        // do bm sounds
        if(!this.oldBloodMoon && NMUtils.getIsBloodMoon()){
            NMUtils.forcePlayMusic(NightmareModeAddon.NM_BLOODMOON.sound(), false);
        }
        this.oldBloodMoon = NMUtils.getIsBloodMoon();
    }

    @Override
    public EnumFacing nm$getHeldDirection() {
        return currentDirection;
    }

    @Override
    public void nm$setHeldDirectionServer(EnumFacing dir) {
        this.currentDirection = dir;
    }
}

