package com.sdms;

import com.sdms.ui.login.LoginFrame;
import com.sdms.utils.UITheme;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UITheme.applyGlobalTheme();
            new LoginFrame().setVisible(true);
        });
    }
}
