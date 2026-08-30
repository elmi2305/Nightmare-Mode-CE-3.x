package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.tileEntities.*;
import com.itlesports.nightmaremode.entity.*;
import com.itlesports.nightmaremode.entity.creepers.*;
import com.itlesports.nightmaremode.entity.underworld.*;
import com.itlesports.nightmaremode.entity.variants.*;
import com.itlesports.nightmaremode.entity.outer.*;
import net.minecraft.src.EntityList;
import net.minecraft.src.EntitySpider;
import net.minecraft.src.TileEntity;

public class NMEntityMapper {
    public NMEntityMapper(){}

    public static void createModEntityMappings() {
        EntityList.addMapping(EntityFireCreeper.class, "NmFireCreeper", 2301, 0xF0C826, 0xFE0C04);
        EntityList.addMapping(EntityShadowZombie.class, "NmShadowZombie", 2302, 0x0, 0x0);
        EntityList.addMapping(NightmareVillager.class, "NmVillager", 2303, 0x0, 0xFF0000);
        EntityList.addMapping(EntityBloodWither.class, "NmBloodWither", 2304);
        EntityList.addMapping(EntityFallingChicken.class, "NmFallingChicken", 2305);
        EntityList.addMapping(EntityNightmareGolem.class, "NmGolem", 2306, 0xFF0000, 0x0);
        EntityList.addMapping(EntityFireSpider.class, "NmFireSpider", 2307, 0xC47000, 0x9C2F00);
        EntityList.addMapping(EntityStoneZombie.class, "NmStoneZombie", 2308, 0xA9A159, 0x0);
        EntityList.addMapping(EntityObsidianCreeper.class, "NmObsidianCreeper", 2309, 0x453E7D, 0x0);
        EntityList.addMapping(EntityNitroCreeper.class, "NmSupercriticalCreeper", 2310, 0xFF0200, 0x300600);
        EntityList.addMapping(EntityBlackWidowSpider.class, "NmBlackWidowSpider", 2311, 0x0, 0xB9E030);
        EntityList.addMapping(EntityRadioactiveEnderman.class, "NmRadioactiveEnderman", 2312, 0x0, 0x1dbd15);
        EntityList.addMapping(EntityDungCreeper.class, "NmDungCreeper", 2313, 0xC4AE84, 0x0);
        EntityList.addMapping(EntityLightningCreeper.class, "NmLightningCreeper", 2314, 0x008D91, 0x0);
        EntityList.addMapping(EntityBloodZombie.class, "NmBloodZombie", 2315, 0xFF1021, 0xffffff);
        EntityList.addMapping(EntityFauxVillager.class, "NmFauxVillager", 2316, 0x4287f5, 0xdded2d);
        EntityList.addMapping(EntityZombieImposter.class, "NmImposterZombie", 2317);
        EntityList.addMapping(EntityBloodMoonSkeleton.class, "NmBloodMoonSkeleton", 2318);
        EntityList.addMapping(EntitySkeletonDrowned.class, "NmDrownedSkeleton", 2319);
        EntityList.addMapping(EntitySkeletonMelted.class, "NmMeltedSkeleton", 2320);
        EntityList.addMapping(EntityObsidianFish.class, "NmObsidianFish", 2321);
        EntityList.addMapping(EntityCreeperGhast.class, "NmCreeperGhast", 2322, 0xd5e2f7, 0x3bb507);
        EntityList.addMapping(FlowerZombie.class, "NmFlowerZombie", 2323);
        EntityList.addMapping(EntityPollenCloud.class, "NmPollenCloud", 2324);
        EntityList.addMapping(FlowerCreeper.class, "NmFlowerCreeper", 2325);
        EntityList.addMapping(FlowerSkeleton.class, "NmFlowerSkeleton", 2326);
        EntityList.addMapping(EntitySporeArrow.class, "NmSporeArrow", 2327);
        EntityList.addMapping(EntityMagicArrow.class, "NmMagicArrow", 2328);
        EntityList.addMapping(EntityBlackHole.class, "NmBlackHole", 2329);
        EntityList.addMapping(EntityBloodAltar.class, "NmBloodAltarTracker", 2330);
        EntityList.addMapping(EntityMushWorm.class, "NmMushWorm", 2331, 0xFF6B6B, 0x808080);
        EntityList.addMapping(EntityRitualPortal.class, "NmEntityRitualPortal", 2332);
        EntityList.addMapping(EntityVoidCreeper.class, "NmEntityVoidCreeper", 2333, 0,0x4F4F4F);
        EntityList.addMapping(EntityGelCreeper.class, "NmEntityGelCreeper", 2334, 0xf071eb,0xeddfed);
        EntityList.addMapping(EntityGlitchCreeper.class, "NmEntityGlitchCreeper", 2335, 0x27ba40,0xff00dd);
//        EntityList.addMapping(EntityRainSpider.class, "Spider", 2336);
        EntityList.addMapping(EntityRift.class, "NmRiftEntity", 2337);
        EntityList.addMapping(EntityHoneySlime.class, "NmHoneySlime", 2338 /*, 0xE6B402, 0xF2F7A8 */);
        EntityList.addMapping(EntityVoidSlime.class, "NmVoidSlime", 2339 /*, 0, 0xe3ffe3 */);
        EntityList.addMapping(EntityVoidSquid.class, "NmVoidSquid", 2340 /*, 0x00446e, 0 */);
        EntityList.addMapping(EntityAwakenedWither.class, "NmAwakenedWither", 2341 /*, 0xFFFFFF, 0 */);
        EntityList.addMapping(EntityWalker.class, "NmWalker", 2342 /* , 0xFFFFFF, 0 */);
        EntityList.addMapping(EntityNetherFish.class, "NmNetherFish", 2343, 0x5A1A12, 0xFF6A00);
        EntityList.addMapping(EntityTier1NetherVillager.class, "NmTier1NetherVillager", 2344, 0x6F3122, 0xE08B31);
        EntityList.addMapping(EntityTier2NetherVillager.class, "NmTier2NetherVillager", 2345, 0x4B2520, 0x8E5CB5);
        EntityList.addMapping(EntityTier3NetherVillager.class, "NmTier3NetherVillager", 2346, 0x24191D, 0x4789C7);
        EntityList.addMapping(EntityCinderPigman.class, "NmCinderPigman", 2347, 0x8C3C28, 0xF06B24);
        EntityList.addMapping(EntityDeadzonePigman.class, "NmDeadzonePigman", 2348, 0x29171D, 0x731C34);
        EntityList.addMapping(EntityCinderBlaze.class, "NmCinderBlaze", 2349, 0x9A3519, 0xFFD05A);
        EntityList.addMapping(EntityHellfireBlaze.class, "NmHellfireBlaze", 2350, 0x4A0D0D, 0xFF2D00);
        EntityList.addMapping(EntityAshGhast.class, "NmAshGhast", 2351, 0x77706C, 0x322B29);
        EntityList.addMapping(EntitySiegeGhast.class, "NmSiegeGhast", 2352, 0x3A2623, 0x9C271B);
        EntityList.addMapping(EntityEnderSilverfish.class, "NmEnderSilverfish", 2353, 0x17131F, 0x7846A8);
        EntityList.addMapping(EntityEnderSkeleton.class, "NmOuterEnderSkeleton", 2354, 0x17131F, 0x7846A8);
        EntityList.addMapping(EntityWitherSkeletonOuter.class, "NmOuterWitherSkeleton", 2355, 0x202020, 0x555555);
        EntityList.addMapping(EntityFireSkeletonOuter.class, "NmOuterFireSkeleton", 2356, 0xA42C13, 0xFFB52E);
        EntityList.addMapping(EntityInfernoSkeleton.class, "NmInfernoSkeleton", 2357, 0x5B1008, 0xFF5A00);
        EntityList.addMapping(EntityIceSkeletonOuter.class, "NmOuterIceSkeleton", 2358, 0xD7F7FF, 0x69B9E8);
        EntityList.addMapping(EntityMummyZombie.class, "NmMummyZombie", 2359, 0xCDBB82, 0x6D5535);
        EntityList.addMapping(EntityIceZombie.class, "NmIceZombie", 2360, 0xBCEBFF, 0x4E86A8);
        EntityList.addMapping(EntityIceGolem.class, "NmIceGolem", 2361, 0xD8F7FF, 0x6BAAC8);
        EntityList.addMapping(EntityAngelSquid.class, "NmAngelSquid", 2362, 0xF5F5F5, 0xD9C8FF);
        EntityList.addMapping(EntityAcidSquid.class, "NmAcidSquid", 2363, 0x5FFF36, 0x164C10);
        EntityList.addMapping(EntityAngelGhast.class, "NmAngelGhast", 2364, 0xF4F4FF, 0xD4B9FF);
        EntityList.addMapping(EntityAcidGhast.class, "NmAcidGhast", 2365, 0x7AFF38, 0x213C13);
        EntityList.addMapping(EntityAngelDragon.class, "NmAngelDragon", 2366, 0xF4F4FF, 0xB5A7E8);
        EntityList.addMapping(EntityFishermanVillager.class, "NmFishermanVillager", 2367, 0x3E6B79, 0xD7E7E8);

    }

