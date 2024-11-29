package com.webprogramming.project.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class EtdDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer etdId;

	@Column
	private String title;

	@Column
	private String author;

	@Column
	private int year;

	@Column
	private String university;

	@Column
	private String program;

	@Column
	private String degree;
	
	@Column
	private String advisor;

	@Column
	private String abstractText;
	
	@Column
	private String pdf;

	// Constructors, getters, and setters

	public EtdDto() {
	}

	public EtdDto(String title, String author, int year, String university, String program, String degree,
			String advisor, String abstractText, String pdf) {
		this.title = title;
		this.author = author;
		this.year = year;
		this.university = university;
		this.program = program;
		this.degree = degree;
		this.advisor = advisor;
		this.abstractText = abstractText;
		this.pdf = pdf;
	}

	// Getters and setters for each field

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getAdvisor() {
		return advisor;
	}

	public void setAdvisor(String advisor) {
		this.advisor = advisor;
	}

	public String getAbstractText() {
		return abstractText;
	}

	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}
}
