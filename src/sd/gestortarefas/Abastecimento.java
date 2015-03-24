/*package sd.gestortarefas;

import java.io.Serializable;

public class Abastecimento implements Comparable<Abastecimento>, Serializable{
	
	private static final long serialVersionUID = 1L;
	private String nick;
        private String material;
	private int quantidade;
	private boolean lida;
	
	public Abastecimento() {
		this.nick = "";
                this.material = "";
		this.quantidade = 0;
                this.lida = false;
	}
	
	public Abastecimento(String nick, String material,int quantidade) {
		this.nick = nick;
                this.material = material;
		this.quantidade = quantidade;
		this.lida = false;
	}
	
	public Abastecimento(Abastecimento o) {
		this.nick = o.getNick();
		this.quantidade = o.getQuantidade();
		this.lida = o.getLida();
	}
	
	public String getNick() {
		return this.nick;
	}
	
	public boolean getLida() {
		return this.lida;
	}
	
	public void setLida(boolean a) {
		this.lida = a; 
	}
	
	public int getQuantidade() {
		return this.quantidade;
	}
 
	public int compareTo(Abastecimento o) {
		if(o.getQuantidade() < this.quantidade) 
			if(o.getNick().compareTo(this.nick) != 0)
				return -1;
			else
				return 0;
		else {
			if(o.getNick().compareTo(this.nick) != 0) 
				return 1;
			else
				return 0;
		}
	}
	
	public Abastecimento clone() {
		return new Abastecimento(this);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Abastecimento other = (Abastecimento) obj;
		if (nick == null) {
			if (other.nick != null)
				return false;
		} else if (!nick.equals(other.nick))
			return false;
		return true;
	}
	
}
*/
