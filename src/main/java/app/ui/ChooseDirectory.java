package app.ui;

import app.menu.Application;
import app.menu.BrowseDirectoriesAction;

import javax.swing.*;
import java.util.function.Consumer;

public class ChooseDirectory extends JFrame {
    private JPanel panel1;
    private JComboBox<String> oComboBox;
    private JButton oBrowseButton;
    private JButton oInitializeButton;
    private JButton extractImgButton;

    public ChooseDirectory() {
        setContentPane(panel1);
        setTitle("Choose Directory...");
        setSize(474, 124);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefault();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setDefault() {
        String sLastOpenedDirectory = Application.OnGetLastOpenDirectory();

        if (sLastOpenedDirectory != null) {
            oComboBox.addItem(sLastOpenedDirectory);
        }

        Consumer<String> oOpenFolderAction = (sDirectory) -> {
            Application.OnSaveLastOpenDirectory(sDirectory);
            oComboBox.addItem(sDirectory);
            oComboBox.setSelectedItem(sDirectory);
        };

        BrowseDirectoriesAction oBrowseDirectoriesAction = new BrowseDirectoriesAction(oOpenFolderAction);
        oBrowseButton.addActionListener(oBrowseDirectoriesAction);

        oInitializeButton.addActionListener(e -> {
            String sDirectory = (String) oComboBox.getSelectedItem();
            if (sDirectory != null && !sDirectory.isEmpty()) {
                new SimpleImgEditor(sDirectory);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a valid directory.",
                        "Invalid Directory",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                     InstantiationException | IllegalAccessException e) {
                System.out.println(e.getMessage());
            }
            new ChooseDirectory();


            // new SimpleImgEditor();
        });
    }
}
