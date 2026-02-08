package app.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class OpenFolderAction extends AbstractAction {

    private final String sPreviousOpenedDirectory;;
    private final Consumer<Path> oConsumePath;

    public OpenFolderAction(Consumer<Path> oConsumePath) {
        this.oConsumePath = oConsumePath;
        sPreviousOpenedDirectory = Application.OnGetLastBrowseDirectory();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser oFileChoose = new JFileChooser();
        oFileChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (sPreviousOpenedDirectory != null) {
            oFileChoose.setCurrentDirectory(new File(sPreviousOpenedDirectory));
        }

        int nReturnValue = oFileChoose.showOpenDialog(null);
        if (nReturnValue == JFileChooser.APPROVE_OPTION) {

            File oSelectedFile = oFileChoose.getSelectedFile();
            Application.OnSaveLastBrowseDirectory(oSelectedFile.getParent());
            Objects.requireNonNull(sPreviousOpenedDirectory, "Directory not found.");

            Path oSelectedPath = oFileChoose.getSelectedFile().toPath();
            oConsumePath.accept(oSelectedPath);
        }
    }
}
