package com.hkmci.ffmpeg;

import java.awt.image.BufferedImage;

import com.xuggle.mediatool.*;
import com.xuggle.xuggler.*;

public class TimeStampTool extends MediaToolAdapter {
	/**
	 * Take a file and display stream info
	 * @param filename
	 */
	public static void main(String[] args) {
		if (args.length!=1)
			throw new IllegalArgumentException("no file.");
		IContainer container = IContainer.make();
		
		if (container.open(args[0], IContainer.Type.READ, null)<0){
			throw new IllegalArgumentException("cannot open that file.");
		}
		
		int numStreams = container.getNumStreams();
		System.out.printf("num streams: %d\n", numStreams);
		
		for (int i=0; i<numStreams;i++){
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();
			
			System.out.printf("stream %d is type %s\n", i+1, coder.getCodecType());
			
		}
		
		IMediaReader reader = ToolFactory.makeReader(args[0]);
		IMediaWriter writer = ToolFactory.makeWriter("C:\\Projects\\output.mp4", reader);
		
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		
		TimeStampTool tsTool = new TimeStampTool();
		
		reader.addListener(tsTool);
		tsTool.addListener(writer);
		
	//	IMediaDebugListener debug = ToolFactory.makeDebugListener(Event.META_DATA);
	//	writer.addListener(debug);
		
	//	IMediaViewer viewer = ToolFactory.makeViewer();
	//	reader.addListener(viewer);
		
		while (reader.readPacket() == null);
		
		

	}
}
