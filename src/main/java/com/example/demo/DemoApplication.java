package com.example.demo;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@SpringBootApplication
public class DemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Controller
	public class UploadController {
		// Azure Blob 存储连接字符串
		private final String connectionString = "DefaultEndpointsProtocol=https;AccountName=dev9d94;AccountKey=mjDdteH3zs6eIKc2edSubkgqA5evFVgsZo+UgSQ3yCYx1aLjntnS0YXlJRmygkmhG//zsTUWSOc5+ASt/MmbSg==;EndpointSuffix=core.windows.net";
		// 初始化 BlobServiceClient
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
		// 容器名称
		private final String containerName = "azure-webjobs-hosts";
		// Blob 名称
		private final String blobName = "dev9d94";
		// 获取 BlobContainerClient
		BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
		// 获取 BlobClient
		BlobClient blobClient = containerClient.getBlobClient(blobName);

		@GetMapping("/upload")
		String showUploadForm() {
			return "upload"; // 返回上传表单的视图名称
		}

		@PostMapping("/upload")
		String handleFileUpload(MultipartFile file, Model model) {
			try {
				if (!file.isEmpty()) {

					String originalFileName = file.getOriginalFilename();
					String uniqueFileName = System.currentTimeMillis() + "-" + originalFileName;

					// 获取文件输入流
					InputStream inputStream = file.getInputStream();

					blobClient.upload(file.getInputStream(), file.getSize());
					model.addAttribute("uploadSuccess", true);
					model.addAttribute("message", "文件上传成功！");
					model.addAttribute("fileName", uniqueFileName);
					model.addAttribute("fileSize", formatFileSize(file.getSize())); // 格式化文件大小
					// 关闭输入流
					inputStream.close();

					return "uploadResult";
					} else {
					model.addAttribute("uploadSuccess", false);
					model.addAttribute("message", "请选择一个文件进行上传。");

					return "uploadResult";
				}
			} catch (Exception e) {
				model.addAttribute("uploadSuccess", false);
				model.addAttribute("message", "上传失败：" + e.getMessage());
			}

			return "uploadResult"; // 返回上传结果视图
		}

		public class ErrorController {
			@RequestMapping("/error")
			public String handleError() {
				return "error"; // 返回错误页面的视图名称
			}
		}
		// 格式化文件大小的方法
		private String formatFileSize(long fileSize) {
			if (fileSize <= 0) {
				return "0 B";
			}

			final String[] units = {"B", "KB", "MB", "GB", "TB"};
			int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));

			return String.format("%.1f %s", fileSize / Math.pow(1024, digitGroups), units[digitGroups]);
		}




	}
}


