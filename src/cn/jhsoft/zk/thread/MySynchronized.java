package cn.jhsoft.zk.thread;

/**
 * synchronized 同步代码块，一个时间内只能有一个线程得到执行。另一个线程必须等待当前线程执行完这个代码块以后才能执行该代码块
 * Created by chen on 2017/7/9.
 */
public class MySynchronized {

    public static void main(String[] args) {
        final MySynchronized mySynchronized = new MySynchronized();
        final MySynchronized mySynchronized1 = new MySynchronized();

        new Thread("thread1"){
            @Override
            public void run() {
                synchronized (mySynchronized){
                    try {
                        System.out.println(this.getName()+"start");
                        Thread.sleep(5000);
                        System.out.println(this.getName()+"醒了");
                        System.out.println(this.getName()+"End");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread("t2"){
            @Override
            public void run() {
                synchronized(mySynchronized){
                    System.out.println(this.getName()+"Start");
                    System.out.println(this.getName()+"End");
                }
            }
        }.start();
    }

}

