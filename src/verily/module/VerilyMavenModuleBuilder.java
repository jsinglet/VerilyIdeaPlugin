package verily.module;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.DefaultJavaProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;
import verily.Constants;
import verily.facet.VerilyFacet;
import verily.sdk.VerilySdkType;
import verily.util.VerilyExeUtil;
import verily.wizard.ConfigureVerilySDKPanel;
import verily.wizard.ConfigureVerilySDKWizardStep;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerilyMavenModuleBuilder extends JavaModuleBuilder {

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        if(sdkType.getName().equals(new VerilySdkType().getName())){
            return true;
        }

        return false;
    }

    @Override
    public ModuleType getModuleType() {
        return VerilyModuleType.INSTANCE;
    }

    @Override
    public String getName() {
        return "GET NAME";
    }
//
//    @Nullable
//    @Override
//    public Module commitModule(@NotNull Project project, @Nullable ModifiableModuleModel model) {
//        return super.commitModule(project, model);    //To change body of overridden methods use File | Settings | File Templates.
//    }
//
    @Nullable
    @Override
    public String getBuilderId() {
        return "verilybuilderid";    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(WizardContext wizardContext, ModulesProvider modulesProvider) {

        final ProjectWizardStepFactory wizardFactory = ProjectWizardStepFactory.getInstance();
        ModuleWizardStep[] steps = new ModuleWizardStep[0];

        //steps[0] = new ConfigureVerilySDKWizardStep();

//        steps[0] = wizardFactory.createSupportForFrameworksStep(wizardContext, this, modulesProvider);

        //wizardFactory.createSourcePathsStep(wizardContext, modulesProvider, getBigIcon(), "reference.dialogs.new.project.fromScratch.source");
//        steps[1] = wizardFactory.createProjectJdkStep(context, JavaSdk.getInstance(), moduleBuilder,
//                GrailsUtils.isJdkNeededToBeConfigured(context), getGrailsModuleWizardIcon(), "project.new.page2");
//        return steps;

        return steps;
    }

    @Override
    public String getPresentableName() {
        return Constants.VERILY_MODULE_DESCRIPTION;
    }


    @Override
    public Icon getNodeIcon() {
        return Constants.VERILY_ICON;
    }

    @Override
    public Icon getBigIcon() {
        return Constants.VERILY_ICON_BIG;
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {

        final Project project = rootModel.getProject();


        rootModel.inheritSdk();

        String verilyRoot = rootModel.getSdk().getHomePath();

        //ProcessBuilder initBuilder = new ProcessBuilder(String.format("java -classpath \".;C:\\Program Files\\Verily\\lib\\*\" VerilyMain -init ./%s", rootModel.getProject().getName()));
///        ProcessBuilder initBuilder = new ProcessBuilder("/usr/bin/verily", "-init", String.format("./%s", rootModel.getProject().getName()));
        ProcessBuilder initBuilder = VerilyExeUtil.initProject(rootModel.getSdk(), project.getName());
        initBuilder.directory(new File(rootModel.getProject().getBasePath()));

        try {
            Process init = initBuilder.start();
            init.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            displayInitError(String.format("Unable to create new verily project: %s", e.getMessage()), project);
        }

        rootModel.addContentEntry(rootModel.getProject().getBaseDir());

        // setup the test paths and stuff
        //rootModel.inheritSdk();
        //super.setupRootModel(rootModel);

        //
        // After the project is created, run the verily creator to make sure the project gets set up right...
        //
        ProjectManager.getInstance().addProjectManagerListener(rootModel.getProject(), new ProjectManagerListener() {

            @Override
            public void projectOpened(Project project) {

                List<VirtualFile> pomFiles = new ArrayList<VirtualFile>();

                if(project.getBaseDir().findChild("pom.xml")==null){
                    displayInitError(String.format("Unable to create new verily project"), project);
                }else{

                    pomFiles.add(project.getBaseDir().findChild("pom.xml"));

                    MavenProjectsManager manager = MavenProjectsManager.getInstance(project);

                    manager.addManagedFiles(pomFiles);
                }

            }

            @Override
            public boolean canCloseProject(Project project) {
                return true;
            }

            @Override
            public void projectClosed(Project project) {
            }

            @Override
            public void projectClosing(Project project) {
            }
        });
    }

    public void displayInitError(final String error, final Project project){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String text = "<html><a href=\"openBrowser\" target=\"_top\">How do I fix this?</a></html>";
                Notifications.Bus.notify(new Notification("SourceFinder", error, text, NotificationType.ERROR, new NotificationListener() {
                    @Override
                    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull javax.swing.event.HyperlinkEvent event) {
                        if (event.getDescription().equals("openBrowser")) {
                            //launchBrowser("https://bitbucket.org/mtiigi/intellij-sourcefinder-plugin");
                        }
                    }
                }), project);
            }
        });


    }


}


//
//33    MavenProjectsManager manager = MavenActionUtil.getProjectsManager(context);
//34    manager.addManagedFiles(Collections.singletonList(getSelectedFile(context)));

