package com.itlesports.nightmaremode.block.blocks;

import api.world.WorldUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.BlockWorkbench;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.World;

public class BlockNetherWorkbench extends BlockWorkbench {
    private Icon bottomIcon;
    private Icon topIcon;
    private Icon northIcon;
    private Icon southIcon;
    private Icon westIcon;
    private Icon eastIcon;

    public BlockNetherWorkbench(int id) {
        super(id);
        this.setHardness(2.5F);
        this.setResistance(5.0F);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("ifhyNetherWorkbench");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.bottomIcon = register.registerIcon("nightmare:ifhyNetherWorkbenchBottom");
        this.topIcon = register.registerIcon("nightmare:ifhyNetherWorkbenchTop");
        this.northIcon = register.registerIcon("nightmare:ifhyNetherWorkbenchNorth");
        this.southIcon = register.registerIcon("nightmare:ifhyNetherWorkbenchSouth");
        this.westIcon = register.registerIcon("nightmare:ifhyNetherWorkbenchWest");
        this.eastIcon = register.registerIcon("nightmare:ifhyNetherWorkbenchEast");
        this.blockIcon = this.northIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        switch (side) {
            case 0:
                return this.bottomIcon;
            case 1:
                return this.topIcon;
            case 2:
                return this.northIcon;
            case 3:
                return this.southIcon;
            case 4:
                return this.westIcon;
            case 5:
                return this.eastIcon;
            default:
                return this.blockIcon;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.displayGUIWorkbench(x, y, z);
        }
        return true;
    }
}
