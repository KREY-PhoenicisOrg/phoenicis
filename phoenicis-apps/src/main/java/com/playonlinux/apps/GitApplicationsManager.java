package com.playonlinux.apps;

import com.playonlinux.apps.dto.CategoryDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GitApplicationsManager implements ApplicationsSource {
    private List<CategoryDTO> cache;
    private final String gitRepositoryURL;
    private final LocalApplicationsSource.Factory localAppsManagerFactory;

    public GitApplicationsManager(String gitRepositoryURL, LocalApplicationsSource.Factory localAppsManagerFactory) {
        this.gitRepositoryURL = gitRepositoryURL;
        this.localAppsManagerFactory = localAppsManagerFactory;
    }

    @Override
    public List<CategoryDTO> fetchInstallableApplications() {
        if(cache != null) {
            return cache;
        }
        // FIXME : Use a background thread
        try {
            final File gitTmp = Files.createTempDirectory("git").toFile();
            gitTmp.deleteOnExit();
            new ProcessBuilder(Arrays.asList("git", "clone", gitRepositoryURL, gitTmp.getAbsolutePath()))
                .start()
                .waitFor();

            cache = localAppsManagerFactory.createInstance(gitTmp.getAbsolutePath()).fetchInstallableApplications();
            return cache;
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }
    }


}