package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.integration.emi.RecipeTreeScreenshot;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.EmiRenderHelper;
import emi.dev.emi.emi.bom.BoM;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.dev.emi.emi.screen.BoMScreen;
import emi.dev.emi.emi.screen.Bounds;
import emi.dev.emi.emi.screen.StackBatcher;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.List;

@Mixin(value = BoMScreen.class, remap = false)
public abstract class EmiBoMScreenMixin extends GuiScreen {
    @Shadow private List<?> nodes;
    @Shadow private List<?> costs;
    @Shadow private Bounds batches;
    @Shadow private Bounds mode;
    @Shadow private int nodeHeight;
    @Shadow private boolean hasRemainders;
    @Shadow private static StackBatcher batcher;
    @Unique private boolean screenshotPending;

    @Unique
    private boolean cameraContains(double x, double y) {
        return x >= this.width - 38 && x < this.width - 20 && y >= this.height - 20 && y < this.height - 2;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void clickCamera(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
        if (BoM.tree != null && cameraContains(x, y)) {
            if (button == 0) {
                screenshotPending = true;
                Minecraft.getMinecraft().sndManager.playSoundFX("random.click", 1, 1);
            }
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void captureTree(DrawContext raw, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!screenshotPending) return;
        screenshotPending = false;
        if (BoM.tree == null || nodes.isEmpty()) return;
        Minecraft mc = Minecraft.getMinecraft();
        try {
            int cy = nodeHeight * 40;
            int labelWidth = mc.fontRenderer.getStringWidth(EmiPort.translatable("emi.total_cost").asString());
            if (hasRemainders) {
                labelWidth = Math.max(labelWidth, mc.fontRenderer.getStringWidth(EmiPort.translatable("emi.leftovers").asString()));
            }
            int left = Math.min(-labelWidth / 2, batches.x());
            int right = Math.max(labelWidth / 2 + 1, Math.max(batches.x() + batches.width(), mode.x() + mode.width()));
            int top = batches.y();
            int bottom = cy + (hasRemainders ? 56 : 16);
            for (Object value : nodes) {
                EmiTreeNodeAccessor node = (EmiTreeNodeAccessor) value;
                left = Math.min(left, node.nm$getX() - node.nm$getWidth() / 2);
                right = Math.max(right, node.nm$getX() + (node.nm$getWidth() + 1) / 2 + 1);
                top = Math.min(top, node.nm$getY() - 19);
                bottom = Math.max(bottom, node.nm$getY() + 20);
            }
            for (Object value : costs) {
                EmiTreeCostAccessor cost = (EmiTreeCostAccessor) value;
                left = Math.min(left, cost.nm$getX());
                right = Math.max(right, cost.nm$getX() + 16 + EmiRenderHelper.getAmountOverflow(cost.nm$getAmountText()));
                bottom = Math.max(bottom, cost.nm$getY() + 20);
            }
            EmiDrawContext context = EmiDrawContext.wrap(raw);
            File file = RecipeTreeScreenshot.save(left - 12, top - 12, right - left + 24, bottom - top + 24,
                    () -> renderCapturedTree(context, delta));
            mc.ingameGUI.getChatGUI().printChatMessage("Saved recipe tree screenshot: " + file.getName());
        } catch (Exception | OutOfMemoryError e) {
            mc.ingameGUI.getChatGUI().printChatMessage("Could not save recipe tree screenshot: " + e.getMessage());
        }
    }

    @Unique
    private void renderCapturedTree(EmiDrawContext context, float delta) {
        batcher.begin(0, 0, 0);
        context.drawCenteredText(EmiPort.translatable("emi.total_cost"), 0, nodeHeight * 40 - 16);
        if (hasRemainders) {
            context.drawCenteredText(EmiPort.translatable("emi.leftovers"), 0, nodeHeight * 40 + 24);
        }
        for (Object cost : costs) ((EmiTreeCostAccessor) cost).nm$render(context);
        for (Object node : nodes) ((EmiTreeNodeAccessor) node).nm$render(context, Integer.MIN_VALUE, Integer.MIN_VALUE, delta);
        context.drawTextWithShadow(EmiPort.literal("x" + BoM.tree.batches), batches.x() + 6, batches.y() + batches.height() / 2 - 4, -1);
        context.setColor(1, 1, 1, 1);
        context.drawTexture(EmiRenderHelper.WIDGETS, mode.x(), mode.y(), BoM.craftingMode ? 16 : 0, 146, mode.width(), mode.height());
        batcher.draw();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void drawCamera(DrawContext raw, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (BoM.tree == null) return;
        int x = this.width - 38;
        int y = this.height - 20;
        boolean hovered = cameraContains(mouseX, mouseY);
        int color = hovered ? 0xff8099ff : 0xffdddddd;
        drawRect(x, y, x + 18, y + 18, 0xdd202020);
        drawRect(x + 3, y + 5, x + 15, y + 14, color);
        drawRect(x + 4, y + 6, x + 14, y + 13, 0xff202020);
        drawRect(x + 5, y + 3, x + 9, y + 5, color);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(hovered ? 0.5f : 0.87f, hovered ? 0.6f : 0.87f, hovered ? 1 : 0.87f, 1);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_QUAD_STRIP);
        for (int i = 0; i <= 24; i++) {
            double angle = i * Math.PI / 12;
            tessellator.addVertex(x + 9 + Math.cos(angle) * 3, y + 9.5 + Math.sin(angle) * 3, 0);
            tessellator.addVertex(x + 9 + Math.cos(angle) * 2, y + 9.5 + Math.sin(angle) * 2, 0);
        }
        tessellator.draw();
        GL11.glPopAttrib();
        if (hovered) {
            String text = "Save entire recipe tree";
            int textX = this.width - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) - 6;
            drawRect(textX - 3, y - 15, this.width - 3, y - 2, 0xee101010);
            EmiDrawContext.wrap(raw).drawTextWithShadow(EmiPort.literal(text), textX, y - 12, -1);
        }
    }
}
