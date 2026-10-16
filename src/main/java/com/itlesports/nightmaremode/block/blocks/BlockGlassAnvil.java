package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.NightmareModeAddon;
import com.itlesports.nightmaremode.block.blocks.templates.BlockHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class BlockGlassAnvil extends BlockHammerAnvil {
    public BlockGlassAnvil(int id) {
        super(id, Material.glass, Block.soundGlassFootstep, "ifhyGlassAnvil", "nightmare:ifhyGlassAnvil");
        this.setHardness(0.3F);
        this.setResistance(0.3F);
    }

    @Override
    protected TileEntityHammerAnvil createAnvilTileEntity() {
        return new TileEntityHammerAnvil();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        ItemStack held = player.getHeldItem();
        if (held == null) return true;
        ItemStack probe = held.copy();
        probe.stackSize = probe.getMaxStackSize();
        if (HammerCraftingManager.instance.getRecipe(probe) != null) {
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D,
                    NightmareModeAddon.IFHY_BREAKING.sound(), 1.0F, 1.0F);
            world.playAuxSFX(2001, x, y, z, this.blockID | world.getBlockMetadata(x, y, z) << 12);
            world.playAuxSFX(2001, x, y, z, this.blockID | world.getBlockMetadata(x, y, z) << 12);
            world.playAuxSFX(2001, x, y, z, this.blockID | world.getBlockMetadata(x, y, z) << 12);
            world.setBlockToAir(x, y, z);
        }
        return true;
    }
}
