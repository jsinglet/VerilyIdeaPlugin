package verily.sdk;


import com.intellij.CommonBundle;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.JavaDependentSdkType;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.ui.OrderRoot;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import verily.Constants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VerilySdkType extends JavaDependentSdkType implements JavaSdkType {

    @NonNls public static final String SDK_NAME = "Verily SDK";
    @NonNls public static final String DEFAULT_EXTERNAL_DOCUMENTATION_URL = "http://verily.io/documentation/";

    public VerilySdkType() {
        super(SDK_NAME);
    }

    @Nullable
    @Override
    public String getBinPath(@NotNull Sdk sdk) {
        final Sdk internalJavaSdk = getInternalJavaSdk(sdk);
        return internalJavaSdk == null ? null : JavaSdk.getInstance().getBinPath(internalJavaSdk);
    }

    @Override
    @Nullable
    public String getToolsPath(@NotNull Sdk sdk) {
        final Sdk jdk = getInternalJavaSdk(sdk);
        if (jdk != null && jdk.getVersionString() != null) {
            return JavaSdk.getInstance().getToolsPath(jdk);
        }
        return null;
    }

    @Override
    @Nullable
    public String getVMExecutablePath(@NotNull Sdk sdk) {
        final Sdk internalJavaSdk = getInternalJavaSdk(sdk);
        return internalJavaSdk == null ? null : JavaSdk.getInstance().getVMExecutablePath(internalJavaSdk);
    }

    @Override
    public String suggestHomePath() {
        return null;
    }

    @Override
    public boolean isValidSdkHome(String path) {
        //return AndroidCommonUtils.createSdkManager(path, NullLogger.getLogger()) != null;
        return true;
    }

    @Override
    public String getVersionString(@NotNull Sdk sdk) {
        final Sdk internalJavaSdk = getInternalJavaSdk(sdk);
        return internalJavaSdk != null ? internalJavaSdk.getVersionString() : null;
    }

    @Nullable
    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return SDK_NAME;
    }


    @Override
    public boolean setupSdkPaths(Sdk sdk, SdkModel sdkModel) {
        final List<String> javaSdks = new ArrayList<String>();
        final Sdk[] sdks = sdkModel.getSdks();
        for (Sdk jdk : sdks) {
            if (VerilySdkUtils.isApplicableJdk(jdk)) {
                javaSdks.add(jdk.getName());
            }
        }

        if (javaSdks.isEmpty()){
            Messages.showErrorDialog("Please set up a valid Java 1.7+ JDK to use Verily", "No Java SDK Found");
            return false;
        }

//        MessageBuildingSdkLog log = new MessageBuildingSdkLog();
//        AndroidSdkData sdkData = AndroidSdkData.parse(sdk.getHomePath(), log);
//
//        if (sdkData == null) {
//            String errorMessage = log.getErrorMessage().length() > 0 ? log.getErrorMessage() : AndroidBundle.message("cannot.parse.sdk.error");
//            Messages.showErrorDialog(errorMessage, "SDK Parsing Error");
//            return false;
//        }
//
//        IAndroidTarget[] targets = sdkData.getTargets();
//
//        if (targets.length == 0) {
//            Messages.showErrorDialog(AndroidBundle.message("no.android.targets.error"), CommonBundle.getErrorTitle());
//            return false;
//        }

//        String[] targetNames = new String[targets.length];
//
//        String newestPlatform = null;
//        AndroidVersion version = null;
//
//        for (int i = 0; i < targets.length; i++) {
//            IAndroidTarget target = targets[i];
//            String targetName = AndroidSdkUtils.getTargetPresentableName(target);
//            targetNames[i] = targetName;
//            if (target.isPlatform() && (version == null || target.getVersion().compareTo(version) > 0)) {
//                newestPlatform = targetName;
//                version = target.getVersion();
//            }
//        }
//
        final VerilyNewSdkDialog dialog =
                new VerilyNewSdkDialog(null, javaSdks, javaSdks.get(0));
        dialog.show();

        if (!dialog.isOK()) {
            return false;
        }
        final String name = javaSdks.get(dialog.getSelectedJavaSdkIndex());
        final Sdk jdk = sdkModel.findSdk(name);
//        final IAndroidTarget target = targets[dialog.getSelectedTargetIndex()];
        final String sdkName = chooseNameForNewLibrary(null);

        setUpSdk(sdk, jdk, sdks,  true, sdkName);


        super.setupSdkPaths(sdk, sdkModel);

        return true;
    }


    public static String chooseNameForNewLibrary(String verilyTarget) {
        return "Verily Framework";
    }



    public static void setUpSdk(@NotNull Sdk verilySdk,
                                @Nullable Sdk javaSdk,
                                @NotNull Sdk[] allSdks,
                                boolean addRoots,
                                @NotNull String sdkName) {
        VerilyAdditionalDataConfiguration data = new VerilyAdditionalDataConfiguration();
        data.setJavaSdk(javaSdk);


        sdkName = SdkConfigurationUtil.createUniqueSdkName(sdkName, Arrays.asList(allSdks));

        final SdkModificator sdkModificator = verilySdk.getSdkModificator();

        sdkModificator.setName(sdkName);

        if (javaSdk != null) {
            sdkModificator.setVersionString(javaSdk.getVersionString());

            final SdkModificator jdkModificator = javaSdk.getSdkModificator();

            for(VirtualFile f : jdkModificator.getRoots(OrderRootType.CLASSES)){
                sdkModificator.addRoot(f, OrderRootType.CLASSES);
            }

        }
        sdkModificator.setSdkAdditionalData(data);

        sdkModificator.commitChanges();
    }


//    @NotNull
//    public static List<OrderRoot> getLibraryRootsForTarget(@NotNull IAndroidTarget target,
//                                                           @Nullable String sdkPath,
//                                                           boolean addPlatformAndAddOnJars) {
//        List<OrderRoot> result = new ArrayList<OrderRoot>();
//
//        if (addPlatformAndAddOnJars) {
//            for (VirtualFile file : getPlatformAndAddOnJars(target)) {
//                result.add(new OrderRoot(file, OrderRootType.CLASSES));
//            }
//        }
//
//        return result;
//    }



    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null; // return new AndroidSdkConfigurable(sdkModel, sdkModificator);
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData data, @NotNull Element e) {
        if (data instanceof VerilyAdditionalDataConfiguration) {
            ((VerilyAdditionalDataConfiguration)data).save(e);
        }

    }


    // you do get called.
    @Override
    public SdkAdditionalData loadAdditionalData(@NotNull Sdk currentSdk, Element additional) {
        return null; //return new AndroidSdkAdditionalData(currentSdk, additional);
    }

    @Override
    public String getPresentableName() {
        return "Verily SDK";

    }

    @Override
    public Icon getIcon() {
        return Constants.VERILY_ICON;
    }

    @Override
    public Icon getIconForAddAction() {
        return getIcon();
    }

    @Nullable
    @Override
    public String getDefaultDocumentationUrl(@NotNull Sdk sdk) {
        return DEFAULT_EXTERNAL_DOCUMENTATION_URL;
    }

    @Nullable
    private static Sdk getInternalJavaSdk(Sdk sdk) {
        final SdkAdditionalData data = sdk.getSdkAdditionalData();
        if (data instanceof VerilyAdditionalDataConfiguration) {
            return ((VerilyAdditionalDataConfiguration)data).getJavaSdk();
        }
        return null;
    }

    public static VerilySdkType getInstance() {
        return SdkType.findInstance(VerilySdkType.class);
    }
}
