package com.example.doc_generator.parser;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ProjectScanner {
    private static final Logger logger = Logger.getLogger(ProjectScanner.class.getName());

    public List<File> scanProject(String projectPath) {
        File projectDir = new File(projectPath);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid project path: " + projectPath);
        }

        List<File> files = getJavaFiles(projectDir);
        logger.info("Found " + files.size() + " Java files in " + projectPath);
        files.forEach(f -> logger.info("Scanning file: " + f.getName()));
        return files;
    }

    private List<File> getJavaFiles(File directory) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(getJavaFiles(file));
                } else if (file.getName().endsWith(".java") && 
                         (file.getName().contains("Controller") || 
                          file.getName().contains("Service"))) {
                    javaFiles.add(file);
                    logger.info("Added file: " + file.getAbsolutePath());
                }
            }
        }
        return javaFiles;
    }
}