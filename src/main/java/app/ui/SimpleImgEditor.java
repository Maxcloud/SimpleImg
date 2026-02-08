package app.ui;

import app.AppMenu;
import app.FileHelp;
import app.MyTreeSelectionListener;
import img.ImgFileCache;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.nio.file.Path;

public class SimpleImgEditor extends JFrame {

    private JPanel oMainPanel;
    private JTabbedPane oTabbedPane;
    private JPanel oTopPanel;
    private JPanel oMiddlePanel;
    private JPanel oBottomPanel;
    private JTree oDefaultTree;
    private JScrollPane oScrollPane;

    private final FileHelp oFileHelp;
    private final String sCurrentDirectory;

    public SimpleImgEditor(String sCurrentDirectory) {
        this.sCurrentDirectory = sCurrentDirectory;

        ImgFileCache oImgFileCache = new ImgFileCache();
        oFileHelp = new FileHelp(
                oImgFileCache, oDefaultTree);

        setContentPane(oMainPanel);
        setTitle("Simple Image Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(new AppMenu(oFileHelp));
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultTree(oDefaultTree);
        OnLoadDirectory(Path.of(sCurrentDirectory));
    }

    private void OnLoadDirectory(Path oPath) {
        oFileHelp.OnLoadMultipleFromJson(oPath);
    }

    private void setDefaultTree(JTree oDefaultTree) {
        DefaultMutableTreeNode oRoot        = new DefaultMutableTreeNode("Root");
        DefaultTreeModel oDefaultTreeModel  = new DefaultTreeModel(oRoot);
        TreePath oDefaultTreePath           = new TreePath(oRoot.getPath());

        MyTreeSelectionListener oMyTreeSelectionListener =
                new MyTreeSelectionListener();

        oDefaultTree.setModel(oDefaultTreeModel);
        oDefaultTree.setRootVisible(false);
        oDefaultTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        oDefaultTree.addTreeSelectionListener(
                oMyTreeSelectionListener);
        oDefaultTree.expandPath(oDefaultTreePath);
    }

}
