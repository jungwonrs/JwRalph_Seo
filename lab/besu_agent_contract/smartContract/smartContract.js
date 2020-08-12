var Web3 = require('web3');
var Tx = require("ethereumjs-tx");
var web3 = new Web3(new Web3.providers.HttpProvider('http://3.35.53.4:8545'));

var ABI = [
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
		"name": "postSale",
		"outputs": [],
		"payable": false,
		"stateMutability": "nonpayable",
		"type": "function"
	}
]

var CA = "0x42699A7612A82f1d9C36148af9C77354759b210b";

var Contract = new web3.eth.Contract(ABI, CA);

module.exports = {
  ABI: ABI,
  CA: CA,
  Contract: Contract
}
