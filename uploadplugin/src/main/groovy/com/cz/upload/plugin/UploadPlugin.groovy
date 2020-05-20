package com.cz.upload.plugin

import com.android.build.gradle.LibraryPlugin
import com.cz.upload.plugin.extension.UploadConfiguration
import com.cz.upload.plugin.task.UploadPrintTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Upload
import org.gradle.api.tasks.bundling.Jar

/**
 * 上传插件
 */
class UploadPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //如果是主项目
        if(project.rootProject==project){
            //进入插件开始执行
            println("> Apply upload plugin")
            //上传配置
            def uploadConfiguration = project.extensions.create("maven",UploadConfiguration,project)
            project.allprojects { childProject->
                childProject.afterEvaluate {
                    //添加打印配置任务
                    childProject.tasks.create("uploadInformation", UploadPrintTask,uploadConfiguration)
                    //添加上传任务
                    applyProjectUploadConfiguration(childProject,uploadConfiguration)
                }
            }
        }
    }

    /**
     * 应用上传信息
     * @param project
     */
    void applyProjectUploadConfiguration(Project project,UploadConfiguration uploadConfiguration){
        //获得所有配置仓库信息
        def repositories = uploadConfiguration.getRepositories()
        repositories.each { repository->
            //当前配置上传仓库信息
            def modules = repository.getModules()
            modules.each { module->
                //如果有需要操作仓库
                if(project.name==module.name){
                    //检测项目类型
//                            if(project.plugins.hasPlugin(JavaPlugin)||
//                                    project.plugins.hasPlugin(LibraryPlugin)){
                    //应用当前插件
                    project.plugins.apply('maven')
                    project.plugins.apply('com.cz.upload.plugin')
                    //为每个仓库，不同策略，生成不同的task
                    createUploadTask(project,module.artifactId+"-"+repository.name,repository,modules,module,null)
                    //上传策略
                    def dependencyFlavors = module.getDependencyFlavors()
                    //根据不同依赖设定,添加task
                    dependencyFlavors.each { dependencyFlavor->
                        //生成上传任务
                        createUploadTask(project,module.artifactId+"-"+repository.name+"-"+dependencyFlavor.name,repository,module,dependencyFlavor)
                    }
//                            } else {
//                                //当前不是一个仓库
//                                System.err.println("> $projectName is not a Java module or Android library module!")
//                            }
                }
            }
        }
    }


    /**
     * 开始上传信息
     * @param project
     */
    void createUploadTask(Project project, String taskName, UploadConfiguration.Repository repositoryItem,
                          List<UploadConfiguration.Repository.Module> moduleList,
                          UploadConfiguration.Repository.Module module,
                          UploadConfiguration.Repository.Module.DependencyFlavor dependencyFlavor) {
        // upload类型的task,会自动依赖
        project.tasks.create([name: taskName, type: Upload, overwrite: true, group: 'MavenUpload']) {
            doFirst {
                applyConfiguration(project, repositoryItem, module, dependencyFlavor)
            }
            //检测包类型
            String packageType="aar"
            //生成jar文件
            if (project.plugins.hasPlugin(LibraryPlugin)) {
                packageType="aar"
                prepareAARArtifacts(project,taskName)
            } else if (project.plugins.hasPlugin(JavaPlugin)) {
                packageType="jar"
                prepareJARArtifacts(project,taskName)
            }
            configuration = project.configurations.getByName('archives')
            //开始上传
            repositories {
                mavenDeployer {
                    repository(url: repositoryItem.url) {
                        authentication(userName: repositoryItem.userName, password: repositoryItem.password)
                    }
                    snapshotRepository(url: repositoryItem.url) {
                        authentication(userName: repositoryItem.userName, password: repositoryItem.password)
                    }
                    pom.project {
                        //项目信息
                        version module.version
                        //如果应用了不同策略.将更改名称
                        if(dependencyFlavor){
                            artifactId module.artifactId+"-"+dependencyFlavor.name
                        } else {
                            artifactId module.artifactId
                        }
                        groupId module.groupId
                        description module.description
                        packaging packageType
                        if (repositoryItem.scm) {
                            scm {
                                url repositoryItem.scm.url
                                connection repositoryItem.scm.connection
                                developerConnection repositoryItem.scm.developerConnection
                            }
                        }
                        //授权信息
                        if (repositoryItem.license) {
                            licenses {
                                license {
                                    name repositoryItem.license.name
                                    url repositoryItem.license.url
                                    distribution repositoryItem.license.distribution
                                }
                            }
                        }
                        //开发者信息
                        if (repositoryItem.developer) {
                            developers {
                                developer {
                                    id repositoryItem.developer.id
                                    name repositoryItem.developer.name
                                    email repositoryItem.developer.email
                                }
                            }
                        }
                    }
                    //1. 检测是否有本地依赖,在此替换为线上依赖
                    pom.whenConfigured {pom ->
                        pom.dependencies.each{ details->
                            if(details.version=="unspecified" && details.groupId==project.rootProject.name){
                                //查找在线依赖
                                final UploadConfiguration.Repository.Module dependencyModule=moduleList.find { it.name == details.artifactId }
                                //动态修改依赖
                                if(dependencyModule){
                                    details.groupId=dependencyModule.groupId
                                    details.artifactId=dependencyModule.artifactId
                                    details.version=dependencyModule.version
                                }
                            }
                        }
                    }
                    //2. 检测是否有多版本配置
                    if(dependencyFlavor){
                        pom.whenConfigured {pom ->
                            pom.dependencies.each{ details->
                                final UploadConfiguration.Repository.Module.DependencyFlavor.Dependency dependency=dependencyFlavor.dependencies.find { it.artifactId == details.artifactId }
                                if(dependency){
                                    details.version=dependency.version
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 应用配置
     * @param p
     */
    private void applyConfiguration(Project project,UploadConfiguration.Repository repositoryItem, UploadConfiguration.Repository.Module module, UploadConfiguration.Repository.Module.DependencyFlavor dependencyFlavor){
        println("> 应用配置")
        //打印上传信息
        String packageType="aar"
        //生成jar文件
        if (project.plugins.hasPlugin(LibraryPlugin)) {
            packageType="aar"
        } else if (project.plugins.hasPlugin(JavaPlugin)) {
            packageType="jar"
        }
        println("> 当前包类型:${packageType}")
        println("> 上传地址:${repositoryItem.url}")
        println("> 上传用户:${repositoryItem.userName}")
//        println("> 上传密码:${repositoryItem.password}")
        println("> 项目地址:${module}")
        if(dependencyFlavor){
            dependencyFlavor.dependencies.each {
                println("> 更改依赖:${it}")
            }
        }
    }

    private void prepareJARArtifacts(Project project,String taskName) {
        Task classesTask = project.tasks.getByName('classes')
        Jar sourcesJar = project.task("${project.name}-$taskName-sourcesJar", type: Jar, dependsOn: classesTask) {
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }
        project.artifacts {
            archives sourcesJar
        }
    }

    private void prepareAARArtifacts(Project project,String taskName) {
        def androidSourcesJar = project.tasks.create([name: "${project.name}-$taskName-sourcesJar", type: Jar]) {
            classifier = 'sources'
            from project.android.sourceSets.main.java.srcDirs
        }
        project.artifacts {
            archives androidSourcesJar
        }
    }

}
