package com.mendhak.gradlecrowdin

import groovyx.net.http.HTTPBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.FileBody
import groovyx.net.http.Method
import groovyx.net.http.ContentType

class UploadSourceFileTask extends DefaultTask {
    def apiKey
    def projectId
    def sourceFiles

    @SuppressWarnings(["unchecked", "GrUnresolvedAccess"])
    @TaskAction
    def uploadSourceFile() {
        def updateFilePath = sprintf('http://api.crowdin.net/api/project/%s/update-file?key=%s', [projectId, apiKey])

        def http = new HTTPBuilder(updateFilePath)

        http.handler.failure = { resp, reader ->
            println "Could not upload file: ${resp.statusLine}"
            println reader
            throw new GradleException("Could not upload file: ${resp.statusLine} \r\n " + reader)
        }

        http.request(Method.POST, ContentType.ANY) { req ->
            MultipartEntity entity = new MultipartEntity()
            sourceFiles.each { pair ->
                def file = new File(pair[1])
                entity.addPart("files[" + pair[0] + "]", new FileBody(file))
            }
            req.entity = entity

            response.success = { resp, json ->
                println "Uploaded ${sourceFiles.size()} files to crowdin"
            }
        }
    }
}
