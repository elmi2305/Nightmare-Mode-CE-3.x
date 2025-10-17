package com.itlesports.nightmaremode;
import com.itlesports.nightmaremode.entity.*;
import net.minecraft.src.EntityList;

public class NightmareModeEntityMapper {
    public NightmareModeEntityMapper(){
    }

    public static void createModEntityMappings() {
        EntityList.addMapping(EntityFireCreeper.class, "NightmareFireCreeper", 2301, 15770182, 16643820);
        EntityList.addMapping(EntityShadowZombie.class, "NightmareShadowZombie", 2302, 0, 0);
        EntityList.addMapping(NightmareVillager.class, "NightmareVillager", 2303, 0, 16711680);
        EntityList.addMapping(EntityBloodWither.class, "NightmareBloodWither", 2304);
        EntityList.addMapping(EntityFallingChicken.class, "NightmareFallingChicken", 2305);
        EntityList.addMapping(EntityNightmareGolem.class, "NightmareGolem", 2306, 16711680, 0);
        EntityList.addMapping(EntityFireSpider.class, "NightmareFireSpider", 2307, 12874496, 10232320);
        EntityList.addMapping(EntityStoneZombie.class, "NightmareStoneZombie", 2308, 11119017, 0);
        EntityList.addMapping(EntityObsidianCreeper.class, "NightmareObsidianCreeper", 2309, 0x453E7D, 0);
        EntityList.addMapping(EntitySuperchargedCreeper.class, "NightmareSupercriticalCreeper", 2310, 16721408, 3148800);
        EntityList.addMapping(EntityBlackWidowSpider.class, "NightmareBlackWidowSpider", 2311, 0, 12189696);
        EntityList.addMapping(EntityRadioactiveEnderman.class, "NightmareRadioactiveEnderman", 2312, 0x0, 0x1dbd15);
        EntityList.addMapping(EntityDungCreeper.class, "NightmareDungCreeper", 2313, 0xC4AE84, 0x0);
        EntityList.addMapping(EntityLightningCreeper.class, "NightmareLightningCreeper", 2314, 0x008D91, 0x0);
        EntityList.addMapping(EntityBloodZombie.class, "NightmareBloodZombie", 2315, 0xFF1021, 0xffffff);
        EntityList.addMapping(EntityFauxVillager.class, "NightmareFauxVillager", 2316, 0x4287f5, 0xdded2d);
        EntityList.addMapping(EntityZombieImposter.class, "NightmareImposterZombie", 2317);
        EntityList.addMapping(EntityCustomSkeleton.class, "NightmareBloodMoonSkeleton", 2318);
        EntityList.addMapping(EntitySkeletonDrowned.class, "NightmareDrownedSkeleton", 2319);
        EntityList.addMapping(EntitySkeletonMelted.class, "NightmareMeltedSkeleton", 2320);
        EntityList.addMapping(EntityObsidianFish.class, "NightmareObsidianFish", 2321);
    }
}
