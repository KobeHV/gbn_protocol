package gbn;

import timer.model;
import timer.timer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * �ͻ���
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
			// ��������˷�������
			sendData();
			// �ӷ������˽���ACK
			byte[] bytes = new byte[4096];
			datagramPacket = new DatagramPacket(bytes, bytes.length);
			datagramSocket.receive(datagramPacket);
			String fromServer = new String(bytes, 0, bytes.length);
			int ack = Integer.parseInt(fromServer.substring(fromServer.indexOf("ack:") + 4).trim());
			base = ack + 1;
			if (base == nextSeq) {
				// ֹͣ��ʱ��
				Model.setTime(0);
			} else {
				// ��ʼ��ʱ��
				Model.setTime(3);
			}
			System.out.println("�ӷ�������õ�����:" + fromServer);
			System.out.println("\n");
		}

	}

	public static void main(String[] args) throws Exception {
		new client();
		new server();

	}

	/*
	 * ���������������
	 *
	 * @throws Exception
	 */
	private void sendData() throws Exception {
		inetAddress = InetAddress.getLocalHost();
		while (nextSeq < base + N && nextSeq <= MaxSeq) {
			// �������Ϊ3�����ݣ�ģ�����ݶ�ʧ
			if (nextSeq == lostSeq) {
				nextSeq++;
				continue;
			}

			String clientData = "�ͻ��˷��͵����ݱ��:" + nextSeq;
			System.out.println("����������͵�����:" + nextSeq);

			byte[] data = clientData.getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetAddress, port);
			datagramSocket.send(datagramPacket);

			if (nextSeq == base) {
				// ��ʼ��ʱ
				Model.setTime(3);
			}
			nextSeq++;
		}
	}

	/*
	 *** ��ʱ�����ش�
	 */
	public void timeOut() throws Exception {
		for (int i = base; i < nextSeq; i++) {
			String clientData = "�ͻ������·��͵����ݱ��:" + i;
			System.out.println("����������·��͵�����:" + i);
			byte[] data = clientData.getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetAddress, port);
			datagramSocket.send(datagramPacket);
		}
	}
}