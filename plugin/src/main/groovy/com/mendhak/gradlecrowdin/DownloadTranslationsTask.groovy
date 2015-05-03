package com.mendhak.gradlecrowdin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class DownloadTranslationsTask extends DefaultTask {
    def destination
    def apiKey
    def projectId
    def renameMapping
    def excludePattern

    @TaskAction
    def downloadTranslations() {

        def buildSubDir = new File(project.buildDir.getPath() , "/crowdin-plugin")
        buildSubDir.mkdirs()

        //Tell Crowdin to do an export
        def exportUrl = sprintf('http://api.crowdin.net/api/project/%s/export?key=%s', [projectId, apiKey])
        new URL(exportUrl).getText()

        //Download actual zip file
        def url = sprintf('http://api.crowdin.net/api/project/%s/download/%s.zip?key=%s', [projectId, 'all', apiKey])
        def translationZip = new File(buildSubDir.getPath(), "translations.zip")
        def file = translationZip.newOutputStream()
        file << new URL(url).openStream()
        file.close()
        println "Translations downloaded"

        def extractedDir = new File(buildSubDir, "res")

        //Extract to build dir/res
        ant.unzip(src:translationZip, dest:extractedDir, overwrite:true)

        if(excludePattern == null){
            excludePattern = ''
        }

        ant.copy( todir: destination  ){
            fileset(dir:extractedDir, includes:'**/**', excludes:excludePattern)
            if(renameMapping != null){
                regexpmapper(renameMapping)
            }

            println "Copying to destination directory"
        }

        println "Done"
    }
}