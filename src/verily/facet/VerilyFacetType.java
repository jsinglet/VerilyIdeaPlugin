package verily.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import verily.Constants;
import verily.module.VerilyModuleType;

import javax.swing.*;

public class VerilyFacetType extends FacetType<VerilyFacet, VerilyFacetConfiguration> {

    private static final String STRING_ID = "Verily";
    private static final String PRESENTABLE_NAME = "Verily Framework";
    public static final FacetTypeId<VerilyFacet> ID = new FacetTypeId<VerilyFacet>(STRING_ID);
    public static final VerilyFacetType INSTANCE = new VerilyFacetType();

    public VerilyFacetType() {
        super(ID, STRING_ID, PRESENTABLE_NAME);
    }

    @Override
    public VerilyFacetConfiguration createDefaultConfiguration() {
        return new VerilyFacetConfiguration();
    }

    @Override
    public VerilyFacet createFacet(@NotNull Module module, String name, @NotNull VerilyFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new VerilyFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return (moduleType instanceof VerilyModuleType);
    }

    @Override
    public boolean isOnlyOneFacetAllowed() {
        return true;
    }

    @Override
    public Icon getIcon() {
        return Constants.VERILY_ICON;
    }

    public static class VerilyFacetDetector extends FacetBasedFrameworkDetector<VerilyFacet, VerilyFacetConfiguration> {
        public VerilyFacetDetector() {
            super(STRING_ID);
        }

        @Override
        public FacetType<VerilyFacet, VerilyFacetConfiguration> getFacetType() {
            //noinspection unchecked
            return FacetTypeRegistry.getInstance().findFacetType(STRING_ID);
        }

        @NotNull
        @Override
        public FileType getFileType() {
            return StdFileTypes.HTML;
        }

        @NotNull
        @Override
        public ElementPattern<FileContent> createSuitableFilePattern() {
            // review: improve "xmlns:wicket" detection with patterns
            return FileContentPattern.fileContent().with(new PatternCondition<FileContent>("wicketNamespace") {
                @Override
                public boolean accepts(@NotNull final FileContent fileContent, final ProcessingContext context) {
                    return fileContent.getContentAsText().toString().contains("xmlns:wicket");
                }
            });
        }
    }
}
