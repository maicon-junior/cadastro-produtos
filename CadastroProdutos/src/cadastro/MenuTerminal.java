package cadastro;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

// cuida de tudo que aparece na tela e do que o usuário digita
public class MenuTerminal {
    private final Scanner scanner;
    private final RepositorioProdutos repositorio;

    public MenuTerminal(RepositorioProdutos repositorio) {
        this.scanner = new Scanner(System.in);
        this.repositorio = repositorio;
    }

    public void executar() {
        exibirBanner();

        // fica repetindo o menu até o usuário escolher sair (0)
        boolean rodando = true;
        while (rodando) {
            int opcao = exibirMenu();
            switch (opcao) {
                case 1:
                    cadastrarProduto();
                    break;
                case 2:
                    listarTodos();
                    break;
                case 3:
                    buscarPorNome();
                    break;
                case 4:
                    listarPorCategoria();
                    break;
                case 5:
                    atualizarProduto();
                    break;
                case 6:
                    removerProduto();
                    break;
                case 7:
                    listarOrdenado();
                    break;
                case 8:
                    produtosEstoqueBaixo();
                    break;
                case 9:
                    exibirRelatorio();
                    break;
                case 0:
                    rodando = false;
                    System.out.println();
                    System.out.println("  Encerrando o sistema. Até mais!");
                    System.out.println();
                    break;
                default:
                    System.out.println();
                    System.out.println("  [!] Opção inválida. Tente novamente.");
            }
        }
    }

    private void exibirBanner() {
        System.out.println();
        System.out.println("  +==============================================+");
        System.out.println("  |                                              |");
        System.out.println("  |      SISTEMA DE CADASTRO DE PRODUTOS         |");
        System.out.println("  |                                              |");
        System.out.println("  +==============================================+");
    }

    private int exibirMenu() {
        System.out.println();
        System.out.println("  MENU PRINCIPAL");
        System.out.println("  ----------------------------------------");
        System.out.println("    [1] Cadastrar novo produto");
        System.out.println("    [2] Listar todos os produtos");
        System.out.println("    [3] Buscar produto por nome");
        System.out.println("    [4] Listar produtos por categoria");
        System.out.println("    [5] Atualizar produto");
        System.out.println("    [6] Remover produto");
        System.out.println("    [7] Listar produtos ordenados");
        System.out.println("    [8] Produtos com estoque baixo");
        System.out.println("    [9] Relatório de estatísticas");
        System.out.println("    [0] Sair");
        System.out.println();
        System.out.print("  Escolha: ");
        return lerInteiro();
    }

    private void cadastrarProduto() {
        cabecalho("NOVO PRODUTO");

        System.out.print("  Nome: ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) {
            System.out.println("  [!] Nome não pode ser vazio.");
            return;
        }

        CategoriaProduto categoria = escolherCategoria();
        if (categoria == null) return;

        System.out.print("  Preço: ");
        double preco = lerDouble();
        if (preco < 0) {
            System.out.println("  [!] Preço inválido.");
            return;
        }

        System.out.print("  Quantidade: ");
        int quantidade = lerInteiro();
        if (quantidade < 0) {
            System.out.println("  [!] Quantidade inválida.");
            return;
        }

        Produto novo = repositorio.cadastrar(nome, categoria, preco, quantidade);
        System.out.println();
        System.out.println("  [OK] Produto cadastrado com ID " + novo.getId() + ".");
    }

    private void listarTodos() {
        List<Produto> lista = repositorio.listarTodos();
        exibirLista(lista, "TODOS OS PRODUTOS");
    }

    private void buscarPorNome() {
        cabecalho("BUSCAR POR NOME");
        System.out.print("  Digite parte do nome: ");
        String termo = scanner.nextLine().trim();
        List<Produto> lista = repositorio.buscarPorNome(termo);
        exibirLista(lista, "RESULTADOS PARA \"" + termo + "\"");
    }

