package btw.community.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.world.biome.BiomeDecoratorBase;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenMinable;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class NightmareMode extends BTWAddon {
    public NightmareMode(){
        super();
    }
    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }
//    @Override
//    public void decorateWorld(BiomeDecoratorBase decorator, World world, Random rand, int x, int y, BiomeGenBase biome) {
//        this.lavaPillowGenThirdStrata = new WorldGenMinable(BTWBlocks.lavaPillow.blockID, 10);
//        for(int var5 = 0; var5 < 30; ++var5) {
//            int var6 = x + rand.nextInt(16) + 8;
//            int var7 = rand.nextInt(20)+4;
//            int var8 = y + rand.nextInt(16) + 8; // this is supposed to be called z
//            this.lavaPillowGenThirdStrata.generate(world, rand, var6, var7, var8);
//        }
//    }
}
