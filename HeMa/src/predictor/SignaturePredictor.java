package predictor;

import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.body.MethodDeclaration;

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
}
