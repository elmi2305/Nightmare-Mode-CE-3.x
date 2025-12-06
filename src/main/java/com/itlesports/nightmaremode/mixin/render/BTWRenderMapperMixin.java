package com.itlesports.nightmaremode.mixin.render;

import btw.client.render.BTWRenderMapper;
import com.itlesports.nightmaremode.block.tileEntities.CustomBasketTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.HellforgeTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityVillagerContainer;
import com.itlesports.nightmaremode.entity.*;
import com.itlesports.nightmaremode.rendering.*;
import net.minecraft.src.RenderGhast;
import net.minecraft.src.RenderManager;
import net.minecraft.src.RenderSkeleton;
import net.minecraft.src.TileEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWRenderMapper.class)
public class BTWRenderMapperMixin {
    @Inject(method = "initEntityRenderers", at = @At("TAIL"),remap = false)
    private static void doNightmareEntityRenderMapping(CallbackInfo ci){
        RenderManager.addEntityRenderer(EntityFireCreeper.class, new RenderFireCreeper());
        RenderManager.addEntityRenderer(EntityShadowZombie.class, new RenderZombieVariant());
        RenderManager.addEntityRenderer(EntityBloodZombie.class, new RenderZombieVariant());
        RenderManager.addEntityRenderer(EntityNightmareGolem.class, new RenderNightmareGolem());
        RenderManager.addEntityRenderer(EntityObsidianCreeper.class, new RenderObsidianCreeper());
        RenderManager.addEntityRenderer(EntitySuperchargedCreeper.class, new RenderSupercriticalCreeper());
        RenderManager.addEntityRenderer(EntityDungCreeper.class, new RenderDungCreeper());
        RenderManager.addEntityRenderer(EntityLightningCreeper.class, new RenderLightningCreeper());
        RenderManager.addEntityRenderer(EntityFauxVillager.class, new RenderFauxVillager());
        RenderManager.addEntityRenderer(EntityZombieImposter.class, new RenderZombieVariant());
        RenderManager.addEntityRenderer(EntityCustomSkeleton.class, new RenderSkeleton());
        RenderManager.addEntityRenderer(EntitySkeletonDrowned.class, new RenderCustomSkeleton());
        RenderManager.addEntityRenderer(EntitySkeletonMelted.class, new RenderCustomSkeleton());
        RenderManager.addEntityRenderer(EntityObsidianFish.class, new RenderObsidianFish());
        RenderManager.addEntityRenderer(EntityPhantomZombie.class, new RenderPhantom());
        RenderManager.addEntityRenderer(EntityCreeperGhast.class, new RenderGhast());



        TileEntityRenderer.instance.addSpecialRendererForClass(HellforgeTileEntity.class, new HellforgeRenderer());
        TileEntityRenderer.instance.addSpecialRendererForClass(CustomBasketTileEntity.class, new CustomBasketRenderer());

    }
}
