package sd.gestortarefas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;


public class Cliente {
    private static String ip = "localhost";
    private static int port = 9999;
    private static Socket s = null;
    
    public static Scanner in = new Scanner(System.in);
    public static HashMap<String, String> hash = new HashMap<>();
    public static Pacote p = null;
    public static ObjectOutputStream o = null;
    public static ObjectInputStream i = null; 

    public static String nick = null;    


    public static void main(String args[]) throws IOException, ClassNotFoundException {
    s = new Socket(ip,port);
        
    menuOriginal();
    }
    
    public static void menuOriginal() throws IOException, ClassNotFoundException {
        String opt;
        
    	do {
            opt = menuInicial();
            hash = null;
            o = null;
        	
        	if(opt.equals("1")) {
                    System.out.println("#################### Novo Utilizador #####################");
                    System.out.println("#                                                        #");
                    in.nextLine();
                    System.out.println("#   Defina um username                                   #");
                    String user = in.nextLine();
                    System.out.println("#   Defina uma password                                  #");
                    String pw = in.nextLine();
                
                    hash = new HashMap<>();
                    hash.put(Servidor.NOME_USER, user);
                    hash.put(Servidor.PW_USER, pw);
                    p = new Pacote(Servidor.REGISTAR,hash);
                
                    o = new ObjectOutputStream(s.getOutputStream());
                    o.writeObject(p);
                    o.flush();
                
                    BufferedReader sktInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    System.out.println("\n"+sktInput.readLine()+"\n");
        	} else {
                    if(opt.equals("2")) {
                    System.out.println("######################## Entrar ##########################");
                    System.out.println("#                                                        #");
                    in.nextLine();
                    System.out.println("#   Introduza um username                                #");
                    String user = in.nextLine();
                    System.out.println("#   Introduza a password                                 #");
                    String pw = in.nextLine();
                    
                    hash = new HashMap<>();
                    hash.put(Servidor.NOME_USER, user);
                    hash.put(Servidor.PW_USER, pw);
                    p = new Pacote(Servidor.ENTRAR,hash);
                    
                    o = new ObjectOutputStream(s.getOutputStream());
                    o.writeObject(p);
                    o.flush();
                    
                    BufferedReader sktInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String result = null;
                    result=sktInput.readLine();
                    
                    if(result.equals("Entrou")) {
                    	nick = user;
                    	menuPrincipal();
                    }
                    else {
                    	System.err.println("#   Autenticação falhada                                 #");
                	System.out.println("#                                                        #");
                	System.out.println("##########################################################");
                    }
                }
        		else {
        			if(opt.equals("3")) {
        				System.exit(0);
        			}
        			else
        				System.out.println("Opção inválida!");
        		}
        	}
        		
        } while(true);
    }
    
    public static void menuPrincipal() throws IOException, ClassNotFoundException {
	System.out.println("#################### Menu Principal ######################");
	System.out.println("#                                                        #");
	System.out.println("#   Bem Vindo "+nick);
	System.out.println("#                                                        #");
        System.out.println("#   1 - Registar Abastecimento                           #");
        System.out.println("#   2 - Criar Tarefa                                     #");
        System.out.println("#   3 - Requisição De Objeto / Inicio Tarefa             #");
        System.out.println("#   4 - Registar Tarefa Concluida                        #");
        System.out.println("#   5 - Ver Stock                                        #");
        System.out.println("#   6 - Lista Tipo de Tarefas Em Curso                   #");
        System.out.println("#   7 - Logout                                           #");
        System.out.println("#                                                        #");
	System.out.println("#   Escolha uma opção                                    #");
	System.out.println("##########################################################");
    	String opt = in.next();
   	
    	do {
            if(opt.equals("1"))
                MenuRegistarAbastecimento();
            if(opt.equals("2"))
                MenuCriarTarefa();    			
            if(opt.equals("3"))
                MenuRequisicaoObjeto();
            if(opt.equals("4"))
                MenuRegistarTarefaConcluida();
            if(opt.equals("5"))
                MenuVerStock();
            if(opt.equals("6"))
                MenuListaTipoTarefas();
            if(opt.equals("7")) {
                logout();
            } else {
    		System.out.println("Opcão inválida!");
                menuPrincipal();
            }
                
    	} while(!(opt.equals("1") || opt.equals("2") || opt.equals("3") || opt.equals("4") || opt.equals("5") || opt.equals("6")|| opt.equals("7")));
    }
    
    private static void logout() throws IOException, ClassNotFoundException {
    	hash = new HashMap<>();
        hash.put(Servidor.NOME_USER, nick);
        p = new Pacote(Servidor.SAIR,hash);
        criarObjeto(p);
        
        nick = null;
        menuOriginal();
    }
    
