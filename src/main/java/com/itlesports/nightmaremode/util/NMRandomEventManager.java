package com.itlesports.nightmaremode.util;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;

import java.util.Arrays;

public class NMRandomEventManager {

    public enum SimpleEvent {
        SLIME_JELLY("nmSlimeRain", ConditionType.DAY_ENDS_WITH, 33, 1);

        private final String name;
        private final ConditionType condition;
        private boolean isActive;
        public final int id;

        SimpleEvent(String name1, ConditionType conditionType, int constant, int id){
            this.name = name1;
            this.condition = conditionType;
            this.condition.setConstantNumber(constant);
            this.id = id;
        }

        public boolean isActive(){
            return this.isActive;
        };
        public enum ConditionType{
            DAY_MULTIPLE() {
                @Override
                protected boolean checkActivation(World w) {
                    return (w.isDaytime() || NMUtils.getIsDayFromWorldTime(w)) && NMUtils.getDayCountFromWorld(w) % this.constantNumber == 0;
                }
            },
            DAY_ENDS_WITH() {
                @Override
                protected boolean checkActivation(World w) {
                    String dayCount = Integer.toString(NMUtils.getDayCountFromWorld(w));
                    // first day check goes off of the skylight, which is set to low during an eclipse and thus the check would fail
                    System.out.println("should be active");
                    return (w.isDaytime() || NMUtils.getIsDayFromWorldTime(w)) && dayCount.endsWith(Integer.toString(this.constantNumber));
                }
            },
            NIGHT_MULTIPLE() {
                @Override
                protected boolean checkActivation(World w) {
                    return !(w.isDaytime() || NMUtils.getIsDayFromWorldTime(w)) && NMUtils.getDayCountFromWorld(w) % this.constantNumber == 0;

                }
            },
            NIGHT_ENDS_WITH() {
                @Override
                protected boolean checkActivation(World w) {
                    String dayCount = Integer.toString(NMUtils.getDayCountFromWorld(w));

                    return !(w.isDaytime() || NMUtils.getIsDayFromWorldTime(w)) && dayCount.endsWith(Integer.toString(this.constantNumber));
                }
            };

            int constantNumber;
            ConditionType(){}
            public void setConstantNumber(int constantNumber){
                this.constantNumber = constantNumber;
            }
            protected abstract boolean checkActivation(World w);
        }
    }

    public static void onServerTick(WorldServer w){
        int chosenEventID = 0;
        for(SimpleEvent event: SimpleEvent.values()){
            event.isActive = event.condition.checkActivation(w);
            chosenEventID = event.id;
            break;
        }
        // these packets get sent to players in every dimension unless redirected earlier
        NightmareMode.sendEventsPacketToAll(chosenEventID);
    }
    public static boolean isEventActiveServer(SimpleEvent event){
        return event.isActive;
    }
    public static boolean isEventActiveClient(SimpleEvent event){
        return NightmareMode.getInstance().activeEventsInt == event.id;
    }
}
