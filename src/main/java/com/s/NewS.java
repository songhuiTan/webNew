package com.s;

import java.io.IOException;
import java.io.PrintWriter;
//import java.net.URLDecoder;
//import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;




//import java.util.logging.Logger;
//import java.util.logging.Level;


/**
 * Servlet implementation class NewS
 */
public class NewS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewS() {
        super();
      
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		Logger logger = Logger.getLogger("java");              
		logger.trace("trace中文");
		logger.debug("debug中文");
		logger.info("info中文");
		logger.fatal("fatal中文");
		logger.error("error中文");

//		try {
//		    int a = 1/0;
//		} catch (Exception e) {
//		    logger.warn("exception occurred中午", e);
//		}
		System.out.println("===12:28=====");
		response.setContentType("text/hmtl;charset=utf-8");
		PrintWriter p=response.getWriter();
		p.write("welcome");
		p.flush();
		p.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
