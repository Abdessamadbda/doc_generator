package com.example.doc_generator.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;

public class AboutSectionExtractor {
    public record AboutInfo(String projectName, String description, String author, String version, String lastModified) {}

    public AboutInfo extractAboutInfo(File file) throws Exception {
        CompilationUnit compilationUnit = StaticJavaParser.parse(file);

        Optional<ClassOrInterfaceDeclaration> mainClass = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class);

        String projectName = mainClass
                .map(ClassOrInterfaceDeclaration::getNameAsString)
                .orElse("Unknown Project");

        String description = mainClass
                .flatMap(cls -> cls.getComment()
                    .map(Comment::getContent)
                    .map(content -> content
                        .replaceAll("\\*", "")
                        .replaceAll("@.*", "")
                        .trim()))
                .orElse("No description available");

        String author = compilationUnit.getAllContainedComments().stream()
                .map(Comment::getContent)
                .filter(content -> content.contains("@author"))
                .map(content -> content
                    .replaceAll(".*@author", "")
                    .trim())
                .findFirst()
                .orElse("Unknown Author");

        String version = compilationUnit.getAllContainedComments().stream()
                .map(Comment::getContent)
                .filter(content -> content.contains("@version"))
                .map(content -> content
                    .replaceAll(".*@version", "")
                    .trim())
                .findFirst()
                .orElse("1.0.0");

        String lastModified = compilationUnit.getAllContainedComments().stream()
                .map(Comment::getContent)
                .filter(content -> content.contains("@since"))
                .map(content -> content
                    .replaceAll(".*@since", "")
                    .trim())
                .findFirst()
                .orElse("Unknown");

        return new AboutInfo(projectName, description, author, version, lastModified);
    }
}