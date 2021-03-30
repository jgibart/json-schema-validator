
@Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.11.4')
import com.fasterxml.jackson.databind.ObjectMapper

@Grab(group='com.fasterxml.jackson.core', module='jackson-core', version='2.11.4')
import com.fasterxml.jackson.core.JsonFactory

@Grab(group='com.github.fge', module='json-schema-validator', version='2.2.5')
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.exceptions.ProcessingException
import com.github.fge.jsonschema.main.JsonSchemaFactory

 
@NonCPS
def call(String payload, String schema) {
  def mapper = new ObjectMapper()
  def jsonFactory = new JsonFactory()
  def jsonSchemaFactory = JsonSchemaFactory.byDefault()
  def schemaJsonNode = null
  try {
      schemaJsonNode = mapper.readTree(jsonFactory.createParser(schema))
  } catch (Exception e) {
    print "invalid json in passed schema $schema"
    throw new ProcessingException('Invalid json in passed schema.', e )
  }
  def jsonSchema = null
  try {
     jsonSchema =jsonSchemaFactory.getJsonSchema(schemaJsonNode)
  } catch (Exception e) {
    print "not a valid json schema $schema"
    throw new ProcessingException('not a valid json schema.', e )
  }
  def payloadJsonNode = null
  try {
      payloadJsonNode = mapper.readTree(jsonFactory.createParser(payload))
  } catch (Exception e) {
    print "invalid json in payload $payload"
    throw new ProcessingException('invalid json in payload.', e )
  }
  def report = jsonSchema.validate(payloadJsonNode)
  if ( !report.isSuccess() ) {
    for ( message in report ) {
      print "$message"
    }
    throw new ProcessingException('Failure validating Pulse payload against schema.')
  } else {
    print 'Sucessfully validated payload against schema.'
  }
}

/*

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

def payload = ''' [ {"name" : "rule1", "ipAddress" : "10.2.5.2000" , "priority": 10 } ]'''

validate(payload, schema)

*/