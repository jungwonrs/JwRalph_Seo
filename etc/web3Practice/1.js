let Web3 = require('web3');

let web3 = new Web3(new Web3.providers.HttpProvider('https://mainnet.infura.io'));

web3.eth.getBlockNumber((err, blockCount) =>{
  console.log(blockCount);
})
