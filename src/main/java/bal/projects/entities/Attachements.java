package bal.projects.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Attachements implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Enumerated(EnumType.STRING)
	private TypeFile type;
	@Column(length = 255)
	private String name;
	private String extension;
	private Integer size;
	private Integer width;
	private Integer height;
	private float fps;
	@Column(length = 255)
	private String url;
	
	public Attachements(String filename, String url) {
		this.name = filename;
		this.url = url;
	}
}
