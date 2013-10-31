package verily.run;

import com.intellij.execution.*;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SimpleJavaSdkType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.PathUtil;
import com.intellij.util.PathsList;
import com.intellij.util.SystemProperties;
import com.intellij.util.net.HttpConfigurable;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import verily.Constants;
import verily.sdk.VerilySdkType;

import javax.swing.*;
import java.util.*;

public class VerilyRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule>   {

    private VerilyRunConfigurationEditor editor;
    private VerilyConfigurationFactory factory;
    private Integer port;
    private String jvmOptions;
    private boolean reloadDeps;
    private boolean launchBrowser;
    private boolean dynamicallyReloadClasses;
    private String workDir;
    public boolean passParentEnv = true;

    private final Map<String, String> envs = new LinkedHashMap<String, String>();



    public VerilyRunConfiguration(VerilyConfigurationFactory factory, Project project, String name) {
        super(name, new RunConfigurationModule(project), factory);
        workDir = PathUtil.getLocalPath(project.getBaseDir());
        port = 8000;
        jvmOptions = "";
        reloadDeps = true;
        launchBrowser = true;
        dynamicallyReloadClasses = true;
        this.factory = factory;

        // needed to find the configuration module
        //getConfigurationModule().init();


    }


    public void setWorkDir(String dir) {
        workDir = dir;
    }

    public String getWorkDir() {
        return workDir;
    }


    @Nullable
    public Module getModule() {
        //TODO figure out why this doesn't happen automatically
        getConfigurationModule().init();
        return getConfigurationModule().getModule();
    }

    public Collection<Module> getValidModules() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();


