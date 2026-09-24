package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.JourneyJournals;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.skill.gui.GuiJourneyJournal;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class ItemJourneyJournal extends ItemEditableBook {
    private final int journalIndex;

    public ItemJourneyJournal(int id, int journalIndex) {
        super(id);
        this.journalIndex = journalIndex;
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setUnlocalizedName("journeyJournal" + (journalIndex + 1));
        this.setTextureName(NMFields.modID + ":journeyJournal" + (journalIndex + 1));
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, EntityPlayer player, int slot, boolean held) {
        if (!stack.hasTagCompound()) stack.setTagCompound(JourneyJournals.create(this.journalIndex).getTagCompound());
        JourneyJournals.collect(player, stack, this.journalIndex);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!stack.hasTagCompound()) stack.setTagCompound(JourneyJournals.create(this.journalIndex).getTagCompound());
        if (world.isRemote) openJournal(player);
        return stack;
    }

    @Environment(EnvType.CLIENT)
    private void openJournal(EntityPlayer player) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiJourneyJournal(player, this.journalIndex, null));
    }

    @Override
    public String getItemDisplayName(ItemStack stack) { return JourneyJournals.title(this.journalIndex); }

    @Override
    public boolean hasEffect(ItemStack stack) { return false; }
}
