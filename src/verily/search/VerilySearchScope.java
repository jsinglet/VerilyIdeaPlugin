package verily.search;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import org.jetbrains.annotations.NotNull;
import verily.facet.VerilyFacet;


public final class VerilySearchScope {
    private VerilySearchScope() {
    }

    @NotNull
    public static GlobalSearchScope resourcesInModuleWithDependenciesAndLibraries(@NotNull Module module) {
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true);
        // add all additional resource paths
        VerilyFacet facet = VerilyFacet.getInstance(module);
        if (facet != null) {
            for (VirtualFilePointer filePointer : facet.getResourcePaths()) {
                VirtualFile virtualFile = filePointer.getFile();
                if (virtualFile != null) {
                    scope = scope.uniteWith(GlobalSearchScopes.directoryScope(module.getProject(), virtualFile, true));
                }
            }
        }
        return scope;
    }

    @NotNull
    public static GlobalSearchScope classInModuleWithDependenciesAndLibraries(@NotNull Module module) {
        return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true);
    }
}
