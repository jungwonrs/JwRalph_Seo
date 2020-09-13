var Web3 = require('web3');
var Tx = require("ethereumjs-tx");

var web3 = new Web3(new Web3.providers.HttpProvider('http://localhost:8545'));

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

var User_Address = "fe3b557e8fb62b89f4916b721be55ceb828dbd73";
var Private_Key = "8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"



var Set_String = Contract.methods.postSale("1", "This is Seo and this is testing!!!!!!!");

var Set_String_Btye_Code = Set_String.encodeABI();

web3.eth.getTransactionCount (User_Address, "pending", (err, nonce) => {
  var Raw_Tx = {
    nonce: web3.utils.toHex(nonce),
    gasPrice: web3.utils.toHex(1000),
    gasLimit: web3.utils.toHex(126165),
    data: Set_String_Btye_Code,
    from: User_Address,
    to: CA
  };

  var Signature = new Buffer.from(Private_Key, "hex");

  var Make_Tx = new Tx(Raw_Tx);
  Make_Tx.sign(Signature);

  var Serialized_Tx = Make_Tx.serialize();
  var Raw_Tx_Hex = '0x' + Serialized_Tx.toString('hex');

  web3.eth.sendSignedTransaction(Raw_Tx_Hex, (err, txHash) => {
    console.log(err);
    console.log(txHash);
  })

})