    private static void MenuVerStock() throws IOException, ClassNotFoundException{
        in.nextLine();
	System.out.println("######################### Stock #########################");
	System.out.println("#                                                       #");
        hash = new HashMap<>();
        p = new Pacote(Servidor.VER_STOCK,hash);
                
        criarObjeto(p);
                
        i = new ObjectInputStream(s.getInputStream());
    	@SuppressWarnings("unchecked")
	HashMap<String, Integer> materiais = (HashMap<String, Integer>) i.readObject();
        Set<String> nomes = materiais.keySet();
        System.out.println("#   Material->Quantidade                                 #");
    	if(!materiais.isEmpty()) {
            for(String mat : nomes)
    		System.out.println("#   "+mat+"->"+materiais.get(mat)+"\n");
    	} 
	System.out.println("#                                                        #");
	System.out.println("##########################################################");
    	menuPrincipal();
              
        criarObjeto(p);
    }

    private static void MenuListaTipoTarefas() throws IOException, ClassNotFoundException {
	in.nextLine();
	System.out.println("############### Tipos De Tarefas Em Curso ###############");
	System.out.println("#                                                       #");
        
        System.out.println("#   Tarefas criadas:                                    #");
        hash = new HashMap<>();
        hash.put(Servidor.ESTADO_TAREFA, "criada");
        p = new Pacote(Servidor.LISTATIPOSTAREFAS,hash);
              
        criarObjeto(p);
            
        i = new ObjectInputStream(s.getInputStream());
        
    	@SuppressWarnings("unchecked")
	HashSet<Tarefa> taref = (HashSet<Tarefa>) i.readObject();
    	if(!taref.isEmpty()) {
    		for(Tarefa tar : taref)
    			System.out.println("#   Tipo: "+tar.getTipo()+"\n#   Nome Tarefa: "+tar.getNome()+"\n#   ID da Tarefa: "+tar.getCodigo()+"\n#   ");
    	}
        else
			System.out.println("\n#   Não há novas tarefas criadas.\n");
        
        System.out.println("#   Tarefas em execução:                                #");
        hash = new HashMap<>();
        hash.put(Servidor.ESTADO_TAREFA, "em execucao");
        p = new Pacote(Servidor.LISTATIPOSTAREFAS,hash);
                
        criarObjeto(p);
                
        i = new ObjectInputStream(s.getInputStream());
    	@SuppressWarnings("unchecked")
	HashSet<Tarefa> tarefas = (HashSet<Tarefa>) i.readObject();
    	if(!tarefas.isEmpty()) {
    		for(Tarefa tar : tarefas)
    			System.out.println("#   Tipo: "+tar.getTipo()+"\n#   Nome Tarefa: "+tar.getNome()+"\n#   ID da Tarefa: "+tar.getCodigo()+"\n#   ");
    	} else
			System.out.println("\n#   Não há tarefas em execução.\n");
        
        System.out.println("#   Tarefas terminadas:                                #");
        hash = new HashMap<>();
        hash.put(Servidor.ESTADO_TAREFA, "terminada");
        p = new Pacote(Servidor.LISTATIPOSTAREFAS,hash);
                
        criarObjeto(p);
                
        i = new ObjectInputStream(s.getInputStream());
    	@SuppressWarnings("unchecked")
	HashSet<Tarefa> tare = (HashSet<Tarefa>) i.readObject();
    	if(!tare.isEmpty()) {
    		for(Tarefa tar : tare)
    			System.out.println("#   Tipo: "+tar.getTipo()+"\n#   Nome Tarefa: "+tar.getNome()+"\n#   ID da Tarefa: "+tar.getCodigo()+"\n#   ");
    	} else
			System.out.println("\n#   Não há tarefas terminadas.\n");

		System.out.println("#                                                        #");
		System.out.println("##########################################################");
    	menuPrincipal();
	}

    private static void MenuRegistarTarefaConcluida() throws IOException, ClassNotFoundException {
	in.nextLine();
	System.out.println("################ Registar Tarefa Concluida ################");
	System.out.println("#                                                         #");
        System.out.println("#   Identificador de tarefa:                              #");
        String id = in.next();
        
        hash = new HashMap<>();
        hash.put(Servidor.NOME_USER, nick);
        hash.put(Servidor.ID, id);
        p = new Pacote(Servidor.CONCLUIR_TAREFA,hash);
                
        criarObjeto(p);
                
        BufferedReader sktInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
        System.out.println(sktInput.readLine());

	System.out.println("#                                                        #");
	System.out.println("##########################################################");
    	menuPrincipal();
	}

