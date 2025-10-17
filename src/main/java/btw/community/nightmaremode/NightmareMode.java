package btw.community.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.world.biome.BiomeDecoratorBase;
import btw.world.util.data.DataEntry;
import btw.world.util.data.DataProvider;
import com.itlesports.nightmaremode.NMInitializer;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.network.IHorseTamingClient;
import com.itlesports.nightmaremode.network.IPlayerDirectionTracker;
import com.itlesports.nightmaremode.tpa.TPACommand;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.mixin.EntityRendererAccessor;
import com.itlesports.nightmaremode.network.SteelLockerNet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.*;
import java.util.*;
import java.util.List;

public class NightmareMode extends BTWAddon {
    public static int SKELETON_ICE = 2;
    public static int SKELETON_FIRE = 3;
    public static int SKELETON_ENDER = 4;
    public static int SKELETON_JUNGLE = 5;
    public static int SKELETON_SUPERCRITICAL = 6;

    private static NightmareMode instance;
    public static int worldState;

    public WorldGenerator lavaPillowGenThirdStrata;
    public WorldGenerator silverfishGenFirstStrata;
    public WorldGenerator silverfishGenSecondStrata;
    public WorldGenerator silverfishGenThirdStrata;

    public WorldGenerator steelOreGenExposedToAir;
    public WorldGenerator steelOreGen;

    public static boolean isBloodMoon;
    public static boolean isEclipse;
    public double NITE_MULTIPLIER = 1;

    public static final int UNDERWORLD_DIMENSION = 2;

    public NightmareMode(){
        super();
    }


