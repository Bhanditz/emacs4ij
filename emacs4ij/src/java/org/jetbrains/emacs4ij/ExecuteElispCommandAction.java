package org.jetbrains.emacs4ij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.emacs4ij.jelisp.Environment;
import org.jetbrains.emacs4ij.jelisp.Parser;
import org.jetbrains.emacs4ij.jelisp.elisp.LispObject;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Ekaterina.Polishchuk
 * Date: 7/19/11
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteElispCommandAction extends AnAction {

    private Parser myParser = new Parser();
    private JTextField myInput = new JTextField();

    public ExecuteElispCommandAction () {
        //Environment.myAction = this;

        myInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                    //keyEvent.consume();
                    if (Environment.ourEmacsPath.equals("")) {
                        Messages.showInfoMessage("You should choose Emacs home directory!", "Emacs4ij");
                        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
                        fileChooser.setDialogTitle("Select Emacs home directory");
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        if (fileChooser.showOpenDialog(myInput) == JFileChooser.APPROVE_OPTION) {
                            Environment.ourEmacsPath = fileChooser.getSelectedFile().getAbsolutePath();
                        } else {
                            Messages.showErrorDialog("You didn't choose Emacs home directory!\nNo command evaluation will be done.", "Emacs4ij");
                            return;
                        }
                    }

                    String parameterValue = ((JTextField)keyEvent.getComponent()).getText();
                    //Messages.showInfoMessage(parameterValue, "component value");
                    LispObject lispObject = myParser.parseLine(parameterValue).evaluate(Environment.ourGlobal);
                    Messages.showInfoMessage(lispObject.toString(), "Evaluation result");
                }
            }
        });
    }

    public void actionPerformed(AnActionEvent e) {
        Editor editor = PlatformDataKeys.EDITOR.getData(e.getDataContext());
        if (editor == null)
            return;
        if (editor.getHeaderComponent() != null) {
            if (!editor.getHeaderComponent().equals(myInput))
                editor.setHeaderComponent(myInput);
        } else
            editor.setHeaderComponent(myInput);

        myInput.setText("'a");
        myInput.grabFocus();

        /*ExecuteElispCommandForm executeElispCommandForm = new ExecuteElispCommandForm(e);
        executeElispCommandForm.setVisible(true);*/
    }

}