package org.gradle

import com.mendhak.gradlecrowdin.DownloadTranslationsTask
import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import static org.junit.Assert.*

class CrowdinPluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply 'com.mendhak.gradlecrowdin'

        assertTrue(project.tasks.crowdinDownload instanceof DownloadTranslationsTask)
    }
}
