// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.5.2
// Paste this class into your mod and generate all required imports

package com.itlesports.nightmaremode;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class SocksMobsModelGoatPossessed extends ModelBase {
	private final ModelRenderer left_back_leg;
	private final ModelRenderer right_back_leg;
	private final ModelRenderer right_front_leg;
	private final ModelRenderer left_front_leg;
	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer head_r1;
	private final ModelRenderer horns;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;

	public SocksMobsModelGoatPossessed() {
		textureWidth = 64;
		textureHeight = 64;

		left_back_leg = new ModelRenderer(this);
		left_back_leg.setRotationPoint(1.0F, 14.0F, 4.0F);
		this.left_back_leg.setTextureOffset(0, 28).addBox(-0.5F, 4.0F, 0.0F, 3, 6, 3, 0.0F);

		right_back_leg = new ModelRenderer(this);
		right_back_leg.setRotationPoint(-3.0F, 14.0F, 4.0F);
		this.right_back_leg.setTextureOffset(0, 28).addBox(-0.5F, 4.0F, 0.0F, 3, 6, 3, 0.0F);

		right_front_leg = new ModelRenderer(this);
		right_front_leg.setRotationPoint(-3.0F, 14.0F, -6.0F);
		this.right_front_leg.setTextureOffset(0, 24).addBox(-0.5F, 0.0F, 0.0F, 3, 10, 3, 0.0F);

		left_front_leg = new ModelRenderer(this);
		left_front_leg.setRotationPoint(1.0F, 14.0F, -6.0F);
		this.left_front_leg.setTextureOffset(0, 24).addBox(-0.5F, 0.0F, 0.0F, 3, 10, 3, 0.0F);

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.body.setTextureOffset(14, 1).addBox(-4.5F, -17.0F, -7.0F, 9, 11, 16, 0.0F);
		this.body.setTextureOffset(15, 28).addBox(-5.5F, -18.0F, -8.0F, 11, 14, 11, 0.0F);

		head = new ModelRenderer(this);
		head.setRotationPoint(1.0F, 14.0F, 0.0F);
		this.head.setTextureOffset(17, 0).addBox(1.5F, -11.0F, -10.0F, 3, 2, 1, 0.0F);
		this.head.setTextureOffset(17, 0).addBox(-6.5F, -11.0F, -10.0F, 3, 2, 1, 0.0F);
		this.head.setTextureOffset(54, 0).addBox(-1.0F, -3.0F, -14.0F, 0, 7, 5, 0.0F);

		head_r1 = new ModelRenderer(this);
		head_r1.setRotationPoint(-0.5F, -8.0F, -8.0F);
		head.addChild(head_r1);
		setRotation(head_r1, -0.7854F, 0.0F, 0.0F);
		this.head_r1.setTextureOffset(0, 0).addBox(-3.0F, -2.0F, -4.0F, 5, 10, 7, 0.0F);

		horns = new ModelRenderer(this);
		horns.setRotationPoint(0.5F, 15.0F, -9.0F);
		head.addChild(horns);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-4.25F, -28.75F, 1.0F);
		horns.addChild(cube_r1);
		setRotation(cube_r1, 0.7854F, 0.0F, -0.7854F);
		this.cube_r1.setTextureOffset(18, 55).addBox(-2.1F, -4.1F, -2.2F, 3, 6, 3, -0.5F);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-6.25F, -29.75F, 1.0F);
		horns.addChild(cube_r2);
		setRotation(cube_r2, 0.0F, 0.0F, -0.3927F);
		this.cube_r2.setTextureOffset(30, 57).addBox(-0.7929F, -3.5F, -3.45F, 2, 5, 2, -0.5F);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(3.25F, -29.75F, 1.0F);
		horns.addChild(cube_r3);
		setRotation(cube_r3, 0.0F, 0.0F, 0.3927F);
		this.cube_r3.setTextureOffset(30, 57).addBox(-1.2071F, -3.5F, -3.45F, 2, 5, 2, -0.5F);

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(1.25F, -28.75F, 1.0F);
		horns.addChild(cube_r4);
		setRotation(cube_r4, 0.7854F, 0.0F, 0.7854F);
		this.cube_r4.setTextureOffset(18, 55).addBox(-0.9F, -4.1F, -2.2F, 3, 6, 3, -0.5F);

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(1.25F, -28.75F, 1.0F);
		horns.addChild(cube_r5);
		setRotation(cube_r5, 0.0F, 0.0F, 0.3927F);
		this.cube_r5.setTextureOffset(0, 54).addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, -0.5F);

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-4.25F, -28.75F, 1.0F);
		horns.addChild(cube_r6);
		setRotation(cube_r6, 0.0F, 0.0F, -0.3927F);
		this.cube_r6.setTextureOffset(0, 54).addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, -0.5F);
	}
	/**
     		* Sets the models various rotation angles then renders the model.
     		*/
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		left_back_leg.render(f5);
		right_back_leg.render(f5);
		right_front_leg.render(f5);
		left_front_leg.render(f5);
		body.render(f5);
		head.render(f5);
	}

	public void setRotation(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

}