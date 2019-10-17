package speakerrecognition.recognition;

import java.io.IOException;
import java.util.List;

import speakerrecognition.math.MatrixException;
import speakerrecognition.speaker.SpeakerData;

public interface SpeakerRecognition {

	String recognize(List<SpeakerData> speakerModels, String resourceSoundSpeechFilePath) throws IOException, MatrixException;

}
