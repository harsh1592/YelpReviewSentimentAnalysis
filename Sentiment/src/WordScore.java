
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.*;

/**
 * Extract and calculate the sentiment score of a given token
 *
 * @author Harsh Patil
 * @author Samita Pradhan
 * 
 */
public class WordScore {
	private String pathToSWN = "SentiWordNet.txt";
	private HashMap<String, Double> _dict;

	private ArrayList<String> stopWordslist;

	public WordScore() throws FileNotFoundException {
		Scanner stopword = new Scanner(new File("stopwords.txt"));
		stopWordslist = new ArrayList<>();
		while (stopword.hasNextLine()) {
			String line = stopword.nextLine();
			stopWordslist.add(line);
		}

		_dict = new HashMap<String, Double>();
		HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
		try {
			BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
			String line = "";
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
				Double score = Double.parseDouble(data[2]) - Double.parseDouble(data[3]);
				String[] words = data[4].split(" ");

				for (String w : words) {
					String[] w_n = w.split("#");
					w_n[0] += "#" + data[0];
					int index = Integer.parseInt(w_n[1]) - 1;
					if (_temp.containsKey(w_n[0])) {
						Vector<Double> v = _temp.get(w_n[0]);
						if (index > v.size())
							for (int i = v.size(); i < index; i++)
								v.add(0.0);
						v.add(index, score);
						_temp.put(w_n[0], v);
					} else {
						Vector<Double> v = new Vector<Double>();
						for (int i = 0; i < index; i++)
							v.add(0.0);
						v.add(index, score);
						_temp.put(w_n[0], v);
					}
				}
			}
			Set<String> temp = _temp.keySet();
			for (Iterator<String> iterator = temp.iterator(); iterator.hasNext();) {
				String word = (String) iterator.next();
				Vector<Double> v = _temp.get(word);
				double score = 0.0;
				double sum = 0.0;
				for (int i = 0; i < v.size(); i++)
					score += ((double) 1 / (double) (i + 1)) * v.get(i);
				for (int i = 1; i <= v.size(); i++)
					sum += (double) 1 / (double) i;
				score /= sum;
				_dict.put(word, score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Double extract(String word) {
		Double total = new Double(0);
		if (_dict.get(word + "#n") != null)
			total = _dict.get(word + "#n") + total;
		if (_dict.get(word + "#a") != null)
			total = _dict.get(word + "#a") + total;
		if (_dict.get(word + "#r") != null)
			total = _dict.get(word + "#r") + total;
		if (_dict.get(word + "#v") != null)
			total = _dict.get(word + "#v") + total;
		return total;
	}

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		WordScore sn = new WordScore();

		double d = sn.extract("worst");
		System.out.println(d);
	}
}