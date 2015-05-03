Gradle Crowdin Plugin
----

This Gradle plugin adds tasks to upload and download translation files from crowdin.com

`crowdinDownload` downloads the latest translations and `crowdinUpload` uploads a source file. 


## Add to your build.gradle

In your build.gradle's `buildScript` section, add this repository and classpath

    buildscript {
        repositories {
            ...
            maven {
                url  "http://dl.bintray.com/mendhak/maven"
            }
        }
        dependencies {
            ...
            classpath group: 'com.mendhak.gradlecrowdin', name: 'crowdin-plugin', version: '0.0.3'
        }
    }

Then apply the plugin

    apply plugin: 'com.mendhak.gradlecrowdin'

## Download task

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


## Upload task

    crowdinUpload {
        apiKey = "31727f222f203349979cf710a471b767"
        projectId = 'my-test-project'
        sourceFile = "$projectDir/src/main/res/values/strings.xml"
    }


 
 