package Filesystem;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
/**
 * Klasa sluzaca do zapisania struktury plikow w podanej path. 
 * Kazdy element reprezentuje jedna pozycje w systemie plikow.
 * @author Mateusz
 *
 */
public class FilesystemElement implements Serializable {
	private static final long serialVersionUID = -3196569544239058008L;
	/**Zwraca true jezeli folder jezeli nie to false*/
	private boolean is_directory;
	/**Nazwa pliku w systemie*/
	private String name;
	/**Data modyfikacji. Oznacza ilosc sekund od 1 stycznia 1970r gdyz tak narzuca nam java.*/
	private long last_modified;
	//**Referencja na folder nadrzedny, w ktorym znajduje sie plik lub folder*/
	private FilesystemElement parent;
	/**Lista plikow i folderow znajdujacych sie w tym katalogu*/
	private List<FilesystemElement> children;
	
	/**
	 * Konstruktor
	 * @param file wskazuje na katalog/plik ktory ma ten obiekt reprezentowac.
	 * @param parent null jezeli ten element ma okreslenie najwyzszego katalogu.
	 * @throws IOException
	 */
	public FilesystemElement(File file, FilesystemElement parent) throws IOException{
		this.is_directory = file.isDirectory();
		this.parent = parent;
		
		if(is_directory  && file != null){
			children = new ArrayList<FilesystemElement>();
			Vector<File> file_list = new Vector<File>(Arrays.asList(file.listFiles()));
			/*
			File[] files = file.listFiles(/*new FileFilter(){
												public boolean accept(File f){
													if(f.getName().star	tsWith("$") ||
													   f.getName() == "Windows" || 
													   f.isHidden() || 
													   f.canRead() == false){
														return false;
													}
													return true;
											}});
											*/
			if (file_list != null){
				for(File child : file_list){
					if (child != null){
						children.add(new FilesystemElement(child, this));
					}
				}
			}
		}
		else{
			
		}
		this.name = file.getName();
		this.last_modified = file.lastModified();
		System.out.println(name);

	}
	/**
	 * 
	 * @return Zwraca boolean czy obiekt reprezentuje folder
	 */
	public boolean isDirectory(){
		return is_directory;
	}
	/**
	 * 
	 * @return Zwraca boolean czy obiekt reprezentuje plik
	 */
	public boolean isFile(){
		return !is_directory;
	}
	/**
	 * 
	 * @return Zwraca pelna path liczac od obiektu ktory reprezentuje korzen
	 */
	public String getFullPath(){
		if(parent == null){
			return toString();
		}
		else{
			return new String(parent.getFullPath() + "\\" + toString());
		}
	}
	/**
	 * 
	 * @return Zwraca czas, ktory uplynal od 1 stycznia 1970r do daty modyfikacji pliku
	 */
	public long getLastModified(){
		return last_modified;
	}
	/**
	 * 
	 * @return Zwraca nazwe katalogu/pliku
	 */
	public String getName(){
		return this.toString();
	}
	public String toString(){
		return name;
	}
	/**
	 * 
	 * @return Zwraca referencje na katalog nadrzedny
	 */
	public FilesystemElement getParent(){
		return parent;
	}
	/**
	 * 
	 * @return Zwraca referencje na liste plikow i folderow potomnych
	 */
	public List<FilesystemElement>getChildren(){
		return children;
	}
	@Override
	public boolean equals(Object o){
		FilesystemElement element = FilesystemElement.class.cast(o);
		if(name != element.toString() ) return false;
		if(last_modified != element.getLastModified()) return false;
		if(is_directory != element.isDirectory()) return false;
		if(getFullPath() != element.getFullPath()) return false;
		return true;
	}
	/**
	 * Zwraca FilesystemElement o podanej nazwie i path do pliku
	 * @param filename nazwa pliku
	 * @param path sciezka do pliku
	 * @return
	 */
	public FilesystemElement getFile(String filename, String path){
		if(path == null){
			for(FilesystemElement child : children){
				if(child.getName() == filename && child.isFile()){
					return child;
					
				}
			}
		}
		else{
			String[] temp = path.split("\\\\", 2);
			for(FilesystemElement child : children){
				if(child.getName() == temp[0] && child.isDirectory()){
					if(temp.length == 1){
						return child.getFile(filename, null);
					}
					else{
						return child.getFile(filename, temp[1]);
					}
					
				}
			}
		}
		return null;
		
	}
	/**
	 * Zwraca FilesystemElement pliku o podanej nazwie
	 * @param filename nazwa pliku
	 * @return
	 */
	public FilesystemElement getFile(String filename){
		System.out.println(filename);
		FilesystemElement temp;
		for(FilesystemElement child : children){
			if(child.getName().equals(filename) && child.isFile()){
				return child;
				
			}
			else{
				if(child.isDirectory()){
					temp = child.getFile(filename);
					if (temp != null){
						return temp;
					}
				}
			}
		}
		return null;
		
	}
	private FilesystemElement(){}
}
