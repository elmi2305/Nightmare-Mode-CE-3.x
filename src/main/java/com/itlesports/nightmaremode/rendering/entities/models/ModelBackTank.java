package com.itlesports.nightmaremode.rendering.entities.models;

import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelBackTank extends ModelBase {
    private final ModelRenderer tankBody;
    private final ModelRenderer tankTop;
    private final ModelRenderer tankBottom;
    private final ModelRenderer tankValve;
    private final ModelRenderer upperCradle;
    private final ModelRenderer lowerCradle;

    public ModelBackTank() {
        this.textureWidth = 32;
        this.textureHeight = 32;

        this.tankBody = new ModelRenderer(this, 0, 0);
        this.tankBody.addBox(-3.0F, 1.0F, 2.25F, 6, 11, 5, 0.18F);

        this.tankTop = new ModelRenderer(this, 0, 13);
        this.tankTop.addBox(-2.75F, 0.25F, 2.5F, 5, 1, 4, 0.2F);

        this.tankBottom = new ModelRenderer(this, 0, 17);
        this.tankBottom.addBox(-2.75F, 12.25F, 2.5F, 5, 1, 4, 0.2F);

        this.tankValve = new ModelRenderer(this, 8, 13);
        this.tankValve.addBox(-1.25F, -0.8F, 2.9F, 2, 1, 1, 0.1F);
        this.tankValve.addBox(-0.6F, -1.5F, 3.1F, 1, 1, 1);

        this.upperCradle = new ModelRenderer(this, 8, 17);
        this.upperCradle.addBox(-3.5F, 3.25F, 2.0F, 7, 1, 1, 0.05F);

        this.lowerCradle = new ModelRenderer(this, 8, 20);
        this.lowerCradle.addBox(-3.5F, 9.0F, 2.0F, 7, 1, 1, 0.05F);
    }

    public void render(float scale) {
        this.tankBody.render(scale);
        this.tankTop.render(scale);
        this.tankBottom.render(scale);
        this.tankValve.render(scale);
        this.upperCradle.render(scale);
        this.lowerCradle.render(scale);
    }
}
