package verily.sdk;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.ValidatableSdkAdditionalData;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.ValidatableSdkAdditionalData;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class VerilyAdditionalDataConfiguration implements ValidatableSdkAdditionalData {

    @NonNls private static final String JDK = "jdk";
    @NonNls private static final String BUILD_TARGET = "sdk";

    private String myJavaSdkName;
    //private final Sdk myAndroidSdk;
    private Sdk myJavaSdk;


    @Override
    public void checkValid(SdkModel sdkModel) throws ConfigurationException {
        if (getJavaSdk() == null) {
            throw new ConfigurationException("Please configure a valid JDK");
        }
    }

    public Object clone() throws CloneNotSupportedException {
        VerilyAdditionalDataConfiguration data = (VerilyAdditionalDataConfiguration)super.clone();
        data.setJavaSdk(getJavaSdk());
        return data;
    }

    @Nullable
    public Sdk getJavaSdk() {
        final ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        if (myJavaSdk == null) {
            if (myJavaSdkName != null) {
                myJavaSdk = jdkTable.findJdk(myJavaSdkName);
                myJavaSdkName = null;
            }
            else {
                for (Sdk jdk : jdkTable.getAllJdks()) {
                    if (VerilySdkUtils.isApplicableJdk(jdk)) {
                        myJavaSdk = jdk;
                        break;
                    }
                }
            }
        }
        return myJavaSdk;
    }

    public void setJavaSdk(final Sdk javaSdk) {
        myJavaSdk = javaSdk;
    }


    public void save(Element element) {
        final Sdk sdk = getJavaSdk();
        if (sdk != null) {
            element.setAttribute(JDK, sdk.getName());
        }
//        if (myBuildTarget != null) {
//            element.setAttribute(BUILD_TARGET, myBuildTarget);
//        }
    }



}
