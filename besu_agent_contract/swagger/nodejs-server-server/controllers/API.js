'use strict';

var utils = require('../utils/writer.js');
var API = require('../service/APIService');

module.exports.rootGET = function rootGET (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.rootGET(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.rootPOST = function rootPOST (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.rootPOST(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};