    private static void MenuRegistarAbastecimento() throws IOException, ClassNotFoundException {
	System.out.println("################# Registar Abastecimento #################");
	System.out.println("#                                                        #");
	in.nextLine();
	System.out.println("#   Insira o nome do objeto                              #");
	String nome = in.next();
          
	System.out.println("#   Insira a quantidade                                  #");
	String quantidade = in.next();
        int q = Integer.parseInt(quantidade);
        if(!eNumero(quantidade) || q < 0) {
            System.out.println("\nPor favor insira um número\n");
            MenuRegistarAbastecimento();
        }
		
	hash = new HashMap<>();
	hash.put(Servidor.NOME_USER, nick);
	hash.put(Servidor.OBJETO, nome);
	hash.put(Servidor.QUANTIDADE, quantidade);
	p = new Pacote(Servidor.ABASTECER, hash);
		
	criarObjeto(p);
		
        BufferedReader sktInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
        System.out.println("#   "+sktInput.readLine());
        
        System.out.println("#                                                        #");
        System.out.println("##########################################################");
	menuPrincipal();
    }
    
    private static void MenuRequisicaoObjeto() throws IOException, ClassNotFoundException {
	System.out.println("########## Requisição de objeto / inicio tarefa ##########");
	System.out.println("#                                                        #");
	in.nextLine();
	System.out.println("#   Insira o id da tarefa                                #");
	String id = in.next();
		
	hash = new HashMap<>();
	hash.put(Servidor.NOME_USER, nick);
	hash.put(Servidor.ID, id);
	p = new Pacote(Servidor.INICIAR, hash);
		
	criarObjeto(p);
		
        BufferedReader sktInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
        System.out.println("#   "+sktInput.readLine());
        
        System.out.println("#                                                        #");
	System.out.println("##########################################################");
	menuPrincipal();
    }

    private static void MenuCriarTarefa() throws IOException, ClassNotFoundException{
        System.out.println("###################### Nova Tarefa #######################");
        System.out.println("#                                                       #");

        in.nextLine();
        System.out.println("#   Nome da tarefa                                       #");
        String nomeTarefa = in.nextLine();
        System.out.println("#   Tipo da tarefa                                       #");
        String tipoTarefa = in.nextLine();
        String material = "";
        String materialTotal = "";
        String quantidade = "";
        while (!material.equals("Sair")){
            System.out.println("#   Material necessário para a tarefa                    #");
            System.out.println("#   Escrever \"Sair\" para concluir                        #");
            material = in.next();
            if (!material.equals("Sair")){
                materialTotal = materialTotal.concat(material);
                materialTotal = materialTotal.concat(",");
                System.out.println("#   Quantidade                                           #");
                quantidade = in.next();
                materialTotal = materialTotal.concat(quantidade);
                materialTotal = materialTotal.concat(",");
                if(!eNumero(quantidade)) {
                    System.out.println("\nPor favor insira um número\n");
                    MenuCriarTarefa();
                }
            }
        }
        
        materialTotal = materialTotal.substring(0, materialTotal.length() - 1);
         
        hash = new HashMap<>();
        hash.put(Servidor.NOME_USER, nick);
        hash.put(Servidor.NOME_TAREFA, nomeTarefa);
        hash.put(Servidor.TIPO_TAREFA, tipoTarefa);
        hash.put(Servidor.MATERIAL_TAREFA, materialTotal);
        
        p = new Pacote(Servidor.CRIAR_TAREFA,hash);
                   
        criarObjeto(p);

        BufferedReader sktInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
        System.out.println("#   "+sktInput.readLine());
        
	System.out.println("#                                                        #");
	System.out.println("##########################################################");
        menuPrincipal();
    }

    public static String menuInicial() {
		System.out.println("################### Gestor de Tarefas ####################");
		System.out.println("#                                                        #");
                System.out.println("#   1 - Registar                                         #");
                System.out.println("#   2 - Entrar                                           #");
                System.out.println("#   3 - Sair da aplicação                                #");
		System.out.println("#                                                        #");
		System.out.println("#   Escolha uma opção:                                   #");
		System.out.println("##########################################################");
        String opt = in.next();
        if ( !(opt.equals("1") || opt.equals("2") || opt.equals("3")) )
             opt = menuInicial();
        
        return opt ;    
    }
	
    public static void criarObjeto(Pacote p) throws IOException {
	o = null;
	o = new ObjectOutputStream(s.getOutputStream());
        o.writeObject(p);
        o.flush();
	}
    
    public static boolean eNumero(String s) {
        boolean res = true;
        boolean isDouble = false;  
        try {  
            Double.parseDouble(s);  
            isDouble = true;  
        } catch (Exception e) {  
           isDouble = false;  
        } 
        return isDouble;
    }
}
