package speakerrecognition.speaker;
import speakerrecognition.math.Matrixes;
import speakerrecognition.math.MatrixException;
import speakerrecognition.math.Statistics;

import java.io.Serializable;

public class SpeakerService implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static double getScore(SpeakerData speakerData, double[][] data) throws MatrixException {
		double score = 0;
		double[] logprob = null;
		double[][] lpr = log_multivariate_normal_density(speakerData, data);
		lpr = Matrixes.addValue(lpr, Matrixes.makeLog(speakerData.weights));
		logprob = Matrixes.logsumexp(lpr);
		score = Statistics.getMean(logprob);
		return score;
	}
	
	static private double[][] log_multivariate_normal_density(SpeakerData speakerData, double[][] data) throws MatrixException {
		//diagonal type
		double[][] lpr;
		int n_dim = data[0].length;
		
		double[] sumLogCov = Matrixes.sum(Matrixes.makeLog(speakerData.covars), 1); //np.sum(np.log(covars), 1)
		double[] sumDivMeanCov = Matrixes.sum(Matrixes.divideElements(Matrixes.power(speakerData.means, 2), speakerData.covars),1); //np.sum((means ** 2) / covars, 1)
		double[][] dotXdivMeanCovT = Matrixes.multiplyByValue(Matrixes.multiplyByMatrix(data, Matrixes.transpose(Matrixes.divideElements(speakerData.means, speakerData.covars))), -2); //- 2 * np.dot(X, (means / covars).T)
		double[][] dotXdivOneCovT = Matrixes.multiplyByMatrix(Matrixes.power(data,  2), Matrixes.transpose(Matrixes.invertElements(speakerData.covars)));
		
		
		sumLogCov = Matrixes.addValue(sumLogCov,n_dim * Math.log(2*Math.PI)); //n_dim * np.log(2 * np.pi) + np.sum(np.log(covars), 1)
		sumDivMeanCov = Matrixes.addMatrixes(sumDivMeanCov, sumLogCov); // n_dim * np.log(2 * np.pi) + np.sum(np.log(covars), 1) + np.sum((means ** 2) / covars, 1)
		dotXdivOneCovT = Matrixes.sum(dotXdivOneCovT, dotXdivMeanCovT); //- 2 * np.dot(X, (means / covars).T) + np.dot(X ** 2, (1.0 / covars).T)
		dotXdivOneCovT = Matrixes.addValue(dotXdivOneCovT, sumDivMeanCov); // (n_dim * np.log(2 * np.pi) + np.sum(np.log(covars), 1) + np.sum((means ** 2) / covars, 1) - 2 * np.dot(X, (means / covars).T) + np.dot(X ** 2, (1.0 / covars).T))
		lpr = Matrixes.multiplyByValue(dotXdivOneCovT, -0.5);
		
		return lpr;
	}

}
