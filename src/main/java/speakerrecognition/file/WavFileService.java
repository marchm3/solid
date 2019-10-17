package speakerrecognition.file;


import speakerrecognition.file.WavFileData;

import java.io.IOException;
import java.io.RandomAccessFile;


public class WavFileService {

	public WavFileData open(String file_path) throws IOException {
		WavFileData fileData = new WavFileData();
		
		RandomAccessFile in = null;
		try{
			in = new RandomAccessFile(file_path.toString(), "r");
		}
		catch(Exception myEx)
        {
            //System.out.println("An exception encourred: " + myEx.getMessage());
            myEx.printStackTrace();
            System.exit(1);
        }


		fileData.byte_samples = new byte[(int) in.length()];
		
		
		in.read(fileData.byte_samples, 0, (int) (in.length()));

		fileData.samples_num = getSamplesNum(fileData.byte_samples[40], fileData.byte_samples[41], fileData.byte_samples[42], fileData.byte_samples[43]);
		fileData.channels_num = getChannelsNum(fileData.byte_samples[22], fileData.byte_samples[23]);
		
		//samples = new int[(int) (in.length()-44)/2/this.channels_num];
		fileData.samples = new int[fileData.samples_num/2/fileData.channels_num];

		fileData.fs = getFs(fileData.byte_samples[24], fileData.byte_samples[25], fileData.byte_samples[26], fileData.byte_samples[27]);
			
		if(fileData.channels_num==1){
			for (int i=44;i<(fileData.samples_num+44)/2; i++){
				fileData.samples[i-44] = toInt(fileData.byte_samples[(i-44)*2+45], fileData.byte_samples[(i-44)*2+44]);
			}
		}
		else if(fileData.channels_num==2){
			int j=44;
			for (int i=44;i<(fileData.samples_num+44)/2; i+=2){
				fileData.samples[j-44] = (toInt(fileData.byte_samples[(i-44)*2+45], fileData.byte_samples[(i-44)*2+44])+toInt(fileData.byte_samples[(i-44)*2+47], fileData.byte_samples[(i-44)*2+46]))/2;
				j++;
			}
		}
		else{
			System.out.println("Too much channels, only 1 or 2 are supported");
		}

		in.close();

		return fileData;
	}
	    
	private int toInt(byte hb, byte lb){
		return ((int)hb << 8) | ((int)lb & 0xFF);
	}
	
	private int getFs(byte x1, byte x2, byte x3, byte x4){
		return ((int)x1 & 0xFF | (int)x2 << 8 | (int)x3 << 16 | (int)x4 << 24 );
	}
	
	private int getSamplesNum(byte x1, byte x2, byte x3, byte x4){
		return ((int)x1 & 0xFF | ((int)x2 << 8) & 0xFF00 | ((int)x3 << 16) & 0xFF0000 | ((int)x4 << 24) & 0xFF000000);
	}
	
	private int getChannelsNum(byte x1, byte x2){
		return ((int)x2 << 8) | ((int)x1 & 0xFF);
	}

}


