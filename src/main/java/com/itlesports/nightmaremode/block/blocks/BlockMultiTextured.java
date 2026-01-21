package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BlockMultiTextured extends NMBlock {
    private Icon iconBottom;
    private Icon iconTop;
    private Icon iconSouth;
    private Icon iconNorth;
    private Icon iconWest;
    private Icon iconEast;

    private String iconBottomName;
    private String iconTopName;
    private String iconSouthName;
    private String iconNorthName;
    private String iconWestName;
    private String iconEastName;
    private boolean canGrowVegetation;

    public BlockMultiTextured(int par1, Material par2Material, String bot, String top, String s, String n, String w, String e) {
        super(par1, par2Material);
        this.iconBottomName = bot;
        this.iconTopName = top;
        this.iconSouthName = s;
        this.iconNorthName = n;
        this.iconWestName = w;
        this.iconEastName = e;

        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public BlockMultiTextured(int id, Material material, String allSides) {
        this(id, material, allSides, allSides, allSides, allSides, allSides, allSides);
    }
    public BlockMultiTextured(int id, Material material, String allSides, boolean canGrowVegetation) {
        this(id, material, allSides);
        this.canGrowVegetation = canGrowVegetation;
    }


    public BlockMultiTextured(int id, Material material, String top, String bot, String sides) {
        this(id, material, bot, top, sides, sides, sides, sides);
    }
    public BlockMultiTextured(int id, Material material, String top, String bot, String side, boolean canGrowVegetation) {
        this(id, material, bot, top, side);
        this.canGrowVegetation = canGrowVegetation;
    }



    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.iconBottom = register.registerIcon(this.iconBottomName);
        this.iconTop = register.registerIcon(this.iconTopName);
        this.iconSouth = register.registerIcon(this.iconSouthName);
        this.iconNorth = register.registerIcon(this.iconNorthName);
        this.iconWest = register.registerIcon(this.iconWestName);
        this.iconEast = register.registerIcon(this.iconEastName);
    }


    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        return switch (side) {
            case 0 -> iconBottom;
            case 1 -> iconTop;
            case 2 -> iconNorth;
            case 3 -> iconSouth;
            case 4 -> iconWest;
            case 5 -> iconEast;
            default -> blockIcon;
        };
    }
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int par2) {
        return switch (side) {
            case 0 -> iconBottom;
            case 1 -> iconTop;
            case 2 -> iconNorth;
            case 3 -> iconSouth;
            case 4 -> iconWest;
            case 5 -> iconEast;
            default -> blockIcon;
        };
    }

    public BlockMultiTextured setGrowsVegetation(boolean b){
        this.canGrowVegetation = b;
        return this;
    }

    public boolean canReedsGrowOnBlock(World world, int x, int y, int z) {
        return this.canGrowVegetation;
    }

    public boolean canSaplingsGrowOnBlock(World world, int x, int y, int z) {
        return this.canGrowVegetation;
    }

    public boolean canWildVegetationGrowOnBlock(World world, int x, int y, int z) {
        return this.canGrowVegetation;
    }

}
