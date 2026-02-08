package app;

import app.menu.ExitAppAction;
import app.menu.OpenFileAction;
import app.menu.OpenFolderAction;
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

        // var oOpenFileItem = new JMenuItem("Open File");
        // var oOpenFolderItem = new JMenuItem("Open Folder");
        var oSaveItem = new JMenuItem("Save");
        var oExitItem = new JMenuItem("Exit");

        /*oOpenFileItem.addActionListener(new OpenFileAction(
                oFileHelp::OnLoadFromJson
        ));*/

        /*oOpenFolderItem.addActionListener(new OpenFolderAction(
                oFileHelp::OnLoadMultipleFromJson
        ));*/

        var oSaveFileAction = new SaveFileAction();
        oSaveItem.addActionListener(oSaveFileAction);

        var oExitAppAction = new ExitAppAction();
        oExitItem.addActionListener(oExitAppAction);

        // oFileMenu.add(oOpenFileItem);
        // oFileMenu.add(oOpenFolderItem);
        oFileMenu.add(SEPARATOR);
        oFileMenu.add(oSaveItem);
        oFileMenu.add(oExitItem);
    }
}
