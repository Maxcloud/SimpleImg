package app;

import img.ImgFileCache;
import img.WzPathNavigator;
import img.io.repository.JsonFileRepository;
import img.model.common.FileImgRecord;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileHelp {

    private final ImgFileCache oImgFileCache;
    private final JTree oDefaultTree;

    public FileHelp(ImgFileCache oImgFileCache, JTree oDefaultTree) {
        this.oImgFileCache = oImgFileCache;
        this.oDefaultTree = oDefaultTree;
    }

    /**
     * Check if the file has the correct .img extension.
     *
     * @param oPath The path of the file to check.
     * @return True if the file has a .img extension, false otherwise.
     */
    private boolean isCorrectFileType(Path oPath) {
        return oPath
                .getFileName()
                .toString()
                .endsWith(".img");
    }

    /**
     * Check if the file has already been loaded.
     *
     * @param oPath The path of the file to check.
     * @return True if the file is already loaded, false otherwise.
     */
    private boolean isFileAlreadyLoaded(Path oPath) {
        return oImgFileCache.getImgCache().containsKey(oPath);
    }

    /**
     * Show a warning dialog with the given title and message.
     *
     * @param sTitle   The title of the warning dialog.
     * @param sMessage The message to display in the warning dialog.
     */
    private void warning(String sTitle, String sMessage) {
        JFrame oFrame = new JFrame();
        oFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JOptionPane.showMessageDialog(oFrame,
                sMessage,
                sTitle,
                JOptionPane.WARNING_MESSAGE);
    }

    public void OnLoadMultipleFromJson(Path oPath) {
        File oFile = oPath.toFile();
        File[] aFiles = oFile.listFiles();
        Objects.requireNonNull(aFiles, "No files found in the directory.");

        Stream<File> oStream = Arrays.stream(aFiles);
        Stream<Path> oMapToPath = oStream.map(File::toPath);
        Predicate<Path> isImgFile = oPathFile -> oPathFile
                .getFileName()
                .toString()
                .endsWith(".img");

        List<Path> pp = oMapToPath.filter(isImgFile).toList();
        for (Path oFilePath : pp) {
            OnLoadFromJson(oFilePath);
        }
    }

    /**
     * Load an image file from the given path in JSON format.
     *
     * @param oPath The path of the image file to load.
     */
    public void OnLoadFromJson(Path oPath) {
        if (!Files.exists(oPath)) {
            warning("Warning!","That file doesn't exist.");
            return;
        }

        if (!isCorrectFileType(oPath)) {
            warning("Warning!","That's not a valid file. Please select a .img file.");
            return;
        }

        if (isFileAlreadyLoaded(oPath)) {
            warning("Warning!","You've already loaded that file!");
            return;
        }

        JsonFileRepository<FileImgRecord> repository =
                new JsonFileRepository<>(oPath, FileImgRecord.class);

        FileImgRecord oFileImgRecord = repository.loadFromJson();
        oImgFileCache.insertFileImgRecord(oPath, oFileImgRecord);

        AddImgToTree(oPath, oFileImgRecord);
    }

    /**
     * Add the loaded image file to the tree view.
     *
     * @param oPath  The path of the image file.
     * @param oImgCache The WzImgCache object representing the image data.
     */
    private void AddImgToTree(Path oPath, FileImgRecord oImgCache) {

        DefaultTreeModel oDefaultTreeModel = (DefaultTreeModel) oDefaultTree.getModel();
        DefaultMutableTreeNode oParent = (DefaultMutableTreeNode) oDefaultTreeModel.getRoot();

        TreePath oTreePath = new TreePath(oParent.getPath());
        WzPathNavigator oWzPathNav = new WzPathNavigator("", oImgCache);

        DefaultMutableTreeNode oNewChild =
                new DefaultMutableTreeNode(oPath.getFileName());

        oDefaultTreeModel.insertNodeInto(oNewChild, oParent, oParent.getChildCount());
        ParseImgFile(oWzPathNav, oNewChild);

        oDefaultTree.expandPath(oTreePath);
    }

    /**
     * Recursively parse the WzPathNavigator and build the tree nodes.
     *
     * @param oRoot       The current WzPathNavigator node.
     * @param oParentNode The parent tree node to which child nodes will be added.
     */
    private void ParseImgFile(WzPathNavigator oRoot, DefaultMutableTreeNode oParentNode) {
        List<String> lChildren = oRoot.getChildren();
        for (String oChildId : lChildren) {
            DefaultMutableTreeNode oChildNode = new DefaultMutableTreeNode(oChildId);
            oParentNode.insert(oChildNode, oParentNode.getChildCount());

            WzPathNavigator oChildNav = oRoot.resolve(oChildId);
            ParseImgFile(oChildNav, oChildNode);
        }
    }
}
