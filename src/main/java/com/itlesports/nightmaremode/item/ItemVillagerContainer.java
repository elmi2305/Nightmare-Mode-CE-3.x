package com.itlesports.nightmaremode.item;

import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;

import java.util.List;

import static net.minecraft.src.EntityVillager.*;

public class ItemVillagerContainer extends NMItemBlock {

    public ItemVillagerContainer(int id) {
        super(id);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
        int meta = stack.getItemDamage();
        list.add(I18n.getString("villager.profession") + ": " + I18n.getString("entity.addonVillager" + this.getTextureForProfession(NMUtils.VillagerMetaCodec.getProfession(meta)) + ".name"));
        list.add(I18n.getString("villager.level") + ": "  + NMUtils.VillagerMetaCodec.getLevel(meta));
        super.addInformation(stack,player,list,adv);
    }


    private String getTextureForProfession(int profession){
        if(profession == PROFESSION_ID_FARMER){
            return "Farmer";
        } else if(profession == PROFESSION_ID_LIBRARIAN){
            return "Librarian";
        } else if(profession == PROFESSION_ID_BLACKSMITH){
            return "Blacksmith";
        } else if(profession == PROFESSION_ID_BUTCHER){
            return "Butcher";
        } else if(profession == PROFESSION_ID_PRIEST){
            return "Priest";
        } else if(profession == 5){
            return "Nightmare";
        }
        return null;
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public boolean onItemUsedByBlockDispenser(ItemStack stack, World world, int i, int j, int k, int facing) {
        int meta = stack.getItemDamage();

        int profession = NMUtils.VillagerMetaCodec.getProfession(meta);
        int level = NMUtils.VillagerMetaCodec.getLevel(meta);
//        int trades = NMUtils.VillagerDataCodec.getTradesCompleted(meta);

        int x = i + Facing.offsetsXForSide[facing];
        int y = j + Facing.offsetsYForSide[facing];
        int z = k + Facing.offsetsZForSide[facing];

        EntityVillager villager = EntityVillager.createVillagerFromProfession(world,profession);
        villager.setTradeLevel(level);

        villager.setLocationAndAngles(x + 0.5, y, z + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
        villager.onSpawnWithEgg((EntityLivingData)null);

        if (!world.isRemote) {
            world.spawnEntityInWorld(villager);
        }

        stack.stackSize--;
        return true;
    }


}
