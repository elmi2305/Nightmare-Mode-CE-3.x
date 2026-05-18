package com.itlesports.nightmaremode.rendering.entities.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.ModelCreeper;
import net.minecraft.src.ModelRenderer;

@Environment(EnvType.CLIENT)
public class ModelDungCreeper extends ModelCreeper {
    public ModelRenderer head2;
    public ModelRenderer head3;

    public ModelDungCreeper(float scale) {
        super(scale);

        // Replace the head model
        this.head = new ModelRenderer(this, 0 ,2);
        this.head.addBox(-4F, -6F, -4F, 8, 6, 8, scale);
        this.head.setRotationPoint(0F, 4F, 0F);

        // Add extra head details
        this.head2 = new ModelRenderer(this, 44, 0);
        this.head2.addBox(-2F, -9F, -2F, 4, 3, 6, scale);
        this.head2.setRotationPoint(0F, 4F, 0F);

        this.head3 = new ModelRenderer(this, 40, 10);
        this.head3.addBox(-3F, -8F, -3F, 6, 2, 6, scale);
        this.head3.setRotationPoint(0F, 4F, 0F);
    }

    @Override
    public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
        super.setRotationAngles(f, g, h, i, j, k, entity);

        // Copy angles from head
        this.head3.rotateAngleY = this.head2.rotateAngleY = this.head.rotateAngleY;
        this.head3.rotateAngleX = this.head2.rotateAngleX = this.head.rotateAngleX;
    }

    @Override
    public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        super.render(entity, f, g, h, i, j, k);
        this.head2.render(k);
        this.head3.render(k);
    }
}
