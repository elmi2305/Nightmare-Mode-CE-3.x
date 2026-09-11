package com.itlesports.nightmaremode.block.blocks;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.world.PhasePortalData;
import com.itlesports.nightmaremode.world.PhasePortalManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;

public class BlockPhasePortalFrame extends Block {
    private static final int[] COLORS = {0x191919,0x993333,0x667F33,0x664C33,0x334CB2,0x7F3FB2,0x4C7F99,0x999999,
            0x4C4C4C,0xF27FA5,0x7FCC19,0xE5E533,0x6699D8,0xB24CD8,0xD87F33,0xF2F2F2};

    public BlockPhasePortalFrame(int id) {
        super(id, Material.iron);
        this.setHardness(12.0F).setResistance(80.0F).setPicksEffectiveOn().setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabBlock).setUnlocalizedName("ifhyPhasePortalFrame");
        this.setTextureName("nightmare:ifhyEnderCeramicBlank");
    }

    @Override public int damageDropped(int metadata) { return metadata & 15; }
    @Override public int getRenderColor(int metadata) { return COLORS[metadata & 15]; }
    @Override public int colorMultiplier(IBlockAccess world, int x, int y, int z) { return COLORS[world.getBlockMetadata(x,y,z) & 15]; }
    @Override public void getSubBlocks(int id, CreativeTabs tab, List list) {
        for (int color = 0; color < 16; ++color) list.add(new ItemStack(id, 1, color));
    }

    public boolean tryToCreatePortal(World world, int clickedX, int clickedY, int clickedZ, EntityPlayer player) {
        if (world.isRemote) return true;
        int color = world.getBlockMetadata(clickedX, clickedY, clickedZ) & 15;
        for (int axis = 0; axis < 2; ++axis) {
            for (int vertical = -4; vertical <= 1; ++vertical) {
                for (int horizontal = -3; horizontal <= 1; ++horizontal) {
                    int x = clickedX + (axis == 0 ? horizontal : 0);
                    int y = clickedY + vertical;
                    int z = clickedZ + (axis == 1 ? horizontal : 0);
                    if (!this.isFrame(world, x, y, z, axis, color)) continue;
                    PhasePortalData.Endpoint endpoint = new PhasePortalData.Endpoint(color, world.provider.dimensionId, x, y, z, axis);
                    if (!PhasePortalManager.register(world, endpoint)) {
                        this.sendFullMessage(player, world, color);
                        return false;
                    }
                    for (int across = 0; across < 2; ++across) for (int up = 0; up < 3; ++up)
                        world.setBlock(x + (axis == 0 ? across : 0), y + up,
                                z + (axis == 1 ? across : 0), NMBlocks.phasePortal.blockID, color, 2);
                    world.playSoundEffect(clickedX + 0.5D, clickedY + 0.5D, clickedZ + 0.5D, "portal.trigger", 1.0F, 1.0F);
                    return true;
                }
            }
        }
        player.sendChatToPlayer(ChatMessageComponent.createFromText("The phase cell cannot find a complete, single-color 2x3 frame (shaped like a standard Nether portal)."));
        return false;
    }

    private boolean isFrame(World world, int x, int y, int z, int axis, int color) {
        for (int across = -1; across <= 2; ++across) for (int up = -1; up <= 3; ++up) {
            boolean edge = across == -1 || across == 2 || up == -1 || up == 3;
            int px = x + (axis == 0 ? across : 0);
            int pz = z + (axis == 1 ? across : 0);
            if (edge) {
                if (world.getBlockId(px, y + up, pz) != this.blockID || (world.getBlockMetadata(px, y + up, pz) & 15) != color) return false;
            } else {
                int id = world.getBlockId(px, y + up, pz);
                if (id != 0 && id != Block.fire.blockID) return false;
            }
        }
        return true;
    }

    private void sendFullMessage(EntityPlayer player, World world, int color) {
        List<PhasePortalData.Endpoint> endpoints = PhasePortalManager.data(world).getEndpoints(color);
        PhasePortalData.Endpoint endpoint = endpoints.get(0);
        WorldServer endpointWorld = MinecraftServer.getServer().worldServerForDimension(endpoint.dimension);
        endpointWorld.theChunkProviderServer.loadChunk(endpoint.x >> 4, endpoint.z >> 4);
        String biome = endpointWorld.getBiomeGenForCoords(endpoint.x, endpoint.z).biomeName;
        String dimension = endpointWorld.provider.getDimensionName();
        player.sendChatToPlayer(ChatMessageComponent.createFromText("A " + PhasePortalManager.COLOR_NAMES[color]
                + " portal already has two endpoints; one is in " + biome + ", " + dimension + "."));
    }
}
