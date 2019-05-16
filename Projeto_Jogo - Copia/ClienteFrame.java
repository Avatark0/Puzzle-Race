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

  public static int inputCount=0;
  public static String inputLine;
  public static String outputString="0:1:";
  
  BufferedImage[] p1anda = new BufferedImage[Player1.descritor[Player1.ANDA][Player1.NUM]];
  BufferedImage[] p1pula = new BufferedImage[Player1.descritor[Player1.PULA][Player1.NUM]];
  BufferedImage[] p1cai = new BufferedImage[Player1.descritor[Player1.CAI][Player1.NUM]];
  BufferedImage[] p1stand = new BufferedImage[Player1.descritor[Player1.STAND][Player1.NUM]];
  //BufferedImage[] p1corre = new BufferedImage[Player1.descritor[Player1.CORRE][Player1.NUM]];

  BufferedImage[] p2anda = new BufferedImage[Player2.descritor[Player2.ANDA][Player2.NUM]];
  BufferedImage[] p2pula = new BufferedImage[Player2.descritor[Player2.PULA][Player2.NUM]];
  BufferedImage[] p2cai = new BufferedImage[Player2.descritor[Player2.CAI][Player2.NUM]];
  BufferedImage[] p2stand = new BufferedImage[Player2.descritor[Player2.STAND][Player1.NUM]];
  //BufferedImage[] p2corre = new BufferedImage[Player2.descritor[Player2.CORRE][Player2.NUM]];

  BufferedImage[] imgCenario=new BufferedImage[2];
  Image[] imgItens=new Image[2];
  
  //JANELA GRAFICA DO CLIENTE: CLASSE INTERNA, GERENCIA AS IMAGENS (passar para classe externa? Quais as vantegens/desvangens de ser uma classe interna?)
  class Janela extends JPanel{
    //Determina propriedades da janela e carrega os sprites.
    Janela(){
      try{
        setPreferredSize(new Dimension(300, 300));
        imgCenario[0]=ImageIO.read(new File("fundo.jpeg"));
        imgCenario[1]=ImageIO.read(new File("fundo2.jpeg"));
        
        //P1
        for(int row=0;row<Player1.descritor[Player1.ANDA][Player1.ROWS];row++)
          for(int col=0;col<Player1.descritor[Player1.ANDA][Player1.COLS];col++)
            if((row*(Player1.descritor[Player1.ANDA][Player1.COLS])+col)<Player1.descritor[Player1.ANDA][Player1.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p1anda[row*(Player1.descritor[Player1.ANDA][Player1.COLS])+col]=ImageIO.read(new File("P1_Anda1.png")).getSubimage(col*Player1.descritor[Player1.ANDA][Player1.WIDTH2],row*Player1.descritor[Player1.ANDA][Player1.HEIGHT2],Player1.descritor[Player1.ANDA][Player1.WIDTH2],Player1.descritor[Player1.ANDA][Player1.HEIGHT2]);

        for(int row=0;row<Player1.descritor[Player1.PULA][Player1.ROWS];row++)
          for(int col=0;col<Player1.descritor[Player1.PULA][Player1.COLS];col++)
            if((row*(Player1.descritor[Player1.PULA][Player1.COLS])+col)<Player1.descritor[Player1.PULA][Player1.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p1pula[row*(Player1.descritor[Player1.PULA][Player1.COLS])+col]=ImageIO.read(new File("P1_Pula.png")).getSubimage(col*Player1.descritor[Player1.PULA][Player1.WIDTH2],row*Player1.descritor[Player1.PULA][Player1.HEIGHT2],Player1.descritor[Player1.PULA][Player1.WIDTH2],Player1.descritor[Player1.PULA][Player1.HEIGHT2]);

        for(int row=0;row<Player1.descritor[Player1.CAI][Player1.ROWS];row++)
          for(int col=0;col<Player1.descritor[Player1.CAI][Player1.COLS];col++)
            if((row*(Player1.descritor[Player1.CAI][Player1.COLS])+col)<Player1.descritor[Player1.CAI][Player1.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p1cai[row*(Player1.descritor[Player1.CAI][Player1.COLS])+col]=ImageIO.read(new File("P1_Cai.png")).getSubimage(col*Player1.descritor[Player1.CAI][Player1.WIDTH2],row*Player1.descritor[Player1.CAI][Player1.HEIGHT2],Player1.descritor[Player1.CAI][Player1.WIDTH2],Player1.descritor[Player1.CAI][Player1.HEIGHT2]);
             
        for(int row=0;row<Player1.descritor[Player1.STAND][Player1.ROWS];row++)
          for(int col=0;col<Player1.descritor[Player1.STAND][Player1.COLS];col++)
            if((row*(Player1.descritor[Player1.STAND][Player1.COLS])+col)<Player1.descritor[Player1.STAND][Player1.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p1stand[row*(Player1.descritor[Player1.STAND][Player1.COLS])+col]=ImageIO.read(new File("P1_Stand.png")).getSubimage(col*Player1.descritor[Player1.STAND][Player1.WIDTH2],row*Player1.descritor[Player1.STAND][Player1.HEIGHT2],Player1.descritor[Player1.STAND][Player1.WIDTH2],Player1.descritor[Player1.STAND][Player1.HEIGHT2]);    

        //P2
        for(int row=0;row<Player2.descritor[Player2.ANDA][Player2.ROWS];row++)
          for(int col=0;col<Player2.descritor[Player2.ANDA][Player2.COLS];col++)
            if((row*(Player2.descritor[Player2.ANDA][Player2.COLS])+col)<Player2.descritor[Player2.ANDA][Player2.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p2anda[row*(Player2.descritor[Player2.ANDA][Player2.COLS])+col]=ImageIO.read(new File("P2_Anda1.png")).getSubimage(col*Player2.descritor[Player2.ANDA][Player2.WIDTH2],row*Player2.descritor[Player2.ANDA][Player2.HEIGHT2],Player2.descritor[Player2.ANDA][Player2.WIDTH2],Player2.descritor[Player2.ANDA][Player2.HEIGHT2]);

        for(int row=0;row<Player2.descritor[Player2.PULA][Player2.ROWS];row++)
          for(int col=0;col<Player2.descritor[Player2.PULA][Player2.COLS];col++)
            if((row*(Player2.descritor[Player2.PULA][Player2.COLS])+col)<Player2.descritor[Player2.PULA][Player2.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p2pula[row*(Player2.descritor[Player2.PULA][Player2.COLS])+col]=ImageIO.read(new File("P2_Pula.png")).getSubimage(col*Player2.descritor[Player2.PULA][Player2.WIDTH2],row*Player2.descritor[Player2.PULA][Player2.HEIGHT2],Player2.descritor[Player2.PULA][Player2.WIDTH2],Player2.descritor[Player2.PULA][Player2.HEIGHT2]);

        for(int row=0;row<Player2.descritor[Player2.CAI][Player2.ROWS];row++)
          for(int col=0;col<Player2.descritor[Player2.CAI][Player2.COLS];col++)
            if((row*(Player2.descritor[Player2.CAI][Player2.COLS])+col)<Player2.descritor[Player2.CAI][Player2.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p2cai[row*(Player2.descritor[Player2.CAI][Player2.COLS])+col]=ImageIO.read(new File("P2_Cai.png")).getSubimage(col*Player2.descritor[Player2.CAI][Player2.WIDTH2],row*Player2.descritor[Player2.CAI][Player2.HEIGHT2],Player2.descritor[Player2.CAI][Player2.WIDTH2],Player2.descritor[Player2.CAI][Player2.HEIGHT2]);
             
        for(int row=0;row<Player2.descritor[Player2.STAND][Player2.ROWS];row++)
          for(int col=0;col<Player2.descritor[Player2.STAND][Player2.COLS];col++)
            if((row*(Player2.descritor[Player2.STAND][Player2.COLS])+col)<Player2.descritor[Player2.STAND][Player2.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p2stand[row*(Player2.descritor[Player2.STAND][Player2.COLS])+col]=ImageIO.read(new File("P2_Stand.png")).getSubimage(col*Player2.descritor[Player2.STAND][Player2.WIDTH2],row*Player2.descritor[Player2.STAND][Player2.HEIGHT2],Player2.descritor[Player2.STAND][Player2.WIDTH2],Player2.descritor[Player2.STAND][Player2.HEIGHT2]);

      }catch(IOException e){
        JOptionPane.showMessageDialog(this,"A imagem nao pode ser carregada!\n"+e,"Erro",JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    }

    //Desenha os componentes na tela.
    public void paintComponent(Graphics g){
      super.paintComponent(g);
      g.drawImage(imgCenario[fundo], 0, 0, getSize().width, getSize().height, this);
      g.drawRect(Player1.HitBox().x,Player1.HitBox().y,Player1.HitBox().width,Player1.HitBox().height);
      g.drawRect(Player2.HitBox().x,Player2.HitBox().y,Player2.HitBox().width,Player2.HitBox().height);
      switch(Player1.estado){
        case Player1.ANDA:g.drawImage(p1anda[Player1.frame], Player1.sposX+Player1.direcaoReajuste, Player1.sposY, Player1.direcao*Player1.descritor[Player1.estado][Player1.WIDTH2],Player1.descritor[Player1.estado][Player1.HEIGHT2],this);break;
        case Player1.PULA:g.drawImage(p1pula[Player1.frame], Player1.sposX+Player1.direcaoReajuste, Player1.sposY, Player1.direcao*Player1.descritor[Player1.estado][Player1.WIDTH2],Player1.descritor[Player1.estado][Player1.HEIGHT2],this);break;
        case Player1.CAI:g.drawImage(p1cai[Player1.frame], Player1.sposX+Player1.direcaoReajuste, Player1.sposY, Player1.direcao*Player1.descritor[Player1.estado][Player1.WIDTH2],Player1.descritor[Player1.estado][Player1.HEIGHT2],this);break;
        case Player1.STAND:g.drawImage(p1stand[Player1.frame], Player1.sposX+Player1.direcaoReajuste, Player1.sposY, Player1.direcao*Player1.descritor[Player1.estado][Player1.WIDTH2],Player1.descritor[Player1.estado][Player1.HEIGHT2],this);break;
      }
      switch(Player2.estado){
        case Player2.ANDA:g.drawImage(p2anda[Player2.frame], Player2.sposX+Player2.direcaoReajuste, Player2.sposY, Player2.direcao*Player2.descritor[Player2.estado][Player2.WIDTH2],Player2.descritor[Player2.estado][Player2.HEIGHT2],this);break;
        case Player2.PULA:g.drawImage(p2pula[Player2.frame], Player2.sposX+Player2.direcaoReajuste, Player2.sposY, Player2.direcao*Player2.descritor[Player2.estado][Player2.WIDTH2],Player2.descritor[Player2.estado][Player2.HEIGHT2],this);break;
        case Player2.CAI:g.drawImage(p2cai[Player2.frame], Player2.sposX+Player2.direcaoReajuste, Player2.sposY, Player2.direcao*Player2.descritor[Player2.estado][Player2.WIDTH2],Player2.descritor[Player2.estado][Player2.HEIGHT2],this);break;
        case Player2.STAND:g.drawImage(p2stand[Player2.frame], Player2.sposX+Player2.direcaoReajuste, Player2.sposY, Player2.direcao*Player2.descritor[Player2.estado][Player2.WIDTH2],Player2.descritor[Player2.estado][Player2.HEIGHT2],this);break;
      }
      Toolkit.getDefaultToolkit().sync();
    }
  }

  //Construtor: instancia a Janela. Onde devem ser definidas as propriedades da janela? Aqui? No contrutor de Janela?
  ClienteFrame(){
    super("Cliente do chat");
    janela=new Janela();//instancia a janela gráfica do jogo
    setPreferredSize(new Dimension(300,300));//PreferredSize é chamado duas vezes (aqui e em Janela()). É necessário?
    add(janela,BorderLayout.CENTER);
    pack();
    setVisible(true);
    addKeyListener(this);
  }

  public static void resetaOutputString(){
    //System.out.println("CLIENTE: resetando outputString:"+outputString);
    outputString="0:1:";
  }

  //EVENTOS DO CLIENTE (os inputs do jogador)
  public synchronized void keyPressed(KeyEvent e){//Como reconhecer imputs compostos? (pulo+a, shif+a, etc) eles são recebidos separadamente. Como lidar com o shift?
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
      System.err.println("CLIENTE: Don't know about host");
    }catch(IOException e){
      System.err.println("CLIENTE: Couldn't get I/O for the connection to host");
    }catch(InterruptedException e){
      System.err.println("CLIENTE: Problema em Thread.sleep");
    }
    //CONEXÃO ESTABELECIDA COM SUCESSO
    try{
      int maxPlayers=Sala.MAXPLAYERS;
      Player1[] playerArray=new Player1[maxPlayers];
      playerArray[0]=new Player1();
      playerArray[1]=new Player2();
      //INPUTS DO SERVIDOR - AQUI AS AÇOES SAO RECEBIDAS
      do {
        inputLine=is.nextLine();//O input recebido
        inputCount++;
        String input=inputLine.substring(2);
        if(inputLine.contains("0:")){
          String ini=outputString.substring(0, outputString.indexOf("1:"));
          String end=outputString.substring(outputString.indexOf("1:"));
          ini=ini.concat(input);
          outputString=ini+end;
        }
        else if(inputLine.contains("1:")){
          outputString=outputString.concat(input);
        }
      }while(!inputLine.equals("::"));
      //CONEXÃO ENCERRADA
      os.close();
      is.close();
      socket.close();//Fechar este socket fecha o socket da vaga deste cliente no servidor?
      System.out.println("CLIENTE: Conexao encerrada com exito");
      System.exit(0);
    } catch(UnknownHostException e){
      System.err.println("CLIENTE: Trying to connect to unknown host: "+e);
    } catch(IOException e){
      System.err.println("CLIENTE: IOException: "+e);
    }
  }
}

//GERENTE_FPS: Controla a taxa de redesenho da Janela.
class GerenteFPS extends TimerTask{
  static long tempoInicio;
  static long tempoFim=0;
  static long tempoExecucao=0;
  static long tempoIntervalo=1000/24;//24 frames/s

  public synchronized void run(){
  int maxPlayers=Sala.MAXPLAYERS;
  Player1[] playerArray=new Player1[maxPlayers];
  playerArray[0]=new Player1();
  playerArray[1]=new Player2();
  String outputString;
  String p0;
  String p1;
  int inputCount=0;
    try{
      while(!Servidor.fecharSala){
        tempoInicio=System.currentTimeMillis();
        while(System.currentTimeMillis()-tempoInicio<(tempoIntervalo-tempoExecucao))wait(tempoIntervalo-tempoExecucao);//Espera pelo tempo do frame. Está correto?
        if(inputCount!=ClienteFrame.inputCount){
          inputCount=ClienteFrame.inputCount;
          outputString=ClienteFrame.outputString;
          ClienteFrame.resetaOutputString();
        }
        else{
          outputString="0:1:";
        }
        p0=outputString.substring(outputString.indexOf("0:")+2, outputString.indexOf("1:"));
        p1=outputString.substring(outputString.indexOf("1:")+2);
        playerArray[0].ExecutaAcao(p0);
        playerArray[1].ExecutaAcao(p1);
        ClienteFrame.janela.repaint();//Atualiza a janela gráfica (pelo cliente).
        tempoFim=System.currentTimeMillis();
        tempoExecucao=tempoFim-tempoInicio;
        if(tempoExecucao>tempoIntervalo*2)System.out.println("GERENTE_FPS: Frames atrasado! tempoExecucao: "+tempoExecucao+", tempoIntervalo: "+tempoIntervalo);
      }
    }catch(IllegalArgumentException erro1){System.err.println("GERENE_FPS: IllegalArgumentException");}
    catch(IllegalMonitorStateException erro2){System.err.println("GERENE_FPS: IllegalMonitorStateException");}
    catch(InterruptedException  erro3){System.err.println("GERENE_FPS: InterruptedException");}
    catch(Exception e){System.err.println("GERENE_FPS: Outro erro");}
  }
}

//A conspiracy of ravens, a murder of crows, a parliament of owls.
