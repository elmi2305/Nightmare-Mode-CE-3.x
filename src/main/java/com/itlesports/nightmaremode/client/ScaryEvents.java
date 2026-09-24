package com.itlesports.nightmaremode.client;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.mixin.interfaces.SoundManagerAccess;
import com.itlesports.nightmaremode.scary.ScaryEvent;
import com.itlesports.nightmaremode.scary.ScaryAchievements;
import com.itlesports.nightmaremode.scary.ScaryRoster;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import paulscode.sound.SoundSystem;
import com.itlesports.nightmaremode.scary.ScaryClock;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Environment(EnvType.CLIENT)
public final class ScaryEvents {
    private static final Random RANDOM = new Random();
    private static final ScaryClock CLOCK = new ScaryClock(RANDOM);
    private static final ScaryRoster ROSTER = new ScaryRoster();
    private static final com.itlesports.nightmaremode.scary.ScaryStare STARE = new com.itlesports.nightmaremode.scary.ScaryStare();
    private static NetClientHandler rosterConnection;
    private static EntityPlayerExt blinkOwner;
    private static final int SCARY_BLINK_LENGTH = 55;
    private static final String SOUND_SOURCE = "nm_scary_event";
    private static final String[] TITLES = {"Memories", "Something's Watching", "Not Alone", "It Remembers", "You Looked Back"};
    private static final String[] NAMES = {"Player 2", "Alex_37", "stone981", "James_12"};
    private static final String[] SOUNDS = {"dig.stone", "random.eat", "random.chestopen", "random.door_close", "step.gravel", "liquid.splash"};
    private static WorldClient world;
    private static EntityClientPlayerMP player;
    private static int dimension;
    private static ScaryEvent active;
    private static boolean forced;
    private static boolean lastEnabled = NightmareMode.scaryEvents;
    private static boolean synchronizedState;
    private static long lastTick = now();
    private static long started;
    private static long deadline;
    private static int progress;
    private static float health;
    private static long busyUntil;
    private static long skyMorning;
    private static int joinsRemaining;
    private static long nextJoin;
    private static long tabStarted;
    private static boolean tabWasDown;
    private static boolean eyeSoundPlayed;
    private static boolean tabSoundPlaying;
    private static long stepDue;
    private static int stepsLeft;
    private static int lastStep;
    private static Vec3 source;
    private static Vec3 figure;
    private static float figureYaw;
    private static String title;
    private static int lastTitle = -1;
    private static ScaryMenu menu;

    private ScaryEvents() {}
    public static long now() { return System.nanoTime() / 1_000_000L; }

    public static void receive(boolean enabled, ScaryEvent event, boolean cancel) {
        context();
        // a passive off-state resync must not cancel an explicitly forced test while already off.
        if (cancel || (!enabled && (lastEnabled || !forced)) || event != null) clear();
        if (cancel || (!enabled && lastEnabled)) ROSTER.clear();
        NightmareMode.scaryEvents = enabled;
        lastEnabled = enabled;
        synchronizedState = true;
        if (event != null) {
            // only /scary sends a forced event; natural events do not print test guidance.
            String failure = start(event, true);
            if (failure != null) chat("Cannot run " + event.id + ": " + failure);
            else if (event.isMenu()) chat("Open the pause menu to test " + event.id + ". Esc dismisses it.");
            else if (event == ScaryEvent.TAB_EYE) chat("Join flood started. The Tab panel keeps the eye until the players leave in about 15 minutes.");
            else if (event == ScaryEvent.FIGURE) chat("Figure placed about " + Math.round(Math.sqrt(player.getDistanceSq(figure.xCoord, figure.yCoord, figure.zCoord)))
                    + " blocks ahead. Look at it to trigger the blink.");
        }
    }

    private static void context() {
        Minecraft mc = Minecraft.getMinecraft();
        if (world != mc.theWorld || player != mc.thePlayer || (player != null && dimension != player.dimension)) {
            STARE.reset();
            clear();
            world = mc.theWorld;
            player = mc.thePlayer;
            dimension = player == null ? 0 : player.dimension;
            synchronizedState = false;
            busyUntil = 0;
            if (player != null && rosterConnection != player.sendQueue) {
                ROSTER.clear();
                rosterConnection = player.sendQueue;
            }
            progress = NightmareMode.worldState;
            health = player == null ? 0 : player.getHealth();
            CLOCK.disturb();
            lastTick = now();
            if (world != null && player != null) com.itlesports.nightmaremode.network.ScaryEventNet.requestState();
        }
        if (lastEnabled && !NightmareMode.scaryEvents) { clear(); ROSTER.clear(); }
        lastEnabled = NightmareMode.scaryEvents;
        if (active != null && ((!forced && !NightmareMode.scaryEvents) || world == null || player == null
                || player.isDead || player.getHealth() <= 0 || now() >= deadline
                || (active.isSky() && (!night() || world.getWorldTime() >= skyMorning)))) clear();
    }

