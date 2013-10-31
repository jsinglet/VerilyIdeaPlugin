package verily.module;

import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;
import verily.ConfigureVerilySDK;
import verily.sdk.VerilySdkType;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VerilyModuleBuilder extends JavaModuleBuilder {

    @Override
    public ModuleType getModuleType() {
        return VerilyModuleType.INSTANCE;
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {


        Process init = null;
        ProcessBuilder initBuilder = new ProcessBuilder(String.format("java -classpath \".;C:\\Program Files\\Verily\\lib\\*\" VerilyMain -init ./%s", rootModel.getProject().getName()));
        initBuilder.directory(new File(rootModel.getProject().getBasePath()));

        try {
            init = initBuilder.start();
            init.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Project project = rootModel.getProject();

        rootModel.addContentEntry(rootModel.getProject().getBaseDir());

        // setup the test paths and stuff
        rootModel.inheritSdk();
        //super.setupRootModel(rootModel);

        ProjectManager.getInstance().addProjectManagerListener(rootModel.getProject(), new ProjectManagerListener(){

                @Override
                public void projectOpened(Project project) {

                    System.out.println("Creating Init!");

                    List<VirtualFile> pomFiles = new ArrayList<VirtualFile>();

                    pomFiles.add(project.getBaseDir().findChild("pom.xml"));

                    MavenProjectsManager manager = new MavenProjectsManager(project);

                    manager.addManagedFiles(pomFiles);

                }

                @Override
                public boolean canCloseProject(Project project) {
                    return true;
                }

                @Override
                public void projectClosed(Project project) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void projectClosing(Project project) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
    }


}
