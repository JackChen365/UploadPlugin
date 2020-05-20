package com.cz.upload.plugin.extension

import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
/**
 * 仓库所有上传信息
 */
class UploadConfiguration {
    Project project
    /**
     * 所有上传仓库信息
     */
    private List<Repository> repositories=[]

    UploadConfiguration(Project project) {
        this.project = project
    }

    /**
     * 一个仓库信息
     */
    void repository(Closure closure){
        //映射信息到闭包
        Repository repository = new Repository()
        ConfigureUtil.configure(closure, repository)
        repositories.add(repository)
    }


    List<Repository> getRepositories(){
        return repositories
    }


    /**
     * 配置仓库信息
     */
    class Repository{
        /**
         * 仓库名称
         */
        private String name
        /**
         * 连接服务器名称
         */
        private String url
        /**
         * 连接名称
         */
        private String userName
        /**
         * 密码
         */
        private String password
        /**
         * scm信息
         */
        private Scm scm
        /**
         * 授权信息
         */
        private License license
        /**
         * 开发者信息
         */
        private Developer developer
        /**
         * 此仓库下
         */
        private List<Module> modules=[]
        /**
         * 用户密码
         * @param closure
         * @return
         */
        String userPassword(Closure closure){
            password=closure()
        }
        void name(String name){
            this.name=name
        }

        void url(String url){
            this.url=url
        }

        void userName(String userName){
            this.userName=userName
        }


        /**
         * 一个项目信息
         */
        def module(Closure closure){
            //映射信息到闭包
            Module module = new Module()
            ConfigureUtil.configure(closure, module)
            modules.add(module)
        }

        /**
         * 一个项目信息
         */
        def scm(Closure closure){
            //映射信息到闭包
            ConfigureUtil.configure(closure, scm)
        }

        /**
         * 项目授权信息
         */
        def license(Closure closure){
            //映射信息到闭包
            ConfigureUtil.configure(closure, license)
        }

        /**
         * 一个项目信息
         */
        def developer(Closure closure){
            //映射信息到闭包
            ConfigureUtil.configure(closure, developer)
        }

        List<Module> getModules(){
            return modules
        }

        String getName(){
            return name
        }

        String getUrl() {
            return url
        }

        String getUserName() {
            return userName
        }

        String getPassword() {
            return password
        }

        Scm getScm() {
            return scm
        }

        License getLicense() {
            return license
        }

        Developer getDeveloper() {
            return developer
        }

        /**
         * 项目信息
         */
        static class Module{
            //库项目名称
            private String name
            //版本信息
            private String version
            //group组信息
            private String groupId
            //库名称
            private String artifactId
            //描述信息
            private String description


            /**
             * 仓库的依赖策略
             */
            private List<DependencyFlavor> dependencyFlavors =[]

            /**
             * 项目依赖信息
             */
            def dependencyFlavor(Closure closure){
                //映射信息到闭包
                DependencyFlavor dependencyFlavor = new DependencyFlavor()
                ConfigureUtil.configure(closure, dependencyFlavor)
                dependencyFlavors.add(dependencyFlavor)
            }

            Module(){
            }

            Module(url, description) {
                (groupId,artifactId,version) = url.split(":")
                this.description = description
            }

            List<DependencyFlavor> getDependencyFlavors(){
                return dependencyFlavors
            }
            void name(String name){
                this.name=name
            }

            void version(String version){
                this.version=version
            }

            void groupId(String groupId){
                this.groupId=groupId
            }

            void artifactId(String artifactId){
                this.artifactId=artifactId
            }

            void description(String description){
                this.description=description
            }
            String getName(){
                return name
            }

            String getVersion() {
                return version
            }

            String getGroupId() {
                return groupId
            }

            String getArtifactId() {
                return artifactId
            }

            String getDescription() {
                return description
            }

            @Override
            String toString() {
                return "$groupId:$artifactId:$version"
            }

            /**
             * 依赖策略
             */
            static class DependencyFlavor {
                /**
                 * 策略名称
                 */
                private String name
                /**
                 * 此策略下的库依赖
                 */
                private List<Dependency> dependencies=[]

                /**
                 * 项目依赖信息
                 */
                def implementation(String url){
                    //映射信息到闭包
                    Dependency dependency = new Dependency(url)
                    dependencies.add(dependency)
                }

                void name(String name) {
                    this.name=name
                }

                String getName() {
                    return name
                }

                List<Dependency> getDependencies() {
                    return dependencies
                }

                @Override
                String toString() {
                    return "$name implementation:${dependencies.size()}"
                }

                /**
                 * 约束依赖信息
                 */
                static class Dependency{
                    //版本信息
                    private String version
                    //group组信息
                    private String groupId
                    //库名称
                    private String artifactId

                    Dependency(url) {
                        (groupId,artifactId,version) = url.split(":")
                    }

                    void version(String version){
                        this.version=version
                    }

                    void groupId(String groupId){
                        this.groupId=groupId
                    }

                    void artifactId(String artifactId){
                        this.artifactId=artifactId
                    }

                    String getVersion() {
                        return version
                    }

                    String getGroupId() {
                        return groupId
                    }

                    String getArtifactId() {
                        return artifactId
                    }

                    @Override
                    String toString() {
                        return "$groupId:$artifactId:$version"
                    }
                }
            }
        }

        static class Scm{
            private String url
            private String connection
            private String developerConnection

            void url(String url){
                this.url=url
            }

            void connection(String connection){
                this.connection=connection
            }

            void developerConnection(String developerConnection){
                this.developerConnection=developerConnection
            }

            String getUrl() {
                return url
            }

            String getConnection() {
                return connection
            }

            String getDeveloperConnection() {
                return developerConnection
            }
        }

        /**
         * 授权信息
         */
        static class License{
            private String name
            private String url
            private String distribution

            void name(String name){
                this.name=name
            }

            void url(String url){
                this.url=url
            }


            void distribution(String distribution){
                this.distribution=distribution
            }

            String getName() {
                return name
            }

            String getUrl() {
                return url
            }

            String getDistribution() {
                return distribution
            }
        }

        /**
         * 开发者信息
         */
        static class Developer{
            private String id
            private String name
            private String email

            void id(String id){
                this.id=id
            }

            void name(String name){
                this.name=name
            }

            void email(String email){
                this.email=email
            }

            String getId() {
                return id
            }

            String getName() {
                return name
            }

            String getEmail() {
                return email
            }
        }
    }

}
