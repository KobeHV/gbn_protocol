package timer;

import gbn.client;

/*
 * 计时器
 */
public class timer extends Thread {

    private model Model;
    private client gbnClient;
    public timer(client gbnClient, model model){
        this.gbnClient = gbnClient;
        this.Model = model;
    }
    
    @Override
    public void run(){
        do{
            int time = Model.getTime();
            if(time > 0){
                try {
                    Thread.sleep(time*1000);

                    System.out.println("\n");
                    if(gbnClient != null){
                    	System.out.println("GBN客户端等待ACK超时");
                        gbnClient.timeOut();
                    }else{                        
                    }
                    Model.setTime(0);

                } catch (InterruptedException e) {
                } catch (Exception e) {
                }
            }
        }while (true);
    }
}