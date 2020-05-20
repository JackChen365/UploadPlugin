## Summery

1. 创建task不能直接以闭包创建,需要以实现Task类方法创建.这样不会在apply方法体时,执行

```
class UploadPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        ...
        project.tasks.create([name: 'uploadAll', type: Upload, overwrite: true, group: 'MavenUpload']) {
            println "hello world!"
        }
        ...
        //更变为:
        project.tasks.create("uploadAll",UploadAllTask,repositories)
    }
}
```


> 异常在此

```
这里有两种定义方式

task A<<{
    println 'A task'
}
task B{
    println 'B task'

两种定义方式的区别是：B会在配置阶段执行，而A需要具体执行A任务或者作为其他任务的依赖才会执行。其实A的这种定义方式将在Gradle 5.0被移除，所以不建议使用了。我们可以通过指定doLast{}来指定。

```

2. 开启调试
> 命令行输入:
    (osx) ./gradlew clean -Dorg.gradle.debug=true --no-daemon
    (windows) gradlew clean -Dorg.gradle.debug=true --no-daemon
> 新建remote启动项


3. 关于上传任务,默认固定依赖此任务:

```
> Task :library2:preBuild UP-TO-DATE
> Task :library2:preReleaseBuild UP-TO-DATE
> Task :library2:compileReleaseAidl UP-TO-DATE
> Task :library2:compileReleaseRenderscript UP-TO-DATE
> Task :library2:checkReleaseManifest UP-TO-DATE
> Task :library2:generateReleaseBuildConfig UP-TO-DATE
> Task :library2:generateReleaseResValues UP-TO-DATE
> Task :library2:generateReleaseResources UP-TO-DATE
> Task :library2:packageReleaseResources UP-TO-DATE
> Task :library2:platformAttrExtractor UP-TO-DATE
> Task :library2:processReleaseManifest UP-TO-DATE
> Task :library2:generateReleaseRFile UP-TO-DATE
> Task :library2:prepareLintJar UP-TO-DATE
> Task :library2:generateReleaseSources UP-TO-DATE
> Task :library2:javaPreCompileRelease UP-TO-DATE
> Task :library2:compileReleaseJavaWithJavac UP-TO-DATE
> Task :library2:extractReleaseAnnotations UP-TO-DATE
> Task :library2:mergeReleaseConsumerProguardFiles UP-TO-DATE
> Task :library2:mergeReleaseShaders UP-TO-DATE
> Task :library2:compileReleaseShaders UP-TO-DATE
> Task :library2:generateReleaseAssets UP-TO-DATE
> Task :library2:packageReleaseAssets UP-TO-DATE
> Task :library2:packageReleaseRenderscript NO-SOURCE
> Task :library2:processReleaseJavaRes NO-SOURCE
> Task :library2:transformResourcesWithMergeJavaResForRelease UP-TO-DATE
> Task :library2:transformClassesAndResourcesWithSyncLibJarsForRelease UP-TO-DATE
> Task :library2:compileReleaseNdk NO-SOURCE
> Task :library2:mergeReleaseJniLibFolders UP-TO-DATE
> Task :library2:transformNativeLibsWithMergeJniLibsForRelease UP-TO-DATE
> Task :library2:transformNativeLibsWithSyncJniLibsForRelease UP-TO-DATE
> Task :library2:bundleRelease UP-TO-DATE 默认固定依赖此任务

> Task :library2:upload-library2-chenzhen
```


4. 自动打源码与文档任务,如果没有在最初应用,随后直接运行任务时,并不会运行

```
private void prepareAARArtifacts(Project project) {
    Javadoc androidJavadocs = project.getTasks().create([name: "androidJavadocs", type: Javadoc]) {
        source = project.android.sourceSets.main.java.srcDirs
        classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
    }
    Jar androidJavadocsJar = project.getTasks().create([name: "androidJavadocsJar", type: Jar, dependsOn: androidJavadocs]) {
        classifier = 'javadoc'
        from androidJavadocs.destinationDir
    }
    Jar androidSourcesJar = project.getTasks().create([name: "androidSourcesJar", type: Jar]) {
        classifier = 'sources'
        from project.android.sourceSets.main.java.srcDirs
    }

    project.artifacts {
        archives androidSourcesJar, androidJavadocsJar
    }
}

// 见此任务结果
project.tasks.create([name: taskName, type: Upload, overwrite: true, group: 'MavenUpload']) {
    //检测包类型
    String packageType="aar"
    //生成jar文件
    if (project.plugins.hasPlugin(LibraryPlugin)) {
        packageType="aar"
        prepareAARArtifacts(project)
    } else if (project.plugins.hasPlugin(JavaPlugin)) {
        packageType="jar"
        prepareJARArtifacts(project)
    }
    configuration = project.configurations.getByName('archives')

    repositories {
        mavenDeployer {
            repository(url: repositoryItem.url) {
                authentication(userName: repositoryItem.userName, password: repositoryItem.password)
            }
            snapshotRepository(url: repositoryItem.url) {
                authentication(userName: repositoryItem.userName, password: repositoryItem.password)
            }
            pom.project {
                ...
            }
        }
    }
}
```