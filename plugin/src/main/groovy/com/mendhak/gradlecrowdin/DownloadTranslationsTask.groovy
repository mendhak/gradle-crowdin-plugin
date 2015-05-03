package com.mendhak.gradlecrowdin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

class DownloadTranslationsTask extends DefaultTask {
    def destination
    def apiKey
    def projectId

    File getDestination() {
        project.file(destination)
    }

    @TaskAction
    def downloadTranslations() {

//        println(apiKey)
//        println(projectId)
        println(destination)

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

        unzipFile(translationZip, buildSubDir)
    }

    def unzipFile(File zipFile, File outputFolder){

        byte[] buffer = new byte[1024];

        try{
            if(!outputFolder.exists()){
                outputFolder.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder, fileName);

                println("Extracting : "+ newFile.getAbsoluteFile());

                if(ze.isDirectory()){
                    newFile.mkdirs()
                }
                else {
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

}