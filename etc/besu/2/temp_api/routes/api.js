var express = require('express');
var router = express.Router();

var smart_contract = require('../smartContract/smartContract');
var call_contract = smart_contract.Contract;


var Web3 = require('web3');
var Tx = require("ethereumjs-tx");

var web3 = new Web3(new Web3.providers.HttpProvider('http://3.34.169.244:8545'));

//sale
router.get('/sale', function(req, res){
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


router.post('/sale', function(req, res){
  var id = req.body.id;
  var raw_data = req.body.data;
  var user_address = "dc45BbA03A4CE2c9b27294B5F74B88420269C415";
  var private_key="27A9605556205AEA8BA7D3394E5E805F6D3E18B27725E764038B7C33A8EE29DA";
  var set_string = call_contract.methods.postSale(id, raw_data);
  var set_string_byte_code = set_string.encodeABI();

  if (req.body.id == null){
    res.status(400).send("Check Id");
  }

  web3.eth.getTransactionCount (user_address, "pending", (err, txCount) =>{
    var raw_tx = {
      nonce: web3.utils.toHex(txCount),
      gasPrice: web3.utils.toHex(1000),
      gasLimit: web3.utils.toHex(4294967296),
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



//sale-cancel
router.get('/sale-cancel', function(req, res){
  var id = req.body.id;
  if (req.body.id == null) {
    res.status(400).send("Check Value");
  }

  call_contract.methods.getSaleCancel(id).call().then(data => {
    res.status(200).json(
    {
      "sucess": data
    }
    );
  })
});




router.post('/sale-cancel', function(req, res){
  var id = req.body.id;
  var raw_data = req.body.data;
  var user_address = "dc45BbA03A4CE2c9b27294B5F74B88420269C415";
  var private_key="27A9605556205AEA8BA7D3394E5E805F6D3E18B27725E764038B7C33A8EE29DA";
  var set_string = call_contract.methods.postSaleCancel(id, raw_data);
  var set_string_byte_code = set_string.encodeABI();

  if (req.body.id == null){
    res.status(400).send("Check Id");
  }

  web3.eth.getTransactionCount (user_address, "pending", (err, txCount) =>{
    var raw_tx = {
      nonce: web3.utils.toHex(txCount),
      gasPrice: web3.utils.toHex(1000),
      gasLimit: web3.utils.toHex(4294967296),
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


//use
router.get('/use', function(req,res){
  var id = req.body.id;
  if (req.body.id == null) {
    res.status(400).send("Check Value");
  }

  call_contract.methods.getUse(id).call().then(data => {
    res.status(200).json(
    {
      "sucess": data
    }
    );
  })
});


router.post('/use', function(req, res){
  var id = req.body.id;
  var raw_data = req.body.data;
  var user_address = "dc45BbA03A4CE2c9b27294B5F74B88420269C415";
  var private_key="27A9605556205AEA8BA7D3394E5E805F6D3E18B27725E764038B7C33A8EE29DA";
  var set_string = call_contract.methods.postUse(id, raw_data);
  var set_string_byte_code = set_string.encodeABI();

  if (req.body.id == null){
    res.status(400).send("Check Id");
  }

  web3.eth.getTransactionCount (user_address, "pending", (err, txCount) =>{
    var raw_tx = {
      nonce: web3.utils.toHex(txCount),
      gasPrice: web3.utils.toHex(1000),
      gasLimit: web3.utils.toHex(4294967296),
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



//use-cancel
router.get('/use-cancel', function(req, res){
  var id = req.body.id;
  if (req.body.id == null) {
    res.status(400).send("Check Value");
  }

  call_contract.methods.getUseCancel(id).call().then(data => {
    res.status(200).json(
    {
      "sucess": data
    }
    );
  })
});


router.post('/use-cancel', function(req, res){
  var id = req.body.id;
  var raw_data = req.body.data;
  var user_address = "dc45BbA03A4CE2c9b27294B5F74B88420269C415";
  var private_key="27A9605556205AEA8BA7D3394E5E805F6D3E18B27725E764038B7C33A8EE29DA";
  var set_string = call_contract.methods.postUseCancel(id, raw_data);
  var set_string_byte_code = set_string.encodeABI();

  if (req.body.id == null){
    res.status(400).send("Check Id");
  }

  web3.eth.getTransactionCount (user_address, "pending", (err, txCount) =>{
    var raw_tx = {
      nonce: web3.utils.toHex(txCount),
      gasPrice: web3.utils.toHex(1000),
      gasLimit: web3.utils.toHex(4294967296),
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
