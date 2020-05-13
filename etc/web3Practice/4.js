let Web3 = require('web3');

let web3 = new Web3(new Web3.providers.HttpProvider('http://127.0.0.1:8545'));

web3.eth.getBalance('0x4554e36685a93db14f236520506e80dd7b7eee65', function(error, balance) {
  console.log(balance);
});

/*
let balanceOf = web3.eth.getBalance('0xfa8ce986d481760c7a863eeaa929daa13a724df5')
balanceof.then ((result) => console.log(result))
*/
