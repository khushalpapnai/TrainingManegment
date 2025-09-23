package org.trainman.model;
import java.time.LocalDate;

public class Employee {
    private String empId;
    private String name;
    private String gender;
    private LocalDate doj;
    private String nsbtBatchNo;
    private String status;
    private LocalDate resignationDate;
    private LocalDate releasedDate;
    private String grade;
    private String bu;
    private String mprNo;
    private String ioName;

    // getters and setters
    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getDoj() { return doj; }
    public void setDoj(LocalDate doj) { this.doj = doj; }
    public String getNsbtBatchNo() { return nsbtBatchNo; }
    public void setNsbtBatchNo(String nsbtBatchNo) { this.nsbtBatchNo = nsbtBatchNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getResignationDate() { return resignationDate; }
    public void setResignationDate(LocalDate resignationDate) { this.resignationDate = resignationDate; }
    public LocalDate getReleasedDate() { return releasedDate; }
    public void setReleasedDate(LocalDate releasedDate) { this.releasedDate = releasedDate; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getBu() { return bu; }
    public void setBu(String bu) { this.bu = bu; }
    public String getMprNo() { return mprNo; }
    public void setMprNo(String mprNo) { this.mprNo = mprNo; }
    public String getIoName() { return ioName; }
    public void setIoName(String ioName) { this.ioName = ioName; }
}

