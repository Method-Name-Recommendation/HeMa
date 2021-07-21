package util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import main.FileParser;
import predictor.Signature;
import visitor.FunctionVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrainSet {
    private static final Map<Signature, Map<String, Integer>> data = new HashMap<>();

    public static void initialize(String dataDirectory) {
        // Initialize Directory
        File root = new File(dataDirectory);

        if (root.exists() && root.isDirectory()) {
            try {
                Files.walk(Paths.get(dataDirectory)).filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".java")).forEach(TrainSet::exploreClass);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void exploreClass(Path path) {
        // Initialize the code from class
        String code;
        try {
            code = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            code = "";
        }

        try {
            // Get method declarations from code
            CompilationUnit cu = FileParser.parseFileWithRetries(code);
            FunctionVisitor functionVisitor = new FunctionVisitor();

            functionVisitor.visit(cu, null);
            ArrayList<MethodDeclaration> nodes = functionVisitor.getMethodDeclarations();

            // Update data with new methods
            for (MethodDeclaration m : nodes) {
                Signature signature = new Signature(m);
                Map<String, Integer> signatureMap = data.getOrDefault(signature, new HashMap<>());

                String methodName = m.getName();
                int times = signatureMap.getOrDefault(methodName, 0) + 1;
                signatureMap.put(methodName, times);

                data.put(signature, signatureMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Signature, Map<String, Integer>> getData() {
        return data;
    }
}
