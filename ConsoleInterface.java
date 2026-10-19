import java.util.Scanner;

public class ConsoleInterface {
    private final Scanner scanner;
    private ITree<Integer> tree;
    private String treeName;

    public ConsoleInterface() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        printHeader();
        chooseTree();
        if(tree == null) {
            scanner.close();
            return;
        }

        boolean running = true;
        while(running) {
            printMenu();
            switch(readInteger("Opção: ")) {
                case 1:
                    insert();
                    break;
                case 2:
                    remove();
                    break;
                case 3:
                    search();
                    break;
                case 4:
                    printInOrder();
                    break;
                case 5:
                    printTree();
                    break;
                case 6:
                    chooseTree();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    printMessage("Opção inválida.");
            }
        }
        scanner.close();
        printMessage("Programa encerrado.");
    }

    private void chooseTree() {
        System.out.println("\nEscolha a estrutura:");
        System.out.println("1 - Árvore AVL");
        System.out.println("2 - Árvore Rubro-Negra");

        int option = readInteger("Tipo de árvore: ");
        if(option == 1) {
            tree = new AVLTree<>();
            treeName = "AVL";
        } else if(option == 2) {
            tree = new RBTree<>();
            treeName = "Rubro-Negra";
        } else {
            printMessage("Tipo de árvore inválido.");
        }
    }

    private void printHeader() {
        System.out.println("==========================================");
        System.out.println("       SISTEMA DE ÁRVORES BALANCEADAS");
        System.out.println("==========================================");
    }

    private void printMenu() {
        System.out.println("\n------------------------------------------");
        System.out.println("Árvore atual: " + treeName);
        System.out.println("1 - Inserir elemento");
        System.out.println("2 - Remover elemento");
        System.out.println("3 - Buscar elemento");
        System.out.println("4 - Percurso em ordem crescente");
        System.out.println("5 - Visualizar estrutura");
        System.out.println("6 - Trocar tipo de árvore");
        System.out.println("0 - Sair");
        System.out.println("------------------------------------------");
    }

    private void insert() {
        int value = readInteger("Valor para inserir: ");
        if(tree.search(value)) {
            printMessage("Valor repetido. A inserção foi ignorada.");
            return;
        }
        tree.insert(value);
        printMessage("Valor inserido com sucesso.");
    }

    private void remove() {
        int value = readInteger("Valor para remover: ");
        if(!tree.search(value)) {
            printMessage("Valor inexistente. Nenhuma remoção foi realizada.");
            return;
        }
        tree.remove(value);
        printMessage("Valor removido com sucesso.");
    }

    private void search() {
        int value = readInteger("Valor para buscar: ");
        printMessage(tree.search(value)
            ? "Valor encontrado na árvore."
            : "Valor não encontrado na árvore.");
    }

    private void printInOrder() {
        System.out.print("Percurso em ordem: ");
        tree.printInOrder();
    }

    private void printTree() {
        System.out.println("\nEstrutura da árvore:");
        if(tree.isEmpty()) {
            printMessage("A árvore está vazia.");
            return;
        }
        tree.printTree();
        if(tree instanceof RBTree) {
            System.out.println("Legenda: [Vermelho] e [Preto] indicam as cores dos nós.");
        } else {
            System.out.println("Legenda: altura indica o número de níveis a partir do nó.");
        }
    }

    private int readInteger(String prompt) {
        System.out.print(prompt);
        while(!scanner.hasNextInt()) {
            printMessage("Digite um número inteiro válido.");
            scanner.next();
            System.out.print(prompt);
        }
        return scanner.nextInt();
    }

    private void printMessage(String message) {
        System.out.println("[Sistema] " + message);
    }
}
