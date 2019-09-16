package predictor;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.VoidType;

import util.Counter;
import util.Tokenizer;

public class GetterSetterPredictor {
	
	public static int predict(MethodDeclaration node) {
		if(node.getParameters().size() > 0) {
			String prediction = predictSetter(node);
			if(prediction != null) {
				Counter.gPredicted++;
				String reference = Tokenizer.tokenize(node.getName()).toLowerCase();
				prediction = Tokenizer.tokenize(prediction).toLowerCase();
				
				if(prediction.startsWith("is ")) {
					prediction = prediction.replaceFirst("is ", "set ");
				}else if(prediction.startsWith("m ")) {
					prediction = prediction.replaceFirst("m ", "set ");
				}else {
					prediction = "set " + prediction;
				}
				
				int precision = reference.equals(prediction) ? 1 : 0;
				Counter.gCorrect += precision;
				return precision;
			}
		}
		
		if(!(node.getElementType() instanceof VoidType)) {
			String prediction = predictGetter(node);
			if(prediction != null) {
				Counter.gPredicted++;
				String reference = Tokenizer.tokenize(node.getName()).toLowerCase();
				
				prediction = Tokenizer.tokenize(prediction).toLowerCase();
				if(node.getElementType() instanceof PrimitiveType && 
						((PrimitiveType)node.getElementType()).getType().name().equals("Boolean")) {
					if(!prediction.startsWith("is "))
						prediction = "is " + prediction;
				}else if(prediction.startsWith("m ")) {
					prediction = prediction.replaceFirst("m ", "get ");
				}else {
					prediction = "get " + prediction;
				}

				int precision = reference.equals(prediction) ? 1 : 0;
				Counter.gCorrect += precision;
				return precision;
			}
		}
		return -1;
	}
	

	private static String predictGetter(MethodDeclaration node) {
		
		if(node.getElementType() instanceof VoidType) {
			return null;
		}
		
		List<ReturnStmt> returnStmts = node.getNodesByType(ReturnStmt.class);
		if(returnStmts.size() == 0) {
			return null;
		}
		
		if(returnStmts.size() > 1) {
			return null;
		}
		
		ReturnStmt returnStmt = returnStmts.get(returnStmts.size()-1);
		Expression expr = returnStmt.getExpr();
		
		String returnName = "";
		if(expr instanceof FieldAccessExpr) {
			returnName = ((FieldAccessExpr)expr).getField();
		}else if(expr instanceof NameExpr) {
			returnName = ((NameExpr)expr).getName();
		}else {
			return null;
		}
		
		FieldDeclaration field = null;
		ClassOrInterfaceDeclaration parent = node.getParentNodeOfType(ClassOrInterfaceDeclaration.class);
		if(parent != null) {
			List<FieldDeclaration> fieldDeclarations = parent.getFields();
			for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
				if(fieldDeclaration.getVariables().get(0).getId().getName().equals(returnName)) {
					if(fieldDeclaration.getElementType().toString().equals(node.getElementType().toString())) {
						field = fieldDeclaration;
						break;
					}
				}
			}
		}
		
		if(field == null) {
			return null;
		}
		return returnName;
	}
	
	private static String predictSetter(MethodDeclaration node) {
		if(node.getParameters().size() == 0) {
			return null;
		}
		
		List<AssignExpr> assignExprs = new ArrayList<>();
		for(AssignExpr assignExpr : node.getBody().getNodesByType(AssignExpr.class)) {
			if(assignExpr.getParentNodeOfType(MethodDeclaration.class).equals(node)) {
				assignExprs.add(assignExpr);
			}
		}
		if(assignExprs.size() == 0) {
			return null;
		}
		if(assignExprs.size() > 1) {
			return null;
		}
		
		AssignExpr assignExpr = assignExprs.get(0);
		Expression targetExpr = assignExpr.getTarget();
		Expression valueExpr = assignExpr.getValue();
		
		String rightName = "";
		if(valueExpr instanceof NameExpr) {
			rightName = ((NameExpr)valueExpr).getName();
		}else {
			return null;
		}
		
		List<String> parameters = new ArrayList<>();
		for(Parameter parameter : node.getParameters()) {
			parameters.add(parameter.getId().getName());
		}
		if(!parameters.contains(rightName)) {
			return null;
		}
		
		String leftName = "";
		if(targetExpr instanceof FieldAccessExpr) {
			leftName = ((FieldAccessExpr)targetExpr).getField();
		}else if(targetExpr instanceof NameExpr) {
			leftName = ((NameExpr)targetExpr).getName();
		}else {
			return null;
		}
		
		FieldDeclaration field = null;
		ClassOrInterfaceDeclaration parent = node.getParentNodeOfType(ClassOrInterfaceDeclaration.class);
		if(parent != null) {
			List<FieldDeclaration> fieldDeclarations = parent.getFields();
			for(FieldDeclaration fieldDeclaration : fieldDeclarations) {
				if(fieldDeclaration.getVariables().get(0).getId().getName().equals(leftName)) {
					field = fieldDeclaration;
					break;
				}
			}
		}
		
		if(field == null) {
			return null;
		}
		return leftName;
	}
	
}
