package speakerrecognition.impl;

import speakerrecognition.SpeakerRecognitionLogger;
import speakerrecognition.file.WavFileData;
import speakerrecognition.file.WavFileService;
import speakerrecognition.math.MatrixException;

import java.io.IOException;
import java.util.List;

public class SpeakerRecognitionLoggerImpl implements SpeakerRecognitionLogger {

    public void print(List<SpeakerData> speakerModels, String resourceSoundSpeechFilePath)
            throws IOException, MatrixException {
        double finalScore = Long.MIN_VALUE;
        String bestFittingModelName = "";
        for(SpeakerData model : speakerModels){
            WavFileService wavFileService1 = new WavFileService();
            WavFileData wavFileData = wavFileService1.open(resourceSoundSpeechFilePath);
            int[] x3 = wavFileData.samples;
            int fs3 = wavFileData.fs;
            MFCC mfcc3 = new MFCC(x3, fs3);
            double[][] speaker_mfcc3 = mfcc3.getMFCC();
            double scoreForTest1 = SpeakerService.getScore(model, speaker_mfcc3);
            System.out.println("Test speech from file "+resourceSoundSpeechFilePath + " is similar to model "+ model.name +" with log probability "+scoreForTest1);

        }

    }
}
