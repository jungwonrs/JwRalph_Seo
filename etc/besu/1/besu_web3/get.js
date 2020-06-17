var Web3 = require('web3');
var Tx = require("ethereumjs-tx");

var web3 = new Web3(new Web3.providers.HttpProvider('http://127.0.0.1:8545'));

var ABI = [
    {
      "constant": true,
      "inputs": [],
      "name": "var1",
      "outputs": [
        {
          "internalType": "string",
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
          "internalType": "string",
          "name": "_var1",
          "type": "string"
        }
      ],
      "name": "setString",
      "outputs": [],
      "payable": false,
      "stateMutability": "nonpayable",
      "type": "function"
    }
  ]


var CA = "0x0f8b2316A67A94DCE794FcE51ba2cBA7788deecE";

var Contract = new web3.eth.Contract(ABI, CA);

Contract.methods.var1().call().then(data => {
  console.log(data);
});
