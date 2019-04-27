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
  JTextField textField;
  JTextArea textArea;

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
      g.drawImage(player1.imgPlayer1[player1.spriteFrame], 50, 50, player1.spriteWidth, player1.spriteHeight, this);
      Toolkit.getDefaultToolkit().sync();
    }
  }

  //JANELA GRAFICA DO CLIENTE
  ClienteFrame() {
    super("Cliente do chat");
    JPanel mapa=new Desenho();//instancia a janela grafica do jogo
    
    setPreferredSize(new Dimension(1000, 600));
    add(mapa,BorderLayout.CENTER);
    /*
    add(textField = new JTextField(20), BorderLayout.NORTH);
    add(textArea = new JTextArea(5, 20), BorderLayout.WEST);
    textField.addActionListener(this);
    textArea.setEditable(false);
    //*/
    pack();
    setVisible(true);
    addKeyListener(this);
    /*
    textField.addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent e){
        int key=e.getKeyCode();
        System.out.println(key+" pela construtura\n");
      }
    });//*/
  }

  //EVENTOS DO CLIENTE
  public void keyPressed(KeyEvent e){
    char key=e.getKeyChar();
    os.println(key);
  }
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}
  /*
  public void actionPerformed(ActionEvent e) {
    os.println(textField.getText());
    textField.setText("");
  }//*/

  //POR QUE INSTANCIAR CLIENTFRAME NO TREAD?
  public static void main(String[] args) {
    new Thread(new ClienteFrame()).start();
  }

  public void run() {
    Socket socket = null;
    Scanner is = null;

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

      //INPUTS DO SERVIDOR - AQUI AS AÇOES SAO RECEBIDAS
      do {
        String line=inputLine=is.nextLine();
        //textArea.append((line)+"\n");
        //adicionado if
        if(line!=""){
          System.out.println(line+" ");
          if(fundo==1)  fundo=0;
          else          fundo=1;
          player1.spriteFrame++;
          if(player1.spriteFrame==player1.spriteNum)player1.spriteFrame=0;
          repaint();//atualiza a tela grafica(pelo servidor)
        }
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
