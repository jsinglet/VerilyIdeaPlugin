package verily.run;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class VerilyRunConfigurationEditor extends SettingsEditor<VerilyRunConfiguration> {

    private final ProgramParamsPanel programParams;

    VerilyRunConfigurationEditor(Module[] modules) {
        programParams = new ProgramParamsPanel(modules);
    }

    protected void applyEditorTo(VerilyRunConfiguration s) {
        programParams.applyEditorTo(s);
    }

    protected void resetEditorFrom(VerilyRunConfiguration s) {
        programParams.reset(s);
    }

    @NotNull
    protected JComponent createEditor() {
        return programParams.getVisual();
    }

    protected void disposeEditor() {
    }
}
