import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//QUANTOS TREADS SAO GERADOS POR UMA INSTANCIA DESTA CLASSE?
public class ClienteFrame extends JFrame implements Runnable, KeyListener {
  static PrintStream os = null;

  //VETOR IMAGEM(CRIAR 1 PARA CADA OBJETO)
  int fundo=0;
  Player1 player1=new Player1();
  
  BufferedImage[] imgCenario=new BufferedImage[2];
  Image[] imgPlayer2=new Image[2];
  Image[] imgItens  =new Image[2];
  
  //CLASSE INTERNA, GERENCIA AS IMAGENS
  class Desenho extends JPanel {

    Desenho() {
      try {
        setPreferredSize(new Dimension(1000, 600));
        imgCenario[0]=ImageIO.read(new File("fundo.jpeg"));
        imgCenario[1]=ImageIO.read(new File("fundo2.jpeg"));
        
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "A imagem nao pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.drawImage(imgCenario[fundo], 0, 0, getSize().width, getSize().height, this);
      //g.drawImage(player1.anda[Player1.frame], 50, 50, 155,164,this);
      switch(Player1.estado){
        case Player1.ANDA:g.drawImage(player1.anda[Player1.frame], Player1.posX, Player1.posY, Player1.descritor[Player1.estado][Player1.WIDTH2],Player1.descritor[Player1.estado][Player1.HEIGHT2],this);break;
        case Player1.PULA:g.drawImage(player1.pula[Player1.frame], Player1.posX, Player1.posY, Player1.descritor[Player1.estado][Player1.WIDTH2],Player1.descritor[Player1.estado][Player1.HEIGHT2],this);break;
      }
      Toolkit.getDefaultToolkit().sync();
    }
  }

  //JANELA GRAFICA DO CLIENTE
  ClienteFrame() {
    super("Cliente do chat");
    JPanel mapa=new Desenho();//instancia a janela grafica do jogo
    
    setPreferredSize(new Dimension(1000, 600));
    add(mapa,BorderLayout.CENTER);
    pack();
    setVisible(true);
    addKeyListener(this);
  }

  //EVENTOS DO CLIENTE
  public void keyPressed(KeyEvent e){
    char key=e.getKeyChar();
    os.println(key);
  }
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}

  //MAIN - INICIA O TREAD
  public static void main(String[] args) {
    new Thread(new ClienteFrame()).start();
  }

  public void run() {
    Socket socket = null;
    Scanner is = null;

    //TENTATIVA DE CONEXAO COM O SERVIDOR (como controlar a tentativa, e reconhecer se o servidor esta cheio?): antes de tentar a conexao checar variavel do servidor se ha vagas?
    try {
      socket = new Socket("127.0.0.1", 80);
      os = new PrintStream(socket.getOutputStream(), true);
      is = new Scanner(socket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host.");
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to host");
    }

    try {
      String inputLine;
      int estadoAnterior=0;
      //INPUTS DO SERVIDOR - AQUI AS AÇOES SAO RECEBIDAS
      do {
        inputLine=is.nextLine();//O input recebido
          System.out.println(inputLine+" ");
          Player1.frame++;
          //ALTERAR VARIAVEIS ESTATICAS PELO SERVIDOR?
          if(inputLine.contains("a")){
            player1.SetEstado(Player1.ANDA); 
          }
          if(inputLine.contains("d")){
            player1.SetEstado(Player1.ANDA);
          }
          if(inputLine.contains(" ")){
            player1.SetEstado(Player1.PULA);
          }
          if(Player1.frame>=Player1.descritor[Player1.estado][Player1.NUM])Player1.frame=0;
          else if(estadoAnterior!=Player1.estado)Player1.frame=0;
          estadoAnterior=Player1.estado;

          repaint();//atualiza a tela grafica(pelo servidor)
      } while (!inputLine.equals(""));

      os.close();
      is.close();
      socket.close();
      System.exit(0);
    } catch (UnknownHostException e) {
      System.err.println("Trying to connect to unknown host: " + e);
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
}


//A conspiracy of ravens, a murder of crows, a parliament of owls.
