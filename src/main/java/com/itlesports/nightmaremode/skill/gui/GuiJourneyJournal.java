package com.itlesports.nightmaremode.skill.gui;

import com.itlesports.nightmaremode.util.JourneyJournals;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.mixin.gui.GuiScreenBookAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class GuiJourneyJournal extends GuiScreenBook {
    private final GuiScreen parent;
    private final ResourceLocation image;
    private final int imagePage;

    public GuiJourneyJournal(EntityPlayer player, int index, GuiScreen parent) {
        this(player, index, parent, readableCopy(index));
    }

    private GuiJourneyJournal(EntityPlayer player, int index, GuiScreen parent, ItemStack stack) {
        super(player, stack, false);
        this.parent = parent;
        this.image = index == 3 ? new ResourceLocation(NMFields.modID, "textures/gui/journals/wither_ritual.png")
                : index == 4 ? new ResourceLocation(NMFields.modID, "textures/gui/journals/blood_wither_ritual.png") : null;
        this.imagePage = this.image == null ? -1 : stack.getTagCompound().getTagList("pages").tagCount() - 1;
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
        if (index >= 3) pages.appendTag(new NBTTagString("", ""));
        stack.getTagCompound().setTag("pages", pages);
        return stack;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.image == null || ((GuiScreenBookAccessor)this).nm$getCurrentPage() != this.imagePage) return;
        this.mc.getTextureManager().bindTexture(this.image);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        double left = (this.width - 192) / 2.0D + 36;
        double top = 34;
        Tessellator draw = Tessellator.instance;
        draw.startDrawingQuads();
        draw.addVertexWithUV(left, top + 118, this.zLevel, 0, 1);
        draw.addVertexWithUV(left + 116, top + 118, this.zLevel, 1, 1);
        draw.addVertexWithUV(left + 116, top, this.zLevel, 1, 0);
        draw.addVertexWithUV(left, top, this.zLevel, 0, 0);
        draw.draw();
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
