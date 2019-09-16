package predictor;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

public class Signature {
	
	private String return_type;
	private String[] paramTypes;
	private String[] paramTokens;
	private int param_num;
	
	public Signature(MethodDeclaration node) {
		Type<?> returnType = node.getType();
		removeComment(returnType);
		this.return_type = returnType.toString();
		
		List<Parameter> parameters = node.getParameters();
		this.param_num = parameters.size();
		this.paramTypes = new String[this.param_num];
		this.paramTokens = new String[this.param_num];
		
		for(int i = 0; i < parameters.size(); i++) {
			Type<?> paramType = parameters.get(i).getType();
			removeComment(paramType);
			this.paramTypes[i] = paramType.toString();
			this.paramTokens[i] =parameters.get(i).getId().getName();
		}
	}
	
	private void removeComment(Node node) {
		node.setComment(null);
		for(Node child : node.getChildrenNodes()) {
			removeComment(child);
		}
	}

	
	public Signature(String signature) {
		String[] strs = signature.split(";");
		this.return_type = strs[0];
		this.param_num = (strs.length-1)/2;
		this.paramTypes = new String[param_num];
		for(int i = 0; i < param_num; i++) {
			this.paramTypes[i] = strs[i*2+1];
		}
		this.paramTokens = new String[param_num];
		for(int i = 0; i < param_num; i++) {
			this.paramTokens[i] = strs[i*2+2];
		}
	}
	
	@Override
	public String toString() {
		String signature = return_type;
		for(int i = 0 ; i < paramTypes.length; i++) {
			signature += ";" + paramTypes[i];
			signature += ";" + paramTokens[i];
		}
		return signature;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		int sum = 0;
		for(String paramType : paramTypes) {
			int hash = paramType == null ? 0 : paramType.hashCode();
			sum += hash;
		}
		result = prime * result + sum;
		sum = 0;
		for(String paramToken : paramTokens) {
			 int hash = paramToken == null ? 0 : paramToken.hashCode();
			 sum += hash;
		}
		result = prime * result + sum;
		result = prime * result + ((return_type == null) ? 0 : return_type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Signature other = (Signature) obj;
		if (!equals(paramTypes, other.paramTypes))
			return false;
		if(!equals(paramTokens, other.paramTokens))
			return false;
		if (return_type == null) {
			if (other.return_type != null)
				return false;
		} else if (!return_type.equals(other.return_type))
			return false;
		return true;
	}
	
	private boolean equals(String[] a, String[] a2) {
        if (a==a2)
            return true;
        if (a==null || a2==null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for(int i = 0; i < length; i++) {
        	int length2 = a2.length;
        	for(int j = 0; j < length2; j++) {
        		if(a[i].equals(a2[j])) {
        			String[] tmp = new String[length2-1];
        			for(int k = 0; k < j; k++) {
        				tmp[k] = a2[k];
        			}
        			for(int k = j+1; k < length2; k++) {
        				tmp[k-1] = a2[k];
        			}
        			a2 = tmp;
        			break;
        		}
        	}
        	if(a2.length == length2)
        		return false;
        }
        return true;
    }
	

}
