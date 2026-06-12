package com.itlesports.nightmaremode.util;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import btw.world.BTWDifficulties;
import com.itlesports.nightmaremode.entity.underworld.EntityRitualPortal;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.mixin.interfaces.EntityLivingBaseAccess;
import com.itlesports.nightmaremode.mixin.interfaces.ItemAccessor;
import com.itlesports.nightmaremode.mixin.interfaces.SoundManagerAccess;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NMUtils {
    private static double buffedSquidBonus = 1;
    private static boolean intenseCorruption = false;

    // Constants and collections
    public static final List<Integer> bloodArmor = new ArrayList<>(Arrays.asList(
            NMItems.bloodSword.itemID,
            NMItems.bloodBoots.itemID,
            NMItems.bloodLeggings.itemID,
            NMItems.bloodChestplate.itemID,
            NMItems.bloodHelmet.itemID
    ));

    public static final List<Integer> bloodTools = new ArrayList<>(Arrays.asList(
            NMItems.bloodSword.itemID,
            NMItems.bloodPickaxe.itemID,
            NMItems.bloodAxe.itemID,
            NMItems.bloodShovel.itemID,
            NMItems.bloodHoe.itemID
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

    // float lerp helper
    public static float lerp(float scaleFactor, float original, float target) {
        return original + scaleFactor * (target - original);
    }

    // World state and time methods
    public static int getWorldProgress() {
        return NightmareMode.worldState;
    }

    public static boolean getIsBloodMoon() {
        return NightmareMode.isBloodMoon;
    }

    public static boolean getIsEclipse() {
        return NightmareMode.isEclipse;
    }

    public static boolean getIsSolarFlare() {
        return false;
    }

    public static boolean getIsMobEclipsed(EntityLivingBase mob) {
        if (mob.dimension == 1) {
            return false;
        }
        if (((EntityLivingBaseAccess)mob).getActivePotionEffects() != null) {
            if (mob.isPotionActive(Potion.field_76443_y)) {
                return true;
            }
        }
        return NightmareMode.isEclipse;
    }

    // Ritual proximity detection methods
    public static boolean isNearActiveRitual(EntityPlayer player, double range) {
        if (player == null || player.worldObj == null) {
            return false;
        }

        World world = player.worldObj;

        List<?> nearbyEntities = world.getEntitiesWithinAABB(EntityRitualPortal.class, player.boundingBox.expand(range, range, range));

        for (Object entityObj : nearbyEntities) {
            if (entityObj instanceof EntityRitualPortal) {
                EntityRitualPortal portal = (EntityRitualPortal) entityObj;

                if (portal.isEntityAlive() && portal.getRitualProgress() < 1.0f) {
                    return true;
                }
            }
        }

        return false;
    }

//    private static float[] skyBrightness = new float[4];
//
//    public static void setSkyBrightness(float[] sky) {
//        skyBrightness = sky;
//    }
//
//
//    public static float[] getSkyBrightness() {
//        return skyBrightness;
//    }

    public static float getRitualIntensity(EntityPlayer player, double range) {
        if (player == null || player.worldObj == null) {
            return 0f;
        }

        World world = player.worldObj;
        float maxIntensity = 0f;

        List<?> nearbyEntities = world.getEntitiesWithinAABB(EntityRitualPortal.class, player.boundingBox.expand(range, range, range));

        for (Object entityObj : nearbyEntities) {
            if (entityObj instanceof EntityRitualPortal) {
                EntityRitualPortal portal =
                    (EntityRitualPortal) entityObj;

                if (portal.isEntityAlive() && portal.getRitualProgress() < 1.0f) {
                    double distance = player.getDistance(portal.posX, portal.posY, portal.posZ);
                    float proximityIntensity = 1.0f - (float) (distance / range);
                    float ritualProgress = portal.getRitualProgress();

                    float intensity = proximityIntensity * (0.5f + ritualProgress * 0.5f);
                    maxIntensity = Math.max(maxIntensity, intensity);
                }
            }
        }

        return Math.max(0f, Math.min(1f, maxIntensity));
    }

    public static EntityRitualPortal getNearestActiveRitual(EntityPlayer player, double range) {
        if (player == null || player.worldObj == null) {
            return null;
        }

        World world = player.worldObj;
        EntityRitualPortal nearest = null;
        double minDistance = Double.MAX_VALUE;

        List<?> nearbyEntities = world.getEntitiesWithinAABB(EntityRitualPortal.class, player.boundingBox.expand(range, range, range));

        for (Object entityObj : nearbyEntities) {
            if (entityObj instanceof EntityRitualPortal) {
                EntityRitualPortal portal = (EntityRitualPortal) entityObj;

                if (portal.isEntityAlive() && portal.getRitualProgress() < 1.0f) {
                    double distance = player.getDistance(portal.posX, portal.posY, portal.posZ);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = portal;
                    }
                }
            }
        }

        return nearest;
    }

    public static void manageEclipseChance(EntityLivingBase mob, int chance) {
        if (NightmareMode.evolvedMobs && mob.worldObj != null && !mob.worldObj.isRemote) {
            if (mob.rand.nextInt(chance) == 0) {
                setMobEclipsed(mob);
            }
        }
    }

    public static void setMobEclipsed(EntityLivingBase mob) {
        mob.addPotionEffect(new PotionEffect(Potion.field_76443_y.id, Integer.MAX_VALUE, 0));
    }

    public static long getNextBloodMoonTime(long currentTime) {
        int currentDay = (int) Math.ceil((double) currentTime / 24000);
        int nextBloodMoonDay = currentDay + (15 - (currentDay % 16) + 9) % 16;
        return (nextBloodMoonDay * 24000L) + 18000;
    }

    public static long getNextEclipseTime(long currentTime) {
        return ((currentTime / 24000) + 1) * 24000;
    }

    public static double getNiteMultiplier() {
        if (!NightmareMode.nite) return 1;
        if (NightmareMode.getInstance() == null) return 1;
        return NightmareMode.getInstance().NITE_MULTIPLIER;
    }

    public static int divByNiteMultiplier(int numerator, int minValue) {
        return (int) Math.max(numerator / NMUtils.getNiteMultiplier(), minValue);
    }

    // Entity equipment checking methods
    public static boolean isWearingFullBloodArmor(EntityLivingBase entity) {
        for (int i = 0; i < 5; i++) {
            if (entity.getCurrentItemOrArmor(i) == null) return false;
            if (entity.getCurrentItemOrArmor(i).itemID == bloodArmor.get(i)) continue;
            return false;
        }
        return true;
    }

    public static boolean isWearingFullBloodArmorWithoutSword(EntityLivingBase entity) {
        for (int i = 1; i < 5; i++) {
            if (entity.getCurrentItemOrArmor(i) == null) return false;
            if (entity.getCurrentItemOrArmor(i).itemID == bloodArmor.get(i)) continue;
            return false;
        }
        return true;
    }

    public static int getBloodArmorWornCount(EntityLivingBase entity) {
        int value = 0;
        for (int i = 1; i < 5; i++) {
            if (entity.getCurrentItemOrArmor(i) == null) continue;
            if (entity.getCurrentItemOrArmor(i).itemID == bloodArmor.get(i)) value += 1;
        }
        return value;
    }

    public static boolean isHoldingBloodSword(EntityLivingBase entity) {
        if (entity.getCurrentItemOrArmor(0) == null) return false;
        return entity.getCurrentItemOrArmor(0).itemID == bloodArmor.get(0);
    }

    // Audio and music methods
    public static void shushMusic() {
        SoundManager sndManager = Minecraft.getMinecraft().sndManager;

        SoundManagerAccess soundManageAccess = (SoundManagerAccess) sndManager;

        if (soundManageAccess.getSoundSystem().playing("BgMusic")) {
            soundManageAccess.getSoundSystem().stop("BgMusic");
        }
        if (soundManageAccess.getSoundSystem().playing("streaming")) {
            soundManageAccess.getSoundSystem().stop("streaming");
        }
    }

    public static void forcePlayMusic(String soundID, boolean toLoop) {
        SoundManager sndManager = Minecraft.getMinecraft().sndManager;
        shushMusic();
        SoundManagerAccess soundManageAccess = (SoundManagerAccess) sndManager;

        if (Minecraft.getMinecraft().gameSettings.musicVolume != 0.0F) {
            SoundPoolEntry sound = soundManageAccess.getSoundPoolSounds().getRandomSoundFromSoundPool(soundID);
            if (sound != null) {
                soundManageAccess.getSoundSystem().backgroundMusic("BgMusic", sound.getSoundUrl(), sound.getSoundName(), toLoop);
                soundManageAccess.getSoundSystem().setVolume("BgMusic", Minecraft.getMinecraft().gameSettings.musicVolume);
                soundManageAccess.getSoundSystem().play("BgMusic");
            }
        }
    }

    public static boolean isGracePeriodServer(World world) {
        return world.getWorldTime() - world.getData(NightmareMode.PORTAL_TIME) < 72000 && world.getData(NightmareMode.PORTAL_TIME) > 0;
    }

    // Game settings and world creation methods
    public static WorldSettings decodeSettings(String code, long seed) {
        if (code == null || code.length() < 5) {
            code = "STDHF";
        }
        char g = code.charAt(0);
        char s = code.charAt(1);
        char w = code.charAt(2);
        char d = code.charAt(3);
        char c = code.charAt(4);

        WorldSettings settings = new WorldSettings(seed,
                (g == 'C') ? EnumGameType.CREATIVE : EnumGameType.SURVIVAL,
                (s == 'T'),
                false,
                (w == 'F') ? WorldType.FLAT : ((w == 'L') ? WorldType.LARGE_BIOMES : WorldType.DEFAULT),
                (d == 'H') ? BTWDifficulties.HOSTILE : BTWDifficulties.STANDARD,
                true
        );
        if (c == 'T') {
            settings.enableCommands();
        } else {
            settings.disableCommands();
        }
        return settings;
    }

    @Unique
    public static String updateWorldName(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        long largest = Long.MIN_VALUE;
        int start = -1, end = -1;
        while (matcher.find()) {
            long num = Long.parseLong(matcher.group());
            if (num > largest) {
                largest = num;
                start = matcher.start();
                end = matcher.end();
            }
        }
        if (largest == Long.MIN_VALUE) {
            return input;
        }
        long incrementedValue = largest + (Long.signum(largest));
        StringBuilder updatedString = new StringBuilder(input);
        updatedString.replace(start, end, String.valueOf(incrementedValue));
        return updatedString.toString();
    }

    @Unique
    public static String makeUseableName(String worldName, Minecraft minecraft) {
        String trim = worldName.trim();
        for (char var4 : ChatAllowedCharacters.allowedCharactersArray) {
            trim = trim.replace(var4, '_');
        }
        if (MathHelper.stringNullOrLengthZero(trim)) {
            trim = "World";
        }
        trim = GuiCreateWorld.func_73913_a(minecraft.getSaveLoader(), trim);
        return trim;
    }

    // State management methods
    public static boolean isIntenseCorruption() {
        return intenseCorruption;
    }

    public static void setIntenseCorruption(boolean intenseCorruption) {
        NMUtils.intenseCorruption = intenseCorruption;
    }

    public static double getBuffedSquidBonus() {
        return buffedSquidBonus * (NightmareMode.buffedSquids ? 2 : 1);
    }

    public static void setBuffedSquidBonus(double par1) {
        buffedSquidBonus = par1;
    }

    public static int getFoodShanksFromLevel(EntityPlayer player) {
        return (int) Math.min(Math.floor((double) player.experienceLevel / 3) * 6 + 18, 60);
    }

    public static void setItemStackSizes(int par1) {
        Item.potion.setMaxStackSize((int) (par1 / 2));

        for (Item item : Item.itemsList) {
            if (item instanceof ItemFood && ((ItemAccessor) (item)).getMaxStackSize() > 4) {
                item.setMaxStackSize(par1);
            }
        }
    }

    public static boolean getIsDayFromWorldTime(World w){
        long time = w.getWorldTime() % 24000;
        return time <= 12541 || time >= 23459;
    }

    public static int getDayCountFromWorld(World w) {
        long time = w.getWorldTime();
        return ((int) Math.ceil((double) time / 24000)) + (time % 24000 >= 23459 ? 1 : 0);
    }

    @Unique
    public static int getScrollMetadata(String input){
        HashMap<String, Integer> dictionary = new HashMap<>();
        dictionary.put("prot",0);
        dictionary.put("fire prot",1);
        dictionary.put("feather",2);
        dictionary.put("blast",3);
        dictionary.put("proj prot",4);
        dictionary.put("resp",5);
        dictionary.put("aqua",6);
        dictionary.put("thorns",7);
        dictionary.put("sharp",16);
        dictionary.put("smite",17);
        dictionary.put("bane",18);
        dictionary.put("knockback",19);
        dictionary.put("fire aspect",20);
        dictionary.put("looting",21);
        dictionary.put("efficiency",32);
        dictionary.put("silk",33);
        dictionary.put("unbreaking",34);
        dictionary.put("fortune",35);
        dictionary.put("power",48);
        dictionary.put("punch",49);
        dictionary.put("flame",50);
        dictionary.put("infinity",51);

        return dictionary.get(input);
    }

    // Villager metadata codec
    public static final class VillagerMetaCodec {
        private static final int FACING_BITS = 2;
        private static final int PROF_BITS = 3;
        private static final int LEVEL_BITS = 3;

        private static final int FACING_SHIFT = 0;
        private static final int PROF_SHIFT = FACING_SHIFT + FACING_BITS;
        private static final int LEVEL_SHIFT = PROF_SHIFT + PROF_BITS;

        private static final int FACING_MASK = (1 << FACING_BITS) - 1;
        private static final int PROF_MASK = (1 << PROF_BITS) - 1;
        private static final int LEVEL_MASK = (1 << LEVEL_BITS) - 1;

        public static int packMeta(int profession, int level, int facing) {
            return ((facing & FACING_MASK) << FACING_SHIFT)
                    | ((profession & PROF_MASK) << PROF_SHIFT)
                    | ((level & LEVEL_MASK) << LEVEL_SHIFT);
        }

        public static int packItemMeta(int profession, int level) {
            return ((profession & PROF_MASK) << PROF_SHIFT)
                    | ((level & LEVEL_MASK) << LEVEL_SHIFT);
        }

        public static int getFacing(int meta) {
            return (meta >> FACING_SHIFT) & FACING_MASK;
        }

        public static int getProfession(int meta) {
            return (meta >> PROF_SHIFT) & PROF_MASK;
        }

        public static int getLevel(int meta) {
            return (meta >> LEVEL_SHIFT) & LEVEL_MASK;
        }

        public static int setFacing(int meta, int facing) {
            int withoutFacing = meta & ~(FACING_MASK << FACING_SHIFT);
            return withoutFacing | ((facing & FACING_MASK) << FACING_SHIFT);
        }

        public static int toItemMeta(int blockMeta) {
            return blockMeta & ~(FACING_MASK << FACING_SHIFT);
        }
    }
}
