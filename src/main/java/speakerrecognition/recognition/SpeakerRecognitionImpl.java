package speakerrecognition.recognition;

import java.io.IOException;
import java.util.List;

import speakerrecognition.file.WavFileData;
import speakerrecognition.file.WavFileService;
import speakerrecognition.math.MatrixException;
import speakerrecognition.mfcc.MFCCService;
import speakerrecognition.speaker.SpeakerData;
import speakerrecognition.speaker.SpeakerService;

public class SpeakerRecognitionImpl implements SpeakerRecognition {

	public String recognize(List<SpeakerData> speakerModels, String resourceSoundSpeechFilePath) throws IOException, MatrixException {
		double finalScore = Long.MIN_VALUE;
		String bestFittingModelName = "";
		for(SpeakerData model : speakerModels){
			WavFileService wavFileService1 = new WavFileService();
			WavFileData fileData = wavFileService1.open(resourceSoundSpeechFilePath);
			int[] x3 = fileData.samples;
			int fs3 = fileData.fs;
			MFCCService mfccService3 = new MFCCService();
			double[][] speaker_mfcc3 = mfccService3.getMFCC(x3, fs3);
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
