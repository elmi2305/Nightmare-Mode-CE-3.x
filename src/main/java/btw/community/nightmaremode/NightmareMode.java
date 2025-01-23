package btw.community.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.item.items.ToolItem;
import btw.world.biome.BiomeDecoratorBase;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class NightmareMode extends BTWAddon {
    private static NightmareMode instance;

    public WorldGenerator lavaPillowGenThirdStrata;
    public WorldGenerator silverfishGenFirstStrata;
    public WorldGenerator silverfishGenSecondStrata;
    public WorldGenerator silverfishGenThirdStrata;

    public WorldGenerator steelOreGenExposedToAir;
    public WorldGenerator steelOreGen;

    public static KeyBinding nightmareZoom;


    public Boolean isBloodMoon;
    public Boolean isEclipse;

    public static String nightmareZoomKey;

    public NightmareMode(){
        super();
    }

    public static NightmareMode getInstance() {
        if (instance == null)
            instance = new NightmareMode();
        return instance;
    }



    @Override
    public void postSetup() {
        float multiplier = 2f;
        ((ToolItem)NMItems.bloodPickaxe).addCustomEfficiencyMultiplier(multiplier);
        ((ToolItem)NMItems.bloodAxe).addCustomEfficiencyMultiplier(multiplier);
        ((ToolItem)NMItems.bloodHoe).addCustomEfficiencyMultiplier(multiplier);
        ((ToolItem)NMItems.bloodShovel).addCustomEfficiencyMultiplier(multiplier);
        super.postInitialize();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");

        NMBlocks.initNightmareBlocks();

        this.lavaPillowGenThirdStrata = new WorldGenMinable(BTWBlocks.lavaPillow.blockID, 10);
        this.silverfishGenFirstStrata = new WorldGenMinable(BTWBlocks.infestedStone.blockID, 8);
        this.silverfishGenSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, 8);
        this.silverfishGenThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, 16);
        this.steelOreGenExposedToAir = new WorldGenMinable(NMBlocks.steelOre.blockID,6).setNeedsAirExposure();
        this.steelOreGen = new WorldGenMinable(NMBlocks.steelOre.blockID,6);
    }
    public static void setEclipse(boolean par1){
        if (instance != null) {
            instance.isEclipse = par1;
        }
    }
    public static void setBloodmoon(boolean par1){
        if (instance != null) {
            instance.isBloodMoon = par1;
        }
    }
    public static Boolean shouldShowDateTimer;
    public static Boolean shouldShowRealTimer;
    public static Boolean bloodmoonColors;
    public static Boolean bloodmare;
    public static Boolean configOnHud;
    public static Boolean totalEclipse;
    public static Boolean buffedSquids;
    public static Boolean evolvedMobs;
    public static Boolean perfectStart;

    @Override
    public void preInitialize() {
        this.registerProperty("NmMinecraftDayTimer", "True", "Set if the minecraft date should show up or not");
        this.registerProperty("NmTimer", "True", "Set if the real time timer should show up or not");
        this.registerProperty("NmZoomKey", "C", "The zoom keybind");
        this.registerProperty("PerfectStart", "False", "Tired of resetting over and over on the first night? This option starts you off on day 2 with a brick oven, an axe and 3/4 food");
        this.registerProperty("BloodmoonColors", "True", "Determines whether the screen should be tinted red during a blood moon");
        this.registerProperty("ConfigOnHUD", "True", "Displays the active config modes on the HUD");
        this.registerProperty("Bloodmare", "False", "Every night is a Blood Moon");
        this.registerProperty("BuffedSquids", "False", "Squids have doubled stats and can chase the player on land");
        this.registerProperty("EvolvedMobs", "False", "All mob variants can spawn, regardless of world progress");
        this.registerProperty("TotalEclipse", "False", "Every day is a solar eclipse");
    }
    // evolvedMobs is currently only used in a few mob classes. it doesn't account for mob variants only present during eclipses

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {
        shouldShowDateTimer = Boolean.parseBoolean(propertyValues.get("NmMinecraftDayTimer"));
        shouldShowRealTimer = Boolean.parseBoolean(propertyValues.get("NmTimer"));
        nightmareZoomKey = propertyValues.get("NmZoomKey");
        perfectStart = Boolean.parseBoolean(propertyValues.get("PerfectStart"));
        bloodmoonColors = Boolean.parseBoolean(propertyValues.get("BloodmoonColors"));
        configOnHud = Boolean.parseBoolean(propertyValues.get("ConfigOnHUD"));
        bloodmare = Boolean.parseBoolean(propertyValues.get("Bloodmare"));
        buffedSquids = Boolean.parseBoolean(propertyValues.get("BuffedSquids"));
        evolvedMobs = Boolean.parseBoolean(propertyValues.get("EvolvedMobs"));
        totalEclipse = Boolean.parseBoolean(propertyValues.get("TotalEclipse"));
    }

    public void initKeybind(){
        nightmareZoom = new KeyBinding(StatCollector.translateToLocal("key.nightmaremode.zoom"), Keyboard.getKeyIndex(nightmareZoomKey));

        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        KeyBinding[] keyBindings = settings.keyBindings;
        keyBindings = Arrays.copyOf(keyBindings, keyBindings.length + 1);
        keyBindings[keyBindings.length - 1] = nightmareZoom;
        settings.keyBindings = keyBindings;
    }



    @Override
    public void decorateWorld(BiomeDecoratorBase decorator, World world, Random rand, int x, int z, BiomeGenBase biome) {
        for(int var5 = 0; var5 < 24; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(20)+5;
            int var8 = z + rand.nextInt(16);
            this.lavaPillowGenThirdStrata.generate(world, rand, var6, var7, var8);
        }
        for(int var5 = 0; var5 < 8; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(30)+50;
            int var8 = z + rand.nextInt(16);
            this.silverfishGenFirstStrata.generate(world, rand, var6, var7, var8);
        }
        for(int var5 = 0; var5 < 8; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(26)+24;
            int var8 = z + rand.nextInt(16);
            this.silverfishGenSecondStrata.generate(world, rand, var6, var7, var8);
        }
        for(int var5 = 0; var5 < 8; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(23)+1;
            int var8 = z + rand.nextInt(16);
            this.silverfishGenThirdStrata.generate(world, rand, var6, var7, var8);
        }
        for(int var5 = 0; var5 < 10; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(20)+16;
            int var8 = z + rand.nextInt(16);
            this.steelOreGenExposedToAir.generate(world, rand, var6, var7, var8);
        }
        for(int var5 = 0; var5 < 3; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(20)+16;
            int var8 = z + rand.nextInt(16);
            this.steelOreGen.generate(world, rand, var6, var7, var8);
        }
    }
}
