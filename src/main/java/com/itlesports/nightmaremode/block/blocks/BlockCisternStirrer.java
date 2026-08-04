package com.itlesports.nightmaremode.block.blocks;

import api.block.MechanicalBlock;
import api.block.util.MechPowerUtils;
import com.itlesports.nightmaremode.block.tileEntities.CisternStirrerTileEntity;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/** Nonflammable mechanical stirrer configured by horizontal redstone and gems. */
public class BlockCisternStirrer extends BlockContainer implements MechanicalBlock {
    public BlockCisternStirrer(int id) {
        super(id, Material.iron);
        this.setHardness(4.0F);
        this.setResistance(20.0F);
        this.setPicksEffectiveOn();
        this.setStepSound(soundMetalFootstep);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setUnlocalizedName("ifhyCisternStirrer");
        this.setTextureName("nightmare:ifhyCisternStirrer");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new CisternStirrerTileEntity();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof CisternStirrerTileEntity stirrer) {
            stirrer.refreshConfiguration();
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getBlockTileEntity(x, y, z);
            if (tile instanceof CisternStirrerTileEntity stirrer) {
                stirrer.refreshConfiguration();
                player.sendChatToPlayer(ChatMessageComponent.createFromText(
                        "Cistern Stirrer: target " + stirrer.getTargetStir()
                                + " | mechanical power " + (stirrer.isMechanicallyPowered() ? "on" : "off")
                                + " | cistern " + (stirrer.getTargetCistern() == null ? "disconnected" : "connected")));
            }
        }
        return true;
    }

    @Override public boolean canInputMechanicalPower() { return true; }
    @Override public boolean canOutputMechanicalPower() { return false; }
    @Override public boolean canInputAxlePowerToFacing(World world, int x, int y, int z, int facing) { return facing == 1; }
    @Override public boolean isInputtingMechanicalPower(World world, int x, int y, int z) {
        return MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, 1);
    }
    @Override public boolean isOutputtingMechanicalPower(World world, int x, int y, int z) { return false; }
    @Override public void overpower(World world, int x, int y, int z) {
        this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        world.setBlockWithNotify(x, y, z, 0);
    }
}
