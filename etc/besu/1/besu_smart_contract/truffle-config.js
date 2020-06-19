const PrivateKeyProvider = require('@truffle/hdwallet-provider');
const privateKeys = [
  'userprivatekey1',
  'userprivatekey2',
  'userprivatekey3'
];

const privateKeyProvider = new PrivateKeyProvider (
  privateKeys,
  'http://127.0.0.1:8545',
  0,
  3
);

module.exports = {
  networks: {
    besu: {
      provider: privateKeyProvider,
      network_id: '*'
    }
  }
}
