let Web3 = require('web3');

let web3 = new Web3(new Web3.providers.HttpProvider('http://127.0.0.1:8545'));

web3.personal.newAccount('baselab', (err, createdAddress) => {
  console.log(createdAddress);

  let accounts = web3.eth.accounts
  console.log(accounts);


})
