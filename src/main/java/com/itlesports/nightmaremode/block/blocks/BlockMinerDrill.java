package com.itlesports.nightmaremode.block.blocks;

import api.item.util.ItemUtils;
import api.util.MiscUtils;
import btw.BTWMod;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.tileEntities.MinerDrillTileEntity;
import com.itlesports.nightmaremode.nmgui.ContainerMinerDrill;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Facing;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

public class BlockMinerDrill extends BlockContainer {
    private final int machineTier;
    private final String drillTexture;
    private Icon frontIcon;
    private Icon backIcon;
    private Icon sideIcon;

    public BlockMinerDrill(int id) {
        this(id, 1, "ifhyMinerDrill", "nightmare:ifhyMinerDrill");
    }

    public BlockMinerDrill(int id, int machineTier, String name, String texture) {
        super(id, BTWBlocks.netherRockMaterial);
        this.machineTier = Math.max(1, machineTier);
        this.drillTexture = texture;
        this.setHardness(6.0F);
        this.setResistance(20.0F);
        this.setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setUnlocalizedName(name);
        this.setTextureName(texture);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int clickedSide,
                             float hitX, float hitY, float hitZ, int metadata) {
        return Block.getOppositeFacing(clickedSide);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new MinerDrillTileEntity(this.machineTier);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int facing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(placer);
        world.setBlockMetadataWithNotify(x, y, z, facing, 3);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.frontIcon = register.registerIcon(this.drillTexture + "Front");
        this.backIcon = register.registerIcon(this.drillTexture + "Back");
        this.sideIcon = register.registerIcon(this.drillTexture + "Side");
        this.blockIcon = this.sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        int facing = metadata & 7;
        if (side == facing) return this.frontIcon;
        if (side == Block.getOppositeFacing(facing)) return this.backIcon;
        return this.sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess access, int x, int y, int z, int side) {
        return this.getIcon(side, access.getBlockMetadata(x, y, z));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof MinerDrillTileEntity drill) {
            BTWMod.serverOpenCustomInterface((EntityPlayerMP)player,
                    new ContainerMinerDrill(player.inventory, drill), ContainerMinerDrill.ID);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int metadata) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof MinerDrillTileEntity drill) {
            ItemStack fuel = drill.getStackInSlot(0);
            if (fuel != null) ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, fuel, 1);
        }
        super.breakBlock(world, x, y, z, blockId, metadata);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (this.isBlockedByAdvancedNode(world, x, y, z)) {
            for (int i = 0; i < 2; ++i) {
                world.spawnParticle("largesmoke", x + 0.25D + random.nextDouble() * 0.5D,
                        y + 0.75D + random.nextDouble() * 0.25D,
                        z + 0.25D + random.nextDouble() * 0.5D, 0.0D, 0.04D, 0.0D);
            }
        }
        if ((world.getBlockMetadata(x, y, z) & 8) != 0) {
            world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "minecart.base",
                    1.0F + random.nextFloat() * 0.1F,
                    0.75F + random.nextFloat() * 0.1F);
        }
    }

    private boolean isBlockedByAdvancedNode(World world, int x, int y, int z) {
        int facing = world.getBlockMetadata(x, y, z) & 7;
        int targetX = x + Facing.offsetsXForSide[facing];
        int targetY = y + Facing.offsetsYForSide[facing];
        int targetZ = z + Facing.offsetsZForSide[facing];
        net.minecraft.src.Block target = net.minecraft.src.Block.blocksList[world.getBlockId(targetX, targetY, targetZ)];
        return target instanceof BlockOreNode && ((BlockOreNode)target).getRequiredDrillTier() > this.machineTier;
    }
}
