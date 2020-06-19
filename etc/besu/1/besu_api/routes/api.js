var express = require('express');
var router = express.Router();

var first_smart_contract = require('../smartContract/firstSmartContract');
var call_first_contract = first_smart_contract.Contract;


var Web3 = require('web3');
var Tx = require("ethereumjs-tx");

var web3 = new Web3(new Web3.providers.HttpProvider('http://localhost:8545'));


router.get('/firstSM',function(req,res){
  call_first_contract.methods.var1().call().then(data => {
    res.status(200).json(
    {
      "sucess": data
    }
    );
  })

});

router.post('/firstSM',function(req,res){
  var user_address = "useradress";
  var private_key = "privatekey";
  var message = req.body.message;
  var set_string = call_first_contract.methods.setString(req.body.message);
  var set_string_byte_code = set_string.encodeABI();

  web3.eth.getTransactionCount (user_address, "pending", (err, txCount) =>{
    var raw_tx = {
      nonce: web3.utils.toHex(txCount),
      gasPrice: web3.utils.toHex(1000),
      gasLimit: web3.utils.toHex(126165),
      data: set_string_byte_code,
      from: user_address,
      to: first_smart_contract.CA
    };
    console.log (raw_tx);
    var signature = new Buffer.from(private_key, "hex");
    var make_tx = new Tx(raw_tx);
    make_tx.sign(signature);
    var serialized_tx = make_tx.serialize();
    var raw_tx_hex = '0x'+serialized_tx.toString('hex');

    web3.eth.sendSignedTransaction(raw_tx_hex, (err, txHash) => {
        res.status(200).json(
          {
            "status": "okay!",
            "transaction_Hash": txHash
          }
        );
    });

  })


});

module.exports = router;
