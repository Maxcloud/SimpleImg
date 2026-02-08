package app.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenFileAction extends AbstractAction {

    private final String sPreviousOpenedDirectory;
    private final Consumer<Path> oConsumePath;

    public OpenFileAction(Consumer<Path> oConsumePath) {
        this.oConsumePath = oConsumePath;
        sPreviousOpenedDirectory = Application.OnGetLastBrowseDirectory();
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
            Path oFilePathSelected = oSelectedFile.toPath();
            String sFilePathSelected = oFilePathSelected.toString();

            Application.OnSaveLastBrowseDirectory(sFilePathSelected);
            Objects.requireNonNull(sPreviousOpenedDirectory, "Directory not found.");

            Pattern pattern = Pattern.compile("[^\\\\]+\\.wz");
            Matcher matcher = pattern.matcher(sPreviousOpenedDirectory);
            if (matcher.find()) {
                String directory = matcher.group();
                String capitalized = directory.substring(0, 1).toUpperCase() +
                        directory.substring(1);
                // System.out.println(capitalized);
                // return capitalized folder name
            }
            oConsumePath.accept(oFilePathSelected);
        }
    }
}
