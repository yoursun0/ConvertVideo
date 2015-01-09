package com.hkmci.ffmpeg;

import java.awt.image.BufferedImage;
import java.io.*;

import com.xuggle.mediatool.*;
import com.xuggle.xuggler.*;

public class ConvertVideo extends MediaToolAdapter {
	/**
	 * Take a file and display stream info
	 * @param filename
	 * @throws IOException 
	 */
	
	public static void generateHTML(String mp4Str, String oggStr) throws IOException {
        File f = new File("video.html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write("<html>");
        bw.write("<head><title>HTML5 Video</title></head>");
        bw.write("<body>");
        bw.write("<h1>HTML5 video convertion</h1>");

        bw.write("<video width='320' height='240' controls='controls'>");
        bw.write("<source src='" + mp4Str + "' type='video/mp4'>");
        bw.write("<source src='" + oggStr + "' type='video/ogg'>");
        bw.write("</video>");

        bw.write("</body>");
        bw.write("</html>");

        bw.close();

    }
	
	public static void main(String[] args) {
		if (args.length!=2)
			throw new IllegalArgumentException("no input or output file.");
		IContainer container = IContainer.make();
		int retval = 0;

/*
        IMetaData parameters = IMetaData.make();
        
        parameters.setValue("flags:v", "qscale");
        parameters.setValue("global_quality:v", "10*QP2LAMBDA");
        parameters.setValue("codec:v", "libtheora");
        parameters.setValue("codec:a", "libvorbis");
        parameters.setValue("q:a", "5");
        
        IMetaData rejectParameters = IMetaData.make();

        retval = container.open(args[0], IContainer.Type.READ, null, false, true, 
                parameters, rejectParameters);
        if (retval < 0)
            throw new RuntimeException("could not open url: " + args[0]);
        if (rejectParameters.getNumKeys() > 0)
            throw new RuntimeException("some parameters were rejected: " + rejectParameters);
		*/
		int numStreams = container.getNumStreams();
		System.out.printf("num streams: %d\n", numStreams);
		
		for (int i=0; i<numStreams;i++){
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			
			System.out.printf("stream %d is type %s\n", i+1, coder.getCodecType());
			
		}
		
		String mp4Str = args[1]+".webm";
		String oggStr = args[1]+".mp4";
		/*
		IMediaReader reader1 = ToolFactory.makeReader(args[0]);
		IMediaWriter writer1 = ToolFactory.makeWriter(mp4Str, reader1);
		
		reader1.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		reader1.addListener(writer1);
		
		while (reader1.readPacket() == null);
		*/
		IMediaReader reader2 = ToolFactory.makeReader(args[0]);
		IMediaWriter writer2 = ToolFactory.makeWriter(oggStr, reader2);
		
		reader2.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		reader2.addListener(writer2);
		
		while (reader2.readPacket() == null);
		
		try {
			generateHTML(mp4Str,oggStr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
