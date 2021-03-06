import requests
from concurrent.futures import ThreadPoolExecutor


def post_url(args):
    return requests.post(args[0], data=args[1], headers=headers)

def data(num):
    body = {
        "id": str(num),
        "data": "[{'id':133140,'ticketOrderProductId':37,'ticketOrderOptionId':4017,'ticketOrderItemId':40,'optionName':'Green Season(주중/어린이)','productName':'워커힐 리버파크 Green Season(주중/어린이)','brandId':'V00A018B005','storeName':'리버파크','supplierCode':'VMPT00000003','supplierName':'원투씨엠','sellerCode':'VMPT00000003','sellerName':'원투씨엠','orderNum':'ODTK-20190530-FGIWNU-IIX','orderAmount':18000.0,'cancelAmount':null,'status':'CONFIRM','orderCancel':'CANCEL','useable':'Y','customerId':'G9GG8fbrGIbYlYlxALxf8YWHXMftw7OqYHmTA81lm1M=','customerName':'/CmjAUMPE/YrCVQvDJX4TA==','customerTelNo':'G+hGfr7SdmUq1Mh2yn7faA==','customerEmail':'G9GG8fbrGIbYlYlxALxf8YWHXMftw7OqYHmTA81lm1M=','purchaseDate':'2019-05-30 06:52:40','cancelDate':'2019-07-15 20:54:42','trxNo':'CNbciVPJZkOO6v+IpyzQR*F2gys+1vXL0qu3BtoQqwc=','supplierTrxNo':null,'marketUrl':null,'channelProductName':'워커힐 리버파크','saleProductCode':'PROD00000001','productOptionName':null,'saleProductOptionCode':'OPTI00000002','salesChannelOptionId':'4818975271','salesChannelProductId':'100000026033','ticketNumber':'FC5059-27000047877357','ticketStatus':'CANCEL_COMPLETE','productId':'P00000000026','itemCode':'ITEM00000002','pinNumber':'9000042175932','voucherTicketNo':'a914a0cfa0fd472aa4ae4d62a9729edb','voucherTrxNo':'20190530065255862-RlE','salesChannelId':'COUPANG','expireDate':null,'storeId':'V00A018B005M00001','stampNo':null,'settlementAmount':18000.0,'fee':1980.0,'salePrice':18000.0,'supplyPrice':16020.0,'commissionPrice':1980.0,'commissionRate':11.0,'channelSupplyPrice':17100.0,'channelCommissionPrice':null,'channelCommissionRate':5.0,'issueDate':'2019-05-30 06:52:56','issueCancelDate':'2019-05-30 06:52:56','useDate':null,'useCancelDate':null,'handledStatus':'N','targetDate':'2019-05-30','batchDate':'2020-02-28 18:36:31'}]"
    }
    return body



headers = {"ad": "ad"}

list_of_urls = [
    ("URL", data(20065)),
    ("URL", data(20066)),
    ("URL", data(20067))
                ]


with ThreadPoolExecutor(max_workers=1) as pool:
    response_list = list(pool.map(post_url, list_of_urls))

for response in response_list:
    print(response)

