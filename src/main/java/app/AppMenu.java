package app;

import app.menu.ExitAppAction;
import app.menu.OpenFileAction;
import app.menu.SaveFileAction;

import javax.swing.*;

public class AppMenu extends JMenuBar {

    private static final JSeparator SEPARATOR = new JSeparator();

    private final FileHelp oFileHelp;

    public AppMenu(FileHelp oFileHelp) {
        this.oFileHelp = oFileHelp;
        ConstructAppMenu();
    }

    public void ConstructAppMenu() {
        var oFileMenu = new JMenu("File");
        var oHelpMenu = new JMenu("Help");

        add(oFileMenu);
        add(oHelpMenu);

        JMenuItem oOpenItem = new JMenuItem("Open");
        JMenuItem oSaveItem = new JMenuItem("Save");
        JMenuItem oExitItem = new JMenuItem("Exit");

        oOpenItem.addActionListener(new OpenFileAction(
                oFileHelp::OnLoadFromJson
        ));

        var oSaveFileAction = new SaveFileAction();
        oSaveItem.addActionListener(oSaveFileAction);

        var oExitAppAction = new ExitAppAction();
        oExitItem.addActionListener(oExitAppAction);

        oFileMenu.add(oOpenItem);
        oFileMenu.add(oSaveItem);
        oFileMenu.add(oExitItem);
    }
}
