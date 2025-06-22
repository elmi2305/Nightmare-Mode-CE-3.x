package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
import com.itlesports.nightmaremode.rendering.TileEntityBloodChestRenderer;
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

    @Shadow public abstract Map getSpecialRendererMap();

    @Inject(method = "<init>",at = @At("TAIL"))
    private void addCustomNightmareRendering(CallbackInfo ci){
        this.specialRendererMap.put(TileEntityBloodChest.class, new TileEntityBloodChestRenderer());
        TileEntityRenderer thisObj = (TileEntityRenderer)(Object)this;

        for (Object var2 : this.specialRendererMap.values()) {
            ((TileEntitySpecialRenderer)var2).setTileEntityRenderer(thisObj);
        }
        ((TileEntitySpecialRenderer)this.specialRendererMap.get(TileEntityBloodChest.class)).setTileEntityRenderer(thisObj);
    }

//    @Inject(method = "getSpecialRendererForEntity", at = @At("HEAD"),cancellable = true)
//    private void addBloodChestRendering(TileEntity par1TileEntity, CallbackInfoReturnable<TileEntitySpecialRenderer> cir){
//        if(par1TileEntity instanceof TileEntityBloodChest){
//            cir.setReturnValue((TileEntitySpecialRenderer) this.specialRendererMap.get(TileEntityBloodChest.class));
//        }
//    }
//    @Inject(method = "getSpecialRendererForClass", at = @At("HEAD"),cancellable = true)
//    private void addBloodChestRendering0(Class par1Class, CallbackInfoReturnable<TileEntitySpecialRenderer> cir){
//        if(par1Class == TileEntityBloodChest.class){
//            cir.setReturnValue((TileEntitySpecialRenderer) this.specialRendererMap.get(TileEntityBloodChest.class));
//        }
//    }
}
