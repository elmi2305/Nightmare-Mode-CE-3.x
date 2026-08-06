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
    @Environment(EnvType.CLIENT)
    private Icon bottomIcon;
    @Environment(EnvType.CLIENT)
    private Icon topIcon;
    @Environment(EnvType.CLIENT)
    private Icon northIcon;
    @Environment(EnvType.CLIENT)
    private Icon southIcon;
//    private Icon westIcon;
//    private Icon eastIcon;

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
        this.blockIcon = this.northIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return switch (side) {
            case 0 -> this.bottomIcon;
            case 1 -> this.topIcon;
            case 2, 5 -> this.northIcon;
            case 3, 4 -> this.southIcon;
            default -> this.blockIcon;
        };
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.displayGUIWorkbench(x, y, z);
        }
        return true;
    }
}
