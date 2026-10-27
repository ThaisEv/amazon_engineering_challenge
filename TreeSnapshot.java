public class TreeSnapshot {
    private final String label;
    private final String annotation;
    private final boolean red;
    private final TreeSnapshot left;
    private final TreeSnapshot right;

    public TreeSnapshot(String label, boolean red, TreeSnapshot left, TreeSnapshot right) {
        this(label, null, red, left, right);
    }

    public TreeSnapshot(
        String label,
        String annotation,
        boolean red,
        TreeSnapshot left,
        TreeSnapshot right
    ) {
        this.label = label;
        this.annotation = annotation;
        this.red = red;
        this.left = left;
        this.right = right;
    }

    public String getLabel() {
        return label;
    }

    public String getAnnotation() {
        return annotation;
    }

    public boolean isRed() {
        return red;
    }

    public TreeSnapshot getLeft() {
        return left;
    }

    public TreeSnapshot getRight() {
        return right;
    }
}
