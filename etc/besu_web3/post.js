var Web3 = require('web3');
var Tx = require("ethereumjs-tx");

var web3 = new Web3(new Web3.providers.HttpProvider('http://localhost:8545'));

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

var User_Address = "dc45BbA03A4CE2c9b27294B5F74B88420269C415";
var Private_Key = "27A9605556205AEA8BA7D3394E5E805F6D3E18B27725E764038B7C33A8EE29DA"

var Set_String = Contract.methods.setString("hello world from Nonce Lab.!!! This is Work!!!");
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
