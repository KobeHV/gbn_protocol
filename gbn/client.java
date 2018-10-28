package gbn;

import timer.model;
import timer.timer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * 客户端
 */

public class client {
	private final int port = 80;
	private DatagramSocket datagramSocket = new DatagramSocket();
	private DatagramPacket datagramPacket;
	private InetAddress inetAddress;
	private model Model;
	// private static client gbnClient;
	private timer Timer;
	private int nextSeq = 1;
	private int base = 1;
	private int N = 5;

	private final int MaxSeq = 10;
	private final int lostSeq = 3;

	public client() throws Exception {
		Model = new model();
		Timer = new timer(this, Model);
		Model.setTime(0);
		Timer.start();
		while (true) {
			// 向服务器端发送数据
			sendData();
			// 从服务器端接受ACK
			byte[] bytes = new byte[4096];
			datagramPacket = new DatagramPacket(bytes, bytes.length);
			datagramSocket.receive(datagramPacket);
			String fromServer = new String(bytes, 0, bytes.length);
			int ack = Integer.parseInt(fromServer.substring(fromServer.indexOf("ack:") + 4).trim());
			base = ack + 1;
			if (base == nextSeq) {
				// 停止计时器
				Model.setTime(0);
			} else {
				// 开始计时器
				Model.setTime(3);
			}
			System.out.println("从服务器获得的数据:" + fromServer);
			System.out.println("\n");
		}

	}

	public static void main(String[] args) throws Exception {
		new client();
		new server();

	}

	/*
	 * 向服务器发送数据
	 *
	 * @throws Exception
	 */
	private void sendData() throws Exception {
		inetAddress = InetAddress.getLocalHost();
		while (nextSeq < base + N && nextSeq <= MaxSeq) {
			// 不发编号为3的数据，模拟数据丢失
			if (nextSeq == lostSeq) {
				nextSeq++;
				continue;
			}

			String clientData = "客户端发送的数据编号:" + nextSeq;
			System.out.println("向服务器发送的数据:" + nextSeq);

			byte[] data = clientData.getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetAddress, port);
			datagramSocket.send(datagramPacket);

			if (nextSeq == base) {
				// 开始计时
				Model.setTime(3);
			}
			nextSeq++;
		}
	}

	/*
	 *** 超时数据重传
	 */
	public void timeOut() throws Exception {
		for (int i = base; i < nextSeq; i++) {
			String clientData = "客户端重新发送的数据编号:" + i;
			System.out.println("向服务器重新发送的数据:" + i);
			byte[] data = clientData.getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetAddress, port);
			datagramSocket.send(datagramPacket);
		}
	}
}