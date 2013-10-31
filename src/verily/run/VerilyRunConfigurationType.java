package verily.run;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import verily.Constants;

import javax.swing.*;

public class VerilyRunConfigurationType implements ConfigurationType {

    public static final VerilyRunConfigurationType INSTANCE = new VerilyRunConfigurationType();

    private final ConfigurationFactory myFactory;

    public VerilyRunConfigurationType() {
        myFactory = new VerilyConfigurationFactory(this);
    }

    @Override
    public String getDisplayName() {
        return Constants.VERILY_DESCRIPTION;
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Creates a run configuration suitable for running a Verily Framework project.";
    }

    @Override
    public Icon getIcon() {
        return Constants.VERILY_ICON;
    }

    @NotNull
    @Override
    public String getId() {
        return "VerilyRunConfiguration";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[] {myFactory};
    }
}
