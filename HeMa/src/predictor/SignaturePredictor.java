package predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.body.MethodDeclaration;

import util.Counter;
import util.Tokenizer;

public class SignaturePredictor {
	
	private static Map<Signature, Map<String, Integer>> trainSet = new HashMap<>();
	
	public static int predict(MethodDeclaration node) {
		String method_name = node.getName();
		Signature signature = new Signature(node);
		
		if(trainSet.containsKey(signature)) {
			Map<String, Integer> counter = trainSet.get(signature);
			int max = 0;
			String prediction = "";
			for(Entry<String, Integer> entry : counter.entrySet()) {
				if(entry.getValue() > max) {
					max = entry.getValue();
					prediction = entry.getKey();
				}
			}
			if(max > 0) {
				Counter.sPredicted++;
				
				String reference = Tokenizer.tokenize(method_name).toLowerCase();
				
				prediction = Tokenizer.tokenize(prediction).toLowerCase();
				
				int precision = reference.equals(prediction) ? 1 : 0;
				Counter.sCorrect += precision;
				return precision;
			}
		}
		return -1;
	}
	
	public static void load_trainset() {
		int total = 0;
		List<String> lines = read("./trainset.csv");
		for(String line : lines){
			total++;
			String[] strs = line.split(",");
			String method_name = strs[0].substring(1, strs[0].length()-1);
			Signature signature = new Signature(strs[1].substring(1, strs[1].length()-1));
			
			if(trainSet.containsKey(signature)) {
				Map<String, Integer> counter = trainSet.get(signature);
				if(counter.containsKey(method_name)) {
					counter.put(method_name, counter.get(method_name)+1);
					trainSet.put(signature, counter);
				}else {
					counter.put(method_name, 1);
					trainSet.put(signature, counter);
				}
			}else {
				Map<String, Integer> counter = new HashMap<>();
				counter.put(method_name, 1);
				trainSet.put(signature, counter);
			}
		}
		System.out.println(total + " train samples loaded.");
	}
	
	public static List<String> read(String filePath){
		List<String> list = new ArrayList<String>();
        try{
            File file = new File(filePath);
            if (file.isFile() && file.exists()){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = null;

                while ((line = bufferedReader.readLine()) != null)
                {
                    list.add(line);
                }
                bufferedReader.close();
                reader.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
	}
}
