package main

import (
	"net/url"
	"strconv"
	"io/ioutil"
	"net/http"
	"crypto/elliptic"
	"crypto/ecdsa"
	"crypto/rand"
	"encoding/hex"
	"fmt"
	"encoding/json"
	"html/template"
	"strings"
	"crypto/sha256"
	"time"
	"database/sql"
	_ "github.com/go-sql-driver/mysql"
)
//default: buyer balance = 100
func PostBuyer(Fname string, Lname string, Email string, Psw string) {
	pubkeyCurve := elliptic.P256()
	privatekey := new(ecdsa.PrivateKey)
	privatekey, err := ecdsa.GenerateKey(pubkeyCurve, rand.Reader)

	var pubkey ecdsa.PublicKey
	pubkey = privatekey.PublicKey


	resp, err := http.PostForm("http://163.239.200.189:3000/api/org.example.empty.Buyer", url.Values{
		"$class": {"org.example.empty.Buyer"},
		"balance": {strconv.Itoa(100)},
		"public_key": {hex.EncodeToString(pubkey.Y.Bytes())},
		"firstName":   {Fname},
		"lastName": {Lname},
		"email": {Email},
		"password" : {Psw}})

	if err != nil {
		panic(err)
		defer resp.Body.Close()

		respBody, err := ioutil.ReadAll(resp.Body)
		if err == nil {
			str := string(respBody)
			println(str)
		}
	}
}

//default: Producer balance = 10
func PostProducer (Fname string, Lname string, Email string, Psw string) {
	pubkeyCurve := elliptic.P256()
	privatekey := new(ecdsa.PrivateKey)
	privatekey, err := ecdsa.GenerateKey(pubkeyCurve, rand.Reader)

	var pubkey ecdsa.PublicKey
	pubkey = privatekey.PublicKey

	resp, err := http.PostForm("http://163.239.200.189:3000/api/org.example.empty.Producer", url.Values{
		"$class": {"org.example.empty.Producer"},
		"balance": {strconv.Itoa(10)},
		"public_key": {hex.EncodeToString(pubkey.Y.Bytes())},
		"firstName":   {Fname},
		"lastName": {Lname},
		"email": {Email},
		"password" : {Psw}})

	if err != nil {
		panic(err)
		defer resp.Body.Close()

		respBody, err := ioutil.ReadAll(resp.Body)
		if err == nil {
			str := string(respBody)
			println(str)
		}
	}
}

