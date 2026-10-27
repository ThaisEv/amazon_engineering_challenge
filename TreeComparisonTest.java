import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Testes executáveis sem dependências externas.
 *
 * Execute com:
 * java -cp "bin;lib/lombok.jar" TreeComparisonTest
 */
public class TreeComparisonTest {
    private static final int DATASET_SIZE = 1_000_000;

    public static void main(String[] args) {
        TreeComparisonTest test = new TreeComparisonTest();
        test.runAllScenarios();
    }

    private void runAllScenarios() {
        System.out.println("=== Comparação AVL x Rubro-Negra ===");
        System.out.println("Itens por cenário: " + DATASET_SIZE);
        System.out.println("Cenário                         Estrutura       BD lido  Altura  Rotações  Resultado");

        runScenario("Crescente", ascendingIds(), Collections.<String>emptyList());
        runScenario("Decrescente", descendingIds(), Collections.<String>emptyList());
        runScenario("Aleatório (seed 39)", randomIds(), Collections.<String>emptyList());
        runScenario("Misto (remove pares)", ascendingIds(), evenIds());

        System.out.println();
        System.out.println("Conclusão: a AVL tende a apresentar menor altura; a Rubro-Negra");
        System.out.println("tende a exigir menos rebalanceamentos em cargas com inserções e remoções.");
        System.out.println("Para este cenário de localização com alta carga de leitura, recomenda-se AVL.");
    }

    private void runScenario(String name, List<String> insertions, List<String> removals) {
        AmazonDatabase database = new AmazonDatabase();
        for(String packageId : insertions) {
            database.register(packageId);
        }

        Result avl = execute(new AVLTree<String>(), database.all(), removals);
        Result redBlack = execute(new RBTree<String>(), database.all(), removals);

        printResult(name, "AVL", avl);
        printResult(name, "Rubro-Negra", redBlack);
        printChart(name, avl, redBlack);
    }

    private Result execute(ITree<String> tree, List<String> initialData,
                           List<String> removals) {
        AmazonDatabase database = new AmazonDatabase();
        int databaseReadSize = initialData.size();
        for(String packageId : initialData) {
            database.register(packageId);
        }
        tree.resetMetrics();
        for(String packageId : database.all()) {
            tree.insert(packageId);
        }
        assertContainsAll(tree, database.all());

        for(String packageId : removals) {
            if(!database.remove(packageId)) {
                throw new AssertionError("O banco falso não continha " + packageId);
            }
            tree.remove(packageId);
            if(tree.search(packageId)) {
                throw new AssertionError("Pacote removido ainda foi encontrado: " + packageId);
            }
        }
        assertContainsAll(tree, database.all());
        return new Result(tree.height(), tree.rotationCount(), databaseReadSize, database.size());
    }

    private void assertContainsAll(ITree<String> tree, List<String> ids) {
        for(String id : ids) {
            if(!tree.search(id)) {
                throw new AssertionError("Pacote não localizado: " + id);
            }
        }
    }

    private void printResult(String scenario, String treeName, Result result) {
        System.out.printf("%-32s %-15s %7d %7d %9d  OK (%d restantes)%n",
            scenario, treeName, result.databaseReadSize, result.height,
            result.rotations, result.items);
    }

    private void printChart(String scenario, Result avl, Result redBlack) {
        System.out.println("Gráfico ASCII - " + scenario);
        printBars("Altura", "AVL", avl.height, "Rubro-Negra", redBlack.height);
        printBars("Rotações", "AVL", avl.rotations, "Rubro-Negra", redBlack.rotations);
    }

    private void printBars(String metric, String firstName, long firstValue,
                           String secondName, long secondValue) {
        long maximum = Math.max(firstValue, secondValue);
        System.out.printf("  %-9s %-12s %5d %s%n", metric, firstName, firstValue,
            bar(firstValue, maximum));
        System.out.printf("  %-9s %-12s %5d %s%n", "", secondName, secondValue,
            bar(secondValue, maximum));
    }

    private String bar(long value, long maximum) {
        int length = maximum == 0 ? 0 : (int) Math.max(1, value * 30 / maximum);
        return "#".repeat(length);
    }

    private List<String> ascendingIds() {
        List<String> ids = new ArrayList<>();
        for(int i = 1; i <= DATASET_SIZE; i++) {
            ids.add(packageId(i));
        }
        return ids;
    }

    private List<String> descendingIds() {
        List<String> ids = ascendingIds();
        Collections.reverse(ids);
        return ids;
    }

    private List<String> randomIds() {
        List<String> ids = ascendingIds();
        Collections.shuffle(ids, new Random(39));
        return ids;
    }

    private List<String> evenIds() {
        List<String> ids = new ArrayList<>();
        for(int i = 2; i <= DATASET_SIZE; i += 2) {
            ids.add(packageId(i));
        }
        return ids;
    }

    private String packageId(int number) {
        return String.format("PKG-%04d", number);
    }

    private static class Result {
        private final int height;
        private final long rotations;
        private final int databaseReadSize;
        private final int items;

        private Result(int height, long rotations, int databaseReadSize, int items) {
            this.height = height;
            this.rotations = rotations;
            this.databaseReadSize = databaseReadSize;
            this.items = items;
        }
    }
}
