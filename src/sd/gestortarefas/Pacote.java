package sd.gestortarefas;

import java.io.Serializable;
import java.util.HashMap;

public class Pacote implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private String accao;
    private HashMap<String,String> argumentos; //chave NomeUser

    public Pacote(String a, HashMap<String,String> args) {
        this.accao = a;
        this.argumentos = args;
    }

    public String getAccao() {
        return accao;
    }

    public HashMap<String, String> getArgumentos() {
        return argumentos;
    }
}


