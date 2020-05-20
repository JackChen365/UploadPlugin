## README

This is actually a tool for the Jitpack. For multi-modules we have to configured a lot in each modules. This plugin makes everything easy to use.

```

//In the project build.gradle.
repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        ...
        classpath "com.github.dcendents:android-maven-gradle-plugin:2.1"
        classpath 'com.github.momodae.UploadPlugin:jitpack:1.0.1'
}


apply plugin: 'jitpack.upload'
jitpack{
    module{
        name "library"
        group "com.cz.upload.test.library"
    }
    module{
        name "library2"
        group "com.cz.upload.test.library2"
    }
    module{
        name "uploadplugin"
        group "com.cz.upload.plugin"
    }
}

```

That all we need to do. As you can see. No matter how many modules.
We only need to configured the modules in the project's build script.
You don't have to change about your module's build script.
That's the benefit.

It supports both Java project and android project.