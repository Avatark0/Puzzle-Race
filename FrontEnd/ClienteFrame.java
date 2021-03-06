import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//Classes: ClienteFrame, GerenteFPS.

public class ClienteFrame extends JFrame implements Runnable, KeyListener, ActionListener{
  //I/O
  static PrintStream os = null;
  static boolean osSet=false;//Checa se o cliente já estabeleceu a conexão com o servidor antes de enviar os inputs
  public static long inputCount=0;
  public static String inputLine;
  public static String outputString="0:1:";
  //Janela do jogo
  static JPanel cards; //painel que usa CardLayout
  static JPanel janela;
  static JPanel menu;
  public static final int MENU = 0;
  public static final int JOGO = 1;
  public static int VITORIA = -1;
  public static int estadoJogo = MENU;
  //CLASSES(OBJETOS DO JOGO)
  int fundo=0;
  Player1 player1=new Player1();
  Player2 player2=new Player2();
  //Cenário (em construção)
  public static int blocosNum=6;//Número de blocos do cenário
  //public static Cenario[] cenario=new Cenario[blocosNum];//Vetor de blocos do cenário
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
  BufferedImage[] imgFim= new BufferedImage[2];
  //Menu
  JButton btnJogar;
  JButton btnSair;
  Image imgJogar;
  Image imgSair;
  ImageIcon icoSair;
  ImageIcon icoJogar;

  //JANELA GRAFICA DO CLIENTE: CLASSE INTERNA, GERENCIA AS IMAGENS (passar para classe externa? Quais as vantegens/desvangens de ser uma classe interna?)
  class Janela extends JPanel{
    //Determina propriedades da janela e carrega os sprites.
    Janela(){
      try{
        imgCenario[0]=ImageIO.read(new File("Jogo.png"));
        //imgCenario[1]=ImageIO.read(new File("fundo2.jpeg"));
        imgFim[0] = ImageIO.read(new File("vitoria1.png"));
        imgFim[1] = ImageIO.read(new File("vitoria2.png"));
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
     /* g.drawRect(21,186,10,14);
      for(int i=0; i<Cenario.blocosNum; i++){
        int posX=0,posY=0,sizeX=0,sizeY=0;
        switch(i){
          //Contorno
          case  0:posX=   0;posY=630;sizeX=1080;sizeY= 20;break;//Chão
          case  1:posX=   0;posY=-50;sizeX=  20;sizeY=700;break;//Parede esquerda
          case  2:posX=1060;posY=-50;sizeX=  20;sizeY=700;break;//Parede direita
          //Escada 1
          case  3:posX=  20;posY=430;sizeX=  50;sizeY= 50;break;//Escada 1, bloco 1
          case  4:posX=  70;posY=480;sizeX=  50;sizeY= 50;break;//Escada 1, bloco 2
          case  5:posX= 120;posY=530;sizeX=  50;sizeY= 50;break;//Escada 1, bloco 3
          case  6:posX= 170;posY=580;sizeX=  50;sizeY= 50;break;//Escada 1, bloco 4
          //Corredor 2
          case  7:posX= 150;posY=410;sizeX= 360;sizeY= 10;break;//Plataforma esquerda
          case  8:posX= 570;posY=430;sizeX= 240;sizeY= 10;break;//Plataforma direita
          case  9:posX= 860;posY=490;sizeX= 100;sizeY= 10;break;//Plataforma esquerda
          //Escada 3
          case 10:posX=1000;posY=420;sizeX=  60;sizeY= 50;break;//Plataforma direita
          case 11:posX=1030;posY=370;sizeX=  30;sizeY= 50;break;//Plataforma esquerda
          case 12:posX= 930;posY=300;sizeX=  50;sizeY= 10;break;//Plataforma direita
          //Plataformas 4
          case 13:posX= 730;posY=280;sizeX= 100;sizeY= 10;break;//Plataforma esquerda
          case 14:posX= 530;posY=250;sizeX=  60;sizeY= 10;break;//Plataforma direita
          case 15:posX= 430;posY=180;sizeX=  40;sizeY= 10;break;//Plataforma esquerda
          case 16:posX= 330;posY=110;sizeX=  20;sizeY= 10;break;//Plataforma direita
          //Desafio final 5
          case 17:posX=  80;posY=270;sizeX=  10;sizeY= 10;break;//Plataforma esquerda
          case 18:posX=  20;posY=200;sizeX=  20;sizeY= 10;break;//Plataforma direita
          //
        }
        g.drawRect(posX,posY,sizeX,sizeY);
      }*/
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
      if(VITORIA != -1)
        g.drawImage(imgFim[VITORIA], 0, 0, getSize().width, getSize().height, this);
      Toolkit.getDefaultToolkit().sync();
    }
  }

