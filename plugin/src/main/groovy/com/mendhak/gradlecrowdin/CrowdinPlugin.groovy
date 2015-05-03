package com.mendhak.gradlecrowdin

import org.gradle.api.Project
import org.gradle.api.Plugin

class CrowdinPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.task('crowdinDownload', type: DownloadTranslationsTask)
    }
}
