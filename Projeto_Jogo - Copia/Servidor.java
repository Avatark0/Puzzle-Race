import java.net.*;
import java.io.*;
import java.util.*;

//Classes: Servidor, Sala.
/*
Sobre o servidor: o servidor gerencia a conexao de maxPlayers clientes com a sala, 
reconhecendo a desconexão de um cliente específico e liberando a respectiva vaga.
Caso a sala fique cheia, o servidor entra em loop de 2s, checando pela liberação de uma vaga.
Os clientes checam se há vagas antes de solicitar a conexão.
*/

//O SERVIDOR
class Servidor {
  final static int MAXPLAYERS=2;
  static boolean salaCheia=false;
  static boolean fecharSala=false;//O nome correto seria "fecharInstanciaDoServidor"?
  static boolean[] slot=new boolean[MAXPLAYERS];

  static void OcupaSlot(int slotNumber){
    slot[slotNumber]=true;
  }
  static void LiberaSlot(int slotNumber){
    slot[slotNumber]=false;
  }

  //Cria o serverSocket, recebe os clientes e instancia a sala.
  public static void main(String[] args){
    ServerSocket serverSocket=null;
    try{
      serverSocket = new ServerSocket(80);
    }catch(IOException e){
      System.err.println("SERVIDOR: Could not listen on port: " + 80 + ", " + e);
      System.exit(1);
    }
    //BUG: SERVIDOR PERMITE QUE UM CLIENTE EXTRA ENTRE OCUPANDO A VAGA DO CLIENTE 0 (APOS TODAS AS OUTRAS VAGAS SEREM PREENCHIDAS)-Contornado pela checagem de vagas pelo cliente antes de solicitar a conexão.
    while(!fecharSala){//Fechar a sala quando estiver cheia? Qual a condicao?
      int clientCount=0;
      Socket clientSocket = null;
      for(int i=0;i<MAXPLAYERS;i++){
        if(!slot[i]){//slot livre
          System.out.println("SERVIDOR: Esperando novo cliente para iniciar thread do slot "+i);
          try {
            clientSocket = serverSocket.accept();
          }catch(IOException e){
            System.err.println("SERVIDOR: Accept falhou: " + 80 + ", " + e);
            System.exit(1);
          }
          System.out.println("SERVIDOR: Accept slot "+i+" funcionou!");
          new Sala(clientSocket).start();
        }
        else clientCount++;
        if(clientCount>=MAXPLAYERS)salaCheia=true;
        try{
          if(salaCheia){
            System.out.println("SERVIDOR: Sala cheia!");
            Thread.sleep(2000);//Espere de 2 segundos antes de checar se há vagas na sala. (Evita processamento desnecessário)
          }
          Thread.sleep(200);//Espera de 0.2 segundos. Evita que o Servidor tente ocupar a última vaga novamente (antes da SALA mudar seu estado para ocupado)
          System.out.println("SERVIDOR: Reiniciando loop de busca por clientes.");
        }catch(InterruptedException e){
          System.err.println("SERVIDOR: Problema em Thread.sleep");
        }
      }
    }
    //Fechando serverSocket. (Em que situação isso deve ser feito?)
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
      //Recebe o cliente do servidor, e estabelece a conexão no socketNumber disponível de menor index.
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

      os[slotNumber]=new PrintStream(clientSocket.getOutputStream());//Estabelece o OutPutStream da sala.

      String inputLine;//Input a ser recebido pelo cliente.
      char ESCAPE[]={(char)(27)};//Não consegui checar por ESC na inputLine de um jeito melhor...
      String ESC=new String(ESCAPE);//Mas deve haver um jeito melhor...
      char DELETE[]={(char)(127)};
      String DEL=new String(DELETE);
      System.out.println("SALA: slotNumber "+slotNumber+", cont "+cont+", maxplayers "+MAXPLAYERS);
      //Loop de leitura dos inputs do cliente.
      do{
        inputLine = is.nextLine();
        if(!inputLine.contains(ESC)&&!inputLine.contains(DEL)){
          //Processando as inputLines individuais no output formatado para todos os clientes.
          GerenteIO.GeraOutput(inputLine,MAXPLAYERS,slotNumber);
          for(int i=0;i<MAXPLAYERS;i++)
            if(Servidor.slot[i]){
              os[i].println(GerenteIO.outputString);
              os[i].flush();
            }
        }
        else if(inputLine.contains(DEL))GerenteIO.ResetOutputPi(slotNumber);
        //Solicitada desconexão por parte do cliente:
        else if(inputLine.contains(ESC)){
          os[slotNumber].println("");
          os[slotNumber].flush();
          Servidor.LiberaSlot(slotNumber);
          cont--;
          os[slotNumber].close();
          clientSocket.close();//É vantajoso fechar o socket? Ele já foi fechado pela desconexão do cliente? Se não fechar, haverá resource leak?
        }
      }while(!inputLine.equals(""));//CONTROLA O FIM DO LOOP DE IO COM OS CLIENTES. SAIR DESTE LOOP ENCERRA TODAS AS INTÂNCIAS DE SALA.

      //ENCERRANDO A CONEXAO
      System.out.println("SALA: Encerrando a sala.");
      for(int i=0;i<MAXPLAYERS;i++)
        if(Servidor.slot[i]){
          Servidor.LiberaSlot(i);
          cont--;
          os[i].close();
        }
      is.close();
      clientSocket.close();
      System.exit(0);//TERMINA A SALA
    }catch(IOException e){
      e.printStackTrace();
    }catch(NoSuchElementException e){
      System.err.println("SALA: Conexacao terminada pelo cliente");
    }catch(StringIndexOutOfBoundsException e){
      System.err.println("SALA: StringIndexOutOfBoundsException");
    }
  }
}

