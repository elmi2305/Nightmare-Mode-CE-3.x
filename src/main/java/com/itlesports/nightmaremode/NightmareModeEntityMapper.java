package com.itlesports.nightmaremode;
import btw.BTWMod;
import btw.entity.BroadheadArrowEntity;
import btw.entity.CanvasEntity;
import btw.entity.CorpseEyeEntity;
import btw.entity.DynamiteEntity;
import btw.entity.EmeraldPileEntity;
import btw.entity.InfiniteArrowEntity;
import btw.entity.MiningChargeEntity;
import btw.entity.RottenArrowEntity;
import btw.entity.SoulSandEntity;
import btw.entity.SpiderWebEntity;
import btw.entity.UrnEntity;
import btw.entity.item.BloodWoodSaplingItemEntity;
import btw.entity.item.FloatingItemEntity;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.entity.mechanical.source.VerticalWindMillEntity;
import btw.entity.mechanical.source.WaterWheelEntity;
import btw.entity.mechanical.source.WindMillEntity;
import btw.entity.mob.DireWolfEntity;
import btw.entity.mob.JungleSpiderEntity;
import btw.entity.mob.villager.BlacksmithVillagerEntity;
import btw.entity.mob.villager.ButcherVillagerEntity;
import btw.entity.mob.villager.FarmerVillagerEntity;
import btw.entity.mob.villager.LibrarianVillagerEntity;
import btw.entity.mob.villager.PriestVillagerEntity;
import net.minecraft.src.EntityList;

public class NightmareModeEntityMapper {
    public NightmareModeEntityMapper(){
    }

    public static void createModEntityMappings() {
        EntityList.addMapping(EntityFireCreeper.class, "NightmareFireCreeper", 2301, 5651506, 12422001);
        EntityList.addMapping(EntityShadowZombie.class, "NightmareShadowZombie", 2302, 0, 0);
//        EntityList.addMapping(SocksMobsEntityGoatPossessed.class, "NightmareHellGoat", 2302, 0, 7208964);
    }
}
