package speakerrecognition.speaker;

public class SpeakerData {
    public double[][] means=null;
    public double[][] covars=null;
    public double[] weights = null;
    public String name = null;

    public SpeakerData(double[][] means, double[][] covars, double[] weights, String name) {
        this.means = means;
        this.covars = covars;
        this.weights = weights;
        this.name = name;
    }
}
