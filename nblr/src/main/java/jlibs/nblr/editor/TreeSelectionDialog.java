/**
 * Copyright 2015 Santhosh Kumar Tekuri
 *
 * The JLibs authors license this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jlibs.nblr.editor;

import jlibs.core.graph.Visitor;
import jlibs.swing.tree.MyTreeCellRenderer;
import jlibs.swing.tree.NavigatorTreeModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Santhosh Kumar T
 */
abstract class TreeSelectionDialog extends JDialog implements TreeSelectionListener{
    protected JTree tree;

    @SuppressWarnings({"unchecked"})
    public TreeSelectionDialog(Window owner, String title){
        super(owner, title);
        setModal(true);
    }

    protected void createContents(){
        JPanel contents = (JPanel)getContentPane();
        contents.setLayout(new BorderLayout(0, 10));
        contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tree = new JTree(treeModel());
        MyTreeCellRenderer cellRenderer = new MyTreeCellRenderer();
        cellRenderer.setTextConvertor(displayVisitor());
        tree.setCellRenderer(cellRenderer);
        tree.setRootVisible(showRoot());
        tree.setShowsRootHandles(true);
        tree.setFont(Util.FIXED_WIDTH_FONT);
        contents.add(new JScrollPane(tree));
        tree.addTreeSelectionListener(this);
        valueChanged(null);
        tree.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent me){
                if(me.getClickCount()>1 && tree.getSelectionPath()!=null)
                    okAction.actionPerformed(null);
            }
        });

        JPanel buttons = new JPanel(new GridLayout(1, 0));
        JButton defaultButton = null;
        for(Action action: actions()){
            JButton button = new JButton(action);
            if(action==defaultAction())
                defaultButton = button;
            buttons.add(button);
        }

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttons, BorderLayout.EAST);
        getContentPane().add(southPanel, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(cancelAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JRootPane.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(defaultButton);

        tree.setVisibleRowCount(15);
        setMinimumSize(new Dimension(400, 500));
        pack();
        setLocationRelativeTo(null);
    }

    protected Action defaultAction(){
        return okAction;
    }
    
    protected Action[] actions(){
        return new Action[]{ okAction, cancelAction };
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent tse){
        okAction.setEnabled(tree.getSelectionPath()!=null);
    }

    boolean ok = false;
    protected void onOK(){
        ok = true;
    }

    protected Action okAction = new AbstractAction("Ok"){
        @Override
        public void actionPerformed(ActionEvent ae){
            onOK();
            setVisible(false);
        }
    };

    @SuppressWarnings({"FieldCanBeLocal"})
    protected Action cancelAction = new AbstractAction("Cancel"){
        @Override
        public void actionPerformed(ActionEvent ae){
            setVisible(false);
        }
    };

    protected abstract NavigatorTreeModel treeModel();
    protected abstract Visitor<Object, String> displayVisitor();
    protected boolean showRoot(){
        return true;
    }
}
