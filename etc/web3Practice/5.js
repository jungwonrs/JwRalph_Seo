let Web3 = require('web3');

let web3 = new Web3(new Web3.providers.HttpProvider('https://ropsten.infura.io'));

web3.eth.getBlockNumber((err, blockNumber) => {
  console.log(blockNumber);
})
