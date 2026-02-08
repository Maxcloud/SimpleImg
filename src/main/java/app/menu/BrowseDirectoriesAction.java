package app.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.function.Consumer;

public class BrowseDirectoriesAction extends AbstractAction {

    private final Consumer<String> oConsumeDirectory;

    public BrowseDirectoriesAction(Consumer<String> oConsumeDirectory) {
        this.oConsumeDirectory = oConsumeDirectory;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser oFolderSelect = new JFileChooser();
        oFolderSelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        String sPreviousOpenedDirectory = Application.OnGetLastBrowseDirectory();
        if (sPreviousOpenedDirectory != null) {
            oFolderSelect.setCurrentDirectory(new File(sPreviousOpenedDirectory));
        }

        int nReturnValue = oFolderSelect.showOpenDialog(null);
        if (nReturnValue == JFileChooser.APPROVE_OPTION) {
            File oSelectedFile = oFolderSelect.getSelectedFile();
            oConsumeDirectory.accept(oSelectedFile.getPath());
        }
    }
}
