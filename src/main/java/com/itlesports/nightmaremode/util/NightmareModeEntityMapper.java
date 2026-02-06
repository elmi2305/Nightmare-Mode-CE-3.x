package com.itlesports.nightmaremode.util;
import com.itlesports.nightmaremode.block.tileEntities.*;
import com.itlesports.nightmaremode.entity.*;
import net.minecraft.src.EntityList;
import net.minecraft.src.TileEntity;

public class NightmareModeEntityMapper {
    public NightmareModeEntityMapper(){
    }

    public static void createModEntityMappings() {
        EntityList.addMapping(EntityFireCreeper.class, "NmFireCreeper", 2301, 15770182, 16643820);
        EntityList.addMapping(EntityShadowZombie.class, "NmShadowZombie", 2302, 0, 0);
        EntityList.addMapping(NightmareVillager.class, "NmVillager", 2303, 0, 16711680);
        EntityList.addMapping(EntityBloodWither.class, "NmBloodWither", 2304);
        EntityList.addMapping(EntityFallingChicken.class, "NmFallingChicken", 2305);
        EntityList.addMapping(EntityNightmareGolem.class, "NmGolem", 2306, 16711680, 0);
        EntityList.addMapping(EntityFireSpider.class, "NmFireSpider", 2307, 12874496, 10232320);
        EntityList.addMapping(EntityStoneZombie.class, "NmStoneZombie", 2308, 11119017, 0);
        EntityList.addMapping(EntityObsidianCreeper.class, "NmObsidianCreeper", 2309, 0x453E7D, 0);
        EntityList.addMapping(EntitySuperchargedCreeper.class, "NmSupercriticalCreeper", 2310, 16721408, 3148800);
        EntityList.addMapping(EntityBlackWidowSpider.class, "NmBlackWidowSpider", 2311, 0, 12189696);
        EntityList.addMapping(EntityRadioactiveEnderman.class, "NmRadioactiveEnderman", 2312, 0x0, 0x1dbd15);
        EntityList.addMapping(EntityDungCreeper.class, "NmDungCreeper", 2313, 0xC4AE84, 0x0);
        EntityList.addMapping(EntityLightningCreeper.class, "NmLightningCreeper", 2314, 0x008D91, 0x0);
        EntityList.addMapping(EntityBloodZombie.class, "NmBloodZombie", 2315, 0xFF1021, 0xffffff);
        EntityList.addMapping(EntityFauxVillager.class, "NmFauxVillager", 2316, 0x4287f5, 0xdded2d);
        EntityList.addMapping(EntityZombieImposter.class, "NmImposterZombie", 2317);
        EntityList.addMapping(EntityCustomSkeleton.class, "NmBloodMoonSkeleton", 2318);
        EntityList.addMapping(EntitySkeletonDrowned.class, "NmDrownedSkeleton", 2319);
        EntityList.addMapping(EntitySkeletonMelted.class, "NmMeltedSkeleton", 2320);
        EntityList.addMapping(EntityObsidianFish.class, "NmObsidianFish", 2321);
        EntityList.addMapping(EntityCreeperGhast.class, "NmCreeperGhast", 2322, 0xd5e2f7, 0x3bb507);
    }

    public static void createTileEntityMappings(){
        TileEntity.addMapping(TileEntityBloodChest.class, "BloodChest");
        TileEntity.addMapping(TileEntitySteelLocker.class, "SteelLocker");
        TileEntity.addMapping(HellforgeTileEntity.class, "Hellforge");
        TileEntity.addMapping(CustomBasketTileEntity.class, "CustomBasket");
        TileEntity.addMapping(TileEntityVillagerContainer.class, "VillagerContainer");
        TileEntity.addMapping(TileEntityDisenchantmentTable.class, "TileEntityDisenchantmentTable");
    }
}
