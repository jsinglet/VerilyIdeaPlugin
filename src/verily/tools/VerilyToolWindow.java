package verily.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

import javax.swing.*;

public class VerilyToolWindow implements ToolWindowFactory {


    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        toolWindow.getComponent().add(new VerilyView().getContentPane());
  }
}



