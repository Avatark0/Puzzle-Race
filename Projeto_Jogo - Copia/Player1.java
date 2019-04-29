import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

public class Player1 extends JFrame{
    public static int posX = 50;//o estado (acao) da classe
    public static int posY = 50;//o estado (acao) da classe
    public static int estado = 0;//o estado (acao) da classe
    public static int frame = 0;//o frame da animacao do estado
    final static int ESTADO_INDEX = 4;//numero de spriteSheets da classe
        final static int ANDA = 0;//Index do estado ANDA, etc
        final static int CORRE = 1;
        final static int PULA = 2;
        final static int CAI = 3;
    final static int PROP_INDEX = 5;//Numero de propriedades necessarias (sprite padrao = 5)
        final static int WIDTH2 = 0;//Index do spriteWidth
        final static int HEIGHT2 = 1;//Index do spriteHeight
        final static int COLS = 2;//Index do spriteCols
        final static int ROWS = 3;//Index do spriteRows
        final static int NUM = 4;//Index do spriteRows
    static int[][] descritor = new int[ESTADO_INDEX][PROP_INDEX];//associa a spriteSheet do estado (acao) com as variaveis dela
    //BufferedImage[] anda = new BufferedImage[descritor[ANDA][NUM]];//os sprites cropados (frames) do estado
    BufferedImage[] anda = new BufferedImage[20];//essa linha me f**** por 6 horas
    BufferedImage[] corre = new BufferedImage[descritor[CORRE][NUM]];
    BufferedImage[] pula = new BufferedImage[52];//setar por numero pq a variavel ainda n foi setada (na compilacao)
    BufferedImage[] cai = new BufferedImage[descritor[CAI][NUM]];

    class Desenho extends JPanel {

        Desenho() {
            try {
                for(int row=0;row<descritor[ANDA][ROWS];row++)
                    for(int col=0;col<descritor[ANDA][COLS];col++)
                        anda[row*(descritor[ANDA][COLS])+col]=ImageIO.read(new File("P1_Anda1.png")).getSubimage(col*descritor[ANDA][WIDTH2],row*descritor[ANDA][HEIGHT2],descritor[ANDA][WIDTH2],descritor[ANDA][HEIGHT2]);
                for(int row=0;row<descritor[PULA][ROWS];row++)
                    for(int col=0;col<descritor[PULA][COLS];col++)
                        if((row*(descritor[PULA][COLS])+col)<descritor[PULA][NUM])//Esta sprite-sheet n e totalmente preenchida
                            pula[row*(descritor[PULA][COLS])+col]=ImageIO.read(new File("P1_Pula.png")).getSubimage(col*descritor[PULA][WIDTH2],row*descritor[PULA][HEIGHT2],descritor[PULA][WIDTH2],descritor[PULA][HEIGHT2]);
                    /*
                for(int j=0;j<descritor[PULA][ROWS];j++)
                    for(int i=0;i<descritor[PULA][COLS];i++)
                        pula[j*(descritor[PULA][ROWS])+i]=ImageIO.read(new File("P1_Pula.png")).getSubimage(i*descritor[PULA][WIDTH],j*descritor[PULA][HEIGHT],descritor[PULA][WIDTH],descritor[PULA][HEIGHT]);
                for(int j=0;j<descritor[CAI][ROWS];j++)
                    for(int i=0;i<descritor[CAI][COLS];i++)
                        cai[j*(descritor[CAI][ROWS])+i]=ImageIO.read(new File("P1_Cai.png")).getSubimage(i*descritor[CAI][WIDTH],j*descritor[CAI][HEIGHT],descritor[CAI][WIDTH],descritor[CAI][HEIGHT]);
                //*/
            }catch (final RasterFormatException rfe) {
                System.out.println("could not read sprite of size [" + descritor[estado][WIDTH2] + "x" + descritor[estado][HEIGHT2] + "] at position [] from sprite'" + this.getName() + "'");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "A imagem nao pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } 
        }
    }

    void SetEstado(int set){
        estado=set;
        System.out.println("SetEstado: estado="+Player1.estado);
    }

    void SetDirection(){

    }

    void SetPosition(){

    }

    Player1(){
        descritor[ANDA][WIDTH2] = 164;
        descritor[ANDA][HEIGHT2] = 155;
        descritor[ANDA][COLS] = 5;
        descritor[ANDA][ROWS] = 4;
        descritor[ANDA][NUM] = 20;//ROWS x COLS
        /*
        descritor[CORRE][WIDTH] = 0;
        descritor[CORRE][HEIGHT] = 0;
        descritor[CORRE][COLS] = 0;
        descritor[CORRE][ROWS] = 0;
        descritor[CORRE][NUM] = 0;
        //*/
        descritor[PULA][WIDTH2] = 164;
        descritor[PULA][HEIGHT2] = 155;
        descritor[PULA][COLS] = 6;
        descritor[PULA][ROWS] = 9;
        descritor[PULA][NUM] = 51;
        /*
        descritor[CAI][WIDTH] = 0;
        descritor[CAI][HEIGHT] = 0;
        descritor[CAI][COLS] = 0;
        descritor[CAI][ROWS] = 0;
        descritor[CAI][NUM] = 0;
        //*/
        new Desenho();
    }  
}