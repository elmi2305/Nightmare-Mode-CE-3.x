package com.itlesports.nightmaremode.util;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static btw.community.nightmaremode.NightmareMode.logical;


public class NMConfUtils {
    public static final List<CONFIG> confList = new ArrayList<>();
    static{
        confList.add(CONFIG.MORE_VARIANTS);
        confList.add(CONFIG.EVOLVED_MOBS);
        confList.add(CONFIG.BUFFED_SQUIDS);
        confList.add(CONFIG.NITE);
        confList.add(CONFIG.DARK_STORMY_NIGHTMARE);
        confList.add(CONFIG.NO_SKYBASES);
        confList.add(CONFIG.CANCER_MODE);
        confList.add(CONFIG.TOTAL_ECLIPSE);
        confList.add(CONFIG.BLOODMARE);
        confList.add(CONFIG.MAGIC_MONSTERS);
        confList.add(CONFIG.UNKILLABLE_MOBS);
        confList.add(CONFIG.NO_HIT);
        confList.add(CONFIG.REAL_TIME);
    }

    public enum CONFIG {
        MORE_VARIANTS(1, "moreVariants", ClearCondition.CLEAR_BW){
            @Override public boolean isActive() {return NightmareMode.moreVariants;}
        },
        BLOODMARE(2, "bloodmare", ClearCondition.CLEAR_HARDMODE){
            @Override public boolean isActive() {return NightmareMode.bloodmare;}
        },
        TOTAL_ECLIPSE(3, "totalEclipse", ClearCondition.CLEAR_BLOODMOON){
            @Override public boolean isActive() {return NightmareMode.totalEclipse;}
        },
        BUFFED_SQUIDS(4, "buffedSquids", ClearCondition.CLEAR_DRAGON){
            @Override public boolean isActive() {return NightmareMode.buffedSquids;}
        },
        EVOLVED_MOBS(5, "evolvedMobs", ClearCondition.CLEAR_BW){
            @Override public boolean isActive() {return NightmareMode.evolvedMobs;}
        },
        MAGIC_MONSTERS(6, "magicMonsters", ClearCondition.CLEAR_HARDMODE){
            @Override public boolean isActive() {return NightmareMode.magicMonsters;}
        },
        NO_HIT(7, "noHit", ClearCondition.CLEAR_GET_ITEM, new ItemStack(BTWBlocks.aestheticVegetation, 1, 2)){
            @Override public boolean isActive() {return NightmareMode.noHit;}
        },
        NITE(8, "nite", ClearCondition.CLEAR_DRAGON){
            @Override public boolean isActive() {return NightmareMode.nite;}
        },
        NO_SKYBASES(9, "noSkybases", ClearCondition.CLEAR_BW){
            @Override public boolean isActive() {return NightmareMode.noSkybases;}
        },
        UNKILLABLE_MOBS(10, "unkillableMobs", ClearCondition.CLEAR_HARDMODE){
            @Override public boolean isActive() {return NightmareMode.unkillableMobs;}
        },
        DARK_STORMY_NIGHTMARE(11, "darkStormyNightmare", ClearCondition.CLEAR_DRAGON){
            @Override public boolean isActive() {return NightmareMode.darkStormyNightmare;}
        },
        REAL_TIME(12, "realTime", ClearCondition.CLEAR_GLOOM){
            @Override public boolean isActive() {return NightmareMode.realTime;}
        },
        CANCER_MODE(13, "isAprilFools", ClearCondition.CLEAR_HARDMODE){
            @Override public boolean isActive() {return NightmareMode.isAprilFools;}
        };

        private final int id;
        private final String fieldName;
        private final ClearCondition clearCondition;
        private ItemStack itemStack;

        CONFIG(int id, String fieldName, ClearCondition clearCondition) {
            this.id = id;
            this.fieldName = fieldName;
            this.clearCondition = clearCondition;
            this.itemStack = null;
        }
        CONFIG(int id, String fieldName, ClearCondition clearCondition, int itemID) {
            this(id, fieldName, clearCondition);
            this.itemStack = new ItemStack(itemID, 1, 0);
        }
        CONFIG(int id, String fieldName, ClearCondition clearCondition, ItemStack stack) {
            this(id, fieldName, clearCondition);
            this.itemStack = stack;
        }

