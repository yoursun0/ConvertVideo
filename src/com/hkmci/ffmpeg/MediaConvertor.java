package com.hkmci.ffmpeg;

import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.*;

public class MediaConvertor {
	private static final Integer WIDTH = 640;
	private static final Integer HEIGHT = 360;
	
	private static final Integer WIDTH4 = 854;
	private static final Integer HEIGHT4 = 480;
	
    private static String INPUT_FILE = "4.wmv";
	
    private static String SERVER_PATH = "C:/programs/apache-tomcat/webapps/opencms/";
    
    private static String FINAL_PATH = "//192.168.11.151/public/PruChannel/";
    
    private static String VIDEO_SERVER_URL = "hkguy.asia";
    
    private static String VIDEO_SERVER_LOGIN = "hkguyasi";
    
    private static String VIDEO_SERVER_PASSWORD = "j487F513";
    /*
	public MediaConvertor(String fileName, String cmsPath, String destinationPath) {
		INPUT_FILE = fileName;
		SERVER_PATH = cmsPath;
		FINAL_PATH = destinationPath;
	}
	
	public void generateHTML(String mp4Str, String oggStr) throws IOException {
        File f = new File("C:/Projects/Prudential/video.html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("<html>");
        bw.write("<head><title>HTML5 Video</title></head>");
        bw.write("<body>");
        bw.write("<h1>HTML5 video convertion</h1>");

        bw.write("<video width='640' height='360' controls='controls'>");
        bw.write("<source src='" + mp4Str + "' type='video/mp4'>");
        bw.write("<source src='" + oggStr + "' type='video/ogg'>");
        bw.write("</video>");

        bw.write("</body>");
        bw.write("</html>");

        bw.close();

        Desktop.getDesktop().browse(f.toURI());
    }
	*/
	public static class VideoThread implements Runnable {
		   private String serverpath;
		   private String finalpath;
		   private String inputfile;
		   public VideoThread(String InputPath, String OutputPath, String FileName) {
			   serverpath = InputPath;
			   finalpath = OutputPath;
			   inputfile = FileName;
		   }
		   
