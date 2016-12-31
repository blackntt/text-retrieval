package text_retrieval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import vn.hus.nlp.tokenizer.TokenizerOptions;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.TextFileFilter;
import vn.hus.nlp.utils.UTF8FileUtility;

/**
 * @author Black
 *
 */
public class TextProcessor {
	private static final String NEWS_DATA_FOLDER= "news_data_utf8";
	private static final String NEWS_DATA_KEY_VALUE_FOLDER= "news_output_key_value";
	private static final String NEWS_DATA_VECTOR_FOLDER= "news_output_vector";
	private static final String WORDS_COUNT_FILE= "words_count.txt";
	private static final String REMOVED_WORDS_FILE = "removed_words.txt";
	private static final String TOKENIZER_PROPERTY_FILE = "tokenizer.properties";
	
	public static void tokenize_and_count_frequent_in_docs(){
		
		TextFileFilter fileFilter = new TextFileFilter(TokenizerOptions.TEXT_FILE_EXTENSION);
		File inputDirFile = new File(NEWS_DATA_FOLDER);
		
		// get the current dir 
		String currentDir = new File(".").getAbsolutePath();
		String outputDirPath = currentDir + File.separator + NEWS_DATA_KEY_VALUE_FOLDER;

		VietTokenizer tokenizer = new VietTokenizer(TOKENIZER_PROPERTY_FILE);
						
		// get all input files
		File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);

