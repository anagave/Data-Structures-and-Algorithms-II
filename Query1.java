// Query1: Sort down “Token’id” by the frequency of the transactions.
// The objective of this query is to classify the rows, based on the TokenId’s by the number of transactions
// associated with that TokenId.
// Key data_structures: Treemap, Hashmap, LinkedHashSet.
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.Map;

public class Query1 {

	public static void radixSort(ArrayList<Integer> list) {
		final int countarray = 10;
		int max = 0;
		ArrayList<ArrayList<Integer>> buckets;
		buckets = new ArrayList<ArrayList<Integer>>(countarray);
		/*
		 * Buckets which are new are to be added into the list each digit in the
		 * numerals of the number is being sorted
		 */
		for (int i = 0; i < countarray; i++)
			buckets.add(new ArrayList<Integer>());

		/*
		 * get the list item with highest place values
		 */
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) > max)
				max = list.get(i);
		}

		/*
		 * Items in the list are sorted by place values Each place value is multiplied
		 * by the numeric base with each iteration of this loop
		 */
		for (int power = 1; max / power != 0; power *= countarray) {
			for (int i = 0; i < list.size(); i++)
				buckets.get(list.get(i) / power % countarray).add(list.get(i));
			int index = 0;
			for (int i = 0; i < buckets.size(); i++) {
				for (int j = 0; j < buckets.get(i).size(); j++) {
					/*
					 * Consider their values should be written back to original list and Overwriting
					 * its previous contents Buckets are now being sorted by current place value
					 */
					list.set(index, buckets.get(i).get(j));
					index++;
				}
			}

			/*
			 * Bucket is dumped and cleared
			 */
			for (int i = 0; i < buckets.size(); i++)
				buckets.get(i).clear();

		}
	}

	static void ExConstructor() throws FileNotFoundException {
		try {
			Map<String, List<String>> map = new TreeMap<>();
			TreeMap<String, Integer> freq = new TreeMap<String, Integer>();
			FileWriter fw = new FileWriter("Output_1.txt", true);
			HashMap<String, Double> currencyConvert = new HashMap<String, Double>();
			currencyConvert.put("ETH", 1309.97);
			currencyConvert.put("WETH", 1322.16);
			currencyConvert.put("ASH", 0.9406);
			currencyConvert.put("GALA", 0.03748);
			currencyConvert.put("TATR", 0.012056);
			currencyConvert.put("USDC", 1.00);
			currencyConvert.put("MANA", 0.64205);
			currencyConvert.put("SAND", 0.7919);
			currencyConvert.put("RARI", 2.18);
			currencyConvert.put("CTZN", 0.00321);
			currencyConvert.put("APE", 4.62);
			/*
			 * loop to read all the files from the directory
			 */
			Double res;
			String line;
			Scanner sc = new Scanner(System.in);
			System.out.print("Enter File Name: ");
			String pathname = sc.nextLine();
			BufferedReader br;

			br = new BufferedReader(new FileReader(pathname));

			line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] s = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -12);
				String p = s[9].replace('"', ' ');
				p = p.replace(",", "");
				String[] price = p.split(" ");
				/*
				 * Converting the cryptocurrency value in dollars and storing all the rows in a
				 * treemap with tokenID as the key.
				 */
				if (price.length >= 3) {
					if (currencyConvert.containsKey(String.valueOf(price[2]))) {
						res = currencyConvert.get(String.valueOf(price[2])) * Double.parseDouble(price[1]);
						String l = s[0] + "," + s[2] + "," + s[4] + "," + s[5] + "," + s[7] + "," + s[8] + ", " + '"'
								+ String.valueOf(res) + '"';
						map.computeIfAbsent(s[6], key -> new ArrayList<>()).add(l);
					}
				} else { // s[9] doesn't have any currency name in it. Try debugging with print
							// statements
					String l = s[0] + "," + s[2] + "," + s[4] + "," + s[5] + "," + s[7] + "," + s[8] + "," + s[9];
					map.computeIfAbsent(s[6], key -> new ArrayList<>()).add(l);
				}
			}
			br.close();

			/*
			 * Storing the frequencies of the tokenID in a hashmap with tokenID as key and
			 * frequency of that particular tokenID as value.
			 */
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				if (!freq.containsKey(entry.getKey())) {
					Integer n = entry.getValue().size();
					freq.put(entry.getKey(), n);
				}
			}
			/*
			 * Sorting of frequencies of the tokenId's
			 */
			ArrayList<Integer> arrayList = new ArrayList<>();
			/*
			 * Storing the frequencies of the tokenId's in an Arraylist
			 */
			for (Map.Entry<String, Integer> entry : freq.entrySet())
				arrayList.add(entry.getValue());
			LinkedHashSet<Integer> hashSet = new LinkedHashSet<>(arrayList);
			/*
			 * Removing the duplicate values from the ArrayList and use that for sorting, to
			 * reduce the runtime of the algorithm.
			 */
			ArrayList<Integer> listWithoutDuplicates = new ArrayList<>(hashSet);
			long startfsTime = System.nanoTime();
			/*
			 * Sorting the frequencies of the token Id's using Radix Sort algorithm.
			 */
			radixSort(listWithoutDuplicates);
			long endfsTime = System.nanoTime();
			long totalfsTime = endfsTime - startfsTime;
			System.out.println(totalfsTime / 1000000.0 + " milli second/s to run for sorting");
			/*
			 * To print the results in the descending order.
			 */
			Collections.reverse(listWithoutDuplicates);
			/*
			 * Loop to write the sorted results in a text file
			 */
			for (int i : listWithoutDuplicates) {
				if (freq.containsValue(i)) {
					for (String s : freq.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), i))
							.map(Map.Entry::getKey).collect(Collectors.toSet())) {
						fw.write(" TokenID:" + s + " (frequency = " + i + ")\n");
						map.get(s).forEach(number -> {
							try {
								fw.write(s + " : " + number + "\n");
							} catch (IOException e) {
								e.printStackTrace();
							}
						});
					}
				}
			}
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Call of Main method
	 */
	public static void main(String[] args) {
		try {
			ExConstructor();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