		   public void movefile(String inputpath, String outputpath, String filename) {
				// TODO Auto-generated method stub
			   
				try {
					File srcFile = new File(inputpath+filename);
					if (!srcFile.exists())
					      throw new IllegalArgumentException("Fail to move file - no such file exist: " + (inputpath+filename));
					
					FTPClient client = new FTPClient();
					FileInputStream fis = null;

					try {
						System.out.println("Connect to the video streaming server: " + VIDEO_SERVER_URL);
					    client.connect(VIDEO_SERVER_URL);
					    client.login(VIDEO_SERVER_LOGIN, VIDEO_SERVER_PASSWORD);

					    //
					    // Store file to server
					    //
					    System.out.println("Start transferring file '" + filename+ "' to the video streaming server.");
					    client.storeFile(outputpath+filename, fis);
					    client.logout();
					} catch (IOException e) {
						System.out.println("Unexpected system error occurs during video transferr.");
					    e.printStackTrace();
					} finally {
					    try {
					        if (fis != null) {
					            fis.close();
					        }
					        client.disconnect();
					        System.out.println("Finish transferring file '" + filename+ "' to the video streaming server.");
					    } catch (IOException e) {
					    	System.out.println("Unexpected system error occurs during disconnecting the connection.");
					        e.printStackTrace();
					    }
					}
					
					System.out.println("Successfully transferr file " + filename + " from " + inputpath +" to " + outputpath);
					
					this.deletefile(inputpath+filename);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			public void deletefile(String filename) {
				// TODO Auto-generated method stub
				try {
					File srcFile = new File(filename);
					
					// Make sure the file exists and isn't write protected
					if (!srcFile.exists()) 
						throw new IllegalArgumentException("Fail to delete - no such file exist: " + filename);
					if (!srcFile.canWrite())
						throw new IllegalArgumentException("Fail to delete - write protected: "+ filename);
					
					// Attempt to delete it
					boolean success = srcFile.delete();
					if (!success) {
						throw new IllegalArgumentException("Delete: deletion failed");
					}else{
						System.out.println("Remove temp file: "+filename);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			public void convert(String inputpath, String outputpath, String filename) {
				
				String inputfile = inputpath + filename;
				int lastDot = filename.lastIndexOf(".");
				String outfile = filename.substring(0, lastDot);

				// Convert the source video to .mp4 format
				String outpath = outputpath + outfile + ".mp4";

				MyVideoListener myVideoListener = new MyVideoListener(WIDTH4, HEIGHT4);
				Resizer resizer = new Resizer(WIDTH4, HEIGHT4);
		 
				// reader
				IMediaReader reader = ToolFactory.makeReader(inputfile);
				reader.addListener(resizer);

				// writer
				IMediaWriter writer = ToolFactory.makeWriter(outpath, reader);
				resizer.addListener(writer);
				writer.addListener(myVideoListener);

				System.out.printf("Converting Video %s to %s ...\n", filename, outfile + ".mp4");

				// show video when encoding
				//reader.addListener(ToolFactory.makeViewer(false));
		 
				while (reader.readPacket() == null) { 
					// continue coding
				}

				System.out.println("MP4 conversion finish!");
				
				// Convert the source video to .ogg format
				outpath = outputpath + outfile + ".ogg";

				MyVideoListener myVideoListener2 = new MyVideoListener(WIDTH, HEIGHT);
				Resizer resizer2 = new Resizer(WIDTH, HEIGHT);
		 
				// reader
				IMediaReader reader2 = ToolFactory.makeReader(inputfile);
				reader2.addListener(resizer2);

				// writer
				IMediaWriter writer2 = ToolFactory.makeWriter(outpath, reader2);
				resizer2.addListener(writer2);
				writer2.addListener(myVideoListener2);

				System.out.printf("Converting Video %s to %s ...\n", filename, outfile + ".ogg");

				// show video when encoding
				//reader.addListener(ToolFactory.makeViewer(false));
		 
				while (reader2.readPacket() == null) { 
					// continue coding
				}

				System.out.println("Video conversion finish!");
		    }
			
		   public void run() {
			    String srcpath= this.serverpath + "video/";
		        String temppath= srcpath + "output/";
			    int lastDot = inputfile.lastIndexOf(".");
	    		String outfile = inputfile.substring(0, lastDot);
	    		
	    		this.convert(srcpath,temppath,inputfile);
	    		this.movefile(temppath,finalpath,outfile+".mp4");
	    		this.movefile(temppath,finalpath,outfile+".ogg");
	    		this.deletefile(srcpath+inputfile);
	    		
	    		System.out.printf("Finish converting video %s to %s and %s. Video conversion is completed.\n", inputfile, outfile+".mp4",outfile+".ogg");
		   }
		}
	
	public boolean startConversion(){
		try{
			System.out.println("Start the video conversion thread.");
			System.out.println("Temp directory = "+System.getProperty("java.io.tmpdir"));
			Runnable r = new VideoThread(INPUT_FILE,SERVER_PATH,FINAL_PATH);
			Thread thread1 = new Thread(r);
			thread1.start();
			System.out.println("OK, the program is end, the video conversion continue.");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
	
		String INPUT_FILE2 = "Demo.webm";
		
	    String SERVER_PATH2 = "C:/Users/Helic/Google Drive/PostSales/Custom_FRT/FRT/FRT_files/";
	    
	    String FINAL_PATH2 = "C:/Users/Helic/Google Drive/PostSales/Custom_FRT/FRT/FRT_files/";
	    
		try{
			System.out.println("Start the video conversion thread.");
			Runnable r = new VideoThread(SERVER_PATH2,FINAL_PATH2,INPUT_FILE2);
			Thread thread1 = new Thread(r);
			thread1.start();
			System.out.println("OK, the program is end, the video conversion continue.");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		/* 
		 * ffmpeg -i Prudential.wmv -flags:v qscale -global_quality:v "10*QP2LAMBDA" -codec:v libtheora -codec:a libvorbis -q:a 5
		 * 
		 * MP4:
		 * 
		 * Baseline Quality (4.69MB):
		 * ffmpeg -i Prudential.wmv -vcodec libx264 -vprofile main -preset slow -b:v 250k -maxrate 250k -bufsize 500k -vf scale=640:360 -threads 0 -acodec libvo_aacenc -ab 96k output_baseline.mp4
		 * 
		 * Standard Quality (7.10MB):
		 * ffmpeg -i Prudential.wmv -vcodec libx264 -vprofile main -preset slow -b:v 400k -maxrate 400k -bufsize 800k -vf scale=640:360 -threads 0 -acodec libvo_aacenc -ab 128k output_standard.mp4
		 * 
		 * OGG:
		 * 
		 * Baseline Quality (5.20MB):
		 * ffmpeg -i Prudential.wmv -codec:v libtheora -quality good -cpu-used 0 -b:v 250k -maxrate 250k -bufsize 500k -qmin 10 -qmax 42 -vf scale=640:360 -threads 4 -codec:a libvorbis -b:a 128k output_baseline.ogg
		 * 
		 * Standard Quality (10.00MB):
		 * ffmpeg -i Prudential.wmv -codec:v libtheora -quality good -cpu-used 0 -b:v 600k -maxrate 600k -bufsize 1200k -qmin 10 -qmax 42 -vf scale=640:360 -threads 4 -codec:a libvorbis -b:a 128k output_standard.ogg
		 */
	}
}