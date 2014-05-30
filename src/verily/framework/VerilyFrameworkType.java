package verily.framework;

import com.intellij.framework.FrameworkType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: John
 * Date: 10/10/13
 * Time: 2:14 AM
 * To change this template use File | Settings | File Templates.
 */


public class VerilyFrameworkType extends FrameworkType {
    protected VerilyFrameworkType(@NotNull String id) {
        super(id);
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Verily Framework!";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/verily/verily_16x16.png");
    }
}
