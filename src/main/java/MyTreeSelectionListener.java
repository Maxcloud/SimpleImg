import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class MyTreeSelectionListener implements TreeSelectionListener {

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode oDefaultMutableTreeNode =
                (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
        // System.out.println(e.getPath().toString());

        if (oDefaultMutableTreeNode == null) return;

        Object oObject = oDefaultMutableTreeNode.getUserObject();
        // System.out.println(oObject.toString());
    }
}
