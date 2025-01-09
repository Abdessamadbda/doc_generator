package com.example.doc_generator.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ParserConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApiDocExtractor {

    // Define the ApiEntry record to store API information (endpoint, HTTP method, and description)
    public record ApiEntry(String endpoint, String method, String description) {}

    // Method to extract API documentation from a given Java source file
    public List<ApiEntry> extractApiDocumentation(File file) throws Exception {
        // Set JavaParser to use Java 17 language level to support modern Java syntax, such as records
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17); // Updated to Java 17
        StaticJavaParser.setConfiguration(parserConfiguration);
        
        // Parse the Java file and create a CompilationUnit (abstract syntax tree)
        CompilationUnit compilationUnit = StaticJavaParser.parse(file);

        // List to hold the extracted API documentation (API entries)
        List<ApiEntry> apiEntries = new ArrayList<>();

        // Iterate over all method declarations in the parsed Java file
        compilationUnit.findAll(MethodDeclaration.class).forEach(method -> {
            // Extract the HTTP method type (GET, POST, etc.) from annotations
            String httpMethod = getHttpMethod(method);
            
            // If an HTTP method is found, extract the endpoint and description
            if (httpMethod != null) {
                String endpoint = getEndpoint(method);  // Extract the endpoint from annotations
                String description = method.getComment()  // Get the method's description (if available)
                        .map(c -> c.getContent().strip()) // Strip leading/trailing spaces from the description
                        .orElse("No description"); // Default description if none is provided
                
                // Add the extracted API entry to the list
                apiEntries.add(new ApiEntry(endpoint, httpMethod, description));
            }
        });

        // Return the list of extracted API entries
        return apiEntries;
    }

    // Helper method to extract the HTTP method type (GET, POST, PUT, DELETE) from annotations
    private String getHttpMethod(MethodDeclaration method) {
        // Loop through each annotation on the method to check for mapping annotations
        for (AnnotationExpr annotation : method.getAnnotations()) {
            // Return corresponding HTTP method based on the annotation name
            if (annotation.getNameAsString().equals("GetMapping")) return "GET";
            if (annotation.getNameAsString().equals("PostMapping")) return "POST";
            if (annotation.getNameAsString().equals("PutMapping")) return "PUT";
            if (annotation.getNameAsString().equals("DeleteMapping")) return "DELETE";
        }
        // Return null if no relevant HTTP mapping annotation is found
        return null;
    }

    // Helper method to extract the endpoint from annotations (GetMapping, PostMapping, etc.)
    private String getEndpoint(MethodDeclaration method) {
        // Look for relevant annotations (GetMapping, PostMapping, etc.) and extract the endpoint
        return method.getAnnotations().stream()
                .filter(a -> a.getNameAsString().matches("GetMapping|PostMapping|PutMapping|DeleteMapping"))
                .findFirst()  // Find the first matching annotation
                .map(a -> a.toString().replaceAll("[\"()]", "")) // Remove quotes and parentheses from the endpoint string
                .orElse("/"); // Default to "/" if no endpoint is found
    }
}
