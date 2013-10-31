package verily.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.impl.ui.FacetEditorsFactoryImpl;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.framework.library.DownloadableLibraryService;
import com.intellij.util.messages.Topic;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;


public class VerilyFacetConfiguration implements FacetConfiguration {

    private static final String RESOURCEURLS_TAG = "resourceUrls";
    private static final String RESOURCEURL_TAG = "resourceUrl";
    private static final String URL = "url";

    public static final Topic<Runnable> ADDITIONAL_PATHS_CHANGED = new Topic<Runnable>("additional resource paths changed", Runnable.class);

    List<String> resourceUrls = new ArrayList<String>();

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
//        validatorsManager.registerValidator(FacetEditorsFactoryImpl.getInstanceImpl().createLibraryValidator(
//                DownloadableLibraryService.getInstance().createDescriptionForType(WicketLibraryType.class),
//                editorContext,
//                validatorsManager,
//                "verily"
//        ));

        return new FacetEditorTab[]{new VerilyFacetEditorTab(editorContext)};
    }

    @Override
    @Deprecated
    public void readExternal(Element element) {
        Element resourceUrlsElement = element.getChild(RESOURCEURLS_TAG);
        if (resourceUrlsElement != null) {
            List resourceUrlsChildren = resourceUrlsElement.getChildren(RESOURCEURL_TAG);
            if (resourceUrlsChildren != null) {
                for (Object child : resourceUrlsChildren) {
                    if (child instanceof Element) {
                        resourceUrls.add(((Element) child).getAttributeValue(URL));
                    }
                }
            }
        }
    }

    @Override
    @Deprecated
    public void writeExternal(Element element) {
        Element resourceUrlsElement = new Element(RESOURCEURLS_TAG);
        element.addContent(resourceUrlsElement);
        for (String resourceUrl : resourceUrls) {
            Element child = new Element(RESOURCEURL_TAG);
            child.setAttribute(URL, resourceUrl);
            resourceUrlsElement.addContent(child);
        }
    }
}