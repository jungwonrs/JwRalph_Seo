let Web3 = require('web3');
const Tx = require("ethereumjs-tx").Transaction;

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

//user address
let EOA1 = "0xdc45BbA03A4CE2c9b27294B5F74B88420269C415";
//user private Key
let PRIVATE_KEY = '27a9605556205aea8ba7d3394e5e805f6d3e18b27725e764038b7c33a8ee29da';

//call bytecode
let setStringExec = Contract.methods.setString("hello i am sending Transaction");
let setStringByteCode = setStringExec.encodeABI();

const GWei = 9;
const uint = 10 ** GWei;
const gasLimit = 310000;
const gasPrice = 21 * uint;

web3.eth.getTransactionCount (EOA1, "pending", (err, nonce) => {
  let rawTx = {
    nonce: nonce,
    gasPrice: gasPrice,
    gasLimit: gasLimit,
    data: setStringByteCode,
    from: EOA1,
    to: CA
  };


  let privateKey = new Buffer.from(PRIVATE_KEY, "hex");

  let tx = new Tx(rawTx, {'chain':'ropsten'});
  tx.sign(privateKey);

  let serializedTx = tx.serialize();

  web3.eth.sendSignedTransaction("0x" + serializedTx.toString("hex"), (err, txHash) =>{
    console.log(err);
    console.log(txHash);
  })
});
