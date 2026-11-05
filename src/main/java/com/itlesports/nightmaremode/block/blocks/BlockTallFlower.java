package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

import static com.itlesports.nightmaremode.util.NMFields.FLOWER_NAMES;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.block.NMBlocks;

public class BlockTallFlower extends BlockFlower {
    private final boolean blightFruitBush;
    public static final int RIPE_BLIGHT_FRUIT = 3;
    @Environment(EnvType.CLIENT)
    private Icon bareBlightBushIcon;
    @Environment(EnvType.CLIENT)
    private Icon ripeBlightBushIcon;
    @Environment(EnvType.CLIENT)
    private Icon[] bottomIcons;
    @Environment(EnvType.CLIENT)
    private Icon[] topIcons;

    private static final int TYPE_MASK = 0x7;
    private static final int TOP_FLAG = 0x8;

    public static int ROSE_BUSH = 0;
    public static int PEONY = 1;
    public static int LILAC = 2;
    public static int SUNFLOWER = 3;
    public static int BERRY_BUSH = 4;
    public static int LAVA_FLOWER = 5;
    public static int VOID_SHRUB = 6;
    public static int LUCID_BLOOM = 7;

    public BlockTallFlower(int blockID) {
        this(blockID, false);
    }

    public BlockTallFlower(int blockID, boolean blightFruitBush) {
        super(blockID, Material.plants);
        this.blightFruitBush = blightFruitBush;
        this.setHardness(0.0F);
        this.setStepSound(soundGrassFootstep);
        this.setUnlocalizedName("tallFlower");
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.initBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 1.0F, 0.9F);
        this.setTickRandomly(true);
        if (blightFruitBush) {
            this.setUnlocalizedName("nmBlightFruitBush");
            this.setTextureName("nightmare:nmBlightFruitBush");
            this.initBlockBounds(0.15F, 0.0F, 0.15F, 0.85F, 0.7F, 0.85F);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (blightFruitBush) return world.isAirBlock(x, y, z) && canBlockStay(world, x, y, z);
        return super.canPlaceBlockAt(world, x, y, z) && world.isAirBlock(x, y + 1, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        if (!blightFruitBush) return super.canBlockStay(world, x, y, z);
        if (y < 1 || y >= world.getHeight()) return false;
        int soil = world.getBlockId(x, y - 1, z);
        return soil == NMBlocks.underGrass.blockID || soil == NMBlocks.underFlowerDirts.blockID;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (!blightFruitBush) return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
        if (world.getBlockMetadata(x, y, z) != RIPE_BLIGHT_FRUIT) return false;
        if (!world.isRemote) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.blightFruit));
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "dig.grass", 0.7F, 1.0F);
        }
        return true;
    }

    @Override
    protected boolean canSilkHarvest(int metadata) {
        return !blightFruitBush && super.canSilkHarvest(metadata);
    }

    @Override
    public int getMobilityFlag() {
        return blightFruitBush ? 2 : super.getMobilityFlag();
    }

    @Override
    public boolean canBeGrazedOn(IBlockAccess world, int x, int y, int z, EntityAnimal animal) {
        return !blightFruitBush && super.canBeGrazedOn(world, x, y, z, animal);
    }

    @Override
    public boolean canBlockStayDuringGenerate(World world, int i, int j, int k) {
        if (blightFruitBush) return canPlaceBlockAt(world, i, j, k);
        // force return true to get floating flowers
        if(world.isAirBlock(i, j + 1, k) && !world.isAirBlock(i, j - 1, k)){
            if(world.getBlockMaterial(i,j - 1,k) == Material.ground){
//                System.out.println(Block.blocksList[world.getBlockId(i,j-1,k)].getLocalizedName());
                return true;
            }
            if(world.getBlockMetadata(i,j,k) == VOID_SHRUB){
                return world.getBlockMaterial(i,j - 1,k) == Material.rock;
            }
        }
        return false;

    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        if (blightFruitBush) return;
        int type = stack.getItemDamage() & TYPE_MASK;
        world.setBlock(x, y, z, this.blockID, type, 2);  // Bottom half

        if (type != LAVA_FLOWER && world.isAirBlock(x, y + 1, z)) {
            world.setBlock(x, y + 1, z, this.blockID, type | TOP_FLAG, 2);  // Top half
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockID, int meta) {
        if (blightFruitBush) {
            super.breakBlock(world, x, y, z, blockID, meta);
            return;
        }
        boolean isTop = (meta & TOP_FLAG) != 0;

        if (isTop) {
            // Break bottom if exists
            if (world.getBlockId(x, y - 1, z) == this.blockID) {
                world.setBlockToAir(x, y - 1, z);
            }
        } else {
            // Break top if exists
            if (world.getBlockId(x, y + 1, z) == this.blockID) {
                world.setBlockToAir(x, y + 1, z);
            }
        }

        super.breakBlock(world, x, y, z, blockID, meta);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
        if (blightFruitBush) {
            if (!world.isRemote) checkFlowerChange(world, x, y, z);
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & TOP_FLAG) != 0) {  // Is top half
            int belowID = world.getBlockId(x, y - 1, z);
            if (belowID != this.blockID || (world.getBlockMetadata(x, y - 1, z) & TOP_FLAG) != 0) {
                world.setBlockToAir(x, y, z);
            }
        }
    }

    @Override
    public int damageDropped(int meta) {
        if (blightFruitBush) return 0;
        return (meta & TOP_FLAG) != 0 ? 0 : (meta & TYPE_MASK);  // Drop type only from bottom
    }

    @Override
    public int idDropped(int meta, Random rand, int fortune) {
        if (blightFruitBush) return meta == RIPE_BLIGHT_FRUIT ? NMItems.blightFruit.itemID : 0;
        return (meta & TOP_FLAG) != 0 ? 0 : this.blockID;  // Drop the block itself (as item)
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (blightFruitBush) {
            if (world.isRemote) return;
            checkFlowerChange(world, x, y, z);
            if (world.getBlockId(x, y, z) != blockID) return;
            int growth = world.getBlockMetadata(x, y, z);
            if (growth < RIPE_BLIGHT_FRUIT && random.nextInt(6) == 0) {
                world.setBlockMetadataWithNotify(x, y, z, growth + 1, 2);
            }
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & TOP_FLAG) != 0 || (meta & TYPE_MASK) != LUCID_BLOOM || !world.isAirBlock(x, y + 1, z)) return;
        long time = world.getWorldTime() % 24000L;
        if (time >= 11500L && time <= 13500L && random.nextInt(3) == 0) {
            world.setBlock(x, y + 1, z, this.blockID, LUCID_BLOOM | TOP_FLAG, 3);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (blightFruitBush) {
            super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, fortune);
            return;
        }
        if ((meta & TOP_FLAG) != 0 || (meta & TYPE_MASK) != LUCID_BLOOM) {
            super.dropBlockAsItemWithChance(world, x, y, z, meta, chance, fortune);
            return;
        }
        if (world.isRemote) return;
        this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.lucidBloomSeeds));
        long time = world.getWorldTime() % 24000L;
        boolean mature = world.getBlockId(x, y + 1, z) == this.blockID
                && (world.getBlockMetadata(x, y + 1, z) & TYPE_MASK) == LUCID_BLOOM;
        if (mature && time >= 12000L && time <= 14000L) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.lucidPetal, 1 + world.rand.nextInt(2)));
            if (world.rand.nextInt(3) == 0) {
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.lucidBloomSeeds));
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        if (blightFruitBush) return meta == RIPE_BLIGHT_FRUIT ? ripeBlightBushIcon : bareBlightBushIcon;
        int type = meta & TYPE_MASK;
        boolean isTop = (meta & TOP_FLAG) != 0;

        if (isTop) {
            return topIcons[type];
        } else {
            return bottomIcons[type];
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister reg) {
        if (blightFruitBush) {
            bareBlightBushIcon = reg.registerIcon("nightmare:nmBlightFruitBush");
            ripeBlightBushIcon = reg.registerIcon("nightmare:nmBlightFruitBush_ripe");
            this.blockIcon = bareBlightBushIcon;
            return;
        }
        bottomIcons = new Icon[8];
        topIcons = new Icon[8];

        for (int i = 0; i < FLOWER_NAMES.length; i++) {
            bottomIcons[i] = reg.registerIcon("nightmare:nmTallFlower_" + FLOWER_NAMES[i] + "_bottom");
            topIcons[i] = reg.registerIcon("nightmare:nmTallFlower_" + FLOWER_NAMES[i] + "_top");
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs tab, List list) {
        if (blightFruitBush) {
            list.add(new ItemStack(blockID, 1, 0));
            list.add(new ItemStack(blockID, 1, RIPE_BLIGHT_FRUIT));
            return;
        }
        for (int i = 0; i < 8; i++) {
            list.add(new ItemStack(blockID, 1, i));
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (blightFruitBush) {
            super.onEntityCollidedWithBlock(world, x, y, z, entity);
            return;
        }
        int meta =  world.getBlockMetadata(x, y, z);
        if (entity instanceof EntityLivingBase elb && (meta == VOID_SHRUB || meta == LAVA_FLOWER)) {
            elb.attackEntityFrom(DamageSource.generic, 1.0f);
        }
        super.onEntityCollidedWithBlock(world, x, y, z, entity);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 1;  // Crossed squares (same as BlockFlower)
    }
}
