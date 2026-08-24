// A general explorer keeps the profile flexible for users who are not CityU students yet.
package com.majordna.model;

public final class CareerExplorer extends User {
    private String educationLevel;
    public CareerExplorer(String name, int age, String educationLevel) { super(name, age); setEducationLevel(educationLevel); }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel == null || educationLevel.isBlank() ? "Exploring" : educationLevel.trim();
    }
    @Override public String getUserType() { return "CAREER_EXPLORER"; }
}

