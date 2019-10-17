package speakerrecognition.mfcc;


import org.jtransforms.fft.DoubleFFT_1D;
import speakerrecognition.math.Matrixes;


public class MFCCService {

	public double[][] getMFCC(int[] x, int y){
		MFCCData mfccData = new MFCCData();
		mfccData.fs = y;
		mfccData.samples = x;
		mfccData.frame_len = 256;
		mfccData.fft_size = mfccData.frame_len;
		mfccData.frame_shift = setFrameShift(mfccData.fs);
		mfccData.window = hamming(mfccData.frame_len);

		mfccData.melfb_coeffs = melfb(mfccData, mfccData.melfilter_bands, mfccData.fft_size, mfccData.fs);

		mfccData.D1 = dctmatrix(mfccData, mfccData.melfilter_bands);

		if(mfccData.melfb_coeffs==null) System.out.println("Cannot initialize melfilter bank");
		extract_MFCC(mfccData);
		return mfccData.mfcc_coeffs;
	}
	
	private int setFrameLen(int sample_rate){
		return (int) (0.025*(double)(sample_rate));
	}
	
	private int setFrameShift(int sample_rate){
		return (int) (0.0125*(double)(sample_rate));
	}
	
	private double[] hamming(int frame_len){
		double[] window_temp = new double[frame_len];
		for(int i=0;i<window_temp.length;i++){
			window_temp[i] = 0.54-0.46*Math.cos(2*Math.PI/(double)frame_len*((double)i+0.5));
		}
		return window_temp;
	}


///////////////// computation of mel filterbank ////////////////

	private double[][] melfb(MFCCData mfccData, int p, int n, int fs){
		// p - number of filterbanks
		// n - length of fft
		// fs - sample rate 
		
		double f0 = 700/(double)fs;
		int fn2 = (int)Math.floor((double)n/2);
		double lr = Math.log((double)1+0.5/f0)/(p+1);
		double[] CF = arange(1,p+1);
		
		for(int i=0;i<CF.length;i++){
			CF[i] = fs*f0*(Math.exp(CF[i]*lr)-1);
			//CF[i] = (Math.exp(CF[i]*lr));
		}
		
		double[] bl = {0, 1, p, p+1};
		
		for(int i=0;i<bl.length;i++){
			bl[i] = n*f0*(Math.exp(bl[i]*lr)-1);
		}
		
		int b1 = (int)Math.floor(bl[0])+1;
		int b2 = (int)Math.ceil(bl[1]);
		int b3 = (int)Math.floor(bl[2]);
		int b4 = Math.min(fn2, (int)Math.ceil(bl[3]))-1;
		double[] pf = arange(b1, b4+1);
		
		for(int i=0;i<pf.length;i++){
			pf[i] = Math.log(1+pf[i]/f0/(double)n)/lr;
		}
		
		double[] fp = new double[pf.length];
		double[] pm = new double[pf.length];
		
		for(int i=0;i<fp.length;i++){
			fp[i] = Math.floor(pf[i]);
			pm[i] = pf[i] - fp[i];
		}

		mfccData.M = new double[p][1+fn2];
		int r=0;
		
		for(int i=b2-1;i<b4;i++){
			r = (int)fp[i]-1;
			mfccData.M[r][i+1] += 2* (1-pm[i]);
		}
		
		for(int i=0;i<b3; i++){
			r = (int)fp[i];
			mfccData.M[r][i+1] += 2* pm[i];
		}
		
		/////////// normalization part //////////
		
		//int xx = M.length;
		double[] temp_row = null;
		double row_energy = 0;
		//System.out.println(Integer.toString(M.length));
		for (int i=0;i<mfccData.M.length;i++){
			temp_row = mfccData.M[i];
			row_energy = energy(temp_row);
			if(row_energy < 0.0001)
				temp_row[i] = i;
			else{
				while(row_energy>1.01){
					temp_row = Matrixes.row_mul(temp_row, 0.99);
					row_energy = energy(temp_row);
				}
				while(row_energy<0.99){
					temp_row = Matrixes.row_mul(temp_row, 1.01);
					row_energy = energy(temp_row);
				}
			}
			mfccData.M[i] = temp_row;
			
		}
		
	
		
		return mfccData.M;
	}

//////////////////////////////////////////////////////////////////////////////////////////

