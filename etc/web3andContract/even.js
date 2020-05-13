let Web3 = require('web3');

let web3 = new Web3(new Web3.providers.HttpProvider('https://ropsten.infura.io'));

let ABI = [
	{
		"anonymous": false,
		"inputs": [
			{
				"indexead": false,
				"internalType": "string",
				"name": "var1",
				"type": "string"
			}
		],
		"name": "E_SetString",
		"type": "event"
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
let CA = "0xc8348cEebba9dcc3b919fDA23aD3404BDEb2813C";

//Create Contract Object
let Contract = new web3.eth.Contract(ABI, CA);

//Call Contract
Contract.events.E_SetString().on('data', (event) => {
  console.log('data set: ');
  console.log(event);

  console.log('data extract:' );
  console.log(event.returnValues);

})