		for (File aFile : inputFiles) {
			// get the simple name of the file
			String input =  aFile.getName();
			// the output file have the same name with the automatic file 
			String output = outputDirPath + File.separator + input;
			UTF8FileUtility.createWriter(output);
	
			String[] paragraphs = UTF8FileUtility.getLines(aFile.getAbsolutePath());
			List<Integer> counts = new ArrayList<Integer>();
			List<String> words = new ArrayList<String>();
			for (String p : paragraphs) {
				String[] sentences = tokenizer.tokenize(p);
				for (String s : sentences) {
					String[] temp_words = (AccentsRemover.removeAccent(s.trim())).split(" ");
					for(String temp_w: temp_words){
						if((int)temp_w.charAt(0)==65279 && temp_w.length()==1)
							temp_w = "";
						temp_w = removeStopWord(temp_w);
						if(temp_w.length()!=0&&temp_w!=null && !temp_w.equals("")&&!temp_w.isEmpty()){
							int flag = 0;
							for(int i=0;i<words.size();i++){
								if(words.get(i).toUpperCase().equals(temp_w.toUpperCase())){
									flag = 1;
									counts.set(i, counts.get(i)+1);
									break;
								}
							}
							if(flag == 0){
								words.add(temp_w);
								counts.add(1);
							}
						}	
					}
				}
			}
			
			//write <word, count> to files
			for(int i=0;i<words.size();i++){
				UTF8FileUtility.write(words.get(i)+" "+counts.get(i));
				UTF8FileUtility.write("\n");
			}
			UTF8FileUtility.closeWriter();
		}
	}
	
	/*
	 * Check whether a word is in the stop word list
	 * @param string
	 * @return string - "" if the input is a stop word
	 * */
	private static String removeStopWord(String str){
		String[] stopwords = UTF8FileUtility.getLines(REMOVED_WORDS_FILE);
		
		for (String stopw:stopwords) {
			if(stopw.equals("")||(stopw.toUpperCase()).equals(str.toUpperCase())){
				return "";
			}
		}
		return str;
	}

	/*
	 * Count TF of words in a document
	 * Using Maximum TF Scaling (alpha: 0)
	 * 
	 * @param words: list of words of a document
	 * @param counts: frequency of words in the order
	 * @return tflist: TF of the words in the order
	 * */
	private static List<Double> countMaxTFScaling(List<String> words, List<Integer> counts){
		List<Double> tflist = new ArrayList<Double>();
		
		//get maximum frequency
		Integer max = counts.get(0);
		for (int i = 1; i < counts.size(); i++) {
			if(counts.get(i) > max)
				max = counts.get(i);
		}
		//compute maximum tf scaling
		for (int i = 0; i < words.size(); i++) {
			tflist.add(0.1+(1-0.1)*counts.get(i)/(double)max);
		}
		
		return tflist;
	}
	
	public static List<Integer> countFreqInAllDocs(String inputDir,String outputFile, double percent){
		
		//contains words in all docs
		List<String> words = new ArrayList<String>();
		//contains freq of all words in the order
		List<Integer> freqs = new ArrayList<Integer>();
		
		TextFileFilter fileFilter = new TextFileFilter(TokenizerOptions.TEXT_FILE_EXTENSION);
		
		File inputDirFile = new File(inputDir);
		
		// get the current dir 
		String currentDir = new File(".").getAbsolutePath();
		String outputFilePath = currentDir + File.separator + outputFile;

		// get all input files
		File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
		for (File aFile : inputFiles) {
			String[] paragraphs = UTF8FileUtility.getLines(aFile.getAbsolutePath());
			for(String s:paragraphs){
				String[] word_count = s.trim().split(" ");
				int flag = 0;
				for (int i = 0; i < words.size(); i++) {
					if(word_count[0].toUpperCase().equals(words.get(i).toUpperCase())){
						flag = 1;
						freqs.set(i, freqs.get(i)+Integer.parseInt(word_count[1]));
						break;
					}
				}
				if(flag==0){
					words.add(word_count[0]);
					freqs.add(Integer.parseInt(word_count[1]));
				}
			}
		}
		
		/*
		 * Sort decremental freq
		 * */	
		for (int i = 0; i < freqs.size()-1; i++) {
			int m = i;
			for (int j = i+1; j < freqs.size(); j++) {
				if(freqs.get(j)>freqs.get(m))
					m = j;
			}
			int temp_i = freqs.get(m);
			freqs.set(m, freqs.get(i));
			freqs.set(i, temp_i);
			String temp_w = words.get(m);
			words.set(m, words.get(i));
			words.set(i, temp_w);
		}
		
		double removingCount = words.size()*percent;
		
		while(removingCount>=1){
			words.remove(0);
			words.remove(words.size()-1);
			removingCount--;
		}
		
		UTF8FileUtility.createWriter(outputFilePath);
		for(int i=0;i<words.size();i++){
			UTF8FileUtility.write(words.get(i)+" "+freqs.get(i));
			UTF8FileUtility.write("\n");
		}
		UTF8FileUtility.closeWriter();
		
		return freqs;
	}

	/*
	 * Count IDF of words in vocabulary
	 * */
	private static List<Double> countIDF(){
		List<Double> idfList = new ArrayList<Double>();
		
		String currentDir = new File(".").getAbsolutePath();
		String inputFilePath = currentDir + File.separator + WORDS_COUNT_FILE;
		
		String[] lines = UTF8FileUtility.getLines(inputFilePath);
		List<String> words = new ArrayList<String>();
		//List<Integer> counts = new ArrayList<Integer>();
		
		File inputDirFile = new File("news_output_key_value");
		TextFileFilter fileFilter = new TextFileFilter(TokenizerOptions.TEXT_FILE_EXTENSION);
		File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
		for(String l:lines){
			String[] word_count =l.trim().split(" ");
			words.add(word_count[0]);
			//counts.add(countdf(word_count[1]));
			int df = countdf(word_count[1]);
			if(df!=0)
				idfList.add(1+Math.log10((double)inputFiles.length/df));
			else
				idfList.add(0.0);
		}
		
		return idfList;
	}
	
	private static int countdf(String term){
		int count = 0;
		File inputDirFile = new File(NEWS_DATA_KEY_VALUE_FOLDER);
		TextFileFilter fileFilter = new TextFileFilter(TokenizerOptions.TEXT_FILE_EXTENSION);
		File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
		
		for (File aFile : inputFiles) {
			String[] lines = UTF8FileUtility.getLines(aFile.getAbsolutePath());
			for(String l:lines){
				String[] word_count = l.trim().split(" ");
				if(word_count[0].toUpperCase().equals(term.toUpperCase())){
					count++;
					break;
				}
			}
		}
		return count;
	}
	
	
	public static List<Double> counttf_idf_Afile(String docFile,List<String> Voca, List<Double> IDF){
		List<Double> tf_idf = new ArrayList<Double>();
		String inputFilePath = docFile;
		
		String[] lines = UTF8FileUtility.getLines(inputFilePath);
		
		List<String> words = new ArrayList<String>();
		List<Integer> counts = new ArrayList<Integer>();
		
		for(String l: lines){
			String[] word_count = l.trim().split(" ");
			words.add(word_count[0]);
			counts.add(Integer.parseInt( word_count[1]));
		}		
		List<Double> maxtfList = countMaxTFScaling(words, counts);
		for (int i = 0; i < Voca.size(); i++) {
			int flag = 0;
			for (int j = 0; j < words.size(); j++) {
				if(Voca.get(i).toUpperCase().equals(words.get(j).toUpperCase())){
					flag  = 1;
					tf_idf.add(maxtfList.get(j)*IDF.get(i));
				}
			}
			if(flag == 0)
				tf_idf.add(0.0);
			
		}

		return tf_idf;
	}
	
	public static void createVector(){
		String currentDir = new File(".").getAbsolutePath();
		String inputDocs = NEWS_DATA_KEY_VALUE_FOLDER;
		String outputDocs = currentDir + File.separator + NEWS_DATA_VECTOR_FOLDER;
		
		List<String> Vocabulary = new ArrayList<String>();
		List<Double> IDFofVocab = countIDF();
		String vocabPath = currentDir + File.separator + WORDS_COUNT_FILE;
		String[] lines = UTF8FileUtility.getLines(vocabPath);
		for(String l:lines){
			Vocabulary.add(l.trim().split(" ")[0]);
		}
		
		TextFileFilter fileFilter = new TextFileFilter(TokenizerOptions.TEXT_FILE_EXTENSION);
		File inputDirFile = new File(inputDocs);
		File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
		
		for(File f:inputFiles){
			String output = outputDocs + File.separator + f.getName();
			List<Double> tf_idf = counttf_idf_Afile(f.getAbsolutePath(),Vocabulary,IDFofVocab);
			
			UTF8FileUtility.createWriter(output);
			for(Double d: tf_idf){
				UTF8FileUtility.write(d.toString());
				UTF8FileUtility.write("\n");
			}

			UTF8FileUtility.closeWriter();
			
		}
		
	}
	
	/*
	 * Computing cosine similarity of vector1 and vector2
	 * */
	public static double cosineSimilarity(List<Double> vector1, List<Double> vector2){
		double dotProduct=0.0;
		double normVector1 =0.0;
		double normVector2 =0.0;
		for (int i = 0; i < vector1.size(); i++) {
			dotProduct +=vector1.get(i)*vector2.get(i);
			normVector1 += Math.pow(vector1.get(i),2);
			normVector2 += Math.pow(vector2.get(i),2);
		}
		return dotProduct/(Math.sqrt(normVector1)*Math.sqrt(normVector2));
	}
	
	
	public static List<String> processQuery(String query){
		VietTokenizer tokenizer = new VietTokenizer(TOKENIZER_PROPERTY_FILE);

		List<String> words = new ArrayList<String>();
		List<Integer> counts = new ArrayList<Integer>();
		
		String[] sentences = tokenizer.tokenize(query);
		
		for (String string : sentences) {
			System.out.println(string);
		}
		
		for (String s : sentences) {
			String[] temp_words = (AccentsRemover.removeAccent(s.trim())).split(" ");
			for(String temp_w: temp_words){
				if((int)temp_w.charAt(0)==65279 && temp_w.length()==1)
					temp_w = "";
				temp_w = removeStopWord(temp_w);
				if(temp_w.length()!=0&&temp_w!=null && !temp_w.equals("")&&!temp_w.isEmpty()){
					int flag = 0;
					for(int i=0;i<words.size();i++){
						if(words.get(i).toUpperCase().equals(temp_w.toUpperCase())){
							flag = 1;
							counts.set(i, counts.get(i)+1);
							break;
						}
					}
					if(flag == 0){
						words.add(temp_w);
						counts.add(1);
					}
				}	
			}
		}
				
		List<Double> maxtfList =  countMaxTFScaling(words,counts);
		
		
		String currentDir = new File(".").getAbsolutePath();
		List<String> Voca = new ArrayList<String>();
		List<Double> IDF = countIDF();
		String vocabPath = currentDir + File.separator + WORDS_COUNT_FILE;
		String[] lines = UTF8FileUtility.getLines(vocabPath);
		for(String l:lines){
			Voca.add(l.trim().split(" ")[0]);
		}
		

		List<Double> tf_idf = new ArrayList<Double>();
		for (int i = 0; i < Voca.size(); i++) {
			int flag = 0;
			for (int j = 0; j < words.size(); j++) {
				if(Voca.get(i).toUpperCase().equals(words.get(j).toUpperCase())){
					flag  = 1;
					tf_idf.add(maxtfList.get(j)*IDF.get(i));
				}
			}
			if(flag == 0)
				tf_idf.add(0.0);
		}
		
		
		TextFileFilter fileFilter = new TextFileFilter(TokenizerOptions.TEXT_FILE_EXTENSION);
		File inputDirFile = new File(NEWS_DATA_VECTOR_FOLDER);
		File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
		List<Double> cosine_list = new ArrayList<Double>();
		List<String> filenames = new ArrayList<String>();
		for (File file : inputFiles) {
			String[] features = UTF8FileUtility.getLines(file.getAbsolutePath());
			List<Double> vector = new ArrayList<Double>();
			for (String string : features) {
				vector.add(Double.parseDouble(string));
			}		
			cosine_list.add(cosineSimilarity(tf_idf, vector));
			filenames.add(file.getName());
		}		
		
		for (int i = 0; i < cosine_list.size()-1; i++) {
			int m = i;
			for (int j = i+1; j < cosine_list.size(); j++) {
				if(cosine_list.get(j)>cosine_list.get(m))
					m = j;
			}
			double temp_cosine = cosine_list.get(m);
			cosine_list.set(m, cosine_list.get(i));
			cosine_list.set(i, temp_cosine);
			String temp_name = filenames.get(m);
			filenames.set(m, filenames.get(i));
			filenames.set(i, temp_name);
		}
		
		for (int i = 0; i < 20; i++) {
			System.out.println(filenames.get(i) + " " +  cosine_list.get(i));
		}
		
		return filenames;
	}
}
