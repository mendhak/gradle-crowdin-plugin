Gradle Crowdin Plugin
----

This Gradle plugin adds tasks to upload and download translation files from crowdin.com

`crowdinDownload` downloads the latest translations and `crowdinUpload` uploads a source file. 

Uses API v1.


## Usage

Add this to your build.gradle:

    plugins {
      id "com.mendhak.gradlecrowdin" version "0.1.1"
    }

For pre-Gradle-2.1, follow the [instructions here](https://plugins.gradle.org/plugin/com.mendhak.gradlecrowdin)

## Download task

For download, the plugin invokes an export of your project's translations and then downloads the translations in a zip file. 
It then unzips and copies to the given destination.

    crowdinDownload{
        apiKey = "31727f222f203349979cf710a471b767"
        destination = "$projectDir/src/main/res"
        projectId = 'my-test-project'
    
        //Optional
        //Android projects
        renameMapping  = [
            from:  '^(.*)/(.*)$',
            to:  /values-\1\/\2/
        ]
        //Optional
        excludePattern = '**/*.txt'
    }

In some cases you may want to modify the structure as it's copied over.  
For example, Android projects would use `values-es` instead of `es`.  You can use the `renameMapping` option to do this.
 
You may also want to exclude certain files, you can do this using `excludePattern`.

## Android download task

Crowdin gives `ru` files as well as `pt-BR` files.  You can get them both with two tasks.

      task crowdin1(group:"crowdin", type:com.mendhak.gradlecrowdin.DownloadTranslationsTask){
    
                apiKey = CROWDIN_API_KEY
                destination = "$projectDir/src/main/res"
                projectId = 'gpslogger-for-android'
    
                //Android projects
                renameMapping = [
                        from: '^([^-]*)/strings.xml$',
                        to  : /values-\1\/strings.xml/
                ]
    
                excludePattern = '**/*.txt'
        }
    
        task crowdin2(group:"crowdin", type:com.mendhak.gradlecrowdin.DownloadTranslationsTask, dependsOn:"crowdin1"){
    
            apiKey = CROWDIN_API_KEY
            destination = "$projectDir/src/main/res"
            projectId = 'gpslogger-for-android'
    
            //Android projects
            renameMapping = [
                    from: '^([^-]*)-([^-]*)/strings.xml$',
                    to  : /values-\1-r\2\/strings.xml/
            ]
    
            excludePattern = '**/*.txt'
        }

And then call it like so

    task getallTranslations(group:"crowdin", dependsOn:"crowdin2")  {    }

As an alternative you can go to the file settings on crowdin and set the resulting file name to something similar to `/app/src/main/res/values-%android_code%/strings.xml`. Crowdin will then automatically generate the appropriate file structure inside the downloaded zip file. In the example file name you would have to also adjust the destination to `"$projectDir"`.

## Upload task

Point this at your source file, such as `values/strings.xml` and the task will update it on crowdin. 

    crowdinUpload {
        apiKey = "31727f222f203349979cf710a471b767"
        projectId = 'my-test-project'
        files = [
            [ name: 'strings.xml', source: 'app/src/main/res/values/strings.xml' ]
        ]
    }

Note that the file must exist on crowdin, this simply acts as an updater. New files are not created.
The files attribute expects a list of files where the name entry referrs to the file name used on crowdin and the source entry to the corresponding file in your project.
This also allows you to batch upload strings.xml files from separate modules.

## Identification

Either use the combination of project identifier(**projectId**) and project key(**apiKey**) or combination of project identifier(**projectId**), username(**username**), and account key(**accountKey**) to authorize yourself.
If they are all present in the configuration file, the second option is prioritized.

    crowdinDownload {
        
        projectId = PROJECT_ID
        
        //First option
        apiKey = CROWDIN_API_KEY
        
        //Second option, prioritized
        username = USERNAME
        accountKey = ACCOUNT_KEY
        
        //...
    }

## Branches

You can also use the version management features of crowdin.
This allows you to upload your files separately for each of your release and feature branches. For more details visit the [official documentation](https://support.crowdin.com/versions-management/).

The following snippet gets the currently checked out branch automatically

    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'org.ajoberstar:grgit:1.9.0'
        }
    }

    ext {
        git = org.ajoberstar.grgit.Grgit.open()
        gitBranchName = git.branch.getCurrent().name
    }

The branch name needs to be set for the crowdinDownload and crowdinUpload tasks.

    crowdinDownload {
        ...
        branch = gitBranchName
        ...
    }

    crowdinUpload {
        ...
        branch = gitBranchName
        ...
    }

If the branch does not exist yet on crowdin it will be created automatically.
Keep in mind that the characters \ / : * ? " < > | are not allowed in crowdin branch names and will be replaced with underscores by this crowdin plugin.
Since a branch on crowdin is basically a special folder all translation files will be copied to the newly created branch.
Therefore if you have set a title or the 'resulting file' field on crowdin and you want to keep them also for your newly created branches you need to specify the title and/or translation fields as well.

    crowdinUpload {
        ...
        files = [
            [
                    title      : 'App',  // Artifact name shown to the translators (optional)
                    name       : 'strings.xml',  // File name used on crowdin
                    source     : 'app/src/main/res/values/strings.xml', // File that should be translated
                    translation: 'app/src/main/res/values-%android_code%/strings.xml' // Path to store the translation (optional)
            ]
        ]
        ...
    }

## Screenshot
 
In IDEA, the task should appear under the category crowdin as shown here

![idea](screenshot.png)

## Building

To build, run the `uploadArchives` task under upload.  This doesn't actually upload it anywhere, it just copies it to the 'repo' folder.
 
You can then reference it in another project locally, modify the gradle file to look like so:
 
    buildscript {
        repositories {
        mavenCentral()
                maven {
                    url uri('/home/mendhak/Code/gradle-crowdin-plugin/plugin/repo')
                }
        }
        dependencies {
            classpath 'com.mendhak.gradlecrowdin:crowdin-plugin:0.1.1'
        }
    }
    
    apply plugin: 'com.mendhak.gradlecrowdin'

I then publish to Maven using the `./gradlew bintrayUpload` task, and to the Gradle using `./gradlew publishPlugins` 

### 2020 Note

This is running on an old version of Gradle.  I tried upgrading and nothing would build or run; the errors were cryptic and the stack traces were useless.  I had to stick to OpenJDK 1.8, Gradle 2.6.  