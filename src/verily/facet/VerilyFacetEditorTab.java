package verily.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.util.PlatformIcons;
import com.intellij.util.SmartList;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


public class VerilyFacetEditorTab extends FacetEditorTab {

    private AdditionalPathPanel additionalPathPanel;

    public VerilyFacetEditorTab(@NotNull FacetEditorContext editorContext) {
        super();
        this.additionalPathPanel = new AdditionalPathPanel(editorContext);
    }

    @Override
    public void reset() {
        additionalPathPanel.reset();
    }

    @Override
    public boolean isModified() {
        return additionalPathPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        additionalPathPanel.apply();
        ApplicationManager.getApplication().getMessageBus().syncPublisher(VerilyFacetConfiguration.ADDITIONAL_PATHS_CHANGED).run();
    }

    @Override
    public JComponent createComponent() {
        return additionalPathPanel;
    }

    @Override
    public void disposeUIResources() {
        additionalPathPanel = null;
    }

    @Override
    @Nls
    public String getDisplayName() {
        return "Verily";
    }

    /**
     *
     */
    private static class AdditionalPathPanel extends JPanel {
        private final FacetEditorContext editorContext;
        private final VerilyFacet VerilyFacet;
        private DefaultListModel additionalPathModel = new DefaultListModel(); // List of VirtualFilePointer

        public AdditionalPathPanel(@NotNull FacetEditorContext editorContext) {
            super(new BorderLayout());
            this.editorContext = editorContext;
            this.VerilyFacet = (VerilyFacet) editorContext.getFacet();

            reset(); // fill current items into model

            final JBList listComponent = new JBList(additionalPathModel);
            listComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listComponent.setCellRenderer(new AdditionalPathListCellRenderer());
            listComponent.getEmptyText().setText("No additional paths defined");
            listComponent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        doEdit(listComponent.getSelectedIndex());
                    }
                }
            });
            JPanel panel = ToolbarDecorator.createDecorator(listComponent)
                    .setAddAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton button) {
                            doEdit(-1);
                        }
                    }).setEditAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton button) {
                            doEdit(listComponent.getSelectedIndex());
                        }
                    }).setRemoveAction(new AnActionButtonRunnable() {
                        @Override
                        public void run(AnActionButton button) {
                            ListUtil.removeSelectedItems(listComponent);
                        }
                    }).setToolbarPosition(ActionToolbarPosition.TOP).createPanel();
            UIUtil.addBorder(panel, IdeBorderFactory.createTitledBorder("Additional Resource Search Path", false));
            add(panel, BorderLayout.CENTER);
        }

        public void reset() {
            additionalPathModel.clear();
            for (VirtualFilePointer filePointer : VerilyFacet.getResourcePaths()) {
                additionalPathModel.addElement(filePointer);
            }
        }

        public boolean isModified() {
            List<VirtualFilePointer> originalList = VerilyFacet.getResourcePaths();
            if (originalList.size() != additionalPathModel.size()) {
                return true;
            }
            for (int i = 0, originalListSize = originalList.size(); i < originalListSize; i++) {
                if (!originalList.get(i).equals(additionalPathModel.get(i))) {
                    return true;
                }
            }
            return false;
        }

        public void apply() {
            List<VirtualFilePointer> list = new SmartList<VirtualFilePointer>();
            for (int i = 0, n = additionalPathModel.size(); i < n; i++) {
                list.add((VirtualFilePointer) additionalPathModel.get(i));
            }
            VerilyFacet.setResourcePaths(list);
        }

        private void doEdit(int index) {
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            fileChooserDescriptor.setHideIgnored(false);
            VirtualFile virtualFile = FileChooser.chooseFile(editorContext.getProject(), fileChooserDescriptor, index >= 0 ? ((VirtualFilePointer) additionalPathModel.get(index)).getFile() : null);
            if (virtualFile != null) {
                VirtualFilePointer filePointer = VirtualFilePointerManager.getInstance().create(virtualFile, VerilyFacet.getModule(), null);
                if (index >= 0) {
                    additionalPathModel.set(index, filePointer);
                } else {
                    additionalPathModel.addElement(filePointer);
                }
            }
        }
    }

    private static class AdditionalPathListCellRenderer extends ColoredListCellRenderer {
        @Override
        protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
            if (value instanceof VirtualFilePointer) {
                VirtualFilePointer filePointer = (VirtualFilePointer) value;
                setIcon(PlatformIcons.DIRECTORY_CLOSED_ICON);
                append(filePointer.getPresentableUrl(), filePointer.isValid() ? SimpleTextAttributes.REGULAR_ATTRIBUTES : SimpleTextAttributes.ERROR_ATTRIBUTES, null);
            }
        }
    }
}
