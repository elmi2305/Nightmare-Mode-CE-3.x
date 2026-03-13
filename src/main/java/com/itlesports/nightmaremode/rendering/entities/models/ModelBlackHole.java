package com.itlesports.nightmaremode.rendering.entities.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

@Environment(value= EnvType.CLIENT)
public class ModelBlackHole extends ModelBase {
    private final ModelRenderer sphere;

    public ModelBlackHole() {
        this.sphere = new ModelRenderer(this, "sphere");
        int textureMultiplier = 4;
        this.sphere.setTextureOffset(0, 0).addBox(-4.0F * textureMultiplier, -4.0F * textureMultiplier, -4.0F * textureMultiplier, 8 * textureMultiplier, 8 * textureMultiplier, 8 * textureMultiplier);

    }

    public void render(float scale) {
        this.sphere.render(scale);
    }
}