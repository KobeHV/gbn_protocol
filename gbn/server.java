package gbn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/*
 * ��������
 */
public class server {
    private final int port = 80;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private int exceptedSeq = 1;

    public server() throws IOException {

    	    System.out.println("server working \n......");
            try {
                datagramSocket = new DatagramSocket(port);
                while (true) {
                    byte[] receivedData = new byte[4096];
                    datagramPacket = new DatagramPacket(receivedData, receivedData.length);
                    datagramSocket.receive(datagramPacket);
                    //�յ�������
                    String received = new String(receivedData, 0, receivedData.length);//offset�ǳ�ʼƫ����
                    System.out.println(received);
                    //�յ���Ԥ�ڵ�����
                    if (Integer.parseInt(received.substring(received.indexOf(":") + 1).trim()) == exceptedSeq) {
                        //����ack
                        sendAck(exceptedSeq);                        
                        System.out.println("server expected seq:" + exceptedSeq);
                        System.out.println("send ACK "+exceptedSeq+" in rerturn");
                        //�ڴ�ֵ��1
                        exceptedSeq++;
                        System.out.println('\n');
                    } else {
                        System.out.println("server expected seq:" + exceptedSeq);
                        System.out.println("+++++++++++++++++++++server don't get data it wanted+++++++++++++++++++++");
                        //�Է���֮ǰ��ack
                        sendAck(exceptedSeq - 1);
                        System.out.println('\n');
                    }
                }
            }catch(SocketException e){
                    e.printStackTrace();
                }
        }

    public static final void main(String[] args) throws IOException {
        new server();
    }

    //��ͻ��˷���ack
    public void sendAck(int ack) throws IOException {
        String response = " ack:"+ack;
        byte[] responseData = response.getBytes();
        InetAddress responseAddress = datagramPacket.getAddress();
        int responsePort = datagramPacket.getPort();
        datagramPacket = new DatagramPacket(responseData,responseData.length,responseAddress,responsePort);
        datagramSocket.send(datagramPacket);
    }
}