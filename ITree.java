public interface ITree<T extends Comparable<T>> {

    ITree<T> insert(T data);

    ITree<T> remove(T data);

    boolean search(T data);

    boolean isEmpty();

    int height();

    long rotationCount();

    void resetMetrics();

    void printInOrder();

    void printTree();

    TreeSnapshot snapshot();
}
