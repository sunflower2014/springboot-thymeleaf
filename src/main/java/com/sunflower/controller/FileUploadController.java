package com.sunflower.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sunflower.model.UploadItem;
import com.sunflower.repository.StorageFileNotFoundException;
import com.sunflower.service.StorageService;

@Controller
public class FileUploadController {
	
	private final StorageService storageService;
	
	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	@GetMapping("/files")
	public String listUploadedFiles(Model model) throws IOException {
		model.addAttribute("files", storageService
												.loadAll()
												.map(path -> MvcUriComponentsBuilder
																			.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
																			.build().toString())
												.collect(Collectors.toList()));
		
		return "uploadForm";
	}
	
	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
	}
	
	@PostMapping("/files")
	public String handleFileUpload(UploadItem uploadItem,
								   BindingResult result,
								   HttpServletRequest request,
								   HttpServletResponse response,
								   HttpSession session,
								   RedirectAttributes redirectAttributes) {
		System.out.println("+++++++++++++++handleFileUpload++++++++++++++");
		System.out.println(uploadItem.getFilename());
		for(MultipartFile multipartFile:uploadItem.getFileData()) {
			storageService.store(multipartFile);
		}
		redirectAttributes.addFlashAttribute("message", "You successfully uploaded !");
		return "redirect:/files";
	}
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
