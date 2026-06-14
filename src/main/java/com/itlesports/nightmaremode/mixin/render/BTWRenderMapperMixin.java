package com.itlesports.nightmaremode.mixin.render;

import btw.client.render.BTWRenderMapper;
import com.itlesports.nightmaremode.block.tileEntities.*;
import com.itlesports.nightmaremode.entity.*;
import com.itlesports.nightmaremode.entity.creepers.*;
import com.itlesports.nightmaremode.entity.underworld.*;
import com.itlesports.nightmaremode.entity.variants.*;
import com.itlesports.nightmaremode.rendering.*;
import com.itlesports.nightmaremode.rendering.entities.*;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWRenderMapper.class)
public class BTWRenderMapperMixin {
    @Inject(method = "initEntityRenderers", at = @At("TAIL"),remap = false)
    private static void doNightmareEntityRenderMapping(CallbackInfo ci){
        RenderManager.addEntityRenderer(EntityFireCreeper.class, new RenderCreeperVariant());
        RenderManager.addEntityRenderer(EntityShadowZombie.class, new RenderZombieVariant());
        RenderManager.addEntityRenderer(EntityBloodZombie.class, new RenderZombieVariant());
        RenderManager.addEntityRenderer(EntityNightmareGolem.class, new RenderNightmareGolem());
        RenderManager.addEntityRenderer(EntityObsidianCreeper.class, new RenderCreeperVariant());
        RenderManager.addEntityRenderer(EntityNitroCreeper.class, new RenderCreeperVariant());
        RenderManager.addEntityRenderer(EntityDungCreeper.class, new RenderCreeperVariant());
        RenderManager.addEntityRenderer(EntityLightningCreeper.class, new RenderCreeperVariant());
        RenderManager.addEntityRenderer(EntityVoidCreeper.class, new RenderCreeperVariant());
        RenderManager.addEntityRenderer(EntityGelCreeper.class, new RenderCreeperVariant());
        RenderManager.addEntityRenderer(EntityGlitchCreeper.class, new RenderCreeperVariant());

        RenderManager.addEntityRenderer(EntityFauxVillager.class, new RenderFauxVillager());
        RenderManager.addEntityRenderer(EntityZombieImposter.class, new RenderZombieVariant());
        RenderManager.addEntityRenderer(EntityBloodMoonSkeleton.class, new RenderSkeleton());
        RenderManager.addEntityRenderer(EntitySkeletonDrowned.class, new RenderCustomSkeleton());
        RenderManager.addEntityRenderer(EntitySkeletonMelted.class, new RenderCustomSkeleton());
        RenderManager.addEntityRenderer(EntityObsidianFish.class, new RenderObsidianFish());
        RenderManager.addEntityRenderer(EntityMushWorm.class, new RenderMushWorm());
        RenderManager.addEntityRenderer(EntityPhantomZombie.class, new RenderPhantom());
        RenderManager.addEntityRenderer(EntityCreeperGhast.class, new RenderGhast());
        RenderManager.addEntityRenderer(FlowerZombie.class, new RenderZombieVariant());
        RenderManager.addEntityRenderer(FlowerCreeper.class, new RenderCreeperVariant());
        RenderManager.addEntityRenderer(FlowerSkeleton.class, new RenderCustomSkeleton());
        RenderManager.addEntityRenderer(EntityMagicArrow.class, new RenderCustomArrow());
        RenderManager.addEntityRenderer(EntitySporeArrow.class, new RenderCustomArrow());
        RenderManager.addEntityRenderer(EntityBlackHole.class, new RenderBlackHole());
        RenderManager.addEntityRenderer(EntityBloodAltar.class, new RenderBloodAltar());
        RenderManager.addEntityRenderer(EntityRitualPortal.class, new RenderRitualPortalEntity());
        RenderManager.addEntityRenderer(EntityRainSpider.class, new RenderSpider());
        RenderManager.addEntityRenderer(EntityRift.class, new RenderUnderworldPortal());
        RenderManager.addEntityRenderer(EntityHoneySlime.class, new RenderSlimeVariant(new ModelSlime(16), new ModelSlime(0), 0.25f));
        RenderManager.addEntityRenderer(EntityVoidSlime.class, new RenderSlimeVariant(new ModelSlime(16), new ModelSlime(0), 0.25f));
        RenderManager.addEntityRenderer(EntityVoidSquid.class, new VoidSquidRender());



        TileEntityRenderer.instance.addSpecialRendererForClass(HellforgeTileEntity.class, new HellforgeRenderer());
        TileEntityRenderer.instance.addSpecialRendererForClass(CustomBasketTileEntity.class, new CustomBasketRenderer());
        TileEntityRenderer.instance.addSpecialRendererForClass(TileEntityDisenchantmentTable.class, new RenderDisenchantmentTable());
        TileEntityRenderer.instance.addSpecialRendererForClass(TileEntityBloodBone.class, new TileEntityBloodBoneRenderer());
        TileEntityRenderer.instance.addSpecialRendererForClass(TileEntityPortalCore.class, new TileEntityPortalCoreRenderer());

    }
}
