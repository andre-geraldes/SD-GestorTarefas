package sd.gestortarefas;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class Handler extends Thread {
    private Socket s;
    private Armazem sk;
    private ObjectInputStream sInput;
    private PrintWriter sOutput;

    public Handler(Socket s, Armazem k) throws InterruptedException {
        this.s= s;
        this.sk = k;
        this.sInput = null;
        this.sOutput = null;
    }
	
    public PrintWriter getSOutPut() {
	return this.sOutput;
    }

    public void run() {		
        try {
            do {
                this.sInput = new ObjectInputStream(s.getInputStream());
		        this.sOutput = new PrintWriter(s.getOutputStream());
				
		Pacote pacote = (Pacote) sInput.readObject();

		if (pacote.getAccao().equals(Servidor.REGISTAR)) {
                    System.err.println("Recebeu Registar");
                    String nick = pacote.getArgumentos().get(Servidor.NOME_USER);
                    String pw = pacote.getArgumentos().get(Servidor.PW_USER);

                    System.out.println("USER: " + nick + " " + pw);
                    boolean existe = sk.registaUtilizador(nick, pw);
					
                    if(existe)
			sOutput.println("Registado com Sucesso");
                    else
			sOutput.println("Utilizador já existe");
					
			sOutput.flush();
		} else {
                    if (pacote.getAccao().equals(Servidor.ENTRAR)) {
                    System.err.println("Pacote Entrar");
                    String nick = pacote.getArgumentos().get(Servidor.NOME_USER);
                    String pw = pacote.getArgumentos().get(Servidor.PW_USER);

                    boolean emSessao = false;
                    if(sk.getUtilizadores().containsKey(nick))
			emSessao =  sk.getUtilizadores().get(nick).isAtivo();
						
                    System.out.println("USER: " + nick + " " + pw);
                    boolean existe = sk.validaUser(nick, pw);
						
                    if(existe && !emSessao)
			sOutput.println("Entrou");
                    else sOutput.println("NaoEntrou");
                    sOutput.flush();
                    } else {
                        if (pacote.getAccao().equals(Servidor.CRIAR_TAREFA)){
                            System.err.println("Pacote Criar Tarefa");
                            String nome = pacote.getArgumentos().get(Servidor.NOME_TAREFA);
                            String tipo = pacote.getArgumentos().get(Servidor.TIPO_TAREFA);
                            String material = pacote.getArgumentos().get(Servidor.MATERIAL_TAREFA);
                            String nick = pacote.getArgumentos().get(Servidor.NOME_USER); 

                            int id = sk.novaTarefa(nome,tipo, nick, material);
                            System.out.println("ID nova tarefa: "+ id);
                            if(id != -1){
                                sOutput.println("Criada");
                                sOutput.flush();
                            } else{ 
                                sOutput.println("Tarefa não criada");
				sOutput.flush();
                            }    
                        } else {
                            if(pacote.getAccao().equals(Servidor.LISTATIPOSTAREFAS)) {
                                System.err.println("Pacote Lista Tarefas Em Curso");
                                String estado = pacote.getArgumentos().get(Servidor.ESTADO_TAREFA);
                                
                                HashSet<Tarefa> res = sk.listaTarefas(estado);
                                
                                ObjectOutputStream out = null;
                                out = new ObjectOutputStream(s.getOutputStream());
                                out.writeObject(res);
                                out.flush();
                            }
                            else {
                        	if(pacote.getAccao().equals(Servidor.ABASTECER)) {
                                    System.err.println("Pacote Abastecer Armazém");
                                    String user = pacote.getArgumentos().get(Servidor.NOME_USER);
                                    String material = pacote.getArgumentos().get(Servidor.OBJETO);
                                    String quantidade = pacote.getArgumentos().get(Servidor.QUANTIDADE);
                        			
                                    int quant = Integer.parseInt(quantidade);
                        			
                                    System.out.println(material+": "+quantidade);
                        			
                                    sk.abastecerArmazem(user, material, quant);
                                    sOutput.println("Armazem abastecido");
                                    sOutput.flush();                                                                   
                        	} 
                                else {
                                    if(pacote.getAccao().equals(Servidor.CONCLUIR_TAREFA)) {
                                        System.err.println("Pacote Concluir Tarefa");
                                        String user = pacote.getArgumentos().get(Servidor.NOME_USER);
                                        String id = pacote.getArgumentos().get(Servidor.ID);
                            			
                                        int i = Integer.parseInt(id);
                                        System.out.println("ID de tarefa: "+ id);
                        			
                                        boolean k = sk.concluirTarefa(user, i);
                                        
                                        if (k) {
                                            sOutput.println("Tarefa concluida");
                                        }
                                        else sOutput.println("Tarefa não concluida");
                                        sOutput.flush(); 
           
                                    } else {
                                        if (pacote.getAccao().equals(Servidor.INICIAR)){
                                            System.err.println("Pacote Iniciar Tarefa");
                                            String user = pacote.getArgumentos().get(Servidor.NOME_USER);
                                            String id = pacote.getArgumentos().get(Servidor.ID);                                 
                        			
                                            int i = Integer.parseInt(id);
                        			
                                            System.out.println("ID de tarefa: "+ id);
                        			
                                            boolean k = sk.iniciarTarefa(this, user, i);
                                            if (k) 
                                                sOutput.println("Tarefa Iniciada");
                                            else    
                                                sOutput.println("Tarefa Inexistente");
                                            sOutput.flush(); 
                                        } else {
                                            if (pacote.getAccao().equals(Servidor.VER_STOCK)){
                                                System.err.println("Pacote Ver Stock");
                                                HashMap<String,Integer> res = sk.getMateriais();
                                
                                                ObjectOutputStream out = null;
                                                out = new ObjectOutputStream(s.getOutputStream());
                                                out.writeObject(res);
                                                out.flush();
                                            }
                                            else {
                                            System.err.println("Pacote Sair");
                                            String nuser = pacote.getArgumentos().get(Servidor.NOME_USER);
                                            sk.logout(nuser);
                                        }
                                        }
                                    }                                   
                        	}
                            }
                        }
                    }
				}
			} while (true);
		} catch (Exception e) {
		}
	}
}
