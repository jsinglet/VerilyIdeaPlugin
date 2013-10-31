package verily.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.DefaultJavaProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.SdkFinder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import verily.util.VerilyExeUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: John
 * Date: 10/11/13          S
 * Time: 3:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class VerilyAddNewMethodRouterPair extends AnAction {
    public void actionPerformed(final AnActionEvent e) {


        String newName= Messages.showInputDialog(e.getProject(), "Method Name", "Method Name", Messages.getQuestionIcon());
        //Messages.showMessageDialog(project, "", "Information", Messages.getInformationIcon())

        Process process = null;

        //final ProcessBuilder initBuilder = new ProcessBuilder(String.format("java -classpath \".;C:\\Program Files\\Verily\\lib\\*\" VerilyMain -new %s", newName));
        //final ProcessBuilder initBuilder = new ProcessBuilder("/usr/bin/verily", "-new", newName);

        final ProcessBuilder initBuilder = VerilyExeUtil.newPair(ProjectRootManager.getInstance(e.getProject()).getProjectSdk(),  newName);


        initBuilder.directory(new File(e.getProject().getBasePath()));

        final ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("Verily Messages");
        final ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(e.getProject()).getConsole();
        final Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "Create New Method", true);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //toolWindow.getContentManager().addContent(content);
                toolWindow.activate(new Runnable(){

                    @Override
                    public void run() {
                        toolWindow.getContentManager().addContent(content);

                        Process process = null;
                        try {
                            process = initBuilder.start();
                            ProcessHandler processHandler = new DefaultJavaProcessHandler(process, null, Charset.defaultCharset());
                            processHandler.startNotify();
                            consoleView.attachToProcess(processHandler);

                            process.waitFor();
                            e.getProject().getBaseDir().refresh(false, true);

                        } catch (IOException e1) {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                });
            }});


    }


}