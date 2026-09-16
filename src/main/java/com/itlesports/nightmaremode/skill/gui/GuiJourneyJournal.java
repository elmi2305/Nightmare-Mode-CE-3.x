package com.itlesports.nightmaremode.skill.gui;

import com.itlesports.nightmaremode.util.JourneyJournals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public class GuiJourneyJournal extends GuiScreenBook {
    private final GuiScreen parent;

    public GuiJourneyJournal(EntityPlayer player, int index, GuiScreen parent) {
        super(player, readableCopy(index), false);
        this.parent = parent;
    }

    private static ItemStack readableCopy(int index) {
        ItemStack stack = JourneyJournals.create(index);
        NBTTagList source = stack.getTagCompound().getTagList("pages");
        NBTTagList pages = new NBTTagList();
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        for (int page = 0; page < source.tagCount(); ++page) {
            String pending = "";
            for (String paragraph : ((NBTTagString)source.tagAt(page)).data.split("\n\n")) {
                String candidate = pending.isEmpty() ? paragraph : pending + "\n\n" + paragraph;
                if (font.splitStringWidth(candidate, 116) <= 118) {
                    pending = candidate;
                    continue;
                }
                if (!pending.isEmpty()) pages.appendTag(new NBTTagString("", pending));
                java.util.List<String> lines = font.listFormattedStringToWidth(paragraph, 116);
                int linesPerPage = Math.max(1, 118 / font.FONT_HEIGHT);
                int first = 0;
                while (lines.size() - first > linesPerPage) {
                    pages.appendTag(new NBTTagString("", String.join("\n", lines.subList(first, first + linesPerPage))));
                    first += linesPerPage;
                }
                pending = String.join("\n", lines.subList(first, lines.size()));
            }
            if (!pending.isEmpty()) pages.appendTag(new NBTTagString("", pending));
        }
        stack.getTagCompound().setTag("pages", pages);
        return stack;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) this.mc.displayGuiScreen(this.parent);
        else super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) this.mc.displayGuiScreen(this.parent);
        else super.keyTyped(character, keyCode);
    }
}