        ArrayList<Module> res = new ArrayList<Module>();
        for (Module module : modules) {
            if (isValidModule(module)) {
                res.add(module);
            }
        }
        return res;
    }


    private boolean isValidModule(Module m){
        return true;
    }

    public void readExternal(Element element) throws InvalidDataException {
        PathMacroManager.getInstance(getProject()).expandPaths(element);
        super.readExternal(element);
        readModule(element);
        //scriptPath = ExternalizablePath.localPathValue(JDOMExternalizer.readString(element, "path"));
        try {
            port = Integer.parseInt(JDOMExternalizer.readString(element, "port"));
        }catch(NumberFormatException e){
            port = 8000;
        }
        jvmOptions = JDOMExternalizer.readString(element, "jvmOptions");
//        final String wrk = JDOMExternalizer.readString(element, "workDir");
//        if (!".".equals(wrk)) {
//            workDir = ExternalizablePath.localPathValue(wrk);
//        }
        dynamicallyReloadClasses = Boolean.parseBoolean(JDOMExternalizer.readString(element, "dynamicallyReloadClasses"));
        reloadDeps               = Boolean.parseBoolean((JDOMExternalizer.readString(element, "reloadDeps")));
        launchBrowser            = Boolean.parseBoolean(JDOMExternalizer.readString(element, "launchBrowser"));
        envs.clear();
        JDOMExternalizer.readMap(element, envs, null, "env");
    }

    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        writeModule(element);

        //JDOMExternalizer.write(element, "path", ExternalizablePath.urlValue(scriptPath));

        JDOMExternalizer.write(element, "port", port);
        JDOMExternalizer.write(element, "jvmOptions", jvmOptions);

        //JDOMExternalizer.write(element, "workDir", ExternalizablePath.urlValue(workDir));

        JDOMExternalizer.write(element, "dynamicallyReloadClasses", dynamicallyReloadClasses);
        JDOMExternalizer.write(element, "launchBrowser", launchBrowser);
        JDOMExternalizer.write(element, "reloadDeps", reloadDeps);

        JDOMExternalizer.writeMap(element, envs, null, "env");
        PathMacroManager.getInstance(getProject()).collapsePathsRecursively(element);
    }

    @Override
    protected ModuleBasedConfiguration createInstance() {
        return new VerilyRunConfiguration(factory, getConfigurationModule().getProject(), getName());
    }


    public RunProfileState getState(@NotNull Executor executor, @NotNull final ExecutionEnvironment environment) throws ExecutionException {


        GeneralCommandLine f;

        //
        // Check that sdk is a Verily SDK
        //
        if(ProjectRootManager.getInstance(getProject()).getProjectSdk().getSdkType() == null || ProjectRootManager.getInstance(getProject()).getProjectSdk().getSdkType().toString().equals(VerilySdkType.SDK_NAME)==false){
            throw new CantRunException("To run this project you must set the SDK Type to a Valid Verily SDK");
        }

        final Module module = getModule();

        return new CommandLineState(environment) {
            @NotNull
            @Override
            protected OSProcessHandler startProcess() throws ExecutionException {

                setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject()));

                List<String> args = new ArrayList<String>();

                // build up the command line
                VerilyRunConfiguration config = (VerilyRunConfiguration)environment.getRunnerAndConfigurationSettings().getConfiguration();
                String homeDir = ProjectRootManager.getInstance(getProject()).getProjectSdk().getHomePath();

                // the executable
                if(SystemInfo.isWindows)
                    args.add(String.format("%s/verily.bat", homeDir));
                else
                    args.add(String.format("%s/verily", homeDir));


                // we are running, so add the -run option
                args.add("-run");
                // limit to one thread
                args.add("-n");
                args.add("1");

                // port
                args.add(config.getPort().toString());

                // hot reload?
                if(config.isDynamicallyReloadClasses()){
                    args.add("-w");
                }
                // this is on by default
                if(config.isReloadDeps()==false){
                    args.add("-fast");
                }

                GeneralCommandLine gcl = new GeneralCommandLine(args);

                gcl.setWorkDirectory(getProject().getBasePath());

                OSProcessHandler handler =  new OSProcessHandler(gcl.createProcess());

                ProcessTerminatedListener.attach(handler, getProject(), "Framework shutdown complete.");

                return handler;

        }
        };

    }


    public void configureCommandLine(JavaParameters params, @Nullable Module module, boolean tests, VirtualFile script, VerilyRunConfiguration configuration) throws CantRunException {

        configureGenericRunner(params, module, "VerilyMain", false, tests);

    //    params.getVMParametersList().addParametersString(configuration.getVMParameters());


//        if (configuration.isDebugEnabled()) {
//            params.getProgramParametersList().add("--debug");
//        }

//        params.getProgramParametersList().add(FileUtil.toSystemDependentName(configuration.getScriptPath()));
//        params.getProgramParametersList().addParametersString(configuration.getScriptParameters());
//
    }

    public static void configureGenericRunner(@NotNull JavaParameters params,
                                                    @NotNull Module module,
                                                    @NotNull String mainClass,
                                                    boolean useBundled,
                                                    boolean tests) throws CantRunException {

//        final VirtualFile groovyJar = findGroovyJar(module);
//        if (useBundled) {
//            params.getClassPath().add(GroovyUtils.getBundledGroovyJar());
//        }
//        else if (groovyJar != null) {
//            params.getClassPath().add(groovyJar);
//        }
//
        getClassPathFromRootModel(module, tests, params, true, params.getClassPath());
//
//        setToolsJar(params);

//        String groovyHome = useBundled ? FileUtil.toCanonicalPath(GroovyUtils.getBundledGroovyJar().getParentFile().getParent()) : LibrariesUtil.getGroovyHomePath(module);
//        if (groovyHome != null) {
//            groovyHome = FileUtil.toSystemDependentName(groovyHome);
//        }
//        if (groovyHome != null) {
//            setGroovyHome(params, groovyHome);
//        }
//
//        final String confPath = getConfPath(groovyHome);
//        params.getVMParametersList().add("-Dgroovy.starter.conf=" + confPath);
        params.getVMParametersList().addAll(HttpConfigurable.convertArguments(HttpConfigurable.getJvmPropertiesList(false, null)));

        params.setMainClass(mainClass);

//        params.getProgramParametersList().add("--conf");
//        params.getProgramParametersList().add(confPath);
//
//        params.getProgramParametersList().add("--main");
//        params.getProgramParametersList().add(mainClass);

//        if (params.getVMParametersList().getPropertyValue(GroovycOSProcessHandler.GRAPE_ROOT) == null) {
//            String sysRoot = System.getProperty(GroovycOSProcessHandler.GRAPE_ROOT);
//            if (sysRoot != null) {
//                params.getVMParametersList().defineProperty(GroovycOSProcessHandler.GRAPE_ROOT, sysRoot);
//            }
//        }
    }


    @Nullable
    public static PathsList getClassPathFromRootModel(Module module,
                                                      boolean isTests,
                                                      JavaParameters params,
                                                      boolean allowDuplication,
                                                      PathsList pathList)
            throws CantRunException {
        if (module == null) {
            return null;
        }

        final JavaParameters tmp = new JavaParameters();
        tmp.configureByModule(module, isTests ? JavaParameters.CLASSES_AND_TESTS : JavaParameters.CLASSES_ONLY);
        if (tmp.getClassPath().getVirtualFiles().isEmpty()) {
            return null;
        }

        Set<VirtualFile> core = new HashSet<VirtualFile>(params.getClassPath().getVirtualFiles());

        for (VirtualFile virtualFile : tmp.getClassPath().getVirtualFiles()) {
            if (allowDuplication || !core.contains(virtualFile)) {
                pathList.add(virtualFile);
            }
        }
        return pathList;
    }

    public static JavaParameters createJavaParametersWithSdk(@Nullable Module module) {
        JavaParameters params = new JavaParameters();
        params.setCharset(null);

        if (module != null) {
            final Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
            if (sdk != null && sdk.getSdkType() instanceof JavaSdkType) {
                params.setJdk(sdk);
            }
        }
        if (params.getJdk() == null) {
            params.setJdk(new SimpleJavaSdkType().createJdk("tmp", SystemProperties.getJavaHome()));
        }
        return params;
    }



    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        Module[] modules = ModuleManager.getInstance(getProject()).getModules();
        editor = new VerilyRunConfigurationEditor(modules);
        return editor;
    }


    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {

        //
        // We don't validate the JVM options but we check that the port number is indeed a valid integer.
        //
        if(port==null || port.intValue() < 8000){
            throw new RuntimeConfigurationException("Invalid port. Please select a numeric port above (or equal to) 8000");
        }

    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public boolean isReloadDeps() {
        return reloadDeps;
    }

    public void setReloadDeps(boolean reloadDeps) {
        this.reloadDeps = reloadDeps;
    }

    public boolean isLaunchBrowser() {
        return launchBrowser;
    }

    public void setLaunchBrowser(boolean launchBrowser) {
        this.launchBrowser = launchBrowser;
    }

    public boolean isDynamicallyReloadClasses() {
        return dynamicallyReloadClasses;
    }

    public void setDynamicallyReloadClasses(boolean dynamicallyReloadClasses) {
        this.dynamicallyReloadClasses = dynamicallyReloadClasses;
    }




    ///
    /// "Important" methods.
    ///

    /*@Override
    public void setVMParameters(String s) {
       jvmOptions = s;
    }

    @Override
    public String getVMParameters() {
        return jvmOptions;
    }

    @Override
    public boolean isAlternativeJrePathEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAlternativeJrePathEnabled(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAlternativeJrePath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAlternativeJrePath(String path) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public String getRunClass() {
        return null;
    }

    @Nullable
    @Override
    public String getPackage() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setProgramParameters(@Nullable String s) {
    }

    @Nullable
    @Override
    public String getProgramParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setWorkingDirectory(@Nullable String value) {
        workDir = value;
    }

    @Override
    public String getWorkingDirectory() {
        return workDir;
    }

    @Override
    public void setEnvs(@NotNull Map<String, String> envs) {
        this.envs.clear();
        this.envs.putAll(envs);
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return envs;
    }

    @Override
    public void setPassParentEnvs(boolean passParentEnvs) {
        this.passParentEnv = passParentEnvs;
    }

    @Override
    public boolean isPassParentEnvs() {
        return passParentEnv;
    }          */






}
