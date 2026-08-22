package com.itlesports.nightmaremode.item.items.bloodItems;

import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.items.template.ItemAchievementGranter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ItemEventController extends ItemAchievementGranter {
    private final int eventType;

    public static int EVENT_BLOODMOON = 1;
    public static int EVENT_ECLIPSE  = 2;
    private static final long EVENT_DRINK_WINDOW_TICKS = 300L;
    private static final Map<UUID, Long> recentBloodMoonDrinkers = new HashMap<>();
    public ItemEventController(int iItemID, int type, Achievement... achievements) {
        super(iItemID, achievements);
        this.eventType = type;
    }


    @Override
    public ItemStack onEaten(ItemStack stack, World w, EntityPlayer player) {
        if (!w.isRemote && this.eventType != 0) {
            if (this.eventType != EVENT_BLOODMOON || allOnlinePlayersDrankBlood(player, w)) {
                performEvent(eventType, w);
            }
        }


        return super.onEaten(stack, w, player);
    }

    private boolean allOnlinePlayersDrankBlood(EntityPlayer drinkingPlayer, World world) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return true;
        }

        long now = world.getTotalWorldTime();
        Set<UUID> onlinePlayers = new HashSet<>();
        for (Object obj : server.getConfigurationManager().playerEntityList) {
            if (obj instanceof EntityPlayer player) {
                onlinePlayers.add(player.getUniqueID());
            }
        }

        recentBloodMoonDrinkers.keySet().retainAll(onlinePlayers);
        recentBloodMoonDrinkers.entrySet().removeIf(entry -> now - entry.getValue() > EVENT_DRINK_WINDOW_TICKS);
        recentBloodMoonDrinkers.put(drinkingPlayer.getUniqueID(), now);

        if (!recentBloodMoonDrinkers.keySet().containsAll(onlinePlayers)) {
            return false;
        }

        recentBloodMoonDrinkers.clear();
        return true;
    }

    private void performEvent(int type, World world){
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null) {
            world = server.worldServers[0];
        }

        if(type == EVENT_BLOODMOON){
            long time = world.getWorldTime();

            if(NMUtils.getIsBloodMoon()){
                time = (long)Math.floor(((double) (time + 24000) / 24000)) * 24000L;
            } else {
                time = NMUtils.getNextBloodMoonTime(time);
            }
            world.setWorldTime(time);
        }
        if(type == EVENT_ECLIPSE){
            long time = world.getWorldTime();

            if(NMUtils.getIsEclipse()){
                time = (long)Math.floor(((double) (time + 24000) / 24000)) * 24000L;
            } else {
                time = NMUtils.getNextEclipseTime(time);
            }
            world.setWorldTime(time);
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add("\247c" + I18n.getString(this.getLineForType(0,eventType)));
        if (!MinecraftServer.getIsServer()) {
            list.add("\247c" + I18n.getString(this.getLineForType(1,eventType)));
        }

        super.addInformation(stack,player,list,advanced);
    }

    private String getLineForType(int line, int type){
        if(type == EVENT_BLOODMOON){
            return "item.desc.bloodmoon" + line;
        }
        if(type == EVENT_ECLIPSE){
            return "item.desc.eclipse" + line;
        }

        return "";
    }
}
