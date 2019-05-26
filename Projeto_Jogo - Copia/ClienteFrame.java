import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//Classes: ClienteFrame, GerenteFPS.

public class ClienteFrame extends JFrame implements Runnable, KeyListener{
  //I/O
  static PrintStream os = null;
  public static long inputCount=0;
  public static String inputLine;
  public static String outputString="0:1:";
  //Janela do jogo
  static JPanel janela;
  //CLASSES(OBJETOS DO JOGO)
  int fundo=0;
  Player1 player1=new Player1();
  Player2 player2=new Player2();
  //Cenário (em construção)
  public static int blocosNum=6;//Número de blocos do cenário
  public static Cenario[] cenario=new Cenario[blocosNum];//Vetor de blocos do cenário
  //SpriteSheets de Player1
  BufferedImage[] p1anda = new BufferedImage[Player1.descritor[Player1.ANDA][Player1.NUM]];
  BufferedImage[] p1pula = new BufferedImage[Player1.descritor[Player1.PULA][Player1.NUM]];
  BufferedImage[] p1cai = new BufferedImage[Player1.descritor[Player1.CAI][Player1.NUM]];
  BufferedImage[] p1PARADO = new BufferedImage[Player1.descritor[Player1.PARADO][Player1.NUM]];
  //BufferedImage[] p1corre = new BufferedImage[Player1.descritor[Player1.CORRE][Player1.NUM]];
  //SpriteSheets de Player2
  BufferedImage[] p2anda = new BufferedImage[Player2.descritor[Player2.ANDA][Player2.NUM]];
  BufferedImage[] p2pula = new BufferedImage[Player2.descritor[Player2.PULA][Player2.NUM]];
  BufferedImage[] p2cai = new BufferedImage[Player2.descritor[Player2.CAI][Player2.NUM]];
  BufferedImage[] p2PARADO = new BufferedImage[Player2.descritor[Player2.PARADO][Player1.NUM]];
  //BufferedImage[] p2corre = new BufferedImage[Player2.descritor[Player2.CORRE][Player2.NUM]];
  //Sprites dos demais objetos
  BufferedImage[] imgCenario=new BufferedImage[2];
  Image[] imgItens=new Image[2];
  
