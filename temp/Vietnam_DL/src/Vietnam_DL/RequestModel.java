package Vietnam_DL;
import javax.servlet.http.HttpServletRequest;

public class RequestModel {
	private String DNum;
	private String SNum;
	
	public RequestModel(HttpServletRequest request){
		this.DNum = request.getParameter("DNum");
		this.SNum = request.getParameter("SNum");
	}

	public String getDNum() {
		return DNum;
	}

	public void setDNum(String dNum) {
		DNum = dNum;
	}

	public String getSNum() {
		return SNum;
	}

	public void setSNum(String sNum) {
		SNum = sNum;
	}


	@Override
	public String toString() {
		return "RequestModel{" +
					" DNum ='" + DNum + '\''+
					", SNum = '"+SNum+'\''+'}';
	}

	
	
	
	
}
