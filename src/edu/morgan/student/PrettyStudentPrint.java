package edu.morgan.student;

import java.util.HashMap;

public class PrettyStudentPrint {
    private String studentInfo;
    private HashMap<String,String> checklist = new HashMap<String,String>();

    public PrettyStudentPrint(String name){
        this.setStudentInfo(name);
    }
    
    public void setChecklistItem(String tag, String foundOrNot){
        if(foundOrNot.toLowerCase().equals("found"))
            this.checklist.put(tag, "y");
        else
            this.checklist.put(tag, "n");
    }
    
    public HashMap<String,String> getChecklist(){
        return this.checklist;
    }
   
    /**
     * @return the studentInfo
     */
    public String getStudentInfo() {
        return studentInfo;
    }

    /**
     * @param studentInfo the studentInfo to set
     */
    public void setStudentInfo(String studentInfo) {
        this.studentInfo = studentInfo;
    }
}