        public int getId() {
            return this.id;
        }
        public ItemStack getItemStack() {return this.itemStack;}
        public String getFieldName() {
            return this.fieldName;
        }
        public ClearCondition getClearCondition(){
            return this.clearCondition;
        }

        public enum ClearCondition {
            CLEAR_BLOODMOON("gui.config.clear.bloodmoon"),
            CLEAR_HARDMODE("gui.config.clear.hardmode"),
            CLEAR_DRAGON("gui.config.clear.dragon"),
            CLEAR_BW("gui.config.clear.bw"),
            CLEAR_WEEK("gui.config.clear.week"),
            CLEAR_GLOOM("gui.config.clear.gloom"),
            CLEAR_GET_ITEM("gui.config.clear.item");

            ClearCondition(String unlocalizedName){
                this.unlocalizedName = unlocalizedName;
            }
            ClearCondition(String unlocalizedName, int itemID){
                this(unlocalizedName);
            }

            private final String unlocalizedName;
            public String getNameUnlocalized(){
                return unlocalizedName;
            }
        }
        public abstract boolean isActive();
    }


    public static boolean isConfigCompleted(CONFIG config){
        int[] confList = getCompletedConfigs();
        if(confList.length < config.getId() - 1){
            return false;
        }
        return confList[config.getId() - 1] == 1;
    }



    public static int[] getWorldConfigData(World w){
        return w.getData(NightmareMode.CONFIGS_CREATED);
    }
    public static int[] getClientConfigData(){

        return new int[]{
                logical(NightmareMode.moreVariants),
                logical(NightmareMode.bloodmare),
                logical(NightmareMode.totalEclipse),
                logical(NightmareMode.buffedSquids),
                logical(NightmareMode.evolvedMobs),
                logical(NightmareMode.magicMonsters),
                logical(NightmareMode.noHit),
                logical(NightmareMode.nite),
                logical(NightmareMode.noSkybases),
                logical(NightmareMode.unkillableMobs),
                logical(NightmareMode.darkStormyNightmare),
                logical(NightmareMode.realTime),
                logical(NightmareMode.isAprilFools)
        };
    }

    private static final String FILE_NAME = "nmsavedata.nmdata";
    public static final int CONFIG_COUNT = 13;

    private static File getConfigFile() {
        return new File(FILE_NAME);
    }


    private static final String KEY_STRING = "ThisIsAVeryLongAndConvolutedSecretKeyStringThatIsAtLeast32CharactersForAES256!";

    private static SecretKey getKey() {
        try {
            byte[] keyBytes = KEY_STRING.getBytes("UTF-8");
            if (keyBytes.length < 32) {
                byte[] padded = new byte[32];
                System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
                return new SecretKeySpec(padded, "AES");
            }
            return new SecretKeySpec(Arrays.copyOf(keyBytes, 32), "AES");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initConfigFile() {
        File file = getConfigFile();

        if (!file.exists()) {
            int[] defaults = new int[CONFIG_COUNT];
            writeConfigs(defaults);
            return;
        }

        // If file exists but decryption fails (e.g., tampered or corrupt), reset
        try {
            getCompletedConfigs(); // This will throw if invalid
        } catch (Exception e) {
            // Reset to defaults
            int[] defaults = new int[CONFIG_COUNT];
            writeConfigs(defaults);
        }
    }

    /** Returns all completed configs */
    public static int[] getCompletedConfigs() {
        File file = getConfigFile();

        if (!file.exists()) {
            initConfigFile();
            return new int[CONFIG_COUNT]; // Defaults are all 0
        }

        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            if (fileBytes.length < 12) { // Min size: IV (12 bytes)
                throw new IllegalStateException("Invalid config file");
            }

            byte[] iv = Arrays.copyOfRange(fileBytes, 0, 12);
            byte[] ciphertext = Arrays.copyOfRange(fileBytes, 12, fileBytes.length);

            byte[] plaintext = decrypt(ciphertext, iv);

            String[] parts = new String(plaintext, "UTF-8").split(",");
            if (parts.length != CONFIG_COUNT) {
                throw new IllegalStateException("Invalid config data");
            }

            int[] configs = new int[CONFIG_COUNT];
            for (int i = 0; i < CONFIG_COUNT; i++) {
                configs[i] = Integer.parseInt(parts[i].trim());
                if (configs[i] != 0 && configs[i] != 1) {
                    throw new IllegalStateException("Invalid config value");
                }
            }

            return configs;
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            // tampering detected
            throw new RuntimeException("Config file tampered or corrupt", e);
        } catch (Exception e) {
            throw new RuntimeException("Error reading config", e);
        }
    }
    public static void setConfig(int index, int value) {
        if (index < 0 || index >= CONFIG_COUNT) {
            throw new IndexOutOfBoundsException("Config index out of range: " + index);
        }
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException("Value must be 0 or 1");
        }

        int[] configs = getCompletedConfigs();
        configs[index] = value;
        writeConfigs(configs);
    }