    public static void unload() {
        STARE.reset();
        clear();
        ROSTER.clear();
        rosterConnection = null;
        world = null;
        player = null;
        synchronizedState = false;
        CLOCK.disturb();
    }

    public static void majorEvent() {
        if (active == null || !active.isSky()) clear();
        CLOCK.disturb();
    }

    public static void clear() {
        if (blinkOwner != null && blinkOwner.nightmareMode$getBlinkLength() == SCARY_BLINK_LENGTH) blinkOwner.nightmareMode$setBlinkLength(0);
        blinkOwner = null;
        active = null;
        forced = false;
        source = figure = null;
        tabStarted = stepDue = 0;
        tabSoundPlaying = false;
        stepsLeft = 0;
        joinsRemaining = 0;
        if (menu != null) {
            menu.restore();
            menu = null;
        }
        SoundSystem sound = soundSystem();
        if (sound != null) {
            sound.stop(SOUND_SOURCE);
            sound.removeSource(SOUND_SOURCE);
        }
    }

    public static ScaryEvent active() {
        context();
        return active;
    }

    public static long elapsed() { return now() - started; }
    public static long tabElapsed() { return tabStarted == 0 ? -1 : now() - tabStarted; }
    public static String title() { return title; }
    public static Vec3 figure() { return figure; }
    public static float figureYaw() { return figureYaw; }

    private static boolean blocked() {
        return player == null || player.isDead || player.getHealth() <= 0 || player.isPlayerSleeping()
                || !NightmareMode.getInstance().getCanLeaveGame() || BossStatus.statusBarLength > 0;
    }

