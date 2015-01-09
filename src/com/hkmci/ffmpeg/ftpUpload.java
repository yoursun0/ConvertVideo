package com.hkmci.ffmpeg;

import java.io.*;
import java.net.*;
import org.apache.commons.net.*;
import org.apache.commons.net.ftp.*;
import org.apache.commons.net.io.*;
import org.apache.commons.net.util.*;

public class ftpUpload {
	public static void main(String[] args) {

		FTPClient client = new FTPClient();
		FileInputStream fis = null;

		try {
		    client.connect("hkgdxwas01");
		    client.login("wasadmin", "abcd4567");

		    //
		    // Create an InputStream of the file to be uploaded
		    //
		    String filename = "aa.txt";
		    String filepath = "C:\\workspace\\";
		    fis = new FileInputStream(filepath+filename);

		    //
		    // Store file to server
		    //
		    client.storeFile("/"+filename, fis);
		    client.logout();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (fis != null) {
		            fis.close();
		        }
		        client.disconnect();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}

	}

}
