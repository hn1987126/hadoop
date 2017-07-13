package cn.jhsoft.zk.thread;

import java.util.Random;

/**
 * Created by chen on 2017/7/9.
 */
public class MyThreadWithExtends extends Thread {


    int x ;
    public MyThreadWithExtends(int x){
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
        MyThreadWithExtends myThreadWithExtends = new MyThreadWithExtends(2);
        MyThreadWithExtends myThreadWithExtends2 = new MyThreadWithExtends(5);
        myThreadWithExtends.start();
        myThreadWithExtends2.start();
    }
}
