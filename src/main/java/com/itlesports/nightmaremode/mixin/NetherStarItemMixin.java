package com.itlesports.nightmaremode.mixin;

import btw.item.items.NetherStarItem;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BloodBoneBlock;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodBone;
import com.itlesports.nightmaremode.item.items.NMBloodStarItem;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetherStarItem.class)
public class NetherStarItemMixin extends ItemSimpleFoiled {
    public NetherStarItemMixin(int i) {
        super(i);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer p, World w, int x, int y, int z, int iFacing, float hitX, float hitY, float hitZ) {
        if (p != null && !p.canPlayerEdit(x, y, z, iFacing, stack)) {
            return false;
        }
        if (stack.stackSize == 0) {
            return false;
        }
        if(!NMUtils.getIsBloodMoon()) return false;
        NetherStarItem thisObj =  (NetherStarItem)(Object)this;
        if(thisObj instanceof NMBloodStarItem) return false;


        int iTargetBlockID = w.getBlockId(x, y, z);
        Block blockTarget = Block.blocksList[iTargetBlockID];
        if (blockTarget != null && iTargetBlockID == NMBlocks.bloodBonesUpgraded.blockID) {
            TileEntity tileEntity = w.getBlockTileEntity(x, y, z);
            if(tileEntity instanceof TileEntityBloodBone te){
                if(((BloodBoneBlock)(blockTarget)).getNetherStarSide() != iFacing) return false;
                if(te.isActive() && w.isRemote) return false;
                te.cancelRitual(); // resets the fields when the ritual is started. prevents anger state and other stuff from carrying over.
                te.setActive(true);
                w.playSoundEffect(x,y,z, "mob.wither.death", 0.5f, 0.905F);
                if (!w.isRemote) {
                    int damage = stack.getItemDamage() + 1;

                    ItemStack stack0 = new ItemStack(Item.netherStar, 1, damage);
                    te.setItemStack(stack0);
                    System.out.println("Stack: " + stack0.getDisplayName() + " damage: " + damage);
                }
                --stack.stackSize;
                return true;
            }
            return false;
        }
        return false;
    }

}
