package edu.wpi.first.gradlerio.wpi

import edu.wpi.first.gradlerio.wpi.dependencies.WPIDepsExtension
import edu.wpi.first.toolchain.NativePlatforms
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.internal.os.OperatingSystem
import edu.wpi.first.nativeutils.NativeUtilsExtension
import org.gradle.nativeplatform.NativeBinarySpec
import org.gradle.platform.base.VariantComponentSpec

import javax.inject.Inject

@CompileStatic
class WPIExtension {
    // WPILib (first.wpi.edu/FRC/roborio/maven) libs
    String wpilibVersion = "2020.3.2"
    String vmxVersion = "2020.3.2-vmxpi-5"
    String vmxPlatformVersion = "1.1.237"
    String niLibrariesVersion = "2020.10.1"
    String opencvVersion = "3.4.7-2"
    String imguiVersion = "1.72b-1"
    String ejmlVersion = "0.38"
    String jacksonVersion = "2.10.0"
    static final String[] validImageVersions = ['2020_v10']

    String wpilibYear = '2020'

    String googleTestVersion = "1.9.0-4-437e100-1"

    String jreArtifactLocation = "edu.wpi.first.jdk:roborio-2020:11.0.4u10-2"

    // WPILib (first.wpi.edu/FRC/roborio/maven) Utilities
    String smartDashboardVersion = "2020.3.2"
    String shuffleboardVersion = "2020.3.2"
    String outlineViewerVersion = "2020.3.2"
    String robotBuilderVersion = "2020.3.2"
    String pathWeaverVersion = "2020.3.2"

    WPIMavenExtension maven
    WPIDepsExtension deps

    String frcYear = '2020'

    NativePlatforms platforms;

    final Project project
    final String toolsClassifier

    NativeUtilsExtension ntExt;

    @Inject
    WPIExtension(Project project) {
        this.project = project
        def factory = project.objects
        maven = factory.newInstance(WPIMavenExtension, project)

        if (project.hasProperty('forceToolsClassifier')) {
            this.toolsClassifier = project.findProperty('forceToolsClassifier')
        } else {
            this.toolsClassifier = (
                    OperatingSystem.current().isWindows() ?
                            System.getProperty("os.arch") == 'amd64' ? 'win64' : 'win32' :
                            OperatingSystem.current().isMacOsX() ? "mac64" :
                                    OperatingSystem.current().isLinux() ? "linux64" :
                                            null
            )
        }

        platforms = new NativePlatforms();
        deps = factory.newInstance(WPIDepsExtension, project, this)
    }

    void maven(final Action<? super WPIMavenExtension> closure) {
      closure.execute(maven);
    }

    public void useLibrary(VariantComponentSpec component, String... libraries) {
        useRequiredLibrary(component, libraries)
    }

    public void useLibrary(NativeBinarySpec binary, String... libraries) {
        useRequiredLibrary(binary, libraries)
    }

    public void useRequiredLibrary(VariantComponentSpec component, String... libraries) {
        if (ntExt == null) {
            ntExt = project.extensions.getByType(NativeUtilsExtension)
        }
        ntExt.useRequiredLibrary(component, libraries)
    }

    public void useRequiredLibrary(NativeBinarySpec binary, String... libraries) {
        if (ntExt == null) {
            ntExt = project.extensions.getByType(NativeUtilsExtension)
        }
        ntExt.useRequiredLibrary(binary, libraries)
    }

    public void useOptionalLibrary(VariantComponentSpec component, String... libraries) {
        if (ntExt == null) {
            ntExt = project.extensions.getByType(NativeUtilsExtension)
        }
        ntExt.useOptionalLibrary(component, libraries)
    }

    public void useOptionalLibrary(NativeBinarySpec binary, String... libraries) {
        if (ntExt == null) {
            ntExt = project.extensions.getByType(NativeUtilsExtension)
        }
        ntExt.useOptionalLibrary(binary, libraries)
    }

    private String frcHomeCache

    String getFrcHome() {
        if (frcHomeCache != null) {
            return this.frcHomeCache
        }
        String frcHome = ''
        if (OperatingSystem.current().isWindows()) {
            String publicFolder = System.getenv('PUBLIC')
            if (publicFolder == null) {
                publicFolder = "C:\\Users\\Public"
            }
            def homeRoot = new File(publicFolder, "wpilib")
            frcHome = new File(homeRoot, this.frcYear).toString()
        } else {
            def userFolder = System.getProperty("user.home")
            def homeRoot = new File(userFolder, "wpilib")
            frcHome = new File(homeRoot, this.frcYear).toString()
        }
        frcHomeCache = frcHome
        return frcHomeCache
    }

    Map<String, Tuple> versions() {
        // Format:
        // property: [ PrettyName, Version, RecommendedKey ]
        return [
                "wpilibVersion"        : new Tuple("WPILib", wpilibVersion, "wpilib"),
                "opencvVersion"        : new Tuple("OpenCV", opencvVersion, "opencv"),
                "wpilibYear"           : new Tuple("WPILib Year", wpilibYear, "wpilibYear"),
                "googleTestVersion"    : new Tuple("Google Test", googleTestVersion, "googleTest"),
                "imguiVersion"         : new Tuple("ImGUI", imguiVersion, "imgui"),
                "ejmlVersion"          : new Tuple("EJML", ejmlVersion, "ejml"),
                "jacksonVersion"       : new Tuple("Jackson", jacksonVersion, "jackson"),

                "smartDashboardVersion": new Tuple("SmartDashboard", smartDashboardVersion, "smartdashboard"),
                "shuffleboardVersion"  : new Tuple("Shuffleboard", shuffleboardVersion, "shuffleboard"),
                "outlineViewerVersion" : new Tuple("OutlineViewer", outlineViewerVersion, "outlineviewer"),
                "robotBuilderVersion"  : new Tuple("RobotBuilder", robotBuilderVersion, "robotbuilder"),
        ]
    }
}
