package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.fishing.*;
import com.itlesports.nightmaremode.network.FishingNet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import java.util.*;

@Environment(EnvType.CLIENT)
public final class GuiFishing extends GuiScreen {
    private static final ResourceLocation ITEM_ATLAS = new ResourceLocation("textures/atlas/items.png");
    private static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
    public final int hook;
    private final boolean lava;
    private final FishingLine rope = new FishingLine();
    private final FishingFishPose fishPose = new FishingFishPose();
    private final Map<Integer, Visual> fish = new LinkedHashMap<>();
    private long lastFrame = System.nanoTime(), lastSnapshot = System.currentTimeMillis();
    private float time, stock = 1, depth = 1, lureX = 400, lureY = 42, ropeAccumulator;
    private int serverAge, finishTicks, caughtItem, fightingId, remaining = 1, catchFlash;
    private int serverFightAge;
    private float serverDepth = 1, depthLimit = 200, serverLureX = 400, serverLureY = 42;
    private boolean positioned, depthWinch;
    private float pendingScroll;
    private float lureRadius = FishingGame.LURE_RADIUS;
    private boolean finished, closed;
    private Cursor previousCursor;
    private Cursor invisibleCursor;

    public GuiFishing(int hook, boolean lava) { this.hook = hook; this.lava = lava; }

    @Override public void initGui() {
        if (!positioned) { positionCursor(400, 42); positioned = true; }
        // hide the native pointer; the lure is the only tracking cursor.
        if (invisibleCursor == null) try {
            previousCursor = Mouse.getNativeCursor();
            int size = Cursor.getMinCursorSize();
            invisibleCursor = new Cursor(size, size, 0, 0, 1, BufferUtils.createIntBuffer(size * size), null);
            Mouse.setNativeCursor(invisibleCursor);
        } catch (org.lwjgl.LWJGLException ignored) {}
    }

    public void snapshot(int age, float stock, float depth, float maxDepth, float lureX, float lureY,
                         int fighting, int fightAge, int remaining, boolean winch, float lureRadius, List<FishingGame.Target> targets) {
        serverDepth = depth; depthLimit = maxDepth;
        serverLureX = lureX; serverLureY = lureY;
        this.remaining = remaining;
        depthWinch = winch;
        this.lureRadius = lureRadius;
        if ((fightingId == 0) != (fighting == 0)) {
            Mouse.setGrabbed(fighting != 0);
            Mouse.getDX(); Mouse.getDY();
            if (fighting == 0) positionCursor(lureX, lureY);
        }
        fightingId = fighting;
        serverFightAge = fightAge;
        serverAge = age;
        this.stock = stock;
        lastSnapshot = System.currentTimeMillis();
        Set<Integer> present = new HashSet<>();
        for (FishingGame.Target target : targets) {
            present.add(target.id);
            Visual visual = fish.get(target.id);
            if (visual == null) fish.put(target.id, new Visual(target));
            else visual.target = target;
        }
        fish.keySet().retainAll(present);
    }

    private void positionCursor(float x, float y) {
        Mouse.setCursorPosition(Math.round(x * mc.displayWidth / 800), Math.round(mc.displayHeight - y * mc.displayHeight / 450));
    }

    public void caught(int item) { caughtItem = item; catchFlash = 35; }

    public void finish(int item) {
        finished = true;
        caughtItem = item;
        if (item == 0) mc.displayGuiScreen(null);
        else {
            mc.sndManager.playSoundFX("random.splash", 0.7F, 1.15F);
            finishTicks = 24;
        }
    }

