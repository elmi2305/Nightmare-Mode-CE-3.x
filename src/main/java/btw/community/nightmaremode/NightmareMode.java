package btw.community.nightmaremode;

import api.AddonHandler;
import api.BTWAddon;
import api.config.AddonConfig;
import api.item.items.ToolItem;
import api.world.BiomeDecoratorBase;
import api.world.data.DataEntry;
import api.world.data.DataProvider;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMTags;
import com.itlesports.nightmaremode.mixin.interfaces.EntityRendererAccessor;
import com.itlesports.nightmaremode.mixin.interfaces.MapGenStructureIOAccess;
import com.itlesports.nightmaremode.network.SteelLockerNet;
import com.itlesports.nightmaremode.tpa.TPACommand;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.StructureScatteredFeatureStartUnderworld;
import com.itlesports.nightmaremode.util.*;
import com.itlesports.nightmaremode.util.command.*;
import com.itlesports.nightmaremode.util.elements.LogSettings;
import com.itlesports.nightmaremode.util.interfaces.AddonConfigExtender;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.IHorseTamingClient;
import com.itlesports.nightmaremode.util.interfaces.IPlayerDirectionTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class NightmareMode extends BTWAddon {

    private static NightmareMode instance;


    // dev
    public static boolean devMode = false;
    public static boolean benchmarkPerformance = false;
    public static volatile double MSPT = 0.0;


    // world gen
    public WorldGenerator lavaPillowGenThirdStrata;
    public WorldGenerator silverfishGenFirstStrata;
    public WorldGenerator silverfishGenSecondStrata;
    public WorldGenerator silverfishGenThirdStrata;

    public WorldGenerator steelOreGenExposedToAir;
    public WorldGenerator steelOreGen;

    // events
    public static boolean isBloodMoon;
    public static boolean isEclipse;
    public int activeEventsInt;

    // misc
    public boolean canAccessMenu = true;
    public double NITE_MULTIPLIER = 1;
    public static int worldState;
    public static float[] skyColors;

    // configs
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
    public static Boolean birthdayBash;
    public static Boolean fullBright;
    public static Boolean fastVillagers;
    public static Boolean bloodMoonHelper;
    public static Boolean realTime;
    private boolean griefLogging;
    public static boolean renderFancyClouds;
    public static boolean renderVignette;

    private LogSettings logSettings;


    public NightmareMode(){
        super();
    }


    public boolean wasConfigModified;
    public void modifyConfigProperty(String propertyName, Object newValue, AddonConfig config, boolean shouldAlert) {

        ((AddonConfigExtender)(Object)config).nightmareMode$modifyProperty(propertyName,newValue);
        wasConfigModified = shouldAlert;
    }




    @Override
    public String getModID() {
        return NMFields.modID;
    }

    public static NightmareMode getInstance() {
        if (instance == null) {
            instance = new NightmareMode();
        }
        instance.modID = NMFields.modID;
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
        return world.getData(NightmareMode.DRAGON_DEFEATED) ? 32 : 16;
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
    private void setLogging(boolean b, int level){
        this.griefLogging = b;
        this.logSettings = new LogSettings(level);
    }
    public LogSettings getLogSettings(){
        return this.logSettings;
    }
    public boolean isGriefLogging(){
        return this.griefLogging;
    }

    private static final File griefLogFile = new File("nmGriefLog.txt");

    public static void appendLogLine(String text) {
        try {
            if (!griefLogFile.exists()) {
                File parent = griefLogFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                griefLogFile.createNewFile();
            }

            ZonedDateTime now = ZonedDateTime.now();

            String timestamp = now.format(
                    DateTimeFormatter.ofPattern("d.M.yyyy | HH:mm:ss z")
            );

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(griefLogFile, true))) {
                writer.write("[" + timestamp + "] " + text);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        if (!MinecraftServer.getIsServer()) {
            // server to client
            initClientPacketInfo();
        } else{
            // client to server
            initServerPacketInfo();
            AddonHandler.registerCommand(new TPACommand(), false);
        }
        AddonHandler.registerCommand(new WorldStateCommand(), false);
        AddonHandler.registerCommand(new EventCommand(), false);
        AddonHandler.registerCommand(new DayCommand(), false);
        AddonHandler.registerCommand(new NightCommand(), false);
        AddonHandler.registerCommand(new RevertEventTimeCommand(), false);
        AddonHandler.registerCommand(new WarpCommand(), false);

        NMBlocks.initNightmareBlocks();
        NMItems.runItemInit();
        NMInitializer.initBeaconEffects();
        NMEntityMapper.createModEntityMappings();
        NMEntityMapper.createTileEntityMappings();
        NMInitializer.initNightmareRecipes();

        NMTags.initTags();
        NMInitializer.miscInit();
        NMAchievements.initialize();
        NMInitializer.manipulateAchievements();

        NMInitializer.runItemPostInit();
        NMInitializer.runDevModePostInit();
        NMInitializer.initNightmareTrades();
        NMInitializer.initMobSpawning();


        SteelLockerNet.register(this);


        this.lavaPillowGenThirdStrata = new WorldGenMinable(BTWBlocks.lavaPillow.blockID, 10);
        this.silverfishGenFirstStrata = new WorldGenMinable(BTWBlocks.infestedStone.blockID, 8);
        this.silverfishGenSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, 8);
        this.silverfishGenThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, 16);
        this.steelOreGenExposedToAir = new WorldGenMinable(NMBlocks.steelOre.blockID,6).setNeedsAirExposure();
        this.steelOreGen = new WorldGenMinable(NMBlocks.steelOre.blockID,6);

//        BiomeGenBase.biomeList[24] = new BiomeGenBlightlands(24).setBiomeName("UnderworldPlains").setMinMaxHeight(1.1F, 1.4F);
//        BiomeGenBase.biomeList[25] = new BiomeGenHighlands(25).setBiomeName("UnderworldDesert").setMinMaxHeight(1.9F, 2.1F).setDisableRain();
//        BiomeGenBase.biomeList[26] = new BiomeGenFlowerFields(26).setBiomeName("UnderworldFlowerFields").setMinMaxHeight(-0.1F, 0.1F).setDisableRain();


//        System.out.println("DEBUGGING IN NIGTHMAREMODE.JAVA");
//        BiomeGenBase.biomeList[24] = BiomeGenUnderworld.flowerFields;

        BiomeGenBase.biomeList[24] = BiomeGenUnderworld.blightlands;
        BiomeGenBase.biomeList[25] = BiomeGenUnderworld.highlands;
        BiomeGenBase.biomeList[26] = BiomeGenUnderworld.flowerFields;
        BiomeGenBase.biomeList[27] = BiomeGenUnderworld.shadowRealm;
        BiomeGenBase.biomeList[28] = BiomeGenUnderworld.underHell;
        MapGenStructureIOAccess.invokeFunctionB(StructureScatteredFeatureStartUnderworld.class, "nmTemple");
    }

    @Environment(EnvType.SERVER)
    private void initServerPacketInfo(){
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


    }

    @Environment(EnvType.CLIENT)
    private void initClientPacketInfo() {
        // world state packet handler
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

        // bloodmoon eclipse sync
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
        // stacksize sync
        AddonHandler.registerPacketHandler("nm|stacksize", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                int newSize = dataStream.readInt();
                NMUtils.setItemStackSizes(newSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // the direction the horse desires
        AddonHandler.registerPacketHandler("nm|HorseDir", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                int horseId = dataStream.readInt();
                byte directionOrdinal = dataStream.readByte();

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



        // horse progress (for the GUI to draw)
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
        // the current held direction by the player (horse minigame)
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

        // server sending active events to client
        AddonHandler.registerPacketHandler("nm|events", (packet, player) -> {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                getInstance().activeEventsInt = dataStream.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        AddonHandler.registerPacketHandler("nm|fear", (packet, player) -> {
            try (DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                float fear = data.readFloat();
                if (player instanceof EntityPlayerExt) {
                    ((EntityPlayerExt) player).nightmareMode$setFear(fear);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        AddonHandler.registerPacketHandler("nm|blink", (packet, player) -> {
            try (DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                int blink = data.readInt();
                if (player instanceof EntityPlayerExt) {
                    ((EntityPlayerExt) player).nightmareMode$setBlinkLength(blink);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        AddonHandler.registerPacketHandler("nm|heartC", (packet, player) -> {
            try (DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                int heartCrackingLength = data.readInt();
                if (player instanceof EntityPlayerExt) {
                    ((EntityPlayerExt) player).nightmareMode$setHeartCrack(heartCrackingLength);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        AddonHandler.registerPacketHandler("nm|foodstat", (packet, player) -> {
            try (DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                int foodMax = data.readInt();
                if (player instanceof EntityPlayerExt) {
                    ((EntityPlayerExt) player).nightmareMode$setFoodMax(foodMax);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // song
        AddonHandler.registerPacketHandler("nm|music", (packet, player) -> {

            try (DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                String soundID = data.readUTF();
                NMUtils.forcePlayMusic(soundID, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // server telling the client that the nether needs an update
        AddonHandler.registerPacketHandler("nm|nUpd8", (packet, player) -> {
            if (player.dimension == -1) {
                player.worldObj.provider.generateLightBrightnessTable();
            }
        });
    }


    private static Packet250CustomPayload createHorseProgressPacket(int horseId, int progress) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeInt(horseId);
            dataStream.writeInt(progress);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Packet250CustomPayload("nm|HorseProg", byteStream.toByteArray());
    }

    private static Packet250CustomPayload createEventPacket(int eventID) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeInt(eventID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Packet250CustomPayload("nm|events", byteStream.toByteArray());
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
    public static void sendHorseProgressToPlayer(EntityHorse horse, int progress, EntityPlayer p) {
        Packet250CustomPayload packet = createHorseProgressPacket(horse.entityId, progress);
        if (p instanceof EntityPlayerMP player && player.ridingEntity instanceof EntityHorse) {
            player.playerNetServerHandler.sendPacketToPlayer(packet);
        }
    }

    public static void sendEventsPacketToAll(int eventID) {
        Packet250CustomPayload packet = createEventPacket(eventID);
        for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (playerObj instanceof EntityPlayerMP player) {
                player.playerNetServerHandler.sendPacketToPlayer(packet);
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

    private static Packet250CustomPayload createNetherChangePacket() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        return new Packet250CustomPayload("nm|nUpd8", byteStream.toByteArray());
    }
    private static Packet250CustomPayload createSongPacket(String soundId) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            dataStream.writeUTF(soundId);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return new Packet250CustomPayload("nm|music", byteStream.toByteArray());
    }
    public static void sendSongToAll(String sourceId) {
        Packet250CustomPayload packet = createSongPacket(sourceId);
        for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (playerObj instanceof EntityPlayerMP player) {
                player.playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    public static void sendSongToPlayer(String sourceId, EntityPlayerMP player) {
        Packet250CustomPayload packet = createSongPacket(sourceId);

        player.playerNetServerHandler.sendPacketToPlayer(packet);
    }



    private static Packet250CustomPayload createMoonAndSunEventPacket(){
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
    public static void sendMoonAndSunEventsToAllPlayers() {
        Packet250CustomPayload packet = createMoonAndSunEventPacket();
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    public static void sendNetherLightmapUpdateToClients() {
        Packet250CustomPayload packet = createNetherChangePacket();
        for (Object player : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }





    public static void sendHorseDirectionToAll(EntityHorse horse, EnumFacing direction) {
        Packet250CustomPayload packet = createHorseDirectionPacket(horse.entityId, (byte) direction.ordinal());
        for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            if (playerObj instanceof EntityPlayerMP player && player.ridingEntity instanceof EntityHorse) {
                if (player.getDistanceSqToEntity(horse) < 8 * 8) {
                    player.playerNetServerHandler.sendPacketToPlayer(packet);
                }
            }
        }
    }
    public static void sendHorseDirectionToPlayer(EntityHorse horse, EnumFacing direction, EntityPlayer p) {
        Packet250CustomPayload packet = createHorseDirectionPacket(horse.entityId, (byte) direction.ordinal());
        if (p instanceof EntityPlayerMP player && player.ridingEntity instanceof EntityHorse) {
            player.playerNetServerHandler.sendPacketToPlayer(packet);
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
    public static void sendDirectionUpdate(EnumFacing direction) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            // Match the server's read expectations: just send a single byte (-1 = none)
            dataStream.writeByte(direction == null ? -1 : direction.ordinal());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use the constructor that automatically sets data and length properly
        Packet250CustomPayload packet = new Packet250CustomPayload("nm|Dir", byteStream.toByteArray());

        // Send to the server
//        System.out.println("Sending nm|Dir packet, dir=" + direction);
        // DEBUG: packet does successfully get sent, and interpreted properly here. whether it arrives at the destination is unknown
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
//        Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
    }

    public static void sendTargetFearToClient(EntityPlayerMP player, float target){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeFloat(target);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload("nm|fear", byteStream.toByteArray());
        player.playerNetServerHandler.sendPacketToPlayer(packet);
    }

    public static void sendBlinkDurationToClient(EntityPlayerMP player, int target){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(target);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload("nm|blink", byteStream.toByteArray());
        player.playerNetServerHandler.sendPacketToPlayer(packet);
    }
    public static void sendHeartCrackingToPlayer(EntityPlayerMP player, int target){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(target);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload("nm|heartC", byteStream.toByteArray());
        player.playerNetServerHandler.sendPacketToPlayer(packet);
    }
    public static void sendFoodToClient(EntityPlayerMP player, int target){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(target);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload("nm|foodstat", byteStream.toByteArray());
        player.playerNetServerHandler.sendPacketToPlayer(packet);
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






    public static final DataEntry.WorldDataEntry<Long> PORTAL_TIME =
        DataProvider.getBuilder(Long.class)
                .name("PortalTime")
                .defaultSupplier(() -> 0L)
                .readNBT(nbt -> nbt.getLong("PortalTime"))
                .writeNBT((nbt, v) -> nbt.setLong("PortalTime", v))
                .global()
                .build();

    public static final DataEntry.WorldDataEntry<Boolean> DRAGON_DEFEATED =
            DataProvider.getBuilder(Boolean.class)
                    .name("DragonDefeated")
                    .defaultSupplier(() -> false)
                    .readNBT(nbt -> nbt.getBoolean("DragonDefeated"))
                    .writeNBT((nbt, v) -> nbt.setBoolean("DragonDefeated", v))
                    .global()
                    .build();

    public static final DataEntry.PlayerDataEntry<Long> APPLE_COOLDOWN =
            DataProvider.getBuilder(Long.class)
                    .name("AppleCooldown")
                    .defaultSupplier(() -> 0L)
                    .readNBT(nbt -> nbt.getLong("AppleCooldown"))
                    .writeNBT((nbt, v) -> nbt.setLong("AppleCooldown", v))
                    .player()
                    .syncPlayer()
                    .buildPlayer();

    public static final DataEntry.PlayerDataEntry<Double> SANITY =
            DataProvider.getBuilder(Double.class)
                    .name("Sanity")
                    .defaultSupplier(() -> 0D)
                    .readNBT(nbt -> nbt.getDouble("Sanity"))
                    .writeNBT((nbt, v) -> nbt.setDouble("Sanity", v))
                    .player()
                    .syncPlayer()
                    .buildPlayer();

    public static final DataEntry.PlayerDataEntry<Boolean> DEFEATED_BM =
            DataProvider.getBuilder(Boolean.class)
                    .name("BloodMoonDefeated")
                    .defaultSupplier(() -> false)
                    .readNBT(nbt -> nbt.getBoolean("BloodMoonDefeated"))
                    .writeNBT((nbt, v) -> nbt.setBoolean("BloodMoonDefeated", v))
                    .player()
                    .syncPlayer()
                    .buildPlayer();


    public static final DataEntry.PlayerDataEntry<Boolean> DEFEATED_BLOODWITHER =
            DataProvider.getBuilder(Boolean.class)
                    .name("BloodWitherDefeated")
                    .defaultSupplier(() -> false)
                    .readNBT(nbt -> nbt.getBoolean("BloodWitherDefeated"))
                    .writeNBT((nbt, v) -> nbt.setBoolean("BloodWitherDefeated", v))
                    .player()
                    .syncPlayer()
                    .buildPlayer();

    public static final DataEntry.WorldDataEntry<int[]> CONFIGS_CREATED =
            DataProvider.getBuilder(int[].class)
                    .name("ConfigsInit")
                    .defaultSupplier(() -> new int[]{
                                    0, // NightmareMode.moreVariants
                                    0, // NightmareMode.bloodmare
                                    0, // NightmareMode.totalEclipse
                                    0, // NightmareMode.buffedSquids
                                    0, // NightmareMode.evolvedMobs
                                    0, // NightmareMode.magicMonsters
                                    0, // NightmareMode.noHit
                                    0, // NightmareMode.nite
                                    0, // NightmareMode.noSkybases
                                    0, // NightmareMode.unkillableMobs
                                    0, // NightmareMode.darkStormyNightmare
                                    0, // NightmareMode.realTime
                                    0  // NightmareMode.isAprilFools
                            }

                    )
                    .readNBT(nbt -> nbt.getIntArray("ConfigsInit"))
                    .writeNBT((nbt, v) -> nbt.setIntArray("ConfigsInit", v))
                    .global()
                    .build();

    public void setCanLeaveGame(boolean par1){
        this.canAccessMenu = par1;
    }
    public boolean getCanLeaveGame(){
        return this.canAccessMenu;
    }

    public static int logical(Boolean b){
        return b ? 1 : 0;
    }
    @Override
    public void preInitialize() {
        super.preInitialize();

        PORTAL_TIME.register();
        DRAGON_DEFEATED.register();
        APPLE_COOLDOWN.register();
        CONFIGS_CREATED.register();
        SANITY.register();
        DEFEATED_BM.register();
        DEFEATED_BLOODWITHER.register();
    }

    @Override
    public void registerConfigProperties(AddonConfig config) {
        super.registerConfigProperties(config);
        config.registerBoolean("NmMinecraftDayTimer", true);
        config.registerBoolean("NmTimer", true);
        config.registerBoolean("BloodmoonColors", true);
        config.registerBoolean("Crimson", false);
        config.registerBoolean("ConfigOnHUD", true);
        config.registerBoolean("PotionParticles", true);
        config.registerBoolean("FishingAnnouncements", true);
        config.registerBoolean("PerfectStart", false);
        config.registerBoolean("Bloodmare", false);
        config.registerBoolean("BuffedSquids", false);
        config.registerBoolean("EvolvedMobs", false);
        config.registerBoolean("MagicMonsters", false);
        config.registerBoolean("NoHit", false);
        config.registerBoolean("TotalEclipse", false);
        config.registerBoolean("NITE", false);
        config.registerBoolean("NoSkybases", false);
        config.registerBoolean("UnkillableMobs", false);
        config.registerBoolean("MoreVariants", false);
        config.registerBoolean("AprilFoolsPatch", false);
        config.registerBoolean("AprilFoolsWarpedRendering", true);
        config.registerBoolean("ExtraArmor", false);
        config.registerBoolean("DarkStormyNightmare", false);
        config.registerBoolean("BirthdayBash", false);
        config.registerBoolean("FullBright", false);
        config.registerBoolean("FastVillagers", false);
        config.registerBoolean("BloodMoonHelper", false);
        config.registerBoolean("RealTime", false);
        config.registerString("WorldInfoString", "STDHF");
        config.registerBoolean("GriefLogging", false, "Enables logging of Tile Entity destruction and signs, including who placed them & where they were placed.", "Useful for detecting griefers.", "Also logs TNT ignited", "MULTIPLAYER ONLY!");
        config.registerInt("GriefLoggingLevel", 1, "First two bits decide logging level", "1 = only log Chests.", "2 = only log Container tile entities", "3 = log ALL tile entity destruction", "Add 4 to any of those values = whether to log items being taken out of chests", "Add 8 to any of those values = whether to log items being destroyed indirectly (not by players mining them)");
        config.registerBoolean("RenderFancyClouds", false, "Enables computationally expensive but cool-looking experimental cloud rendering for the overworld!", "Only use if you have a powerful graphics card.");
        config.registerBoolean("RenderVignette", true, "Enables custom NM Vignette around the screen borders. It gets particularly intense around the edges of the screen or in stressful scenarios, such as caving, Blood Moons, taking damage and similar", "Recommended to keep this enabled");
        getInstance().addonConfig = config;
    }

    @Override
    public void handleConfigProperties(AddonConfig config) {
        super.handleConfigProperties(config);

        shouldShowDateTimer = config.getBoolean("NmMinecraftDayTimer");
        shouldShowRealTimer = config.getBoolean("NmTimer");
        perfectStart = config.getBoolean("PerfectStart");
        shouldDisplayFishingAnnouncements = config.getBoolean("FishingAnnouncements");
        bloodmoonColors = config.getBoolean("BloodmoonColors");
        potionParticles = config.getBoolean("PotionParticles");
        crimson = config.getBoolean("Crimson");
        configOnHud = config.getBoolean("ConfigOnHUD");
        bloodmare = config.getBoolean("Bloodmare");
        buffedSquids = config.getBoolean("BuffedSquids");
        evolvedMobs = config.getBoolean("EvolvedMobs");
        magicMonsters = config.getBoolean("MagicMonsters");
        noHit = config.getBoolean("NoHit");
        totalEclipse = config.getBoolean("TotalEclipse");
        nite = config.getBoolean("NITE");
        noSkybases = config.getBoolean("NoSkybases");
        unkillableMobs = config.getBoolean("UnkillableMobs");
        moreVariants = config.getBoolean("MoreVariants");
        extraArmor = config.getBoolean("ExtraArmor");
        isAprilFools = config.getBoolean("AprilFoolsPatch");
        aprilFoolsRendering = config.getBoolean("AprilFoolsWarpedRendering");
        darkStormyNightmare = config.getBoolean("DarkStormyNightmare");
        birthdayBash = config.getBoolean("BirthdayBash");
        fullBright = config.getBoolean("FullBright");
        fastVillagers = config.getBoolean("FastVillagers");
        bloodMoonHelper = config.getBoolean("BloodMoonHelper");
        realTime = config.getBoolean("RealTime");
        renderFancyClouds = config.getBoolean("RenderFancyClouds");
        renderVignette = config.getBoolean("RenderVignette");
        if (MinecraftServer.getIsServer()) {
            getInstance().setLogging(config.getBoolean("GriefLogging"), config.getInt("GriefLoggingLevel"));
        }
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