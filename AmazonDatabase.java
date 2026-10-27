import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Fonte de dados em memória usada pelos testes de desempenho.
 * A ordem de inserção é preservada para reproduzir cada cenário.
 */
public class AmazonDatabase {
    private final Set<String> packageIds = new LinkedHashSet<>();

    public void register(String packageId) {
        packageIds.add(packageId);
    }

    public boolean contains(String packageId) {
        return packageIds.contains(packageId);
    }

    public boolean remove(String packageId) {
        return packageIds.remove(packageId);
    }

    public List<String> all() {
        return new ArrayList<>(packageIds);
    }

    public int size() {
        return packageIds.size();
    }
}
