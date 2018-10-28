package timer;

/*
 * 保证线程安全性
 */
public class model {

    public volatile int time;

    public  synchronized int getTime() {
        return time;
    }

    public synchronized void setTime(int time) {
        this.time = time;
    }
}
