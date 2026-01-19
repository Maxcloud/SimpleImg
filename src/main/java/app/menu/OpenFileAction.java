package app.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public class OpenFileAction extends AbstractAction {

    private final String sPreviousOpenedDirectory;;
    private final Consumer<Path> oConsumePath;

    public OpenFileAction(Consumer<Path> oConsumePath) {
        this.oConsumePath = oConsumePath;
        sPreviousOpenedDirectory = SimpleImgApplication.getLastOpenedDirectory();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser oFileSelect = new JFileChooser();
        oFileSelect.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (sPreviousOpenedDirectory != null) {
            oFileSelect.setCurrentDirectory(new File(sPreviousOpenedDirectory));
        }

        int nReturnValue = oFileSelect.showOpenDialog(null);
        if (nReturnValue == JFileChooser.APPROVE_OPTION) {

            File oSelectedFile = oFileSelect.getSelectedFile();
            SimpleImgApplication.saveLastOpenedDirectory(oSelectedFile.getParent());

            Path oSelectedPath = oFileSelect.getSelectedFile().toPath();
            oConsumePath.accept(oSelectedPath);
        }
    }
}
