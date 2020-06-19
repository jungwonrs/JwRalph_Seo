var express = require('express');
var body_parser = require('body-parser');
var ejs = require('ejs');
var app = express();
var path = require('path');
var swaggerUi = require('swagger-ui-express');
var yaml = require('yamljs');
var swaggerDocument = yaml.load('API.yaml');

app.set("view engine", "ejs");
app.use(body_parser.urlencoded({extended:true}));
app.use(body_parser.json());
app.use(express.static(path.join(__dirname, 'public')));

var smart_contract_api = require('./routes/api');

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

app.use('/', function(req, res){
  res.status(404).send("check URL");
})


app.use(function(req, res, next){

  try {
    if (!req.headers.hasOwnProperty('api-key'))
    {
      return res.status(401).send("Wrong API Key or Value").end();
    }
    if (req.header('api-key') != 'one-two-cm')
    {
      return res.status(403).send("Wrong API Key or Value").end();
    }
    next();
  } catch(error){
    res.status(500).send("server error");
  }

})

app.use('/blockchain', smart_contract_api);


var port = process.env.Port || 8888;

var server = app.listen(port, function(){
  console.log ("server working");
});
