package cn.jhsoft.zk.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chen on 2017/7/9.
 */
public class MyLock {

    static Lock lock = new ReentrantLock();

    public static void main(String[] args) {


        new Thread(){
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                lock.lock();
                try {
                    System.out.println(this.getName() + "得到了锁");

                }finally {
                    System.out.println(this.getName() + "释放了锁");
                    lock.unlock();
                }
            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                lock.lock();
                try {
                    System.out.println(this.getName()+"得到了锁");
                }finally {
                    System.out.println(this.getName()+"释放了锁");
                    lock.unlock();
                }
            }
        }.start();


    }

}
