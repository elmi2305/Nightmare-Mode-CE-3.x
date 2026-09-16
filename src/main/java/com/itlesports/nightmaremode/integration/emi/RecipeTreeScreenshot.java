package com.itlesports.nightmaremode.integration.emi;

import net.minecraft.src.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class RecipeTreeScreenshot {
    private RecipeTreeScreenshot() {}

    public static File save(int left, int top, int width, int height, Runnable render) throws IOException {
        Minecraft mc = Minecraft.getMinecraft();
        // tiles stay within the actual framebuffer, regardless of the full tree size.
        int tileWidth = Math.min(1024, mc.displayWidth);
        int tileHeight = Math.min(1024, mc.displayHeight);
        if (width <= 0 || height <= 0 || tileWidth <= 0 || tileHeight <= 0
                || (long) width * height > 64L * 1024 * 1024) {
            throw new IOException("Recipe tree is too large to capture at full resolution");
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteBuffer pixels = BufferUtils.createByteBuffer(tileWidth * tileHeight * 4);
        int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            GL11.glReadBuffer(GL11.GL_BACK);
            GL11.glDrawBuffer(GL11.GL_BACK);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, 0);
            GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, 0);
            GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, 0);
            for (int y = 0; y < height; y += tileHeight) {
                for (int x = 0; x < width; x += tileWidth) {
                    int w = Math.min(tileWidth, width - x);
                    int h = Math.min(tileHeight, height - y);
                    GL11.glViewport(0, 0, w, h);
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                    GL11.glColorMask(true, true, true, true);
                    GL11.glDepthMask(true);
                    GL11.glClearColor(0.08f, 0.08f, 0.08f, 1);
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                    GL11.glMatrixMode(GL11.GL_PROJECTION);
                    GL11.glLoadIdentity();
                    GL11.glOrtho(left + x, left + x + w, top + y + h, top + y, 1000, 3000);
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glLoadIdentity();
                    GL11.glTranslatef(0, 0, -2000);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glColor4f(1, 1, 1, 1);
                    render.run();
                    pixels.clear();
                    GL11.glReadPixels(0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
                    for (int row = 0; row < h; row++) {
                        for (int col = 0; col < w; col++) {
                            int i = (row * w + col) * 4;
                            image.setRGB(x + col, y + h - 1 - row,
                                    (pixels.get(i) & 255) << 16 | (pixels.get(i + 1) & 255) << 8 | pixels.get(i + 2) & 255);
                        }
                    }
                }
            }
        } finally {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(matrixMode);
            GL11.glPopClientAttrib();
            GL11.glPopAttrib();
        }
        File directory = new File(mc.mcDataDir, "screenshots");
        if (!directory.isDirectory() && !directory.mkdirs()) {
            throw new IOException("Could not create screenshots directory");
        }
        String name = "recipe-tree-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
        File file = new File(directory, name + ".png");
        int suffix = 2;
        while (!file.createNewFile()) {
            file = new File(directory, name + "_" + suffix++ + ".png");
        }
        if (!ImageIO.write(image, "png", file)) {
            throw new IOException("PNG encoder unavailable");
        }
        return file;
    }
}
