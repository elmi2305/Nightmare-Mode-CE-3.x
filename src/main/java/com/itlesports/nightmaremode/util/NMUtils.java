package com.itlesports.nightmaremode.util;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.util.*;

public class NMUtils {
    private static double buffedSquidBonus = 1;
    private static boolean intenseCorruption = false;

    public static final List<Integer> bloodArmor = new ArrayList<>(Arrays.asList(
            NMItems.bloodSword.itemID,
            NMItems.bloodBoots.itemID,
            NMItems.bloodLeggings.itemID,
            NMItems.bloodChestplate.itemID,
            NMItems.bloodHelmet.itemID
    ));
    public static final Set<Integer> LONG_RANGE_ITEMS = new HashSet<>(Arrays.asList(
            Item.swordStone.itemID,
            Item.swordIron.itemID,
            Item.swordGold.itemID,
            BTWItems.steelSword.itemID,
            Item.axeStone.itemID,
            Item.axeDiamond.itemID,
            Item.axeIron.itemID,
            Item.shovelIron.itemID,
            Item.shovelStone.itemID,
            Item.shovelGold.itemID,
            Item.shovelDiamond.itemID,
            BTWItems.boneClub.itemID,
            Item.swordWood.itemID,
            Item.swordDiamond.itemID,
            Item.axeGold.itemID,
            Item.pickaxeStone.itemID
    ));
    public static final Set<Integer> LESSER_RANGE_ITEMS = new HashSet<>(Arrays.asList(
            BTWItems.boneClub.itemID,
            Item.swordWood.itemID,
            Item.swordDiamond.itemID,
            Item.axeGold.itemID
    ));

    public static final class VillagerMetaCodec {
        private static final int PROF_BITS = 3;
        private static final int LEVEL_BITS = 3;

        private static final int PROF_SHIFT = 0;
        private static final int LEVEL_SHIFT = PROF_SHIFT + PROF_BITS;

        private static final int PROF_MASK = (1 << PROF_BITS) - 1; // 0x7
        private static final int LEVEL_MASK = (1 << LEVEL_BITS) - 1; // 0x7

        public static int packMeta(int profession, int level) {
            return ((profession & PROF_MASK) << PROF_SHIFT)
                    | ((level & LEVEL_MASK) << LEVEL_SHIFT);
        }

        public static int getProfession(int meta) {
            return (meta >> PROF_SHIFT) & PROF_MASK;
        }

        public static int getLevel(int meta) {
            return (meta >> LEVEL_SHIFT) & LEVEL_MASK;
        }
    }



    public static boolean isIntenseCorruption() {
        return intenseCorruption;
    }

    public static void setIntenseCorruption(boolean intenseCorruption) {
        NMUtils.intenseCorruption = intenseCorruption;
    }

    public static double getBuffedSquidBonus(){
        return buffedSquidBonus * (NightmareMode.buffedSquids ? 2 : 1);
    }
    public static void setBuffedSquidBonus(double par1){
        buffedSquidBonus = par1;
    }


    public static int getFoodShanksFromLevel(EntityPlayer player){
        return (int) Math.min(Math.floor((double) player.experienceLevel / 3) * 6 + 18, 60);
    }

    public static void setItemStackSizes(int par1){
        Item.potion.setMaxStackSize((int)(par1 / 2));

        for(Item item : Item.itemsList){
            if(item instanceof ItemFood){
                item.setMaxStackSize(par1);
            }
        }
    }

    public static void shushMusic() {
        SoundManager sndManager = Minecraft.getMinecraft().sndManager;
        if (sndManager.sndSystem.playing("BgMusic")) {
            sndManager.sndSystem.stop("BgMusic");
        }
        if (sndManager.sndSystem.playing("streaming")) {
            sndManager.sndSystem.stop("streaming");
        }
    }

    public static long getNextBloodMoonTime(long currentTime) {
        int currentDay = (int) Math.ceil((double) currentTime / 24000);

        // Find the next day that satisfies the blood moon cycle (day % 16 == 9)
        int nextBloodMoonDay = currentDay + (15 - (currentDay % 16) + 9) % 16;

        // Convert back to ticks and set the time to 18000 (nighttime)
        return (nextBloodMoonDay * 24000L) + 18000;
    }

    public static void forcePlayMusic(String soundID, boolean toLoop) {
        SoundManager sndManager = Minecraft.getMinecraft().sndManager;
        shushMusic();
        if (Minecraft.getMinecraft().gameSettings.musicVolume != 0.0F) {
            SoundPoolEntry sound = sndManager.soundPoolSounds.getRandomSoundFromSoundPool(soundID);
            if (sound != null) {
                sndManager.sndSystem.backgroundMusic("BgMusic", sound.getSoundUrl(), sound.getSoundName(), toLoop);
                sndManager.sndSystem.setVolume("BgMusic", Minecraft.getMinecraft().gameSettings.musicVolume);
                sndManager.sndSystem.play("BgMusic");
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

    public static double getNiteMultiplier(){
        if(NightmareMode.getInstance() == null){return 1;}
        return NightmareMode.getInstance().NITE_MULTIPLIER;
    }


    public static int getWorldProgress() {
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
        if(NightmareMode.evolvedMobs && mob.worldObj != null && !mob.worldObj.isRemote){
            if(mob.rand.nextInt(chance) == 0){
                mob.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, Integer.MAX_VALUE,0));
            }
        }
    }

    /**
     * @param numerator number that gets divided by the NITE multiplier, then rounded to an integer
     * @param minValue the minimum value is to prevent return value of 0
     * @return always greater than 0
     */
    public static int divByNiteMultiplier(int numerator, int minValue){
        return (int) Math.max(numerator / NMUtils.getNiteMultiplier(), minValue);
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
