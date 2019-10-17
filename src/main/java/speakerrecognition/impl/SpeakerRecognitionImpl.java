package speakerrecognition.impl;

import java.io.IOException;
import java.util.List;

import speakerrecognition.SpeakerRecognition;
import speakerrecognition.file.WavFileData;
import speakerrecognition.file.WavFileService;
import speakerrecognition.math.MatrixException;

public class SpeakerRecognitionImpl implements SpeakerRecognition {

	public String recognize(List<SpeakerData> speakerModels, String resourceSoundSpeechFilePath) throws IOException, MatrixException {
		double finalScore = Long.MIN_VALUE;
		String bestFittingModelName = "";
		for(SpeakerData model : speakerModels){
			WavFileService wavFileService1 = new WavFileService();
			WavFileData fileData = wavFileService1.open(resourceSoundSpeechFilePath);
			int[] x3 = fileData.samples;
			int fs3 = fileData.fs;
			MFCC mfcc3 = new MFCC(x3, fs3);
			double[][] speaker_mfcc3 = mfcc3.getMFCC();
			double scoreForTest1 = SpeakerService.getScore(model, speaker_mfcc3);
			if(scoreForTest1 > finalScore){
				finalScore = scoreForTest1;
				bestFittingModelName = model.name;
			}
			
		}
			String recogResult = "Test speech from file "+resourceSoundSpeechFilePath + " is most similar to model "+ bestFittingModelName;
		return recogResult;
	}

}
