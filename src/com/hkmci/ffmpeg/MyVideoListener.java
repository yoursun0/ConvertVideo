package com.hkmci.ffmpeg;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
 
public class MyVideoListener extends MediaToolAdapter {
	private Integer width;
	private Integer height;
 
	public MyVideoListener(Integer aWidth, Integer aHeight) {
		this.width = aWidth;
		this.height = aHeight;
	}
 
	@Override
	public void onAddStream(IAddStreamEvent event) {
		int streamIndex = event.getStreamIndex();
		IStreamCoder streamCoder = event.getSource().getContainer().getStream(streamIndex).getStreamCoder();
		if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
			streamCoder.setCodec(ICodec.ID.CODEC_ID_VORBIS); 
			event.getSource().getContainer().setForcedAudioCodec(ICodec.ID.CODEC_ID_VORBIS);
			streamCoder.setBitRate(128000); 
		} else if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
			streamCoder.setWidth(width);
			streamCoder.setHeight(height);
			streamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
			streamCoder.setGlobalQuality(width);

			// streamCoder.setBitRate(200000);
			// streamCoder.setTimeBase(IRational.make(1,25));
			/*
			 * -flags:v qscale -global_quality:v "10*QP2LAMBDA" -codec:v libtheora -codec:a libvorbis -q:a 5
			 * 
			 */
		}
		super.onAddStream(event);
	}
 
}