    public boolean wasConfigModified;
    public void modifyConfigProperty(String propertyName, String newValue) {
        String filename = "config/" + this.modID + ".properties";
        File config = new File(filename);

        // Ensure the file exists before modifying
        if (!config.exists()) {
            System.out.println("Config file does not exist. Creating a new one.");
            try {
                config.getParentFile().mkdirs(); // Ensure config directory exists
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        List<String> lines = new ArrayList<>();
        boolean propertyFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(config))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(propertyName + "=")) {
                    lines.add(propertyName + "=" + newValue);
                    propertyFound = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // if the property wasn't found, add it at the end
        if (!propertyFound) {
            lines.add(propertyName + "=" + newValue);
        }

        // Write back the modified config file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(config, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        wasConfigModified = true;
    }

    @Override
    public String getModID() {
        return "nightmare_mode";
    }

    public static NightmareMode getInstance() {
        if (instance == null) {
            instance = new NightmareMode();
        }
        instance.modID = "nightmare_mode";
        return instance;
    }

    @Override
    public void postSetup() {
        float multiplier = 2f;
        NMItems.bloodPickaxe.addCustomEfficiencyMultiplier(multiplier);
        NMItems.bloodAxe.addCustomEfficiencyMultiplier(multiplier);
        NMItems.bloodHoe.addCustomEfficiencyMultiplier(multiplier);
        NMItems.bloodShovel.addCustomEfficiencyMultiplier(multiplier);

        super.postSetup();
    }

    @Override
    public void postInitialize() {
        super.postInitialize();
    }

    public static int getCurrentStackSize(World world) {
        return NightmareMode.getInstance().shouldStackSizesIncrease ? 32 : 16;
    }
    public static void syncItemStackSizesToProgress(World world) {
        int size = getCurrentStackSize(world);
        NMUtils.setItemStackSizes(size);

        for (Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (obj instanceof EntityPlayerMP player) {
                sendStackSizeSyncPacket(player, size);
            }
        }
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        if (!MinecraftServer.getIsServer()) {
            initClientPacketInfo();
        } else{
            AddonHandler.registerCommand(new TPACommand(), false);
        }

        NMBlocks.initNightmareBlocks();
        NMInitializer.initNightmareRecipes();


        NMInitializer.miscInit();
        NMAchievements.initialize();
        NMInitializer.manipulateAchievements();

        NMInitializer.runItemPostInit();
        NMInitializer.initNightmareTrades();
        NMInitializer.initMobSpawning();


        SteelLockerNet.register(this);


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
        AddonHandler.registerPacketHandler("nm|stat", (packet, player) -> {
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


        AddonHandler.registerPacketHandler("nm|BMEC", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                isBloodMoon = dataStream.readBoolean();
                isEclipse = dataStream.readBoolean();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.entityRenderer != null) {
                ((EntityRendererAccessor) mc.entityRenderer).nightmaremode$updateLightmap(1.0f);
            }
        });

        AddonHandler.registerPacketHandler("nm|stacksize", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                int newSize = dataStream.readInt();
                NMUtils.setItemStackSizes(newSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        AddonHandler.registerPacketHandler("nm|HorseDir", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                int horseId = dataStream.readInt();
                byte directionOrdinal = dataStream.readByte(); // read as byte

                Minecraft mc = Minecraft.getMinecraft();
                if (mc != null && mc.theWorld != null) {
                    Entity entity = mc.theWorld.getEntityByID(horseId);
                    if (entity instanceof EntityHorse) {
                        ((IHorseTamingClient) entity).nm$setRequiredDirection(directionOrdinal);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        AddonHandler.registerPacketHandler("nm|Dir", (packet, player) -> {
            try (DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                byte dirOrdinal = data.readByte();
                EnumFacing direction = dirOrdinal >= 0 ? EnumFacing.values()[dirOrdinal] : null;

                if (player instanceof IPlayerDirectionTracker) {
                    ((IPlayerDirectionTracker) player).nm$setHeldDirectionServer(direction);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        AddonHandler.registerPacketHandler("nm|HorseProg", (packet, player) -> {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                int horseId = in.readInt();
                int progress = in.readInt();

                Entity e = Minecraft.getMinecraft().theWorld.getEntityByID(horseId);
                if (e instanceof EntityHorse && e instanceof IHorseTamingClient) {
                    ((IHorseTamingClient) e).nm$setTamingProgress(progress);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private static Packet250CustomPayload createHorseProgressPacket(int horseId, int progress) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeInt(horseId);       // horse entity ID
            dataStream.writeInt(progress);      // current taming progress
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Packet250CustomPayload("nm|HorseProg", byteStream.toByteArray());
    }

    public static void sendHorseProgressToAll(EntityHorse horse, int progress) {
        Packet250CustomPayload packet = createHorseProgressPacket(horse.entityId, progress);
        for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (playerObj instanceof EntityPlayerMP player && player.ridingEntity instanceof EntityHorse) {
                if (player.getDistanceSqToEntity(horse) < 8 * 8) {
                    player.playerNetServerHandler.sendPacketToPlayer(packet);
                }
            }
        }
    }


    private static Packet250CustomPayload createWorldStatePacket() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeInt(NightmareMode.worldState);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return new Packet250CustomPayload("nm|stat", byteStream.toByteArray());
    }


    private static Packet250CustomPayload createBloodMoonAndEclipsePacket(){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeBoolean(isBloodMoon);
            dataStream.writeBoolean(isEclipse);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return new Packet250CustomPayload("nm|BMEC", byteStream.toByteArray());
    }


    public static void setEclipse(boolean par1){
        isEclipse = par1;
    }
    public static void setBloodmoon(boolean par1){
        isBloodMoon = par1;
    }

    public static void sendWorldStateToAllPlayers() {
        Packet250CustomPayload packet = createWorldStatePacket();
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }
    public static void sendBloodmoonEclipseToAllPlayers(){
        Packet250CustomPayload packet = createBloodMoonAndEclipsePacket();
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    private static Packet250CustomPayload createHorseDirectionPacket(int horseId, byte directionOrdinal) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeInt(horseId);
            dataStream.writeByte(directionOrdinal); // send as byte
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Packet250CustomPayload("nm|HorseDir", byteStream.toByteArray());
    }



    public static void sendHorseDirectionToAll(EntityHorse horse, EnumFacing direction) {
        Packet250CustomPayload packet = createHorseDirectionPacket(horse.entityId, (byte) direction.ordinal());
        for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (playerObj instanceof EntityPlayerMP player && player.ridingEntity instanceof EntityHorse) {
                // Optionally check distance so we only send to nearby players
                if (player.getDistanceSqToEntity(horse) < 8 * 8) {
                    player.playerNetServerHandler.sendPacketToPlayer(packet);
                }
            }
        }
    }

    public static void sendDirectionUpdate(EnumFacing direction) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bos);

            // Write ordinal or -1 if null (no input)
            out.writeByte(direction == null ? -1 : direction.ordinal());

            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = "nm|Dir";
            packet.data = bos.toByteArray();
            packet.length = bos.size();

            // Send to server
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverPlayerConnectionInitialized(NetServerHandler serverHandler, EntityPlayerMP playerMP) {
        sendWorldStateToClient(serverHandler);
        Packet250CustomPayload onJoinPacket = new Packet250CustomPayload("nm|onJoin", new byte[0]);
        serverHandler.sendPacketToPlayer(onJoinPacket);
        syncItemStackSizesToProgress(playerMP.worldObj);
    }

    public static void sendStackSizeSyncPacket(EntityPlayerMP player, int size) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Packet250CustomPayload packet = new Packet250CustomPayload("nm|stacksize", byteStream.toByteArray());
        player.playerNetServerHandler.sendPacketToPlayer(packet);
    }

    private static void sendWorldStateToClient(NetServerHandler serverHandler) {
        Packet250CustomPayload packet = createWorldStatePacket();
        serverHandler.sendPacketToPlayer(packet);
    }

    public static void setNiteMultiplier(double par1){
        if (instance != null) {
            instance.NITE_MULTIPLIER = par1;
        }
    }

    public static Boolean shouldShowDateTimer;
    public static Boolean shouldShowRealTimer;
    public static Boolean bloodmoonColors;
    public static Boolean crimson;
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
    public static Boolean potionParticles;
    public static Boolean moreVariants;
    public static Boolean shouldDisplayFishingAnnouncements;
    public static Boolean isAprilFools;
    public static Boolean aprilFoolsRendering;
    public static Boolean extraArmor;
    public static Boolean darkStormyNightmare;
    public static Boolean hordeMode;
    public static Boolean birthdayBash;
    public static Boolean fullBright;
    public static Boolean fastVillagers;

    public boolean canAccessMenu = true;
    public long portalTime = 0;
    public boolean shouldStackSizesIncrease;

    public static final DataEntry.WorldDataEntry<Long> PORTAL_TIME =
        DataProvider.getBuilder(Long.class)
                .name("PortalTime")
                .defaultSupplier(() -> 0L)
                .readNBT(nbt -> nbt.getLong("PortalTime"))
                .writeNBT((nbt, v) -> nbt.setLong("PortalTime", v))
                .global()  // attaches GlobalDataComponent
                .build();

    public static final DataEntry.WorldDataEntry<Boolean> DRAGON_DEFEATED =
            DataProvider.getBuilder(Boolean.class)
                    .name("HasDragonBeenDefeated")
                    .defaultSupplier(() -> false)
                    .readNBT(nbt -> nbt.getBoolean("HasDragonBeenDefeated"))
                    .writeNBT((nbt, v) -> nbt.setBoolean("HasDragonBeenDefeated", v))
                    .global()
                    .build();




    @Override
    public void preInitialize() {
        this.registerProperty("NmMinecraftDayTimer", "True");
        this.registerProperty("NmTimer", "True");
        this.registerProperty("BloodmoonColors", "True");
        this.registerProperty("Crimson", "False");
        this.registerProperty("ConfigOnHUD", "True");
        this.registerProperty("PotionParticles", "True");
        this.registerProperty("FishingAnnouncements", "True");
        this.registerProperty("PerfectStart", "False");
        this.registerProperty("Bloodmare", "False");
        this.registerProperty("BuffedSquids", "False");
        this.registerProperty("EvolvedMobs", "False");
        this.registerProperty("MagicMonsters", "False");
        this.registerProperty("NoHit", "False");
        this.registerProperty("TotalEclipse", "False");
        this.registerProperty("NITE", "False");
        this.registerProperty("NoSkybases", "False");
        this.registerProperty("UnkillableMobs", "False");
        this.registerProperty("MoreVariants", "False");
        this.registerProperty("AprilFoolsPatch", "False");
        this.registerProperty("AprilFoolsWarpedRendering", "True");
        this.registerProperty("ExtraArmor", "False");
        this.registerProperty("DarkStormyNightmare", "False");
        this.registerProperty("HordeMode", "False");
        this.registerProperty("BirthdayBash", "False");
        this.registerProperty("FullBright", "False");
        this.registerProperty("FastVillagers", "False");


        PORTAL_TIME.register();
        DRAGON_DEFEATED.register();
    }
    public void setCanLeaveGame(boolean par1){
        this.canAccessMenu = par1;
    }
    public boolean getCanLeaveGame(){
        return this.canAccessMenu;
    }

    @Override
    public void handleConfigProperties(Map<String, String> propertyValues) {
        shouldShowDateTimer = Boolean.parseBoolean(propertyValues.get("NmMinecraftDayTimer"));
        shouldShowRealTimer = Boolean.parseBoolean(propertyValues.get("NmTimer"));
        perfectStart = Boolean.parseBoolean(propertyValues.get("PerfectStart"));
        shouldDisplayFishingAnnouncements = Boolean.parseBoolean(propertyValues.get("FishingAnnouncements"));
        bloodmoonColors = Boolean.parseBoolean(propertyValues.get("BloodmoonColors"));
        potionParticles = Boolean.parseBoolean(propertyValues.get("PotionParticles"));
        crimson = Boolean.parseBoolean(propertyValues.get("RedSeaOfDeath"));
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
        moreVariants = Boolean.parseBoolean(propertyValues.get("MoreVariants"));
        extraArmor = Boolean.parseBoolean(propertyValues.get("ExtraArmor"));
        isAprilFools = Boolean.parseBoolean(propertyValues.get("AprilFoolsPatch"));
        aprilFoolsRendering = Boolean.parseBoolean(propertyValues.get("AprilFoolsWarpedRendering"));
        darkStormyNightmare = Boolean.parseBoolean(propertyValues.get("DarkStormyNightmare"));
        hordeMode = Boolean.parseBoolean(propertyValues.get("HordeMode"));
        birthdayBash = Boolean.parseBoolean(propertyValues.get("BirthdayBash"));
        fullBright = Boolean.parseBoolean(propertyValues.get("FullBright"));
        fastVillagers = Boolean.parseBoolean(propertyValues.get("FastVillagers"));
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