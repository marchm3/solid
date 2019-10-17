package speakerrecognition.file;

import speakerrecognition.math.MatrixException;

import java.io.IOException;

public interface FileOpener {
    public int[] open(String resourcePath) throws IOException, MatrixException;
}
