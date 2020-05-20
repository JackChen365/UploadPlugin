package com.cz.upload.plugin.task

import com.cz.upload.plugin.extension.UploadConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

/**
 * 上传任务信息打印
 */
class UploadPrintTask extends DefaultTask {
    /**
     * 项目所有配置信息
     */
    private UploadConfiguration configuration
    @Inject
    UploadPrintTask(UploadConfiguration configuration) {
        //记录上传仓库信息
        this.configuration=configuration
        //设置分组
        setGroup("MavenUpload")
    }

    @TaskAction
    void run() {
        StringBuilder output=new StringBuilder()
        println("+-----------------------------------------------------------------------------+")
        println("|                           Configuration Information                         |")
        println("+-----------------------------------------------------------------------------+")
        output.append("maven {\n")
        //遍历配置仓库信息
        configuration.repositories.each { repository->
            output.append("\trepository {\n")
            output.append("\t\turl \"${repository.url}\"\n")
            output.append("\t\tuserName \"${repository.userName}\"\n")
            output.append("\t\tuserPassword { \"${repository.password}\" }\n")

            //当前项目module
            if(repository.modules){
                repository.modules.each { module->
                    output.append("\t\tmodule {\n")
                    output.append("\t\t\tgroupId \"${module.groupId}\"\n")
                    output.append("\t\t\tartifactId \"${module.artifactId}\"\n")
                    output.append("\t\t\tversion \"${module.version}\"\n")
                    output.append("\t\t\tdescription \"${module.description}\"\n")

                    //加入策略信息
                    if(module.dependencyFlavors){
                        module.dependencyFlavors.each { strategy->
                            output.append("\t\t\tdependencyStrategy {\n")
                            output.append("\t\t\t\tname \"${strategy.name}\"\n")
                            strategy.dependencies.each { dependency->
                                output.append("\t\t\t\timplementation \"${dependency}\"\n")
                            }
                            output.append("\t\t\t}\n")
                        }
                    }
                    output.append("\t\t}\n")
                }
            }
            //加入
            if(repository.scm){
                output.append("\t\tscm {\n")
                output.append("\t\t\turl \"${repository.scm.url}\"\n")
                output.append("\t\t\tconnection \"${repository.scm.connection}\"\n")
                output.append("\t\t\tdeveloperConnection \"${repository.scm.developerConnection}\"\n")
                output.append("\t\t}\n")
            }
            if(repository.license){
                output.append("\t\tlicense {\n")
                output.append("\t\t\turl \"${repository.license.url}\"\n")
                output.append("\t\t\tname \"${repository.license.name}\"\n")
                output.append("\t\t\tdistribution \"${repository.license.distribution()}\"\n")
                output.append("\t\t}\n")
            }
            if(repository.developer){
                output.append("\t\tdeveloper {\n")
                output.append("\t\t\tid \"${repository.developer.id}\"\n")
                output.append("\t\t\tname \"${repository.developer.name}\"\n")
                output.append("\t\t\temail \"${repository.developer.email}\"\n")
                output.append("\t\t}\n")
            }
            output.append("\t}\n")
        }
        output.append("}\n")
        println output.toString()

        println("+-----------------------------------------------------------------------------+")
    }
}