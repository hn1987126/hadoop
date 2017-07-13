package cn.jhsoft.zk.thread;

import java.util.Random;

/**
 * Created by chen on 2017/7/9.
 */
public class MyThreadWithImpliment implements Runnable {

    int x ;
    public MyThreadWithImpliment(int x){
        this.x = x;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println("线程"+name+"被调用了");
        Random random = new Random();

        for(int i=0; i<10;i++){
            int r = random.nextInt(10);
            try {
                Thread.sleep(r*100);
                System.out.println(name+",随机数"+r+",flag"+x);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new MyThreadWithImpliment(1));
        Thread thread2 = new Thread(new MyThreadWithImpliment(2), "thread-hhhhaaa2");
        thread1.start();
        thread2.start();

    }
}