	private void extract_MFCC(MFCCData mfccData){
		// https://gist.github.com/jongukim/4037243
		//http://dp.nonoo.hu/projects/ham-dsp-tutorial/05-sine-fft/
		
		if(mfccData.samples!=null){
			DoubleFFT_1D fftDo = new DoubleFFT_1D(mfccData.frame_len);
			double[] fft1 = new double[mfccData.frame_len * 2];
			double[] fft_final = new double[mfccData.frame_len/2+1];
			//int[] x = this.samples;
			int frames_num = (int)((double)(mfccData.samples.length - mfccData.frame_len)/(double)(mfccData.frame_shift))+1;
			mfccData.mfcc_coeffs = new double[frames_num][mfccData.mfcc_num];
			double[] frame = new double[mfccData.frame_len];
							
			for(int i=0;i<frames_num;i++){
				
				for(int j=0;j<mfccData.frame_len;j++){
					frame[j] = (double)mfccData.samples[i*mfccData.frame_shift+j];
				}
				
				try{
					frame = Matrixes.row_mul(frame, mfccData.window);
				
					frame = preemphasis(mfccData, frame);
					System.arraycopy(frame, 0, fft1, 0, mfccData.frame_len);
					fftDo.realForwardFull(fft1);
					/*for(double d: fft1) {
			          System.out.println(d);
					}*/
					
					for(int k=0;k<(mfccData.frame_len/2+1);k++){
						fft_final[k] = Math.pow(Math.sqrt(Math.pow(fft1[k*2],2)+Math.pow(fft1[k*2+1],2)), 2);
						
						if(fft_final[k]<mfccData.power_spectrum_floor) fft_final[k]=mfccData.power_spectrum_floor;
					}
					
					double[] dot_prod = Matrixes.multiplyByMatrix(mfccData.melfb_coeffs, fft_final);
					for(int j=0;j<dot_prod.length;j++){
						dot_prod[j] = Math.log(dot_prod[j]);
					}
					//double[][]D1 = dctmatrix(melfilter_bands);
					dot_prod = Matrixes.multiplyByMatrix(mfccData.D1, dot_prod);
					mfccData.mfcc_coeffs[i] = dot_prod;
				}
				catch(Exception myEx)
		        {
					System.out.println("An exception encourred: " + myEx.getMessage());
		            myEx.printStackTrace();
		            System.exit(1);		            
		        }
				
			}
			//this.mfcc_coeffs = 
		}
		else{
			System.out.println("Vector of input samples is null");
		}
		
	}
	
	///////////// math functions ///////////////////////////////////////////////////////////////
		
	private static double[] arange(int x1, int x2){
		double[] temp = null;
		try{
		temp = new double[x2-x1];
			for(int i=0;i<temp.length;i++){
				temp[i] = x1+i;
			}
		
		}
		catch(IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage());
		}
		return temp;
	}
	
	private static double energy(double[] x){
		double en = 0;
		for(int i=0; i<x.length;i++)
			en = en + Math.pow(x[i], 2);
		return en;
		}
		
	private double[] preemphasis(MFCCData mfccData, double[] x){
		double[] y = new double[x.length];
		y[0] = x[0];
		for(int i=1;i<x.length;i++){
			y[i] = x[i]-mfccData.pre_emph*x[i-1];
		}
		return y;
	}

	private double[][] dctmatrix(MFCCData mfccData, int n){
		double[][] d1 = new double[n][n];
		double[][] x = Matrixes.meshgrid_ox(n);
		double[][] y = Matrixes.meshgrid_oy(n);
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				x[i][j] = (x[i][j]*2+1)*Math.PI/(2*n);
			}
		}
		
		try{
			d1 = Matrixes.multiplyMatrixesElByEl(x, y);
		}
		catch(Exception myEx)
        {
            //System.out.println("An exception encourred: " + myEx.getMessage());
            myEx.printStackTrace();
            System.exit(1);
        }
		
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				d1[i][j] = Math.sqrt(2/(double)n)*Math.cos(d1[i][j]);
			}
		}
		for(int i=0;i<n;i++){
			d1[0][i] /= Math.sqrt(2);
		}
		
		double[][] d = new double[mfccData.mfcc_num][n];
		for(int i=1;i<mfccData.mfcc_num+1;i++){
			for(int j=0;j<n;j++){
				d[i-1][j] = d1[i][j];
			}
			
		}
		
		return d;
	}
}
