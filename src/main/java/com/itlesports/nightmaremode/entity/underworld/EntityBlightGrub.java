package com.itlesports.nightmaremode.entity.underworld;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

// a distinct entity keeps the prototype's save and spawn identity separate from mushroom worms.
public class EntityBlightGrub extends EntityMob {
    public EntityBlightGrub(World world) {
        super(world);
        setSize(1.1F, 0.5F);
        experienceValue = 2;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(12.0);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.23);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0);
    }

    @Override
    protected void attackEntity(Entity target, float distance) {
        if (attackTime <= 0 && distance < 1.5F && canEntityBeSeen(target)
                && target.boundingBox.maxY > boundingBox.minY && target.boundingBox.minY < boundingBox.maxY) {
            attackTime = 20;
            attackEntityAsMob(target);
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(boundingBox.minY);
        int z = MathHelper.floor_double(posZ);
        int soil = worldObj.getBlockId(x, y - 1, z);
        return dimension == NMFields.UNDERWORLD_DIMENSION
                && worldObj.getBiomeGenForCoords(x, z) == BiomeGenUnderworld.blightlands
                && (soil == NMBlocks.underGrass.blockID || soil == NMBlocks.underFlowerDirts.blockID)
                && super.getCanSpawnHere();
    }

    @Override
    protected boolean isValidLightLevel() {
        return worldObj.getBlockLightValueNoSky(MathHelper.floor_double(posX),
                MathHelper.floor_double(boundingBox.minY), MathHelper.floor_double(posZ)) == 0;
    }

    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        return 0.0F;
    }

    @Override
    protected void dropFewItems(boolean killedByPlayer, int looting) {
        if (!worldObj.isRemote) dropItem(NMItems.rawGrubMeat.itemID, 1 + rand.nextInt(2 + Math.max(0, looting)));
    }

    @Override protected String getLivingSound() { return "mob.silverfish.say"; }
    @Override protected String getHurtSound() { return "mob.silverfish.hit"; }
    @Override protected String getDeathSound() { return "mob.silverfish.kill"; }
    @Override protected float getSoundPitch() { return super.getSoundPitch() * 0.7F; }
}
