import app.AppMenu;
import app.FileHelp;
import app.TmpFileCache;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class SimpleImgEditor extends JFrame {

    private JPanel oMainPanel;
    private JTabbedPane oTabbedPane;
    private JPanel oTopPanel;
    private JPanel oMiddlePanel;
    private JPanel oBottomPanel;
    private JTree oDefaultTree;
    private JScrollPane oScrollPane;

    public SimpleImgEditor() {
        setContentPane(oMainPanel);
        setTitle("Simple Image Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAppMenu();
        setVisible(true);
        setDefaultTree(oDefaultTree);
    }

    private void setAppMenu() {
        TmpFileCache oTmpFileCache = new TmpFileCache();
        FileHelp oFileHelp = new FileHelp(
                oTmpFileCache, oDefaultTree);

        setJMenuBar(new AppMenu(oFileHelp));
    }

    private void setDefaultTree(JTree oDefaultTree) {
        DefaultMutableTreeNode oRoot        = new DefaultMutableTreeNode("Root");
        DefaultTreeModel oDefaultTreeModel  = new DefaultTreeModel(oRoot);
        TreePath oDefaultTreePath           = new TreePath(oRoot.getPath());

        oDefaultTree.setModel(oDefaultTreeModel);
        oDefaultTree.setRootVisible(false);
        oDefaultTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        oDefaultTree.addTreeSelectionListener(
                new MyTreeSelectionListener());
        oDefaultTree.expandPath(oDefaultTreePath);
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
            new SimpleImgEditor();
        });
    }

}
