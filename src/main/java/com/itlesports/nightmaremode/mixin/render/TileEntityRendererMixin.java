package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import com.itlesports.nightmaremode.block.tileEntities.TileEntitySteelLocker;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
import com.itlesports.nightmaremode.rendering.TileEntityBloodChestRenderer;
import com.itlesports.nightmaremode.rendering.TileEntitySteelLockerRenderer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityRenderer;
import net.minecraft.src.TileEntitySpecialRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(TileEntityRenderer.class)
public abstract class TileEntityRendererMixin {
    @Shadow public Map specialRendererMap;
    @Shadow public static TileEntityRenderer instance;


    @Inject(method = "<init>",at = @At("TAIL"))
    private void addCustomNightmareRendering(CallbackInfo ci){
        this.specialRendererMap.put(TileEntityBloodChest.class, new TileEntityBloodChestRenderer());
        this.specialRendererMap.put(TileEntitySteelLocker.class, new TileEntitySteelLockerRenderer());

        TileEntityRenderer thisObj = (TileEntityRenderer) (Object) this;
        for (Object renderer : this.specialRendererMap.values()) {
            ((TileEntitySpecialRenderer) renderer).setTileEntityRenderer(thisObj);
        }
        ((TileEntitySpecialRenderer) this.specialRendererMap.get(TileEntityBloodChest.class)).setTileEntityRenderer(thisObj);
        ((TileEntitySpecialRenderer) this.specialRendererMap.get(TileEntitySteelLocker.class)).setTileEntityRenderer(thisObj);
    }
}
