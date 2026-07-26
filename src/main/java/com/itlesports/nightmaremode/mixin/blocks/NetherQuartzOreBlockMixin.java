package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.NetherQuartzOreBlock;
import api.item.util.ItemUtils;
import api.util.MiscUtils;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemSoulFlint;
import com.itlesports.nightmaremode.item.items.ItemTungstenPickaxe;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetherQuartzOreBlock.class)
public abstract class NetherQuartzOreBlockMixin extends BlockOre {
    protected NetherQuartzOreBlockMixin(int id) {
        super(id);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemSoulFlint) {
            return player.getCurrentPlayerStrVsBlock(this, x, y, z) / this.blockHardness / 30.0F;
        }
//        if (held != null && held.getItem() instanceof ItemTungstenPickaxe) {
//            return player.getCurrentPlayerStrVsBlock(this, x, y, z) / this.blockHardness / 10.0F;
//        }
        return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || !(held.getItem() instanceof ItemSoulFlint)) {
            super.harvestBlock(world, player, x, y, z, metadata);
            return;
        }
        if (!world.isRemote) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                    new ItemStack(NMItems.quartzDust),
                    MiscUtils.convertOrientationToFlatBlockFacingReversed(player));
            held.damageItem(1, player);
            world.setBlock(x, y, z, Block.netherrack.blockID, 0, 3);
            player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        }
    }
}
