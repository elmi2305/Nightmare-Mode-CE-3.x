package com.itlesports.nightmaremode.util;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

public class NMEvents {

    public enum SimpleEvent {
        SLIME_RAIN("nmSlimeRain", ConditionType.DAY_ENDS_WITH, 33, 1, NMFields.HARDMODE, EnumChatFormatting.GREEN) {
            @Override
            protected void doCustomEventLogic(World w) {
                w.setRainStrength(1.0f);
                w.getWorldInfo().setRaining(true);
                w.getWorldInfo().setRainTime(12000);
                if (w.getWorldInfo().isThundering()) {
                    w.getWorldInfo().setThundering(false);
                }
            }
        },

        JELLY_SLIMES(ConditionType.DAY_ENDS_WITH, 76, 2, NMFields.PREHARDMODE) {
            @Override
            protected void doCustomEventLogic(World w) {}
        },

        GREAT_HARVEST("nmGreatHarvest", ConditionType.DAY_MULTIPLE, 44, 3, NMFields.HARDMODE, EnumChatFormatting.GOLD) {
            @Override
            protected void doCustomEventLogic(World w) {
                if (w.isRaining()) {
                    w.getWorldInfo().setRaining(false);
                }
            }
        },
        HELL("nmHell", ConditionType.NIGHT_MULTIPLE, 100, 4, NMFields.HARDMODE, EnumChatFormatting.RED) {
            @Override
            protected void doCustomEventLogic(World w) {
                if (w.isRaining()) {
                    w.getWorldInfo().setRaining(false);
                }
            }
        },
        SPIDER_RAIN("nmSpiderRain", ConditionType.NIGHT_ENDS_WITH, 42, 5, NMFields.HARDMODE, EnumChatFormatting.GRAY) {
            @Override
            protected void doCustomEventLogic(World w) {
                if (!w.isRaining()) {
                    w.getWorldInfo().setRaining(true);
                    w.getWorldInfo().setRainTime(12000);
                }
            }
        };

        private final String string;
        private final ConditionType condition;
        private final int constant;
        private boolean wasChosen;
        public final int id;
        public final int worldStateRequirement;
        public final EnumChatFormatting color;

        SimpleEvent(String name1, ConditionType conditionType, int constant, int id,
                    int worldStateRequirement, EnumChatFormatting color) {
            this.string = name1;
            this.condition = conditionType;
            this.constant = constant;
            this.id = id;
            this.worldStateRequirement = worldStateRequirement;
            this.color = color;
        }

        SimpleEvent(String name1, ConditionType conditionType, int constant, int id,
                    int worldStateRequirement) {
            this(name1, conditionType, constant, id, worldStateRequirement, EnumChatFormatting.WHITE);
        }

        SimpleEvent(ConditionType conditionType, int constant, int id,
                    int worldStateRequirement, EnumChatFormatting color) {
            this(null, conditionType, constant, id, worldStateRequirement, color);
        }

        SimpleEvent(ConditionType conditionType, int constant, int id,
                    int worldStateRequirement) {
            this(null, conditionType, constant, id, worldStateRequirement, EnumChatFormatting.WHITE);
        }

        public boolean isActive() {
            return NightmareMode.getInstance().activeEventsInt == this.id;
        }

        public enum ConditionType {
            DAY_MULTIPLE {
                @Override
                protected boolean checkActivation(World w, int constant) {
                    return (w.isDaytime() || NMUtils.getIsDayFromWorldTime(w))
                            && NMUtils.getDayCountFromWorld(w) % constant == 0;
                }
            },
            DAY_ENDS_WITH {
                @Override
                protected boolean checkActivation(World w, int constant) {
                    // first day check goes off of the skylight...
                    String dayCount = Integer.toString(NMUtils.getDayCountFromWorld(w));
                    return (w.isDaytime() || NMUtils.getIsDayFromWorldTime(w))
                            && dayCount.endsWith(Integer.toString(constant));
                }
            },
            NIGHT_MULTIPLE {
                @Override
                protected boolean checkActivation(World w, int constant) {
                    return !(w.isDaytime() || NMUtils.getIsDayFromWorldTime(w))
                            && NMUtils.getDayCountFromWorld(w) % constant == 0;
                }
            },
            NIGHT_ENDS_WITH {
                @Override
                protected boolean checkActivation(World w, int constant) {
                    String dayCount = Integer.toString(NMUtils.getDayCountFromWorld(w));
                    return !(w.isDaytime() || NMUtils.getIsDayFromWorldTime(w))
                            && dayCount.endsWith(Integer.toString(constant));
                }
            };

            protected abstract boolean checkActivation(World w, int constant);
        }

        public boolean shouldActivate(World w) {
            return condition.checkActivation(w, this.constant);
        }

        protected abstract void doCustomEventLogic(World w);

        public void displayEventAndStart(World w){
            if(this.wasChosen) return;
            // this helps it only run initialization logic once!
            this.doCustomEventLogic(w);
            if(this.string == null) return; // event does not want to be displayed in chat

            ChatMessageComponent text = new ChatMessageComponent();
            ChatMessageComponent text1 = new ChatMessageComponent();
            text.addText(I18n.getString("nm.events." + this.string));
            text.setColor(this.color);
            text.setBold(true);

            text1.addText(I18n.getString("nm.events." + this.string + ".info"));
            text1.setColor(EnumChatFormatting.WHITE);
            for(Object p : w.playerEntities){

                ((EntityPlayer)p).sendChatToPlayer(text);
                ((EntityPlayer)p).sendChatToPlayer(text1);
            }
        }
    }

    public static void onServerTick(WorldServer w){
        SimpleEvent chosen = null;

        for (SimpleEvent event : SimpleEvent.values()) {
            if (chosen == null && event.condition.checkActivation(w, event.constant) && NMUtils.getWorldProgress() >= event.worldStateRequirement) {
                chosen = event;
                event.displayEventAndStart(w);
                event.wasChosen = true;
            } else {
                event.wasChosen = false;
            }
        }

        // these packets get sent to players in ONLY the overworld, as events only run on that world server
        NightmareMode.sendEventsPacketToAll(chosen != null ? chosen.id : 0);
    }
    public static boolean noEventsActive(){
        return NightmareMode.getInstance().activeEventsInt == 0;
    }


//    public static boolean isEventActiveServer(SimpleEvent event){
//        return event.isActive;
//    }
//    public static boolean isEventActiveClient(SimpleEvent event){
//        return NightmareMode.getInstance().activeEventsInt == event.id;
//    }
//    public static boolean isActive(SimpleEvent event){
//        // unified for both server and client,
//        return NightmareMode.getInstance().activeEventsInt == event.id;
//    }
}
