package verily.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import verily.Constants;
import verily.search.VerilySearchScope;
import verily.util.VerilyFileUtil;

import java.util.ArrayList;
import java.util.List;


public class VerilyFacet extends Facet<VerilyFacetConfiguration> {
    private List<VirtualFilePointer> resourcePaths;

    public VerilyFacet(@NotNull FacetType facetType, @NotNull Module module, String name, @NotNull VerilyFacetConfiguration configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @NotNull
    public List<VirtualFilePointer> getResourcePaths() {
        if (resourcePaths == null) {
            resourcePaths = new ArrayList<VirtualFilePointer>();
            for (String resourceUrl : getConfiguration().resourceUrls) {
                resourcePaths.add(VirtualFilePointerManager.getInstance().create(resourceUrl, getModule(), null));
            }
        }
        return resourcePaths;
    }

    public void setResourcePaths(@NotNull List<VirtualFilePointer> resourcePaths) {
        this.resourcePaths = resourcePaths;
        VerilyFacetConfiguration configuration = getConfiguration();
        configuration.resourceUrls.clear();
        for (VirtualFilePointer virtualFilePointer : resourcePaths) {
            configuration.resourceUrls.add(FileUtil.toSystemIndependentName(virtualFilePointer.getUrl()));
        }
    }

    @Nullable
    public static VerilyFacet getInstance(@NotNull final Module module) {
        return FacetManager.getInstance(module).getFacetByType(VerilyFacetType.ID);
    }

    public static boolean isLibraryPresent(@Nullable Module module) {
        if (module == null) {
            return false;
        }
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(module.getProject());
        return psiFacade.findClass(Constants.VERILY_CONTENT, VerilySearchScope.classInModuleWithDependenciesAndLibraries(module)) != null;
    }

    /**
     * @param element
     * @return true if element has VerilyFacet or is from library
     */
    public static boolean hasFacetOrIsFromLibrary(@Nullable PsiElement element) {
        if (element != null) {
            VirtualFile vf = PsiUtil.getVirtualFile(element);
            if (vf != null) {
                Project project = element.getProject();
                Module module = ModuleUtil.findModuleForFile(vf, project);
                // if we got a module -> check if VerilyFacet available
                if (module != null) {
                    return getInstance(module) != null;
                }
                // else check if file from lib
                return VerilyFileUtil.isInLibrary(vf, project);
            }
        }
        return false;
    }
}