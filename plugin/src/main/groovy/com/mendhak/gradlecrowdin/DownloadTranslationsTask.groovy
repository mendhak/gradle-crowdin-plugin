package com.mendhak.gradlecrowdin

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

class DownloadTranslationsTask extends DefaultTask {
    def destination
    def apiKey
    def projectId

    @TaskAction
    def downloadTranslations() {

//        println(apiKey)
//        println(projectId)
//        println(destination)

        def buildSubDir = new File(project.buildDir.getPath() , "/crowdin-plugin")
        buildSubDir.mkdirs()

        //Tell Crowdin to do an export
        def exportUrl = sprintf('http://api.crowdin.net/api/project/%s/export?key=%s', [projectId, apiKey])
        println(new URL(exportUrl).getText())

        //Download actual zip file
        def url = sprintf('http://api.crowdin.net/api/project/%s/download/%s.zip?key=%s', [projectId, 'all', apiKey])
        def translationZip = new File(buildSubDir.getPath(), "translations.zip")
        def file = translationZip.newOutputStream()
        file << new URL(url).openStream()
        file.close()

        //Extract to build dir/res
        ant.unzip(src:translationZip, dest:new File(buildSubDir, "res"), overwrite:true)

        def list = []
        def extractedDir = new File(buildSubDir, "res")
        extractedDir.eachFileRecurse (FileType.FILES) { f ->
            list << f
        }

        //Copy to values-XYZ in the destination folder
        list.each {
            def copyTo = new File(destination, "values-"+ it.getParentFile().getName() + "/" + it.getName() )
            println copyTo.getPath()
            ant.copy( file:it.getPath(), tofile:copyTo.getPath())
        }
    }

}