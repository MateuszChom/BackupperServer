package Connection;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
/**
 * Klasa odpowiadajaca za interfejs pomiedzy gniazdkami klienta i serwera
 * @author Mateusz
 *
 */
public class ConnectionHandler {
	private static final long serialVersionUID = 1L;
	
	private Socket socket;
	public ConnectionHandler(Socket socket){
		this.socket = socket;
	}
	private ConnectionHandler(){}
	
	/**
	 * Odczytanie pliku z gniazdka
	 * @param filename Nazwa pliku odczytanego z gniazdka
	 * @throws Exception 
	 */
	public void getFileFromSocket(String filepath) throws Exception{
		String str = (String)getObjectFromSocket();
		if (str.equals("FILE_NOT_FOUND")){
			throw new Exception("File not found");
		}
		else if(str.equals("READY")){
			byte[] mybytearray = new byte[1024];
			
		    FileOutputStream fos = new FileOutputStream(filepath);
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    
		    long numberOfBlocks = (long)getObjectFromSocket();    
		    for(long i = 0; i < numberOfBlocks; i++)
		    {
		    	mybytearray = (byte[])(getObjectFromSocket());
		    	int len = (int)(getObjectFromSocket());
		    	bos.write(mybytearray, 0, len);
		    }
		    bos.close();
		}
		else if(str.equals("EMPTY_FILE")){
			File f = new File(filepath);
			f.createNewFile();
		}
		else{
			throw new Exception("Communication error");
		}
	}
	
	/**
	 * Odczytanie obiektu z gniazdka
	 * @return Obiekt odczytany z gniazdka
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object getObjectFromSocket() throws IOException, ClassNotFoundException{
		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
		Object obj = input.readObject();
		return obj;
		
	}
	/**
	 * Wyslanie obiektu do gniazdka
	 * @param obj Obiekt do wyslania
	 * @throws IOException 
	 */
	public void writeObjectToSocket(Object obj) throws IOException{
		ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
		output.writeObject(obj);
		output.flush();
		
	}
	/**
	 * Wysylanie pliku do/na gniazdka
	 * @param filename Plik do wyslania
	 * @throws IOException 
	 */
	public void writeFileToSocket(String filepath) throws IOException{
		File file = new File(filepath);
		if(file.exists() == false){
			writeObjectToSocket("FILE_NOT_FOUND");
		}
		else{
			
			if(file.length() == 0) writeObjectToSocket("EMPTY_FILE");
			else{
				writeObjectToSocket("READY");
				FileInputStream fis = new FileInputStream(file);
			    BufferedInputStream bis = new BufferedInputStream(fis);
			    	byte [] bytearray  = new byte [1024];
				    long numberOfBlocks = file.length()/bytearray.length +1;
				    writeObjectToSocket(numberOfBlocks);
				    for(long i = 0; i < numberOfBlocks; i++)
				    {	
				    	int len = bis.read(bytearray,0,bytearray.length);
				    	writeObjectToSocket(bytearray);
				    	writeObjectToSocket(len);
				    	
				    }
				    bis.close();
			}
		    
		    
		}
	}
}