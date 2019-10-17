package speakerrecognition.recognition;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import speakerrecognition.logger.SpeakerRecognitionLogger;
import speakerrecognition.file.WavFileData;
import speakerrecognition.file.WavFileService;
import speakerrecognition.logger.SpeakerRecognitionLoggerImpl;
import speakerrecognition.math.MatrixException;
import speakerrecognition.mfcc.MFCCService;
import speakerrecognition.speaker.SpeakerData;


public class TestClass {
	
	private SpeakerRecognition speakerRecognition = new SpeakerRecognitionImpl();
	private SpeakerRecognitionLogger speakerRecognitionLogger = new SpeakerRecognitionLoggerImpl();
	
	@Test
	public void testCase() throws IOException, ClassNotFoundException, MatrixException {
		
		//given
		MFCCService mfccService = new MFCCService();
		WavFileService wavFileService = new WavFileService();

		WavFileData wavFileData = wavFileService.open("src/test/resources/training/speaker1_2.WAV");
		int[] x = wavFileData.samples;
		int fs = wavFileData.fs;

		double[][] speaker_mfcc = mfccService.getMFCC(x, fs);
		GMM gmm = new GMM(speaker_mfcc, 32);
		gmm.fit();
		SpeakerData sd1 = new SpeakerData(gmm.get_means(), gmm.get_covars(), gmm.get_weights(), "speaker1model");

		WavFileData wavFileData2 = wavFileService.open("src/test/resources/training/speaker2_2.WAV");
		int[] x2 = wavFileData2.samples;
		int fs2 = wavFileData2.fs;
		double[][] speaker_mfcc2 = mfccService.getMFCC(x2, fs2);
		GMM gmm2 = new GMM(speaker_mfcc2, 32);
		gmm2.fit();
		SpeakerData sd2 = new SpeakerData(gmm2.get_means(), gmm2.get_covars(), gmm2.get_weights(), "speaker2model");
		
		List<SpeakerData> speakerModels = Arrays.asList(sd1, sd2);
		
		//when
		
		System.out.println(speakerRecognition.recognize(speakerModels, "src/test/resources/test/speaker1_1.WAV"));
		System.out.println(speakerRecognition.recognize(speakerModels, "src/test/resources/test/speaker2_1.WAV"));


		speakerRecognitionLogger.print(speakerModels, "src/test/resources/test/speaker1_1.WAV");
		speakerRecognitionLogger.print(speakerModels, "src/test/resources/test/speaker2_1.WAV");
		
		//then

	}
	
}
