package br.edu.utfpr.servicebook.model.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Table(name = "professionals")
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class Professional extends User {

	private static final long serialVersionUID = 1L;

	public Professional(String name, String email, String type, String phoneNumber, String cpf){
		super(name,email, type, phoneNumber);
		this.cpf =  cpf;
	}
	
	@ManyToMany
	@JoinTable(name = "professional_expertises",
			  joinColumns = @JoinColumn(name = "professional_id"),
			  inverseJoinColumns = @JoinColumn(name = "expertise_id"))
	private Set<Expertise> expertises = new HashSet<>();
	
	@NonNull
	@Column(unique = true)
	private String cpf;
	
	@OneToMany(mappedBy = "professional")
	private Set<JobContracted> jobContracted = new HashSet<>();

	@OneToMany(mappedBy = "professional")
	Set<JobCandidate> candidatures;

	private String description;

	private int rating;

	private int denounceAmount;

}
