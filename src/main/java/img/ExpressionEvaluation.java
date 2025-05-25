package img;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class ExpressionEvaluation {

    public int evaluate(String name, String variable, int level) {
        name = normalize(name);
        try {
            Expression expression = new ExpressionBuilder(name)
                    .variable(variable)
                    .functions(
                            new Function("u", 1) {
                                @Override
                                public double apply(double... args) {
                                    return Math.ceil(args[0]);
                                }
                            },
                            new Function("d", 1) {
                                @Override
                                public double apply(double... args) {
                                    return Math.floor(args[0]);
                                }
                            }
                    )
                    .build()
                    .setVariable(variable, level);

            // System.out.println("Evaluating: [" + name + "] Result: (" + content + " with " + variable + "=" + level + " = " + result + ")");
            return (int) expression.evaluate();
        } catch (Exception e) {
            System.err.println("Failed to evaluate: " + name + " with " + variable + "=" + level);
            return 0;
        }
    }

    private static String normalize(String expr) {
        // remove '=' prefix, it's a mistake and needs to be removed
        if (expr.startsWith("=")) {
            expr = expr.substring(1);
        }

        return expr.trim();
    }

}