  class Menu extends JPanel{
	Menu(){
		try{
			imgCenario[1]=ImageIO.read(new File("Menu.png"));
		}catch(IOException e){
			JOptionPane.showMessageDialog(this,"A imagem nao pode ser carregada!\n"+e,"Erro",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	public void paintComponent(Graphics g){
      super.paintComponent(g);
	  g.drawImage(imgCenario[1], 0, 0, getSize().width, getSize().height, this);
	}
  }

  //Construtor: instancia a Janela.
  ClienteFrame(){
    super("PuzzleRace");
    setPreferredSize(new Dimension(1098, 680));//1080+18;650+30
    cards = new JPanel(new CardLayout());
    menu = new Menu();
    try{
			imgJogar = ImageIO.read(new File("btnJogar.png"));
      imgSair = ImageIO.read(new File("btnSair.png"));
		}catch(IOException e){
			JOptionPane.showMessageDialog(this,"A imagem nao pode ser carregada!\n"+e,"Erro",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
    }
    icoJogar = new ImageIcon(imgJogar);
    icoSair = new ImageIcon(imgSair);
    btnJogar = new JButton(icoJogar);
    btnSair = new JButton(icoSair);
    btnJogar.addActionListener(this);
    btnSair.addActionListener(this);
    btnJogar.setBounds(680,398,339,92);
    btnSair.setBounds(680,520,339,92);
    //Color c = new Color(1,1,1,0);
    //btnJogar.setBackground(c);
    //btnSair.setBackground(c);
    menu.setLayout(null);
    menu.add(btnJogar);
    menu.add(btnSair);
    janela=new Janela();//instancia a janela gráfica do jogo
    cards.add(menu,"MENU");
    cards.add(janela,"JANELA");
    add(cards);
    addKeyListener(this);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setVisible(true);
    pack();
	////////////////
	menu.repaint();
    ////////////////
  }
  
  static boolean keyA=false;
  static boolean keyW=false;
  static boolean keyS=false;
  static boolean keyD=false;
  static boolean keySpace=false;
  char ESCAPE=(char)(27);
  
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == btnJogar){
      estadoJogo = JOGO;
      os.println("pronto");os.flush();//jogador envia ao servidor string "pronto" ao clicar no botão jogar
      menu.repaint();
      requestFocus();
    }else if(e.getSource() == btnSair){
      FecharJogo();
    }
  }

  //EVENTOS DO CLIENTE (os inputs do jogador)
  public synchronized void keyPressed(KeyEvent e){//Como reconhecer imputs compostos? (pulo+a, shif+a, etc) eles são recebidos separadamente. Como lidar com o shift? - pressed e released.
    char key=e.getKeyChar();
    if(key=='a'||key=='A')keyA=true;
    if(key=='w'||key=='W')keyW=true;
    if(key=='s'||key=='S')keyS=true;
    if(key=='d'||key=='D')keyD=true;
    if(key==' ')keySpace=true;
  }
  public void keyReleased(KeyEvent e){
    char key=e.getKeyChar();
    if(key=='a'||key=='A')keyA=false;
    if(key=='w'||key=='W')keyW=false;
    if(key=='s'||key=='S')keyS=false;
    if(key=='d'||key=='D')keyD=false;
    if(key==' ')keySpace=false;
    if(key==ESCAPE){os.println(key);os.flush();}
  }
  public void keyTyped(KeyEvent e){}

  static void SendInputs(){
    String input="";
    if(keyA)input=input.concat("a");
    if(keyW)input=input.concat("w");
    if(keyS)input=input.concat("s");
    if(keyD)input=input.concat("d");
    if(keySpace)input=input.concat(" ");
    if(osSet){os.println(input);os.flush();}
  }
  //Atualiza as posições dos Players de acordo com a inputLine
  static void AplicaInputsRecebidosDoServidor(String inputLine){
    /*formato de inputLine = I:playerIposX,playerIposY,playerIestado,playerIdirecao.I.*/
    if(inputLine.contains("0:"))Player1.SetInputsRecebidosDoServidor(inputLine);
    if(inputLine.contains("1:"))Player2.SetInputsRecebidosDoServidor(inputLine);
  }

  void FecharJogo(){
    os.println(ESCAPE);
    os.flush();
  }

  //MAIN - INICIA OS THREADS
  public static void main(String[] args){

    new Thread(new ClienteFrame()).start();
    new Thread(new GerenteFPS()).start();
  }

  //Faz a conexão e comunicação com o servidor
  public void run(){
    Socket socket=null;
    Scanner is=null;
    //TENTATIVA DE CONEXÃO COM O SERVIDOR
    try{
      socket=new Socket("127.0.0.1", 80);
      //socket=new Socket("200.145.148.186", 80);
      os=new PrintStream(socket.getOutputStream(), true);
      osSet=true;//Valida o estado da conexão com o servidor
      is=new Scanner(socket.getInputStream());
    }catch(UnknownHostException e){
      System.err.println("CLIENTE: Don't know about host");
    }catch(IOException e){
      System.err.println("CLIENTE: Couldn't get I/O for the connection to host");
    }
    //CONEXÃO ESTABELECIDA COM SUCESSO
    try{
      //INPUTS DO SERVIDOR - AQUI AS AÇÕES SÃO RECEBIDAS
      do {
        inputLine=is.nextLine();//O input recebido pelo servidor
        if(inputLine.contains("VITORIA:")){
          if(inputLine.contains("0")) VITORIA = 0;
          if(inputLine.contains("1")) VITORIA = 1;
        }
        else if(inputLine.contains("pronto")){
          System.out.println("PRONTO");
          CardLayout cl = (CardLayout)(cards.getLayout());
          cl.show(cards,"JANELA");
        }
        else if(!inputLine.equals("::"))AplicaInputsRecebidosDoServidor(inputLine);
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
  static long tempoIntervalo=1000/48;//48 frames/s (valor dobrado para tentar otimizar o processamento do servidor)

  public synchronized void run(){
    try{
      while(true){
        while(ClienteFrame.estadoJogo==ClienteFrame.MENU){wait(100);}
        //Loop de cada frame
        while(ClienteFrame.estadoJogo==ClienteFrame.JOGO){ //mudou, era: while(!Servidor.fecharSala)
          tempoInicio=System.currentTimeMillis();//Marca o tempo de inicio do frame
          Player1.ExecutaAcao();
          Player2.ExecutaAcao();
          ClienteFrame.janela.repaint();//Atualiza a janela gráfica (pelo cliente)
          ClienteFrame.SendInputs();//Envia os inputs ao ServidoR
          //Controle de encerramento do frame:
          tempoFim=System.currentTimeMillis();//Registra o tempo de encerramento do frame
          tempoExecucao=tempoFim-tempoInicio;//Calcula o tempo de execução deste frame
          if(tempoExecucao>tempoIntervalo*1.2)System.out.println("GERENTE_FPS: Frames atrasado! tempoExecucao: "+tempoExecucao+", tempoIntervalo: "+tempoIntervalo);//Notifica caso frame tenha levado mais tempo que o esperado
          while(System.currentTimeMillis()-tempoInicio<tempoIntervalo)wait(tempoIntervalo-tempoExecucao);//Espera pelo tempo do frame. Está correto?
        }
      }
    }catch(IllegalArgumentException erro1){System.err.println("GERENTE_FPS: IllegalArgumentException");}
    catch(IllegalMonitorStateException erro2){System.err.println("GERENTE_FPS: IllegalMonitorStateException");}
    catch(InterruptedException erro3){System.err.println("GERENTE_FPS: InterruptedException");}
  }
}

//A conspiracy of ravens, a murder of crows, a parliament of owls.
