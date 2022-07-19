# JPlag Evaluation Software
## Introduction
This software enables the user to create labeled datasets and run JPlag and these datasets using multiple different configurations and versions of JPlag.
The software then saves a multitude of statistics that can be helpful when evaluating different configurations and versions.

This Software is a Prototype. A lot of functionality exists only as a placeholder.

## Running
This project can be run directly in an IDE or after building a jar file. The command line argument must contain the path to the evaluation directory. This directory must contain all the files described in de _Configuration_ section.

## Results
The current implementation creates a new subdirectory in the `result` directory for every run.
This new directory is named `eval-` followed by a timestamp in `YYYY-MM-DD-hh-mm-ss` format.
The directory contains a `summary.csv` file and one `.csv` file for every configuration-project pair.

The summary file contains key metrics for every combination of jar configuration and project.
Notable statistics are the average similarity depending on the type of submission pair (e.g. _plagiarism_, _no_plagiarism_).
The values for detection rate are created using a hardcoded similarity threshold or the `suspicious` flag contained in `JPlagComparison` objects if present. This should be changed in the future.

The individual files contain the non-zero-similarity submission comparisons. They can be used for debugging and finding plagiarized submission pairs that evaded detection.

## Building
This is a maven project. To build a single jar file containing all dependencies, run `mvn clean package assembly:single` in the top level directory of the maven project.

## Configuration
To use the software, a specific directory structure is required:
```
├── dataset
├── jars
└── result
```

### Projects and Truth Files
The `dataset` subdirectory contains the individual project directories and corresponding truth files:

```
├── dataset
│   ├── project-1
│   │   ├── submission1.cpp
│   │   ├── submission2.cpp
│   │   ├── submission3.cpp
│   │   ├── ...
│   ├── project-1-truth.txt
│   ├── project-2
│   ├── project-2-truth.txt
│   ├── ...
...
```

A truth file contains information about the relation of submission pairs, especially if they are plagiarism.
A truth file must have the exact name as the corresponding project directory followed by the `-truth.txt`-suffix.
The information must be provided in the following format:

```
>mossad_plag
student8363_mossad_1.cpp,student8363.cpp
student8363_mossad_2.cpp,student8363.cpp

>mossad_sibling
student8363_mossad_1.cpp,student8363_mossad_2.cpp

>common_plag
student3206.cpp,student2675.cpp,student8363.cpp
```

Each line contains at least two submissions separated by a comma.
The pair/group is assigned the type above.

### Jars and Configuration
The `jars` subdirectory must contain a `config.json` and the JPlag jar files.
The names of the jar files are arbitrary. The jar files must contain all dependencies.

```
├── jars
│   ├── config.json
│   ├── jplag-1.jar
│   ├── jplag-2.jar
│   ├── jplag-3.jar
│   ├── ...
...
```

The `config.json` configuration file can be used to define different running configurations. The following schema must be used:


```json
{
    "jars" : [JarDefinition]
}
```

#### JarDefinition:
```json
{
    "file": "",
    "commit": "",
    "configs": [JarConfig]
}
```
Registers a jar file and allows the definition of multiple configurations.
- `"file"`: The filename of the jar that is defined in this entry. Example: `"jplag-4.0.0.jar"` 
- `"commit"` (optional): The git commit that this jar file was built from. Defaults to `"unknown"`.
- `"skip"` (optional): If this entry is present and set to any value, this jar is skipped when running. 
- `"configs"`: Array containing the JSON of type `JarConfig` describing the different configurations that will be used when running this jar file. If left empty, a single configuration without options and `"default"` as the identifier is created.

#### JarConfig:
```json
{
    "config_id": "",
    "options": [OptionsOverride]
}
```
- `"config_id"` (optional): An identifier to differentiate the jar configurations during evaluation. Uniqueness is not enforced but strongly recommended, unless this is the only configuration. Defaults to `"unknown"`.
- `"skip"` (optional): If this entry is present and set to any value, this configuration is skipped when running. 
- `"options"` Array containing JSON following the `OptionsOverride` schema describing which JPlag options will be set. Can be left empty to run the default configuration of the jar file.

#### OptionsOverride:
```json
{
    "setter": "",
    "type": "",
    "value": ""
}
```
- `"setter"`: The name of setter contained in the `JPlagOptions` object that will be used when calling JPlag.
- `"type"`: The type of the value that will be set. Supports `int`, `float`, `boolean` and `Enum`. 
To define an `Enum` type, set this value to `"enum:classpath"` where classpath corresponds to the classpath of the `Enum` type definition. Example: `"enum:de.jplag.options.Verbosity"`.
- `"value"`: The value the setter will receive. Must be of the type defined above.
For enums, set this to the name of the enum value. Example: `"LONG"`

#### Full Example
```json
{
  "jars": [
    {
      "file": "jplag-test.jar",
      "commit": "eb7c707655c5d303b06ad720b7f0fdd639619df0",
      "configs": []
    },
    {
      "file": "jplag-test-2.jar",
      "commit": "eb7c707655c5d303b06ad720b7f0fdd639619df0",
      "configs": [
        {
          "config_id": "lower_token_match",
          "options": [
            {
              "setter": "setMinimumTokenMatch",
              "type": "int",
              "value": "4"
            },
            {
              "setter": "setVerbosity",
              "type": "enum:de.jplag.options.Verbosity",
              "value": "LONG"
            }
          ]
        }
      ]
    }
  ]
}
```