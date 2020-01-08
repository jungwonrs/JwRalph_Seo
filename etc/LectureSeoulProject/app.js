var express = require('express');
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var ejs = require('ejs');
var app = express();

app.set("view engine", "ejs");
app.use(bodyParser.urlencoded({extended:true}));
app.use(bodyParser.json());
app.use(express.static(__dirname+"/"));

//MogoDB Connection
mongoose.connect('mongodb://localhost:27017/testDB');
const db = mongoose.connection;

//MogoDB Schema to connect collections
const loginSchema = new mongoose.Schema({
  id: String,
  pwd: String,
  position: String,
  status: String
});

const regSchema = new mongoose.Schema({
  date: Date,
  location: String,
  driver: String,
  fork: String,
  forkKg: Number
});

const driverLocation = new mongoose.Schema({
  orderDate: Date,
  location: String,
  time: Number,
  driver: String,
  fork: String,
  forkKg: Number
});


//connect to "login" collections
const model = mongoose.model("Login",loginSchema,'login');
//connect to "orderInfo" collections
const model2 = mongoose.model("OrderInfo", regSchema);
//connect to "DeliveryStartTime" collections
const model3 = mongoose.model("DeliveryStartTime", driverLocation);
//connect to "DeliveryDoneTime" collections
const model4 = mongoose.model("DeliveryDoneTime", driverLocation);
//connect to "RetrunTime"
const model5 = mongoose.model("RetrunTime", driverLocation);

//set server port
var port = process.env.Port || 8888;

// connect to first page
app.get('/', function(req, res){
  res.render("Login");
});

