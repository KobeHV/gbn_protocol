package timer;

import gbn.client;

/*
 * ¼ÆÊ±Æ÷
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

                    System.out.println("check if timeout......");
                    if(gbnClient != null){
                    	System.out.println("GBN wait ACK timeout");
                        gbnClient.timeOut();
                    }else{            
                    	System.out.println("The project has finished.");
                    }
                    Model.setTime(0);

                } catch (InterruptedException e) {
                } catch (Exception e) {
                }
            }
        }while (true);
    }
}