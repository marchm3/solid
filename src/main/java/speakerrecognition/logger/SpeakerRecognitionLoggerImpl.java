package speakerrecognition.logger;

import speakerrecognition.speaker.SpeakerData;
import speakerrecognition.speaker.SpeakerService;
import speakerrecognition.file.WavFileData;
import speakerrecognition.file.WavFileService;
import speakerrecognition.math.MatrixException;
import speakerrecognition.mfcc.MFCCService;

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
            MFCCService mfccService3 = new MFCCService();
            double[][] speaker_mfcc3 = mfccService3.getMFCC(x3, fs3);
            double scoreForTest1 = SpeakerService.getScore(model, speaker_mfcc3);
            System.out.println("Test speech from file "+resourceSoundSpeechFilePath + " is similar to model "+ model.name +" with log probability "+scoreForTest1);

        }

    }
}
