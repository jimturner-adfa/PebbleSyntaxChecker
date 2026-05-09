package com.example;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.extension.escaper.SafeString;
import io.pebbletemplates.pebble.lexer.Syntax;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
	// Validate that an argument was passed
        if (args.length < 1) {
            System.err.println("Usage: mvn exec:java -Dexec.args=\"templates/home.html\"");
            System.exit(1);
        }

        String templatePath = args[0];

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
	    PebbleEngine engine = new PebbleEngine.Builder()
		.syntax(customSyntax)
		.build();
	    PebbleTemplate compiledTemplate = engine.getTemplate(templatePath);
	    
	    Map<String, Object> context = new HashMap<>();
	    context.put("name", "Ubuntu User");
	    // Open a FileWriter to the same path to overwrite the file
            // Passing 'false' as the second argument ensures it overwrites
            try (Writer fileWriter = new FileWriter(templatePath, false)) {
                compiledTemplate.evaluate(fileWriter, context);
                System.out.println("Success: Output written back to " + templatePath);
            }
	    Writer writer = new StringWriter();
	    compiledTemplate.evaluate(writer, context);
	    
	    System.out.println(writer.toString());
	}
    }
}
