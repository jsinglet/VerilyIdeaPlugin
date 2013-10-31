package verily.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.project.actions.AddFileAsMavenProjectAction;
import org.jetbrains.idea.maven.utils.actions.MavenAction;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: John
 * Date: 10/11/13
 * Time: 2:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class AddTemplateAction  extends AnAction implements DumbAware  {
    @Override
    public void actionPerformed(AnActionEvent e) {
        final DataContext context = e.getDataContext();
        MavenProjectsManager manager = MavenActionUtil.getProjectsManager(context);
        manager.addManagedFiles(Collections.singletonList(getSelectedFile(context)));
    }

//    @Override
//    protected boolean isAvailable(AnActionEvent e) {
//        return true;
//    }
//
//    @Override
//    protected boolean isVisible(AnActionEvent e) {
//        return super.isVisible(e) && isAvailable(e);
//    }
//
//    private static boolean isExistingProjectFile(DataContext context, VirtualFile file) {
//        MavenProjectsManager manager = MavenActionUtil.getProjectsManager(context);
//        return manager.findProject(file) != null;
//    }

    @Nullable
    private static VirtualFile getSelectedFile(DataContext context) {
        return PlatformDataKeys.VIRTUAL_FILE.getData(context);
    }


}
