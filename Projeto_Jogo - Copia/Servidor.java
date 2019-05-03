import java.net.*;
import java.io.*;
import java.util.*;

//Classes: Servidor, Sala.
/*
Sobre o servidor: o servidor gerencia a conexao de n clientes com a sala, 
reconhecendo a desconexão de um cliente específico e liberando a respectiva vaga.
Caso a sala fique cheia, o servidor entra em loop de 0.5s, checando pela liberação de uma vaga.
Os clientes checam se há vagas antes de solicitar a conexão.
*/

//O SERVIDOR
class Servidor {
  final static int MAXPLAYERS=2;
  static boolean salaCheia=false;
  static boolean fecharSala=false;
  static boolean[] slot=new boolean[MAXPLAYERS];

  static void OcupaSlot(int slotNumber){
    slot[slotNumber]=true;
  }
  static void LiberaSlot(int slotNumber){
    slot[slotNumber]=false;
  }

  //CRIA O serverSocket, RECEBE OS CLIENTES E INSTANCIA A SALA
  public static void main(String[] args){
    ServerSocket serverSocket=null;

    try{
      serverSocket = new ServerSocket(80);
    }catch(IOException e){
      System.out.println("SERVIDOR: Could not listen on port: " + 80 + ", " + e);
      System.exit(1);
    }
    //BUG: SERVIDOR PERMITE QUE UM CLIENTE EXTRA ENTRE OCUPANDO A VAGA DO CLIENTE 0 (APOS TODAS AS OUTRAS VAGAS SEREM PREENCHIDAS)
    while(!fecharSala){//Fechar a sala quando estiver cheia? Qual a condicao?
      int clientCount=0;
      Socket clientSocket = null;
      for(int i=0;i<MAXPLAYERS;i++){
        System.out.println("SERVIDOR: SlotNumber "+i+", slot "+slot[i]+" (false=livre)");
        if(!slot[i]){//slot livre
          System.out.println("SERVIDOR: Esperando novo cliente para iniciar thread do slot "+i);
          try {
            clientSocket = serverSocket.accept();
          }catch(IOException e){
            System.out.println("SERVIDOR: Accept falhou: " + 80 + ", " + e);
            System.exit(1);
          }
          System.out.println("SERVIDOR: Accept Funcionou!");
          new Sala(clientSocket).start();
        }
        else clientCount++;
        if(clientCount>=MAXPLAYERS)salaCheia=true;
        try{
          if(salaCheia){
            System.out.println("SERVIDOR: Sala cheia!");
            Thread.sleep(1000);
          }
          Thread.sleep(200);//Espera de 0.2 segundos. Evita que o Servidor tente ocupar a última vaga novamente (antes da SALA mudar seu estado para ocupado)
          System.out.println("SERVIDOR: Reiniciando loop de busca por clientes.");
        }catch(InterruptedException e){
          System.out.println("SERVIDOR: Problema em Thread.sleep");
        }
      }
    }

    try{
      serverSocket.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}

//A SALA
class Sala extends Thread{
  Socket clientSocket;
  final static int MAXPLAYERS=Servidor.MAXPLAYERS;
  static PrintStream os[]=new PrintStream[MAXPLAYERS];
  static int cont=0;

  Sala(Socket clientSocket){
    this.clientSocket=clientSocket;
  }

  public void run(){
    try{
      Scanner is=new Scanner(clientSocket.getInputStream());
      int slotNumber=0;
      for(int i=0;i<MAXPLAYERS;i++){
        if(!Servidor.slot[i]){
          slotNumber=i;
          Servidor.OcupaSlot(slotNumber);
          cont++;
          break;
        }
      }
      os[slotNumber]=new PrintStream(clientSocket.getOutputStream());

      String inputLine, outputLine;
      char ESCAPE[]={(char)(27)};//nao consegui chegar por ESC na inputLine de um jeito melhor...
      String ESC=new String(ESCAPE);//Mas com certeza ha um jeito melhor...
      System.out.println("SALA: slotNumber "+slotNumber+", cont "+cont+", maxplayers "+MAXPLAYERS);
      do{
        //AQUI ESTE THREAD DO SERVIDOR RECEBE A MENSAGEM DO SEU CLIENTE
        inputLine = is.nextLine();

        //Em construção
        GerenteIO gg= new GerenteIO(inputLine,slotNumber);

        //AQUI ESTE THREAD DO SERVIDOR MANDA MENSAGEM PARA UM/TODOS OS CLIENTES (casos especiais como sair do jogo por parte do cliente são lidados apenas com o cliente específico)
        for(int i=0;i<MAXPLAYERS;i++){
          if(Servidor.slot[i]){
            if(inputLine.contains(ESC)&&i==slotNumber){
              os[slotNumber].println("");
              os[slotNumber].flush();
              Servidor.LiberaSlot(slotNumber);
              cont--;
              os[slotNumber].close();
              
              clientSocket.close();//É vantajoso fechar o socket? Ele já foi fechado pela desconexão do cliente? Se não fechar, haverá resource leak?
            }
            else if(!inputLine.contains(ESC)){
              os[i].println(inputLine);//atualmente o servidor apenas devolve para os clientes a linha de ação. onde será executado o processamento das ações?
              os[i].flush();
            }
          }
        }
      }while(!inputLine.equals(""));//CONTROLA O FIM DO LOOP DE IO COM OS CLIENTES (botao exit do menu, nenhum jogador na sala, etc)

      //ENCERRANDO A CONEXAO
      System.out.println("SALA: Encerrando a sala.");
      for(int i=0;i<Servidor.MAXPLAYERS;i++)
        if(Servidor.slot[i]){
          Servidor.LiberaSlot(i);//tem chance do servidor tentar uma conexao antes deste cliente terminar de encerrar esta conexao (e possivelmente exeder o limite de maxplayers?)
          cont--;
          os[i].close();
        }
      is.close();
      clientSocket.close();
      System.exit(0);//TERMINA A SALA
    }catch(IOException e){
      e.printStackTrace();
    }catch(NoSuchElementException e){
      System.out.println("SALA: Conexacao terminada pelo cliente");
    }
  }
}
