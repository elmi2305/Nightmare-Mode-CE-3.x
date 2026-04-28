package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityVillagerContainer extends TileEntity {

    private int profession;
    private int level;

    public int getProfession() {
        return profession;
    }

    public void setProfession(int profession) {
        this.profession = profession;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("Profession", profession);
        nbt.setInteger("Level", level);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        profession = nbt.getInteger("Profession");
        level = nbt.getInteger("Level");
    }

    public void readFromItemMeta(int meta) {
        this.profession = NMUtils.VillagerMetaCodec.getProfession(meta);
        this.level = NMUtils.VillagerMetaCodec.getLevel(meta);
    }
}