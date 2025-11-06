package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityVillagerContainer extends TileEntity {

    private int profession;
    private int level;

    public void setProfession(int profession) { this.profession = profession; }
    public void setLevel(int level) { this.level = level; }

    public int getProfession() { return profession; }
    public int getLevel() { return level; }

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

    public int writeToItemMeta() {
        return NMUtils.VillagerMetaCodec.packMeta(profession, level);
    }
}
