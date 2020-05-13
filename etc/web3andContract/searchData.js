let Web3 = require('web3');
const Tx = require("ethereumjs-tx");

let web3 = new Web3(new Web3.providers.HttpProvider('https://ropsten.infura.io'));

let ABI = [
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
	},
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
	}
]

//Smart Contract address
let CA = "0x02C718D26a4e0845Cd5f34014052F671AcDc82d7";

//Create Contract Object
let Contract = new web3.eth.Contract(ABI, CA);

//Call Contract
Contract.methods.var1().call().then(data => {
  console.log(data);
});
