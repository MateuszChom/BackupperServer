package Server;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import Connection.ConnectionHandler;
import Connection.RequestedCommand;
import Filesystem.FilesystemElement;
/**
 * Klasa pozwalajaca na obslugiwanie jednego klienta
 * @author Mateusz
 *
 */
public class Client extends Thread {	
	private Socket socket;
	private ConnectionHandler connection;
	private String username;
	private boolean thread_running = true;
	private String backupPath, absoluteBackupPath;
	
	public Client(Socket socket){
		backupPath = ServerConfig.getBackupPath();
		this.socket = socket;
		connection = new ConnectionHandler(socket);
		this.start();
	}
	
	/**
	 * Watek glowny obslugiwania zadan klienta
	 */
	public void run(){
		try{
			username = getUserLogin();
			absoluteBackupPath =  backupPath;
			backupPath = backupPath + "\\" + username;
		}

		catch (Exception e) {
			System.out.println("User login recive failed");
			forceClose();
		}
		if(username == null){
			forceClose();
		}
		else{
			RequestedCommand command = null;
			String temp = null;
			while(thread_running){
				try {
					command = (RequestedCommand)connection.getObjectFromSocket();
					
					System.out.println("Zalapalo, dziala.");
				} catch (ClassNotFoundException | IOException e) {
					
				}
				if (command != null){
					switch(command){
						case DELETE_FILE:
							System.out.println("DELETE_FILE");
							deleteFile();
							break;
						case DELETE_FOLDER:
							System.out.println("DELETE_FOLDER");
							deleteFolder();
							break;
						case GET_FILE:
							System.out.println("GET_FILE");
							pushFile();
							break;
						case GET_FILE_TREE:
							System.out.println("GET_FILE_TREE");
							getFileTree();
							break;
						case PUSH_FILE:
							System.out.println("PUSH_FILE");
							getFile();
							break;
						default:
							forceClose();
							break;
					}
					command = null;
				}
			}
		}
	}
	/**
	 * Zestaw instrukcji pozwalajacy na wyslanie zadanego pliku do klienta
	 */
	private void pushFile() {
		String filename = null;
			try {
				System.out.println("!oddzielacz!");
				connection.writeObjectToSocket("READY");
				filename = (String)connection.getObjectFromSocket();
				System.out.println("push " + filename);
			} catch (ClassNotFoundException | IOException e) {
				forceClose();
			}
		if(filename != null){
			try {
				System.out.println(optimizeFilePathForClient(filename));
				connection.writeFileToSocket(optimizeFilePathForClient(filename));
			} catch (IOException e) {
				forceClose();
			}
		}
		else{
			forceClose();
		}
			
	}
	/**
	 * Zestaw instrukcji pozwalajacy na wyslanie drzewa plikow do klienta
	 */
	private void getFileTree() {
			FilesystemElement fe = null;
			try {
				fe = new FilesystemElement(new File(backupPath), null);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				connection.writeObjectToSocket(fe);
			} catch (IOException e) {
				e.printStackTrace();
				forceClose();
			}
	}
	/**
	 * Zestaw instrukcji pozwalajacy na odebranie pliku od klienta
	 */
	private void getFile() {
		String filename = null;
		try {
			connection.writeObjectToSocket("READY");
			filename = (String)connection.getObjectFromSocket();
		} catch (ClassNotFoundException | IOException e) {
			forceClose();
		}
		if(filename != null){
			try {
				System.out.println(optimizeFilePathForServer(filename));
				connection.getFileFromSocket(optimizeFilePathForServer(filename));
			} catch (Exception e) {
				forceClose();
			}
		}
			
	}
	/**
	 * Zestaw instrukcji pozwalajacy na usuniecie zdalne folderu przez klienta
	 */
	private void deleteFolder() {
		String filename = null;
		try {
			connection.writeObjectToSocket("READY");
			filename = (String)connection.getObjectFromSocket();
		} catch (ClassNotFoundException | IOException e) {
			forceClose();
		}
		if(filename != null){
			try {
				Files.deleteIfExists(Paths.get(optimizeFilePathForClient(filename)));
			} catch (IOException e) {
				forceClose();
			}
		}
			
	}
	/**
	 * Zestaw instrukcji pozwalajacy na usuniecie zdalne pliku przez klienta
	 */
	private void deleteFile() {
			deleteFolder();
			
	}

	/**
	 * Metoda do idetyfikacji uzyszkownika
	 * @return String z nazwa uzyszkownika
	 * @throws Exception 
	 */
	private String getUserLogin() throws Exception {
		return (String)connection.getObjectFromSocket();
	}
	
	/**
	 * Konczenie watku glownego zamykanie polaczenia z klientem
	 */
	public void forceClose() {
		thread_running = false;
		if (socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Wymuszone zakonczenie od strony serwera");
			}
		}
	}
	
	/**
	 * Dostosowanie path pliku przesylanej przez klienta do struktury plikow serwera
	 * @param filename path podana przez klienta
	 * @return
	 */
	private String optimizeFilePathForServer(String filename){
		String new_filename = backupPath + "\\" + filename;
		new_filename.replace(':', ';');
		return new_filename;
	}
	/**
	 * Dostosowanie path pliku przesylanej przez serwer do struktury plikow klienta
	 * @param filename �cie�ka na serwerze 
	 * @return
	 */
	private String optimizeFilePathForClient(String filename){
		
		
		String new_filename = new String(filename);
		new_filename = absoluteBackupPath + "\\" +  new_filename;
		new_filename.replace(':', ' ');
		System.out.println(new_filename);
		
		return new_filename;
	}
}