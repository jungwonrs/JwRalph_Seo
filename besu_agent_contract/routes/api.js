var express = require('express');
var router = express.Router();

var smart_contract = require('../smartContract/smartContract');
var call_contract = smart_contract.Contract;


var Web3 = require('web3');
var Tx = require("ethereumjs-tx");

var web3 = new Web3(new Web3.providers.HttpProvider('http://3.35.53.4:8545'));


router.get('/data',function(req,res){
  var id = req.body.id;
  if (req.body.id == null) {
    res.status(400).send("Check Value");
  }

  call_contract.methods.getSale(id).call().then(data => {
    res.status(200).json(
    {
      "sucess": data
    }
    );
  })
});

router.post('/data',function(req,res){
  var id = req.body.id;
  var raw_data = req.body.data;

  console.log(id);
  console.log(raw_data);
  var user_address = "627306090abaB3A6e1400e9345bC60c78a8BEf57";
  var private_key="c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3";
  var set_string = call_contract.methods.postSale(id, raw_data);
  var set_string_byte_code = set_string.encodeABI();

  if (req.body.id == null){
    res.status(400).send("Check Id");
  }

  web3.eth.getTransactionCount (user_address, "pending", (err, txCount) =>{
    var raw_tx = {
      nonce: web3.utils.toHex(txCount),
      gasPrice: web3.utils.toHex(1000),
      gasLimit: web3.utils.toHex(4700000),
      data: set_string_byte_code,
      to: smart_contract.CA
    };

    var signature = new Buffer.from(private_key, "hex");
    var make_tx = new Tx(raw_tx);
    make_tx.sign(signature);
    var serialized_tx = make_tx.serialize();
    var raw_tx_hex = '0x'+serialized_tx.toString('hex');

web3.eth.sendSignedTransaction(raw_tx_hex)
.on('error', function(error){
      console.log(error);
  res.status(400).json({
    "status": "Please Check ID or Data Format / Length",
    "error": error
  });
})
.once('receipt', function(receipt){
  if(receipt.status == true){
    res.status(200).json({
      "status": "okay!",
      "transaction_Hash": receipt.transactionHash
    });
  };
});
})
});

router.get('*', function(req, res){
  res.status(404).send("check URL");
});


module.exports = router;
