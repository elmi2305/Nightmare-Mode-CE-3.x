package com.itlesports.nightmaremode;
import net.minecraft.src.EntityList;

public class NightmareModeEntityMapper {
    public NightmareModeEntityMapper(){
    }

    public static void createModEntityMappings() {
        EntityList.addMapping(EntityFireCreeper.class, "NightmareFireCreeper", 2301, 15770182, 16643820);
        EntityList.addMapping(EntityShadowZombie.class, "NightmareShadowZombie", 2302, 0, 0);
//        EntityList.addMapping(NightmareFlameEntity.class, "NightmareFlame", 2303, 100000, 100000);
//        EntityList.addMapping(SocksMobsEntityGoatPossessed.class, "NightmareHellGoat", 2302, 0, 7208964);
//        EntityList.addMapping(NightmareEntity.class,"NightmareEntity",2304,0,0);
    }
}
