package io.netty.example.http.websocketx.base;

import io.netty.example.http.websocketx.util.PropertyUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Global {

	public static String rootKeyStorePath = null;
	public static String rootCertificatePassword = null;

	public static String serverKeyStorePath = null;
	public static String serverCertificatePassword = null;

	public static String clientKeyStorePath = null;
	public static String clientCertificatePassword = null;

	public static String websocketPath = null;

	public static int wssServicePort=0;

	public static String bootStrapServers=null;
	public static String topic=null;

	static {
		log.info("开始加载配置文件");
		rootKeyStorePath= PropertyUtil.getInstance().read("rootKeyStorePath");
		rootCertificatePassword=PropertyUtil.getInstance().read("rootCertificatePassword");

		serverKeyStorePath= PropertyUtil.getInstance().read("serverKeyStorePath");
		serverCertificatePassword=PropertyUtil.getInstance().read("serverCertificatePassword");

		clientKeyStorePath=PropertyUtil.getInstance().read("clientKeyStorePath");
		clientCertificatePassword= PropertyUtil.getInstance().read("clientCertificatePassword");

		wssServicePort=Integer.parseInt(PropertyUtil.getInstance().read("wssServicePort"));
		websocketPath=PropertyUtil.getInstance().read("websocketPath");

		bootStrapServers=PropertyUtil.getInstance().read("bootStrapServers");
		topic=PropertyUtil.getInstance().read("topic");
		log.info("配置文件加载完毕");
	}
}
