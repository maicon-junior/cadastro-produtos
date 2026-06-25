package cadastro;

// um produto do nosso estoque
public class Produto {
    // o id é final porque depois de criado não muda mais
    private final long id;
    private String nome;
    private CategoriaProduto categoria;
    private double preco;
    private int quantidade;

    public Produto(long id, String nome, CategoriaProduto categoria,
                   double preco, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public CategoriaProduto getCategoria() {
        return categoria;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCategoria(CategoriaProduto categoria) {
        this.categoria = categoria;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    // quanto vale tudo desse produto no estoque (preço x quantidade)
    public double getValorTotalEstoque() {
        return preco * quantidade;
    }
}
