package cn.jhsoft.zk.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chen on 2017/7/9.
 */
public class MyTryLock {

    static Lock lock = new ReentrantLock();

    public static void main(String[] args) {


        new Thread(){
            @Override
            public void run() {
                boolean tryLock = lock.tryLock();
                System.out.println(this.getName() + " "+tryLock);
                if (tryLock){
                    try {
                        System.out.println(this.getName() + "得到了锁");
                        Thread.sleep(5000);

                    } catch (Exception e) {

                    } finally {
                        System.out.println(this.getName() + "释放了锁");
                        lock.unlock();
                    }
                }

            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                boolean tryLock = lock.tryLock();
                System.out.println(this.getName() + " "+tryLock);
                if (tryLock){
                    try {
                        System.out.println(this.getName()+"得到了锁");
                    }finally {
                        System.out.println(this.getName()+"释放了锁");
                        lock.unlock();
                    }
                }
            }
        }.start();



    }

}
