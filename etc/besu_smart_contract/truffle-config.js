const PrivateKeyProvider = require('@truffle/hdwallet-provider');
const privateKeys = [
  '0x27A9605556205AEA8BA7D3394E5E805F6D3E18B27725E764038B7C33A8EE29DA',
  '0xc87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3',
  '0xae6ae8e5ccbfb04590405997ee2d52d2b330726137b875053c36d94e974d162f'
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
