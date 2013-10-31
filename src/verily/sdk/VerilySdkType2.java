package verily.sdk;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.JavaDependentSdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.HashMap;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import verily.ConfigureVerilySDK;
import verily.Constants;

import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.*;


public class VerilySdkType2 extends SdkType  implements  JavaSdkType{

    public static final VerilySdkType2 INSTANCE = new VerilySdkType2();
    private static final Icon VERILY_ICON = Constants.VERILY_ICON;

    public VerilySdkType2() {
        super("VERILY");
    }

    public String suggestHomePath() {
        File versionsRoot = null;

        if (SystemInfo.isLinux) {
//            versionsRoot = new File("/usr/lib");
//            if (!versionsRoot.isDirectory())
//                return null;
//            versions = versionsRoot.list(new FilenameFilter() {
//                public boolean accept(File dir, String name) {
//                    return name.toLowerCase().startsWith("ghc") && new File(dir, name).isDirectory();
//                }
//            });
//            append = null;
        } else if (SystemInfo.isWindows) {


            String progFiles = System.getenv("ProgramFiles(x86)");
            if (progFiles == null) {
                progFiles = System.getenv("ProgramFiles");
            }
            if (progFiles == null)
                return null;
//            versionsRoot = new File(progFiles, "Verily");
//
//            if(versionsRoot!=null){
//
//            }
//
//            if (!versionsRoot.isDirectory())
//                return progFiles;
//            versions = versionsRoot.list();
///            append = null;

            return progFiles;
        } else if (SystemInfo.isMac) {
            versionsRoot = new File("/Applications");
        } else {
            return null;
        }
        return versionsRoot.getAbsolutePath();
    }

    private static String getLatestVersion(String[] names) {
       return null;
    }

    public boolean isValidSdkHome(String path) {


//        ConfigureVerilySDK dialog = new ConfigureVerilySDK();
//        dialog.pack();
//
//        dialog.setVisible(true);


        File f = new File(path);

        if(f.isDirectory()){
            File verily = new File(f, "verily");
            if(verily.exists()){
                return true;
            }
        }

        return false;
    }

    public String suggestSdkName(String currentSdkName, String sdkHome) {
//        String suggestedName;
//        if (currentSdkName != null && currentSdkName.length() > 0) {
//            suggestedName = currentSdkName;
//        } else {
//            String versionString = getVersionString(sdkHome);
//            if (versionString != null) {
//                suggestedName = "GHC " + versionString;
//            } else {
//                suggestedName = "Unknown";
//            }
//        }
//        return suggestedName;

        return "Verily SDK";
    }

    private final Map<String, String> cachedVersionStrings = new HashMap<String, String>();

    public String getVersionString(String sdkHome) {
//        if (cachedVersionStrings.containsKey(sdkHome)) {
//            return cachedVersionStrings.get(sdkHome);
//        }
//        String versionString = getGhcVersion(sdkHome);
//        if (versionString != null && versionString.length() == 0) {
//            versionString = null;
//        }
//
//        if (versionString != null) {
//            cachedVersionStrings.put(sdkHome, versionString);
//        }

        Sdk s = ProjectJdkTable.getInstance().getAllJdks()[0];

        return s.getVersionString();
    }
//
//    @Nullable
//    public static String getGhcVersion(String homePath) {
//        if (homePath == null || !new File(homePath).isDirectory()) {
//            return null;
//        }
//        try {
//            String output = new ProcessLauncher(
//                    false, null,
//                    homePath + File.separator + "bin" + File.separator + "ghc",
//                    "--numeric-version"
//            ).getStdOut();
//            return output.trim();
//        } catch (Exception ex) {
//            // ignore
//        }
//        return null;
//    }

//    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
//        return new HaskellSdkConfigurable();
//    }
//
//    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
//        if (additionalData instanceof HaskellSdkAdditionalData) {
//            ((HaskellSdkAdditionalData) additionalData).save(additional);
//        }
//    }
//
//    @Override
//    public SdkAdditionalData loadAdditionalData(Element additional) {
//        return new HaskellSdkAdditionalData(additional);
//    }

    public String getPresentableName() {
        return "Verily";
    }

    @Override
    public Icon getIcon() {
        return VERILY_ICON;
    }

    @Override
    public Icon getIconForAddAction() {
        return getIcon();
    }

    @Override
    public String adjustSelectedSdkHome(String homePath) {
        return super.adjustSelectedSdkHome(homePath); // todo: if 'bin' or 'ghc' selected, choose parent folder
    }

    @Override
    public void setupSdkPaths(Sdk sdk) {

        ///Sdk s = ProjectJdkTable.getInstance().getAllJdks()[0];

    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRootTypeApplicable(OrderRootType type) {
        return false;
    }

    public static VerilySdkType2 getInstance() {
        return SdkType.findInstance(VerilySdkType2.class);
    }

    @Override
    public void saveAdditionalData(SdkAdditionalData sdkAdditionalData, Element element) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getBinPath(Sdk sdk) {
        Sdk s = ProjectJdkTable.getInstance().getAllJdks()[0];

        return ((JavaSdkType)s).getBinPath(s);
    }

    @Override
    public String getToolsPath(Sdk sdk) {
        Sdk s = ProjectJdkTable.getInstance().getAllJdks()[0];

        return ((JavaSdkType)s).getToolsPath(s);
    }

    @Override
    public String getVMExecutablePath(Sdk sdk) {
        Sdk s = ProjectJdkTable.getInstance().getAllJdks()[0];

        return ((JavaSdkType)s).getVMExecutablePath(s);
    }
}