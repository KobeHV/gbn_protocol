package gbn;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
/*
 * 客户端
 */

public class client {
	private final int port = 80;
	private DatagramSocket datagramSocket = new DatagramSocket();
	private DatagramPacket datagramPacket;
	private InetAddress inetAddress;
	private int nextSeq = 1;
	private int base = 1;
	private int N = 5;
	private volatile int ack = 0;
	private final int MaxSeq = 10;
	private final int lostSeq = 3;
	Timer[] timers = new Timer[MaxSeq + 1];

	public client() throws Exception {

		System.out.println("client working \n......");
		while (true) {
			// 向服务器端发送数据
			sendData();
			// 从服务器端接受ACK
			byte[] bytes = new byte[4096];
			datagramPacket = new DatagramPacket(bytes, bytes.length);
			datagramSocket.receive(datagramPacket);
			String fromServer = new String(bytes, 0, bytes.length);
			ack = Integer.parseInt(fromServer.substring(fromServer.indexOf("ack:") + 4).trim());
			timers[ack].stop();
			base = ack + 1;

			System.out.println("\nfrom server get:" + fromServer);
			if (ack == MaxSeq) {
				System.out.println("all the ACK has got :)");
				System.exit(-1);
			}
		}

	}

	public static void main(String[] args) throws Exception {

		new client();
	}

	/*
	 * 向服务器发送数据
	 *
	 * @throws Exception
	 */
	private void sendData() throws Exception {
		inetAddress = InetAddress.getLocalHost();
		while (nextSeq < base + N && nextSeq <= MaxSeq) {
			// 不发编号为lostseq的数据，模拟数据丢失
			timers[nextSeq] = new Timer(10000, new DelayActionListener(this, nextSeq));
			timers[nextSeq].start();
			if (nextSeq == lostSeq) {
				nextSeq++;
				continue;
			}

			String clientData = "client send the seq:" + nextSeq;
			System.out.println("send to server data:" + nextSeq);

			byte[] data = clientData.getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetAddress, port);
			datagramSocket.send(datagramPacket);
			nextSeq++;
		}
	}

	/*
	 *** 超时数据重传
	 */
	public void timeOut() throws Exception {
		for (int i = base; i < nextSeq && i > ack; i++) {
			String clientData = "client send the repeat seq:" + i;
			System.out.println("\nsend to server repeat data:" + i);
			byte[] data = clientData.getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetAddress, port);
			datagramSocket.send(datagramPacket);
		}
	}

	class DelayActionListener implements ActionListener {
		client Client;
		int seq;

		public DelayActionListener(client Client, int seq) {
			this.Client = Client;
			this.seq = seq;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				System.out.println();
				System.out.println(seq + "-TimeOut!!!\nRetransmission:");
				Client.timeOut();
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
			}
		}
	}
}
