package com.brandon3055.draconicevolution.network;

import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created by brandon3055 on 02/05/2024
 */
public class InputSync {

    private static final WeakHashMap<UUID, Boolean> SPRINT_STATE = new WeakHashMap<>();

    public static Boolean getSprintState(UUID uuid) {
        return SPRINT_STATE.getOrDefault(uuid, false);
    }

    public static void setSprintState(UUID uuid, Boolean sprintState) {
        SPRINT_STATE.put(uuid, sprintState);
    }

}
