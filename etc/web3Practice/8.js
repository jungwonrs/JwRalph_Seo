let Web3 = require('web3');

let web3 = new Web3(new Web3.providers.HttpProvider('http://127.0.0.1:8545'));

//web3.personal.newAccount('0x4554e36685a93db14f236520506e80dd7b7eee65', ())
web3.eth.getTransaction('0x61ed23fa2276115e9e397153d84b18d026e1401ce8ac2c33ae63045e72100349', function(err, txHash){

  console.log(txHash);
})
