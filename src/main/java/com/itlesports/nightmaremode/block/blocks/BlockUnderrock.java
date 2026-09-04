package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Material;

public class BlockUnderrock extends BlockTieredUnderworld {
    private Icon[] strataIcons;

    public BlockUnderrock(int id) {
        super(id, Material.rock, UnderworldToolTier.TITANIUM);
        this.setUnlocalizedName("nmUnderworldRock");
        this.setTextureName("nightmare:nmUnderworldRock");
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        this.strataIcons = new Icon[3];
        this.strataIcons[0] = register.registerIcon("nightmare:nmUnderworldRock");
        this.strataIcons[1] = register.registerIcon("nightmare:nmUnderworldRock_strata_1");
        this.strataIcons[2] = register.registerIcon("nightmare:nmUnderworldRock_strata_2");
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Icon getIcon(int side, int metadata) {
        return strataIcons[Math.min(2, metadata & 3)];
    }
}
