package com.dentalclinicsystem.config;

import javax.swing.JFrame;

public class FrameManager {

    private static JFrame frame;

    public static JFrame getFrame() {

        return frame;
    }

    public static void setFrame(
            JFrame frame) {

        FrameManager.frame = frame;
    }
}
