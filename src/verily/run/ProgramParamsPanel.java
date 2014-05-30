package verily.run;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;

import javax.swing.*;

public class ProgramParamsPanel {
    private JPanel contentPanel;
    private JCheckBox doNotRecalculateDependenciesCheckBox;
    private JCheckBox dynamicallyReloadClassesCheckBox;
    private JPanel optionalRunSettingsPanel;
    private JCheckBox openBrowserAtLaunchCheckBox;
    private JTextField port;
    private JTextField jvmOptions;
    private JCheckBox enableContractsCheckBox;
    private JCheckBox disableStaticCheckingCheckBox;

    ProgramParamsPanel(Module[] modules) {
        // some default values
        port.setText("8000");
    }


    protected void reset(VerilyRunConfiguration s){

        port.setText(s.getPort().toString());
        dynamicallyReloadClassesCheckBox.setSelected(s.isDynamicallyReloadClasses());
        openBrowserAtLaunchCheckBox.setSelected(s.isLaunchBrowser());
        doNotRecalculateDependenciesCheckBox.setSelected(!s.isReloadDeps());
        jvmOptions.setText(s.getJvmOptions());
    }

    protected void applyEditorTo(VerilyRunConfiguration s){

        s.setDynamicallyReloadClasses(dynamicallyReloadClassesCheckBox.isSelected());
        s.setLaunchBrowser(openBrowserAtLaunchCheckBox.isSelected());
        s.setReloadDeps(!doNotRecalculateDependenciesCheckBox.isSelected());
        s.setJvmOptions(jvmOptions.getText());

        s.setDisableStaticChecking(disableStaticCheckingCheckBox.isSelected());
        s.setEnableContracts(enableContractsCheckBox.isSelected());


        // in this case null indicates an error
        try {
            s.setPort(Integer.parseInt(port.getText()));
        }catch(NumberFormatException ex){
            s.setPort(null);
        }


    }
    public JComponent getVisual(){ return contentPanel;}

}
