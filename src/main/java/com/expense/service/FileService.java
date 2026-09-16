package com.expense.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.io.IOException;
import jakarta.annotation.PostConstruct;

@Service
public class FileService {

	private final Path uploadPath=Paths.get("uploads/profile");
	
	@PostConstruct
	public void init() throws IOException {
	    try {
			Files.createDirectories(uploadPath);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	public String saveFile(MultipartFile file)throws IOException, java.io.IOException {
	    String originalFileName = file.getOriginalFilename();
	    String extension = "";
	    
	    if (originalFileName != null && originalFileName.contains(".")) 
	    {
	        extension = originalFileName.substring(originalFileName.lastIndexOf("."));
	    }

	    String fileName = UUID.randomUUID() + extension;

	    Path targetLocation = uploadPath.resolve(fileName);
	    Files.copy(file.getInputStream(),targetLocation,StandardCopyOption.REPLACE_EXISTING);
	    return "profile/" + fileName;
	}
}
