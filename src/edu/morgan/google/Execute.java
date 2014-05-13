package edu.morgan.google;

import java.io.IOException;
import java.util.ArrayList;

import edu.morgan.student.IncompleteStudent;
import edu.morgan.student.PrettyStudentPrint;

public class Execute {
	public void changeChecklist(ArrayList<IncompleteStudent> studentsList, String checklistItem, IncompleteStudent student) throws IOException{
        String aux;
        aux = student.getChecklist().replace(checklistItem + "::", "");
        if(student.getChecklist().equals(aux))
            aux = student.getChecklist().replace(checklistItem, "");

        student.setChecklist(aux);

        if(student.getChecklist().equals(""))
            student.setChecklist("COMPLETE");

        if(!studentsList.contains(student))
            studentsList.add(student);
        else
            studentsList.get(studentsList.indexOf(student)).setChecklist(aux);
    }
    public void organizeArray(ArrayList<PrettyStudentPrint> pspArray, PrettyStudentPrint psp, String dataChanged, String token){
        if(pspArray.contains(psp)){
            if(!pspArray.get(pspArray.indexOf(psp)).getFoundChecklist().contains(dataChanged))
                pspArray.get(pspArray.indexOf(psp)).setFoundChecklist(dataChanged);
        }
        else{
            psp.setFoundChecklist(dataChanged);
            pspArray.add(psp);
        }
        
    }
}
