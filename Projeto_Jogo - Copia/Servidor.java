import java.net.*;
import java.io.*;
import java.util.*;

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
  public static void main(String[] args) {
    ServerSocket serverSocket=null;

    try {
      serverSocket = new ServerSocket(80);
    } catch (IOException e) {
      System.out.println("SERVIDOR: Could not listen on port: " + 80 + ", " + e);
      System.exit(1);
    }
    //SERVIDOR PERMITE QUE UM CLIENTE EXTRA ENTRE OCUPANDO A VAGA DO CLIENTE 0 (APOS TODAS AS OUTRAS VAGAS SEREM PREENCHIDAS)
    while(!fecharSala){//Fechar a sala quando estiver cheia? Qual a condicao?
      int clientCount=0;
      Socket clientSocket = null;
      for(int i=0;i<MAXPLAYERS;i++){
        System.out.println("SERVIDOR: SlotNumber "+i+", slot "+slot[i]);
        if(!slot[i]){//slot livre
          System.out.println("SERVIDOR: Esperando novo cliente para iniciar tread do slot "+i);
          try {
            clientSocket = serverSocket.accept();
          } catch (IOException e) {
            System.out.println("SERVIDOR: Accept falhou: " + 80 + ", " + e);
            System.exit(1);
          }
          System.out.println("SERVIDOR: Accept Funcionou!");
          new Servindo(clientSocket).start();
        }
        else  clientCount++;
        if(clientCount>=MAXPLAYERS)salaCheia=true;
        try{
          if(salaCheia){
            System.out.println("SERVIDOR: Sala cheia!");
            Thread.sleep(1000);//o q fazer quando a sala estiver cheia? Avisar o CLIENTE que tentar se conectar que a sala esta cheia! (COMO?): utilizando uma variavel estatica pelo servidor e acessando ela pelo cliente antes de solicitar o pedido?
          }
          Thread.sleep(200);//espera de 0.2 segundos, para evitar que o Servidor tente ocupar a ultima vaga novamente (antes da SALA mudar seu estado para ocupado)
          System.out.println("SERVIDOR: Reiniciando loop do for");
        }catch(InterruptedException e){
          System.out.println("SERVIDOR: Problema em Thread.sleep");
        }
      }
    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

//A SALA
class Servindo extends Thread {
  Socket clientSocket;
  final static int MAXPLAYERS=Servidor.MAXPLAYERS;
  static PrintStream os[] = new PrintStream[MAXPLAYERS];
  static int cont=0;

  Servindo(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    try {
      Scanner is = new Scanner(clientSocket.getInputStream());
      int slotNumber=0;
      for(int i=0;i<MAXPLAYERS;i++){
        if(!Servidor.slot[i]){
          slotNumber=i;
          Servidor.OcupaSlot(slotNumber);
          cont++;
          break;
        }
      }
      os[slotNumber] = new PrintStream(clientSocket.getOutputStream());

      String inputLine, outputLine;
      char ESCAPE[]={(char)(27)};//nao consegui chegar por ESC na inputLine de um jeito melhor...
      String ESC=new String(ESCAPE);//Mas com certeza ha um jeito melhor...
      System.out.println("SALA: slotNumber "+slotNumber+", cont "+cont+", maxplayers "+MAXPLAYERS);
      do {
        //AQUI ESTE TREAD DO SERVIDOR RECEBE A MENSAGEM DO SEU CLIENTE
        inputLine = is.nextLine();
        //AQUI ESTE TREAD DO SERVIDOR MANDA MENSAGEM PARA TODOS OS CLIENTES
        for (int i=0;i<MAXPLAYERS;i++){
          if(Servidor.slot[i]){
            if(inputLine.contains(ESC)&&i==slotNumber){
              os[slotNumber].println("");
              os[slotNumber].flush();
              Servidor.LiberaSlot(slotNumber);
              cont--;
              os[slotNumber].close();
            }
            else if(!inputLine.contains(ESC)){
              os[i].println(inputLine);
              os[i].flush();
            }
          }
        }
      } while (!inputLine.equals(""));//CONTROLA O FIM DO LOOP DE IO COM OS CLIENTES (botao exit do menu, nenhum jogador na sala, etc)

      //ENCERRANDO A CONEXAO
      //controle de saida apenas do cliente especifico:(tentativa de, n funcionou)
      Servidor.slot[slotNumber]=false;//tem chance do servidor tentar uma conexao antes deste cliente terminar de encerrar esta conexao (e possivelmente exeder o limite de maxplayers?)
      cont--;
      os[slotNumber].close();
      /*linhas originais: (encerram a conexao para todos os clientes)
      for (int i=0; i<cont; i++)
        os[i].close();//*/
      is.close();
      clientSocket.close();
      System.exit(0);//TERMINA O PROGRAMA
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      System.out.println("SALA: Conexacao terminada pelo cliente");
    }
  }
}
