package com.webprogramming.project.dto;

import java.util.List;

public class SearchTermDto {

	private String etdId;
	
	private String title;
	
	private List<String> spellCorrectors; 

	public String getEtdId() {
		return etdId;
	}

	public void setEtdId(String etdId) {
		this.etdId = etdId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getSpellCorrectors() {
		return spellCorrectors;
	}

	public void setSpellCorrectors(List<String> spellCorrectors) {
		this.spellCorrectors = spellCorrectors;
	}
	
}
