package com.tigo.utils;

import java.util.ResourceBundle;

public class ParametersWeb {

	static {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String EXPRECION_REGULAR_USUARIO;
	public static String MENSAJE_VALIDACION_USUARIO;
	public static String EXPRECION_REGULAR_PASSWORD;
	public static String MENSAJE_VALIDACION_PASSWORD;
	public static String TEXTO_COMUN;
	public static String MENSAJE_COMUN;
	public static String OPERADOR_RELACIONAL;

	public static void init() {

		String configFile = "config_web_es";
		ResourceBundle rb = ResourceBundle.getBundle(configFile);

		ParametersWeb.EXPRECION_REGULAR_USUARIO = rb.getString("EXPRECION_REGULAR_USUARIO");
		ParametersWeb.MENSAJE_VALIDACION_USUARIO = rb.getString("MENSAJE_VALIDACION_USUARIO");
		ParametersWeb.EXPRECION_REGULAR_PASSWORD = rb.getString("EXPRECION_REGULAR_PASSWORD");
		ParametersWeb.MENSAJE_VALIDACION_PASSWORD = rb.getString("MENSAJE_VALIDACION_PASSWORD");
		ParametersWeb.TEXTO_COMUN = rb.getString("TEXTO_COMUN");
		ParametersWeb.MENSAJE_COMUN = rb.getString("MENSAJE_COMUN");
		ParametersWeb.OPERADOR_RELACIONAL = rb.getString("OPERADOR_RELACIONAL");

	}
}
