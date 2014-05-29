package edu.morgan.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.api.services.drive.model.File;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

import edu.morgan.google.GoogleDrive;
import edu.morgan.student.IncompleteStudent;
import edu.morgan.student.PrettyStudentPrint;

public class UploadFile extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<IncompleteStudent> studentList = new ArrayList<IncompleteStudent>();
	private ArrayList<PrettyStudentPrint> prettyPrint = new ArrayList<PrettyStudentPrint>();
	private BlobKey blobKey;
	//private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
    	RequestDispatcher rd = request.getRequestDispatcher("Error");
    	rd.forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
    	response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=datafile.csv");
		
    	try {
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iterator = upload.getItemIterator(request);
			
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				if(!item.isFormField())
					read(stream);
			}
			
			GoogleDrive drive = new GoogleDrive();
			drive.setCode(request.getParameter("code"));
			
			///*
			PrintWriter out = response.getWriter();
			
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
	        
	        //*/
	        
	        /*
	         * Call main method
	         */
			this.OrganizeFiles(drive, out, studentList);
			
			//this.OrganizeFiles(drive, studentList);
			
			byte[] bytes = this.printArray(this.prettyPrint);
			
			BlobstoreService bstore = this.saveCSVFile(bytes);
			if(bstore != null)
				bstore.serve(this.blobKey, response);
			else{
				RequestDispatcher rd = request.getRequestDispatcher("Error");
    			rd.forward(request, response);
			}
			
			///*
			out.println("</div>");
	        
	        out.println("</body>");
	        out.println("</html>");
			//*/
			
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
        
    }
    
    public BlobstoreService saveCSVFile(byte[] bytes){
    	try{
	    	// Get a file service
	    	FileService fileService = FileServiceFactory.getFileService();
	
	    	// Create a new Blob file with mime-type "text/plain"
	    	AppEngineFile file = fileService.createNewBlobFile("text/csv");
	
	    	// Open a channel to write to it
	    	boolean lock = false;
	    	FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
	
	    	// Different standard Java ways of writing to the channel
	    	// are possible. Here we use a PrintWriter:
	    	PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
	    	out.print(bytes);
	
	    	// Close, finalize and save the file path for writing later
	    	out.close();
	    	writeChannel.closeFinally();
	    	
	    	/*
	    	String path = file.getFullPath();
	
	    	// Write more to the file in a separate request:
	    	file = new AppEngineFile(path);
	
	    	// This time lock because we intend to finalize
	    	lock = true;
	    	writeChannel = fileService.openWriteChannel(file, lock);
	
	    	// This time we write to the channel directly
	    	writeChannel.write(ByteBuffer.wrap("And miles to go before I sleep.".getBytes()));
	
	    	// Now finalize
	    	writeChannel.closeFinally();
			*/
	    	
	    	
	    	/*
	    	// Later, read from the file using the Files API
	    	lock = false; // Let other people read at the same time
	    	FileReadChannel readChannel = fileService.openReadChannel(file, false);
	
	    	// Again, different standard Java ways of reading from the channel.
	    	BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
	    	String line = reader.readLine();
	    	// line = "The woods are lovely dark and deep."
	
	    	readChannel.close();
			*/
	    	
	    	
	    	// Now read from the file using the Blobstore API
	    	this.blobKey = fileService.getBlobKey(file);
	    	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	    	return blobstoreService;
	    	//blobstoreService.serve(blobKey, response);
	    	/*
	    	BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
	    	String segment = new String(blobStoreService.fetchData(blobKey, 30, 40));
	    	*/
    	}
    	catch(Exception e){
    		e.getStackTrace();
    		return null;
    	}
    }
    
    public void read(InputStream inputFile) throws IOException {
		try {
			Workbook w;
			
			w = Workbook.getWorkbook(inputFile);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			// Loop over first 10 column and lines
			//GlobalSingleton.getInstance().setWorkbook(w);
			
			for(int row = 1; row <= sheet.getRows(); row++){
				IncompleteStudent student = new IncompleteStudent();
				
				for(int col = 0; col < sheet.getColumns(); col++){
					Cell identifier = sheet.getCell(col, 0);
					String strIdentifier = identifier.getContents();
					
					if (strIdentifier.toLowerCase().contains("first") && strIdentifier.toLowerCase().contains("name")) {
						Cell cell = sheet.getCell(col, row);
						student.setFirstName(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("last") && strIdentifier.toLowerCase().contains("name")) {
						Cell cell = sheet.getCell(col, row);
						student.setLastName(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("date") && strIdentifier.toLowerCase().contains("birth")) {
						Cell cell = sheet.getCell(col, row);
						student.setDateOfBirth(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("id")) {
						Cell cell = sheet.getCell(col, row);
						student.setId(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("checklist")) {
						Cell cell = sheet.getCell(col, row);
						student.setChecklist(cell.getContents());
					}
					else if (strIdentifier.toLowerCase().contains("term")) {
						Cell cell = sheet.getCell(col, row);
						student.setTerm(cell.getContents());
					}
				}
				studentList.add(student);
			}
			//GlobalSingleton.getInstance().setStudentList(studentList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void OrganizeFiles(GoogleDrive drive, PrintWriter out, ArrayList<IncompleteStudent> incompletestudents){
    //public void OrganizeFiles(GoogleDrive drive, ArrayList<IncompleteStudent> incompletestudents){
        try {

            ArrayList<File> googleDriveFolders = drive.getAllFolders();
            int counter = 0;
            File autoFolder = drive.getCreateFolder(googleDriveFolders,"AUTO");
            
            for (IncompleteStudent student : incompletestudents) {
                PrettyStudentPrint psp = new PrettyStudentPrint(student.getLastName() + ", " + student.getFirstName() + ", " + student.getId());

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
                                
                                psp.setChecklistItem("TSTS", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TSTS", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("TSTS", "not");
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
                                    
                                    psp.setChecklistItem(codeItem, "found");
                                    
                                    for (File file : tempFiles) {
                                        drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                    }
                                } else
                                    psp.setChecklistItem(codeItem, "not");
                            }

                            if (checklistitem.contains("act")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "scores", "act"});
                                if (!tempFiles.isEmpty()) {
                                    
                                    psp.setChecklistItem("TSTS", "found");
                                    
                                    for (File file : tempFiles) {
                                        drive.MoveFiles(file, studentFolder, student, "TSTS", checklistitem);
                                    }
                                } else
                                    psp.setChecklistItem("TSTS", "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
                        }
                        if (checklistitem.contains("waiver") && checklistitem.contains("application")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "waiver", "application"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("APW", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "APW", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("APW", "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
                        }
                        if (checklistitem.contains("transcript")) {
                            codeItem = "";
                            if (checklistitem.contains("high") && checklistitem.contains("school")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "high", "school", "transcript"});
                                //tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "high", "school"});
                                codeItem = "HST";
                            } else if (checklistitem.contains("official") && checklistitem.contains("college")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "college", "transcript"});
                                //tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "college"});
                                codeItem = "CLT";
                            } else if (checklistitem.contains("unofficial")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "unofficial", "transcript"});
                                //tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "unofficial"});
                                codeItem = "UNO";
                            } else if (checklistitem.contains("evaluation")) {
                                tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "evaluation", "transcript"});
                                //tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "evaluation"});
                                codeItem = "TRNE";
                            }
                            if (!tempFiles.isEmpty()) {
                                if (codeItem.equals("TRNE")) {
                                    
                                    psp.setChecklistItem(codeItem, "found");
                                }
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
                        }
                        if (checklistitem.contains("affidavit") && checklistitem.contains("support")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "affidavit", "support"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("AOS", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "AOS", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("AOS", "not");
                        }
                        if (checklistitem.contains("essay") && checklistitem.contains("personal")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "essay", "personal"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("ESSY", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "ESSY", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("ESSY", "not");
                        }
                        if (checklistitem.contains("advanced") && checklistitem.contains("placement") && checklistitem.contains("board")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "advanced", "affidavit", "support"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("AP", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "AP", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("AP", "not");
                        }
                        if (checklistitem.contains("civilian") && checklistitem.contains("millitary") && checklistitem.contains("person")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "civilian", "millitary", "person"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("BRAC", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "BRAC", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("BRAC", "not");
                        }
                        if (checklistitem.contains("art") && checklistitem.contains("portfolio")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "art", "portfolio"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("ARTP", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "ARTP", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("ARTP", "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
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
                                
                                psp.setChecklistItem(codeItem, "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            } else
                                psp.setChecklistItem(codeItem, "not");
                        }
                        if (checklistitem.contains("deferred") && checklistitem.contains("action") && checklistitem.contains("childhood")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "deferred", "action", "childhood"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("DACA", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "DACA", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("DACA", "not");
                        }
                        if (checklistitem.contains("employment") && checklistitem.contains("authorizaation")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "employment", "authorization"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("EAC", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "EAC", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("EAC", "not");
                        }
                        if (checklistitem.contains("confirmation") && checklistitem.contains("incentive")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "confirmation", "incentive"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("IEG", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "IEG", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("IEG", "not");
                        }
                        if (checklistitem.contains("graduate") && checklistitem.contains("diploma") && checklistitem.contains("equivalency")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "graduate", "diploma", "equivalency"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("GED", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "GED", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("GED", "not");
                        }
                        if (checklistitem.contains("bank") && checklistitem.contains("statement")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "bank", "statement"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("BS", "found");
                                
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "BS", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("BS", "not");
                        }
                        if (checklistitem.contains("cambridge") && checklistitem.contains("proficiency") && checklistitem.contains("test")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "cambridge", "proficiency", "test"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("CPE", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "CPE", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("CPE", "not");
                        }
                        if (checklistitem.contains("i-20") && checklistitem.contains("student") && checklistitem.contains("visa")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "i-20", "student", "visa"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("F1", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "F1", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("F1", "not");
                        }
                        if (checklistitem.contains("dream") && checklistitem.contains("act")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "dream", "act"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("I797", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "I797", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("I797", "not");
                        }
                        if (checklistitem.contains("national") && checklistitem.contains("external") && checklistitem.contains("diploma")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "national", "external", "diploma"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("NEDP", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "NEDP", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("NEDP", "not");
                        }
                        if (checklistitem.contains("paternal") && checklistitem.contains("consent")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "consent", "paternal"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("PAC", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "PAC", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("PAC", "not");
                        }
                        if (checklistitem.contains("midyear") || checklistitem.contains("mid year") || checklistitem.contains("mid-year") && checklistitem.contains("grade")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "grade", "mid-year"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("MIDY", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "MIDY", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("MIDY", "not");
                        }
                        if (checklistitem.contains("military") && checklistitem.contains("orders")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "military", "orders"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("MO", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "MO", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("MO", "not");
                        }
                        if (checklistitem.contains("court") && checklistitem.contains("order")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "court", "order"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("COOR", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "COOR", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("COOR", "not");
                        }
                        if (checklistitem.contains("resume")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "resume"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("RESU", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "RESU", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("RESU", "not");
                        }
                        if (checklistitem.contains("residence") && checklistitem.contains("verification")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "residence", "verification"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("RSV", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "RSV", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("RSV", "not");
                        }
                        if (checklistitem.contains("world") && checklistitem.contains("education") || checklistitem.contains("educ")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "world", "education"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("WES1", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "WES1", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("WES1", "not");
                        } 
                        if (checklistitem.contains("toefl")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "toefl"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("TOEFL", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TOEFL", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("TOEFL", "not");
                        }
                        if (checklistitem.contains("tax") && checklistitem.contains("return") && checklistitem.contains("personal")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "tax", "return", "personal"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("TAXP", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TAXP", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("TAXP", "not");
                        }
                        if (checklistitem.contains("test") && checklistitem.contains("spoken") && checklistitem.contains("english")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "test", "spoken", "english"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("TSE", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TSE", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("TSE", "not");
                        }
                        if (checklistitem.contains("social") && checklistitem.contains("security")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "security", "social"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("SS", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "SS", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("SS", "not");
                        }
                        if (checklistitem.contains("tax") && checklistitem.contains("return") && checklistitem.contains("personal")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "tax", "return", "personal"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("TAXP", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "TAXP", checklistitem);
                                }
                            } else 
                                psp.setChecklistItem("TAXP", "not");
                        }
                        if (checklistitem.contains("i-551") && checklistitem.contains("permanent") || checklistitem.contains("perm") && checklistitem.contains("residence")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "residence", "permanent", "i-551"});
                            if (!tempFiles.isEmpty()) {
                                
                                psp.setChecklistItem("PRC", "found");
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "PRC", checklistitem);
                                }
                            } else
                                psp.setChecklistItem("PRC", "not");
                        }
                        if (checklistitem.contains("official") && checklistitem.contains("exam")) {
                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "sssce"});
                            if (!tempFiles.isEmpty()) {
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, "OFEX", checklistitem);
                                }
                            }

                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "sce"});
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "waec"});
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "cxc"});
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            tempFiles = drive.getStudentFiles(new String[]{student.getLastName(), student.getFirstName(), student.getId(), "official", "exam", "gde"});
                            if (!tempFiles.isEmpty()) {
                                codeItem = "OFEX";
                                for (File file : tempFiles) {
                                    drive.MoveFiles(file, studentFolder, student, codeItem, checklistitem);
                                }
                            }

                            if (!codeItem.equals("")) {
                                psp.setChecklistItem(codeItem, "found");
                            } else if (codeItem.equals("")) {
                                psp.setChecklistItem(codeItem, "not");
                            }
                        }
                    }

                }
                this.prettyPrint.add(psp);
                drive.MoveFiles(studentFolder, autoFolder);
            }
            
            out.println("<li><h3>All students processed. Total of students: " + counter + "</h3></li>");
        } catch (Exception ex) {
            Logger.getLogger(UploadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public byte[] printArray(ArrayList<PrettyStudentPrint> pspArray) throws IOException{
    	StringBuffer buffer = new StringBuffer();
    	String[] tags = {"TSTS", "S05", "SAT", "S01", "S02", "IE11", "IE37", "IE75", "IEW", "IEX", "APO", "APH", "AP25", "APW", "AUD2", "AUDE", "LRE2", "LRE1", "SSC", "COBC", "COMC", "FC", "CON", "CER", "HST", "CLT", "UNO", "TRNE", "D214", "RESP", "ASG", "TREL", "AOS", "ESSY", "AP", "BRAC", "ARTP", "COMT", "MAD", "IEP", "ECE1", "MDHR", "REF3", "ISA", "IELT", "SUPP", "GCEA", "GCEO", "CLEP", "PASS", "COPS", "GRR", "GRRP", "ETR", "ESL", "DEPA", "DEPD", "DACA", "EAC", "IEG", "GED", "BS", "CPE", "F1", "I797", "NEDP", "PAC", "MIDY", "MO", "COOR", "RESU", "RSV", "WES1", "TOEFL", "TAXP", "TSE", "SS", "TAXP", "PRC", "OFEX"};
    	
    	
    	buffer.append("Last Name, First Name, Morgan ID, TSTS, S05, SAT, S01, S02, IE11, IE37, IE75, " + 
                "IEW, IEX, APO, APH, AP25, APW, AUD2, AUDE, LRE2, LRE1, SSC, COBC, COMC, FC, CON, " +
                "CER, HST, CLT, UNO, TRNE, D214, RESP, ASG, TREL, AOS, ESSY, AP, BRAC, ARTP, " +
                "COMT, MAD, IEP, ECE1, MDHR, REF3, ISA, IELT, SUPP, GCEA, GCEO, CLEP, PASS, COPS, " +
                "GRR, GRRP, ETR, ESL, DEPA, DEPD, DACA, EAC, IEG, GED, BS, CPE, F1, I797, NEDP, PAC, " +
                "MIDY, MO, COOR, RESU, RSV, WES1, TOEFL, TAXP, TSE, SS, TAXP, PRC, OFEX\n");
        
        for(PrettyStudentPrint psp: pspArray){
            buffer.append(psp.getStudentInfo());
            for(String tag : tags){
                if(psp.getChecklist().containsKey(tag))
                	buffer.append(", " + psp.getChecklist().get(tag));
                else
                	buffer.append(",");
            }
            buffer.append("\n");
        }
        
        byte[] bytes = buffer.toString().getBytes();

        return bytes;
    }
    
}
