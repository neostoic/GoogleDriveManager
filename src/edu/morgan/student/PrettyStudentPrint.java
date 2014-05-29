package edu.morgan.student;

import java.util.HashMap;

public class PrettyStudentPrint {
    private String studentInfo;
    private HashMap<String,String> checklist = new HashMap<String,String>();

    public PrettyStudentPrint(String name){
        this.setStudentInfo(name);
        //String[] tags = {"TSTS", "S05", "SAT", "S01", "S02", "IE11", "IE37", "IE75", "IEW", "IEX", "APO", "APH", "AP25", "APW", "AUD2", "AUDE", "LRE2", "LRE1", "SSC", "COBC", "COMC", "FC", "CON", "CER", "HST", "CLT", "UNO", "TRNE", "D214", "RESP", "ASG", "TREL", "AOS", "ESSY", "AP", "BRAC", "ARTP", "COMT", "MAD", "IEP", "ECE1", "MDHR", "REF3", "ISA", "IELT", "SUPP", "GCEA", "GCEO", "CLEP", "PASS", "COPS", "GRR", "GRRP", "ETR", "ESL", "DEPA", "DEPD", "DACA", "EAC", "IEG", "GED", "BS", "CPE", "F1", "I797", "NEDP", "PAC", "MIDY", "MO", "COOR", "RESU", "RSV", "WES1", "TOEFL", "TAXP", "TSE", "SS", "TAXP", "PRC", "OFEX"};
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
    /*
    public String getChecklist(){
        String results = "";
        int size = 0;
        for(String value : this.checklist.values()){
            size++;
            if(size == this.checklist.size() || size == 1)
                results += value;
            else
                results += ", " + value;
        }
        return results;
    }
    */
    
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