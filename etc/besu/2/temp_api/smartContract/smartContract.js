var Web3 = require('web3');
var Tx = require("ethereumjs-tx");
var web3 = new Web3(new Web3.providers.HttpProvider('http://3.34.169.244:8545'));

var ABI = [
  {
      "constant": false,
      "inputs": [
        {
          "name": "_id",
          "type": "string"
        },
        {
          "name": "_value",
          "type": "string"
        }
      ],
      "name": "postSale",
      "outputs": [],
      "payable": false,
      "stateMutability": "nonpayable",
      "type": "function"
    },
    {
      "constant": true,
      "inputs": [
        {
          "name": "_id",
          "type": "string"
        }
      ],
      "name": "getSale",
      "outputs": [
        {
          "name": "",
          "type": "string"
        }
      ],
      "payable": false,
      "stateMutability": "view",
      "type": "function"
    },
    {
      "constant": false,
      "inputs": [
        {
          "name": "_id",
          "type": "string"
        },
        {
          "name": "_value",
          "type": "string"
        }
      ],
      "name": "postSaleCancel",
      "outputs": [],
      "payable": false,
      "stateMutability": "nonpayable",
      "type": "function"
    },
    {
      "constant": true,
      "inputs": [
        {
          "name": "_id",
          "type": "string"
        }
      ],
      "name": "getSaleCancel",
      "outputs": [
        {
          "name": "",
          "type": "string"
        }
      ],
      "payable": false,
      "stateMutability": "view",
      "type": "function"
    },
    {
      "constant": false,
      "inputs": [
        {
          "name": "_id",
          "type": "string"
        },
        {
          "name": "_value",
          "type": "string"
        }
      ],
      "name": "postUse",
      "outputs": [],
      "payable": false,
      "stateMutability": "nonpayable",
      "type": "function"
    },
    {
      "constant": true,
      "inputs": [
        {
          "name": "_id",
          "type": "string"
        }
      ],
      "name": "getUse",
      "outputs": [
        {
          "name": "",
          "type": "string"
        }
      ],
      "payable": false,
      "stateMutability": "view",
      "type": "function"
    },
    {
      "constant": false,
      "inputs": [
        {
          "name": "_id",
          "type": "string"
        },
        {
          "name": "_value",
          "type": "string"
        }
      ],
      "name": "postUseCancel",
      "outputs": [],
      "payable": false,
      "stateMutability": "nonpayable",
      "type": "function"
    },
    {
      "constant": true,
      "inputs": [
        {
          "name": "_id",
          "type": "string"
        }
      ],
      "name": "getUseCancel",
      "outputs": [
        {
          "name": "",
          "type": "string"
        }
      ],
      "payable": false,
      "stateMutability": "view",
      "type": "function"
    }
  ]

var CA = "0x0f8b2316A67A94DCE794FcE51ba2cBA7788deecE";

var Contract = new web3.eth.Contract(ABI, CA);

module.exports = {
  ABI: ABI,
  CA: CA,
  Contract: Contract
}
