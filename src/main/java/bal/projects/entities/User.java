package bal.projects.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Entity
@Table(	name = "users")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Builder
@AllArgsConstructor
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String identifiant;
	@NotBlank
	@Size(max = 20)
	@Column(unique = true)
	private String username;
	private String firstname;
	private String lastname;
	@NotBlank
	@Size(max = 50)
	@Email
	@Column(unique = true)
	private String email;
	@Size(max = 120)
	private String password;
	private String profile;
	private String function;
	private Boolean isJury;
	@Enumerated(EnumType.STRING)
	private Level level;
	@Enumerated(EnumType.STRING)
	private Speciality speciality;
	private String className;
	private float meanPI;
	@Temporal(TemporalType.DATE)
	private Date dateCreation;
	@Temporal(TemporalType.DATE)
	private Date dateModification;
	private Boolean status;
	private Integer code;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(	name = "user_roles", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_notifications", 
				joinColumns = @JoinColumn(name = "user_id"), 
				inverseJoinColumns = @JoinColumn(name = "notification_id"))
	private Set<Notification> notifications = new HashSet<>();
	
	@OneToOne(mappedBy = "responsable")
	private Option option;
	
	@ManyToOne
	private Option myOption;
	
	@ManyToMany(cascade = CascadeType.REFRESH,fetch = FetchType.LAZY)
	@JoinTable(	name = "user_modules", 
	joinColumns = @JoinColumn(name = "teacher_id"), 
	inverseJoinColumns = @JoinColumn(name = "module_id"))
	private List<Module> modules = new ArrayList<>();
	
	public User() {
	}
	public User(String username, String email, String password,String lastname,String firstname) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.lastname = lastname;
		this.firstname = firstname;
	}
}
