package com.deltacopiers.www.comprafacil.app;

public class AppConfig {
	public static String HOST      = "http://192.168.1.70:8000/";//https://www.comprafacil.com.ni/
	// Server user login url
	public static String URL_LOGIN = HOST+"api-token-auth/";

	// Server user register url
	public static String URL_REGISTER  = HOST+"api/register";
	public static String URL_PRODUCTO  = HOST+"api/api_producto/";
	public static String URL_CATEGORIA = HOST+"api/categoria/2/";
	public static String URL_PROVEEDOR = HOST+"api/api_proveedor/1/";

}
