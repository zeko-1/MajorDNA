package com.majordna.model;

public final class CityUStudent extends User {
    private String studentId;
    public CityUStudent(String name, int age, String studentId) { super(name, age); setStudentId(studentId); }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId == null ? "" : studentId.trim(); }
    @Override public String getUserType() { return "CITYU_STUDENT"; }
}
