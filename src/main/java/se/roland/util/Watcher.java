package se.roland.util;

import se.roland.abstractions.Call;

public class Watcher extends Thread{
    public int delay;
    public Call callback;
    public Watcher(int seconds){
        this.delay = seconds;
    }

    public void run() {
        while (true){
            try {
                Thread.sleep(delay*1000);
                callback.doIt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {

        Watcher watcher = new Watcher(1);
        watcher.callback = new Call() {
            int counter = 0;
            @Override
            public void doIt() {
                System.out.println((counter++) + " Bro");
            }
        };
        watcher.start();
        Thread.sleep(20000);
        watcher.stop();
        watcher.interrupt();
        Thread.sleep(20000);
    }

}
