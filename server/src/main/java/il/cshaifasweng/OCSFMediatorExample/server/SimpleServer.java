package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import static java.lang.Character.isDigit;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);

	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			//we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
			// the IDs at data field in Message entity and send back to all subscribed clients a request to update
			//their IDs text fields. An example of use of observer design pattern.
			//message format: "change submitters IDs: 123456789, 987654321"
			else if(request.startsWith("change submitters IDs: ")){
				message.setData(request.substring(23));
				message.setMessage("update submitters IDs");
				sendToAllClients(message);
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
			//we got a message from client requesting to echo Hello, so we will send back to client Hello world!
			else if(request.startsWith("echo Hello")){
				message.setMessage("Hello World!");
				client.sendToClient(message);
			}
			else if(request.startsWith("send Submitters IDs")){
				message.setMessage("313598484, 308283886");
				client.sendToClient(message);
			}
			else if (request.startsWith("send Submitters")){
				message.setMessage("Yuval Fisher, Or Meir");
				client.sendToClient(message);
			}
			else if (request.equals("what day it is?")) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date = new Date();
				message.setMessage(formatter.format(date));
				client.sendToClient(message);
			}
			else if (request.startsWith("add")){
				Vector<Integer> nums =  new Vector<Integer>(2);
				int j,k=0;
				int posOrNeg1 = 1;
				int posOrNeg2 = 1;

				for(int i = 0 ; i<request.length(); i++)
				{
					if(isDigit(request.charAt(i)))
					{
						if(i-1 >= 0 && request.charAt(i-1)=='-')
						{
							if(nums.isEmpty())
							{
								posOrNeg1 = -1;
							}
							else
							{
								posOrNeg2 = -1;
							}
						}
						
						for(j = i ; j<request.length() && isDigit(request.charAt(j))  ; j++ ){}
						String s = request.substring(i,j);
						nums.add(Integer.parseInt(s));
						i=j;
					}
				}
				int answer = (posOrNeg1*nums.get(0)) + (posOrNeg2*nums.get(1));
				message.setMessage(Integer.toString(answer));
				client.sendToClient(message);
			}
			else{
				sendToAllClients(message);
				//add code here to send received message to all clients.
				//The string we received in the message is the message we will send back to all clients subscribed.
				//Example:
				// message received: "Good morning"
				// message sent: "Good morning"
				//see code for changing submitters IDs for help
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
