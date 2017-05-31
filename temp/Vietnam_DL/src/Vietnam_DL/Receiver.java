package Vietnam_DL;


import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value="/receiver", asyncSupported = true)
public class Receiver extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		req.setCharacterEncoding("UTF-8");	
		
		try {
			RequestModel request = new RequestModel(req);
			Main main = new Main(request);
			main.testVDriver();
			
			resp.getWriter().println("asdad");
			resp.getWriter().close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}	
}