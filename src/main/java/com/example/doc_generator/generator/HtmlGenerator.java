package com.example.doc_generator.generator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.example.doc_generator.parser.ApiDocExtractor.ApiEntry;
import com.example.doc_generator.parser.AboutSectionExtractor.AboutInfo;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HtmlGenerator {
   private String outputDir;
    private Document doc;
    private Map<String, Element> controllerSections;

    public HtmlGenerator(String outputDir) {
        this.outputDir = outputDir;
        this.controllerSections = new HashMap<>();
        initializeDocument();
        addProjectOverview();
    }

    private void addProjectOverview() {
        Element overview = new Element("div").addClass("project-overview");
        overview.append("""
            <div class="overview-header">
                <h1>Project Overview</h1>
                <div class="overview-content">
                    <h2>HiringHero API Documentation</h2>
                    <p>This documentation provides a comprehensive guide to the HiringHero REST API endpoints.</p>
                    
                    <div class="project-info">
                        <h3>Project Information</h3>
                        <ul>
                            <li><strong>Project Name:</strong> HiringHero Backend API</li>
                            <li><strong>Version:</strong> 1.0.0</li>
                            <li><strong>Base URL:</strong> /api/v1</li>
                        </ul>
                    </div>
                    
                    <div class="tech-stack">
                        <h3>Technology Stack</h3>
                        <ul>
                            <li>Spring Boot</li>
                            <li>Spring Security</li>
                            <li>JWT Authentication</li>
                            <li>RESTful Architecture</li>
                        </ul>
                    </div>
                </div>
            </div>
            <style>
                .project-overview {
                    background: #ffffff;
                    padding: 2rem;
                    margin: 2rem 0;
                    border-radius: 0.5rem;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                }
                .overview-header h1 {
                    color: #1a365d;
                    margin-bottom: 1.5rem;
                }
                .project-info, .tech-stack {
                    margin: 1.5rem 0;
                }
                .overview-content ul {
                    list-style: none;
                    padding-left: 0;
                }
                .overview-content li {
                    margin: 0.5rem 0;
                }
                .tech-stack ul {
                    display: flex;
                    gap: 1rem;
                    flex-wrap: wrap;
                }
                .tech-stack li {
                    background: #e2e8f0;
                    padding: 0.25rem 0.75rem;
                    border-radius: 1rem;
                }
            </style>
        """);
        
        doc.body().prependChild(overview);
    }

    private void initializeDocument() {
        doc = Jsoup.parse("<!DOCTYPE html><html><head><title>API Documentation</title></head><body></body></html>");
        
        Element head = doc.head();
        head.append("""
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    max-width: 1000px;
                    margin: 0 auto;
                    padding: 2rem;
                    line-height: 1.6;
                    color: #333;
                }
                h1 {
                    color: #1a365d;
                    border-bottom: 2px solid #e2e8f0;
                    padding-bottom: 0.5rem;
                    margin-top: 2rem;
                }
                h2 {
                    color: #2c5282;
                    margin-top: 1.5rem;
                }
                .controller-section {
                    margin: 2rem 0;
                    padding: 1rem;
                    background: #f8fafc;
                    border-radius: 0.5rem;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                }
                .endpoint {
                    background: white;
                    border-left: 4px solid #4299e1;
                    padding: 1rem;
                    margin: 1rem 0;
                    border-radius: 0.25rem;
                }
                .method {
                    display: inline-block;
                    background: #4299e1;
                    color: white;
                    padding: 0.25rem 0.75rem;
                    border-radius: 0.25rem;
                    font-weight: bold;
                    margin-right: 1rem;
                }
                .description {
                    margin: 1rem 0;
                    padding-left: 1rem;
                    border-left: 2px solid #e2e8f0;
                }
                .params {
                    margin-top: 0.5rem;
                    font-family: monospace;
                }
                .nav {
                    position: sticky;
                    top: 0;
                    background: white;
                    padding: 1rem;
                    border-bottom: 1px solid #e2e8f0;
                    margin-bottom: 2rem;
                }
            </style>
        """);
        
        doc.body().append("<div class='nav'><h1>API Documentation</h1></div>");
    }

    public void generateDocumentation(AboutInfo aboutInfo, List<ApiEntry> apiEntries) {
        String controllerName = aboutInfo.projectName();
        
        Element controllerSection = controllerSections.computeIfAbsent(controllerName, k -> {
            Element section = new Element("div").addClass("controller-section");
            section.append("<h2>" + controllerName + "</h2>");
            section.append("<div class='description'>" + aboutInfo.description() + "</div>");
            
            Element metadata = new Element("div").addClass("metadata");
            metadata.append("<p><strong>Author:</strong> " + aboutInfo.author() + "</p>");
            metadata.append("<p><strong>Version:</strong> " + aboutInfo.version() + "</p>");
            metadata.append("<p><strong>Last Modified:</strong> " + aboutInfo.lastModified() + "</p>");
            
            section.appendChild(metadata);
            doc.body().appendChild(section);
            return section;
        });

        for (ApiEntry apiEntry : apiEntries) {
            Element endpoint = new Element("div").addClass("endpoint");
            endpoint.append("<h3>" + apiEntry.endpoint() + "</h3>");
            endpoint.append("<span class='method'>" + apiEntry.method() + "</span>");
            
            String description = apiEntry.description()
                .replace("* ", "")
                .replace("*", "")
                .trim();
            
            endpoint.append("<div class='description'>" + description + "</div>");
            controllerSection.appendChild(endpoint);
        }

        try (FileWriter writer = new FileWriter(outputDir + "/index.html")) {
            writer.write(doc.outerHtml());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}