package verily.util;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public final class VerilyFileUtil {

    public static boolean isInLibrary(@NotNull VirtualFile vf, @NotNull com.intellij.openapi.project.Project project) {
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        return projectFileIndex.isInLibrarySource(vf) || projectFileIndex.isInLibraryClasses(vf);
    }
}
