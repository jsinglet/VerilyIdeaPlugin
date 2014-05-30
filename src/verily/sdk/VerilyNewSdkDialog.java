package verily.sdk;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CollectionComboBoxModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: John
 * Date: 10/17/13
 * Time: 10:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class VerilyNewSdkDialog extends DialogWrapper {
    private JPanel myContentPanel;
    private JComboBox myInternalJdkComboBox;

    protected VerilyNewSdkDialog(@Nullable Project project,
                                  @NotNull List<String> javaSdkNames,
                                  @NotNull String selectedJavaSdkName) {
        super(project);
        setTitle("Create New Verily SDK");
        myInternalJdkComboBox.setModel(new CollectionComboBoxModel(javaSdkNames, selectedJavaSdkName));

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myContentPanel;
    }

    public int getSelectedJavaSdkIndex() {
        return myInternalJdkComboBox.getSelectedIndex();
    }


}