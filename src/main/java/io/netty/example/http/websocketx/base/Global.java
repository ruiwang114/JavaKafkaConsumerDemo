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
	public static int wssServicePort1=0;
	public static int wssServicePort2=0;

	public static String bootStrapServers=null;
	public static String topic=null;
	public static String topics=null;

	static {
		log.info("开始加载配置文件");
		rootKeyStorePath= PropertyUtil.getInstance().read("rootKeyStorePath");
		rootCertificatePassword=PropertyUtil.getInstance().read("rootCertificatePassword");

		serverKeyStorePath= PropertyUtil.getInstance().read("serverKeyStorePath");
		serverCertificatePassword=PropertyUtil.getInstance().read("serverCertificatePassword");

		clientKeyStorePath=PropertyUtil.getInstance().read("clientKeyStorePath");
		clientCertificatePassword= PropertyUtil.getInstance().read("clientCertificatePassword");

//		wssServicePort=Integer.parseInt(PropertyUtil.getInstance().read("wssServicePort"));
		wssServicePort1=Integer.parseInt(PropertyUtil.getInstance().read("wssServicePort1"));
		wssServicePort2=Integer.parseInt(PropertyUtil.getInstance().read("wssServicePort2"));
		websocketPath=PropertyUtil.getInstance().read("websocketPath");

		bootStrapServers=PropertyUtil.getInstance().read("bootStrapServers");
		topic=PropertyUtil.getInstance().read("topic");
		topics=PropertyUtil.getInstance().read("topics");
		log.info("配置文件加载完毕");
	}
}
