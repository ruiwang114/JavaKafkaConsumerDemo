package io.netty.example.http.websocketx.test;

public class ClientTest2 {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 200; i++) {
//            if(i==0){
//                Thread thread = new Thread(new ServerTask());
//                thread.start();
//                Thread.sleep(5000);
//            }

            Thread thread = new Thread(new ServerTask());
            Thread.sleep(100);
            thread.start();
        }
    }
}
