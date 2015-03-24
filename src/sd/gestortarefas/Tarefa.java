package sd.gestortarefas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class Tarefa implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int codigo;
	private String user; 
	private String nome;
	private String tipo;
        private String estado; //criada, em execucao ou terminada
	private HashMap<String,Integer> materialNecessario; //inalterável
	private boolean terminada;
	private TreeSet<Abastecimento> ofertas;

	public Tarefa() {
		this.codigo = 0;
		this.nome = "";
		this.tipo = "";
                this.estado = "criada";
		this.materialNecessario = new HashMap<>();
		this.terminada = false;
	}

	public Tarefa(String n, String d, String u) {
		this.codigo = Servidor.CODIGO;
		this.user = u;
		this.nome = n;
		this.tipo = d;
                this.estado = "criada";
		this.materialNecessario = new HashMap<>();
		this.terminada = false;
		this.ofertas = new TreeSet<>();
		Servidor.CODIGO++;
	}

	public Tarefa(Tarefa p) {
		this.codigo = p.getCodigo();
		this.nome = p.getNome();
		this.tipo = p.getTipo();
                this.estado = p.getEstado();
		this.materialNecessario = p.getMaterialNecessario();
		this.terminada = p.isTerminada();
	} 
        
	public int getCodigo() {
		return this.codigo;
	}
	
	public String getUtilizador() {
		return this.user;
	}

	public String getNome() {
		return this.nome;
	}

	public String getTipo() {
		return this.tipo;
	}
        
        public HashMap<String,Integer> getMaterialNecessario() {
            return this.materialNecessario;
        }
        
        
        public String getEstado() {
            return this.estado;
        }
        
        public void setEstado(String estado) {
            this.estado = estado;
        }
	
	public boolean isTerminada() {
		return this.terminada;
	}
	
	public TreeSet<Abastecimento> getAbastecimento() {
		TreeSet<Abastecimento> res = new TreeSet<>();
		
		for(Abastecimento o : this.ofertas)
			res.add(o);
		
		return res;
	}

        //Não deve permitir fazer mais do que um setMaterial
        public void setMaterial(HashMap<String,Integer> materialNecessario) {
            this.materialNecessario = materialNecessario;
            
        }
        
	public void setTerminada(boolean f) {
		this.terminada = f;
	}

	public Tarefa clone() {
		return new Tarefa(this);
	}

        public String toString() {
		StringBuilder s = new StringBuilder();
		  s.append("#   Código: " + this.getCodigo());
		s.append("\n#   Projeto de: " +this.getUtilizador());
		s.append("\n#   Nome: " + this.getNome());	
		s.append("\n#   Descrição: " + this.getTipo());
		s.append("\n#   Já está terminada? " + this.isTerminada());
		
		return s.toString();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if ((o == null) || (o.getClass() != this.getClass()))
			return false;
		else {
			Tarefa p = (Tarefa) o;
			return (this.getCodigo() == p.getCodigo());	
		}
	}
}


