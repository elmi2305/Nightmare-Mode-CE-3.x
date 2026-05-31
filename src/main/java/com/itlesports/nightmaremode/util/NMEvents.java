package com.itlesports.nightmaremode.util;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

public class NMEvents {

    public enum SimpleEvent {
        SLIME_RAIN("nmSlimeRain", ConditionType.DAY_ENDS_WITH, 33, 1, NMFields.HARDMODE) {
            @Override protected void doCustomEventLogic(World w) {
                w.setRainStrength(1.0f);
                w.getWorldInfo().setRaining(true);
                w.getWorldInfo().setRainTime(12000);
                if(w.getWorldInfo().isThundering()){
                    w.getWorldInfo().setThundering(false);
                }
            }};

        private final String string;
        private final ConditionType condition;
        private boolean wasChosen;
        public final int id;
        public final int worldStateRequirement;

        SimpleEvent(String name1, ConditionType conditionType, int constant, int id, int worldStateRequirement){
            this.string = name1;
            this.condition = conditionType;
            this.condition.setConstantNumber(constant);
            this.id = id;
            this.worldStateRequirement = worldStateRequirement;
        }

        public boolean isActive(){
            return NightmareMode.getInstance().activeEventsInt == this.id;
        };
        public enum ConditionType{
            DAY_MULTIPLE() {
                @Override protected boolean checkActivation(World w) {
                    return (w.isDaytime() || NMUtils.getIsDayFromWorldTime(w)) && NMUtils.getDayCountFromWorld(w) % this.constantNumber == 0;
                }},
            DAY_ENDS_WITH() {
                @Override protected boolean checkActivation(World w) {
                    String dayCount = Integer.toString(NMUtils.getDayCountFromWorld(w));
                    // first day check goes off of the skylight, which is set to low during an eclipse and thus the check would fail
                    return (w.isDaytime() || NMUtils.getIsDayFromWorldTime(w)) && dayCount.endsWith(Integer.toString(this.constantNumber));
                }},
            NIGHT_MULTIPLE() {
                @Override protected boolean checkActivation(World w) {
                    return !(w.isDaytime() || NMUtils.getIsDayFromWorldTime(w)) && NMUtils.getDayCountFromWorld(w) % this.constantNumber == 0;

                }},
            NIGHT_ENDS_WITH() {
                @Override protected boolean checkActivation(World w) {
                    String dayCount = Integer.toString(NMUtils.getDayCountFromWorld(w));

                    return !(w.isDaytime() || NMUtils.getIsDayFromWorldTime(w)) && dayCount.endsWith(Integer.toString(this.constantNumber));
                }};

            int constantNumber;

            ConditionType(){}
            public void setConstantNumber(int constantNumber){
                this.constantNumber = constantNumber;
            }
            protected abstract boolean checkActivation(World w);
        }

        protected abstract void doCustomEventLogic(World w);

        public void displayEventAndStart(World w){
            if(this.wasChosen) return;
            // this helps it only run initialization logic once!
            this.doCustomEventLogic(w);

            ChatMessageComponent text = new ChatMessageComponent();
            text.addText(I18n.getString("nm.events." + this.string));
            text.setColor(EnumChatFormatting.WHITE);
            for(Object p : w.playerEntities){

                ((EntityPlayer)p).sendChatToPlayer(text);
            }
        }
    }

    public static void onServerTick(WorldServer w){
        SimpleEvent chosen = null;

        for (SimpleEvent event : SimpleEvent.values()) {
            if (chosen == null && event.condition.checkActivation(w) && NMUtils.getWorldProgress() >= event.worldStateRequirement) {
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
