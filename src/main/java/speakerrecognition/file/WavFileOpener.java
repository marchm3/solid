package speakerrecognition.file;

import speakerrecognition.math.MatrixException;

import java.io.IOException;

public class WavFileOpener implements FileOpener
{
    public int[] open(String resourcePath) throws IOException, MatrixException {
        WavFileService wavFileService = new WavFileService();
        WavFileData fileData = wavFileService.open(resourcePath);
        return fileData.samples;
    }
}
