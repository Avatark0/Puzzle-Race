import java.net.*;
import java.io.*;
import java.util.*;

//O SERVIDOR
class Servidor {
  final static int MAXPLAYERS=20;

  //CRIA O serverSocket, RECEBE OS CLIENTES E INSTANCIA A SALA
  public static void main(String[] args) {
    ServerSocket serverSocket=null;

    try {
      serverSocket = new ServerSocket(80);
    } catch (IOException e) {
      System.out.println("Could not listen on port: " + 80 + ", " + e);
      System.exit(1);
    }

    for (int i=0; i<MAXPLAYERS; i++) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException e) {
        System.out.println("Accept failed: " + 80 + ", " + e);
        System.exit(1);
      }

      System.out.println("Accept Funcionou!");
      new Servindo(clientSocket).start();
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

  //PRA Q SERVE ESTA LINHA? - atribui o clientSocket nativo da classe ao Socket clientSocket (variavel)
  Servindo(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    try {
      Scanner is = new Scanner(clientSocket.getInputStream());
      os[cont++] = new PrintStream(clientSocket.getOutputStream());
      String inputLine, outputLine;
      System.out.println("cont = "+cont+", maxplayers = "+MAXPLAYERS);
      do {
        //AQUI ESTE TREAD DO SERVIDOR RECEBE A MENSAGEM DO SEU CLIENTE
        inputLine = is.nextLine();
        for (int i=0; i<cont; i++) {
          //AQUI ESTE TREAD DO SERVIDOR MANDA MENSAGEM PARA TODOS OS CLIENTES
          os[i].println(inputLine);
          os[i].flush();
        }
      } while (!inputLine.equals(""));//CONTROLA O FIM DO LOOP DE IO COM OS CLIENTES

      //ENCERRANDO A CONEXAO
      for (int i=0; i<cont; i++)
        os[i].close();
      is.close();
      clientSocket.close();
      System.exit(0);//TERMINA O PROGRAMA
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      System.out.println("Conexacao terminada pelo cliente");
    }
  }
}
