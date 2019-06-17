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
    try{
      serverSocket=new ServerSocket(80);
    }catch(IOException e){
      System.err.println("SERVIDOR: Could not listen on port: "+80+", "+e);
      System.exit(1);
    }
    while(!fecharSala){//Fecha o Servidor e a Sala. Ainda não utilizado por nenhum controle (sempre false)
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
  static int cont=0;//Contagem de clientes da Sala
  static PrintStream os[]=new PrintStream[MAXPLAYERS];//Array de outputStreams da Sala
  static String[] inputString=new String[MAXPLAYERS];//Array de inputStrings da Sala
  static String outputString;//OutputString único da Sala
  static boolean[] jogadorPronto=new boolean[MAXPLAYERS];//Controle dos jogadores. Quando o jogador clica no botão jogar, seu valor vira "true"
  static boolean salaPronta=false;//Controle da Sala. Quando todos os jogadores estiverem prontos seu valor vira "true"
  static Cenario[] cenario=new Cenario[Cenario.blocosNum];//Array de blocos do cenário
  static Player1[] jogador=new Player1[MAXPLAYERS];
  public int slotNumber=0;//Número do slot de conexão do jogador com a Sala. Identifica os jogadores
  //Construtor. Atribui a conexão do clienteSocket, faz a reserva e contagem de vagas da Sala e inicializa as variáveis do jogador
  Sala(Socket clientSocket){
    this.clientSocket=clientSocket;
    for(int i=0;i<MAXPLAYERS;i++){      //Para cada vaga da Sala
      if(!Servidor.slot[i]){            //Checa se a vaga esta vazia
        slotNumber=i;                   //Se estiver, registra o valor da vaga
        Servidor.OcupaSlot(slotNumber); //Modifica a vaga para ocupada
        cont++;                         //Aumenta a contagem de vagas ocupadas
        break;
      }
    }
    //Inicializa as variáveis do jogador
    jogadorPronto[slotNumber]=false;
    inputString[slotNumber]="";
    jogador[slotNumber]=new Player1();
    jogador[slotNumber].slotNumber=slotNumber;
  }
  //Instancia o Cenário da partida. É chamado junto com a instanciação do BackFPS (Antes das posições e ações dos jogadores serem computadas)
  void setCenario(){
    for(int i=0;i<Cenario.blocosNum;i++){
      cenario[i]=new Cenario(i);
    }
  }
  //Controle de encerramento da partida. Atualmente apenas uma imagem é desenhada sobre a tela ao acabar a partida
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
  //Chama as funções de cálculo dos inputs de cada jogador
  static void ExecutaInputs(String input, int player){
    switch(player){
      case 0: jogador[0].ExecutaAcao(input);break;
      case 1: jogador[1].ExecutaAcao(input);break;
    }
  }
  //Utilizado pelo BackFPS para resetar a inputString do jogador entre frames
  static void ResetaInputString(int player){
    inputString[player]="";
  }
  //Checa as posições dos Jogadores e atualiza os valores na outputString
  static void AtualizaOutputString(){
    String aux="";
    for(int i=0;i<MAXPLAYERS;i++){
      String posX=String.valueOf(jogador[i].posX);
      String posY=String.valueOf(jogador[i].posY);
      String estado=String.valueOf(jogador[i].estado);
      String direcao=String.valueOf(jogador[i].direcao);
      aux=aux.concat(i+":").concat(posX).concat(",").concat(posY).concat(",").concat(estado).concat(",").concat(direcao).concat("."+i+".");
    }
    outputString=aux;
  }
  //Envia a outputString completa para todos os jogadores
  static void EnviaOutputString(){
    for(int i=0;i<MAXPLAYERS;i++)
      if(Servidor.slot[i]){
        os[i].println(outputString);
        os[i].flush();
      }
  }

  public void run(){
    try{
      //Recebe o cliente do servidor, e estabelece a conexão no socketNumber disponível de menor index.
      Scanner is=new Scanner(clientSocket.getInputStream());//Estabelece o InPutStream da sala.
      os[slotNumber]=new PrintStream(clientSocket.getOutputStream());//Estabelece o OutPutStream da sala.
      String inputLine;//Input a ser recebido pelo cliente.
      char ESCAPE[]={(char)(27)};//Não consegui checar por ESC na inputLine de um jeito melhor...
      String ESC=new String(ESCAPE);//Mas deve haver um jeito melhor...
      
      System.out.println("SALA: slotNumber "+slotNumber+", cont "+cont+", maxplayers "+MAXPLAYERS);
      //Loop de leitura dos inputs do cliente.
      do{
        inputLine = is.nextLine();//Recebe o input do cliente
        if(salaPronta){//Recebimento principal de inputs, após o inicio da partida
          inputString[slotNumber]=inputString[slotNumber].concat(inputLine);//Concatena na inputString do cliente para este frame
          if(inputLine.contains(ESC)){//Solicitada desconexão por parte do cliente. (inputLine contém ESC)
            os[slotNumber].println("::");
            os[slotNumber].flush();
            Servidor.LiberaSlot(slotNumber);
            cont--;
            os[slotNumber].close();
            clientSocket.close();//É vantajoso fechar o socket? Ele já foi fechado pela desconexão do cliente? Se não fechar, haverá resource leak?
          }
        }
        else if(inputLine.contains("pronto")){//Controle de prontidão dos jogadores e da Sala
          System.out.println("Sala: cliente "+slotNumber+" pronto.");
          jogadorPronto[slotNumber]=true;
          if(cont==MAXPLAYERS){
            int contAux=cont;
            for(int i=0;i<MAXPLAYERS;i++)
              if(jogadorPronto[i]==true)contAux--;
            if(contAux==0){
              for(int i=0;i<MAXPLAYERS;i++){
                os[slotNumber].println("pronto");
                os[slotNumber].flush();
              }
              setCenario();
              new Thread(new BackFPS()).start();
              salaPronta=true;
              System.out.println("Sala: sala pronta!");
            }
          }
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
  //Controle de FPS do Servidor
  static class BackFPS extends TimerTask{
    //Controles de tempo de cada frame
    static long tempoInicio;
    static long tempoFim;
    static long tempoExecucao;
    static long tempoIntervalo=1000/24;//24 frames/s (valor reduzido para metado do FPS do frontEnd. Isto elimina inputs "mortos")
    
    public synchronized void run(){
      System.out.println("BackFPS iniciado!");
      try{
        while(!salaPronta)wait(100);//espera todos os jogadores estarem prontos (clicarem no bt jogar) antes de iniciar o loop de jogo.
        while(!Servidor.fecharSala){
          tempoInicio=System.currentTimeMillis();//Marca o tempo de início do frame
          for(int i=0;i<MAXPLAYERS;i++){
            ExecutaInputs(inputString[i], i);//i é o slotNumber do player.
            ResetaInputString(i);
          }
          AtualizaOutputString();
          EnviaOutputString();
          tempoFim=System.currentTimeMillis();//Registra o tempo de encerramento do frame
          tempoExecucao=tempoFim-tempoInicio;//Calcula o tempo de execução deste frame
          if(tempoExecucao>tempoIntervalo*1.1)System.out.println("BACK_FPS: Frames atrasado! tempoExecucao: "+tempoExecucao+", tempoIntervalo: "+tempoIntervalo);//Notifica caso frame tenha levado mais tempo que o esperado
          while(System.currentTimeMillis()-tempoInicio<tempoIntervalo)wait(tempoIntervalo-tempoExecucao);//Espera pelo tempo do frame. Está correto?
        }
      }catch(IllegalArgumentException erro1){System.err.println("BACK_FPS: IllegalArgumentException");}
      catch(IllegalMonitorStateException erro2){System.err.println("BACK_FPS: IllegalMonitorStateException");}
      catch(InterruptedException erro3){System.err.println("BACK_FPS: InterruptedException");}
    }
  }
}
