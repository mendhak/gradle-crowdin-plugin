package com.mendhak.gradlecrowdin

import org.gradle.api.Project
import org.gradle.api.Plugin

class CrowdinPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.task('crowdinDownload', type: DownloadTranslationsTask, group:'crowdin', description:'Download and copy translated files to given destination')
        target.task('crowdinUpload', type: UploadSourceFileTask, group:'crowdin', description: 'Update a given source file on crowdin')
    }
}
