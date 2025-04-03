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
        EntityList.addMapping(EntityMetalCreeper.class, "NightmareMetalCreeper", 2309, 11119017, 9013641);
        EntityList.addMapping(EntitySuperchargedCreeper.class, "NightmareSupercriticalCreeper", 2310, 16721408, 3148800);
        EntityList.addMapping(EntityBlackWidowSpider.class, "NightmareBlackWidowSpider", 2311, 0, 12189696);
        EntityList.addMapping(EntityRadioactiveEnderman.class, "NightmareRadioactiveEnderman", 2312, 0, 47625);
        EntityList.addMapping(EntityDungCreeper.class, "NightmareDungCreeper", 2313, 12889732 , 0);
    }
}