    private void listarPorCategoria() {
        cabecalho("LISTAR POR CATEGORIA");
        CategoriaProduto categoria = escolherCategoria();
        if (categoria == null) return;
        List<Produto> lista = repositorio.listarPorCategoria(categoria);
        exibirLista(lista, "CATEGORIA: " + categoria.getDescricao().toUpperCase());
    }

    private void atualizarProduto() {
        cabecalho("ATUALIZAR PRODUTO");
        System.out.print("  ID do produto: ");
        long id = lerLong();

        Produto p = repositorio.buscarPorId(id);
        if (p == null) {
            System.out.println("  [!] Produto não encontrado.");
            return;
        }

        System.out.println();
        System.out.println("  Produto atual:");
        exibirProduto(p);
        System.out.println();
        System.out.println("  (deixe em branco para manter o valor atual)");
        System.out.println();

        System.out.print("  Novo nome [" + p.getNome() + "]: ");
        String nome = scanner.nextLine().trim();
        if (!nome.isEmpty()) {
            p.setNome(nome);
        }

        System.out.print("  Trocar categoria? (s/n): ");
        String resposta = scanner.nextLine().trim();
        if (resposta.equalsIgnoreCase("s")) {
            CategoriaProduto categoria = escolherCategoria();
            if (categoria != null) {
                p.setCategoria(categoria);
            }
        }

        System.out.print("  Novo preço [R$ " + formatarPreco(p.getPreco()) + "]: ");
        String precoStr = scanner.nextLine().trim();
        if (!precoStr.isEmpty()) {
            try {
                double novoPreco = Double.parseDouble(precoStr.replace(",", "."));
                if (novoPreco >= 0) {
                    p.setPreco(novoPreco);
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Preço inválido, mantido o anterior.");
            }
        }

        System.out.print("  Nova quantidade [" + p.getQuantidade() + "]: ");
        String qtdStr = scanner.nextLine().trim();
        if (!qtdStr.isEmpty()) {
            try {
                int novaQtd = Integer.parseInt(qtdStr);
                if (novaQtd >= 0) {
                    p.setQuantidade(novaQtd);
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Quantidade inválida, mantida a anterior.");
            }
        }

        System.out.println();
        System.out.println("  [OK] Produto atualizado.");
    }

    private void removerProduto() {
        cabecalho("REMOVER PRODUTO");
        System.out.print("  ID do produto: ");
        long id = lerLong();

        Produto p = repositorio.buscarPorId(id);
        if (p == null) {
            System.out.println("  [!] Produto não encontrado.");
            return;
        }

        System.out.println();
        System.out.println("  Produto a remover:");
        exibirProduto(p);
        System.out.println();
        System.out.print("  Confirma remoção? (s/n): ");
        String resposta = scanner.nextLine().trim();
        if (resposta.equalsIgnoreCase("s")) {
            repositorio.remover(id);
            System.out.println();
            System.out.println("  [OK] Produto removido.");
        } else {
            System.out.println();
            System.out.println("  Remoção cancelada.");
        }
    }

    private void listarOrdenado() {
        cabecalho("LISTAR ORDENADO");
        System.out.println("  Ordenar por:");
        System.out.println("    [1] Nome");
        System.out.println("    [2] Preço (menor para maior)");
        System.out.println("    [3] Quantidade (menor para maior)");
        System.out.println();
        System.out.print("  Escolha: ");
        int op = lerInteiro();

        List<Produto> lista;
        String titulo;

        if (op == 1) {
            lista = repositorio.listarOrdenadoPorNome();
            titulo = "PRODUTOS ORDENADOS POR NOME";
        } else if (op == 2) {
            lista = repositorio.listarOrdenadoPorPreco();
            titulo = "PRODUTOS ORDENADOS POR PREÇO";
        } else if (op == 3) {
            lista = repositorio.listarOrdenadoPorQuantidade();
            titulo = "PRODUTOS ORDENADOS POR QUANTIDADE";
        } else {
            System.out.println("  [!] Opção inválida.");
            return;
        }

        exibirLista(lista, titulo);
    }

    private void produtosEstoqueBaixo() {
        cabecalho("ESTOQUE BAIXO");
        System.out.print("  Limite (Enter para usar 5): ");
        String entrada = scanner.nextLine().trim();
        int limite = 5;
        if (!entrada.isEmpty()) {
            try {
                limite = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Valor inválido, usando 5.");
            }
        }
        List<Produto> lista = repositorio.listarComEstoqueBaixo(limite);
        exibirLista(lista, "PRODUTOS COM ESTOQUE <= " + limite);
    }

    private void exibirRelatorio() {
        cabecalho("RELATÓRIO DE ESTATÍSTICAS");

        List<Produto> todos = repositorio.listarTodos();
        System.out.println("  Total de produtos cadastrados: " + todos.size());
        System.out.println("  Valor total em estoque:        R$ "
            + formatarPreco(repositorio.calcularValorTotalEstoque()));

        System.out.println();

        Produto maisCaro = repositorio.produtoMaisCaro();
        Produto maisBarato = repositorio.produtoMaisBarato();
        if (maisCaro != null) {
            System.out.println("  Produto mais caro:   " + maisCaro.getNome()
                + " (R$ " + formatarPreco(maisCaro.getPreco()) + ")");
        }
        if (maisBarato != null) {
            System.out.println("  Produto mais barato: " + maisBarato.getNome()
                + " (R$ " + formatarPreco(maisBarato.getPreco()) + ")");
        }

        System.out.println();
        System.out.println("  Contagem por categoria:");
        Map<CategoriaProduto, Integer> contagem = repositorio.contarPorCategoria();
        for (CategoriaProduto cat : CategoriaProduto.values()) {
            System.out.println("    - " + cat.getDescricao() + ": " + contagem.get(cat));
        }
    }

    // mostra as categorias numeradas e devolve a que o usuário escolheu
    private CategoriaProduto escolherCategoria() {
        System.out.println();
        System.out.println("  Categorias:");
        CategoriaProduto[] valores = CategoriaProduto.values();
        for (int i = 0; i < valores.length; i++) {
            System.out.println("    [" + (i + 1) + "] " + valores[i].getDescricao());
        }
        System.out.println();
        System.out.print("  Escolha: ");
        int escolha = lerInteiro();
        if (escolha < 1 || escolha > valores.length) {
            System.out.println("  [!] Categoria inválida.");
            return null;
        }
        return valores[escolha - 1];
    }

    private void exibirLista(List<Produto> lista, String titulo) {
        cabecalho(titulo);
        if (lista.isEmpty()) {
            System.out.println("  Nenhum produto encontrado.");
            return;
        }
        for (Produto p : lista) {
            exibirProduto(p);
            System.out.println();
        }
        System.out.println("  ----------------------------------------");
        System.out.println("  Total: " + lista.size() + " produto(s)");
    }

    // mostra um produto em duas linhas pra ficar mais organizado
    private void exibirProduto(Produto p) {
        System.out.println("  [ID " + p.getId() + "] " + p.getNome());
        System.out.println("         Categoria: " + p.getCategoria().getDescricao()
            + "  |  Preço: R$ " + formatarPreco(p.getPreco())
            + "  |  Estoque: " + p.getQuantidade());
    }

    // título de cada tela, com as barras em cima e embaixo
    private void cabecalho(String titulo) {
        System.out.println();
        System.out.println("  ================================================");
        System.out.println("    " + titulo);
        System.out.println("  ================================================");
        System.out.println();
    }

    // deixa o preço com 2 casas (ex: 1234,56)
    // truque: multiplica por 100, arredonda e separa os centavos
    private String formatarPreco(double preco) {
        long centavos = Math.round(preco * 100);
        long parteInteira = centavos / 100;
        long parteDecimal = centavos % 100;
        String dec;
        if (parteDecimal < 10) {
            dec = "0" + parteDecimal;
        } else {
            dec = "" + parteDecimal;
        }
        return parteInteira + "," + dec;
    }

    // esses 3 métodos leem número do teclado; se vier algo inválido devolve -1
    private int lerInteiro() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private long lerLong() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double lerDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
