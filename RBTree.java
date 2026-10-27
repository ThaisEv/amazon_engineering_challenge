public class RBTree<T extends Comparable<T>> implements ITree<T> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private final RBNode<T> nil = new RBNode<>(null, BLACK);
    private RBNode<T> root = nil;
    private long rotationCount;

    public RBTree() {
        nil.left = nil;
        nil.right = nil;
        nil.parent = nil;
    }

    @Override
    public ITree<T> insert(T data) {
        RBNode<T> parent = nil;
        RBNode<T> current = root;

        while(current != nil) {
            parent = current;
            int comparison = data.compareTo(current.data);
            if(comparison == 0) {
                return this;
            }
            current = comparison < 0 ? current.left : current.right;
        }

        RBNode<T> node = new RBNode<>(data, RED);
        node.left = nil;
        node.right = nil;
        node.parent = parent;
        if(parent == nil) {
            root = node;
        } else if(data.compareTo(parent.data) < 0) {
            parent.left = node;
        } else {
            parent.right = node;
        }
        fixAfterInsert(node);
        return this;
    }

    @Override
    public ITree<T> remove(T data) {
        RBNode<T> node = find(data);
        if(node == nil) {
            return this;
        }

        RBNode<T> replacement = node;
        boolean originalColor = replacement.color;
        RBNode<T> child;
        if(node.left == nil) {
            child = node.right;
            transplant(node, node.right);
        } else if(node.right == nil) {
            child = node.left;
            transplant(node, node.left);
        } else {
            replacement = minimum(node.right);
            originalColor = replacement.color;
            child = replacement.right;
            if(replacement.parent == node) {
                child.parent = replacement;
            } else {
                transplant(replacement, replacement.right);
                replacement.right = node.right;
                replacement.right.parent = replacement;
            }
            transplant(node, replacement);
            replacement.left = node.left;
            replacement.left.parent = replacement;
            replacement.color = node.color;
        }
        if(originalColor == BLACK) {
            fixAfterRemove(child);
        }
        return this;
    }

    @Override
    public boolean search(T data) {
        return find(data) != nil;
    }

    @Override
    public boolean isEmpty() {
        return root == nil;
    }

    @Override
    public int height() {
        return height(root);
    }

    private int height(RBNode<T> node) {
        if(node == nil) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    @Override
    public long rotationCount() {
        return rotationCount;
    }

    @Override
    public void resetMetrics() {
        rotationCount = 0;
    }

    @Override
    public void printInOrder() {
        printInOrder(root);
        System.out.println();
    }

    private void printInOrder(RBNode<T> node) {
        if(node == nil) {
            return;
        }
        printInOrder(node.left);
        System.out.print(node.data + "(" + colorName(node) + ") ");
        printInOrder(node.right);
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

    private TreeSnapshot snapshot(RBNode<T> node) {
        if(node == nil) {
            return null;
        }
        return new TreeSnapshot(
            node.data.toString(),
            node.color == RED,
            snapshot(node.left),
            snapshot(node.right)
        );
    }

    private void printTree(RBNode<T> node, String prefix) {
        if(node == nil) {
            return;
        }
        if(node.left != nil) {
            System.out.println(prefix + "|-- L: " + nodeDescription(node.left));
            printTree(node.left, prefix + "|   ");
        }
        if(node.right != nil) {
            System.out.println(prefix + "`-- R: " + nodeDescription(node.right));
            printTree(node.right, prefix + "    ");
        }
    }

    private String colorName(RBNode<T> node) {
        return node.color == RED ? "Vermelho" : "Preto";
    }

    private String nodeDescription(RBNode<T> node) {
        return node == nil ? "(vazia)" : node.data + " [" + colorName(node) + "]";
    }

    private RBNode<T> find(T data) {
        RBNode<T> current = root;
        while(current != nil) {
            int comparison = data.compareTo(current.data);
            if(comparison == 0) {
                return current;
            }
            current = comparison < 0 ? current.left : current.right;
        }
        return nil;
    }

    private RBNode<T> minimum(RBNode<T> node) {
        while(node.left != nil) {
            node = node.left;
        }
        return node;
    }

    private void rotateLeft(RBNode<T> node) {
        rotationCount++;
        RBNode<T> right = node.right;
        node.right = right.left;
        if(right.left != nil) {
            right.left.parent = node;
        }
        right.parent = node.parent;
        if(node.parent == nil) {
            root = right;
        } else if(node == node.parent.left) {
            node.parent.left = right;
        } else {
            node.parent.right = right;
        }
        right.left = node;
        node.parent = right;
    }

    private void rotateRight(RBNode<T> node) {
        rotationCount++;
        RBNode<T> left = node.left;
        node.left = left.right;
        if(left.right != nil) {
            left.right.parent = node;
        }
        left.parent = node.parent;
        if(node.parent == nil) {
            root = left;
        } else if(node == node.parent.right) {
            node.parent.right = left;
        } else {
            node.parent.left = left;
        }
        left.right = node;
        node.parent = left;
    }

    private void fixAfterInsert(RBNode<T> node) {
        while(node.parent.color == RED) {
            if(node.parent == node.parent.parent.left) {
                RBNode<T> uncle = node.parent.parent.right;
                if(uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if(node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                RBNode<T> uncle = node.parent.parent.left;
                if(uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if(node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = BLACK;
        root.parent = nil;
    }

    private void transplant(RBNode<T> oldNode, RBNode<T> newNode) {
        if(oldNode.parent == nil) {
            root = newNode;
        } else if(oldNode == oldNode.parent.left) {
            oldNode.parent.left = newNode;
        } else {
            oldNode.parent.right = newNode;
        }
        newNode.parent = oldNode.parent;
    }

    private void fixAfterRemove(RBNode<T> node) {
        while(node != root && node.color == BLACK) {
            if(node == node.parent.left) {
                RBNode<T> sibling = node.parent.right;
                if(sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }
                if(sibling.left.color == BLACK && sibling.right.color == BLACK) {
                    if(sibling != nil) {
                        sibling.color = RED;
                    }
                    node = node.parent;
                } else {
                    if(sibling.right.color == BLACK) {
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.right.color = BLACK;
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                RBNode<T> sibling = node.parent.left;
                if(sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }
                if(sibling.right.color == BLACK && sibling.left.color == BLACK) {
                    if(sibling != nil) {
                        sibling.color = RED;
                    }
                    node = node.parent;
                } else {
                    if(sibling.left.color == BLACK) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.left.color = BLACK;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        node.color = BLACK;
    }

    private static class RBNode<T> {
        private T data;
        private boolean color;
        private RBNode<T> left;
        private RBNode<T> right;
        private RBNode<T> parent;

        private RBNode(T data, boolean color) {
            this.data = data;
            this.color = color;
        }
    }
}
