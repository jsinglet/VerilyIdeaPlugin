package verily.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public class VerilyConfigurationFactory extends ConfigurationFactory {
    public VerilyConfigurationFactory(VerilyRunConfigurationType runConfigurationType) {
        super(runConfigurationType);
    }

    public RunConfiguration createTemplateConfiguration(Project project) {
        return new VerilyRunConfiguration(this, project, "Verily Project");
    }

}