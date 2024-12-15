package com.itlesports.nightmaremode;

import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NightmareUtils {
    public static final List<Integer> bloodArmor = new ArrayList<>(Arrays.asList(NMItems.bloodSword.itemID,NMItems.bloodBoots.itemID,NMItems.bloodLeggings.itemID,NMItems.bloodChestplate.itemID,NMItems.bloodHelmet.itemID));
    public static final List<Integer> bloodTools = new ArrayList<>(Arrays.asList(NMItems.bloodSword.itemID,NMItems.bloodPickaxe.itemID,NMItems.bloodAxe.itemID,NMItems.bloodShovel.itemID,NMItems.bloodHoe.itemID));

    public static int getWorldProgress(World world) {
        if (!world.worldInfo.getDifficulty().shouldHCSRangeIncrease()) {
            return 0;
        }
        else if (WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly()) {
            return 3;
        }
        else if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
            return 2;
        }
        else if (WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
            return 1;
        }
        return 0;
    }
    public static boolean getIsBloodMoon(){
        if(NightmareMode.getInstance() == null){return false;}
        return Objects.requireNonNullElse(btw.community.nightmaremode.NightmareMode.getInstance().isBloodMoon, false);
    }

    public static boolean isWearingFullBloodArmor(EntityLivingBase entity){
        for(int i = 0; i < 5; i++){
            if(entity.getCurrentItemOrArmor(i) == null){return false;}
            if(entity.getCurrentItemOrArmor(i).itemID == bloodArmor.get(i)) continue;
            return false;
        }
        return true;
    }
    public static boolean isWearingFullBloodArmorWithoutSword(EntityLivingBase entity){
        for(int i = 1; i < 5; i++){
            if(entity.getCurrentItemOrArmor(i) == null){return false;}
            if(entity.getCurrentItemOrArmor(i).itemID == bloodArmor.get(i)) continue;
            return false;
        }
        return true;
    }

    public static boolean isWearingAnyBloodArmor(EntityLivingBase entity){
        for(int i = 1; i < 5; i++){
            if(entity.getCurrentItemOrArmor(i) == null) continue;
            if(entity.getCurrentItemOrArmor(i).itemID == bloodArmor.get(i)){return true;}
        }
        return false;
    }

    public static int getBloodArmorWornCount(EntityLivingBase entity){
        int value = 0;
        for(int i = 1; i < 5; i++){
            if(entity.getCurrentItemOrArmor(i) == null) continue;
            if(entity.getCurrentItemOrArmor(i).itemID == bloodArmor.get(i)){value += 1;}
        }
        return value;
    }

    public static boolean isHoldingBloodSword(EntityLivingBase entity){
        if(entity.getCurrentItemOrArmor(0) == null){return false;}
        return entity.getCurrentItemOrArmor(0).itemID == bloodArmor.get(0);
    }
}
