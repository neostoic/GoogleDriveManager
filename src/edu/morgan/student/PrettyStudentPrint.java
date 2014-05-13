package edu.morgan.student;

public class PrettyStudentPrint {
    private String studentInfo;
    private String foundChecklist = "";
    private String notFoundChecklist = "";

    public PrettyStudentPrint(String name){
        this.setStudentInfo(name);
    }
   
    public String getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(String studentInfo) {
        this.studentInfo = studentInfo;
    }

    public String getFoundChecklist() {
        return foundChecklist;
    }

    public void setFoundChecklist(String foundChecklist) {
        if(this.foundChecklist.equals(""))
            this.foundChecklist = foundChecklist;
        else
            this.foundChecklist += ", " + foundChecklist;
    }

    public String getNotFoundChecklist() {
        return notFoundChecklist;
    }

    public void setNotFoundChecklist(String notFoundChecklist) {
        if(this.notFoundChecklist.equals(""))
            this.notFoundChecklist = notFoundChecklist;
        else
            this.notFoundChecklist += ", " + notFoundChecklist;
    }
}
