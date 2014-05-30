package verily.util;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaDependentSdkType;
import com.intellij.openapi.util.SystemInfo;
import verily.sdk.VerilySdkType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: John
 * Date: 10/18/13
 * Time: 12:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class VerilyExeUtil {


    public static String openCmd(){
        if(SystemInfo.isWindows){
            return "start";
        }

        //todo: Linux? Oh wait, there's no standard for doing something this simple. Great.

        return "open";
    }

    public static  ProcessBuilder initProject(Sdk sdk, String name){

        String verilyRoot = sdk.getHomePath();
        String java = new VerilySdkType().getVMExecutablePath(sdk);


        //-z3 "C:\Program Files\Verily\tools\z3-4.3.0-win" -jml "C:\Program Files\Verily\tools\openjml"

        if(SystemInfo.isWindows){
            return  new ProcessBuilder(
                    "java",
                    "-classpath",
                    String.format("\".;%s\\tools\\openjml-head\\jmlruntime.jar;%s\\lib\\*\"", verilyRoot, verilyRoot),
                    "VerilyMain",

                    "-z3", String.format("\"%s\\tools\\z3-4.3.0-win\"", verilyRoot),
                    "-jml", String.format("\"%s\\tools\\openjml\"", verilyRoot),

                    "-init", String.format("./%s", name)
            );
        }else{
            return  new ProcessBuilder(String.format("%s/verily", verilyRoot), "-init", String.format("./%s", name));
        }
    }

    public static  ProcessBuilder newPair(Sdk sdk, String name){

        String verilyRoot = sdk.getHomePath();
        String java = new VerilySdkType().getVMExecutablePath(sdk);


        if(SystemInfo.isWindows){
            return  new ProcessBuilder("java", "-classpath", String.format("\".;%s\\lib\\*\"", verilyRoot.replaceAll("/", "\\\\")), "VerilyMain", "-new", name);
        }else{
            return  new ProcessBuilder(String.format("%s/verily", verilyRoot), "-new", name);
        }
    }


    public static  ProcessBuilder run(Sdk sdk, String name){

        String verilyRoot = sdk.getHomePath();
//        String java = new VerilySdkType().getVMExecutablePath(sdk);


        if(SystemInfo.isWindows){
            return  new ProcessBuilder("java",
                    "-classpath",
                    String.format("\".;%s\\tools\\openjml-head\\jmlruntime.jar;%s\\lib\\*\"", verilyRoot, verilyRoot),

                    "VerilyMain",

                    "-z3", String.format("\"%s\\tools\\z3-4.3.0-win\"", verilyRoot),
                    "-jml", String.format("\"%s\\tools\\openjml\"", verilyRoot),

                    "-run");
        }else{
            return  new ProcessBuilder(String.format("%s/verily", verilyRoot), "-run");
        }
    }

    public static void main(String[] args) throws IOException {

        Process p = new ProcessBuilder("mvn.bat", "help").redirectErrorStream(true).start();

        InputStream is = p.getInputStream();

        InputStreamReader isr = new InputStreamReader(is);

        int c = isr.read();

        while (c != -1) {
            System.out.write(c);
            c = isr.read();
        }

        is.close();


    }

}