//
//extends JavaModuleBuilder { //ModuleBuilder implements SourcePathsBuilder {
//
//
//
//    @Override
//    public String getPresentableName() {
//        return "Verily Module";
//    }
//
//    @Override
//    public String getDescription() {
//        return "Maven modules are used for developing <b>JVM-based</b> applications with dependencies managed by <b>Maven</b>. " +
//                "You can create either a blank Maven module or a module based on a <b>Maven archetype</b>.";
//    }
//
//    @Override
//    public Icon getBigIcon() {
//        return AllIcons.Modules.Types.JavaModule;
//    }
//
//    @Override
//    public Icon getNodeIcon() {
//        return Constants.VERILY_ICON;
//    }
//
//    public ModuleType getModuleType() {
//        return StdModuleTypes.JAVA;
//    }
//
//
//    @Override
//    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
//        ProjectJdkTable table = ProjectJdkTable.getInstance();
//        Sdk[] sdks = table.getAllJdks();
//        Sdk verily = null;
//        for (Sdk sdk : sdks) {
//            if (sdk.getSdkType().equals(VerilySdkType.INSTANCE)) {
//                verily = sdk;
//                break;
//            }
//        }
//        if (verily == null) {
//            Comparator<Sdk> sdkComparator = new Comparator<Sdk>() {
//                public int compare(Sdk s1, Sdk s2) {
//                    return 1;
//                }
//            };
//
//
//
//            ProjectManager.getInstance().addProjectManagerListener(rootModel.getProject(), new ProjectManagerListener(){
//
//                @Override
//                public void projectOpened(Project project) {
//                    //displayNotFoundMessage(project);
//                    System.out.println("Project Opened!");
//                }
//
//                @Override
//                public boolean canCloseProject(Project project) {
//                    return true;
//                }
//
//                @Override
//                public void projectClosed(Project project) {
//                    //To change body of implemented methods use File | Settings | File Templates.
//                }
//
//                @Override
//                public void projectClosing(Project project) {
//                    //To change body of implemented methods use File | Settings | File Templates.
//                }
//            });
//
//            //displayNotFoundMessage(rootModel.getProject());
//
//
//            //verily = SdkConfigurationUtil.findOrCreateSdk(sdkComparator, VerilySdkType.INSTANCE);
//        }


//        MavenUtil.runWhenInitialized(project, new DumbAwareRunnable() {
//            public void verily.run() {
//                new MavenModuleBuilderHelper(myProjectId, myAggregatorProject, myParentProject, myInheritGroupId,
//                        myInheritVersion, myArchetype, "Create new Maven module").configure(project, root, false);
//            }
//        });

//
//        Process init = null;
//        ProcessBuilder initBuilder = new ProcessBuilder(String.format("java -classpath \".;C:\\Program Files\\Verily\\lib\\*\" VerilyMain -init ./%s", rootModel.getProject().getName()));
//        initBuilder.directory(new File(rootModel.getProject().getBasePath()));
//
//        try {
//            init = initBuilder.start();
//            init.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        final Project project = rootModel.getProject();
//
//        final VirtualFile root = rootModel.getProject().getBaseDir();
//        rootModel.addContentEntry(root);
//
//        rootModel.inheritSdk();

//        MavenUtil.runWhenInitialized(project, new DumbAwareRunnable() {
//            public void verily.run() {
//
//                System.out.println("Creating Init!");
//
//                List<VirtualFile> pomFiles = new ArrayList<VirtualFile>();
//
//                pomFiles.add(root.findChild("pom.xml"));
//
//                MavenProjectsManager manager = new MavenProjectsManager(project);
//
//                manager.addManagedFiles(pomFiles);
//
//
//            }
//        });

//        }

//    @Override
//    public List<Pair<String, String>> getSourcePaths() throws ConfigurationException {
//        return Collections.emptyList();
//    }
//
//    @Override
//    public void setSourcePaths(List<Pair<String, String>> pairs) {
//    }
//
//    @Override
//    public void addSourcePath(Pair<String, String> stringStringPair) {
//    }
////
////    private void displayNotFoundMessage(final Project project) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void verily.run() {
//                String text = "<html><a href=\"openBrowser\" target=\"_top\">Maybe you want to write your own resolver?</a></html>";
//                Notifications.Bus.notify(new Notification("SourceFinder", "No sources were found.", text, NotificationType.WARNING, new NotificationListener() {
//                    @Override
//                    public void hyperlinkUpdate(@NotNull Notification notification, @NotNull javax.swing.event.HyperlinkEvent event) {
//                        if (event.getDescription().equals("openBrowser")) {
//                            //launchBrowser("https://bitbucket.org/mtiigi/intellij-sourcefinder-plugin");
//                        }
//                    }
//                }), project);
//            }
//        });
//    }


//        @Override
//    public String getGroupName() {
//        return "Dynamic Web";
//    }
//

