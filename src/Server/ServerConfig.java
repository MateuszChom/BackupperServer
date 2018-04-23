package Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *  Klasa z ktorej wczytujemy wartosci config
 * @author Mateusz
 */
public class ServerConfig {
		private static final String CONFIG_FILE_NAME = "config.conf";
		private static Properties prop;
		/**
		 * Konstruktor inicjuje wczytywanie ustawien z pliku config.conf
		 */
		static{
			prop = loadFromFile();
		}

		private static Properties loadFromFile() {
			Properties p = new Properties();
			File file = new File(CONFIG_FILE_NAME);
			InputStream input;
			try{
				input = new FileInputStream(file);
				p.load(input);
				input.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return p;
		}
		/**
		 * 
		 * @return Zwraca numer portu
		 */
		public static int getPort(){
			return Integer.parseInt(prop.getProperty("Port"));
		}
		/**
		 * 
		 * @return Zwraca path backupu gdzie powinien byc folder z uzyszkownikiem
		 */
		public static String getBackupPath(){
			return prop.getProperty("BackupPath");
		}

}
