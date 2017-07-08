package com.sunflower.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class PageStaticizeFilter implements Filter {
	
	private static final String HTML_PATH_MAP = "html_path_map";
	private static final String STATIC_PAGES = "/static_pages";
	private ServletContext context;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.context = filterConfig.getServletContext();
		this.context.setAttribute(HTML_PATH_MAP, new HashMap<String, String>());
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		Map<String, String> htmlPathMap = (Map<String, String>) context.getAttribute(HTML_PATH_MAP);
		
		String htmlName = request.getServletPath().replace("/", "_") + ".html";
		String htmlPath = htmlPathMap.get(htmlName);
		
		if (htmlPath == null) {
			htmlPath = context.getRealPath(STATIC_PAGES) + "/" + htmlName;
			htmlPathMap.put(htmlName, htmlPath);
			PageStaticizeResponse sResponse = new PageStaticizeResponse(response, htmlPath);
			chain.doFilter(request, response);
			sResponse.close();
		}
		String redirectPath = context.getContextPath() + STATIC_PAGES + htmlName;
		response.sendRedirect(redirectPath);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	private static final class PageStaticizeResponse extends HttpServletResponseWrapper {
		
		private PrintWriter writer;

		public PageStaticizeResponse(HttpServletResponse response, String path) throws FileNotFoundException, UnsupportedEncodingException {
			super(response);
			writer = new PrintWriter(path, "UTF-8");
		}
		
		@Override
		public PrintWriter getWriter() throws IOException {
			return this.writer;
		}
		
		public void close() {
			this.writer.close();
		}
	}

}
