package speakerrecognition.logger;

import speakerrecognition.math.MatrixException;
import speakerrecognition.speaker.SpeakerData;

import java.io.IOException;
import java.util.List;

public interface SpeakerRecognitionLogger {
    void print(List<SpeakerData> speakerModels, String resourceSoundSpeechFilePath) throws IOException, MatrixException;
}
