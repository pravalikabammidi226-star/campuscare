package com.campuscare;

import com.campuscare.controller.AppController;
import com.campuscare.view.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application entry point for CampusCare+ – Smart Campus Complaint & Grievance Management System.
 * Initializes Look-and-Feel, Front Controller, Background SLA Monitoring Daemon, and Main GUI Frame.
 */
public class Main {

    public static void main(String[] args) {
        // Set cross-platform or system Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Default fallback
            }
        }

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            AppController controller = AppController.getInstance();
            MainFrame frame = new MainFrame();
            controller.setMainFrame(frame);

            // Start background escalation monitoring daemon
            controller.startBackgroundTasks();

            frame.setVisible(true);
        });
    }
}
