package com.itlesports.nightmaremode.util;

import net.minecraft.src.World;
import net.minecraft.src.WorldServer;

public class NMRandomEventManager {

    public enum SimpleEvent {
        SLIME_JELLY("nmSlimeRain", ConditionType.DAY_ENDS_WITH, 33);

        private final String name;
        private final ConditionType condition;
        private boolean isActive;

        SimpleEvent(String name1, ConditionType conditionType, int constant){
            this.name = name1;
            this.condition = conditionType;
            this.condition.setConstantNumber(constant);
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
        for(SimpleEvent event: SimpleEvent.values()){
            event.isActive = event.condition.checkActivation(w);
        }
    }
}