    private static void writeConfigs(int[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CONFIG_COUNT; i++) {
            if (values[i] != 0 && values[i] != 1) {
                throw new IllegalArgumentException("Values must be 0 or 1");
            }
            sb.append(values[i]).append(",");
        }
        sb.deleteCharAt(sb.length() - 1); // remove last comma

        try {
            byte[] plaintext = sb.toString().getBytes("UTF-8");

            // encrypt
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            byte[] ciphertext = encrypt(plaintext, iv);

            try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
                fos.write(iv);
                fos.write(ciphertext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] encrypt(byte[] plaintext, byte[] iv) throws Exception {
        SecretKey key = getKey();
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        return cipher.doFinal(plaintext);
    }

    private static byte[] decrypt(byte[] ciphertext, byte[] iv) throws Exception {
        SecretKey key = getKey();
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(ciphertext);
    }

    private static void appendFlag(StringBuilder sb, String text) {
        if (!sb.isEmpty()) {
            sb.append("+");
        }
        sb.append(text);
    }

    public static String getTextForActiveConfig(int[] arr) {
        StringBuilder sb = new StringBuilder();

        if (isEnabled(arr, CONFIG.BLOODMARE.getId() - 1)) appendFlag(sb, "BM");
        if (isEnabled(arr, CONFIG.TOTAL_ECLIPSE.getId() - 1)) appendFlag(sb, "TE");
        if (isEnabled(arr, CONFIG.EVOLVED_MOBS.getId() - 1)) appendFlag(sb, "EM");
        if (isEnabled(arr, CONFIG.BUFFED_SQUIDS.getId() - 1)) appendFlag(sb, "BS");
        if (isEnabled(arr, CONFIG.MAGIC_MONSTERS.getId() - 1)) appendFlag(sb, "MM");
        if (isEnabled(arr, CONFIG.NITE.getId() - 1)) appendFlag(sb, "NITE");
        if (isEnabled(arr, CONFIG.NO_SKYBASES.getId() - 1)) appendFlag(sb, "NS");
        if (isEnabled(arr, CONFIG.UNKILLABLE_MOBS.getId() - 1)) appendFlag(sb, "UM");
        if (isEnabled(arr, CONFIG.MORE_VARIANTS.getId() - 1)) appendFlag(sb, "MV");
        if (isEnabled(arr, CONFIG.CANCER_MODE.getId() - 1)) appendFlag(sb, "CM");
        if (isEnabled(arr, CONFIG.DARK_STORMY_NIGHTMARE.getId() - 1)) appendFlag(sb, "DSN");
        if (isEnabled(arr, CONFIG.REAL_TIME.getId() - 1)) appendFlag(sb, "RT");

        if (isEnabled(arr, CONFIG.NO_HIT.getId() - 1)) {
            return !sb.isEmpty() ? "NoHit " + sb : "NoHit";
        }

        return sb.toString();
    }


    private static boolean isEnabled(int[] arr, int index) {
        return arr != null && index >= 0 && index < arr.length && arr[index] == 1;
    }


    public static boolean isClientUsingHelpConfig(){
        return NightmareMode.perfectStart || NightmareMode.fastVillagers || NightmareMode.extraArmor;
    }

}