    public static void tick() {
        context();
        long time = now();
        long delta = time - lastTick;
        lastTick = time;
        if (world == null || player == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        boolean quiet = !blocked() && player.hurtTime == 0
                && !player.isBurning()
                && player.getHealth() >= health && progress == NightmareMode.worldState
                && !NightmareMode.isBloodMoon && !NightmareMode.isEclipse;
        if (player.isSwingInProgress || !player.onGround || player.isInWater()) busyUntil = time + 3000;
        if (quiet && player.ticksExisted % 20 == 0
                && !world.getEntitiesWithinAABB(EntityMob.class, player.boundingBox.expand(12, 6, 12)).isEmpty()) CLOCK.disturb();
        health = player.getHealth();
        progress = NightmareMode.worldState;
        if ((active == null || !active.isSky()) && (blocked() || player.hurtTime > 0 || player.isBurning())) clear();
        if (active != null && !active.isMenu() && !active.isSky() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) clear();
        boolean playing = mc.currentScreen == null && mc.inGameHasFocus && !mc.getIsGamePaused();
        boolean due = CLOCK.advance(delta, playing && NightmareMode.scaryEvents && synchronizedState, quiet,
                active != null || ROSTER.eyeActive(time));

        for (String name : ROSTER.expire(time)) {
            if (!realPlayerName(name)) chat(EnumChatFormatting.YELLOW + name + " has left the game");
        }

        if (active != null) {
            if (mc.currentScreen != null && !active.isSky() && !(active.isMenu() && (menu == null || mc.currentScreen == menu.screen()))) clear();
            if (active == ScaryEvent.FOOTSTEPS) tickFootsteps(time);
            if (active == ScaryEvent.FIGURE && elapsed() > 1000 && figure != null && gaze(figure.xCoord, figure.yCoord + 1.8, figure.zCoord) > .997
                    && visible(figure.xCoord, figure.yCoord + 1.8, figure.zCoord)) blink();
        }
        tickTab(time);

        if (due && active == null && dimension == 0 && time >= busyUntil) {
            // one weighted choice per opportunity; contextual events never get a separate frequency budget.
            int roll = RANDOM.nextInt(1000);
            ScaryEvent event = roll < 160 ? ScaryEvent.FOOTSTEPS : roll < 350 ? ScaryEvent.SOUND
                    : roll < 465 ? ScaryEvent.CHAT_LAN : roll < 540 ? ScaryEvent.CHAT_CRASH
                    : roll < 650 ? ScaryEvent.CHAT_WHEEL : roll < 770 ? ScaryEvent.CHAT_JOIN
                    : roll < 905 ? ScaryEvent.ACHIEVEMENT : roll < 960 ? ScaryEvent.FIGURE
                    : roll < 983 ? ScaryEvent.MENU_LABELS : roll < 994 ? ScaryEvent.MENU_BLACKOUT
                    : roll < 996 ? ScaryEvent.MENU_EXIT_SPAM
                    : roll < 998 ? ScaryEvent.SKY_EYES : ScaryEvent.SKY_NIGHT;
            if (event.isSky() && (!cave() || !night())) event = ScaryEvent.SOUND;
            if (event == ScaryEvent.CHAT_LAN) {
                ScaryEvent[] chats = {ScaryEvent.CHAT_LAN, ScaryEvent.CHAT_ACHIEVEMENT, ScaryEvent.CHAT_COMMAND,
                        ScaryEvent.CHAT_TIME, ScaryEvent.CHAT_SCREENSHOT, ScaryEvent.CHAT_GAMEMODE};
                event = chats[RANDOM.nextInt(chats.length)];
            }
            String failure = start(event, false);
            if (failure != null) start(ScaryEvent.SOUND, false);
        }
    }

    private static String start(ScaryEvent event, boolean force) {
        if (world == null || player == null) return "no active world/player.";
        if (!force && (!NightmareMode.scaryEvents || !synchronizedState)) return "disabled.";
        if (blocked()) return "death, sleep, a boss or a locked sequence is active.";
        if (player.hurtTime > 0 || player.isBurning()) return "the player is in danger.";
        if ((event == ScaryEvent.FOOTSTEPS || event == ScaryEvent.FIGURE) && !cave()) return "requires an overworld cave below Y 60.";
        if (event.isSky() && !cave()) return "start underground in the overworld, below Y 60 with a ceiling overhead.";
        if (event.isSky() && !night()) return "requires night (world time 13000–22999); it then stays until morning.";
        if (event == ScaryEvent.CHAT_TIME && (!cave() || player.posY >= 40)) return "requires a deep cave below Y 40.";
        Achievement chatAchievement = event == ScaryEvent.CHAT_ACHIEVEMENT ? ScaryAchievements.pick(RANDOM) : null;
        if (event == ScaryEvent.CHAT_ACHIEVEMENT && chatAchievement == null) return "no achievements in BTW's registered tabs.";
        if ((event == ScaryEvent.BLINK || event == ScaryEvent.FIGURE)
                && ((EntityPlayerExt)player).nightmareMode$getBlinkLength() > 0) return "another blink is already active.";
        Vec3 position = event == ScaryEvent.FIGURE ? findFigure() : null;
        if (event == ScaryEvent.FIGURE && position == null) return "needs a visible dry floor 4–6 blocks ahead, with 3 blocks of height and 3 blocks of width for its arms. Stand below Y 60 under a ceiling and face the open space.";
        clear();
        active = event;
        forced = force;
        started = now();
        deadline = started + 15_000;
        CLOCK.afterEvent();
        switch (event) {
            case FOOTSTEPS -> {
                stepsLeft = 3 + RANDOM.nextInt(4);
                lastStep = (int)player.distanceWalkedOnStepModified;
                source = behind();
            }
            case SOUND -> {
                source = behind(12, 18);
                sound(SOUNDS[RANDOM.nextInt(SOUNDS.length)], source, .65f, 1f, 24f);
                deadline = started + 2500;
            }
            case CHAT_LAN -> { chat("Local game hosted on port " + (49152 + RANDOM.nextInt(16384))); deadline = started + 1000; }
            case CHAT_CRASH -> {
                String[] trace = {"Uncaught exception in java.util.concurrent.ExecutionException",
                        "java.lang.IllegalStateException: Task failed",
                        "  at java.util.concurrent.FutureTask.report(FutureTask.java:122)",
                        "  at java.util.concurrent.FutureTask.get(FutureTask.java:192)",
                        "  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)",
                        "  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)",
                        "  at java.lang.Thread.run(Thread.java:745)",
                        "Caused by: java.lang.NullPointerException", "  ..."};
                for (String line : trace) chat(EnumChatFormatting.RED + line);
                deadline = started + 1000;
            }
            case CHAT_WHEEL -> { player.addChatMessage("message.windMill.notEnoughRoom"); deadline = started + 1000; }
            case CHAT_JOIN -> {
                joinPlayer();
                if (!force && RANDOM.nextInt(24) == 0) {
                    beginJoinFlood();
                } else deadline = started + 1000;
            }
            case TAB_EYE -> { joinPlayer(); beginJoinFlood(); }
            case FIGURE -> { figure = position; figureYaw = player.rotationYaw; deadline = started + 20_000; }
            case BLINK -> beginBlink();
            case ACHIEVEMENT -> {
                int pick = RANDOM.nextInt(TITLES.length - 1);
                if (pick >= lastTitle) pick++;
                lastTitle = pick % TITLES.length;
                title = TITLES[lastTitle];
                deadline = started + 3000;
            }
            case SKY_EYES, SKY_NIGHT -> {
                skyMorning = world.getWorldTime() - Math.floorMod(world.getWorldTime(), 24_000) + 23_000;
                deadline = Long.MAX_VALUE;
            }
            case CHAT_ACHIEVEMENT -> {
                String name = ROSTER.names().isEmpty() ? newPlayerName() : ROSTER.names().get(0);
                chat(EnumChatFormatting.WHITE + name + " Achievement get! [" + EnumChatFormatting.GREEN
                        + I18n.getString(chatAchievement.getName()) + EnumChatFormatting.WHITE + "]");
                deadline = started + 1000;
            }
            case CHAT_COMMAND -> { chat(EnumChatFormatting.RED + "Unknown command. Try /help for a list of commands"); deadline = started + 1000; }
            case CHAT_TIME -> { chat("Set the time to " + (1 + RANDOM.nextInt(23)) * 1000); deadline = started + 1000; }
            case CHAT_SCREENSHOT -> { chat("Saved screenshot as " + EnumChatFormatting.UNDERLINE + screenshotName() + EnumChatFormatting.RESET); deadline = started + 1000; }
            case CHAT_GAMEMODE -> { chat("Your gamemode has been updated"); deadline = started + 1000; }
            case MENU_LABELS, MENU_BLACKOUT, MENU_EXIT_SPAM -> deadline = started + 60_000;
        }
        return null;
    }

    public static void blink() {
        boolean wasForced = forced;
        clear();
        active = ScaryEvent.BLINK;
        forced = wasForced;
        started = now();
        beginBlink();
    }

    private static void beginBlink() {
        blinkOwner = (EntityPlayerExt)player;
        blinkOwner.nightmareMode$setBlinkLength(SCARY_BLINK_LENGTH);
        deadline = now() + 3000;
    }

    public static boolean skyActive() {
        ScaryEvent event = active();
        return event == ScaryEvent.SKY_EYES || event == ScaryEvent.SKY_NIGHT;
    }

    private static void tickTab(long time) {
        if (active == ScaryEvent.TAB_EYE && joinsRemaining > 0 && time >= nextJoin) {
            joinPlayer();
            joinsRemaining--;
            nextJoin = time + 350;
        }
        boolean down = ROSTER.eyeActive(time) && tabDown();
        if (!down && tabSoundPlaying) {
            SoundSystem sound = soundSystem();
            if (sound != null) sound.stop(SOUND_SOURCE);
            tabSoundPlaying = false;
        }
        if (down && !tabWasDown) {
            tabStarted = time;
        }
        if (down && time - tabStarted >= 180 && !eyeSoundPlayed && (active == null || active == ScaryEvent.TAB_EYE)) {
            source = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
            sound("mob.endermen.stare", source, .28f, .85f);
            eyeSoundPlayed = true;
            tabSoundPlaying = true;
        }
        tabWasDown = down;
    }

    public static boolean tabDown() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.currentScreen == null && mc.gameSettings.keyBindPlayerList.pressed;
    }

