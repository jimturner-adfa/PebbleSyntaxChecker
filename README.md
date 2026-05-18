## DryRun
*DryRun* is a Java-based utility designed to validate and process Pebble templates within a Gradle-managed environment. It provides a safe way to test template logic, path resolution, and environment configurations without executing a full production deployment.

## 🚀 Getting Started
### Prerequisites:
Ensure your environment is configured with the following:

1. JDK 17 or higher
2. Gradle 7.x+

### Environment Variables:

1. JAVA_HOME: Points to your JDK installation.
2. ANDROID_HOME: Points to your Android SDK.

### Installation
Clone the repository to your local machine:


Bash
```
git clone https://github.com/jimturner-adfa/PebbleSyntaxChecker.git
cd DryRun
```

### ⚙️ Configuration
The project utilizes environment variables to resolve template paths. You can verify your setup using the included bash logic:

Bash
```
if [[ -z "$JAVA_HOME" ]]; then
    echo "Warning: JAVA_HOME is not set."
fi
if [[ -z "$ANDROID_HOME" ]]; then
    echo "Warning: ANDROID_HOME is not set."
fi
```

### Pebble Parser Settings
This project is configured to use relative paths for template loading. Ensure your FileLoader prefix is correctly pointed to your resources directory:

### 🛠 Usage

Bash
```
DryRun.sh <templeDir> <Language>
```

where:
* templateDir - the directory containing the template with the peb files
* language - is either java or kotlin

### Implementation:

The script will:
1. Copy your tenplate directory into a new directory with the suffix .copy.
2. Run the Pebble parser on the peb file in the copy
3. Based on the value of language it will delete files not used (i.e. if the language is "java", the all kotlin files will be deleted)
4. run gradle to build the template and verify the syntax
