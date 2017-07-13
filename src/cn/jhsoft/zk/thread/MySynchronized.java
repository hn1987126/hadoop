package cn.jhsoft.zk.thread;

/**
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

