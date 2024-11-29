package com.webprogramming.project.dto;

import java.util.Map;

public class SearchResponseDto {
    private String etdid;
    private String title;
    private String author;
    private String year;
    private String university;
    private String program;
    private String degree;
    private String advisor;
    private String abstractText;
    private String pdf;
    private Map<String, String> wikifierTerms;

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

    public Map<String, String> getWikifierTerms() {
        return wikifierTerms;
    }

    public void setWikifierTerms(Map<String, String> wikifierTerms) {
        this.wikifierTerms = wikifierTerms;
    }

    public String getEtdid() {
        return etdid;
    }

    public void setEtdid(String etdid) {
        this.etdid = etdid;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
