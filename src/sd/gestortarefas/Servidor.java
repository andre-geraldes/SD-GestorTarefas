package sd.gestortarefas;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static int CODIGO=1;
    public static int PORTA=9999;
    
    public static final String REGISTAR = "RegistarUser";
    public static final String NOME_USER = "NomeUser";
    public static final String PW_USER = "PwUser";
    public static final String ENTRAR = "Entrar";
	
    public static final String OBJETO = "Objeto";
    public static final String QUANTIDADE = "Quantidade objeto";
    public static final String ABASTECER = "Abastecer armazém";

    public static final String ID = "ID Tarefa";
    public static final String NOME_TAREFA = "Nome tarefa";
    public static final String INICIAR = "Iniciar tarefa";
    public static final String CRIAR_TAREFA = "Criar Tarefa";
    public static final String TIPO_TAREFA = "Tipo Tarefa";
    public static final String ESTADO_TAREFA = "Estado tarefa";
    public static final String MATERIAL_TAREFA = "MaterialTarefa";
    public static final String CONCLUIR_TAREFA = "ConcluirTarefa";
    
    public static final String LISTATIPOSTAREFAS = "ListaTarefasConcluidas";

    public static final String STOCK = "Stock";
    public static final String VER_STOCK = "Ver Stock";

    public static final String SAIR = "Sair Gestor de Tarefas";
	
    private static Armazem k = null;
	
    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {
        ServerSocket sv = new ServerSocket(PORTA);
        k = new Armazem();
        
        k.registaUtilizador("admin", "admin");     
        
        while(true) {
            Socket cliente = sv.accept();
            System.out.println("Entrou no servidor\nIP: "+cliente.getInetAddress());
            
            Handler thread = new Handler(cliente,k);
            thread.start();
        }
    }  
}
