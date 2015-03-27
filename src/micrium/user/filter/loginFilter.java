package micrium.user.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import micrium.user.controler.ControlTimeOutImpl;
import micrium.user.controler.NodoClient;

import org.apache.log4j.Logger;

public class loginFilter implements Filter {
	private static Logger log = Logger.getLogger(loginFilter.class);
	private FilterConfig filterConfig = null;
	public static String pathRaiz = "";
	private static String pathLogin = "";
	private static String rederingMenu = "";

	@Inject
	private ControlTimeOutImpl controlTA;

	public loginFilter() {
	}

	/**
	 * Init method for this filter
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		String context = filterConfig.getServletContext().getContextPath();
		log.info("**** Contexto WEB:"+context);
		loginFilter.pathRaiz = context+"/";
		loginFilter.pathLogin = context +"/view/login.xhtml";
		loginFilter.rederingMenu = context+ "/view/menu.xhtml";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpSession session = ((HttpServletRequest) request).getSession();
		HttpServletRequest req = (HttpServletRequest) request;

		if (req.getRequestedSessionId() != null && !req.isRequestedSessionIdValid()) {
			session.invalidate();
			req.getSession(true);
			HttpServletResponse hres = (HttpServletResponse) response;
			hres.sendRedirect(pathRaiz);
			return;
		}

		String path = req.getRequestURI();
		String usuario = (String) req.getSession().getAttribute("TEMP$USER_NAME");

		if (path.equals(pathRaiz)) {
			if (usuario == null)
				chain.doFilter(request, response);
			else {
				HttpServletResponse hres = (HttpServletResponse) response;
				hres.sendRedirect(rederingMenu);
			}
			return;
		}

		if (path.equals(pathLogin)) {

			if (request.getContentLength() != -1 && estanParametros(req.getParameterMap())) {

				// agregado para actualizar session
				Map<String, Object> mapValueNames = new HashMap<String, Object>();

				Enumeration<String> nombres = session.getAttributeNames();

				for (String valueName : Collections.list(nombres)) {
					mapValueNames.put(valueName, session.getAttribute(valueName));
				}
				session.setAttribute("TEMP$ACTION_MESSAGE_ID", "");
				session.setAttribute("TEMP$USER_NAME", "");
				session.setAttribute("TEMP$GROUP", "");
				session.invalidate();
				session = req.getSession(true);
				List<String> listKeys = new ArrayList<String>(mapValueNames.keySet());
				for (String key : listKeys) {
					session.setAttribute(key, mapValueNames.get(key));
				}
				// agregado para actualizar session

				chain.doFilter(request, response);

			} else {
				HttpServletResponse hres = (HttpServletResponse) response;
				hres.sendRedirect(pathRaiz);
			}
			return;
		}

		if (usuario != null) {

			String addressIP = request.getRemoteAddr();
			String addressUser = controlTA.getAddressIP(usuario);

			if (!addressIP.equals(addressUser)) {
				session.setAttribute("TEMP$USER_NAME", "");
				session.setAttribute("TEMP$GROUP", "");
				session.invalidate();
				HttpServletResponse hres = (HttpServletResponse) response;
				hres.sendRedirect(pathRaiz);
			} else {
				NodoClient nd = controlTA.getNodoClient(usuario);
				String pageRequest = path;
				int k = pageRequest.lastIndexOf("/");
				String strPg = pageRequest.substring(k + 1);
				if (nd.existeUrl(strPg)) {
					long tp = session.getLastAccessedTime();
					controlTA.setDatos(usuario, tp);
					chain.doFilter(request, response);
				} else {
					RequestDispatcher dispatcher = request.getRequestDispatcher(pathRaiz);
					dispatcher.forward(request, response);
				}
			}

		} else {
			long timeMax = session.getMaxInactiveInterval() * 1000;
			controlTA.registerOutTime(timeMax);
			session.setAttribute("TEMP$ACTION_MESSAGE_ID", "");
			session.setAttribute("TEMP$USER_NAME", "");
			session.setAttribute("TEMP$GROUP", "");
			session.invalidate();
			HttpServletResponse hres = (HttpServletResponse) response;
			hres.sendRedirect(pathRaiz);
		}
	}

	private boolean estanParametros(Map<String, String[]> map) {
		return map != null && map.size() == 5 && valP(map.get("formLogin:usernameId")) && valP(map.get("formLogin:passwordId")) && valP(map.get("formLogin")) && valP(map.get("formLogin:comanLogin")) && valP(map.get("javax.faces.ViewState"));
	}

	private boolean valP(String[] param) {
		if (param != null) {
			if (param.length == 1) {
				String pp = param[0];
				return pp != null;// && !pp.trim().isEmpty();
			}
		}
		return false;
	}

	@Override
	public void destroy() {
	}

	@Override
	public String toString() {
		if (filterConfig == null) {
			return ("loginFilter()");
		}
		StringBuffer sb = new StringBuffer("loginFilter(");
		sb.append(filterConfig);
		sb.append(")");
		return (sb.toString());
	}

	public void log(String msg) {
		filterConfig.getServletContext().log(msg);
	}

}
