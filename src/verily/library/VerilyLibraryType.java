package verily.library;


import com.intellij.framework.library.DownloadableLibraryTypeBase;
import org.jetbrains.annotations.NotNull;
import verily.Constants;

import java.net.URL;

public class VerilyLibraryType extends DownloadableLibraryTypeBase {
    private static final String[] DETECTIONCLASSNAMES = new String[]{Constants.VERILY_APPLICATION};

    public VerilyLibraryType() {
        super("Verily", "verily", "verily", Constants.VERILY_ICON, getUrl());

    }

    @Override
    protected String[] getDetectionClassNames() {
        return DETECTIONCLASSNAMES;
    }

    @NotNull
    private static URL getUrl() {
        return VerilyLibraryType.class.getResource("/resources/verily.xml");
    }
}