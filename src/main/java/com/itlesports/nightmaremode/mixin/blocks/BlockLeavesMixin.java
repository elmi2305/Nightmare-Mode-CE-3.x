package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

import static com.itlesports.nightmaremode.util.NMFields.CRIMSON_COLOR;

@Mixin(BlockLeaves.class)
public class BlockLeavesMixin extends BlockLeavesBase {

    public BlockLeavesMixin(int par1, Material par2Material, boolean par3) {
        super(par1, par2Material, par3);
    }

    @Override
    public boolean isBreakableBarricade(World world, int i, int j, int k, boolean advancedBreaker) {
        return false;
    }

    @Inject(method = "idDropped", at= @At("HEAD"),cancellable = true)
    private void allowAppleDrops(int metadata, Random rand, int fortuneModifier, CallbackInfoReturnable<Integer> cir){
        if(rand.nextInt(8) == 0){
            cir.setReturnValue(NMItems.twig.itemID);
            return;
        }
        cir.setReturnValue(NMItems.leaf.itemID);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
        super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, fortune);
        EntityPlayer player = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 8.0D);
        float bonus = player == null ? 0.0F : SkillHandler.getPlayerData(player).twigDropChanceBonus;
        if (!world.isRemote && bonus > 0.0F && world.rand.nextFloat() < bonus) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.twig));
        }
    }

    @Override
    public int tickRate(World w) {
        return 4;
    }
}
