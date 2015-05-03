package com.mendhak.gradlecrowdin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class DownloadTranslationsTask extends DefaultTask {
    def destination
    def apiKey
    def projectId
    def renameMapping

    @TaskAction
    def downloadTranslations() {

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

        def extractedDir = new File(buildSubDir, "res")

        //Extract to build dir/res
        ant.unzip(src:translationZip, dest:extractedDir, overwrite:true)

        ant.copy( todir: destination ){
            fileset(dir:extractedDir, includes:'**/**')
            if(renameMapping != null){
                regexpmapper(renameMapping)
            }

        }
    }
}