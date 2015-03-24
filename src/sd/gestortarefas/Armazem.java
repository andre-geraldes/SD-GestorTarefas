package sd.gestortarefas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Armazem {
	private HashMap<String,Utilizador> utilizadores;
	private HashMap<Integer,Tarefa> tarefas;
    private HashMap<String,Integer> materiais;
	
	private Lock lock = new ReentrantLock();
	private Condition cond = lock.newCondition();

	public Armazem() {
            this.utilizadores = new HashMap<>();
            this.tarefas = new HashMap<>();
            this.materiais = new HashMap<>();
	}
	
	public HashMap<String, Utilizador> getUtilizadores() {
		return this.utilizadores;
	}

	public void setUtilizadores(HashMap<String, Utilizador> utilizadores) {
		this.utilizadores = utilizadores;
	}
	
	public HashMap<Integer, Tarefa> getTarefas() {
		return this.tarefas;
	}

	public void setTarefas(HashMap<Integer, Tarefa> tarefas) {
		this.tarefas = tarefas;
	}
        
        public HashMap<String,Integer> getMateriais() {
                return this.materiais;
        }
        
        public void setMateriais(HashMap<String,Integer> materiais) {
            this.materiais = materiais;
        }

        
        /**
         * Regista um utilizador 
         * @param nick
         * @param pass
         * @return 
         */
	public boolean registaUtilizador(String nick, String pass) {
		boolean res = false;
		
		lock.lock();
		try {
			if (!this.utilizadores.containsKey(nick)) {
				Utilizador u = new Utilizador(nick,pass);
				this.utilizadores.put(nick, u);
				res = true;
			}
		} finally {
			lock.unlock();
		}
		return res;
	}
        
        /**
         * Autenticacao de um utilizador
         * @param nick
         * @param pass
         * @return 
         */
	public boolean validaUser(String nick, String pass) {
		boolean res = false;
		
                lock.lock();
		try {
			if (this.utilizadores.containsKey(nick)) {
				if (this.utilizadores.get(nick).getPassword().equals(pass)) {
					this.utilizadores.get(nick).setAtivo(true);
                                        //Utilizador u = this.utilizadores.get(nick);
					res = true;
                                        //res = u.Autenticacao(nick, pass);
			}
			}
		} finally {
			lock.unlock();
		}
		return res;
	}
        
        /**
         * Cria uma nova tarefa
         * @param h
         * @param nome
         * @param desc
         * @param u
         * @return
         * @throws InterruptedException 
         */
	public int novaTarefa(String nome, String tipo, String user, String mat) throws InterruptedException {
               int res = -1;		
               boolean existe = false;
               
                for ( Tarefa x : this.tarefas.values()){
                    if (x.getNome().equals(nome)){
                        existe = true;
                    }   
                }
                    
                if (!existe){
                    Tarefa t = new Tarefa(nome,tipo,user);
                
                
                String delimit = "[,]";
                String[] materiais = mat.split(delimit);
                
                HashMap<String,Integer> materialNecessario = new HashMap<String,Integer>();
                for(int i=0; i<materiais.length; i+=2) {
                    materialNecessario.put(materiais[i], Integer.parseInt(materiais[i+1]));
                }
                
                t.setMaterial(materialNecessario);
                this.utilizadores.get(user).addTarefa(t);
                
                if (!this.tarefas.containsKey(t.getCodigo())) {
                    this.tarefas.put(t.getCodigo(), t);
                    res = t.getCodigo();
                }
                }

                
		return res;
	}		
        
        /**
         * Conclui uma tarefa repondo o stock
         * @param nick
         * @param idtarefa 
         */
        public boolean concluirTarefa(String nick, int idtarefa) {
            lock.lock();
            boolean k = false;
            
            if(this.tarefas.containsKey(idtarefa) && this.tarefas.get(idtarefa).getEstado().equals("em execucao")) {
                HashMap<String,Integer> materialNecessario = this.tarefas.get(idtarefa).getMaterialNecessario();
                Set<String> matNecessario = materialNecessario.keySet();
                k = true;
                for(String mn : matNecessario) {
                    this.materiais.put(mn,(this.materiais.get(mn)+ materialNecessario.get(mn)));
                }
                this.tarefas.get(idtarefa).setEstado("terminada");
                this.tarefas.get(idtarefa).setTerminada(true);
            }
            this.cond.signalAll();
            return k;
        }
        
        /**
         * Requisicao de material para uma tarefa
         * @param nick
         * @param idtarefa
         * @param h 
         */
        public boolean iniciarTarefa(Handler h, String nick, int idtarefa) throws InterruptedException {
            boolean emEspera = true;
            boolean s = false;
            lock.lock();
            try {
                if(this.tarefas.containsKey(idtarefa)) {
                    s = true;
                    HashMap<String,Integer> materialNecessario = this.tarefas.get(idtarefa).getMaterialNecessario();
                    Set<String> matNecessario = materialNecessario.keySet();
                    
                    while(emEspera) {
                        if(this.temMaterialSuficiente(materialNecessario)) {
                            this.tarefas.get(idtarefa).setEstado("em execucao");
                            for(String mn : matNecessario) {
                                this.materiais.put(mn,(this.materiais.get(mn)- materialNecessario.get(mn)));
                            }
                            emEspera = false;
                        } else {
                            this.cond.await();
                        } 
                    }
                }
                else s = false;
            } finally {
                System.out.println("saiu do wait");
                lock.unlock();
            }
        return s;  
        }
        
        /**
         * Abastece armazem, sem limitações
         * @param nick
         * @param material
         * @param quantidade
         * @throws InterruptedException 
         */
        public void abastecerArmazem(String nick, String material, int quantidade) throws InterruptedException {
            int novaQuantidade;
            
            lock.lock();
            try {
                if(this.materiais.containsKey(material)) {
                    novaQuantidade = this.materiais.get(material) + quantidade;
                    this.materiais.put(material,novaQuantidade);
                    
                } else {
                    this.materiais.put(material,quantidade);
                }
                this.cond.signalAll();
            } finally {
                lock.unlock();
            }
        }
        
        /**
         * Tarefas que nao terminaram
         * @param estado
         * @return 
         */
	public HashSet<Tarefa> listaTarefas(String estado) {
		HashSet<Tarefa> res = new HashSet<>();
		lock.lock();
		
		try {
			for (Tarefa t : this.tarefas.values())
				if (t.getEstado().equals(estado))
					res.add(t);
		} finally {
			lock.unlock();
		}
		return res;
	}
        
        /**
         * Verifica se o armazem tem um dado material numa quantidade 
         * @param paraRequisitar
         * @return 
         */
        public boolean temMaterialSuficiente(HashMap<String,Integer> paraRequisitar) {
            boolean res = true;
            
            //chaves
            Set<String> matParaRequisitar = paraRequisitar.keySet();
            
            for(String mpr : matParaRequisitar) {
                if(this.materiais.containsKey(mpr)) {
                    if(this.materiais.get(mpr) < paraRequisitar.get(mpr)) {
                        res = false;
                    }
                } else {
                    res = false;
                }
            }
            return res;
        }
        
        
        /**
         * Logout do utilizador
         * @param nick 
         */ 
	public void logout(String nick) {
            lock.lock();
            
            try {
                this.utilizadores.get(nick).setAtivo(false);
            } finally {
                lock.unlock();
            }
	}


	
}
