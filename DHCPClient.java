import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.StringBuilder;

public class DHCPClient{
	public static void main(String args[]) throws IOException{
		System.out.println("[CLIENT]\n");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		int sportno=Integer.parseInt(args[0]);
		DatagramSocket clientSocket=new DatagramSocket();
		InetAddress IPAddress=InetAddress.getByName("ff02::1");
		byte[] sendData=new byte[1024];
		byte[] receiveData=new byte[1024];
		String lifetime="3600";

		String DHCP_Discover="::,100";
		sendData = DHCP_Discover.getBytes();
		DatagramPacket sendPacket=new DatagramPacket(sendData, sendData.length, IPAddress, sportno);
		clientSocket.send(sendPacket);
		System.out.println("Discover DHCP to server");

		DatagramPacket receivePacket=new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		System.out.print("\nOffer from server\n");
		String serverMsg=new String(receivePacket.getData());

		List<String> DHCP_Offer=Arrays.asList(serverMsg.split(","));

		System.out.println("1.yiaddr: "+DHCP_Offer.get(0));
		System.out.println("2.T_ID: "+DHCP_Offer.get(1));
		System.out.println("3.Lifetime: "+lifetime+"\n");

		double trans_id=Double.parseDouble(DHCP_Offer.get(1));
		
		String new_trans_id=Double.toString(trans_id);

		String final_DHCP_Request="";

		if(!DHCP_Offer.get(0).equals("::")){
			StringBuilder DHCP_Request=new StringBuilder();

			DHCP_Request.append(DHCP_Offer.get(0)+",");
			DHCP_Request.append(new_trans_id+",");
			DHCP_Request.append(lifetime);

			final_DHCP_Request=DHCP_Request.toString();
		}

		System.out.println("Request: "+DHCP_Offer.get(0)+"\n");
		sendData=final_DHCP_Request.getBytes();
		DatagramPacket sendRequestPacket=new DatagramPacket(sendData, sendData.length, IPAddress, sportno);
		clientSocket.send(sendRequestPacket);

		DatagramPacket receiveACKPacket=new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receiveACKPacket);

		System.out.println("Receive acknowledgement from server");
		String DHCPACK=new String(receivePacket.getData());

		List<String> DHCP_ACK=Arrays.asList(DHCPACK.split(","));

		System.out.println("1.yiaddr: "+DHCP_ACK.get(0));
		System.out.println("2.T_ID: "+DHCP_ACK.get(1));
		System.out.println("3.Lifetime: "+lifetime+"\n");
		
		clientSocket.close();
	}
}
