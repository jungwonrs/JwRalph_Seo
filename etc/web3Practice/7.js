let Web3 = require('web3');

let web3 = new Web3(new Web3.providers.HttpProvider('http://127.0.0.1:8545'));

//web3.personal.newAccount('0x4554e36685a93db14f236520506e80dd7b7eee65', ())
web3.eth.sendTransaction({from: '0x4554e36685a93db14f236520506e80dd7b7eee65', to: '0xfa8ce986d481760c7a863eeaa929daa13a724df5', value:100 }, function(err, txHash){

  console.log(txHash);
})
