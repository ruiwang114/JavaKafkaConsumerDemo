package io.netty.example.http.websocketx.base;

public class testGlobal {
    public static void main(String[] args) {
//        System.out.println(Global.keyStorePath);
        boolean SSL = System.getProperty("ssl") != null;
        System.out.println(SSL);
        int PORT = Integer.parseInt(System.getProperty("port", SSL? "8443" : "8080"));
        System.out.println(PORT);
    }
}
