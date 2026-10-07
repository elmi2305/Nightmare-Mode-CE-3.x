package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import btw.block.blocks.UnfiredPotteryBlock;
import com.itlesports.nightmaremode.block.tileEntities.UnfiredNetherBrickTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFluid;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.ITileEntityProvider;
import net.minecraft.src.Icon;
import net.minecraft.src.Material;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(UnfiredPotteryBlock.class)
public abstract class UnfiredPotteryBlockMixin extends Block implements ITileEntityProvider {
    protected UnfiredPotteryBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new UnfiredNetherBrickTileEntity();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockID, int metadata) {
        super.breakBlock(world, x, y, z, blockID, metadata);
        world.removeBlockTileEntity(x, y, z);
    }

    @Override
    public boolean getPreventsFluidFlow(World world, int x, int y, int z, Block fluid) {
        int metadata = world.getBlockMetadata(x, y, z);
        if ((metadata == 7 || metadata == 8) && fluid.blockMaterial == Material.lava) return false;
        return super.getPreventsFluidFlow(world, x, y, z, fluid);
    }

    @Override
    public void onFluidFlowIntoBlock(World world, int x, int y, int z, BlockFluid fluid) {
        int metadata = world.getBlockMetadata(x, y, z);
        if ((metadata == 7 || metadata == 8) && fluid.blockMaterial == Material.lava) {
            world.setBlockToAir(x, y, z);
            return;
        }
        super.onFluidFlowIntoBlock(world, x, y, z, fluid);
    }

    @Override
    public void onCookedByKiLn(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == 7 || metadata == 8) {
            world.setBlock(x, y, z, NMBlocks.placedNetherBrick.blockID, metadata == 8 ? 1 : 0, 3);
            return;
        }
        super.onCookedByKiLn(world, x, y, z);
    }

    @Inject(method = "renderBlockSecondPass", at = @At("TAIL"))
    @Environment(EnvType.CLIENT)
    private void renderLavaCookingOverlay(RenderBlocks renderer, int x, int y, int z,
                                          boolean firstPassResult, CallbackInfo ci) {
        if (!firstPassResult || renderer.hasOverrideBlockTexture()) {
            return;
        }
        TileEntity tile = renderer.blockAccess.getBlockTileEntity(x, y, z);
        if (!(tile instanceof UnfiredNetherBrickTileEntity brick) || !brick.isCooking()) {
            return;
        }
        int level = brick.getCookLevel();
        Icon[] icons = ((KilnBlockAccessor)(Object)BTWBlocks.kiln).nightmareMode$getCookIcons();
        if (level > 0 && level <= icons.length) {
            this.renderBlockWithTexture(renderer, x, y, z, icons[level - 1]);
        }
    }
}
