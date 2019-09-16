package predictor;

import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;

import util.Counter;


public class Predictor {

	private List<MethodDeclaration> nodes;
	

	public Predictor(List<MethodDeclaration> nodes) {
		this.nodes = nodes;
	}
	
	public void run() {
		for(int i = 0; i < this.nodes.size(); i++) {
			MethodDeclaration node = this.nodes.get(i);
			Counter.total++;
			
			if(GetterSetterPredictor.predict(node) > -1)
				continue;
			if(ShortMPredictor.predict(node) > -1)
				continue;
			SignaturePredictor.predict(node);
		}
	}
	
}
