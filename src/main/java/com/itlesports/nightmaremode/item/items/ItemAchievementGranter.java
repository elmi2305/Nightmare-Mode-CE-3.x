package com.itlesports.nightmaremode.item.items;

import api.achievement.AchievementHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;

public class ItemAchievementGranter extends ItemFood {
    private final List<Achievement> achievementList = new ArrayList<>();


    public ItemAchievementGranter(int iItemID, Achievement... achievements) {
        super(iItemID, 0, 0, false, false);
        for(Object ac : achievements){
            this.achievementList.add((Achievement) ac);
        }
    }
    public String getModId() {
        return "nightmare_mode";
    }


    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        for(Object ac : this.achievementList){
            AchievementHandler.triggerAchievement(par3EntityPlayer, (Achievement) ac);
            displayAchievement((Achievement) ac,par3EntityPlayer);
        }
        return super.onEaten(par1ItemStack, par2World, par3EntityPlayer);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return super.getMaxItemUseDuration(par1ItemStack) * 4;
    }
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.drink;
    }

    private void displayAchievement(Achievement achievement, EntityPlayer player){
        if(AchievementHandler.hasUnlocked(player,achievement)) return;
//        ChatMessageComponent name = ChatMessageComponent.createFromTranslationKey(achievement.toString());
        ChatMessageComponent achievementMessage = ChatMessageComponent.createFromText("[").addKey(achievement.toString()).addText("]").setColor(achievement.achievementColor);
        ChatMessageComponent msg = ChatMessageComponent.createFromText(player.username + " ").appendComponent(ChatMessageComponent.createFromTranslationKey("achievement.get")).addText(" ").appendComponent(achievementMessage);
        if (!achievement.shouldAnnounce || player.worldObj.isRemote) return;
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(msg);
    }
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        if (!this.achievementList.isEmpty()) {
            list.add("\2472" + I18n.getString("achievement.nm.grants"));
            for (Object ac : this.achievementList) {
                String name = I18n.getString(ac.toString());
                list.add("\247e" + name);
            }
        }
        super.addInformation(stack,player,list,advanced);
    }
}
