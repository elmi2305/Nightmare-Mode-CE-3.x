package com.itlesports.nightmaremode.mixin.blocks;

import api.item.items.ToolItem;
import btw.block.blocks.NetherBrickBlock;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.StatList;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetherBrickBlock.class)
public abstract class NetherBrickBlockMixin extends Block {
    protected NetherBrickBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null
                && (held.getItem() == NMItems.tungstenPickaxe || held.getItem() == BTWItems.steelPickaxe || (held.getItem() instanceof ToolItem ti && ti.toolMaterial.getHarvestLevel() >= 4))) {
            super.harvestBlock(world, player, x, y, z, metadata);
        } else if (!world.isRemote) {
            player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        }
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player,
                                                 int x, int y, int z, int metadata) {
        if (!world.isRemote) {
            player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        }
    }
}