    @Override public void updateScreen() {
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.isDead
                || System.currentTimeMillis() - lastSnapshot > 12000) {
            mc.displayGuiScreen(null);
            return;
        }
        if (finished) {
            if (--finishTicks <= 0) mc.displayGuiScreen(null);
            return;
        }
        if (catchFlash > 0) catchFlash--;
        if (fightingId != 0) {
            FishingNet.input(hook, Mouse.getDX() * 800F / mc.displayWidth, -Mouse.getDY() * 450F / mc.displayHeight, false, true, 0);
        } else {
            readMouse();
            FishingNet.input(hook, lureX, lureY, false, false, pendingScroll);
        }
        pendingScroll = 0;
    }

    @Override public void handleMouseInput() {
        if (depthWinch && fightingId == 0 && !finished)
            pendingScroll = FishingGame.clamp(pendingScroll - Mouse.getEventDWheel() / 120F, -6, 6);
        super.handleMouseInput();
    }

    private void readMouse() {
        lureX = FishingGame.clamp(Mouse.getX() * 800F / mc.displayWidth, 22, 778);
        lureY = FishingGame.clamp((mc.displayHeight - Mouse.getY()) * 450F / mc.displayHeight, 28, 428);
    }

    @Override protected void keyTyped(char character, int key) {
        if (key == Keyboard.KEY_ESCAPE || key == mc.gameSettings.keyBindInventory.keyCode) mc.displayGuiScreen(null);
    }

    @Override public boolean doesGuiPauseGame() { return false; }

    @Override public void onGuiClosed() {
        com.itlesports.nightmaremode.client.FishingClient.dismiss(hook);
        if (!closed && !finished) FishingNet.input(hook, 0, 0, true, fightingId != 0, 0);
        closed = true;
        if (fightingId != 0) Mouse.setGrabbed(false);
        if (invisibleCursor != null) {
            try { Mouse.setNativeCursor(previousCursor); } catch (org.lwjgl.LWJGLException ignored) {}
            invisibleCursor.destroy();
            invisibleCursor = null;
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long now = System.nanoTime();
        float dt = Math.min(0.05F, (now - lastFrame) / 1_000_000_000F);
        lastFrame = now;
        time += dt;
        depth += (serverDepth - depth) * Math.min(1, dt * (fightingId == 0 ? 6 : 16));
        if (!finished && fightingId == 0) readMouse();
        if (fightingId != 0) {
            lureX += (serverLureX - lureX) * Math.min(1, dt * 25);
            lureY += (serverLureY - lureY) * Math.min(1, dt * 25);
        }
        ropeAccumulator += dt;
        while (ropeAccumulator >= 1F / 120) { rope.step(lureX, lureY, time); ropeAccumulator -= 1F / 120; }
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GL11.glScalef(width / 800F, height / 450F, 1);
        drawWater();
        for (Visual visual : fish.values()) {
            FishingGame.Target t = visual.target;
            float blend = 1 - (float)Math.exp(-dt * 22);
            visual.x += (t.x - visual.x) * blend;
            visual.y += (t.y - visual.y) * blend;
            visual.angle += FishingGame.wrap(t.angle - visual.angle) * blend;
            visual.progress += (t.progress - visual.progress) * blend;
            drawCatch(t.spec, visual.x, visual.y, visual.angle, t.fright, t.id);
            if (visual.progress > 0.001F && fightingId != t.id) {
                float y = visual.y - t.spec.radius - 12;
                drawRect((int)visual.x - 22, (int)y, (int)visual.x + 22, (int)y + 4, 0xB3102228);
                drawRect((int)visual.x - 21, (int)y + 1, (int)(visual.x - 21 + 42 * visual.progress), (int)y + 3,
                        t.spec.rare ? 0xFFFFD87C : 0xFF9EE6CE);
            }
        }
        drawRope();
        drawLure();
        GL11.glPopMatrix();
        drawHud();
        GL11.glPopAttrib();
    }

    private void drawWater() {
        float darkness = Math.min(0.42F, depth / 100);
        int top = lava ? rgb(88, 32, 24, darkness) : rgb(25, 93, 109, darkness);
        int bottom = lava ? rgb(29, 12, 23, darkness) : rgb(5, 25, 48, darkness);
        drawGradientRect(0, 0, 800, 450, top, bottom);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_TRIANGLES);
        for (int i = 0; i < 6; i++) {
            float x = i * 175 - 80 + (float)Math.sin(time * 0.12 + i) * 35;
            GL11.glColor4f(0.65F, lava ? 0.3F : 0.88F, lava ? 0.13F : 0.85F, 0.04F);
            GL11.glVertex2f(x, 0); GL11.glVertex2f(x + 54, 0);
            GL11.glColor4f(0.4F, 0.8F, 0.8F, 0);
            GL11.glVertex2f(x + 170, 450);
        }
        GL11.glEnd();
        // silhouettes have no collision, progress, or connection to the actual catch population.
        for (int i = 0; i < 10; i++) {
            float x = (i * 137 + time * (i % 2 == 0 ? 9 : -7)) % 950;
            if (x < 0) x += 950;
            float y = (i * 73 - depth * 9 * (0.3F + i % 3 * 0.12F)) % 530;
            if (y < 0) y += 530;
            silhouette(x - 70, y - 40, 8 + i % 4 * 3, i % 2 == 0);
        }
        // persistent kelp columns continue beneath the viewport at every depth.
        GL11.glLineWidth(2.5F);
        for (int i = 0; i < 25; i++) {
            float base = i * 167 % 850 - 25;
            float tip = 15 + i * 131 % 350 - depth * 7;
            float alpha = base < 140 || base > 660 ? 0.65F : 0.18F;
            GL11.glColor4f(lava ? 0.34F : 0.08F, lava ? 0.15F : 0.34F, lava ? 0.12F : 0.25F, alpha);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (float y = Math.max(-40, tip); y <= 520; y += 7)
                GL11.glVertex2f(kelpX(base, y, i), y);
            GL11.glEnd();
            float firstLeaf = (float)Math.ceil((Math.max(-40, tip) + depth * 7) / 23) * 23 - depth * 7;
            for (float y = firstLeaf; y < 510; y += 23) {
                float x = kelpX(base, y, i);
                int leaf = (int)((y + depth * 7) / 23);
                float side = (leaf & 1) == 0 ? 1 : -1;
                float length = 12 + Math.abs((leaf * 31 + i * 17) % 12);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(x, y); GL11.glVertex2f(x + side * 5, y - 13);
                GL11.glVertex2f(x + side * length, y - 20); GL11.glVertex2f(x + side * (length - 3), y - 7);
                GL11.glEnd();
            }
        }
        GL11.glPointSize(2);
        GL11.glColor4f(0.65F, lava ? 0.4F : 0.87F, lava ? 0.13F : 0.9F, 0.25F);
        GL11.glBegin(GL11.GL_POINTS);
        for (int i = 0; i < 95; i++) {
            float y = (i * 61 - time * (4 + i % 5) - depth * 7) % 460;
            if (y < 0) y += 460;
            GL11.glVertex2f((i * 113 % 800) + (float)Math.sin(time * 0.25 + i) * 6, y);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void silhouette(float x, float y, float size, boolean right) {
        GL11.glPushMatrix(); GL11.glTranslatef(x, y, 0); GL11.glScalef(right ? 1 : -1, 1, 1);
        GL11.glColor4f(0.025F, 0.1F, 0.15F, 0.28F);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(0, 0);
        for (int i = 0; i <= 20; i++) {
            float a = i / 20F * (float)Math.PI * 2;
            GL11.glVertex2f((float)Math.cos(a) * size, (float)Math.sin(a) * size * 0.38F);
        }
        GL11.glEnd();
        float flutter = (float)Math.sin(time * 4 + size) * 2;
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(-size * 0.75F, 0); GL11.glVertex2f(-size * 1.5F, -size * 0.4F + flutter);
        GL11.glVertex2f(-size * 1.5F, size * 0.4F + flutter);
        GL11.glEnd(); GL11.glPopMatrix();
    }

    private float kelpX(float base, float y, int index) {
        return base + (float)Math.sin((y + depth * 7) * 0.024 + time * 0.65 + index) * 11;
    }

    private void drawCatch(FishingGame.Spec spec, float x, float y, float angle, float fear, int id) {
        if (spec.kraken) {
            com.itlesports.nightmaremode.rendering.FishingKrakenRenderer.draw(x, y, angle, fear, time, spec.radius,
                    fightingId == id ? fightAnimationTicks() : -1);
            return;
        }
        GL11.glEnable(GL11.GL_BLEND);
        Icon icon = null;
        if (spec.itemId < 0) {
            mc.renderEngine.bindTexture(new ResourceLocation("nightmare:textures/gui/fishing/"
                    + (spec.itemId == FishingCreatures.BLUE_SQUID ? "blue_squid.png" : "giant_squid.png")));
        } else {
            Item item = Item.itemsList[spec.itemId];
            icon = item.getIconFromDamage(0);
            if (icon == null) return;
            mc.renderEngine.bindTexture(item.getSpriteNumber() == 0 ? BLOCK_ATLAS : ITEM_ATLAS);
        }
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPushMatrix(); GL11.glTranslatef(x, y, 0);
        GL11.glRotatef(spec.fish ? angle * 180F / (float)Math.PI : (float)Math.sin(time + id) * 8, 0, 0, 1);
        float scale = spec.radius / 11.5F;
        GL11.glScalef(scale, scale, 1);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        for (int u = 0; u < 16; u++) for (int v = 0; v < 16; v++) {
            vertex(tess, icon, spec, u, v + 1, fear, id);
            vertex(tess, icon, spec, u + 1, v + 1, fear, id);
            vertex(tess, icon, spec, u + 1, v, fear, id);
            vertex(tess, icon, spec, u, v, fear, id);
        }
        tess.draw(); GL11.glPopMatrix();
    }

    private void vertex(Tessellator tess, Icon icon, FishingGame.Spec spec, int u, int v, float fear, int id) {
        float x = u - 8, y = v - 8;
        if (spec.animation != FishingCreatures.FISH) {
            x = 8 - v;
            y = u - 8;
            float pulse = (float)Math.sin(time * (spec.animation == FishingCreatures.JELLYFISH ? 4 : 7) + id);
            if (v < 9) y *= 0.88F + pulse * 0.12F;
            else y += (float)Math.sin(time * (6 + fear * 8) + v * 0.8 + u * 0.4 + id) * (v - 8) * 0.25F;
        } else if (spec.fish) {
            fishPose.vertex(spec, u, v, time, fear, id);
            x = fishPose.x; y = fishPose.y;
        }
        tess.addVertexWithUV(x, y, 0, icon == null ? u / 16F : icon.getInterpolatedU(u), icon == null ? v / 16F : icon.getInterpolatedV(v));
    }

    private void drawRope() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1.3F);
        GL11.glColor4f(0.74F, 0.87F, 0.81F, 0.72F);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i < FishingLine.SEGMENTS; i++) for (int j = 0; j < 4; j++) {
            float t = j / 4F;
            int a = Math.max(0, i - 1), b = i, c = i + 1, d = Math.min(FishingLine.SEGMENTS, i + 2);
            GL11.glVertex2f(curve(rope.x[a], rope.x[b], rope.x[c], rope.x[d], t), curve(rope.y[a], rope.y[b], rope.y[c], rope.y[d], t));
        }
        GL11.glVertex2f(lureX, lureY); GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void drawLure() {
        int x = Math.round(lureX), y = Math.round(lureY);
        drawRect(x - 6, y - 9, x + 7, y + 8, 0xFF142129);
        drawRect(x - 4, y - 8, x + 5, y + 6, 0xFFE8C178);
        drawRect(x - 3, y - 5, x + 4, y + 3, 0xFF865B38);
        drawRect(x - 6, y - 8, x + 7, y - 6, 0xFFF7E4AC);
        drawRect(x - 6, y + 4, x + 7, y + 6, 0xFFC9D9D5);
        GL11.glEnable(GL11.GL_BLEND); GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(2.5F); GL11.glColor4f(0.86F, 0.94F, 0.91F, 1);
        float hookWidth = lureRadius / FishingGame.LURE_RADIUS;
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y + 6); GL11.glVertex2f(x, y + 16);
        GL11.glVertex2f(x + 4 * hookWidth, y + 17 + 2 * hookWidth);
        GL11.glVertex2f(x + 8 * hookWidth, y + 16); GL11.glVertex2f(x + 8 * hookWidth, y + 12);
        GL11.glEnd();
        if (fightingId != 0) {
            Visual hooked = fish.get(fightingId);
            boolean giant = hooked != null && hooked.target.spec.kraken;
            int phase = FishingStruggle.phase(fightAnimationTicks());
            int color = !giant || phase == FishingStruggle.WINDUP ? 0xFFE8C178
                    : phase == FishingStruggle.SURGE ? 0xFFE27A63 : 0xFF94D6BF;
            int left = Math.round(400 - FishingStruggle.gateHalfWidth(giant));
            int right = Math.round(400 + FishingStruggle.gateHalfWidth(giant));
            int bottom = Math.round(FishingStruggle.SURFACE_Y + FishingStruggle.GATE_HEIGHT);
            GL11.glColor4f((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F, 0.9F);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex2f(left, 25); GL11.glVertex2f(left, bottom); GL11.glVertex2f(right, bottom); GL11.glVertex2f(right, 25);
            GL11.glEnd();
            if (hooked != null) {
                drawRect(left, bottom + 5, right, bottom + 8, 0xA0142129);
                drawRect(left, bottom + 5, left + Math.round((right - left) * hooked.target.progress), bottom + 8, color);
            }
            if (giant) {
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                String key = phase == FishingStruggle.WINDUP ? "fishing.kraken.coiling"
                        : phase == FishingStruggle.SURGE ? "fishing.kraken.surge" : "fishing.kraken.recovery";
                fontRenderer.drawStringWithShadow(I18n.getString(key), left, 12, color);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
            }
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private float fightAnimationTicks() {
        return serverFightAge + Math.min(3, (System.currentTimeMillis() - lastSnapshot) / 50F);
    }

    private void drawHud() {
        drawGradientRect(0, 0, width, 38, 0x8004121C, 0x0004121C);
        String depthText = String.format(java.util.Locale.ROOT, "%.1f / %.0f m", depth, depthLimit);
        fontRenderer.drawStringWithShadow(depthText, width - 16 - fontRenderer.getStringWidth(depthText), 14, 0xA2C5C9);
        int color = stock < 0.25F ? 0xE86E59 : stock < 0.55F ? 0xD3AF67 : 0x82C6AB;
        for (int i = 0; i < 12; i++) {
            drawRect(16 + i * 6, height - 18, 20 + i * 6, height - 15, 0xB01A232C);
            float fraction = FishingGame.clamp(stock * 12 - i, 0, 1);
            if (fraction > 0) drawRect(16 + i * 6, height - 18, 20 + i * 6, height - 15, ((int)(fraction * 255) << 24) | color);
        }
        if ((finished || catchFlash > 0) && caughtItem != 0)
            drawCenteredString(fontRenderer, I18n.getString("fishing.caught") + " " + catchName(caughtItem), width / 2, height / 2 - 35, 0xFFE3A3);
    }

    private String catchName(int item) {
        return item < 0 ? I18n.getString(item == FishingCreatures.BLUE_SQUID ? "fishing.blue_squid.name" : "entity.NmGiantSquid.name")
                : new ItemStack(Item.itemsList[item]).getDisplayName();
    }

    private static int rgb(int r, int g, int b, float darkness) {
        float factor = 1 - darkness;
        return 0xFF000000 | (int)(r * factor) << 16 | (int)(g * factor) << 8 | (int)(b * factor);
    }

    private static float curve(float a, float b, float c, float d, float t) {
        return 0.5F * (2 * b + (-a + c) * t + (2 * a - 5 * b + 4 * c - d) * t * t + (-a + 3 * b - 3 * c + d) * t * t * t);
    }

    private static final class Visual {
        private FishingGame.Target target;
        private float x, y, angle, progress;
        private Visual(FishingGame.Target t) { target = t; x = t.x; y = t.y; angle = t.angle; progress = t.progress; }
    }
}
