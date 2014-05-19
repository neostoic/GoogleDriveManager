package edu.morgan;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.services.drive.model.File;

import edu.morgan.google.GoogleDrive;
import edu.morgan.google.Start;
import edu.morgan.student.IncompleteStudent;

public class Results extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    	PrintWriter out = response.getWriter();
    	
    	@SuppressWarnings("unchecked")
		ArrayList<IncompleteStudent> studentList = (ArrayList<IncompleteStudent>) request.getAttribute("studentlist");
    	String googleToken = request.getParameter("code");
    	
    	GoogleDrive drive = new GoogleDrive();
    	drive.setCode(googleToken);
    	
    	/*
    	out.print(drive);
    	out.println();
    	out.println(drive.getCode());
    	*/
    	
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Morgan Admissions - Web App</title>"); 
        out.println("<link href='http://getbootstrap.com/dist/css/bootstrap.min.css' rel='stylesheet'/>");
        out.println("</head>");
        out.println("<body style='background-color:#eee;'>");
        out.println("	<div class='header container' style='width:700px'>");
        out.println("		<img src='http://www.morgan.edu/images/shared/logo-headerRt.png' style='width: 700px;' />");
        out.println("	</div>");
        
        out.println("<h3 align='center'>All set! Check your GoogleDrive account.</h3>\n");
        out.println("<h4 align='center'>Incomplete Students checked:" + studentList.size() + " </h4><br/>");
        out.println("<div class='container' style='width:250px'>");
        
        out.println("Student list:");
        
        for(File file :drive.getStudentFiles(new String[]{studentList.get(0).getLastName(), studentList.get(0).getFirstName(), studentList.get(0).getId(), "high", "school"}) )
        	out.println(file.getTitle());
        /*
        for(IncompleteStudent student : studentList)
        	out.println(student.getFirstName() + ", " + student.getLastName());
        */
        
        //this.OrganizeFiles(drive, out, studentList);
        
        /*
        ArrayList<File> studentFiles = drive.getAllFiles();
        for(File file : studentFiles)
        	out.println(file.getTitle());
        */
        out.println("</div>");
        
        out.println("</body>");
        out.println("</html>");
    }
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    public void OrganizeFiles(GoogleDrive drive, PrintWriter out, ArrayList<IncompleteStudent> incompletestudents){
        //Execute exec = new Execute();

        //ArrayList<IncompleteStudent> studentsProcessed = new ArrayList<IncompleteStudent>();
        //ArrayList<PrettyStudentPrint> prettyPrint = new ArrayList<PrettyStudentPrint>();

        try {

            ArrayList<File> googleDriveFolders = drive.getAllFolders();
            int counter = 0;
            File autoFolder = drive.getCreateFolder(googleDriveFolders,"AUTO");
            
            for (IncompleteStudent student : incompletestudents) {
                //PrettyStudentPrint psp = new PrettyStudentPrint(student.getLastName() + ", " + student.getFirstName() + ", " + student.getId());

                // Get or Create Folder
                File studentFolder = drive.getCreateFolder(googleDriveFolders, student.getLastName(), student.getFirstName(), student.getId());

                out.println("<li>" + student.getLastName() + ", " + student.getFirstName() + " - " + ++counter + "</li>");

                if (!student.getChecklist().equals("")) {
                    for (String checklistitem : student.getChecklist().split("::")) {
                        ArrayList<File> tempFiles = new ArrayList<File>();
                        String codeItem = "";

                        if (checklistitem.contains("act") && checklistitem.contains("sat") && checklistitem.contains("scores")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "scores", "sat", "act"});
                            if (!tempFiles.isEmpty()) {
                                ////exec.organizeArray(prettyPrint, psp, "TSTS", "found");
                                ////exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TSTS", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("scores")) {
                            if (checklistitem.contains("sat")) {
                                if (checklistitem.contains("tswe")) {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "scores", "sat", "tswe"});
                                    codeItem = "S05";
                                } else {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "scores", "sat"});
                                    codeItem = "SAT";
                                }

                                if (!tempFiles.isEmpty()) {
                                    //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                    //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                    for (File file : tempFiles) {
                                        drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                    }
                                }
                            }

                            if (checklistitem.contains("act")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "scores", "act"});
                                if (!tempFiles.isEmpty()) {
                                    //exec.organizeArray(prettyPrint, psp, "TSTS", "found");
                                    //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                    for (File file : tempFiles) {
                                        drive.MoveFiles(file, studentFolder, student, "TSTS", checklistitem);
                                    }
                                }
                            }
                        }
                        if (checklistitem.contains("sat")) {
                            if (checklistitem.contains("verbal")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "sat", "verbal"});
                                codeItem = "S01";
                            } else if (checklistitem.contains("math")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "sat", "math"});
                                codeItem = "S02";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("fee")) {
                            if (checklistitem.contains("confirmation")) {
                                if (checklistitem.contains("112.50")) {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "fee", "confirmation", "112.50"});
                                    codeItem = "IE11";
                                } else if (checklistitem.contains("37.50")) {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "fee", "confirmation", "37.50"});
                                    codeItem = "IE37";
                                } else if (checklistitem.contains("75.00")) {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "fee", "confirmation", "75.00"});
                                    codeItem = "IE75";
                                } else if (checklistitem.contains("waiver")) {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "fee", "confirmation", "waiver"});
                                    codeItem = "IEW";
                                } else if (checklistitem.contains("nexus")) {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "fee", "confirmation", "nexus"});
                                    codeItem = "IEX";
                                }
                            } else if (checklistitem.contains("waiver")) {
                                if (checklistitem.contains("house") && checklistitem.contains("open")) {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "fee", "waiver", "house", "open"});
                                    codeItem = "APO";
                                } else if (checklistitem.contains("half")) {
                                    tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "fee", "waiver", "half"});
                                    codeItem = "APH";
                                }
                            } else if (checklistitem.contains("application") && checklistitem.contains("35")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "fee", "application", "35"});
                                codeItem = "AP25";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("waiver") && checklistitem.contains("application")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "waiver", "application"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "APW", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "APW", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("detailed") && checklistitem.contains("eval")) {
                            if (checklistitem.contains("ece")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "detailed", "eval", "ece"});
                                codeItem = "AUD2";
                            } else if (checklistitem.contains("wes")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "detailed", "eval", "wes"});
                                codeItem = "AUDE";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("recommendation")) {
                            if (checklistitem.contains("counselor")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "recommendation", "counselor"});
                                codeItem = "LRE2";
                            } else if (checklistitem.contains("teacher")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "recommendation", "teacher"});
                                codeItem = "LRE1";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("certificate")) {
                            if (checklistitem.contains("secondary") && checklistitem.contains("school")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "certificate", "secondary", "school"});
                                codeItem = "SSC";
                            } else if (checklistitem.contains("birth")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "certificate", "birth"});
                                codeItem = "COBC";
                            } else if (checklistitem.contains("marriage")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "certificate", "marriage"});
                                codeItem = "COMC";
                            } else if (checklistitem.contains("financial")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "certificate", "financial"});
                                codeItem = "FC";
                            } else if (checklistitem.contains("naturalization")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "certificate", "naturalization"});
                                codeItem = "CON";
                            } else if (checklistitem.contains("achievement")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "certificate", "achievement"});
                                codeItem = "CER";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("essay") && checklistitem.contains("personal")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "essay", "personal"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "ESSY", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "ESSY", checklistitem);
                                }
                            } else {
                                //exec.organizeArray(prettyPrint, psp, checklistitem, "not");
                            }
                        }
                        if (checklistitem.contains("transcript")) {
                            codeItem = "";
                            if (checklistitem.contains("high") && checklistitem.contains("school")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "high", "school", "transcript"});
                                codeItem = "HST";
                            } else if (checklistitem.contains("official") && checklistitem.contains("college")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "college", "transcript"});
                                codeItem = "CLT";
                            } else if (checklistitem.contains("unofficial")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "unofficial", "transcript"});
                                codeItem = "UNO";
                            } else if (checklistitem.contains("evaluation")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "evaluation", "transcript"});
                                codeItem = "TRNE";
                            }
                            if (!tempFiles.isEmpty()) {
                                if (codeItem.equals("TRNE")) {
                                    //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                }
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("form")) {
                            if (checklistitem.contains("dd") && checklistitem.contains("214")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "dd", "214", "form"});
                                codeItem = "D214";
                            } else if (checklistitem.contains("residence")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "form", "residence"});
                                codeItem = "RESP";
                            } else if (checklistitem.contains("asylium-refugee")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "form", "asylium-refugee"});
                                codeItem = "ASG";
                            } else if (checklistitem.contains("transfer") && checklistitem.contains("eligibility")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "form", "transfer", "eligibility"});
                                codeItem = "TREL";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("affidavit") && checklistitem.contains("support")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "affidavit", "support"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "AOS", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "AOS", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("advanced") && checklistitem.contains("placement") && checklistitem.contains("board")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "advanced", "affidavit", "support"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "AP", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "AP", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("civilian") && checklistitem.contains("millitary") && checklistitem.contains("person")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "civilian", "millitary", "person"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "BRAC", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "BRAC", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("art") && checklistitem.contains("portfolio")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "art", "portfolio"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "ARTP", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "ARTP", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("maryland")) {
                            if (checklistitem.contains("tax") && checklistitem.contains("return")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "maryland", "tax", "return"});
                                codeItem = "COMT";
                            } else if (checklistitem.contains("adult") && checklistitem.contains("external") && checklistitem.contains("diploma") || checklistitem.contains("diplom")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "maryland", "adult", "external", "diploma"});
                                codeItem = "MAD";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("educational") || checklistitem.contains("educ")) {
                            if (checklistitem.contains("individual") && checklistitem.contains("plan")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "educational", "tax", "return"});
                                codeItem = "IEP";
                            } else if (checklistitem.contains("world") && checklistitem.contains("services")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "educational", "adult", "external"});
                                codeItem = "";
                            } else if (checklistitem.contains("evaluators") && checklistitem.contains("cred")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "educational", "adult", "external"});
                                codeItem = "ECE1";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("letter")) {
                            if (checklistitem.contains("human") && checklistitem.contains("resources")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "letter", "human", "resources"});
                                codeItem = "MDHR";
                            } else if (checklistitem.contains("reference")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "letter", "reference"});
                                codeItem = "REF3";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("international")) {
                            if (checklistitem.contains("student") && checklistitem.contains("application") || checklistitem.contains("app")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "international", "student", "application"});
                                codeItem = "ISA";
                            } else if (checklistitem.contains("english") && checklistitem.contains("lan") && checklistitem.contains("test")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "international", "english", "lan", "test"});
                                codeItem = "IELT";
                            } else if (checklistitem.contains("info") && checklistitem.contains("applicant") || checklistitem.contains("app")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "international", "info", "applicant"});
                                codeItem = "SUPP";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("level")) {
                            if (checklistitem.contains("gca") && checklistitem.contains("advance")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "level", "gca", "advance"});
                                codeItem = "GCEA";
                            } else if (checklistitem.contains("gce") && checklistitem.contains("ordinary")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "level", "ordinary", "gce"});
                                codeItem = "GCEO";
                            } else if (checklistitem.contains("college") && checklistitem.contains("exam") && checklistitem.contains("program")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "level", "exam", "program", "college"});
                                codeItem = "CLEP";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("passport")) {
                            if (checklistitem.contains("non")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "non", "passport"});
                                codeItem = "PASS";
                            } else if (checklistitem.contains("us")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "us", "passport"});
                                codeItem = "COPS";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("report") || checklistitem.contains("repo")) {
                            if (checklistitem.contains("grade") && checklistitem.contains("card")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "report", "grade"});
                                codeItem = "GRR";
                            } else if (checklistitem.contains("secondary") && checklistitem.contains("school")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "report", "secondary", "school"});
                                codeItem = "GRRP";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("english")) {
                            if (checklistitem.contains("translation") && checklistitem.contains("records")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "english", "translation", "records"});
                                codeItem = "ETR";
                            } else if (checklistitem.contains("second") && checklistitem.contains("language")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "english", "second", "language"});
                                codeItem = "ESL";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("approval")) {
                            if (checklistitem.contains("department")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "approval", "department"});
                                codeItem = "DEPA";
                            } else if (checklistitem.contains("departmental")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "approval", "departmental"});
                                codeItem = "DEPD";
                            }
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("deferred") && checklistitem.contains("action") && checklistitem.contains("childhood")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "deferred", "action", "childhood"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "DACA", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "DACA", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("employment") && checklistitem.contains("authorizaation")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "employment", "authorization"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "EAC", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "EAC", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("confirmation") && checklistitem.contains("incentive")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "confirmation", "incentive"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "IEG", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "IEG", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("graduate") && checklistitem.contains("diploma") && checklistitem.contains("equivalency")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "graduate", "diploma", "equivalency"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "GED", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("bank") && checklistitem.contains("statement")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "bank", "statement"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "BS", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "BS", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("cambridge") && checklistitem.contains("proficiency") && checklistitem.contains("test")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "cambridge", "proficiency", "test"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "CPE", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "CPE", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("i-20") && checklistitem.contains("student") && checklistitem.contains("visa")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "i-20", "student", "visa"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "F1", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "F1", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("dream") && checklistitem.contains("act")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "dream", "act"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "I797", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("national") && checklistitem.contains("external") && checklistitem.contains("diploma")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "national", "external", "diploma"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "NEDP", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "NEDP", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("paternal") && checklistitem.contains("consent")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "consent", "paternal"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "PAC", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "PAC", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("midyear") || checklistitem.contains("mid year") || checklistitem.contains("mid-year")  && checklistitem.contains("grade")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "grade", "mid-year"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "MIDY", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "MIDY", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("military") && checklistitem.contains("orders")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "military", "orders"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "MO", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "MO", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("court") && checklistitem.contains("order")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "court", "order"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "COOR", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "COOR", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("resume")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "resume"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "RESU", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "RESU", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("residence") && checklistitem.contains("verification")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "residence", "verification"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "RSV", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "RSV", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("world") && checklistitem.contains("education") || checklistitem.contains("educ")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "world", "education"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "WES1", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "WES1", checklistitem);
                                }
                            }
                        } //
                        if (checklistitem.contains("toefl")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "toefl"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "TOEFL", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TOEFL", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("tax") && checklistitem.contains("return") && checklistitem.contains("personal")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "tax", "return", "personal"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "TAXP", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TAXP", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("test") && checklistitem.contains("spoken") && checklistitem.contains("english")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "test", "spoken", "english"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "TSE", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TSE", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("social") && checklistitem.contains("security")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "security", "social"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "SS", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "SS", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("tax") && checklistitem.contains("return") && checklistitem.contains("personal")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "tax", "return", "personal"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "TAXP", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TAXP", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("i-551") && checklistitem.contains("permanent") || checklistitem.contains("perm") && checklistitem.contains("residence")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "residence", "permanent", "i-551"});
                            if (!tempFiles.isEmpty()) {
                                //exec.organizeArray(prettyPrint, psp, "PRC", "found");
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "PRC", checklistitem);
                                }
                            }
                        }
                        if (checklistitem.contains("official") && checklistitem.contains("exam")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "sssce"});
                            codeItem = "";
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "sce"});
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "waec"});
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "cxc"});
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "gde"});
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                //exec.changeChecklist(studentsProcessed, checklistitem, student);
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            if (!codeItem.equals("")) {
                                //exec.organizeArray(prettyPrint, psp, codeItem, "found");
                            }
                        }
                    }

                }
                drive.MoveFiles(studentFolder, autoFolder);
            }

            // Generate new JSONFile
            /*
            incompletestudents.generateJSON(incompletestudents.convertToUsers(studentsProcessed), "BAFASE_new_min");
            WriteCSVFile.printArray(prettyPrint);
            WriteXMLFile.printArray(prettyPrint);
            */
            out.println("<li><h3>All students processed. Total of students: " + counter + "</h3></li>");
        } catch (Exception ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
