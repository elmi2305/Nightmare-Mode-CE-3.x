package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.tileEntities.*;
import com.itlesports.nightmaremode.entity.*;
import com.itlesports.nightmaremode.entity.creepers.*;
import com.itlesports.nightmaremode.entity.underworld.*;
import com.itlesports.nightmaremode.entity.variants.*;
import net.minecraft.src.EntityList;
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
        EntityList.addMapping(FlowerZombie.class, "NmFlowerZombie", 2323, 0x00FF00, 0x0000FF);
        EntityList.addMapping(EntityPollenCloud.class, "NmPollenCloud", 2324);
        EntityList.addMapping(FlowerCreeper.class, "NmFlowerCreeper", 2325, 0x00FF00, 0x0000FF);
        EntityList.addMapping(FlowerSkeleton.class, "NmFlowerSkeleton", 2326, 0x00FF00, 0x0000FF);
        EntityList.addMapping(EntitySporeArrow.class, "NmSporeArrow", 2327);
        EntityList.addMapping(EntityMagicArrow.class, "NmMagicArrow", 2328);
        EntityList.addMapping(EntityBlackHole.class, "NmBlackHole", 2329);
        EntityList.addMapping(EntityBloodAltar.class, "NmBloodAltarTracker", 2330, 0x0, 0x0);
        EntityList.addMapping(EntityMushWorm.class, "NmMushWorm", 2331, 0xFF6B6B, 0x808080);
        EntityList.addMapping(EntityRitualPortal.class, "NmEntityRitualPortal", 2332);
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
    }
}
