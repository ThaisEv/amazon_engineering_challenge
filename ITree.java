public interface ITree<T extends Comparable<T>> {

    ITree<T> insert(T data);

    ITree<T> remove(T data);

    boolean search(T data);

    boolean isEmpty();

    void printInOrder();

    void printTree();
}
