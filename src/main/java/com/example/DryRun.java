package com.example;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.extension.escaper.SafeString;
import io.pebbletemplates.pebble.lexer.Syntax;
import io.pebbletemplates.pebble.loader.FileLoader; 
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DryRun {
    private static String outputPath = "";
    private static String prefix = "";
    private static String templatePath = "";
    
    public static void main(String[] args) throws Exception {
	// Validate that an argument was passed
        if (args.length < 2) {
            System.err.println("Usage: mvn exec:java -Dexec.args=\"templates/home.html\"");
            System.exit(1);
        }

	prefix = args[0];
        templatePath = args[1];

	// Calculate the output path by removing the .peb extension
        outputPath = templatePath;
        int lastDotIndex = templatePath.lastIndexOf(".");
        if (lastDotIndex != -1) {
            outputPath = templatePath.substring(0, lastDotIndex);
        }
	
	try {
	    // Create a custom syntax configuration
	    Syntax customSyntax = new Syntax.Builder()
		.setPrintOpenDelimiter("${{")  // Change from {{
		.setPrintCloseDelimiter("}}")  // Keep or change closing delimiter
		.setExecuteOpenDelimiter("${%")  // Change from {{
		.setExecuteCloseDelimiter("%}")  // Keep or change closing delimiter
		.setCommentOpenDelimiter("${#")  // Change from {{
		.setCommentCloseDelimiter("#}")  // Keep or change closing delimiter
		.build();
	    // Note: FileLoader requires an absolute path or a specific base directory in 4.x
	    FileLoader loader = new FileLoader(prefix);
	    //// JMT loader.setPrefix("/home/yaturner/Documents/GitHub/DryRun");
	    PebbleEngine engine = new PebbleEngine.Builder()
		.syntax(customSyntax)
		.loader(loader)
		.build();
	    PebbleTemplate compiledTemplate = engine.getTemplate(templatePath);
	    
	    Map<String, Object> context = new HashMap<>();
	    context.put("PACKAGE_NAME","com.example.myapplication");
	    context.put("APP_NAME","My Application");
	    context.put("CLASS_NAME","MainActivity");
	    context.put("SAVE_LOCATION","/sdcard/CodeOnTheGoProjects/My Application");
	    context.put("AGP_VERSION","8.11.0");
	    context.put("KOTLIN_VERSION","1.9.22");
	    context.put("GRADLE_VERSION","8.14.3");
	    context.put("LANGUAGE","kotlin");
	    context.put("COMPILE_SDK","36");
	    context.put("MIN_SDK","21");
	    context.put("TARGET_SDK","36");
	    context.put("JAVA_SOURCE_COMPAT","JavaVersion.VERSION_17");
	    context.put("JAVA_TARGET_COMPAT","JavaVersion.VERSION_17");
	    context.put("JAVA_TARGET","17");
	    
	    // Open a FileWriter to write the file
            try (Writer fileWriter = new FileWriter(outputPath, false)) {
                compiledTemplate.evaluate(fileWriter, context);
                System.out.println("Success: Output written to " + outputPath);
		deletePeb();
            }
	    
	    /* Test output DEBUGGING ONLY ****************
	    Writer writer = new StringWriter();
       	    compiledTemplate.evaluate(writer, context);
	    
	    System.out.println(writer.toString());
	    ************ DEBUGGING ONLY ******************/
	} catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void deletePeb() {
	Path PathPeb = Paths.get(templatePath);
	Path PathParsed = Paths.get(outputPath);

	try {
	    if(Files.exists(PathParsed)) {
		boolean wasDeleted = Files.deleteIfExists(PathPeb);
		if(!wasDeleted) {
		    System.out.println("Failed to find file " + PathParsed.toString());
		}
	    }
	} catch (IOException e) {
	    System.err.println("Error during deleting file " + PathPeb.toString() + " : "
			       + e.getMessage());
	}
    }
}
