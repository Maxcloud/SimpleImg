package app;

import img.WzPathNavigator;
import img.io.repository.JsonFileRepository;
import img.model.common.WzImgCache;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileHelp {

    private final TmpFileCache oTmpFileCache;
    private final JTree oDefaultTree;

    public FileHelp(TmpFileCache oTmpFileCache, JTree oDefaultTree) {
        this.oTmpFileCache = oTmpFileCache;
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
        return oTmpFileCache.getPathToImgCache().containsKey(oPath);
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

        JsonFileRepository<WzImgCache> repository =
                new JsonFileRepository<>(oPath, WzImgCache.class);

        WzImgCache oWzImgCache = repository.OnLoadJsonAsObj();
        oTmpFileCache.addImgCache(oPath, oWzImgCache);
        AddImgToTree(oPath, oWzImgCache);
    }

    /**
     * Add the loaded image file to the tree view.
     *
     * @param oPath  The path of the image file.
     * @param oImgCache The WzImgCache object representing the image data.
     */
    private void AddImgToTree(Path oPath, WzImgCache oImgCache) {

        DefaultTreeModel oDefaultTreeModel = (DefaultTreeModel) oDefaultTree.getModel();
        DefaultMutableTreeNode oRoot = (DefaultMutableTreeNode) oDefaultTreeModel.getRoot();

        TreePath oTreePath = new TreePath(oRoot.getPath());
        WzPathNavigator oWzPathNav = new WzPathNavigator("", oImgCache);

        DefaultMutableTreeNode oDefaultTreeNode =
                new DefaultMutableTreeNode(oPath.getFileName());

        oDefaultTreeModel.insertNodeInto(oDefaultTreeNode, oRoot, oRoot.getChildCount());
        ParseImgFile(oWzPathNav, oDefaultTreeNode);

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
