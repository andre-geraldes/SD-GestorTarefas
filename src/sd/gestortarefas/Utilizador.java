package sd.gestortarefas;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String nickname;	
	private String password;
        private TreeMap<Integer,Tarefa> listaTarefas; //<identificador,Tarefa>
	private boolean ativo;
        
        private Lock lock = new ReentrantLock();
        private Condition cond = lock.newCondition();

	public Utilizador() {
		this.nickname = "";
		this.password = "";
                this.listaTarefas = new TreeMap<Integer,Tarefa>();
		this.ativo = false;
	}

	public Utilizador(String nick, String pw) {
		this.nickname = nick;
		this.password = pw;
		this.listaTarefas = new TreeMap<Integer,Tarefa>();
                this.ativo = false;
	}

	public Utilizador(Utilizador c) {
		this.nickname = c.getNickname();
		this.password = c.getPassword();
		this.listaTarefas = c.getListaTarefas();
                this.ativo = c.isAtivo();
	}
	
        //Getters e Setters
	public boolean isAtivo() {
		return this.ativo;
	}
	
	public void setAtivo(boolean a) {
		this.ativo = a;
	} 

	public String getNickname() {
		return this.nickname;
	}

	public String getPassword() {
		return this.password;
	}
        
        public TreeMap<Integer,Tarefa> getListaTarefas() {
            return this.listaTarefas;
        }

	public Utilizador clone() {
		return new Utilizador(this);
	}
        
        /**
         * Devolve a lista de tarefas terminadas
         * @return 
         */
        public TreeMap<Integer,Tarefa> tarefasTermindadas() {
            TreeMap<Integer,Tarefa> res = new TreeMap<Integer,Tarefa>(); 
                    
            for(Tarefa t : this.listaTarefas.values()) {
                if(t.isTerminada()) {
                    res.put(t.getCodigo(),t.clone());
                }
            }
            return res;
        } 
        
        /**
         * Autenticacao do utilizador
         * @param n
         * @param p
         * @return 
         */
        public boolean Autenticacao(String n, String p) {
            return (this.nickname.equals(n) && this.password.equals(p) && !this.ativo);
        }
        
        /**
         * Adiciona uma tarefa
         * @param t 
         */
        public void addTarefa(Tarefa t) {
            if(!this.listaTarefas.containsKey(t.getCodigo()) && t.getUtilizador().equals(this.nickname)) {
                this.listaTarefas.put(t.getCodigo(),t);
            }
        }

        
	public String toString() {
                StringBuilder s = new StringBuilder("***Cliente***\n");
                s.append("Utilizador: " + this.getNickname());
		s.append("Password " + this.getPassword());
		return s.toString();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if ((o == null) || (o.getClass() != this.getClass()))
			return false;
		else {
			Utilizador c = (Utilizador) o;
			return this.getNickname().equals(c.getNickname());
		}
	}
        
        
}


