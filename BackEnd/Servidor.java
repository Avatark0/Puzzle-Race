import java.net.*;
import java.io.*;
import java.util.*;

//Classes: Servidor, Sala.
/*
Sobre o servidor: o servidor gerencia a conexao dos clientes com a sala, 
reconhecendo a desconexão de um cliente específico e liberando a respectiva vaga.
Caso a sala fique cheia, o servidor entra em loop de 2s, checando pela liberação de uma vaga.

*/

//O SERVIDOR
class Servidor {
  final static int MAXPLAYERS=2;//Número de Players permitido na Sala
  static boolean salaCheia=false;//Controle do limite de players da Sala
  static boolean fecharSala=false;//Controle de encerramento do servidor. O nome correto seria "fecharInstanciaDoServidor"?
  static boolean[] slot=new boolean[MAXPLAYERS];//Controle de status de cada slot de Player da Sala

  //Ocupa ou desocupa o slot do Cliente
  static void OcupaSlot(int slotNumber){
    slot[slotNumber]=true;
  }
  static void LiberaSlot(int slotNumber){
    slot[slotNumber]=false;
  }

  //Cria o serverSocket, instancia o GerenteFPS, recebe os clientes e instancia a sala.
  public static void main(String[] args){
    ServerSocket serverSocket=null;
    new Thread(new BackFPS()).start();
    try{
      serverSocket=new ServerSocket(80);
    }catch(IOException e){
      System.err.println("SERVIDOR: Could not listen on port: "+80+", "+e);
      System.exit(1);
    }
    while(!fecharSala){//Fechar a sala quando estiver cheia? Qual a condicao?
      int clientCount=0;
      Socket clientSocket=null;
      for(int i=0;i<MAXPLAYERS;i++){
        if(!slot[i]){//Slot livre
          System.out.println("SERVIDOR: Esperando novo cliente para iniciar thread do slot "+i);
          try{
            clientSocket=serverSocket.accept();
          }catch(IOException e){
            System.err.println("SERVIDOR: Accept falhou: "+80+", "+e);
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
            Thread.sleep(5000);//Espera de 5 segundos antes de checar se há vagas na sala. (Evita processamento desnecessário)
          }
          Thread.sleep(200);//Espera de 0.2 segundos. Evita que o Servidor tente ocupar a última vaga novamente (antes da SALA mudar seu estado para ocupado)//funciona?
          System.out.println("SERVIDOR: Reiniciando loop de busca por clientes");
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
  Socket clientSocket=null;
  final static int MAXPLAYERS=Servidor.MAXPLAYERS;
  static Player1[] playerArray=new Player1[MAXPLAYERS];//Array de jogadores. Cada jogador pussui uma classe própria, todos extendidos de Player1
  static PrintStream os[]=new PrintStream[MAXPLAYERS];
  static int cont=0;
  static String[] inputString=new String[MAXPLAYERS];
  static String outputString;
  static boolean p1=false;
  static boolean p2=false;
  public static Cenario[] cenario=new Cenario[Cenario.blocosNum];//Vetor de blocos do cenário

  Sala(Socket clientSocket){
    this.clientSocket=clientSocket;
  }
  void setCenario(){
    for(int i=0;i<Cenario.blocosNum;i++){
      cenario[i]=new Cenario(i);
    }
  }
  static void SetPlayers(){//Pode ser removido. Basta utilizar switch(player) em ExecutaInputs
    playerArray[0]=new Player1();
    playerArray[1]=new Player2();  
  }
  static synchronized void EncerraPartida(int PlayerVencedor){
    String stringVitoria="";
    if(PlayerVencedor==0){
      stringVitoria="VITORIA:0";
    }
    else if(PlayerVencedor==1){
      stringVitoria="VITORIA:1";
    }
    for(int i=0;i<MAXPLAYERS;i++)
      if(Servidor.slot[i]){
        os[i].println(stringVitoria);
        os[i].flush();
      }
  }
  static void ExecutaInputs(String input, int player){
    if(player==0)Player1.ExecutaAcao(input);
    else if(player==1)Player2.ExecutaAcao(input);
  }
  static void ResetaInputString(int player){
    inputString[player]="";
  }
  static void AtualizaOutputString(){//Checa as posições dos Players e atualiza os valores na outputString
    String p1x=String.valueOf(Player1.posX);
    String p1y=String.valueOf(Player1.posY);
    String p1Estado=String.valueOf(Player1.estado);
    String p1Direcao=String.valueOf(Player1.direcao);
    String p2x=String.valueOf(Player2.posX);
    String p2y=String.valueOf(Player2.posY);
    String p2Estado=String.valueOf(Player2.estado);
    String p2Direcao=String.valueOf(Player2.direcao);
    String aux="";
    aux=aux.concat("0:").concat(p1x).concat(",").concat(p1y).concat(",").concat(p1Estado).concat(",").concat(p1Direcao).concat(".1:").concat(p2x).concat(",").concat(p2y).concat(",").concat(p2Estado).concat(",").concat(p2Direcao);
    outputString=aux;
  }
  static void EnviaOutputString(){
    //System.out.println("SALA: outputString="+outputString);
    for(int i=0;i<MAXPLAYERS;i++)
      if(Servidor.slot[i]){
        os[i].println(outputString);
        os[i].flush();
      }
  }

  public void run(){
    try{
      //Recebe o cliente do servidor, e estabelece a conexão no socketNumber disponível de menor index.
      Scanner is=new Scanner(clientSocket.getInputStream());
      int slotNumber=0;
      for(int i=0;i<MAXPLAYERS;i++){
        if(!Servidor.slot[i]){
          slotNumber=i;
          if(slotNumber==0)setCenario();//Cria o cenario físico. Associado à conexão do player1. Seria melhor associar ao inicio da partida (preenchimento da sala)
          switch(i){
            case 0: p1=true; break;
          }
          Servidor.OcupaSlot(slotNumber);
          cont++;
          break;
        }
      }

      os[slotNumber]=new PrintStream(clientSocket.getOutputStream());//Estabelece o OutPutStream da sala.

      String inputLine;//Input a ser recebido pelo cliente.
      char ESCAPE[]={(char)(27)};//Não consegui checar por ESC na inputLine de um jeito melhor...
      String ESC=new String(ESCAPE);//Mas deve haver um jeito melhor...
      System.out.println("SALA: slotNumber "+slotNumber+", cont "+cont+", maxplayers "+MAXPLAYERS);
      ResetaInputString(slotNumber);//Formata a input string do jogador
      //Loop de leitura dos inputs do cliente.
      do{
        inputLine = is.nextLine();//Recebe o input do cliente
        inputString[slotNumber]=inputString[slotNumber].concat(inputLine);//Concatena na inputString do cliente
        if(inputLine.contains(ESC)){//Solicitada desconexão por parte do cliente. (inputLine contém ESC)
          os[slotNumber].println("::");
          os[slotNumber].flush();
          Servidor.LiberaSlot(slotNumber);
          cont--;
          os[slotNumber].close();
          clientSocket.close();//É vantajoso fechar o socket? Ele já foi fechado pela desconexão do cliente? Se não fechar, haverá resource leak?
        }
      }while(!inputLine.equals(":::"));//CONTROLA O FIM DO LOOP DE IO COM OS CLIENTES. SAIR DESTE LOOP ENCERRA TODAS AS INTÂNCIAS DE SALA.

      //ENCERRANDO A CONEXAO
      System.out.println("SALA: Encerrando a sala");
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

//Controle de FPS do Servidor
class BackFPS extends TimerTask{
  //Controles de tempo de cada frame
  static long tempoInicio;
  static long tempoFim;
  static long tempoExecucao;
  static long tempoIntervalo=1000/48;//48 frames/s
  
  public synchronized void run(){
    try{
      while(!Sala.p1)wait(50);//espera o player1 estar conectado antes de iniciar o loop. Talvez seja melhor instanciar a sala apenas quando ela estiver cheia
      Sala.SetPlayers();//Define as referências aos players
      for(int i=0;i<Sala.MAXPLAYERS;i++)
        Sala.ResetaInputString(i);//Formata as inputStrings dos jogadores. Também é chamado no inicio do recebimento de input de cada jogador (A primeira ocorrência tem sido por lá)
      while(!Servidor.fecharSala){
        tempoInicio=System.currentTimeMillis();//Marca o tempo de início do frame
        Sala.ExecutaInputs(Sala.inputString[0], 0);//0 tem q ser substituido pelo número do player. Caso sejam mais que dois será melhor usar um "for"
        Sala.ResetaInputString(0);
        Sala.ExecutaInputs(Sala.inputString[1], 1);
        Sala.ResetaInputString(1);
        Sala.AtualizaOutputString();
        Sala.EnviaOutputString();
        tempoFim=System.currentTimeMillis();//Registra o tempo de encerramento do frame
        tempoExecucao=tempoFim-tempoInicio;//Calcula o tempo de execução deste frame
        if(tempoExecucao>tempoIntervalo*1.1)System.out.println("BACK_FPS: Frames atrasado! tempoExecucao: "+tempoExecucao+", tempoIntervalo: "+tempoIntervalo);//Notifica caso frame tenha levado mais tempo que o esperado
        while(System.currentTimeMillis()-tempoInicio<tempoIntervalo)wait(tempoIntervalo-tempoExecucao);//Espera pelo tempo do frame. Está correto?
      }
    }catch(IllegalArgumentException erro1){System.err.println("BACK_FPS: IllegalArgumentException");}
    catch(IllegalMonitorStateException erro2){System.err.println("BACK_FPS: IllegalMonitorStateException");}
    catch(InterruptedException erro3){System.err.println("BACK_FPS: InterruptedException");}
    //catch(Exception e){System.err.println("BACK_FPS: Outro erro");}
  }
}
/*
//LEGADO.
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
}//*/
