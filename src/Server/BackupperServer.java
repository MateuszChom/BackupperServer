package Server;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 * Klasa do nasluchu nowych klientow
 * @author Mateusz
 *
 */
public class BackupperServer implements Runnable
{
	private int port = 8000;
	private List<Client> clients;
	private ServerSocket server_socket;
	private boolean listen_for_clients = false;
	private Thread th;
	
	/**
	 * 
	 */
	BackupperServer(){
		port = ServerConfig.getPort();
	}
	public static void main(String[] args){
		BackupperServer server = new BackupperServer();
		
		EventQueue.invokeLater(new Runnable(){ public void run(){
			server.initUI();
		}});
	}
	/**
	 * Inicjalizacja interfejsu serwera
	 * @param server
	 */
	private void initUI(){
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                stopServer();
                System.exit(0);
            }
        } );
		
		Button btt = new Button("Start server");
		btt.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){
			if(listen_for_clients){
				stopServer();
				btt.setLabel("Start Server");
			}
			else {
				startServer();
				btt.setLabel("StopServer");
			}
			
		}});
		window.add(btt);
		window.pack();
		window.setVisible(true);
	}
	
	/**
	 * Metoda zatrzymujaca dzialanie watku nasluchiwania na klientow
	 */
	private void stopServer() {
		listen_for_clients = false;
		if(server_socket != null){
			try {
				server_socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Zakonczono nasluchiwanie.");
			}
		}
		if(clients != null){
			for(Client cl : clients){
				cl.forceClose();
			}
		}
		
		th = null;
	}
	
	/**
	 * Metoda od wlaczania nasluchu klientow.
	 */
	private void startServer(){
		listen_for_clients = true;
		if(th == null){
			th = new Thread (this);
			th.start();
		}
		else{
			System.out.println("Znowu dziala nasluch.");
		}
	}
	
	/**
	 * Glowna czesc kodu od nasluchiwania
	 */
	public void run(){
		System.out.println("Serwer zalaczony i dziala.");
		listen_for_clients = true;
		clients = new ArrayList<Client>();
		Socket socket = null;
			
	    try {
			server_socket = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

			
		while(listen_for_clients){
			try{
				 socket = server_socket.accept();
				 clients.add(new Client(socket));
				 System.out.println("Klient poloczony.");
			}
			 catch (IOException e){
				 System.out.println("Zakonczono czekam na kolejnych klientow.");
				 for(Client cl : clients){
					 cl.forceClose();
				 }
			 }
		}
		System.out.println("Serwer zatrzymany.");
	}
}
