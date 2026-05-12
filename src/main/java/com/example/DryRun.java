package com.example;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.extension.escaper.SafeString;
import io.pebbletemplates.pebble.lexer.Syntax;
import io.pebbletemplates.pebble.loader.FileLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class DryRun {
    private static String outputPath = "";
    private static String prefix = "";
    private static String templatePath = "";
    private static boolean DEBUG = false;
    private static Map<String, Object> context = new HashMap<>();
    private static String id;
    private static String val;
    private static Boolean bval;
    
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
		.strictVariables(true)
		.build();
	    PebbleTemplate compiledTemplate = engine.getTemplate(templatePath);
	    
	    
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

	    //Get any additional user defines values
	    jsonParser(context);
	    
	    // Open a FileWriter to write the file
            try (Writer fileWriter = new FileWriter(outputPath, false)) {
                compiledTemplate.evaluate(fileWriter, context);
		System.out.println("Success: Output written to " + outputPath);
		deletePeb();
	    } catch (Exception e) {
		System.err.println("Error: " + e.getMessage());
	    }
	    
	    /* Test output DEBUGGING ONLY ****************
	    Writer writer = new StringWriter();
       	    compiledTemplate.evaluate(writer, context);
	    
	    System.out.println(writer.toString());
	    ************ DEBUGGING ONLY ******************/
	} catch (io.pebbletemplates.pebble.error.PebbleException e) {
            System.err.println("Error: " + e.getMessage());	    
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
    
    public static void jsonParser(Map<String, Object> context) {
	String filePath = "./template/template.json";
	
	try {
	    // 1. Read all bytes from the file path and convert to String
	    String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
	    if(DEBUG) {
		System.out.println("****Read json file at " + Paths.get(filePath).toString());
	    }
			       
	    // 2. Parse the String into a JSONObject
	    JSONObject root = new JSONObject(content);
	    
	    // 3. Extract the "user" JSONArray
	    if (root.has("user")) {
		JSONObject userObject = root.getJSONObject("user");
		if(DEBUG) {
		    System.out.println(" userObject=\"" + userObject.toString() + "\"" );
		}

		if(userObject.has("checkbox")) {
			JSONArray checkboxArray = userObject.getJSONArray("checkbox");
			for(int index=0; index < checkboxArray.length(); index++) {
			    JSONObject cbObject = checkboxArray.getJSONObject(index);
			    if(DEBUG) {
				System.out.println(" cbObject=\"" + cbObject.toString() + "\"" );
			    }
			    id = cbObject.getString("identifier");
			    bval = cbObject.getBoolean("default");
			    context.put(id, bval);
			}
		    }
		    
		if(userObject.has("text")) {
			JSONArray textArray = userObject.getJSONArray("text");
			for(int index=0; index < textArray.length(); index++) {
			    JSONObject txtObject = textArray.getJSONObject(index);
			    if(DEBUG) {
				System.out.println(" txtObject=\"" + txtObject.toString() + "\"" );
			    }
			    id = txtObject.getString("identifier");
			    val = txtObject.getString("default");
			    if(DEBUG) {
				System.out.println("context.put("+id+","+val+");");
			    }
			    context.put(id, val);
			}
		    }
		    
		// 4. Iterate and process
		/****************
		for (int i = 0; i < userArray.length(); i++) {
		    JSONObject user = userArray.getJSONObject(i);
		    System.out.println("Parsing user index: " + i);
		    // Example: String username = user.getString("username");
		}
                ****************/
	    } else {
		System.out.println("Key 'user' not found in JSON.");
	    }
	    
	} catch (Exception e) {
	    System.err.println("Error reading or parsing the file: " + e.getMessage());
	    e.printStackTrace();
	}
    }
}
    




