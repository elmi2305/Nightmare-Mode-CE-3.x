package com.itlesports.nightmaremode.skill.item;

import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillNet;
import com.itlesports.nightmaremode.skill.gui.GuiSkillTree;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.World;

public class ItemSkillBook extends NMItem {
    public ItemSkillBook(int id) {
        super(id);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            SkillNet.sendSyncRequest();
            Minecraft.getMinecraft().displayGuiScreen(new GuiSkillTree());
        } else if (player instanceof EntityPlayerMP playerMP) {
            SkillHandler.sync(playerMP);
        }
        return stack;
    }
}
