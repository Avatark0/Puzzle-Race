import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

public class Player1 extends JFrame{
  final int spriteWidth = 164;
  final int spriteHeight = 155;
  final int spriteCols = 5;
  final int spriteRows = 4;
  final int spriteNum = spriteCols*spriteRows;
  public int spriteFrame = 0;
  BufferedImage[] imgPlayer1=new BufferedImage[spriteNum];

  Player1(){
      new Desenho();
  }  

    class Desenho extends JPanel {

        Desenho() {
            try {
                for(int i=0;i<spriteCols;i++)
                for(int j=0;j<spriteRows;j++)
                    imgPlayer1[i*(spriteRows)+j]=ImageIO.read(new File("P1_Anda1.png")).getSubimage(i*spriteWidth,j*spriteHeight,spriteWidth,spriteHeight);
            }catch (final RasterFormatException rfe) {
                System.out.println("could not read sprite of size [" + spriteWidth + "x" + spriteHeight + "] at position [] from sprite'" + this.getName() + "'");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "A imagem nao pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } 
        }
    }
}