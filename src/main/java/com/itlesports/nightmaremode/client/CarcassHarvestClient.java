package com.itlesports.nightmaremode.client;

import com.itlesports.nightmaremode.util.CarcassHarvestNet;
import com.itlesports.nightmaremode.util.CarcassHarvesting;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.Gui;
import net.minecraft.src.I18n;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ScaledResolution;

@Environment(EnvType.CLIENT)
public final class CarcassHarvestClient {
    private static int activeEntityId = -1;

    private CarcassHarvestClient() {
    }

    public static boolean consumeCarcassRightClick(Minecraft mc) {
        if (activeEntityId != -1) {
            return true;
        }
        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != EnumMovingObjectType.ENTITY) {
            return false;
        }

        Entity target = mc.objectMouseOver.entityHit;
        if (!(target instanceof EntityAnimal) || !(target instanceof CarcassAnimal carcass) || !carcass.nm$isCarcass()) {
            return false;
        }

        ItemStack held = mc.thePlayer.getHeldItem();
        if (CarcassHarvesting.isValidTool(held)) {
            activeEntityId = target.entityId;
            CarcassHarvestNet.sendContinue(activeEntityId);
        }
        return true;
    }

    public static void tick(Minecraft mc) {
        if (activeEntityId == -1 || mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        Entity entity = mc.theWorld.getEntityByID(activeEntityId);
        if (!(entity instanceof EntityAnimal) || !(entity instanceof CarcassAnimal carcass)
                || entity.isDead || !carcass.nm$isCarcass()) {
            clear(false);
            return;
        }

        if (mc.currentScreen != null || isMoving(mc)) {
            clear(true);
            return;
        }

        int harvesterId = carcass.nm$getHarvesterId();
        if (harvesterId != -1 && harvesterId != mc.thePlayer.entityId) {
            clear(false);
            return;
        }

        CarcassHarvestNet.sendContinue(activeEntityId);
    }

    public static boolean isHarvesting() {
        return activeEntityId != -1;
    }

    public static void renderProgress(Minecraft mc) {
        if (activeEntityId == -1 || mc.thePlayer == null || mc.theWorld == null || mc.currentScreen != null) {
            return;
        }

        Entity entity = mc.theWorld.getEntityByID(activeEntityId);
        if (!(entity instanceof CarcassAnimal carcass) || !carcass.nm$isCarcass()) {
            return;
        }

        ScaledResolution resolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int screenWidth = resolution.getScaledWidth();
        int screenHeight = resolution.getScaledHeight();
        int width = 110;
        int height = 8;
        int x = (screenWidth - width) / 2;
        int y = screenHeight - 67;
        int progress = Math.min(1000, Math.max(0, carcass.nm$getHarvestProgress()));
        int fill = (width - 2) * progress / 1000;

        Gui.drawRect(x - 1, y - 1, x + width + 1, y + height + 1, 0xCC000000);
        Gui.drawRect(x, y, x + width, y + height, 0xFF3A1717);
        Gui.drawRect(x + 1, y + 1, x + 1 + fill, y + height - 1, 0xFFB52A2A);

        String label = I18n.getString("gui.ifhyHarvestingCarcass");
        mc.fontRenderer.drawStringWithShadow(label, (screenWidth - mc.fontRenderer.getStringWidth(label)) / 2, y - 11, 0xFFFFFF);
    }

    private static boolean isMoving(Minecraft mc) {
        return mc.gameSettings.keyBindForward.pressed
                || mc.gameSettings.keyBindBack.pressed
                || mc.gameSettings.keyBindLeft.pressed
                || mc.gameSettings.keyBindRight.pressed
                || mc.gameSettings.keyBindJump.pressed
                || mc.gameSettings.keyBindSneak.pressed;
    }

    private static void clear(boolean notifyServer) {
        int oldEntityId = activeEntityId;
        activeEntityId = -1;
        if (notifyServer && oldEntityId != -1) {
            CarcassHarvestNet.sendCancel(oldEntityId);
        }
    }
}
