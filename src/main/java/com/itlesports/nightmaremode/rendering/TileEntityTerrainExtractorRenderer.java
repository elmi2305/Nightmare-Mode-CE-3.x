package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.block.tileEntities.TerrainExtractorTileEntity;
import com.itlesports.nightmaremode.rendering.models.ModelTerrainExtractor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class TileEntityTerrainExtractorRenderer extends TileEntitySpecialRenderer {
    private static final ResourceLocation[] TEXTURES_SIDE = {
            texture("Potassium", "Side"), texture("Nitrogen", "Side"), texture("Moisture", "Side"),
            texture("Porosity", "Side"), texture("Acidity", "Side")
    };
    private static final ResourceLocation[] TEXTURES_TOP = {
            texture("Potassium", "Top"), texture("Nitrogen", "Top"), texture("Moisture", "Top"),
            texture("Porosity", "Top"), texture("Acidity", "Top")
    };
    private static final ResourceLocation[] TEXTURES_BOTTOM = {
            texture("Potassium", "Bottom"), texture("Nitrogen", "Bottom"), texture("Moisture", "Bottom"),
            texture("Porosity", "Bottom"), texture("Acidity", "Bottom")
    };
    private static final ResourceLocation[] TEXTURES_STUB = {
            texture("Potassium", "Stub"), texture("Nitrogen", "Stub"), texture("Moisture", "Stub"),
            texture("Porosity", "Stub"), texture("Acidity", "Stub")
    };
    private final ModelTerrainExtractor[] models = {
            new ModelTerrainExtractor(),
            new ModelTerrainExtractor(),
            new ModelTerrainExtractor(),
            new ModelTerrainExtractor(),
            new ModelTerrainExtractor()
    };

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        if (!(tileEntity instanceof TerrainExtractorTileEntity extractor)) return;

        int type = extractor.getFieldType();
        ModelTerrainExtractor model = this.models[type];

        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslated(x, y, z);
        GL11.glDisable(GL11.GL_CULL_FACE);
        int brightness = 0xF000F0;

        this.bindTexture(TEXTURES_SIDE[type]);
        model.renderSides(brightness);
        this.bindTexture(TEXTURES_TOP[type]);
        model.renderTop(brightness);
        this.bindTexture(TEXTURES_BOTTOM[type]);
        model.renderBottom(brightness);
        this.bindTexture(TEXTURES_STUB[type]);
        model.renderStubs(brightness);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static ResourceLocation texture(String type, String segment) {
        return new ResourceLocation("nightmare:textures/blocks/ifhyExtractor" + type + segment + ".png");
    }
}