func GetBuyerID(Id string) [] byte {
	resp, err := http.Get("http://163.239.200.189:3000/api/org.example.empty.Buyer/"+Id)
	if err != nil {
		panic (err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		panic(err)
	}

	return data
}

func GetProducerID(Id string) [] byte {
	resp, err := http.Get("http://163.239.200.189:3000/api/org.example.empty.Producer/"+Id)
	if err != nil {
		panic (err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		panic(err)
	}

	return data
}
func GetBuyerEmail(Email string) [] byte {
	resp, err := http.Get("http://163.239.200.189:3000/api/queries/selectBuyerByemail?eamil="+Email)
	if err != nil {
		panic (err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		panic(err)
	}
	return data
}
func GetProducerEmail(Email string) [] byte {
	resp, err := http.Get("http://163.239.200.189:3000/api/queries/selectProducerByemail?eamil="+Email)
	if err != nil {
		panic (err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		panic(err)
	}
	return data
}
func PostProduct (Kw string,Hash string, Abs string, pk string){
	fmt.Println("kw" + Kw)
	fmt.Println("hash" +  Hash)
	fmt.Println("abs"+ Abs)
	fmt.Println("pk"+pk)

	resp, err := http.PostForm("http://163.239.200.189:3000/api/org.example.empty.Product", url.Values{
		"$class":     {"org.example.empty.Product"},
		"key_word":    {Kw},
		"data_hash":   {Hash},
		"abstract":   {Abs},
		"producer_public_key":   {pk}})

	if err != nil {
		panic(err)

		defer resp.Body.Close()

		// Response 체크.
		respBody, err := ioutil.ReadAll(resp.Body)
		if err == nil {
			str := string(respBody)
			println(str)
		}
	}

}
func GetProduct (hash string) [] byte{
	resp, err := http.Get("http://163.239.200.189:3000/api/org.example.empty.Product/"+ hash)
	if err != nil {
		panic (err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		panic(err)
	}
	return data
}

func PostRtypeTx(Kw string, Hash string, Price string, Id string){

	resp, err := http.PostForm("http://163.239.200.189:3000/api/org.example.empty.Rtx", url.Values{

		"$class": {"org.example.empty.Rtx"},
		"price": {Price},
		"key_word":   {Kw},
		"data_hash": {Hash},
		"producer_public_key": {Id}})

	if err != nil {
		panic(err)
		defer resp.Body.Close()

		respBody, err := ioutil.ReadAll(resp.Body)
		if err == nil {
			str := string(respBody)
			println(str)
		}
	}

}
func GetRtypeTx(TxId string) []byte{
	resp, err := http.Get("http://163.239.200.189:3000/api/org.example.empty.Rtx/"+TxId)
	if err != nil {
		panic (err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		panic(err)
	}
	return data
}

func GetAllRtypeTx() [] byte {

	resp, err := http.Get("http://163.239.200.189:3000/api/org.example.empty.Rtx")
	if err != nil {
		panic(err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		panic(err)
	}
	return data

}

func PostBtypeTx(TxId string, Price int, BuyerPublicKey string){
	RtypeData := GetRtypeTx(TxId)
	BuyerData := GetBuyerID(BuyerPublicKey)

	type RtypeTx struct {
		Price             int    `json:"price"`
		ProducerPublicKey string `json:"producer_public_key"`
		DataHash          string `json:"data_hash"`
		TransactionId     string `json:"transactionId"`
	}
	var rtx RtypeTx
	err := json.Unmarshal(RtypeData, &rtx)
	if err != nil{
		fmt.Println("json error", err)
	}
	Rprice := rtx.Price
	RproducerPublicKey := rtx.ProducerPublicKey
	RtransactionId := rtx.TransactionId
	//RealDataHash := "resource:org.example.empty.Product#"+DataHash
	//RdataHash := rtx.DataHash


	type Buyer struct {
		BuyerPublicKey     string    `json:"public_key"`
	}
	var b Buyer
	_ : json.Unmarshal(BuyerData, &b)

	if b.BuyerPublicKey != ""{
		if Rprice == Price{
				resp, err := http.PostForm("http://163.239.200.189:3000/api/org.example.empty.Btx", url.Values{
					"$class": {"org.example.empty.Btx"},
					"price": {strconv.Itoa(Price)},
					"R_type_id":  {RtransactionId},
					"buyer_public_key":   {BuyerPublicKey},
					"producer_public_key": {RproducerPublicKey}})

				if err != nil {
					panic(err)
					defer resp.Body.Close()

					respBody, err := ioutil.ReadAll(resp.Body)
					if err == nil {
						str := string(respBody)
						println(str)
					}
				}

		} else {
			fmt.Println("price error")
		}
	} else {
		fmt.Println("buyer public key error")
	}

}

func GetBtypeTx(TxId string) [] byte{
	resp, err := http.Get("http://163.239.200.189:3000/api/org.example.empty.Btx/"+TxId)
	if err != nil {
		panic (err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		panic(err)
	}
	return data
}

func GetAllBtx () [] byte {
	resp, err := http.Get("http://163.239.200.189:3000/api/org.example.empty.Btx/")
	if err != nil {
		panic (err)
	}
	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		panic(err)
	}
	return data
}

func PostCtypeTx(TxId string, Price int){
	BtypeData := GetBtypeTx(TxId)

	type BtypeTx struct {
		Price             int    `json:"price"`
		BuyerPublicKey    string `json:"buyer_public_key"`
		ProducerPublicKey string `json:"producer_public_key"`
		TransactionId     string `json:"transactionId"`
	}

	var btx BtypeTx
	err := json.Unmarshal(BtypeData, &btx)
	if err != nil {
		fmt.Println("json error", err)
	}

	Bprice := btx.Price
	BuyerPublicKey := btx.BuyerPublicKey
	BtransactionId := btx.TransactionId
	BproducerPublicKey := btx.ProducerPublicKey

	if Bprice == Price{
		resp, err := http.PostForm("http://163.239.200.189:3000/api/org.example.empty.Ctx", url.Values{
			"$class": {"org.example.empty.Ctx"},
			"B_type_id": {BtransactionId},
			"price": {strconv.Itoa(Price)},
			"buyer_public_key":   {BuyerPublicKey},
			"producer_public_key": {BproducerPublicKey}})

		if err != nil {
			panic(err)
			defer resp.Body.Close()

			respBody, err := ioutil.ReadAll(resp.Body)
			if err == nil {
				str := string(respBody)
				println(str)
			}
		}
	} else {
		fmt.Println("price error")
	}
}

func Login(w http.ResponseWriter, r *http.Request){
	t, _ := template.ParseFiles("Login.html")
	t.Execute(w, nil)
}

func selab(w http.ResponseWriter, r *http.Request)  {
	r.ParseForm()
	uname := r.Form["uname"]
	psw := r.Form["psw"]
	position := r.Form["position"]

	stUname := strings.Join(uname, "")
	stPsw := strings.Join(psw, "")
	stPosition := strings.Join(position, "")

	switch stPosition {

	case "Buyer":
		GetBuyerInfo := GetBuyerEmail(stUname)

		type Buyer struct {
			PublicKey string `json:"public_key"`
			Email     string `json:"email"`
			Password  string `json:"password"`
			Balance   int    `json:"balance"`
		}
		var BuyerInfo [] Buyer
		json.Unmarshal(GetBuyerInfo, &BuyerInfo)

		if len(BuyerInfo) > 0 {
			if BuyerInfo[0].Password != stPsw {
				fmt.Fprint(w, "<html><body>password error</body></html>")
				// no answer
			} else if BuyerInfo[0].Password == stPsw {
				/*t, _ := template.ParseFiles("BuyerMain.html")
				//t.ExecuteTemplate(w, "BuyerMain.html",BuyerInfo[0])*/
				BuyerMain(w, r)
			}
		} else {
			fmt.Fprint(w, "<html><body>id error </body></html>")
		}
	case "Producer":
		GetProducerInfo := GetProducerEmail(stUname)

		type Producer struct {
			PublicKey string `json:"public_key"`
			Email     string `json:"email"`
			Password  string `json:"password"`
			Balance   int    `json:"balance"`
		}
		var ProducerInfo [] Producer
		json.Unmarshal(GetProducerInfo, &ProducerInfo)

		if len(ProducerInfo) > 0 {
			if ProducerInfo[0].Password != stPsw {
				fmt.Fprint(w, "<html><body>password error</body></html>")
				// no answer
			} else if ProducerInfo[0].Password == stPsw {
				t, _ := template.ParseFiles("ProductRegister.html")
				t.ExecuteTemplate(w, "ProductRegister.html",ProducerInfo[0])
			}
		} else {
			fmt.Fprint(w, "<html><body>id error </body></html>")
		}
	}

}

func signup(w http.ResponseWriter, r *http.Request){
	t, _ := template.ParseFiles("SignUp.html")
	t.Execute(w, nil)

	r.ParseForm()
	fname := r.Form["fName"]
	lname := r.Form["lName"]
	email := r.Form["email"]
	psw := r.Form["password"]
	position := r.Form["position"]

	stFname := strings.Join(fname, "")
	stLname := strings.Join(lname, "")
	stEmail := strings.Join(email, "")
	stPsw := strings.Join(psw, "")
	stPosition := strings.Join(position, "")

	switch stPosition {

	case "Buyer":
		GetBuyerInfo := GetBuyerEmail(stEmail)

		type Buyer struct {
			Email     string `json:"email"`
		}
		var BuyerInfo [] Buyer
		json.Unmarshal(GetBuyerInfo, &BuyerInfo)

		if len(BuyerInfo) > 0  {
			fmt.Fprint(w, "<html><body>id already exist </body></html>")
		}else{
			PostBuyer(stFname, stLname, stEmail, stPsw)
			fmt.Fprint(w, "<html><body>good </body></html>")
		}
	case "Producer":
		GetProducerInfo := GetProducerEmail(stEmail)

		type Producer struct {
			Email     string `json:"email"`
		}
		var ProducerInfo [] Producer
		json.Unmarshal(GetProducerInfo, &ProducerInfo)

		if len(ProducerInfo) > 0  {
			fmt.Fprint(w, "<html><body>id already exist </body></html>")
		}else{
			PostProducer(stFname, stLname, stEmail, stPsw)
			fmt.Fprint(w, "<html><body>good </body></html>")
		}
	}

}
func makeDatahash(a string) [32]byte {
	var dataHash = sha256.Sum256([]byte(a))
	return dataHash
}


func ProductRegister(w http.ResponseWriter, r *http.Request) {
	file, _, err := r.FormFile("file")
	if err != nil {
		return
	}
	defer file.Close()
	b, _ := ioutil.ReadAll(file)

	r.ParseForm()
	keyword := r.Form["keyword"]
	abstract := r.Form["abstract"]
	pubkey := r.Form["pubkey"]
	price := r.Form["price"]

	stKeyword := strings.Join(keyword, "")
	stAbstract := strings.Join(abstract, "")
	stPubkey := strings.Join(pubkey, "")
	stPrice := strings.Join(price, "")

	Utime := time.Now().Unix()

	data_hash:=makeDatahash(fmt.Sprintf(string(b), Utime, pubkey))
	stHash := hex.EncodeToString(data_hash[:])

	fmt.Println(abstract)
	fmt.Println(stAbstract)
	//fmt.Println(hex.EncodeToString(data_hash[:]))

	/*fmt.Println(stKeyword)
	fmt.Println(stHash)
	fmt.Println(stAbstract)
	fmt.Println(stPubkey)
	fmt.Println(stPrice)
*/
	PostProduct(stKeyword, stHash, stAbstract, stPubkey)
	//GetProduct()
	PostRtypeTx(stKeyword, stHash,stPrice, stPubkey)
	//GetRtypeTx()
	fmt.Fprint(w, "<html><body>Success </body></html>")

	db, err := sql.Open("mysql", "root:root@/dataexchange")

	if err != nil{
		fmt.Println(err)
	}

	fmt.Println(stHash)
	fmt.Println(b)

	result, err := db.Exec("INSERT INTO new_table VALUES(?,?)",stHash ,string(b))
	if err != nil{
		fmt.Println(err)
	}

	result.RowsAffected()
	defer db.Close()

}

//Todo: add buyer's balance
func BuyerMain(w http.ResponseWriter, r *http.Request) {
	t, _ := template.ParseFiles("BuyerMain.html")
	stUserId := r.PostFormValue("uname")
	fmt.Println(stUserId)
	getBuyer := GetBuyerEmail(stUserId)

	type Buyer struct {
		PublicKey string `json:"public_key"`
		Email     string `json:"email"`
		Password  string `json:"password"`
		Balance   int    `json:"balance"`
	}
	var BuyerInfo [] Buyer
	err1 := json.Unmarshal(getBuyer, &BuyerInfo)
	if err1 != nil {
		fmt.Println("error:", err1)
	}
	if len(BuyerInfo) > 0 {
		t.ExecuteTemplate(w, "BuyerMain.html", BuyerInfo[0])
	}
	}


func BuyerDetail(w http.ResponseWriter, r *http.Request) {
	t, _ := template.ParseFiles("BuyerDetail.html")
	BuyerBalance := r.FormValue("balance")
	BuyerKey := r.FormValue("publicKey")
	KeyWord := r.FormValue("keyword")
	/*fmt.Println(r.FormValue("balance"))
	fmt.Println(r.FormValue("publicKey"))
	fmt.Println(r.FormValue("keyword"))*/

	GetRx := GetAllRtypeTx()
	type RtypeTx struct {
		BuyerBalance      string
		BuyerKey          string
		Abstract          string
		ProducerName      string
		Class             string `json:"$class"`
		Price             int    `json:"price"`
		Keyword           string `json:"key_word"`
		DataHash          string `json:"data_hash"`
		ProducerPublicKey string `json:"producer_public_key"`
		TransactionId     string `json:"transactionId"`
		Timestamp         string `json:"timestamp"`
	}
	var rtx []RtypeTx
	err := json.Unmarshal(GetRx, &rtx)
	if err != nil {
		fmt.Println("error:", err)
	}

	var count = 0
	for index := 0; index < len(rtx); index++ {
		if KeyWord == rtx[index].Keyword {
			count++
		}
	}

	tlist := make([]RtypeTx, count)

	count = 0
	for index := 0; index < len(rtx); index++ {
		if KeyWord == rtx[index].Keyword {
			tlist[count] = rtx[index]
			tlist[count].BuyerBalance = BuyerBalance
			tlist[count].BuyerKey = BuyerKey

			s := strings.Split(tlist[count].DataHash, "#")
			_, Phash := s[0], s[1]
			//fmt.Println(Phash)

			ProductAbs := GetProduct(Phash)

			type Product struct {
				Abstract         string    `json:"abstract"`
			}

			var abs Product
			err := json.Unmarshal(ProductAbs, &abs)
			if err != nil{
				fmt.Println("json error", err)
			}
			GetAbs := abs.Abstract

			tlist[count].Abstract = GetAbs

			s2 := strings.Split(tlist[count].ProducerPublicKey, "#")
			_, Producer := s2[0], s2[1]

			ProducerName := GetProducerID(Producer)

			type ProducerSt struct {
				Email         string    `json:"email"`
			}

			var email ProducerSt
			json.Unmarshal(ProducerName, &email)
			GetEmail := email.Email

			tlist[count].ProducerName = GetEmail

			count++
		}

	}
	t.ExecuteTemplate(w, "BuyerDetail.html", tlist)

}


func Confirm(w http.ResponseWriter, r *http.Request) {
	t, _ := template.ParseFiles("Confirm.html")
	Data := r.FormValue("buy")

	s := strings.Split(Data, "|")

	Rtx, BBalance, BKey, ProducerKey, Price, DataHash := s[0],s[1],s[2],s[3],s[4],s[5]

	s2 := strings.Split(ProducerKey, "#")
	_, PKey := s2[0], s2[1]

	iPrice, _ :=  strconv.Atoi(Price)
	iBBalance, _ := strconv.Atoi(BBalance)

	PostBtypeTx(Rtx, iPrice, BKey)

	type show struct {
		DataHash string
		Rtx      string
		BKey     string
		PKey     string
		Price    int
		Balance  int

	}
	var sd show
	sd.Rtx = Rtx
	sd.BKey = BKey
	sd.PKey = PKey
	sd.Price = iPrice
	sd.Balance = iBBalance - iPrice
	sd.DataHash = DataHash


	t.ExecuteTemplate(w, "Confirm.html", sd)
}

func last(w http.ResponseWriter, r *http.Request) {
	//Price := r.FormValue("Price")
	Balance := r.FormValue("Balance")
	Rtx := r.FormValue("Rtx")
	Datahash := r.FormValue("hash")
	//BKey := r.FormValue("BKey")
	//PKey := r.FormValue("PKey")

	iBBalance, _ := strconv.Atoi(Balance)
	//iPrice, _ := strconv.Atoi(Price)

	s := strings.Split(Datahash, "#")
	_, stDataHash := s[0], s[1]



	if iBBalance >= 0 {

		GetBx := GetAllBtx()

		type BtypeTx struct {
			Price             int    `json:"price"`
			BuyerPublicKey    string `json:"buyer_public_key"`
			ProducerPublicKey string `json:"producer_public_key"`
			TransactionId     string `json:"transactionId"`
			Rtx               string  `json:"R_type_id"`
		}

		var btx[] BtypeTx
		err := json.Unmarshal(GetBx, &btx)
		if err != nil {
			fmt.Println("json error", err)
		}

		for _, n := range btx {
			if Rtx == n.Rtx {
				//fmt.Println(n.TransactionId)
				PostCtypeTx(n.TransactionId, n.Price)

				db, err := sql.Open("mysql", "root:root@/dataexchange")

				if err != nil{
					fmt.Println(err)
				}

				fmt.Println(stDataHash)
				var data string
				err2 := db.QueryRow("SELECT file FROM dataexchange.new_table where Datahash = \""+stDataHash+"\"").Scan(&data)
				if err2 != nil{
					fmt.Println(err2)
				}

				defer db.Close()


				fmt.Fprint(w, "<html><body>"+data+"<body></html>")
				//fmt.Fprint(w, "<html><body>fuck u<body></html>")
			}
		}

		}else{
		fmt.Fprint(w, "<html><body>Not Enough Money</body></html>")



		}


	}






func main() {

	http.HandleFunc("/last", last)
	http.HandleFunc("/selab", selab)
	http.HandleFunc("/Confirm", Confirm)
	http.HandleFunc("/ProductRegister", ProductRegister)
	http.HandleFunc("/BuyerMain", BuyerMain)
	http.HandleFunc("/signup", signup)
	http.HandleFunc("/BuyerDetail",BuyerDetail)
	http.HandleFunc("/", Login)
	http.ListenAndServe(":8000",nil)


	/*resp, err := http.PostForm("http://163.239.200.189:3000/api/org.example.empty.Product", url.Values{
		"$class":     {"org.example.empty.Product"},
		"key_word":    {"adsfac"},
		"data_hash":   {"daca"},
		"abstract":   {"erdc"},
		"producer_public_key":   {"ste"}})

	if err != nil {
		panic(err)

		defer resp.Body.Close()

		// Response 체크.
		respBody, err := ioutil.ReadAll(resp.Body)
		if err == nil {
			str := string(respBody)
			println(str)
		}
	}*/


	/*db, err := sql.Open("mysql", "root:root@/dataexchange")

	if err != nil{
		fmt.Println(err)
	}

	var data string
	err2 := db.QueryRow("SELECT file FROM dataexchange.new_table where Datahash = \"76b0fe8faaa51746984adcb352dce6e80637c8290bb6701f5c9f526a5f268c73\"").Scan(&data)
	if err2 != nil{
		fmt.Println(err2)
	}

	defer db.Close()

	fmt.Println(data)*/
	//76b0fe8faaa51746984adcb352dce6e80637c8290bb6701f5c9f526a5f268c73
	}


