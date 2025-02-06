package btw.community.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.entity.mob.villager.trade.TradeProvider;
import btw.item.items.ToolItem;
import btw.world.biome.BiomeDecoratorBase;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class NightmareMode extends BTWAddon {
    public static int postWitherSunTicks = 0;
    public static int postNetherMoonTicks = 0;

    public final static int sunTransitionTime = 360;
    public final static int moonTransitionTime = 240;

    private static NightmareMode instance;
    public static int worldState;

    public WorldGenerator lavaPillowGenThirdStrata;
    public WorldGenerator silverfishGenFirstStrata;
    public WorldGenerator silverfishGenSecondStrata;
    public WorldGenerator silverfishGenThirdStrata;

    public WorldGenerator steelOreGenExposedToAir;
    public WorldGenerator steelOreGen;

    public static KeyBinding nightmareZoom;
    public static String nightmareZoomKey;

    public boolean isBloodMoon;
    public boolean isEclipse;
    public double NITE_MULTIPLIER = 1;

    public NightmareMode(){
        super();
    }

    public static NightmareMode getInstance() {
        if (instance == null)
            instance = new NightmareMode();
        return instance;
    }


    public boolean getIsBloodmoon(){
        return this.isBloodMoon;
    }

    public boolean getIsEclipse(){
        return this.isEclipse;
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
        if (!MinecraftServer.getIsServer()) {
            initClientPacketInfo();
        }

        NMBlocks.initNightmareBlocks();
        // because apparently adding this trade crashes if I do it in the trade list mixin ???
        for(int i = 1; i <= 4; i++){
            TradeProvider.getBuilder().profession(5).level(i).sell().item(NMBlocks.bloodBones.blockID).buySellSingle().weight(0.613f * (float)Math.log(i) + 0.05f).addToTradeList();
        }
        // this is stupid ^
        this.lavaPillowGenThirdStrata = new WorldGenMinable(BTWBlocks.lavaPillow.blockID, 10);
        this.silverfishGenFirstStrata = new WorldGenMinable(BTWBlocks.infestedStone.blockID, 8);
        this.silverfishGenSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, 8);
        this.silverfishGenThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, 16);
        this.steelOreGenExposedToAir = new WorldGenMinable(NMBlocks.steelOre.blockID,6).setNeedsAirExposure();
        this.steelOreGen = new WorldGenMinable(NMBlocks.steelOre.blockID,6);
    }

    @Environment(EnvType.CLIENT)
    private void initClientPacketInfo() {
        //world state packet handler
        AddonHandler.registerPacketHandler("nightmaremode|state", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            int worldState = -1;
            try {
                worldState = dataStream.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (worldState != -1) {
                NightmareMode.worldState = worldState;
            }
        });


        AddonHandler.registerPacketHandler("nightmaremode|bloodmoonEclipse", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                this.isBloodMoon = dataStream.readBoolean();
                this.isEclipse = dataStream.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //this is to stop unintended transitions
        AddonHandler.registerPacketHandler("nightmaremode|onJoin", (packet, player) -> {
            postWitherSunTicks = 999;
            postNetherMoonTicks = 999;
        });
    }
    private static Packet250CustomPayload createWorldStatePacket() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeInt(NightmareMode.worldState);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload("nightmaremode|state", byteStream.toByteArray());
        return packet;
    }


    private Packet250CustomPayload createBloodMoonAndEclipsePacket(){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeBoolean(this.isBloodMoon);
            dataStream.writeBoolean(this.isEclipse);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload("nightmaremode|BMEC", byteStream.toByteArray());
        return packet;
    }


//    public static void setEclipse(boolean par1){
//        if (instance != null) {
//            instance.isEclipse = par1;
//        }
//    }
//    public static void setBloodmoon(boolean par1){
//        if (instance != null) {
//            instance.isBloodMoon = par1;
//        }
//    }

    public static void sendWorldStateToAllPlayers() {
        Packet250CustomPayload packet = createWorldStatePacket();
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }
    public void sendBloodmoonEclipseToAllPlayers(){
        Packet250CustomPayload packet = this.createBloodMoonAndEclipsePacket();
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        sendWorldStateToClient(serverHandler);
        Packet250CustomPayload onJoinPacket = new Packet250CustomPayload("nightmaremode|onJoin", new byte[0]);
        serverHandler.sendPacketToPlayer(onJoinPacket);
    }

    private static void sendWorldStateToClient(NetServerHandler serverHandler) {
        Packet250CustomPayload packet = createWorldStatePacket();
        serverHandler.sendPacketToPlayer(packet);
    }
    public void sendBloodMoonAndEclipseToClient(NetServerHandler serverHandler) {
        Packet250CustomPayload packet = this.createBloodMoonAndEclipsePacket();
        serverHandler.sendPacketToPlayer(packet);
    }



    public void setEclipse(boolean par1){
        this.isEclipse = par1;
    }
    public void setBloodmoon(boolean par1){
        this.isBloodMoon = par1;
    }

    public static void setNiteMultiplier(double par1){
        if (instance != null) {
            instance.NITE_MULTIPLIER = par1;
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
    public static Boolean magicMonsters;
    public static Boolean noHit;
    public static Boolean perfectStart;
    public static Boolean nite;
    public static Boolean noSkybases;
    public static Boolean unkillableMobs;

    @Override
    public void preInitialize() {
        this.registerProperty("NmMinecraftDayTimer", "True", "Set if the minecraft date should show up or not");
        this.registerProperty("NmTimer", "True", "Set if the real time timer should show up or not");
        this.registerProperty("NmZoomKey", "C", "The zoom keybind");
        this.registerProperty("BloodmoonColors", "True", "Determines whether the screen should be tinted red during a blood moon");
        this.registerProperty("ConfigOnHUD", "True", "Displays the active config modes on the HUD");
        this.registerProperty("PerfectStart", "False", "Tired of resetting over and over on the first night? This option starts you off on day 2 with a brick oven and an axe. However, you start with only 6 shanks.");
        this.registerProperty("Bloodmare", "False", "Every night is a Blood Moon");
        this.registerProperty("BuffedSquids", "False", "Squids have doubled stats and can chase the player on land");
        this.registerProperty("EvolvedMobs", "False", "All mob variants can spawn, regardless of world progress");
        this.registerProperty("MagicMonsters", "False", "All mobs are witches");
        this.registerProperty("NoHit", "False", "One hit, and you're out");
        this.registerProperty("TotalEclipse", "False", "Every day is a solar eclipse");
        this.registerProperty("NITE", "False", "Nightmare Is Too Easy. Start with 3 hearts and shanks. Gain them back by levelling up. Mobs get stronger the longer you play. Raw food is safe to eat. Reduced hunger cost & movement penalties. Inspired by MITE");
        this.registerProperty("NoSkybases", "False", "Logs have gravity");
        this.registerProperty("UnkillableMobs", "False", "Mobs cannot take direct damage");
    }

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
        magicMonsters = Boolean.parseBoolean(propertyValues.get("MagicMonsters"));
        noHit = Boolean.parseBoolean(propertyValues.get("NoHit"));
        totalEclipse = Boolean.parseBoolean(propertyValues.get("TotalEclipse"));
        nite = Boolean.parseBoolean(propertyValues.get("NITE"));
        noSkybases = Boolean.parseBoolean(propertyValues.get("NoSkybases"));
        unkillableMobs = Boolean.parseBoolean(propertyValues.get("UnkillableMobs"));
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