    public static void createTileEntityMappings(){
        TileEntity.addMapping(TileEntityBloodChest.class, "BloodChest");
        TileEntity.addMapping(TileEntitySteelLocker.class, "SteelLocker");
        TileEntity.addMapping(HellforgeTileEntity.class, "Hellforge");
        TileEntity.addMapping(CustomBasketTileEntity.class, "CustomBasket");
        TileEntity.addMapping(TileEntityVillagerContainer.class, "VillagerContainer");
        TileEntity.addMapping(TileEntityDisenchantmentTable.class, "TileEntityDisenchantmentTable");
        TileEntity.addMapping(TileEntityBloodBone.class, "TileEntityBloodBone");
        TileEntity.addMapping(TileEntityPortalCore.class, "TileEntityPortalCore");
        TileEntity.addMapping(CisternTileEntity.class, "NmCistern");
        TileEntity.addMapping(TileEntityHammerAnvil.class, "NmHammerAnvil");
        TileEntity.addMapping(TileEntityStoneAnvil.class, "NmStoneAnvil");
        TileEntity.addMapping(TileEntityDiamondAnvil.class, "NmDiamondAnvil");
        TileEntity.addMapping(TileEntityNetherrackAnvil.class, "NmNetherrackAnvil");
        TileEntity.addMapping(UnfiredNetherBrickTileEntity.class, "NmUnfiredNetherBrick");
        TileEntity.addMapping(DryingGrassTileEntity.class, "NmDryingGrass");
        TileEntity.addMapping(ObsidianMillstoneTileEntity.class, "NmObsidianMillstone");
        TileEntity.addMapping(ChuteHopperTileEntity.class, "NmChuteHopper");
        TileEntity.addMapping(OreNodeTileEntity.class, "NmOreNode");
        TileEntity.addMapping(MinerDrillTileEntity.class, "NmMinerDrill");
        TileEntity.addMapping(CisternInterfaceTileEntity.class, "NmCisternInterface");
        TileEntity.addMapping(CisternStirrerTileEntity.class, "NmCisternStirrer");
        TileEntity.addMapping(CisternDrainTileEntity.class, "NmCisternDrain");
        TileEntity.addMapping(TerrainExtractorTileEntity.class, "NmTerrainExtractor");
        TileEntity.addMapping(EnderAssemblerTileEntity.class, "NmEnderAssembler");
    }
}
