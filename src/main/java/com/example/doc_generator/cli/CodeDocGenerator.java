package com.example.doc_generator.cli;
import com.example.doc_generator.parser.ApiDocExtractor;
import com.example.doc_generator.parser.AboutSectionExtractor;
import com.example.doc_generator.generator.HtmlGenerator;
import com.example.doc_generator.parser.ProjectScanner;
import java.io.File;
import java.util.List;
public class CodeDocGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java CodeDocGenerator <source-dir> <output-dir>");
            return;
        }
        
        String sourceDir = args[0];
        String outputDir = args[1];

        ProjectScanner scanner = new ProjectScanner();
        ApiDocExtractor apiExtractor = new ApiDocExtractor();
        AboutSectionExtractor aboutExtractor = new AboutSectionExtractor();
        HtmlGenerator generator = new HtmlGenerator(outputDir);

        List<File> javaFiles = scanner.scanProject(sourceDir);

        for (File file : javaFiles) {
            List<ApiDocExtractor.ApiEntry> apiEntries = apiExtractor.extractApiDocumentation(file);
            AboutSectionExtractor.AboutInfo aboutInfo = aboutExtractor.extractAboutInfo(file);

            generator.generateDocumentation(aboutInfo, apiEntries);
        }

        System.out.println("Documentation generated at: " + outputDir);
    }
}
