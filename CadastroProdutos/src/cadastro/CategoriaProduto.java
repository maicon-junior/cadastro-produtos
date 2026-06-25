package cadastro;

// categorias que um produto pode ter
public enum CategoriaProduto {
    ELETRONICO("Eletrônico"),
    ALIMENTO  ("Alimento"),
    VESTUARIO ("Vestuário"),
    LIVRO     ("Livro"),
    FERRAMENTA("Ferramenta"),
    OUTRO     ("Outro");

    // texto que aparece pro usuário (com acento)
    private final String descricao;

    CategoriaProduto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
