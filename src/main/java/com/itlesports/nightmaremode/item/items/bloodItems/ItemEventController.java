package com.itlesports.nightmaremode.item.items.bloodItems;

import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.item.items.ItemAchievementGranter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;

public class ItemEventController extends ItemAchievementGranter {
    private final int eventType;

    public static int EVENT_BLOODMOON = 1;
    public static int EVENT_ECLIPSE  = 2;
    public ItemEventController(int iItemID, int type, Achievement... achievements) {
        super(iItemID, achievements);
        this.eventType = type;
    }


    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if(this.eventType != 0){
            performEvent(eventType,par2World);
        }


        return super.onEaten(par1ItemStack, par2World, par3EntityPlayer);
    }
    private void performEvent(int type, World world){
        if(type == EVENT_BLOODMOON){
            long time = world.getWorldTime();

            if(NMUtils.getIsBloodMoon()){
                time = (long)Math.floor(((double) (time + 24000) / 24000)) * 24000L;
            } else if (!MinecraftServer.getIsServer()) {
                time = NMUtils.getNextBloodMoonTime(time);
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

        return "";
    }
}
