## UploadPlugin
> 本库为配置同时上传多个库,简化上传私有仓库配置
> 上层自管理配置,自动生成多个配置,可以自由配置不同的软件版本.自动生成基于不同版本的上传任务

### 如何使用

```
allprojects {
    repositories {
        google()
        jcenter()
        //服务器1
        maven{ url "http://xxx.xxx.xxx.xxx/repository/xxx/"}
    }
}
dependencies {
        // classpath 'com.android.tools.build:gradle:3.0.1'
        classpath 'com.cz.upload.plugin:upload-plugin:1.0.2'//本地maven
}

```


### 如何使用

1. 应用插件

```
apply plugin: 'maven'
apply plugin: 'com.cz.upload.plugin'
```

2. 配置仓库信息

```
maven {
    // 配置仓库信息(允许配置多个
    repository{
        // 仓库1
        name "chenzhen"
        url "http://xxx.xxx.xxx.xxx/repository/xxx/"
        userName "chenzhen"
        userPassword { "xxxxxx" }
        // 创建一个库信息(可以创建多个)
        module{
            name="library"
            groupId "com.cz.upload.test.library"
            artifactId "upload-library"
            version "1.0.0"
            description "演示1"

            // 创建一个依赖更变,同样可以创建多个
            dependencyFlavor{
                name "version28"
                implementation "com.android.support:appcompat-v7:28.0.0"
                implementation "com.android.support:design:28.0.0"
                implementation 'com.android.support.constraint:constraint-layout:1.1.0'
            }
        }
        // 其他(以下可以忽略,如果上传jcenter/其他私服需要)-------------------------
        scm{
            ...
        }
        license{
            ...
        }
        developer{
            ...
        }
    }

    // 仓库2
    repository{
        //...
    }
}

```

> Task生成介绍

```
1. 根目录Task下会生成所有的上传Task,在组:,如例示会生成

root:
mavenupload
|-- upload_library(Module名称)-chenzhen(仓库名)
|-- upload_library(Module名称)-chenzhen(仓库名)-version-28(不同的依赖策略)
|-- uploadInformation(查看配置信息)

library:
|-- upload_library(Module名称)-chenzhen(仓库名)
|-- upload_library(Module名称)-chenzhen(仓库名)-version-28(不同的依赖策略)
|-- uploadInformation(查看配置信息)


```

* 执行 上传Task

```
> Task :library2:upload-library2-chenzhen-version28
> 应用配置
> 当前包类型:aar
> 上传地址:http://www.momoda.pro:8081/repository/maven/
> 上传用户:admin
> 项目地址:com.cz.upload.test.library2:upload-library2:1.0.0
> 更改依赖:com.android.support:appcompat-v7:28.0.0
> 更改依赖:com.android.support.constraint:constraint-layout:1.1.0
```

* 执行 uploadInformation

```
> Task :app:uploadInformation
+-----------------------------------------------------------------------------+
|                           Configuration Information                         |
+-----------------------------------------------------------------------------+
maven {
	repository {
		url "http://xxx.xxx.xxx.xxx/repository/xxx/"
		userName "chenzhen"
		userPassword { "123456" }
		generate "aar"
		module {
			groupId "com.cz.sample1"
			artifactId "sample-library1"
			version "1.0.1"
			description "演示1"
		}
		dependencyStrategy {
			name "support"
			implementation "com.android.support:appcompat-v7:26.1.0"
			implementation "com.android.support:design:26.1.0"
			implementation "com.android.support.constraint:constraint-layout:1.1.3"
		}
		dependencyStrategy {
			name "normal"
			implementation "com.android.support:appcompat-v7:28.0.0"
			implementation "com.android.support:design:28.0.0"
			implementation "com.android.support.constraint:constraint-layout:1.1.3"
		}
	}
}

+-----------------------------------------------------------------------------+
```


### 简化使用

> 本插件为简化多个仓库上传,所以建议仅在外围根目录进行配置,其他子项目,会自动应用

1. 见模板配置文件 [upload-maven-config.gradle](maven/upload-maven-config.gradle)

2. 在项目根目录下引用此文件,重新编辑即可

```
//加入自定义上传配置
apply from: "maven/upload-maven-config.gradle"

```


### 可能出现的问题

1. 首次使用时,请一定要先引入库编辑完成后,再加入配置信息,否则会报某某配置信息不存在

2. 未知原因,引入插件时,会提示clean task己存在,将 task注释掉即可

```
//task clean(type: Delete) {
//    delete rootProject.buildDir
//}
Cannot add task 'clean' as a task with that name already exists.
```

### 其他文档

* [更新日志](document/changeLog.md)

* [部分总结](document/summery.md)

* [TODO](document/todo.md)


### Maven目录下脚本介绍

* [maven/library-maven](library-maven.gradle) 私服配置
* [maven/upload-maven-config](upload-maven-config.gradle) 模板配置
