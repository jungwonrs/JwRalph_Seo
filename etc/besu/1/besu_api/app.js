var express = require('express');
var body_parser = require('body-parser');
var ejs = require('ejs');
var app = express();
var path = require('path');

app.set("view engine", "ejs");
app.use(body_parser.urlencoded({extended:true}));
app.use(body_parser.json());
app.use(express.static(path.join(__dirname, 'public')));

var smart_contract_api = require('./routes/api');


app.use('/', smart_contract_api);

app.use(function(req, res, next){
  next(createError(404));
})


var port = process.env.Port || 8888;

var server = app.listen(port, function(){
  console.log ("server working");
});
