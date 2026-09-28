package com.itlesports.nightmaremode.block.blocks.templates;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

/**
 * A block that uses metadata (0–15) to represent up to 16 distinct variants,
 * conserving block ID space. Each variant defines its own textures, hardness,
 * resistance, and behavioral flags.
 *
 *
 * <h3>Forcing a separate block ID</h3>
 * If a variant truly needs its own ID (e.g. it must be visible in creative while
 * its siblings are hidden, or it has a fundamentally different material), it can
 * be registered as a standalone {@code BlockMultiTextured}
 *
 * <h3>Explosion resistance caveat</h3>
 * The classic {@code getExplosionResistance(Entity)} overload has no world-coordinate
 * parameter, so the block-level {@code blockResistance} field is set to the minimum
 * resistance across all variants as a conservative fallback. If the runtime provides
 * the 5-parameter overload {@code getExplosionResistance(Entity, World, int, int, int)},
 * that override returns the exact per-variant value.
 */
public class BlockMetaMultiTextured extends NMBlock {

    public static class Variant {

        final String[] textures;
        final float hardness;
        final float resistance;
        final boolean canGrowVegetation;
        final boolean isExplosive;
        final String unlocalizedName;

        private Variant(Builder b) {
            this.textures          = b.textures.clone();
            this.hardness          = b.hardness;
            this.resistance        = b.resistance;
            this.canGrowVegetation = b.canGrowVegetation;
            this.isExplosive       = b.isExplosive;
            this.unlocalizedName   = b.unlocalizedName;
        }


        /** All six faces share one texture. */
        public static Builder allSides(String tex) {
            return new Builder(tex, tex, tex, tex, tex, tex);
        }

        /**
         * Unique top and bottom texture; other faces the same
         */
        public static Builder topBotSides(String top, String bot, String sides) {
            return new Builder(bot, top, sides, sides, sides, sides);
        }

        /**
         * Each face has its own texture
         */
        public static Builder custom(String bot, String top, String south, String north, String west, String east) {
            return new Builder(bot, top, south, north, west, east);
        }



        public static class Builder {

            private final String[] textures = new String[6];
            private float   hardness          = Float.NaN;
            private float   resistance        = Float.NaN;
            private boolean canGrowVegetation = false;
            private boolean isExplosive       = false;
            private String  unlocalizedName   = "";

            private Builder(String bot, String top, String south, String north, String west, String east) {
                textures[0] = bot;
                textures[1] = top;
                textures[2] = north;
                textures[3] = south;
                textures[4] = west;
                textures[5] = east;
            }

            public Builder hardness(float h)   { this.hardness   = h; return this; }
            public Builder resistance(float r) { this.resistance = r; return this; }
            public Builder growsVegetation()   { this.canGrowVegetation = true; return this; }
            public Builder explosive()         { this.isExplosive = true; return this; }
            public Builder name(String n)      { this.unlocalizedName = n; return this; }

            public Variant build() { return new Variant(this); }
        }
    }


    private final Variant[] variants;

    @Environment(EnvType.CLIENT)
    private Icon[][] icons;


    public BlockMetaMultiTextured(int id, Material material, Variant... variants) {
        super(id, material);
        if (variants == null || variants.length == 0)
            throw new IllegalArgumentException("BlockMetaMultiTextured requires at least one Variant (block id " + id + ")");
        if (variants.length > 16)
            throw new IllegalArgumentException("BlockMetaMultiTextured supports at most 16 variants (block id " + id + ")");

        this.variants = variants;
        this.setCreativeTab(CreativeTabs.tabBlock);

        // ----------------------------------------------------------------
        // BLOCK HARDNESS SHENANIGANS
        //
        // getBlockHardness(World,x,y,z) is the method used for player digging.
        // The fields below are fallbacks used in other places (e.g. the single-argument explosion check).
        //
        // I use the minimum value across all variants so the block is never accidentally harder than intended
        // The meta overrides then enforce exact values during actual gameplay
        // ----------------------------------------------------------------

        float minH = Float.NaN;
        float minR = Float.NaN;
        for (Variant v : this.variants) {
            if (!Float.isNaN(v.hardness)   && (Float.isNaN(minH) || v.hardness   < minH)) minH = v.hardness;
            if (!Float.isNaN(v.resistance) && (Float.isNaN(minR) || v.resistance < minR)) minR = v.resistance;
        }
        if (!Float.isNaN(minH)) this.setHardness(minH);
        if (!Float.isNaN(minR)) this.setResistance(minR);
    }

    // textures
    @Environment(EnvType.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        icons = new Icon[variants.length][6];
        for (int meta = 0; meta < variants.length; meta++) {
            for (int side = 0; side < 6; side++) {
                icons[meta][side] = register.registerIcon(variants[meta].textures[side]);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private Icon safeIcon(int meta, int side) {
        if (icons == null || meta < 0 || meta >= icons.length) return blockIcon;
        if (side < 0 || side >= 6) return blockIcon;
        return icons[meta][side];
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        return safeIcon(world.getBlockMetadata(x, y, z), side);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Icon getIcon(int side, int meta) {
        return safeIcon(meta, side);
    }


    // metadata hardness and resistance
    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= 0 && meta < variants.length) {
            float h = variants[meta].hardness;
            if (!Float.isNaN(h)) return h;
        }
        return blockHardness;
    }

    /**
     * Returns the exact per-variant resistance when world coordinates are available.
     * Vanilla divides the returned value by 5 internally before comparing.
     */
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= 0 && meta < variants.length) {
            float r = variants[meta].resistance;
            if (!Float.isNaN(r)) return r / 5.0f;
        }
        return super.getExplosionResistance(entity);
    }

    // metadata behaviour

    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        int meta = world.getBlockMetadata(x, y, z);
        if (!world.isRemote && meta >= 0 && meta < variants.length && variants[meta].isExplosive) {
            EntitySilverfish sf = new EntitySilverfish(world);
            sf.setPosition(x, y, z);
            world.spawnEntityInWorld(sf);
        }
        super.onBlockDestroyedByExplosion(world, x, y, z, explosion);
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
        if (meta >= 0 && meta < variants.length && variants[meta].isExplosive) {
            world.createExplosion(null, x, y, z, 2.75f, true);
        }
        super.onBlockDestroyedByPlayer(world, x, y, z, meta);
    }

    @Override
    public boolean canReedsGrowOnBlock(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return meta >= 0 && meta < variants.length && variants[meta].canGrowVegetation;
    }

    @Override
    public boolean canSaplingsGrowOnBlock(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return meta >= 0 && meta < variants.length && variants[meta].canGrowVegetation;
    }

    @Override
    public boolean canWildVegetationGrowOnBlock(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return meta >= 0 && meta < variants.length && variants[meta].canGrowVegetation;
    }

    // drop and metadata preservation

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    // localization

    public String getUnlocalizedName(int meta) {
        if (meta >= 0 && meta < variants.length) {
            String n = variants[meta].unlocalizedName;
            if (n != null && !n.isEmpty()) return "tile." + n;
        }
        return getUnlocalizedName();
    }

    /** Returns the first variant's name as a no-meta fallback. */
    @Override
    public String getUnlocalizedName() {
        if (variants.length > 0) {
            String n = variants[0].unlocalizedName;
            if (n != null && !n.isEmpty()) return "tile." + n;
        }
        return super.getUnlocalizedName();
    }

    // utility

    /** Number of registered variants (= highest valid metadata value + 1). */
    public int variantCount() { return variants.length; }
}