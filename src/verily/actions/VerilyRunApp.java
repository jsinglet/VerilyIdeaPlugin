package verily.actions;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.DefaultJavaProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import verily.run.VerilyRunConfiguration;
import verily.run.VerilyRunConfigurationType;
import verily.util.VerilyExeUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * User: John
 * Date: 10/11/13
 * Time: 4:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class VerilyRunApp extends AnAction {
    public void actionPerformed(final AnActionEvent e) {

        RunnerAndConfigurationSettings[] t = RunManager.getInstance(e.getProject()).getConfigurationSettings(new VerilyRunConfigurationType());

        ProgramRunnerUtil.executeConfiguration(e.getProject(), t[0], DefaultRunExecutor.getRunExecutorInstance());

//
//        Process process = null;
////        final ProcessBuilder initBuilder = new ProcessBuilder(String.format("java -classpath \".;C:\\Program Files\\Verily\\lib\\*\" VerilyMain -verily.run"));
//        final ProcessBuilder initBuilder = VerilyExeUtil.run(ProjectRootManager.getInstance(e.getProject()).getProjectSdk(), null);
//
//        initBuilder.directory(new File(e.getProject().getBasePath()));
//
//        final ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("Verily Messages");
//        final ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(e.getProject()).getConsole();
//        final Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "Run Application", true);
//
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                //toolWindow.getContentManager().addContent(content);
//                toolWindow.activate(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        toolWindow.getContentManager().addContent(content);
//
//                        Process process = null;
//                        try {
//
////                            final ProcessBuilder browser = new ProcessBuilder(VerilyExeUtil.openCmd(),  "http://localhost:8000/HelloWorld/hello");
////                            browser.start();
//
//
//                            process = initBuilder.start();
//                            ProcessHandler processHandler = new DefaultJavaProcessHandler(process, null, Charset.defaultCharset());
//                            processHandler.startNotify();
//                            consoleView.attachToProcess(processHandler);
//
//                        } catch (IOException e1) {
//                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                        }
//                    }
//                });
//            }
//        });


    }
}
