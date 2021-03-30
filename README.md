# json-schema-validator

a way to check complex input using a json schema

https://json-schema.org/

the function validateJson will throw if the input doesnt match the schema passed ( and thus pipeline will fail )

## prerequisites : 

install the shared library in jenkins

( System Configuration -> System configuration -> in Global Pipeline Libraries -> add )

- modern scm
- github
- Repository HTTPS URL  : https://github.com/jgibart/json-schema-validator.git
- Version : main


## caviats : 

When input buffer is too big ( understand really huge, limit unknown but over  80K ) run fails saying
```
ERROR: Workspace has a .git repository, but it appears to be corrupt.
hudson.plugins.git.GitException: Error performing git command: git rev-parse --is-inside-work-tree
...
Caused by: java.io.IOException: Cannot run program "git" (in directory "/var/jenkins_home/workspace/TestPipeline@libs/json_schema_validator"): error=7, Argument list too long
```


## example pipeline : 

```
@Library('json-schema-validator')_
 

properties([
  parameters([
    text(name: 'JSON_INPUT', defaultValue: '  [ {"name" : "rule1", "ipAddress" : "10.2.5.20" , "priority": 10 } ] ', description: 'the json complex input', )
   ])
])

  
node {
    
def schema = '''{
      "type": "array",
      "items": {
        "type" : "object",
        "properties" : {
            "priority" : {"type" : "number"},
            "ipAddress" : {"type" : "string", "pattern": "^(?:[0-9]{1,3}\\\\.){3}[0-9]{1,3}$" },
            "port" : {"type" : "number"},
            "name" : {"type" : "string"}
        },
         "required": [ "priority", "name", "ipAddress" ]
      }
}'''

 
validateJson(params.JSON_INPUT , schema)


 }

```

