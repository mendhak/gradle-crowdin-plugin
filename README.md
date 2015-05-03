Gradle Crowdin Plugin
----

This Gradle plugin adds tasks to upload and download translation files from crowdin.com

`crowdinDownload` downloads the latest translations and `crowdinUpload` uploads a source file. 


## Usage

Add this to your build.gradle:

    plugins {
      id "com.mendhak.gradlecrowdin" version "0.0.4"
    }

For pre-Gradle-2.1, follow the [instructions here](https://plugins.gradle.org/plugin/com.mendhak.gradlecrowdin)

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


 
 