package bal.projects.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "options")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Option implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;
	@Enumerated(EnumType.STRING)
	private Speciality speciality;
	@Temporal(TemporalType.DATE)
	private Date dateCreation;
	@Temporal(TemporalType.DATE)
	private Date dateModification;
	
	@JsonIgnore
	@OneToOne
	private User responsable;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(	name = "option_modules", 
				joinColumns = @JoinColumn(name = "option_id"), 
				inverseJoinColumns = @JoinColumn(name = "module_id"))
	private List<Module> modules;
}
