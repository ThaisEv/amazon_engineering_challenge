import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class TreeGraphicalInterface extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = Color.decode("#170202");
    private static final Color FOREGROUND = Color.WHITE;
    private static final Color AVL_NODE = Color.decode("#6D2C2C");
    private static final Color RED_NODE = Color.decode("#A83232");
    private static final Color BLACK_NODE = Color.decode("#303030");

    private ITree<Integer> tree;
    private final TreeCanvas canvas;
    private final JLabel status;
    private final JTextField valueField;
    private final JComboBox<String> treeSelector;

    public TreeGraphicalInterface() {
        super("Sistema de Arvores Balanceadas");
        tree = new AVLTree<>();
        canvas = new TreeCanvas();
        status = new JLabel("Arvore AVL pronta.");
        valueField = new JTextField(10);
        treeSelector = new JComboBox<>(new String[] {"AVL", "Rubro-Negra"});

        configureWindow();
        buildInterface();
        refreshView();
    }

    public void start() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 700);
        setLocationRelativeTo(null);
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBackground(BACKGROUND);
        controls.add(label("Tipo:"));
        controls.add(treeSelector);
        controls.add(label("Valor:"));
        controls.add(valueField);
        controls.add(button("Inserir", this::insert));
        controls.add(button("Remover", this::remove));
        controls.add(button("Buscar", this::search));
        controls.add(button("Limpar", event -> clearTree()));

        treeSelector.addActionListener(event -> changeTree());
        valueField.addActionListener(this::insert);

        status.setForeground(FOREGROUND);
        status.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JScrollPane scrollPane = new JScrollPane(canvas);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        root.add(controls, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(FOREGROUND);
        return label;
    }

    private JButton button(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    private void changeTree() {
        if("AVL".equals(treeSelector.getSelectedItem())) {
            tree = new AVLTree<>();
            status.setText("Arvore AVL selecionada. Estrutura reiniciada.");
        } else {
            tree = new RBTree<>();
            status.setText("Arvore Rubro-Negra selecionada. Estrutura reiniciada.");
        }
        refreshView();
    }

    private void insert(ActionEvent event) {
        Integer value = readValue();
        if(value == null) {
            return;
        }
        if(tree.search(value)) {
            status.setText("Valor repetido: insercao ignorada.");
        } else {
            tree.insert(value);
            status.setText("Valor " + value + " inserido.");
        }
        refreshView();
    }

    private void remove(ActionEvent event) {
        Integer value = readValue();
        if(value == null) {
            return;
        }
        if(!tree.search(value)) {
            status.setText("Valor inexistente: nenhuma remocao realizada.");
        } else {
            tree.remove(value);
            status.setText("Valor " + value + " removido.");
        }
        refreshView();
    }

    private void search(ActionEvent event) {
        Integer value = readValue();
        if(value == null) {
            return;
        }
        status.setText(tree.search(value)
            ? "Valor " + value + " encontrado."
            : "Valor " + value + " nao encontrado.");
    }

    private void clearTree() {
        tree = "AVL".equals(treeSelector.getSelectedItem())
            ? new AVLTree<>()
            : new RBTree<>();
        status.setText("Arvore limpa.");
        refreshView();
    }

    private Integer readValue() {
        try {
            return Integer.valueOf(valueField.getText().trim());
        } catch(NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Informe um numero inteiro valido.",
                "Entrada invalida",
                JOptionPane.WARNING_MESSAGE
            );
            valueField.requestFocusInWindow();
            return null;
        }
    }

    private void refreshView() {
        canvas.setSnapshot(tree.snapshot(), tree instanceof RBTree);
    }

    private class TreeCanvas extends JPanel {
        private static final long serialVersionUID = 1L;
        private TreeSnapshot snapshot;
        private boolean redBlack;

        private TreeCanvas() {
            setBackground(BACKGROUND);
            setPreferredSize(new Dimension(1000, 600));
        }

        private void setSnapshot(TreeSnapshot snapshot, boolean redBlack) {
            this.snapshot = snapshot;
            this.redBlack = redBlack;
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if(snapshot == null) {
                graphics.setColor(FOREGROUND);
                graphics.drawString("A arvore esta vazia.", 20, 30);
                return;
            }
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );
            int width = Math.max(getWidth(), getPreferredSize().width);
            drawNode(g, snapshot, width / 2, 55, width / 4, 0);
            g.dispose();
        }

        private void drawNode(
            Graphics2D g,
            TreeSnapshot node,
            int x,
            int y,
            int horizontalGap,
            int depth
        ) {
            final int radius = 28;
            final int verticalGap = 90;
            int childY = y + verticalGap;
            int nextGap = Math.max(horizontalGap / 2, radius + 10);

            if(node.getLeft() != null) {
                int childX = x - horizontalGap;
                drawEdge(g, x, y, childX, childY);
                drawNode(g, node.getLeft(), childX, childY, nextGap, depth + 1);
            }
            if(node.getRight() != null) {
                int childX = x + horizontalGap;
                drawEdge(g, x, y, childX, childY);
                drawNode(g, node.getRight(), childX, childY, nextGap, depth + 1);
            }

            g.setColor(redBlack ? (node.isRed() ? RED_NODE : BLACK_NODE) : AVL_NODE);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            g.setColor(FOREGROUND);
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            drawCenteredText(g, node.getLabel(), x, y);
            if(node.getAnnotation() != null) {
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                drawCenteredText(g, node.getAnnotation(), x, y - radius - 5);
            }
        }

        private void drawEdge(Graphics2D g, int x1, int y1, int x2, int y2) {
            g.setColor(FOREGROUND);
            g.drawLine(x1, y1 + 30, x2, y2 - 30);
        }

        private void drawCenteredText(Graphics2D g, String text, int x, int y) {
            int textWidth = g.getFontMetrics().stringWidth(text);
            int baseline = y - (g.getFontMetrics().getAscent()
                + g.getFontMetrics().getDescent()) / 2
                + g.getFontMetrics().getAscent();
            g.drawString(text, x - textWidth / 2, baseline);
        }
    }
}
