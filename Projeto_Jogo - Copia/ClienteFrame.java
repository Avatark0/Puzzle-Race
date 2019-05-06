import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//Classes: ClienteFrame.

public class ClienteFrame extends JFrame implements Runnable, KeyListener{
  static PrintStream os = null;

  static JPanel janela;
  //CLASSES(OBJETOS DO JOGO)
  int fundo=0;
  Player1 player1=new Player1();
  Player2 player2=new Player2();
  
  BufferedImage[] imgCenario=new BufferedImage[2];
  Image[] imgItens  =new Image[2];
  
  //JANELA GRAFICA DO CLIENTE: CLASSE INTERNA, GERENCIA AS IMAGENS (passar para classe externa?)
  class Janela extends JPanel{
    //Determina propriedades da janela (atualmente carrega alguns sprites, mas o carregamento será movido para as próprias classes)
    Janela(){
      try{
        setPreferredSize(new Dimension(1000, 600));
        imgCenario[0]=ImageIO.read(new File("fundo.jpeg"));
        imgCenario[1]=ImageIO.read(new File("fundo2.jpeg"));
      }catch(IOException e){
        JOptionPane.showMessageDialog(this,"A imagem nao pode ser carregada!\n"+e,"Erro",JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    }

    //Desenha os componentes na tela.
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      g.drawImage(imgCenario[fundo], 0, 0, getSize().width, getSize().height, this);
      
      switch(Player1.estado){
        case Player1.ANDA:g.drawImage(player1.anda[Player1.frame], Player1.posX*Player1.direcao, Player1.posY, Player1.descritor[Player1.estado][Player1.WIDTH2],Player1.descritor[Player1.estado][Player1.HEIGHT2],this);break;
        case Player1.PULA:g.drawImage(player1.pula[Player1.frame], Player1.posX*Player1.direcao, Player1.posY, Player1.descritor[Player1.estado][Player1.WIDTH2],Player1.descritor[Player1.estado][Player1.HEIGHT2],this);break;
      }
      switch(Player2.estado){
        case Player2.ANDA:g.drawImage(player2.anda[Player2.frame], Player2.posX*Player2.direcao, Player2.posY, Player2.descritor[Player2.estado][Player2.WIDTH2],Player2.descritor[Player2.estado][Player2.HEIGHT2],this);break;
        case Player2.PULA:g.drawImage(player2.pula[Player2.frame], Player2.posX*Player2.direcao, Player2.posY, Player2.descritor[Player2.estado][Player2.WIDTH2],Player2.descritor[Player2.estado][Player2.HEIGHT2],this);break;
      }
      Toolkit.getDefaultToolkit().sync();
    }
  }

  //Construtor: instancia a Janela.
  ClienteFrame(){
    super("Cliente do chat");
    janela=new Janela();//instancia a janela grafica do jogo
    
    setPreferredSize(new Dimension(1000,600));
    add(janela,BorderLayout.CENTER);
    pack();
    setVisible(true);
    addKeyListener(this);
  }

  //EVENTOS DO CLIENTE (os inputs do jogador)
  public void keyPressed(KeyEvent e){
    char key=e.getKeyChar();
    os.println(key);
  }
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}

  //MAIN - INICIA OS THREADS
  public static void main(String[] args){
    new Thread(new ClienteFrame()).start();
    new Thread(new GerenteFPS()).start();
  }

  public void run(){
    Socket socket=null;
    Scanner is=null;

    //TENTATIVA DE CONEXÃO COM O SERVIDOR
    try{
      while(Servidor.salaCheia)
        Thread.sleep(500);//Intervalo de 0.5 segundos antes de checar o servidor por vagas.
      socket=new Socket("127.0.0.1", 80);
      os=new PrintStream(socket.getOutputStream(), true);
      is=new Scanner(socket.getInputStream());
    }catch(UnknownHostException e){
      System.err.println("CLIENTE: Don't know about host.");
    }catch(IOException e){
      System.err.println("CLIENTE: Couldn't get I/O for the connection to host");
    }catch(InterruptedException e){
      System.err.println("CLIENTE: Problema em Thread.sleep");
    }
    //CONEXÃO ESTABELECIDA COM SUCESSO
    try{
      int maxPlayers=Sala.MAXPLAYERS;
      Player1[] playerArray=new Player1[maxPlayers];
      for(int i=0;i<maxPlayers;i++){
        playerArray[i]=new Player1();
      }
      int indexIni;
      int indexEnd;
      String inputLine;
      String input;

      //INPUTS DO SERVIDOR - AQUI AS AÇOES SAO RECEBIDAS
      do {
        inputLine=is.nextLine();//O input recebido
        for(int i=0;i<maxPlayers;i++){
          indexIni=3+inputLine.indexOf("P"+i+":");//identificador (Pi:) + 3
          if(i+1<maxPlayers){
            indexEnd=inputLine.indexOf("P"+(i+1)+":");//identificador seguinte
            input=inputLine.substring(indexIni,indexEnd);
          }
          else input=inputLine.substring(indexIni);
          //System.out.println(playerArray[i].getClass());
          //if(input.contains("a"))(playerArray[i].getClass()).SetAcao("a");//Corrigir uso do getClass
          if(input.contains("a"))Player1.SetAcao(input);
        }
        /*
          System.out.println(inputLine+" ");
          Player1.SetAcao(inputLine);
          Player2.SetAcao(inputLine);
        //*/
      } while (!inputLine.equals(""));
      //CONEXÃO ENCERRADA
      os.close();
      is.close();
      socket.close();//Fechar este socket fecha o socket da vaga deste cliente no servidor?
      System.out.println("CLIENTE: Conexao encerrada com exito;");
      System.exit(0);
    } catch(UnknownHostException e){
      System.err.println("CLIENTE: Trying to connect to unknown host: "+e);
    } catch(IOException e){
      System.err.println("CLIENTE: IOException:  "+e);
    }
  }
}

//GERENTE_FPS: Controla a taxa de redesenho da Janela.
class GerenteFPS extends TimerTask{
  static long tempoInicio;
  static long tempoFim=0;
  static long tempoExecucao=0;
  static long tempoIntervalo=1000/24;//intenção futura: 24 frames/s
  static boolean frameFlag=false;

  static void FrameFlag(){
      frameFlag=false;
  }

  public void run(){
    try{
      while(!Servidor.fecharSala){//Qual condição de encerramento do loop seria mais adequada? Acessar a var do Servidor é custoso?
        tempoInicio=System.currentTimeMillis();
        while(System.currentTimeMillis()-tempoInicio<(tempoIntervalo-tempoExecucao));//Espera pelo tempo so frame. Está correto?
        ClienteFrame.janela.repaint();//Atualiza a janela gráfica (pelo cliente).
        tempoFim=System.currentTimeMillis();
        tempoExecucao=tempoFim-tempoInicio;
        if(tempoExecucao>tempoIntervalo*2)System.out.println("GERENTE_FPS: Frames atrasando! tempoExecucao: "+tempoExecucao+", tempoIntervalo: "+tempoIntervalo);
      }
    }catch(Exception e){
      System.err.println("GERENE_FPS: erro");
    }
  }
}

//A conspiracy of ravens, a murder of crows, a parliament of owls.
