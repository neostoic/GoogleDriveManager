package edu.morgan;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Workbook;
import edu.morgan.google.GoogleDrive;
import edu.morgan.student.IncompleteStudent;;

public class GlobalSingleton {
	private static GlobalSingleton instance = null;
	private GoogleDrive drive = null;
	private ArrayList<IncompleteStudent> studentList = new ArrayList<IncompleteStudent>();
	private ArrayList<InputStream> streamList = new ArrayList<InputStream>();
	private Workbook workbook;
	
	public ArrayList<InputStream> getStreamList(){
		return this.streamList;
	}
	
	public void addStream(InputStream in){
		this.streamList.add(in);
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	private GlobalSingleton(){}
	
	public static GlobalSingleton getInstance(){
		if(instance == null)
			instance = new GlobalSingleton();
		return instance;
	}
	
	public GoogleDrive getDrive() {
		return drive;
	}
	
	public void setDrive(GoogleDrive drive) {
		this.drive = drive;
	}
	
	public ArrayList<IncompleteStudent> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<IncompleteStudent> studentList) {
		this.studentList = studentList;
	}
	
}
