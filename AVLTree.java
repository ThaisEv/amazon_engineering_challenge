

public class AVLTree<T extends Comparable<T>> implements ITree<T> {
    private Node<T> root;
    private long rotationCount;

    private Node<T> insert(T data, Node<T> node) {
        if(node == null) {
            return new Node<>(data);
        }
        if(data.compareTo(node.getData()) < 0) {
            node.setLeftChild(insert(data, node.getLeftChild()));
        } else if(data.compareTo(node.getData()) > 0) {
            node.setRightChild(insert(data, node.getRightChild()));
        } else {
            return node; // Duplicate values are not allowed
        }
        updateHeight(node);
        return applyRotations(node);
    }

    @Override 
    public ITree<T> insert(T data) {
        root = insert(data, root);
        return this;
    }

    private T getMax(Node<T> node) {
        while(node.getRightChild() != null) {
            node = node.getRightChild();
        }
        return node.getData();
    }

    private Node<T> remove(T data, Node<T> node) {
        if(node == null) { return null; }
        int comparison = data.compareTo(node.getData());

        if(comparison < 0) {
            node.setLeftChild(remove(data, node.getLeftChild()));
        } else if(comparison > 0) {
            node.setRightChild(remove(data, node.getRightChild()));
        } else {
            if(node.getLeftChild() == null) {
                return node.getRightChild();
            }
            if(node.getRightChild() == null) {
                return node.getLeftChild();
            }

            node.setData(getMax(node.getLeftChild()));
            node.setLeftChild(remove(node.getData(), node.getLeftChild()));
        }

        updateHeight(node);
        return applyRotations(node);
    }

    @Override 
    public ITree<T> remove(T data) {
        root = remove(data, root);
        return this;
    }

    @Override
    public int height() {
        return height(root);
    }

    @Override
    public long rotationCount() {
        return rotationCount;
    }

    @Override
    public void resetMetrics() {
        rotationCount = 0;
    }

    private int height(Node<T> node) {
        return node == null ? 0 : node.getHeight();
    }

    private void updateHeight(Node<T> node) {
        int maxHeight = Math.max(
            height(node.getLeftChild()), 
            height(node.getRightChild())
        );
        node.setHeight(maxHeight + 1);
    }

    private int balance(Node<T> node) {
        return node == null ? 0 : height(node.getLeftChild()) - height(node.getRightChild());
    }

    private Node<T> rotateLeft(Node<T> node) {
        rotationCount++;
        Node<T> rightNode = node.getRightChild();
        Node<T> leftNode = rightNode.getLeftChild();
        rightNode.setLeftChild(node);
        node.setRightChild(leftNode);
        updateHeight(node);
        updateHeight(rightNode);
        return rightNode;
    }

    private Node<T> rotateRight(Node<T> node) {
        rotationCount++;
        Node<T> leftNode = node.getLeftChild();
        Node<T> rightNode = leftNode.getRightChild();
        leftNode.setRightChild(node);
        node.setLeftChild(rightNode);
        updateHeight(node);
        updateHeight(leftNode);
        return leftNode;
    }

    private Node<T> applyRotations(Node<T> node) {
        int balance = balance(node);
        if(balance > 1) {
            if(balance(node.getLeftChild()) < 0) {
                node.setLeftChild(rotateLeft(node.getLeftChild()));
            }
            node = rotateRight(node);
        } else if(balance < -1) {
            if(balance(node.getRightChild()) > 0) {
                node.setRightChild(rotateRight(node.getRightChild()));
            }
            node = rotateLeft(node);
        }
        return node;
    }

    @Override
    public boolean search(T data) {
        Node<T> current = root;
        while(current != null) {
            if(data.compareTo(current.getData()) < 0) {
                current = current.getLeftChild();
            } else if(data.compareTo(current.getData()) > 0) {
                current = current.getRightChild();
            } else {
                return true; // Found
            }
        }
        return false; // Not found
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public void printInOrder() {
        printInOrder(root);
        System.out.println();
    }

    private void printInOrder(Node<T> node) {
        if(node == null) {
            return;
        }
        printInOrder(node.getLeftChild());
        System.out.print(node.getData() + " ");
        printInOrder(node.getRightChild());
    }

    @Override
    public void printTree() {
        System.out.println("ROOT: " + nodeDescription(root));
        printTree(root, "");
    }

    @Override
    public TreeSnapshot snapshot() {
        return snapshot(root);
    }

    private TreeSnapshot snapshot(Node<T> node) {
        if(node == null) {
            return null;
        }
        return new TreeSnapshot(
            node.getData().toString(),
            "h: " + node.getHeight(),
            false,
            snapshot(node.getLeftChild()),
            snapshot(node.getRightChild())
        );
    }

    private void printTree(Node<T> node, String prefix) {
        if(node == null) {
            return;
        }
        if(node.getLeftChild() != null) {
            System.out.println(prefix + "|-- L: " + nodeDescription(node.getLeftChild()));
            printTree(node.getLeftChild(), prefix + "|   ");
        }
        if(node.getRightChild() != null) {
            System.out.println(prefix + "`-- R: " + nodeDescription(node.getRightChild()));
            printTree(node.getRightChild(), prefix + "    ");
        }
    }

    private String nodeDescription(Node<T> node) {
        return node == null ? "(vazia)" : node.getData() + " [altura=" + node.getHeight() + "]";
    }
}