app.post('/', function(req, res){
  //login
  model.find({id:req.body.id, pwd:req.body.pwd}, 'position', function(err, doc){

    var data = JSON.stringify(doc);
    if (data == '[]'){
      res.send("check id or password");
      }

    //manager check
    if (data.includes('manager')){
      res.redirect('/manager');
    }

    //driver check
    if (data.includes('driver')){
      var driverName = req.body.id;
      //console.log(driverName);
      res.redirect('/driver');

      app.get('/driver', function(req, res){
        res.render('Driver', {name: driverName});

        app.post('/driver', function(req, res){
          var inputValue = req.body.button;

            if (inputValue == "assgined"){
              res.redirect('/driver/assginedView');
            }
            if (inputValue == "DeliveryStart"){
              res.redirect('/driver/deliveryStart');
            }
            if (inputValue == "DeliveryDone"){
            res.redirect('/driver/deliveryDone');
          }
          if (inputValue == "Return"){
            res.redirect('/driver/return');
          }

          app.get('/driver/assginedView', function(req, res){
            model2.find({driver:driverName}, function(err, doc){
            var date = doc.map(function(obj) {
              return obj.date;
              });
            var location = doc.map(function(obj) {
                return obj.location;
              });
            var driver = doc.map(function(obj) {
                return obj.driver;
              });
            var fork = doc.map(function(obj) {
                return obj.fork;
              });
            var forkKg = doc.map(function(obj) {
                return obj.forkKg;
              });

              res.render('AssignedView', {date:date, location:location, driver:driver, fork:fork, forkKg:forkKg});
              //order confirm
              app.post('/driver/assginedView', function(req, res){
                model.findOne({id:driver}, function(err, doc){
                  doc.status = "orderForm-Confirm";
                  doc.save();
                  res.send("okay! good!!");
                });
              })
          });
          });

          //deliverStart
          app.get('/driver/deliveryStart', function(req, res){
            model2.find({driver:driverName}, function(err, doc){
            var date = doc.map(function(obj) {
              return obj.date;
              });
            var location = doc.map(function(obj) {
                return obj.location;
              });
            var driver = doc.map(function(obj) {
                return obj.driver;
              });
            var fork = doc.map(function(obj) {
                return obj.fork;
              });
            var forkKg = doc.map(function(obj) {
                return obj.forkKg;
              });
            var ts = Date.now();

              res.render('DeliveryStart', {date:date, location:location, time:ts, driver:driver, fork:fork, forkKg:forkKg});

              app.post('/driver/deliveryStart', function(req, res){
                model.findOne({id:driver}, function(err, doc){
                  doc.status = "driving";
                  doc.save();

                  var driverStartTime = new model3();
                  driverStartTime.orderDate = date;
                  driverStartTime.location = location.join();
                  driverStartTime.time = ts;
                  driverStartTime.driver = driver.join();
                  driverStartTime.fork = fork.join();
                  driverStartTime.forkKg = forkKg.join();

                  driverStartTime.save(function(err){
                    if(err){
                      console.error(err);
                      return;
                    }
                    res.send("safe driving please~");
                  });
                });
              })
          });
          });
          //DeliveryDone
          app.get('/driver/deliveryDone', function(req, res){
            model2.find({driver:driverName}, function(err, doc){
            var date = doc.map(function(obj) {
              return obj.date;
              });
            var location = doc.map(function(obj) {
                return obj.location;
              });
            var driver = doc.map(function(obj) {
                return obj.driver;
              });
            var fork = doc.map(function(obj) {
                return obj.fork;
              });
            var forkKg = doc.map(function(obj) {
                return obj.forkKg;
              });
            var ts = Date.now();

              res.render('DeliveryDone', {date:date, location:location, time:ts, driver:driver, fork:fork, forkKg:forkKg});

              app.post('/driver/deliveryDone', function(req, res){
                model.findOne({id:driver}, function(err, doc){
                  doc.status = "Delivery-Done";
                  doc.save();

                  var driverStartTime = new model4();
                  driverStartTime.orderDate = date;
                  driverStartTime.location = location.join();
                  driverStartTime.time = ts;
                  driverStartTime.driver = driver.join();
                  driverStartTime.fork = fork.join();
                  driverStartTime.forkKg = forkKg.join();

                  driverStartTime.save(function(err){
                    if(err){
                      console.error(err);
                      return;
                    }
                    res.send("good job comeback~");
                  });
                });
              })
          });
          });

          //arrived
          app.get('/driver/return', function(req, res){
            model2.find({driver:driverName}, function(err, doc){
            var date = doc.map(function(obj) {
              return obj.date;
              });
            var location = doc.map(function(obj) {
                return obj.location;
              });
            var driver = doc.map(function(obj) {
                return obj.driver;
              });
            var fork = doc.map(function(obj) {
                return obj.fork;
              });
            var forkKg = doc.map(function(obj) {
                return obj.forkKg;
              });
            var ts = Date.now();

              res.render('Return', {date:date, location:location, time:ts, driver:driver, fork:fork, forkKg:forkKg});

              app.post('/driver/return', function(req, res){
                model.findOne({id:driver}, function(err, doc){
                  doc.status = "on";
                  doc.save();

                  var driverStartTime = new model5();
                  driverStartTime.orderDate = date;
                  driverStartTime.location = location.join();
                  driverStartTime.time = ts;
                  driverStartTime.driver = driver.join();
                  driverStartTime.fork = fork.join();
                  driverStartTime.forkKg = forkKg.join();

                  driverStartTime.save(function(err){
                    if(err){
                      console.error(err);
                      return;
                    }
                    res.send("take a rest");
                  });
                });
              })
          });
          });

        });
      });
    }
  });
});

app.get('/manager', function(req, res){
  res.render('Manager');
});


app.post('/manager',function(req,res){
  res.redirect('/orderFormView');
});

app.get('/orderFormView', function(req,res){
  model.find({position:"driver", status:"on"}, 'id', function(err, doc){

    var getId = doc.map(function(obj) {
      return obj.id;
    });

    res.render('orderFormView', {driverlist: getId});

});
});



app.post('/orderFormView', function(req,res){
  var inputValue = req.body.button;
  if (inputValue == "confirm"){
    var registerForm = new model2();
    registerForm.date = req.body.date;
    registerForm.location =req.body.location;
    registerForm.driver = req.body.driver;
    registerForm.fork = req.body.fork;
    registerForm.forkKg = req.body.kg;

    registerForm.save(function(err){
      if(err){
        console.error(err);
        res.send("form error");
        return;
      }
      res.send("form is registered")
    });
  }

  if (inputValue == "cancle"){
    res.redirect('/');
  }
});


var server = app.listen(port, function(){
  console.log("server working");
});
