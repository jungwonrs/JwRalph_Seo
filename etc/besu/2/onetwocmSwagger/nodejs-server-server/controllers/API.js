'use strict';

var utils = require('../utils/writer.js');
var API = require('../service/APIService');

module.exports.getSale = function getSale (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.getSale(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getSaleCancel = function getSaleCancel (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.getSaleCancel(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getUse = function getUse (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.getUse(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getUseCancel = function getUseCancel (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.getUseCancel(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.postSale = function postSale (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.postSale(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.postSaleCancel = function postSaleCancel (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.postSaleCancel(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.postUse = function postUse (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.postUse(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.postUseCancel = function postUseCancel (req, res, next) {
  var headers = req.swagger.params['Headers'].value;
  var body = req.swagger.params['Body'].value;
  API.postUseCancel(headers,body)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};
