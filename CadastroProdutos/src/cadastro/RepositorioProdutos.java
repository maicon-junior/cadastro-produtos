package cadastro;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// todos os produtos + parte de ler o arquivo
public class RepositorioProdutos {
    private static final String SEPARADOR = ";";

    private final String caminhoArquivo;
    private final List<Produto> produtos;
    private long proximoId;

    public RepositorioProdutos(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
        this.produtos = new ArrayList<>();
        this.proximoId = 1;
    }

    // lê o csv e joga os produtos pra dentro da lista
    public void carregar() throws IOException {
        produtos.clear();
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            // ainda não tem arquivo, começa do zero
            proximoId = 1;
            return;
        }

        Scanner leitor = new Scanner(arquivo, "UTF-8");
        long maiorId = 0;
        boolean primeiraLinha = true;

        while (leitor.hasNextLine()) {
            String linha = leitor.nextLine();
            if (primeiraLinha) {
                // a primeira linha é só o cabeçalho, pula
                primeiraLinha = false;
                continue;
            }
            if (linha.trim().isEmpty()) continue;

            String[] partes = linha.split(SEPARADOR);
            if (partes.length < 5) continue;

            try {
                long id = Long.parseLong(partes[0].trim());
                String nome = partes[1].trim();
                CategoriaProduto categoria = CategoriaProduto.valueOf(partes[2].trim());
                double preco = Double.parseDouble(partes[3].trim());
                int quantidade = Integer.parseInt(partes[4].trim());

                produtos.add(new Produto(id, nome, categoria, preco, quantidade));
                // guarda o maior id pra saber por onde continuar
                if (id > maiorId) {
                    maiorId = id;
                }
            } catch (Exception e) {
                // linha estranha, melhor ignorar do que quebrar
            }
        }

        leitor.close();
        proximoId = maiorId + 1;
    }

    public Produto cadastrar(String nome, CategoriaProduto categoria,
                             double preco, int quantidade) {
        Produto novo = new Produto(proximoId, nome, categoria, preco, quantidade);
        proximoId++;
        produtos.add(novo);
        return novo;
    }

    public boolean remover(long id) {
        Produto encontrado = buscarPorId(id);
        if (encontrado != null) {
            produtos.remove(encontrado);
            return true;
        }
        return false;
    }

    public Produto buscarPorId(long id) {
        for (Produto p : produtos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Produto> buscarPorNome(String termo) {
        List<Produto> resultado = new ArrayList<>();
        String t = termo.toLowerCase();
        for (Produto p : produtos) {
            // deixa tudo minúsculo pra busca não diferenciar maiúscula
            if (p.getNome().toLowerCase().contains(t)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    public List<Produto> listarPorCategoria(CategoriaProduto categoria) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getCategoria() == categoria) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // ordena pelo nome, de A a Z
    public List<Produto> listarOrdenadoPorNome() {
        List<Produto> lista = new ArrayList<>(produtos);
        Collections.sort(lista, new Comparator<Produto>() {
            public int compare(Produto a, Produto b) {
                return a.getNome().compareToIgnoreCase(b.getNome());
            }
        });
        return lista;
    }

    // ordena do mais barato pro mais caro
    public List<Produto> listarOrdenadoPorPreco() {
        List<Produto> lista = new ArrayList<>(produtos);
        Collections.sort(lista, new Comparator<Produto>() {
            public int compare(Produto a, Produto b) {
                return Double.compare(a.getPreco(), b.getPreco());
            }
        });
        return lista;
    }

    // ordena de quem tem menos estoque pra quem tem mais
    public List<Produto> listarOrdenadoPorQuantidade() {
        List<Produto> lista = new ArrayList<>(produtos);
        Collections.sort(lista, new Comparator<Produto>() {
            public int compare(Produto a, Produto b) {
                return Integer.compare(a.getQuantidade(), b.getQuantidade());
            }
        });
        return lista;
    }

    // produtos que estão acabando (quantidade <= limite)
    public List<Produto> listarComEstoqueBaixo(int limite) {
        List<Produto> resultado = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getQuantidade() <= limite) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // conta quantos produtos tem em cada categoria
    public Map<CategoriaProduto, Integer> contarPorCategoria() {
        Map<CategoriaProduto, Integer> contagem = new HashMap<>();
        // começa todo mundo zerado
        for (CategoriaProduto cat : CategoriaProduto.values()) {
            contagem.put(cat, 0);
        }
        for (Produto p : produtos) {
            CategoriaProduto cat = p.getCategoria();
            contagem.put(cat, contagem.get(cat) + 1);
        }
        return contagem;
    }

    // soma o valor de estoque de todos os produtos
    public double calcularValorTotalEstoque() {
        double total = 0;
        for (Produto p : produtos) {
            total = total + p.getValorTotalEstoque();
        }
        return total;
    }

    public Produto produtoMaisCaro() {
        if (produtos.isEmpty()) return null;
        Produto maisCaro = produtos.get(0);
        for (Produto p : produtos) {
            if (p.getPreco() > maisCaro.getPreco()) {
                maisCaro = p;
            }
        }
        return maisCaro;
    }

    public Produto produtoMaisBarato() {
        if (produtos.isEmpty()) return null;
        Produto maisBarato = produtos.get(0);
        for (Produto p : produtos) {
            if (p.getPreco() < maisBarato.getPreco()) {
                maisBarato = p;
            }
        }
        return maisBarato;
    }
}
