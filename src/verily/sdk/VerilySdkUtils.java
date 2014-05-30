package verily.sdk;

import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: John
 * Date: 10/17/13
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class VerilySdkUtils {
    public static boolean isApplicableJdk(@NotNull com.intellij.openapi.projectRoots.Sdk jdk) {
        if (!(jdk.getSdkType() instanceof JavaSdk)) {
            return false;
        }
        JavaSdkVersion version = JavaSdk.getInstance().getVersion(jdk);
        return version == JavaSdkVersion.JDK_1_6 || version == JavaSdkVersion.JDK_1_8 || version == JavaSdkVersion.JDK_1_7;
    }

}
