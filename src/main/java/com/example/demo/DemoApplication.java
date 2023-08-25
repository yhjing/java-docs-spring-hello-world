package com.example.demo;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
public class DemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Controller
	public class UploadController {

		private final String connectionString = "DefaultEndpointsProtocol=https;AccountName=dev9d94;AccountKey=mjDdteH3zs6eIKc2edSubkgqA5evFVgsZo+UgSQ3yCYx1aLjntnS0YXlJRmygkmhG//zsTUWSOc5+ASt/MmbSg==;EndpointSuffix=core.windows.net";
		private final String containerName = "azure-webjobs-hosts";

		@GetMapping("/")
		String showUploadForm() {
			return "upload"; // 返回上传表单的视图名称
		}

		@PostMapping("/upload")
		String handleFileUpload(@RequestParam("file")MultipartFile file, Model model) {
			try {
				if (!file.isEmpty()) {
					BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
					BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(file.getOriginalFilename());

					blobClient.upload(file.getInputStream(), file.getSize());
					model.addAttribute("message", "文件上传成功！");
				} else {
					model.addAttribute("message", "请选择一个文件进行上传。");
				}
			} catch (Exception e) {
				model.addAttribute("message", "上传失败：" + e.getMessage());
			}

			return "uploadResult"; // 返回上传结果视图
		}
	}
}



