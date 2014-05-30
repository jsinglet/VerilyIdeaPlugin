package verily.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: John
 * Date: 10/11/13
 * Time: 1:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class VerilyAddPairAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final DataContext context = anActionEvent.getDataContext();
        MavenProjectsManager manager = MavenActionUtil.getProjectsManager(context);
        manager.addManagedFiles(Collections.singletonList(getSelectedFile(context)));

    }

    @Nullable
    private static VirtualFile getSelectedFile(DataContext context) {
        return PlatformDataKeys.VIRTUAL_FILE.getData(context);
    }


    /**
     *
     *  @Override
    25  public void More ...update(AnActionEvent e) {
    26    super.update(e);
    27    Presentation p = e.getPresentation();
    28    p.setEnabled(isAvailable(e));
    29    p.setVisible(isVisible(e));
    30  }


        Project project = event.getData(PlatformDataKeys.PROJECT);
        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        Messages.showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());

    31
    32  protected boolean More ...isAvailable(AnActionEvent e) {
    33    return MavenActionUtil.hasProject(e.getDataContext());
    34  }
    35
    36  protected boolean More ...isVisible(AnActionEvent e) {
    37    return true;
    38  }
     */
}
