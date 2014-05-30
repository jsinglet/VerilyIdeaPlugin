package verily.facet;

import com.intellij.facet.ui.FacetBasedFrameworkSupportProvider;
import com.intellij.framework.library.DownloadableLibraryService;
import com.intellij.framework.library.FrameworkSupportWithLibrary;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportConfigurableBase;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModel;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportProviderBase;
import com.intellij.ide.util.frameworkSupport.FrameworkVersion;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.libraries.CustomLibraryDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import verily.library.VerilyLibraryType;

import java.util.List;

public class VerilyFrameworkSupportProvider extends FacetBasedFrameworkSupportProvider<VerilyFacet> {


    public VerilyFrameworkSupportProvider() {
        super(VerilyFacetType.INSTANCE);
    }

    @Override
    protected void setupConfiguration(VerilyFacet facet, ModifiableRootModel rootModel, FrameworkVersion version) {
        //
    }

    @Override
    public String getTitle() {
        return "Verily Framework";
    }

    @NotNull
    @Override
    public FrameworkSupportConfigurableBase createConfigurable(@NotNull FrameworkSupportModel model) {
        return new VerilyFrameworkSupportConfigurable(this, model, getVersions(), getVersionLabelText());
    }

    private static class VerilyFrameworkSupportConfigurable extends FrameworkSupportConfigurableBase implements FrameworkSupportWithLibrary {
        private VerilyFrameworkSupportConfigurable(FrameworkSupportProviderBase frameworkSupportProvider, FrameworkSupportModel model, @NotNull List<FrameworkVersion> versions, @Nullable String versionLabelText) {
            super(frameworkSupportProvider, model, versions, versionLabelText);
        }

        @Override
        @NotNull
        public CustomLibraryDescription createLibraryDescription() {
            return DownloadableLibraryService.getInstance().createDescriptionForType(VerilyLibraryType.class);
        }

        @Override
        public boolean isLibraryOnly() {
            return false;
        }
    }
}