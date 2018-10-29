package gbn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/*
 * 服务器端
 */
public class server {
	private final int port = 80;
	private final int MaxSeq = 10;
	private final int lostAck = 9;
	private int cnt = 0;
	private int ack = 0;
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
				// 收到的数据
				String received = new String(receivedData, 0, receivedData.length);// offset是初始偏移量
				System.out.println(received);
				// 收到了预期的数据
				ack = Integer.parseInt(received.substring(received.indexOf(":") + 1).trim());
				if (ack == lostAck && cnt == 0) {
					cnt++;
					continue;
				}
				if (ack == exceptedSeq) {
					// 发送ack
					sendAck(exceptedSeq);
					System.out.println("server expected seq:" + exceptedSeq);
					System.out.println("send ACK " + exceptedSeq + " in rerturn");
					if (exceptedSeq == MaxSeq) {
						System.out.println("\nAll the ACK has sent :)");

						System.exit(-1);
					} // 期待值加1
					exceptedSeq++;
					System.out.println('\n');
				} else {
					System.out.println("server expected seq:" + exceptedSeq);
					System.out.println("+++++++++++++++++++++server don't get data it wanted+++++++++++++++++++++");
					// 仍发送之前的ack
					sendAck(exceptedSeq - 1);
					System.out.println('\n');
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static final void main(String[] args) throws IOException {
		new server();
	}

	// 向客户端发送ack
	public void sendAck(int ack) throws IOException {
		String response = " ack:" + ack;

		byte[] responseData = response.getBytes();
		InetAddress responseAddress = datagramPacket.getAddress();
		int responsePort = datagramPacket.getPort();
		datagramPacket = new DatagramPacket(responseData, responseData.length, responseAddress, responsePort);
		datagramSocket.send(datagramPacket);

	}
}