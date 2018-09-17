/**
 * @param {org.example.empty.Ctx} offer
 * @transaction
 */

async function offer (offer) {
  
  const OfferPrice = offer.price;
  const Buyer = offer.buyer_public_key;
  const Producer = offer.producer_public_key;
  
  var NewBalance;
  var BuyerBalance = offer.buyer_public_key.balance;
  var ProducerBalance = offer.producer_public_key.balance;
  
  
  if ( OfferPrice <= BuyerBalance ){
  
  
  Buyer.balance = BuyerBalance - OfferPrice;
  Producer.balance = ProducerBalance + OfferPrice;
  
  console.log(Buyer.balance);
  console.log(Producer.balance);

  const BuyerRegistry = await getParticipantRegistry('org.example.empty.Buyer');
  await BuyerRegistry.update(Buyer);
  
  const ProducerRegistry = await getParticipantRegistry('org.example.empty.Producer');
  await ProducerRegistry.update(Producer);                       
  }
  else {
  	throw new Error ("no money!");
  }
}
/**
 * @param {org.example.empty.Rtx} register
 * @transaction
 */
async function register (registering) {
}

/**
 * @param {org.example.empty.Btx} buy
 * @transaction
 */
async function buy (buying) {
}


