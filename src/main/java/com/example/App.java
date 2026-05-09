package com.example;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.extension.escaper.SafeString;
import io.pebbletemplates.pebble.lexer.Syntax;
import io.pebbletemplates.pebble.loader.FileLoader; 
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
	// Validate that an argument was passed
        if (args.length < 2) {
            System.err.println("Usage: mvn exec:java -Dexec.args=\"templates/home.html\"");
            System.exit(1);
        }

	String prefix = args[0];
        String templatePath = args[1];

	// Calculate the output path by removing the .peb extension
        String outputPath = templatePath;
        int lastDotIndex = templatePath.lastIndexOf(".");
        if (lastDotIndex != -1) {
            outputPath = templatePath.substring(0, lastDotIndex);
        }
	
	try {
	    // Create a custom syntax configuration
	    Syntax customSyntax = new Syntax.Builder()
		.setPrintOpenDelimiter("${{")  // Change from {{
		.setPrintCloseDelimiter("}}")  // Keep or change closing delimiter
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
	    context.put("name", "Ubuntu User");
	    
	    // Open a FileWriter to write the file
            try (Writer fileWriter = new FileWriter(outputPath, false)) {
                compiledTemplate.evaluate(fileWriter, context);
                System.out.println("Success: Output written to " + outputPath);
            }
	    
	    //Test output
	    Writer writer = new StringWriter();
	    compiledTemplate.evaluate(writer, context);
	    
	    System.out.println(writer.toString());
	} catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
