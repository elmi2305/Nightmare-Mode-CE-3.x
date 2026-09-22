package com.itlesports.nightmaremode.client;

import com.itlesports.nightmaremode.scary.ScaryEvent;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class ScaryMenu extends Gui {
    private final GuiIngameMenu screen;
    private final ScaryEvent event;
    private final List<SavedButton> originals = new ArrayList<>();
    private final List<GuiButton> fakeButtons = new ArrayList<>();

    private record SavedButton(GuiButton button, String label, boolean enabled) {}

    ScaryMenu(GuiIngameMenu screen, List buttons, ScaryEvent event, Random random) {
        this.screen = screen;
        this.event = event;
        for (Object entry : buttons) {
            GuiButton button = (GuiButton)entry;
            originals.add(new SavedButton(button, button.displayString, button.enabled));
            if (event == ScaryEvent.MENU_LABELS) button.displayString = "Leave Game";
            if (event == ScaryEvent.MENU_BLACKOUT && button.id != 1) button.enabled = false;
        }
        if (event == ScaryEvent.MENU_EXIT_SPAM) {
            for (int i = 0; i < 26; i++) {
                int width = Math.min(100, screen.width);
                fakeButtons.add(new GuiButton(1, random.nextInt(Math.max(1, screen.width - width + 1)),
                        random.nextInt(Math.max(1, screen.height - 20)), width, 20, "Leave Game"));
            }
        }
    }

    GuiIngameMenu screen() { return screen; }

    void restore() {
        for (SavedButton saved : originals) {
            saved.button.displayString = saved.label;
            saved.button.enabled = saved.enabled;
        }
        fakeButtons.clear();
    }

    GuiButton clickedFake(int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        for (GuiButton fake : fakeButtons) {
            if (!fake.mousePressed(mc, x, y)) continue;
            for (SavedButton saved : originals) if (saved.button.id == 1 && saved.button.enabled) return saved.button;
        }
        return null;
    }

    void draw(int mouseX, int mouseY) {
        if (event == ScaryEvent.MENU_BLACKOUT) {
            for (SavedButton saved : originals) {
                GuiButton b = saved.button;
                if (b.id == 1) continue;
                b.enabled = false;
                drawRect(b.xPosition, b.yPosition, b.xPosition + b.width, b.yPosition + b.height, 0xff000000);
            }
        }
        for (GuiButton fake : fakeButtons) fake.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
    }
}
