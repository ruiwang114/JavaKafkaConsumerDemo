package io.netty.example.http.websocketx.test;

public class ClientTest {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 140; i++) {
            if(i==0){
                Thread thread = new Thread(new ServerTask());
                thread.start();
                Thread.sleep(5000);
            }
            Thread thread = new Thread(new ServerTask());
            thread.start();
        }
    }
}
