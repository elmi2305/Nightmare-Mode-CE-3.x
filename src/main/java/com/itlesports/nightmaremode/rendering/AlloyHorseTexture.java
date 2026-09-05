package com.itlesports.nightmaremode.rendering;

import net.minecraft.src.AbstractTexture;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.ResourceManager;
import net.minecraft.src.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class AlloyHorseTexture extends AbstractTexture {
    private final String[] layers;
    private final int armorColor;

    public AlloyHorseTexture(String[] layers, int armorColor) {
        this.layers = layers.clone();
        this.armorColor = armorColor;
    }

    @Override
    public void loadTexture(ResourceManager resourceManager) {
        try {
            TextureUtil.uploadTextureImage(getGlTextureId(), composeLayers(resourceManager));
        } catch (IOException exception) {
            Minecraft.getMinecraft().getLogAgent().logWarningException("failed to load alloy horse texture", exception);
            TextureUtil.uploadTextureImage(getGlTextureId(), new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        }
    }

    private BufferedImage composeLayers(ResourceManager resourceManager) throws IOException {
        BufferedImage composite = null;
        for (int layer = 0; layer < layers.length; layer++) {
            if (layers[layer] == null) continue;
            BufferedImage image;
            try (InputStream stream = resourceManager.getResource(new ResourceLocation(layers[layer])).getInputStream()) {
                image = ImageIO.read(stream);
            }
            if (image == null) throw new IOException("invalid horse texture: " + layers[layer]);
            if (layer == 2) {
                // tint only the armor, preserving its shading and alpha.
                BufferedImage tinted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int pixel = image.getRGB(x, y);
                        int red = ((pixel >> 16) & 255) * ((armorColor >> 16) & 255) / 255;
                        int green = ((pixel >> 8) & 255) * ((armorColor >> 8) & 255) / 255;
                        int blue = (pixel & 255) * (armorColor & 255) / 255;
                        tinted.setRGB(x, y, (pixel & 0xFF000000) | (red << 16) | (green << 8) | blue);
                    }
                }
                image = tinted;
            }
            if (composite == null) {
                composite = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            }
            Graphics2D graphics = composite.createGraphics();
            try {
                graphics.drawImage(image, 0, 0, null);
            } finally {
                graphics.dispose();
            }
        }
        if (composite == null) throw new IOException("missing horse texture layers");
        return composite;
    }
}
