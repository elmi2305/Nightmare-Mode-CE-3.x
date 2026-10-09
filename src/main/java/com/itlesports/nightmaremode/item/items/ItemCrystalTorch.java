package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.CrystalTorchBlock;
import com.itlesports.nightmaremode.block.blocks.SubmergedCrystalTorchBlock;
import com.itlesports.nightmaremode.block.tileEntities.CrystalTorchTileEntity;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.*;

public class ItemCrystalTorch extends NMItem {
    public ItemCrystalTorch(int id) {
        super(id);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("ifhyCrystalTorch");
        this.setTextureName("nightmare:ifhyCrystalTorch");
    }

    @Override public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                                       int side, float hitX, float hitY, float hitZ) {
        x += Facing.offsetsXForSide[side];
        y += Facing.offsetsYForSide[side];
        z += Facing.offsetsZForSide[side];
        int target = world.getBlockId(x, y, z);
        boolean water = target == Block.waterMoving.blockID || target == Block.waterStill.blockID;
        if (stack.stackSize <= 0 || (!water && !world.isAirBlock(x, y, z))
                || !player.canPlayerEdit(x, y, z, side, stack)) return false;
        int orientation = NMBlocks.crystalTorch.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, 0) & 7;
        if (!CrystalTorchBlock.isSupported(world, x, y, z, orientation)) return false;
        if (world.isRemote) return true;
        boolean placed = water
                ? SubmergedCrystalTorchBlock.place(world, x, y, z, world.getBlockMetadata(x, y, z), orientation)
                : world.setBlock(x, y, z, NMBlocks.crystalTorch.blockID, orientation, 3);
        if (placed) {
            TileEntity tile = world.getBlockTileEntity(x, y, z);
            if (tile instanceof CrystalTorchTileEntity torch && stack.hasTagCompound()
                    && stack.getTagCompound().hasKey("CrystalBurnTicks")) {
                torch.setRemainingBurnTicks(stack.getTagCompound().getInteger("CrystalBurnTicks"));
            }
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "step.wood", 0.5F, 1.0F);
            if (!player.capabilities.isCreativeMode) --stack.stackSize;
        }
        return placed;
    }

}
