import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.StringBuilder;

public class DHCPServer{
	public static void main(String args[])throws IOException{
		int sportno=Integer.parseInt(args[0]);
		DatagramSocket sSocket=new DatagramSocket(sportno);
		byte[] receiveData=new byte[1024];
		byte[] sendData=new byte[1024];
		String lifetime="3600";

		while(true){
			System.out.println("Waiting...\n");
			
			DatagramPacket receivePacket=new DatagramPacket(receiveData, receiveData.length);
			sSocket.receive(receivePacket);
			System.out.println("Discover from client");
			String clientMsg=new String(receivePacket.getData());
	
			List<String> Client_Msg=Arrays.asList(clientMsg.split(","));
			System.out.println("1.yiaddr: "+Client_Msg.get(0));
			System.out.println("2.T_ID: "+Client_Msg.get(1)+"\n");

			double trans_id=Double.parseDouble(Client_Msg.get(1));

			String final_DHCP_Offer="";
			String currentIP="";

			if(Client_Msg.get(0).equals("::")){
				System.out.println("Taking IP from IP file...\n");

				currentIP=popOneIP("IPFile.txt");

				StringBuilder DHCP_Offer=new StringBuilder();

				DHCP_Offer.append(currentIP+",");
				DHCP_Offer.append(Client_Msg.get(1)+",");
				DHCP_Offer.append(lifetime);

				final_DHCP_Offer=DHCP_Offer.toString();

				InetAddress IPAddress = receivePacket.getAddress();
				int cportno=receivePacket.getPort();

				System.out.println("Offer IP:  "+currentIP+"\n");

				sendData=final_DHCP_Offer.getBytes();
				DatagramPacket sendPacket=new DatagramPacket(sendData, sendData.length, IPAddress, cportno);
				sSocket.send(sendPacket);
			}

			DatagramPacket receiveRequestPacket=new DatagramPacket(receiveData, receiveData.length);
			sSocket.receive(receiveRequestPacket);
			System.out.println("Request from client");
			String DHCPRequest=new String(receivePacket.getData());

			List<String> DHCP_Request=Arrays.asList(DHCPRequest.split(","));

			double trans_request_id=Double.parseDouble(DHCP_Request.get(1));
			trans_request_id=trans_request_id+1;
			String new_trans_request_id=Double.toString(trans_request_id);

			System.out.println("1.yiaddr: "+DHCP_Request.get(0));
			System.out.println("2.T_ID: "+new_trans_request_id);
			System.out.println("3.Lifetime: "+lifetime+"\n");

			String final_DHCP_ACK="";

			if(!DHCP_Request.get(0).equals("::")){
				StringBuilder DHCP_ACK=new StringBuilder();

				DHCP_ACK.append(DHCP_Request.get(0)+",");
				DHCP_ACK.append(new_trans_request_id+",");
				DHCP_ACK.append(lifetime);

				final_DHCP_ACK=DHCP_ACK.toString();
				
				InetAddress IPAddress = receivePacket.getAddress();
				int cportno = receivePacket.getPort();

				System.out.println("Send acknowledgement to client");
				sendData=final_DHCP_ACK.getBytes();
				DatagramPacket sendACKPacket=new DatagramPacket(sendData, sendData.length, IPAddress, cportno);
				sSocket.send(sendACKPacket);
			}
		}
	}

	public static String popOneIP(String fileName){
		String line=null;
		
		Vector<String> IPs=new Vector();
		String IP="";

		try{
			FileReader fileReader=new FileReader(fileName);
			BufferedReader bufferedReader=new BufferedReader(fileReader);

			while((line=bufferedReader.readLine()) != null){
				IPs.add(line);
			}

			IP=IPs.lastElement().toString();

			IPs.removeElement(IPs.lastElement());

			File newIPs=new File(fileName);
			FileWriter IPWriter=new FileWriter(newIPs, false);

			for(int i=0; i<IPs.size(); i++){
				IPWriter.write(IPs.get(i)+"\n");
			}
			IPWriter.close();

			bufferedReader.close();
		}
			
		catch(FileNotFoundException ex){
			System.out.println("Unable to open file '"+fileName+"'");
		}	
			
		catch(IOException ex){
			System.out.println("Error reading file '"+fileName+"'");
		}
		
		return IP;
	}
}
