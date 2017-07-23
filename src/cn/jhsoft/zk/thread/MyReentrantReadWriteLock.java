package cn.jhsoft.zk.thread;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by chen on 2017/7/9.
 */
public class MyReentrantReadWriteLock {

    private ReentrantReadWriteLock rw1 = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        final MyReentrantReadWriteLock test = new MyReentrantReadWriteLock();

        new Thread(){
            @Override
            public void run() {
                test.get(Thread.currentThread());
                test.write(Thread.currentThread());
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                test.get(Thread.currentThread());
                test.write(Thread.currentThread());
            }
        }.start();

    }

    private void write(Thread thread) {
        rw1.writeLock().lock();
        try {
            for (int i = 0; i < 3; i++) {
                System.out.println(thread.getName()+"正在进行写");
            }
            System.out.println(thread.getName()+"写完毕");

        }finally {
            rw1.writeLock().unlock();
        }
    }

    private void get(Thread thread) {
        try {
            rw1.readLock().lock();
            for (int i = 0; i < 5; i++) {
                System.out.println(thread.getName() + "正在进行读操作");
                Thread.sleep(1000);
            }
            System.out.println(thread.getName()+"读操作完毕");
        } catch (InterruptedException e) {

        } finally {
            rw1.readLock().unlock();
        }

    }

}
