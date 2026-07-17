package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlockGroundLayer;
import com.itlesports.nightmaremode.crafting.manager.WashingRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.WashingRecipe;
import net.minecraft.src.*;

import java.util.Random;

public class NMCrushedIronLayer extends NMBlockGroundLayer {
    public NMCrushedIronLayer(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Override
    public void updateTick(World w, int x, int y, int z, Random rand) {
        super.updateTick(w, x, y, z, rand);
        int metadata = w.getBlockMetadata(x, y, z);
        WashingRecipe recipe = WashingRecipeManager.instance.getRainRecipe(this, metadata);
        if (recipe != null
                && this.ticksExisted > recipe.getDuration()
                && w.isRainingAtPos(x, y + 1, z)
                && rand.nextInt(recipe.getChanceDivisor()) == 0) {
            w.destroyBlock(x, y, z, false);
            w.setBlockAndMetadataWithNotify(
                    x, y, z, recipe.getOutputBlock().blockID, recipe.getOutputMetadata());
        }
    }

}
