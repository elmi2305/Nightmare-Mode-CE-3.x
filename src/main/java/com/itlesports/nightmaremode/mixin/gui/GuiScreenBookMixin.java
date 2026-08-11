package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiScreenBook;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenBook.class)
public abstract class GuiScreenBookMixin extends GuiScreen {
    private static final int NIGHTMARE_FULL_BOOK_LIGHT = 13;
    private static final int NIGHTMARE_MINIMUM_BOOK_LIGHT = 5;
    private static final float NIGHTMARE_MAX_BOOK_DARKNESS = 0.85F;

    public boolean doesGuiPauseGame() {
        return false;
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void nightmareMode$darkenBookInLowLight(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (minecraft.theWorld == null || player == null) return;

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY + player.getEyeHeight());
        int z = MathHelper.floor_double(player.posZ);
        int light = minecraft.theWorld.getBlockLightValue(x, y, z);
        if (light >= NIGHTMARE_FULL_BOOK_LIGHT) return;

        float darknessProgress = (NIGHTMARE_FULL_BOOK_LIGHT - light)
                / (float)(NIGHTMARE_FULL_BOOK_LIGHT - NIGHTMARE_MINIMUM_BOOK_LIGHT);
        float darkness = Math.min(1.0F, darknessProgress) * NIGHTMARE_MAX_BOOK_DARKNESS;
        int alpha = Math.round(darkness * 255.0F);
        Gui.drawRect(0, 0, this.width, this.height, alpha << 24);
    }
}
