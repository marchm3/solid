package speakerrecognition;

import speakerrecognition.math.MatrixException;
import speakerrecognition.impl.SpeakerData;

import java.io.IOException;
import java.util.List;

public interface SpeakerRecognitionLogger {
    void print(List<SpeakerData> speakerModels, String resourceSoundSpeechFilePath) throws IOException, MatrixException;
}
