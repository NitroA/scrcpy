package com.genymobile.scrcpy.wrappers;

import android.os.IInterface;

import com.genymobile.scrcpy.DisplayInfo;
import com.genymobile.scrcpy.Size;

public final class DisplayManager {
    private final IInterface manager;

    public DisplayManager(IInterface manager) {
        this.manager = manager;
    }

    public DisplayInfo getDisplayInfo(int displayId) {
        try {
            Object displayInfo = manager.getClass().getMethod("getDisplayInfo", int.class).invoke(manager, displayId);
            if (displayInfo == null) throw new IllegalArgumentException(suggestFixMessage(displayId, getDisplayIds()));
            Class<?> cls = displayInfo.getClass();
            // width and height already take the rotation into account
            int width = cls.getDeclaredField("logicalWidth").getInt(displayInfo);
            int height = cls.getDeclaredField("logicalHeight").getInt(displayInfo);
            int rotation = cls.getDeclaredField("rotation").getInt(displayInfo);
            int layerStack = cls.getDeclaredField("layerStack").getInt(displayInfo);
            int flags = cls.getDeclaredField("flags").getInt(displayInfo);
            return new DisplayInfo(displayId, new Size(width, height), rotation, layerStack, flags);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public int[] getDisplayIds() {
        try {
            return (int[]) manager.getClass().getMethod("getDisplayIds").invoke(manager);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private String suggestFixMessage(int displayId, int[] displayIds) {
        if (displayIds == null || displayIds.length == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append("Failed to get displayId=[")
                .append(displayId)
                .append("]\nTry to use one of these available display ids:\n");
        for (int id : displayIds) {
            sb.append("scrcpy --display ").append(id).append("\n");
        }
        return sb.toString();
    }
}
