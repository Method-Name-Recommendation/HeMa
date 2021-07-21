package predictor;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.body.MethodDeclaration;

import predictor.dao.VisitDB;
import util.Counter;
import util.Tokenizer;
import util.TrainSet;

public class SignaturePredictor {
	
	public static int predict(MethodDeclaration node) {
		String method_name = node.getName();
		Signature signature = new Signature(node);
		
		if(TrainSet.getData().containsKey(signature)) {
			Map<String, Integer> counter = TrainSet.getData().get(signature);
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

	// Not Used
	public static void load_trainset(VisitDB db) throws Exception {
		int total = 0;
		ResultSet rs = db.executeQuery("select method, signature from trainset");
		while(rs.next()) {
			total++;
			String method_name = rs.getString(1);
			Signature signature = new Signature(rs.getString(2));
			
			if(TrainSet.getData().containsKey(signature)) {
				Map<String, Integer> counter = TrainSet.getData().get(signature);
				if(counter.containsKey(method_name)) {
					counter.put(method_name, counter.get(method_name)+1);
					TrainSet.getData().put(signature, counter);
				}else {
					counter.put(method_name, 1);
					TrainSet.getData().put(signature, counter);
				}
			}else {
				Map<String, Integer> counter = new HashMap<>();
				counter.put(method_name, 1);
				TrainSet.getData().put(signature, counter);
			}
		}
		System.out.println(total + " train samples loaded.");
	}	
}
