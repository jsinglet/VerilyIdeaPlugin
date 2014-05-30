package verily.module;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectWizardStepFactory;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.IconLoader;
import verily.Constants;
import verily.sdk.VerilySdkType;

import javax.swing.*;
import java.awt.*;


public class VerilyModuleType extends JavaModuleType {

        public static final VerilyModuleType INSTANCE = new VerilyModuleType();

        public VerilyModuleType() {
            super("JAVA_MODULE");
        }

        public VerilyModuleBuilder createModuleBuilder() {
            return new VerilyModuleBuilder();
        }

        public String getName() {
            return "Verily Framework Project";
        }

        public String getDescription() {
            return "<b>Verily Framework</b> project";
        }

        public Icon getBigIcon() {
            return (Constants.VERILY_ICON_BIG);
        }

        public Icon getNodeIcon(boolean isOpened) {
            return Constants.VERILY_ICON;
        }

        @Override
        public boolean isValidSdk(Module module, Sdk projectSdk) {
            if(projectSdk instanceof VerilySdkType){
                return true;
            }

            return false;
        }

        public static ModuleType<?> get(Module module) {
            return ModuleType.get(module);
        }

        public ModuleWizardStep[] createWizardSteps(WizardContext context, JavaModuleBuilder moduleBuilder, ModulesProvider modulesProvider) {
            final ProjectWizardStepFactory wizardFactory = ProjectWizardStepFactory.getInstance();
            ModuleWizardStep[] steps = new ModuleWizardStep[1];

            steps[0] = wizardFactory.createSourcePathsStep(context, moduleBuilder, getBigIcon(), "reference.dialogs.new.project.fromScratch.source");
//        steps[1] = wizardFactory.createProjectJdkStep(context, JavaSdk.getInstance(), moduleBuilder,
//                GrailsUtils.isJdkNeededToBeConfigured(context), getGrailsModuleWizardIcon(), "project.new.page2");
//        return steps;

            return steps;
        }


//    public ModuleWizardStep[] createWizardSteps(WizardContext context, JavaModuleBuilder moduleBuilder, ModulesProvider modulesProvider) {
//        final ProjectWizardStepFactory wizardFactory = ProjectWizardStepFactory.getInstance();
//        ModuleWizardStep[] steps = new ModuleWizardStep[1];
//
//        steps[0] = wizardFactory.createSourcePathsStep(context, moduleBuilder, getBigIcon(), "reference.dialogs.new.project.fromScratch.source");
////        steps[1] = wizardFactory.createProjectJdkStep(context, JavaSdk.getInstance(), moduleBuilder,
////                GrailsUtils.isJdkNeededToBeConfigured(context), getGrailsModuleWizardIcon(), "project.new.page2");
////        return steps;
//
//        return steps;
//    }

}
