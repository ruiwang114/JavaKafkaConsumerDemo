package io.netty.example.http.websocketx.factory;

import io.netty.example.http.websocketx.base.Global;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.annotation.XmlElementDecl;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

public final class SecureChatSslContextFactory {
	
	private static final String PROTOCOL = "TLS";

	//服务器安全套接字协议
	private static SSLContext SERVER_CONTEXT;
	//客户端安全套接字协议
    private static SSLContext CLIENT_CONTEXT;
	//服务器安全证书密码
	private static String SERVER_CERT_PWD="";
	//客户端安全证书密码
	private static String CLIENT_CERT_PWD="";

	public static SSLContext getServerContext(String pkPath,String caPath){
		if(SERVER_CONTEXT!=null) return SERVER_CONTEXT;
		InputStream in =null;
		InputStream tIN = null;

		SERVER_CERT_PWD= Global.serverCertificatePassword;
		try{
			//密钥管理器
			KeyManagerFactory kmf = null;
			if(pkPath!=null){
				KeyStore ks = KeyStore.getInstance("JKS");
				in = new FileInputStream(pkPath);
				ks.load(in, SERVER_CERT_PWD.toCharArray());

				kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, SERVER_CERT_PWD.toCharArray());
			}
			//信任库 
			TrustManagerFactory tf = null;
			if (caPath != null) {
			    KeyStore tks = KeyStore.getInstance("JKS");
			    tIN = new FileInputStream(caPath);
			    tks.load(tIN, SERVER_CERT_PWD.toCharArray());
//			    tks.load(tIN, "sNetty".toCharArray());
			    tf = TrustManagerFactory.getInstance("SunX509");
			    tf.init(tks);
			}
			
			SERVER_CONTEXT= SSLContext.getInstance(PROTOCOL);
			//初始化此上下文
			//参数一：认证的密钥      参数二：对等信任认证  参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
			SERVER_CONTEXT.init(kmf.getKeyManagers(),tf.getTrustManagers(), null);
			
		}catch(Exception e){
			throw new Error("Failed to initialize the server-side SSLContext", e);
		}finally{
			if(in !=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
			
			if (tIN != null){
			    try {
			    	tIN.close();
			    } catch (IOException e) {
			    	e.printStackTrace();
			    }
				tIN = null;
		   }
		}	

		return SERVER_CONTEXT;
	 }
	
	
	 public static SSLContext getClientContext(String pkPath,String caPath){
		 if(CLIENT_CONTEXT!=null) return CLIENT_CONTEXT;

		 CLIENT_CERT_PWD= Global.clientCertificatePassword;
		 InputStream in = null;
		 InputStream tIN = null;
		 try{
			KeyManagerFactory kmf = null;
			if (pkPath != null) {
			    KeyStore ks = KeyStore.getInstance("JKS");
			    in = new FileInputStream(pkPath);
//			    ks.load(in, CLIENT_CERT_PWD.toCharArray());
			    ks.load(in, "cNetty".toCharArray());
			    kmf = KeyManagerFactory.getInstance("SunX509");
//			    kmf.init(ks, CLIENT_CERT_PWD.toCharArray());
			    kmf.init(ks, "cNetty".toCharArray());
			}
				
			TrustManagerFactory tf = null;
			if (caPath != null) {
			    KeyStore tks = KeyStore.getInstance("JKS");
			    tIN = new FileInputStream(caPath);
//			    tks.load(tIN, CLIENT_CERT_PWD.toCharArray());
			    tks.load(tIN, "cNetty".toCharArray());
			    tf = TrustManagerFactory.getInstance("SunX509");
			    tf.init(tks);
			}
			 
			 CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
			 //初始化此上下文
			 //参数一：认证的密钥      参数二：对等信任认证  参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
			 CLIENT_CONTEXT.init(kmf.getKeyManagers(),tf.getTrustManagers(), null);
			 
		 }catch(Exception e){
			 throw new Error("Failed to initialize the client-side SSLContext");
		 }finally{
			 if(in !=null){
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					in = null;
				}
				
				if (tIN != null){
				    try {
				    	tIN.close();
				    } catch (IOException e) {
				    	e.printStackTrace();
				    }
					tIN = null;
			   }
		 }
		 
		 return CLIENT_CONTEXT;
	 }

}
