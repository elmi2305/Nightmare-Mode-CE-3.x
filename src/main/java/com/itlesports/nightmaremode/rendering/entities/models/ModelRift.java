package com.itlesports.nightmaremode.rendering.entities.models;

import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelRift extends ModelBase {
    public ModelRenderer mainBody;
    public ModelRift() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        // Main rectangular body (you can adjust these values)
        // Width (X), Height (Y), Depth (Z)
        this.mainBody = new ModelRenderer(this, 0, 0);
        this.mainBody.setRotationPoint(0F, 0F, 0F); // Center at origin

        // Add a rectangular box: offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ
        // Example: 8 wide, 4 tall, 12 deep (a flat-ish rectangular prism)
        this.mainBody.addBox(-4F, -2F, -6F, 8, 4, 12);

        // Optional: You can add more boxes for details if needed
        // this.mainBody.addBox(...);
    }
}
