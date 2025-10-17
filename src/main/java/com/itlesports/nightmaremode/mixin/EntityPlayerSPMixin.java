package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.NightmareModeAddon;
import com.itlesports.nightmaremode.network.IPlayerDirectionTracker;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends EntityPlayer implements IPlayerDirectionTracker {
    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void playMusicInTheEnd(Minecraft par1Minecraft, World par2World, Session par3Session, int par4, CallbackInfo ci) {
        if (par2World.provider.dimensionId == 1) {
            NMUtils.forcePlayMusic(NightmareModeAddon.NM_BOSS_MUSIC.sound(), true);
        }
        else {
            NMUtils.shushMusic();
        }
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
            NightmareMode.sendDirectionUpdate(currentDirection);
        }
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

