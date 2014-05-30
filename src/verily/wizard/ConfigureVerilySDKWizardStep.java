package verily.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

public class ConfigureVerilySDKWizardStep extends ModuleWizardStep {

    ConfigureVerilySDKPanel panel;

    @Override
    public JComponent getComponent() {
        panel = new ConfigureVerilySDKPanel();
        return panel.getContentPane();
    }

    @Override
    public void updateDataModel() {
    }
}
