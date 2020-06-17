pragma solidity ^0.4.22;

contract oneTwoCM {

    struct saleRawData {
        string data;
        bool exist;
    }

    struct saleCancelRawData {
        string data;
        bool exist;
    }

    struct useRawData {
        string data;
        bool exist;
    }

    struct useCancelRawData {
        string data;
        bool exist;
    }


    mapping (string => saleRawData) saleMap;
    mapping (string => saleCancelRawData) saleCancelMap;
    mapping (string => useRawData) useMap;
    mapping (string => useCancelRawData) useCancelMap;


    //sale
    function postSale(string  _id, string  _value) public {

        var saleRawData = saleMap[_id];

        require (saleRawData.exist == false, "ID already exist");

        saleRawData.data = _value;
        saleRawData.exist = true;

    }

    function getSale(string _id) view public returns (string){
        return (saleMap[_id].data);
    }


    //sale_cancle
    function postSaleCancel(string  _id, string  _value) public {

        var saleCancelRawData = saleCancelMap[_id];

        require (saleCancelRawData.exist == false, "ID already exist");

        saleCancelRawData.data = _value;
        saleCancelRawData.exist = true;

    }

    function getSaleCancel(string _id) view public returns (string){
        return (saleCancelMap[_id].data);
    }

    //use
    function postUse(string  _id, string  _value) public {

        var useRawData = useMap[_id];

        require (useRawData.exist == false, "ID already exist");

        useRawData.data = _value;
        useRawData.exist = true;

    }

    function getUse(string _id) view public returns (string){
        return (useMap[_id].data);
    }



    //use_cancel
     function postUseCancel(string  _id, string  _value) public {

        var useCancelRawData = useCancelMap[_id];

        require (useCancelRawData.exist == false, "ID already exist");

        useCancelRawData.data = _value;
        useCancelRawData.exist = true;

    }

    function getUseCancel(string _id) view public returns (string){
        return (useCancelMap[_id].data);
    }


}
