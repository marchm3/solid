package speakerrecognition.mfcc;

class MFCCData {
    public int frame_len;
    public int frame_shift;
    public int fft_size;// = 256;
    public int melfilter_bands = 40;
    public int mfcc_num = 13;
    public double power_spectrum_floor = 0.0001;
    public double pre_emph = 0.95;
    public double[] window = null;
    public double[][] M = null;
    public double[][] melfb_coeffs = null;
    public double[][] mfcc_coeffs = null;
    public int[] samples = null;
    public int fs;
    public double[][] D1 = null;
}
