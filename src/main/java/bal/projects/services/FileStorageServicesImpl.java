package bal.projects.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServicesImpl implements IFileStorageServices{

	private static final Logger log = Logger.getLogger(FileStorageServicesImpl.class);
	
	private final Path root = Paths.get("src/main/resources/uploads");
	private final Path rootProfiles = Paths.get("src/main/resources/uploads/profiles");
	
	  @Override
	  public void init() {
	    try {
	      Files.createDirectory(root);
	    } catch (IOException e) {
	      log.debug("Could not initialize folder for upload!");
	    }
	  }
	  
	  @Override
	  public void save(MultipartFile file) {
		  
	    try {
	      Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
	    } catch (Exception e) {
	    	log.debug("Could not store the file. Error: " + e.getMessage());
	    }
	  }
	  
	  @Override
	  public Resource load(String filename) {
	    try {
	    	Path file =  root.resolve(filename);
	 
	      Resource resource = new UrlResource(file.toUri());
	      if (resource.exists() || resource.isReadable()) {
	        return resource;
	      } else {
	    	  log.info("Could not read the file! :"+filename);
	    	 return null;
	      }
	    } catch (MalformedURLException e) {
	    	log.debug(e);
	      return null;
	    }
	    
	  }
	  
	  @Override
	  public Resource loadProfiles(String filename) {
	    try {
	    	Path file =  rootProfiles.resolve(filename);
	 
	      Resource resource = new UrlResource(file.toUri());
	      if (resource.exists() || resource.isReadable()) {
	        return resource;
	      } else {
	    	  log.info("Could not read the file!");
		    return new UrlResource(root.resolve("default-profile.jpg").toUri());
	      }
	    } catch (MalformedURLException e) {
	    	log.debug("Error: " + e.getMessage());
	    	return null;
	    }
	    
	  }
	  
	  @Override
	  public void deleteAll() {
	    FileSystemUtils.deleteRecursively(root.toFile());
	  }
	  
	  @Override
	  public Stream<Path> loadAll() {
	    try {
	      return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
	    } catch (IOException e) {
	    	log.info("Could not read the file!");
	    	return null;
	    }
	  }
}
