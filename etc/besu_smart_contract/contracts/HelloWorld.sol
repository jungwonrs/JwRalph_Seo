pragma solidity ^0.5.0;

contract HelloWorld {
    string public var1 = "hello world! from Nonce Lab.";

    function setString(string memory _var1) public {

        var1 = _var1;

    }
}
