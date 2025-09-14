package de.turtle.pi_cloud.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "files")
public class FileEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String path;
	private Long size;
	private String type;
	private LocalDateTime uploadedAt;

	public FileEntity() {}

	public FileEntity(String name, String path, Long size, String type, LocalDateTime uploadedAt) {
		this.name = name;
		this.path = path;
		this.size = size;
		this.type = type;
		this.uploadedAt = uploadedAt;
	}

	public Long getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }
	public Long getSize() { return size; }
	public void setSize(Long size) { this.size = size; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public LocalDateTime getUploadedAt() { return uploadedAt; }
	public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
