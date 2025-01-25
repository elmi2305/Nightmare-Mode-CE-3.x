package com.itlesports.nightmaremode;
import net.minecraft.src.EntityList;

public class NightmareModeEntityMapper {
    public NightmareModeEntityMapper(){
    }

    public static void createModEntityMappings() {
        EntityList.addMapping(EntityFireCreeper.class, "NightmareFireCreeper", 2301, 15770182, 16643820);
        EntityList.addMapping(EntityShadowZombie.class, "NightmareShadowZombie", 2302, 0, 0);
        EntityList.addMapping(NightmareVillager.class, "NightmareVillager", 2303, 0, 16711680);
        EntityList.addMapping(EntityBloodWither.class, "NightmareBloodWither", 2304, 16711680, 11011333);
        EntityList.addMapping(EntityFallingChicken.class, "NightmareFallingChicken", 2305, 13882323 , 14277081);
        EntityList.addMapping(EntityNightmareGolem.class, "NightmareGolem", 2306, 16711680, 0);
    }
}
