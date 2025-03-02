package com.itlesports.nightmaremode;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NightmareUtils {
    public static final List<Integer> bloodArmor = new ArrayList<>(Arrays.asList(
            NMItems.bloodSword.itemID,
            NMItems.bloodBoots.itemID,
            NMItems.bloodLeggings.itemID,
            NMItems.bloodChestplate.itemID,
            NMItems.bloodHelmet.itemID
    ));
    public static final List<Item> foodList = new ArrayList<>(Arrays.asList(
            NMItems.calamariRoast,
            NMItems.friedCalamari,
            Item.appleRed,
            Item.pumpkinPie,
            Item.cookie,
            Item.porkCooked,
            Item.beefCooked,
            Item.chickenCooked,
            Item.fishCooked,
            BTWItems.cookedCheval,
            BTWItems.cookedLiver,
            BTWItems.mashedMelon,
            Item.melon,
            BTWItems.cookedKebab,
            BTWItems.cookedMutton,
            BTWItems.cookedMysteryMeat,
            BTWItems.cookedScrambledEggs,
            BTWItems.cookedWolfChop,
            BTWItems.donut,
            BTWItems.cookedCarrot,
            BTWItems.chocolate,
            Item.bakedPotato,
            Item.potato,
            BTWItems.porkDinner,
            BTWItems.hardBoiledEgg,
            BTWItems.steakDinner,
            BTWItems.wolfDinner,
            BTWItems.hamAndEggs,
            BTWItems.chowder,
            BTWItems.heartyStew,
            BTWItems.chickenSoup,
            Item.goldenCarrot,
            Item.bread,
            BTWItems.tastySandwich,
            BTWItems.steakAndPotatoes
    ));


    public static int getFoodShanksFromLevel(EntityPlayer player){
        return (int) Math.min(Math.floor((double) player.experienceLevel / 3) * 6 + 18, 60);
    }

    public static void updateItemStackSizes(){
        Item.potion.setMaxStackSize(16);
        for(Item item : foodList){
            if(item.getItemStackLimit() == 16){
                item.setMaxStackSize(32);
            }
        }
    }

    public static final List<Integer> bloodTools = new ArrayList<>(Arrays.asList(
            NMItems.bloodSword.itemID,
            NMItems.bloodPickaxe.itemID,
            NMItems.bloodAxe.itemID,
            NMItems.bloodShovel.itemID,
            NMItems.bloodHoe.itemID
    ));

    public static final List<Integer> chainArmor = new ArrayList<>(Arrays.asList(
            Item.bootsChain.itemID,
            Item.legsChain.itemID,
            Item.plateChain.itemID,
            Item.helmetChain.itemID
    ));

    public static double getNiteMultiplier(){
        if(NightmareMode.getInstance() == null){return 1;}
        return NightmareMode.getInstance().NITE_MULTIPLIER;
    }

    public static int getWorldProgress(World world) {
        if (!world.worldInfo.getDifficulty().shouldHCSRangeIncrease()) {
            return 0;
        }
        return NightmareMode.worldState;
    }
    public static boolean getIsBloodMoon(){
        return NightmareMode.isBloodMoon;
    }
    public static boolean getIsEclipse(){
        return NightmareMode.isEclipse;
    }
    public static boolean getIsMobEclipsed(EntityLivingBase mob){
        if(mob.dimension == 1){
            return false;
        }
        if(mob.activePotionsMap != null){
            if (mob.isPotionActive(Potion.field_76443_y)) {
                return true;
            }
        }
        return NightmareMode.isEclipse;
    }
    public static void manageEclipseChance(EntityLivingBase mob, int chance){
        if(NightmareMode.evolvedMobs && mob.worldObj != null){
            if(mob.rand.nextInt(chance) == 0){
                mob.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, Integer.MAX_VALUE,0));
            }
        }
    }

    public static int divByNiteMultiplier(int numerator, int minValue){
        // divides the numerator by the NITE multiplier, rounds down to an integer
        // to prevent it from returning 0, a minimum value is given
        return (int) Math.max(numerator / NightmareUtils.getNiteMultiplier(), minValue);
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
