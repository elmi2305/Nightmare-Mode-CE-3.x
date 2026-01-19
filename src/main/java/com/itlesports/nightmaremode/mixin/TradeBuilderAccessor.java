package com.itlesports.nightmaremode.mixin;

import api.entity.mob.villager.TradeItem;
import api.entity.mob.villager.TradeProvider;
import api.entity.mob.villager.VillagerTrade;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(TradeProvider.TradeBuilder.class)
public interface TradeBuilderAccessor {
    @Accessor(value = "name",remap = false)
    ResourceLocation getName();
    @Accessor(value = "name",remap = false)
    void setName(ResourceLocation name);
    @Accessor(value = "profession",remap = false)
    int getProfession();
    @Accessor(value = "profession",remap = false)
    void setProfession(int prof);

    @Accessor(value = "input",remap = false)
    TradeItem getInput();
    @Accessor(value = "input",remap = false)
    void setInput(TradeItem input);

    @Accessor(value = "secondaryInput",remap = false)
    TradeItem getSecondaryInput();
    @Accessor(value = "secondaryInput",remap = false)
    void setSecondaryInput(TradeItem item);

    @Accessor(value = "output",remap = false)
    TradeItem getOutput();
    @Accessor(value = "output",remap = false)
    void setOutput(TradeItem output);

    @Accessor(value = "weight",remap = false)
    float getWeight();
    @Accessor(value = "weight",remap = false)
    void setWeight(float w);

    @Accessor(value = "isMandatory",remap = false)
    boolean isMandatory();
    @Accessor(value = "isMandatory",remap = false)
    void setMandatory(boolean m);

    // if you want to change itemID/metadata directly:
    @Accessor(value = "itemID",remap = false) int getItemID();
    @Accessor(value = "itemID",remap = false) void setItemID(int id);
    @Accessor(value = "metadata",remap = false) int getMetadata();
    @Accessor(value = "metadata",remap = false) void setMetadata(int meta);

    // tradeVariants if needed
    @Accessor(value = "tradeVariants",remap = false)
    ArrayList<VillagerTrade> getVariants();
    @Accessor(value = "tradeVariants",remap = false)
    void setVariants(ArrayList<VillagerTrade> v);
}
