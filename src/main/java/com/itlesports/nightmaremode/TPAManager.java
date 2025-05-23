package com.itlesports.nightmaremode;

import net.minecraft.src.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TPAManager {
    private static class TpaRequest {
        UUID sender;
        long timestamp; // System.currentTimeMillis()

        TpaRequest(UUID sender) {
            this.sender = sender;
            this.timestamp = System.currentTimeMillis();
        }
    }

    // target UUID -> request (sender + timestamp)
    private static final Map<UUID, TpaRequest> tpaRequests = new HashMap<>();

    public static void sendRequest(EntityPlayerMP sender, EntityPlayerMP target) {
        tpaRequests.put(target.getUniqueID(), new TpaRequest(sender.getUniqueID()));
    }

    public static UUID getRequestSender(UUID targetId) {
        TpaRequest request = tpaRequests.get(targetId);
        if (request == null) return null;
        if (System.currentTimeMillis() - request.timestamp > 60000) { // expired after 60s
            tpaRequests.remove(targetId);
            return null;
        }
        return request.sender;
    }

    public static void removeRequest(UUID targetId) {
        tpaRequests.remove(targetId);
    }

    public static boolean hasRequest(UUID targetId) {
        return getRequestSender(targetId) != null;
    }
}

