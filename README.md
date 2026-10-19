# Sistema de árvores

O programa permite escolher, na inicialização, entre uma árvore AVL e uma
árvore Rubro-Negra. Ambas oferecem inserção, remoção, busca, percurso em ordem
e impressão da estrutura.

As duas implementações ignoram valores repetidos, preservando um único nó por
valor. A remoção e a busca de valores inexistentes não alteram a árvore e
informam essa situação ao usuário. Essas decisões evitam ambiguidades no
percurso e mantêm a propriedade de árvore de busca binária.

A AVL recalcula alturas e aplica rotações simples ou duplas após inserções e
remoções. A Rubro-Negra usa um nó sentinela preto e os casos clássicos de
recolorização e rotação para inserção e remoção; a impressão identifica cada
nó como `Vermelho` ou `Preto`.

Para compilar e executar com o Lombok usado por `Node`:

```text
javac -cp "lib\lombok.jar" -processorpath "lib\lombok.jar" *.java
java -cp ".;lib\lombok.jar" MainTree
```

`MainTree` inicia a interface de console implementada em `ConsoleInterface`.
Durante a execução, a opção 6 permite trocar entre as duas estruturas. A
troca cria uma nova árvore vazia; os valores da estrutura anterior não são
copiados, evitando misturar estados de implementações diferentes.