  //JANELA GRAFICA DO CLIENTE: CLASSE INTERNA, GERENCIA AS IMAGENS (passar para classe externa? Quais as vantegens/desvangens de ser uma classe interna?)
  class Janela extends JPanel{
    //Determina propriedades da janela e carrega os sprites.
    Janela(){
      try{
        setPreferredSize(new Dimension(800, 600));
        imgCenario[0]=ImageIO.read(new File("fundo.jpeg"));
        imgCenario[1]=ImageIO.read(new File("fundo2.jpeg"));
        
        //P1
        for(int row=0;row<Player1.descritor[Player1.ANDA][Player1.ROWS];row++)
          for(int col=0;col<Player1.descritor[Player1.ANDA][Player1.COLS];col++)
            if((row*(Player1.descritor[Player1.ANDA][Player1.COLS])+col)<Player1.descritor[Player1.ANDA][Player1.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p1anda[row*(Player1.descritor[Player1.ANDA][Player1.COLS])+col]=ImageIO.read(new File("P1_Anda1.png")).getSubimage(col*Player1.descritor[Player1.ANDA][Player1.LARGURA],row*Player1.descritor[Player1.ANDA][Player1.ALTURA],Player1.descritor[Player1.ANDA][Player1.LARGURA],Player1.descritor[Player1.ANDA][Player1.ALTURA]);

        for(int row=0;row<Player1.descritor[Player1.PULA][Player1.ROWS];row++)
          for(int col=0;col<Player1.descritor[Player1.PULA][Player1.COLS];col++)
            if((row*(Player1.descritor[Player1.PULA][Player1.COLS])+col)<Player1.descritor[Player1.PULA][Player1.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p1pula[row*(Player1.descritor[Player1.PULA][Player1.COLS])+col]=ImageIO.read(new File("P1_Pula.png")).getSubimage(col*Player1.descritor[Player1.PULA][Player1.LARGURA],row*Player1.descritor[Player1.PULA][Player1.ALTURA],Player1.descritor[Player1.PULA][Player1.LARGURA],Player1.descritor[Player1.PULA][Player1.ALTURA]);

        for(int row=0;row<Player1.descritor[Player1.CAI][Player1.ROWS];row++)
          for(int col=0;col<Player1.descritor[Player1.CAI][Player1.COLS];col++)
            if((row*(Player1.descritor[Player1.CAI][Player1.COLS])+col)<Player1.descritor[Player1.CAI][Player1.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p1cai[row*(Player1.descritor[Player1.CAI][Player1.COLS])+col]=ImageIO.read(new File("P1_Cai.png")).getSubimage(col*Player1.descritor[Player1.CAI][Player1.LARGURA],row*Player1.descritor[Player1.CAI][Player1.ALTURA],Player1.descritor[Player1.CAI][Player1.LARGURA],Player1.descritor[Player1.CAI][Player1.ALTURA]);
             
        for(int row=0;row<Player1.descritor[Player1.PARADO][Player1.ROWS];row++)
          for(int col=0;col<Player1.descritor[Player1.PARADO][Player1.COLS];col++)
            if((row*(Player1.descritor[Player1.PARADO][Player1.COLS])+col)<Player1.descritor[Player1.PARADO][Player1.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p1PARADO[row*(Player1.descritor[Player1.PARADO][Player1.COLS])+col]=ImageIO.read(new File("P1_Parado.png")).getSubimage(col*Player1.descritor[Player1.PARADO][Player1.LARGURA],row*Player1.descritor[Player1.PARADO][Player1.ALTURA],Player1.descritor[Player1.PARADO][Player1.LARGURA],Player1.descritor[Player1.PARADO][Player1.ALTURA]);    

        //P2
        for(int row=0;row<Player2.descritor[Player2.ANDA][Player2.ROWS];row++)
          for(int col=0;col<Player2.descritor[Player2.ANDA][Player2.COLS];col++)
            if((row*(Player2.descritor[Player2.ANDA][Player2.COLS])+col)<Player2.descritor[Player2.ANDA][Player2.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p2anda[row*(Player2.descritor[Player2.ANDA][Player2.COLS])+col]=ImageIO.read(new File("P2_Anda1.png")).getSubimage(col*Player2.descritor[Player2.ANDA][Player2.LARGURA],row*Player2.descritor[Player2.ANDA][Player2.ALTURA],Player2.descritor[Player2.ANDA][Player2.LARGURA],Player2.descritor[Player2.ANDA][Player2.ALTURA]);

        for(int row=0;row<Player2.descritor[Player2.PULA][Player2.ROWS];row++)
          for(int col=0;col<Player2.descritor[Player2.PULA][Player2.COLS];col++)
            if((row*(Player2.descritor[Player2.PULA][Player2.COLS])+col)<Player2.descritor[Player2.PULA][Player2.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p2pula[row*(Player2.descritor[Player2.PULA][Player2.COLS])+col]=ImageIO.read(new File("P2_Pula.png")).getSubimage(col*Player2.descritor[Player2.PULA][Player2.LARGURA],row*Player2.descritor[Player2.PULA][Player2.ALTURA],Player2.descritor[Player2.PULA][Player2.LARGURA],Player2.descritor[Player2.PULA][Player2.ALTURA]);

        for(int row=0;row<Player2.descritor[Player2.CAI][Player2.ROWS];row++)
          for(int col=0;col<Player2.descritor[Player2.CAI][Player2.COLS];col++)
            if((row*(Player2.descritor[Player2.CAI][Player2.COLS])+col)<Player2.descritor[Player2.CAI][Player2.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p2cai[row*(Player2.descritor[Player2.CAI][Player2.COLS])+col]=ImageIO.read(new File("P2_Cai.png")).getSubimage(col*Player2.descritor[Player2.CAI][Player2.LARGURA],row*Player2.descritor[Player2.CAI][Player2.ALTURA],Player2.descritor[Player2.CAI][Player2.LARGURA],Player2.descritor[Player2.CAI][Player2.ALTURA]);
             
        for(int row=0;row<Player2.descritor[Player2.PARADO][Player2.ROWS];row++)
          for(int col=0;col<Player2.descritor[Player2.PARADO][Player2.COLS];col++)
            if((row*(Player2.descritor[Player2.PARADO][Player2.COLS])+col)<Player2.descritor[Player2.PARADO][Player2.NUM])//Checa se a sprite-sheet é totalmente preenchida
              p2PARADO[row*(Player2.descritor[Player2.PARADO][Player2.COLS])+col]=ImageIO.read(new File("P2_Parado.png")).getSubimage(col*Player2.descritor[Player2.PARADO][Player2.LARGURA],row*Player2.descritor[Player2.PARADO][Player2.ALTURA],Player2.descritor[Player2.PARADO][Player2.LARGURA],Player2.descritor[Player2.PARADO][Player2.ALTURA]);

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
      ////////////////////
      //Atualmente Cenario tem hitbox estática. Será necessário instânciar cada unidade do cenário, ou é possivel armazenar tudo de forma estática?
      for(int i=0; i<blocosNum; i++){
        g.drawRect(cenario[i].HitBox().x,cenario[i].HitBox().y,cenario[i].HitBox().width,cenario[i].HitBox().height);
      }
      ///////////////////
      switch(Player1.estado){
        case Player1.ANDA:g.drawImage(p1anda[Player1.frame], Player1.sposX+Player1.direcaoReajuste, Player1.sposY, Player1.direcao*Player1.descritor[Player1.estado][Player1.LARGURA],Player1.descritor[Player1.estado][Player1.ALTURA],this);break;
        case Player1.PULA:g.drawImage(p1pula[Player1.frame], Player1.sposX+Player1.direcaoReajuste, Player1.sposY, Player1.direcao*Player1.descritor[Player1.estado][Player1.LARGURA],Player1.descritor[Player1.estado][Player1.ALTURA],this);break;
        case Player1.CAI:g.drawImage(p1cai[Player1.frame], Player1.sposX+Player1.direcaoReajuste, Player1.sposY, Player1.direcao*Player1.descritor[Player1.estado][Player1.LARGURA],Player1.descritor[Player1.estado][Player1.ALTURA],this);break;
        case Player1.PARADO:g.drawImage(p1PARADO[Player1.frame], Player1.sposX+Player1.direcaoReajuste, Player1.sposY, Player1.direcao*Player1.descritor[Player1.estado][Player1.LARGURA],Player1.descritor[Player1.estado][Player1.ALTURA],this);break;
      }
      switch(Player2.estado){
        case Player2.ANDA:g.drawImage(p2anda[Player2.frame], Player2.sposX+Player2.direcaoReajuste, Player2.sposY, Player2.direcao*Player2.descritor[Player2.estado][Player2.LARGURA],Player2.descritor[Player2.estado][Player2.ALTURA],this);break;
        case Player2.PULA:g.drawImage(p2pula[Player2.frame], Player2.sposX+Player2.direcaoReajuste, Player2.sposY, Player2.direcao*Player2.descritor[Player2.estado][Player2.LARGURA],Player2.descritor[Player2.estado][Player2.ALTURA],this);break;
        case Player2.CAI:g.drawImage(p2cai[Player2.frame], Player2.sposX+Player2.direcaoReajuste, Player2.sposY, Player2.direcao*Player2.descritor[Player2.estado][Player2.LARGURA],Player2.descritor[Player2.estado][Player2.ALTURA],this);break;
        case Player2.PARADO:g.drawImage(p2PARADO[Player2.frame], Player2.sposX+Player2.direcaoReajuste, Player2.sposY, Player2.direcao*Player2.descritor[Player2.estado][Player2.LARGURA],Player2.descritor[Player2.estado][Player2.ALTURA],this);break;
      }
      Toolkit.getDefaultToolkit().sync();
    }
  }

  //Construtor: instancia a Janela. Onde devem ser definidas as propriedades da janela? Aqui? No contrutor de Janela?
  ClienteFrame(){
    super("Cliente do chat");
    janela=new Janela();//instancia a janela gráfica do jogo
    setPreferredSize(new Dimension(800,600));//PreferredSize é chamado duas vezes (aqui e em Janela()). Onde é melhor?
    add(janela,BorderLayout.CENTER);
    pack();
    setVisible(true);
    addKeyListener(this);
  }

  //Utilizado pelo Gerente_FPS para resetar a outputString a cada frame
  public static void resetaOutputString(){
    //System.out.println("CLIENTE: resetando outputString:"+outputString);
    outputString="0:1:";
  }

  //Em construção
  static void ConstroiCenario(){
    for(int i=0; i<blocosNum; i++){
      cenario[i]=new Cenario(i);
    }
  }

  //EVENTOS DO CLIENTE (os inputs do jogador)
  public synchronized void keyPressed(KeyEvent e){//Como reconhecer imputs compostos? (pulo+a, shif+a, etc) eles são recebidos separadamente. Como lidar com o shift? - pressed e released.
    char key=e.getKeyChar();
    os.println(key);
  }
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}

  //MAIN - INICIA OS THREADS
  public static void main(String[] args){
    new Thread(new ClienteFrame()).start();
    new Thread(new GerenteFPS()).start();
    ConstroiCenario();
  }

  //Faz a conexão e comunicação com o servidor
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
      //INPUTS DO SERVIDOR - AQUI AS AÇÕES SÃO RECEBIDAS
      do {
        inputLine=is.nextLine();//O input recebido pelo servidor
        inputCount++;//Atualiza a contagem de inputs
        //Concatena o input na outputString (no trecho do jogador que executou o input)
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
      }while(!inputLine.equals("::"));//Comando de encerramento da conexão pelo cliente
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

//GERENTE_FPS: Controla a taxa de redesenho da Janela e a aplicação da outputString a cada frame
class GerenteFPS extends TimerTask{
  //Controles de tempo de cada frame
  static long tempoInicio;
  static long tempoFim=0;
  static long tempoExecucao=0;
  static long tempoIntervalo=1000/24;//24 frames/s

  public synchronized void run(){
  int maxPlayers=Sala.MAXPLAYERS;
  Player1[] playerArray=new Player1[maxPlayers];//Array de jogadores. Cada jogador pussui uma classe própria, todos extendidos de Player1
  playerArray[0]=new Player1();
  playerArray[1]=new Player2();
  //Strings dos inputs
  String outputString;
  String p0;
  String p1;
  long inputCount=0;//Recebe a contagem de inputs do Cliente
    try{
      //Loop de cada frame
      while(!Servidor.fecharSala){
        tempoInicio=System.currentTimeMillis();//Marca o tempo de inicio do frame
        while(System.currentTimeMillis()-tempoInicio<(tempoIntervalo-tempoExecucao))wait(tempoIntervalo-tempoExecucao);//Espera pelo tempo do frame. Está correto? - funcina corretamente...
        if(inputCount!=ClienteFrame.inputCount){//Checa se a última outputString já foi computada
          inputCount=ClienteFrame.inputCount;//Caso não tenha sido, atualiza a contagem 
          outputString=ClienteFrame.outputString;//Computa a outputString
          ClienteFrame.resetaOutputString();//Reseta a outputString para o próximo frame
        }
        else{//Caso a última outputString já tenha sido computada, reseta seu valor local (sem alterar a contagem, apenas o cliente altera a contagem com inputs novos)
          outputString="0:1:";
        }
        //Extrai os inputs individuais para cada Player
        p0=outputString.substring(outputString.indexOf("0:")+2, outputString.indexOf("1:"));
        p1=outputString.substring(outputString.indexOf("1:")+2);
        //Executa as ações dos inputs e do frame de cada Player
        playerArray[0].ExecutaAcao(p0);
        playerArray[1].ExecutaAcao(p1);
        //Atualiza a janela gráfica (pelo cliente).
        ClienteFrame.janela.repaint();
        //Controla o encerramento do frame
        tempoFim=System.currentTimeMillis();//Registra o tempo de encerramento do frame
        tempoExecucao=tempoFim-tempoInicio;//Calcula o tempo de execução deste frame
        if(tempoExecucao>tempoIntervalo*2)System.out.println("GERENTE_FPS: Frames atrasado! tempoExecucao: "+tempoExecucao+", tempoIntervalo: "+tempoIntervalo);//Notifica caso frame tenha levado mais tempo que o esperado
      }
    }catch(IllegalArgumentException erro1){System.err.println("GERENE_FPS: IllegalArgumentException");}
    catch(IllegalMonitorStateException erro2){System.err.println("GERENE_FPS: IllegalMonitorStateException");}
    catch(InterruptedException  erro3){System.err.println("GERENE_FPS: InterruptedException");}
    catch(Exception e){System.err.println("GERENE_FPS: Outro erro");}
  }
}

//A conspiracy of ravens, a murder of crows, a parliament of owls.
