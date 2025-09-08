package com.itlesports.nightmaremode.item.items;

import btw.item.items.ToolItem;
import net.minecraft.src.*;

public class ItemIronKnittingNeedles extends ToolItem {

    public ItemIronKnittingNeedles(int iItemID) {
        super(iItemID, 0, EnumToolMaterial.IRON);
        this.setMaxStackSize(1);
        this.setMaxDamage(64);
        this.setDamageVsEntity(1);
        this.setBuoyant();
        this.setFilterableProperties(4);
        this.setUnlocalizedName("nmNeedles");
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public String getModId() {
        return "nightmare_mode";
    }

    @Override
    public boolean isToolTypeEfficientVsBlockType(Block var1) {
        return false;
    }
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase defendingEntity, EntityLivingBase attackingEntity) {
        return true;
    }

    @Override
    public boolean isDamagedInCrafting() {
        return true;
    }

    @Override
    public void onBrokenInCrafting(EntityPlayer player) {
        player.worldObj.playSoundAtEntity(player, "random.break", 0.5f, player.rand.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    public boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k) {
        return false;
    }
}