    public static boolean eyePanelActive() {
        context();
        return player != null && !player.isDead && ROSTER.eyeActive(now()) && tabDown();
    }

    public static float stareIntensity() {
        boolean visible = eyePanelActive();
        return STARE.update(now(), visible, player != null && !player.isDead && player.getHealth() > 0
                && ROSTER.eyeActive(now()) && Minecraft.getMinecraft().currentScreen == null);
    }

    public static float stareGlitch() { return STARE.glitch(); }

    private static void tickFootsteps(long time) {
        if (!cave() || source == null || gaze(source.xCoord, player.posY, source.zCoord) > .35) { clear(); return; }
        int step = (int)player.distanceWalkedOnStepModified;
        if (stepsLeft > 0 && step != lastStep) {
            lastStep = step;
            if (stepDue == 0 && player.onGround && !player.isSneaking()) stepDue = time + 140 + RANDOM.nextInt(150);
        }
        if (stepDue != 0 && time >= stepDue) {
            source = behind();
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.boundingBox.minY - .1);
            int z = MathHelper.floor_double(player.posZ);
            Block ground = Block.blocksList[world.getBlockId(x, y, z)];
            StepSound stepSound = (ground == null ? Block.stone : ground).getStepSound(world, x, y, z);
            sound(stepSound.getStepSound(), source, stepSound.getStepVolume() * .3f, stepSound.getStepPitch());
            stepDue = 0;
            if (--stepsLeft == 0) deadline = time + 500;
        }
    }

    private static Vec3 behind() {
        return behind(5, 10);
    }

    private static Vec3 behind(double minimum, double maximum) {
        double yaw = Math.toRadians(player.rotationYaw + RANDOM.nextInt(61) - 30);
        double distance = minimum + RANDOM.nextDouble() * (maximum - minimum);
        return Vec3.createVectorHelper(player.posX + Math.sin(yaw) * distance,
                player.boundingBox.minY + .2, player.posZ - Math.cos(yaw) * distance);
    }

    private static boolean cave() {
        return dimension == 0 && player.posY < 60 && !openSky();
    }

    private static boolean openSky() {
        return dimension == 0 && world.canBlockSeeTheSky(MathHelper.floor_double(player.posX),
                MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
    }

    private static boolean night() {
        long time = Math.floorMod(world.getWorldTime(), 24_000);
        return dimension == 0 && time >= 13_000 && time < 23_000;
    }

    private static Vec3 findFigure() {
        double bodyYaw = Math.toRadians(player.rotationYaw);
        double armX = 1.43 * Math.abs(Math.cos(bodyYaw)) + .13 * Math.abs(Math.sin(bodyYaw));
        double armZ = 1.43 * Math.abs(Math.sin(bodyYaw)) + .13 * Math.abs(Math.cos(bodyYaw));
        for (double distance : new double[]{5, 4, 6}) {
          for (int angle : new int[]{0, 15, -15, 30, -30, 45, -45, 60, -60}) {
            double yaw = Math.toRadians(player.rotationYaw + angle);
            double centerX = player.posX - Math.sin(yaw) * distance;
            double centerZ = player.posZ + Math.cos(yaw) * distance;
            int x = MathHelper.floor_double(centerX);
            int z = MathHelper.floor_double(centerZ);
            for (int offset : new int[]{0, -1, 1, -2, 2}) {
                int y = MathHelper.floor_double(player.boundingBox.minY) + offset;
                if (y < 2 || y > 58 || !world.checkChunksExist(x - 2, y - 1, z - 2, x + 2, y + 3, z + 2)) continue;
                if (!world.doesBlockHaveSolidTopSurface(x, y - 1, z) || world.canBlockSeeTheSky(x, y, z)) continue;
                AxisAlignedBB body = AxisAlignedBB.getBoundingBox(centerX - .35, y, centerZ - .35, centerX + .35, y + 2.8, centerZ + .35);
                AxisAlignedBB arms = AxisAlignedBB.getBoundingBox(centerX - armX, y + 2, centerZ - armZ, centerX + armX, y + 2.25, centerZ + armZ);
                if (!world.getCollidingBoundingBoxes(player, body).isEmpty() || !world.getCollidingBoundingBoxes(player, arms).isEmpty()
                        || world.isAnyLiquid(body) || world.isAnyLiquid(arms)) continue;
                if (visible(centerX, y + 1.8, centerZ) && visible(centerX, y + .3, centerZ)) {
                    return Vec3.createVectorHelper(centerX, y, centerZ);
                }
            }
          }
        }
        return null;
    }

    private static void beginJoinFlood() {
        active = ScaryEvent.TAB_EYE;
        deadline = now() + 5000;
        ROSTER.armEye();
        eyeSoundPlayed = false;
        tabWasDown = false;
        joinsRemaining = 11;
        nextJoin = now() + 350;
    }

    private static void joinPlayer() {
        String name = newPlayerName();
        ROSTER.join(name, now());
        chat(EnumChatFormatting.YELLOW + name + " has joined the game");
    }

    private static String newPlayerName() {
        String name = NAMES[RANDOM.nextInt(NAMES.length)];
        while (ROSTER.names().contains(name) || realPlayerName(name)) name = "Player_" + (100 + RANDOM.nextInt(9900));
        return name;
    }

    private static boolean realPlayerName(String name) {
        if (player == null) return false;
        if (player.username.equalsIgnoreCase(name)) return true;
        for (Object entry : player.sendQueue.playerInfoList) if (((GuiPlayerInfo)entry).name.equalsIgnoreCase(name)) return true;
        return false;
    }

    public static List<GuiPlayerInfo> playerList(NetClientHandler connection) {
        context();
        // vanilla still draws its gray panel and rows, but no names, scores or ping icons.
        if (eyePanelActive()) return java.util.Collections.emptyList();
        List<GuiPlayerInfo> list = new ArrayList<>(connection.playerInfoList);
        if (list.isEmpty() && player != null) list.add(new GuiPlayerInfo(player.username));
        for (String name : ROSTER.names()) {
            if (realPlayerName(name)) continue;
            GuiPlayerInfo entry = new GuiPlayerInfo(name);
            entry.responseTime = 45;
            list.add(entry);
        }
        return list;
    }

    private static String screenshotName() {
        String base = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        File directory = new File(Minecraft.getMinecraft().mcDataDir, "screenshots");
        String name = base + ".png";
        for (int suffix = 2; new File(directory, name).exists(); suffix++) name = base + "_" + suffix + ".png";
        return name;
    }

    private static boolean visible(double x, double y, double z) {
        return world.clip(player.getPosition(1), Vec3.createVectorHelper(x, y, z)) == null;
    }

    private static double gaze(double x, double y, double z) {
        Vec3 eye = player.getPosition(1);
        return player.getLookVec().dotProduct(Vec3.createVectorHelper(x - eye.xCoord, y - eye.yCoord, z - eye.zCoord).normalize());
    }

    private static void chat(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(message);
    }

    private static SoundSystem soundSystem() {
        SoundManager manager = Minecraft.getMinecraft().sndManager;
        return manager == null ? null : ((SoundManagerAccess)manager).getSoundSystem();
    }

    private static void sound(String name, Vec3 position, float volume, float pitch) {
        sound(name, position, volume, pitch, 16f);
    }

    private static void sound(String name, Vec3 position, float volume, float pitch, float range) {
        Minecraft mc = Minecraft.getMinecraft();
        SoundSystem sound = soundSystem();
        if (sound == null || mc.gameSettings.soundVolume == 0) return;
        SoundPoolEntry entry = ((SoundManagerAccess)mc.sndManager).getSoundPoolSounds().getRandomSoundFromSoundPool(name);
        if (entry == null) return;
        sound.stop(SOUND_SOURCE);
        sound.newSource(false, SOUND_SOURCE, entry.getSoundUrl(), entry.getSoundName(), false,
                (float)position.xCoord, (float)position.yCoord, (float)position.zCoord, 2, range);
        sound.setVolume(SOUND_SOURCE, volume * mc.gameSettings.soundVolume);
        sound.setPitch(SOUND_SOURCE, pitch);
        sound.play(SOUND_SOURCE);
    }

    public static void openMenu(GuiIngameMenu screen, java.util.List buttons) {
        ScaryEvent event = active();
        if (event == null || !event.isMenu() || blocked()) return;
        boolean alreadyOpen = menu != null;
        if (alreadyOpen) menu.restore();
        menu = new ScaryMenu(screen, buttons, event, RANDOM);
        if (!alreadyOpen) deadline = now() + 30_000;
    }

    public static void closeMenu(GuiIngameMenu screen) {
        if (menu != null && menu.screen() == screen) clear();
    }

    public static void renderMenu(int mouseX, int mouseY) {
        active();
        if (menu != null) menu.draw(mouseX, mouseY);
    }

    public static GuiButton clickedFake(int mouseX, int mouseY) {
        active();
        return menu == null ? null : menu.clickedFake(mouseX, mouseY);
    }

    public static void screenChanging(GuiScreen next) {
        if (active == null) return;
        if (active.isSky()) return;
        if (next instanceof GuiIngameMenu && active.isMenu() && menu == null) return;
        if (next != null || menu != null) clear();
    }
}
