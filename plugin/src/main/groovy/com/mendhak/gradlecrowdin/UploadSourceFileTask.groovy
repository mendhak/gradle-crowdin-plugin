package com.mendhak.gradlecrowdin


import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.FileBody
import groovyx.net.http.Method
import groovyx.net.http.ContentType

class UploadSourceFileTask  extends DefaultTask {
    def apiKey
    def projectId
    def sourceFile

    @SuppressWarnings(["unchecked", "GrUnresolvedAccess"])
    @TaskAction
    def uploadSourceFile() {

        def updateFilePath = sprintf('http://api.crowdin.net/api/project/%s/update-file?key=%s', [projectId, apiKey])

        def http = new groovyx.net.http.HTTPBuilder( updateFilePath)

        http.handler.failure = { resp, reader ->
            println "Could not upload file: ${resp.statusLine}"
            println reader
            throw new GradleException("Could not upload file: ${resp.statusLine} \r\n " + reader)
        }

        http.request( Method.POST, ContentType.ANY ) { req ->
            //uri.path = updateFilePath
            //requestContentType = 'multipart/form-data'
            MultipartEntity entity = new MultipartEntity()
            def file = new File(sourceFile)
            entity.addPart("files[" + file.getName() + "]", new FileBody(file))
            req.entity = entity

            response.success = { resp, json ->
                println file.getName() + " uploaded to crowdin"
            }
        }


    }

}

