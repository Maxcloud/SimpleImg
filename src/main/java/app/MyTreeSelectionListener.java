package app;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.function.Consumer;

public class MyTreeSelectionListener implements TreeSelectionListener {

    private Consumer<String> oTreeSelectionEvent;

    /*public app.MyTreeSelectionListener(Consumer<String> oTreeSelectionEvent) {
        this.oTreeSelectionEvent = oTreeSelectionEvent;
    }*/

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode oDefaultMutableTreeNode =
                (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
        System.out.println(e.getPath().toString());

        if (oDefaultMutableTreeNode == null) return;

        Object oObject = oDefaultMutableTreeNode.getUserObject();
        // oTreeSelectionEvent.accept(cow);
        // System.out.println(oObject);
    }
}