//GERENTE_IO: Padroniza o Input recebido pelos Clientes numa outputString única.
class GerenteIO{
  static String outputString="";//OutPut formatado, será passado a todos os jogadores.
  static boolean outputStringInitialized=false;

  static synchronized void ResetOutputPi(int playerNum){
    int indexIni=outputString.indexOf("P"+playerNum+":");
    int indexEnd=outputString.indexOf("P"+(playerNum+1)+":");
    String stringIni=outputString.substring(0,indexIni+3);
    String stringEnd;
    if(indexEnd>=0)stringEnd=outputString.substring(indexEnd);
    else stringEnd="";
    outputString=stringIni+stringEnd;
  }

  //Recebe o input individual de cada Player e processa no output formatado. (Chamado no 'for' de output da Sala)
  //Synchronized precisa de algum tratamento especial?
  static synchronized void GeraOutput(String inputLine, int maxPlayers, int playerNum){
    //Inicializando a outputString:
    if(!outputStringInitialized){
      for(int i=0;i<maxPlayers;i++)
        outputString=outputString.concat("P"+i+":");
      outputStringInitialized=true;
    }
    //Criando as substrings necessárias para formatação:
    int indexIni=outputString.indexOf("P"+playerNum+":");
    int indexEnd=outputString.indexOf("P"+(playerNum+1)+":");
    String stringIni=outputString.substring(0,indexIni+3);
    String stringInput;
    String stringEnd;
    if(indexEnd>=0){
        stringInput=outputString.substring(indexIni+3,indexEnd);
        stringEnd=outputString.substring(indexEnd);
    }
    else{
        stringInput=outputString.substring(indexIni+3);
        stringEnd="";
    }
    //Processando o inputString(gerado pelo cliente) na stringInput(gerada nesta classe):
    if(inputLine.contains("a")||inputLine.contains("A"))
        if(!stringInput.contains("a")&&!stringInput.contains("d")&&!stringInput.contains("w")&&!stringInput.contains("s"))
            stringInput=stringInput.concat("a");
    else if(inputLine.contains("d")||inputLine.contains("D"))
        if(!stringInput.contains("a")&&!stringInput.contains("d")&&!stringInput.contains("w")&&!stringInput.contains("s"))
            stringInput=stringInput.concat("d");
    else if(inputLine.contains("w")||inputLine.contains("W"))
        if(!stringInput.contains("a")&&!stringInput.contains("d")&&!stringInput.contains("w")&&!stringInput.contains("s"))
            stringInput=stringInput.concat("w");
    else if(inputLine.contains("s")||inputLine.contains("S"))
        if(!stringInput.contains("a")&&!stringInput.contains("d")&&!stringInput.contains("w")&&!stringInput.contains("s"))
            stringInput=stringInput.concat("s");
    if(inputLine.contains(" "))
        if(!stringInput.contains(" "))
            stringInput=stringInput.concat(" ");
    //Gerando a outputString:
    outputString=stringIni+stringInput+stringEnd;
    //System.out.println("GerenteIO: outputString="+outputString);
  }
}
