package cadastro;

import java.io.IOException;

// classe principal
public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "dados/produtos.csv";

        try {
            RepositorioProdutos repositorio = new RepositorioProdutos(caminhoArquivo);
            repositorio.carregar();

            MenuTerminal menu = new MenuTerminal(repositorio);
            menu.executar();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o sistema: " + e.getMessage());
        }
    }
}
