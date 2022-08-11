package bal.projects.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import bal.projects.entities.Attachements;
import bal.projects.payload.response.MessageResponse;
import bal.projects.services.IFileStorageServices;

@RestController
@RequestMapping("/joints")
public class FileController {

	@Autowired
	IFileStorageServices storageService;
	  @PostMapping("/upload")
	  public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
	    String message = "";
	    try {
	      storageService.save(file);
	      message = "Uploaded the file successfully: " + file.getOriginalFilename();
	      return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
	    } catch (Exception e) {
	      message = "Could not upload the file: " + file.getOriginalFilename() + "!";
	      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
	    }
	  }
	  @GetMapping("/files")
	  public ResponseEntity<List<Attachements>> getListFiles() {
	    List<Attachements> fileInfos = storageService.loadAll().map(path -> {
	      String filename = path.getFileName().toString();
	      String url = MvcUriComponentsBuilder
	          .fromMethodName(FileController.class, "getFile", path.getFileName().toString()).build().toString();
	      return new Attachements(filename, url);
	    }).collect(Collectors.toList());
	    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	  }
	  
	  @GetMapping("/files/{filename:.+}")
	  @ResponseBody
	  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
	    Resource file = storageService.load(filename);
	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
	  }
	 
	  @GetMapping("/profiles/{filename:.+}")
	  @ResponseBody
	  public ResponseEntity<Resource> getFileForProfile(@PathVariable String filename) throws IOException {
	    Resource file = storageService.loadProfiles(filename);
	    Path path = file.getFile()
                .toPath();
	    
	    return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
	  }
	  
}
