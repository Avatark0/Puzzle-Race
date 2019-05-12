import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//Classes: Player1, Player2.

public class Objeto extends JFrame{
    final static int PROP_INDEX_SIZE = 5;//Número de propriedades necessárias (sprite padrão = 5)
        final static int WIDTH2 = 0;//Index do spriteWidth, etc (por um conflito de valores ao usar os nomes WIDTH e HEIGHT, foi adicionado o 2 nos respectivos nomes).
        final static int HEIGHT2 = 1;
        final static int COLS = 2;
        final static int ROWS = 3;
        final static int NUM = 4;
    final static int ESTADO_INDEX_SIZE = 4;//Número de spriteSheets da classe
        final static int ANDA = 0;//Index do estado ANDA, etc
        final static int CORRE = 1;
        final static int PULA = 2;
        final static int CAI = 3;
    final public static int DIR=-1;
    final public static int ESQ=1;
}

class Player1 extends Objeto{
    //Decritores (devem ser redeclarados para o player2, caso contrario eles são compartilhados entre as classes).
    static int[][] descritor = new int[ESTADO_INDEX_SIZE][PROP_INDEX_SIZE];//Associa a spriteSheet do estado (ação) com as variáveis dela
    public static int posX = 50;//Posição do Objeto (0,0 = canto esquerdo superior)
    public static int posY = 20;
    public static int sizeX = 20;//Tamanho do Objeto
    public static int sizeY = 75;
    public static int estado = 0;//O estado (acao) da classe
    public static int estadoAnterior = 0;//Utilizado para checar mudança de estado
    public static int frame = 0;//O frame da animação do estado
    public static int direcao=1;
    public static int direcaoReajuste=0;
    public static int acelVert=0;

    static int sdifX=8;//Diferença de posição do centro da hitBox com o centro do sprite
    static int sdifY=-19;
    static int sposX=0;//Inicializado no construtor
    static int sposY=0;

    static boolean[] pathBlocked=new boolean[4];
    static final int pathBlockedIndexDir=0;//DIREITA
    static final int pathBlockedIndexCim=1;//CIMA
    static final int pathBlockedIndexEsq=2;//ESQUERDA
    static final int pathBlockedIndexBai=3;//BAIXO

    //BufferedImage[] ação = new BufferedImage[descritor[AÇÃO][NUM]];//os sprites cropados (frames) do estado
    BufferedImage[] anda = new BufferedImage[20];//Setar por número. Neste ponto da compilação a váriavel ainda não foi setada. (6 horas).
    BufferedImage[] corre = new BufferedImage[descritor[CORRE][NUM]];
    BufferedImage[] pula = new BufferedImage[51];
    BufferedImage[] cai = new BufferedImage[descritor[CAI][NUM]];

    //Cropa as spriteSheets.
    class CropSpriteSheet extends JPanel{
        CropSpriteSheet(){
            try{
                for(int row=0;row<descritor[ANDA][ROWS];row++)
                    for(int col=0;col<descritor[ANDA][COLS];col++)
                        if((row*(descritor[ANDA][COLS])+col)<descritor[ANDA][NUM])//Checa se a sprite-sheet é totalmente preenchida
                            anda[row*(descritor[ANDA][COLS])+col]=ImageIO.read(new File("P1_Anda1.png")).getSubimage(col*descritor[ANDA][WIDTH2],row*descritor[ANDA][HEIGHT2],descritor[ANDA][WIDTH2],descritor[ANDA][HEIGHT2]);
                for(int row=0;row<descritor[PULA][ROWS];row++)
                    for(int col=0;col<descritor[PULA][COLS];col++)
                        if((row*(descritor[PULA][COLS])+col)<descritor[PULA][NUM])
                            pula[row*(descritor[PULA][COLS])+col]=ImageIO.read(new File("P1_Pula.png")).getSubimage(col*descritor[PULA][WIDTH2],row*descritor[PULA][HEIGHT2],descritor[PULA][WIDTH2],descritor[PULA][HEIGHT2]);
                /*Ainda não implementado
                for(int j=0;j<descritor[PULA][ROWS];j++)
                    for(int i=0;i<descritor[PULA][COLS];i++)
                        pula[j*(descritor[PULA][ROWS])+i]=ImageIO.read(new File("P1_Pula.png")).getSubimage(i*descritor[PULA][WIDTH],j*descritor[PULA][HEIGHT],descritor[PULA][WIDTH],descritor[PULA][HEIGHT]);
                for(int j=0;j<descritor[CAI][ROWS];j++)
                    for(int i=0;i<descritor[CAI][COLS];i++)
                        cai[j*(descritor[CAI][ROWS])+i]=ImageIO.read(new File("P1_Cai.png")).getSubimage(i*descritor[CAI][WIDTH],j*descritor[CAI][HEIGHT],descritor[CAI][WIDTH],descritor[CAI][HEIGHT]);
                //*/
            }catch(final RasterFormatException rfe){
                System.err.println("could not read sprite of size ["+descritor[estado][WIDTH2]+"x"+descritor[estado][HEIGHT2]+"] at position [] from sprite'" +this.getName()+"'");
            }catch(IOException e){
                JOptionPane.showMessageDialog(this,"A imagem nao pode ser carregada!\n"+e,"Erro",JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } 
        }
    }
    /********************************************************************************************************************************/
    //Define o estado (ação) do Objeto
    static void SetEstado(int set){
        if(acelVert>0)estado=PULA;
        else if(!pathBlocked[pathBlockedIndexBai])estado=CAI;
        else if(set==PULA){estado=PULA;acelVert=10;}
        else estado=ANDA;
    }

    //Define a posição do objeto
    static void SetPosition(String mov){
        if(mov.contains("a") && !pathBlocked[pathBlockedIndexEsq]){posX-=2;sposX-=2;}
        else if(mov.contains("d") && !pathBlocked[pathBlockedIndexDir]){posX+=2;sposX+=2;}
        else if(mov.contains("s") && !pathBlocked[pathBlockedIndexBai]){posY+=1;sposY+=1;}
        else if(mov.contains("w") && !pathBlocked[pathBlockedIndexCim]){posY-=1;sposY-=1;}
        if(acelVert>0){
            if(!pathBlocked[pathBlockedIndexCim]){posY-=acelVert/3;sposY-=acelVert/3;}
            acelVert--;
        }
        else if(!pathBlocked[pathBlockedIndexBai]){
            posY-=acelVert/3;sposY-=acelVert/3;
            if(acelVert>-10)acelVert--;
        }
    }

    //Retorna um retângulo com a hitBox do Objeto
    static Rectangle HitBox(){
        Rectangle hitBox= new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }

    //Atualmente atualiza o frame do objeto
    public void ExecutaAcao(String input){
        frame++;
        Colisoes();
        if(input.contains("a")||input.contains("A")){
            SetEstado(ANDA);
            direcao=ESQ;
            direcaoReajuste=0;
            SetPosition("a");
        }
        else if(input.contains("d")||input.contains("D")){
            SetEstado(ANDA);
            direcao=DIR;
            direcaoReajuste=descritor[ANDA][WIDTH2]-sdifX*2;
            SetPosition("d");
        }
        else if(input.contains("w")||input.contains("W")){
            //SetPosition("w");
        }
        else if(input.contains("s")||input.contains("S")){
            //SetPosition("s");
        }
        if(input.contains(" ")){
            SetEstado(PULA);
            SetPosition(" ");
        }
        if(frame>=descritor[estado][NUM])frame=0;
        else if(estadoAnterior!=estado)frame=0;
        estadoAnterior=estado;
    }

    void Colisoes(){
        if(HitBox().intersects(Player2.HitBox())){
            float relX=posX-Player2.posX;
            float relY=posY-Player2.posY;
            if(relX>sizeX*0.92)pathBlocked[pathBlockedIndexDir]=true;//players se trombando pela direita.
            else pathBlocked[pathBlockedIndexDir]=false;
            if(relX<-sizeX*0.92)pathBlocked[pathBlockedIndexEsq]=true;//players se trombando pela Esquerda.
            else pathBlocked[pathBlockedIndexEsq]=false;
            if(relY>sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//players se trombando por baixo.
            else pathBlocked[pathBlockedIndexBai]=false;
            if(relY<sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//players se trombando por cima.
            else pathBlocked[pathBlockedIndexCim]=false;
        }
        if(HitBox().intersects(new Rectangle()));//checagem com cenario e items.
    }

    /********************************************************************************************************************************/
    Player1(){
        descritor[ANDA][WIDTH2] = 164;
        descritor[ANDA][HEIGHT2] = 155;
        descritor[ANDA][COLS] = 5;
        descritor[ANDA][ROWS] = 4;
        descritor[ANDA][NUM] = 20;//ROWS x COLS
        descritor[PULA][WIDTH2] = 164;
        descritor[PULA][HEIGHT2] = 155;
        descritor[PULA][COLS] = 6;
        descritor[PULA][ROWS] = 9;
        descritor[PULA][NUM] = 51;
        /*Ainda não implementado
        descritor[CORRE][WIDTH] = 0;
        descritor[CORRE][HEIGHT] = 0;
        descritor[CORRE][COLS] = 0;
        descritor[CORRE][ROWS] = 0;
        descritor[CORRE][NUM] = 0;
        descritor[CAI][WIDTH] = 0;
        descritor[CAI][HEIGHT] = 0;
        descritor[CAI][COLS] = 0;
        descritor[CAI][ROWS] = 0;
        descritor[CAI][NUM] = 0;
        //*/
        sposX=posX-(descritor[ANDA][WIDTH2]-sizeX)/2+sdifX;
        sposY=posY-(descritor[ANDA][HEIGHT2]-sizeY)/2+sdifY;
        
        new CropSpriteSheet();
    }  
}

class Player2 extends Player1{
    static int[][] descritor = new int[ESTADO_INDEX_SIZE][PROP_INDEX_SIZE];//Associa a spriteSheet do estado (ação) com as variáveis dela
    public static int posX = 150;//Posição da hitBox (0,0 = canto esquerdo superior)
    public static int posY = 100;
    public static int sizeX = 20;//Tamanho da hitBox
    public static int sizeY = 75;
    public static int estado = 0;//O estado (acao) da classe
    public static int estadoAnterior = 0;//Utilizado para checar mudança de estado
    public static int frame = 0;//O frame da animação do estado
    public static int direcao=1;
    public static int direcaoReajuste=0;

    static int sdifX=11;//Diferença de posição do centro da hitBox com o centro do sprite (possibilidade dos sinais estarem invertidos)
    static int sdifY=-12;
    static int sposX=0;//Inicializado no construtor
    static int sposY=0;

    static boolean[] pathBlocked=new boolean[4];
    static final int pathBlockedIndexDir=0;//DIREITA
    static final int pathBlockedIndexCim=1;//CIMA
    static final int pathBlockedIndexEsq=2;//ESQUERDA
    static final int pathBlockedIndexBai=3;//BAIXO
    
    //BufferedImage[] ação = new BufferedImage[descritor[AÇÃO][NUM]];//os sprites cropados (frames) do estado
    BufferedImage[] anda = new BufferedImage[20];//Setar por número. Neste ponto da compilação a váriavel ainda não foi setada. (Essa linha me custou 6 horas).
    BufferedImage[] corre = new BufferedImage[descritor[CORRE][NUM]];
    BufferedImage[] pula = new BufferedImage[51];
    BufferedImage[] cai = new BufferedImage[descritor[CAI][NUM]];

    //Cropa as spriteSheets.
    class CropSpriteSheet extends JPanel{
        CropSpriteSheet(){
            try{
                for(int row=0;row<descritor[ANDA][ROWS];row++)
                    for(int col=0;col<descritor[ANDA][COLS];col++)
                        if((row*(descritor[ANDA][COLS])+col)<descritor[ANDA][NUM])
                            anda[row*(descritor[ANDA][COLS])+col]=ImageIO.read(new File("P2_Anda1.png")).getSubimage(col*descritor[ANDA][WIDTH2],row*descritor[ANDA][HEIGHT2],descritor[ANDA][WIDTH2],descritor[ANDA][HEIGHT2]);
                for(int row=0;row<descritor[PULA][ROWS];row++)
                    for(int col=0;col<descritor[PULA][COLS];col++)
                        if((row*(descritor[PULA][COLS])+col)<descritor[PULA][NUM])//Esta sprite-sheet n e totalmente preenchida
                            pula[row*(descritor[PULA][COLS])+col]=ImageIO.read(new File("P2_Pula.png")).getSubimage(col*descritor[PULA][WIDTH2],row*descritor[PULA][HEIGHT2],descritor[PULA][WIDTH2],descritor[PULA][HEIGHT2]);
                /*Ainda não implementado
                for(int j=0;j<descritor[PULA][ROWS];j++)
                    for(int i=0;i<descritor[PULA][COLS];i++)
                        pula[j*(descritor[PULA][ROWS])+i]=ImageIO.read(new File("P1_Pula.png")).getSubimage(i*descritor[PULA][WIDTH],j*descritor[PULA][HEIGHT],descritor[PULA][WIDTH],descritor[PULA][HEIGHT]);
                for(int j=0;j<descritor[CAI][ROWS];j++)
                    for(int i=0;i<descritor[CAI][COLS];i++)
                        cai[j*(descritor[CAI][ROWS])+i]=ImageIO.read(new File("P1_Cai.png")).getSubimage(i*descritor[CAI][WIDTH],j*descritor[CAI][HEIGHT],descritor[CAI][WIDTH],descritor[CAI][HEIGHT]);
                //*/
            }catch(final RasterFormatException rfe){
                System.err.println("could not read sprite of size ["+descritor[estado][WIDTH2]+"x"+descritor[estado][HEIGHT2]+"] at position [] from sprite'" +this.getName()+"'");
            }catch(IOException e){
                JOptionPane.showMessageDialog(this,"A imagem nao pode ser carregada!\n"+e,"Erro",JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } 
        }
    }  
    /********************************************************************************************************************************/
    //Define o estado (ação) do Objeto
    static void SetEstado(int set){
        if(acelVert>0)estado=PULA;
        else if(!pathBlocked[pathBlockedIndexBai])estado=CAI;
        else if(set==PULA){estado=PULA;acelVert=10;}
        else estado=ANDA;
    }

    //Define a posição do objeto
    static void SetPosition(String mov){
        if(mov.contains("a") && !pathBlocked[pathBlockedIndexEsq]){posX-=2;sposX-=2;}
        else if(mov.contains("d") && !pathBlocked[pathBlockedIndexDir]){posX+=2;sposX+=2;}
        else if(mov.contains("s") && !pathBlocked[pathBlockedIndexBai]){posY+=1;sposY+=1;}
        else if(mov.contains("w") && !pathBlocked[pathBlockedIndexCim]){posY-=1;sposY-=1;}
        if(acelVert>0){
            if(!pathBlocked[pathBlockedIndexCim]){posY-=acelVert/3;sposY-=acelVert/3;}
            acelVert--;
        }
        else if(!pathBlocked[pathBlockedIndexBai]){
            posY-=acelVert/3;sposY-=acelVert/3;
            if(acelVert>-10)acelVert--;
        }
    }

    //Retorna um retângulo com a hitBox do Objeto
    static Rectangle HitBox(){
        Rectangle hitBox= new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }

    //Atualmente atualiza o frame do objeto
    public void ExecutaAcao(String input){
        frame++;
        Colisoes();
        if(input.contains("a")||input.contains("A")){
            SetEstado(ANDA);
            direcao=ESQ;
            direcaoReajuste=0;
            SetPosition("a");
        }
        else if(input.contains("d")||input.contains("D")){
            SetEstado(ANDA);
            direcao=DIR;
            direcaoReajuste=descritor[ANDA][WIDTH2]-sdifX*2;
            SetPosition("d");
        }
        else if(input.contains("w")||input.contains("W")){
            //SetPosition("w");
        }
        else if(input.contains("s")||input.contains("S")){
            //SetPosition("s");
        }
        if(input.contains(" ")){
            SetEstado(PULA);
            SetPosition(" ");
        }
        if(frame>descritor[estado][NUM])frame=0;//mudança recente, era >=
        else if(estadoAnterior!=estado)frame=0;
        estadoAnterior=estado;
    }

    void Colisoes(){
        if(HitBox().intersects(Player1.HitBox())){
            float relX=posX-Player1.posX;
            float relY=posY-Player1.posY;
            if(relX>sizeX*0.92)pathBlocked[pathBlockedIndexDir]=true;//players se trombando pela direita.
            else pathBlocked[pathBlockedIndexDir]=false;
            if(relX<-sizeX*0.92)pathBlocked[pathBlockedIndexEsq]=true;//players se trombando pela Esquerda.
            else pathBlocked[pathBlockedIndexEsq]=false;
            if(relY>sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//players se trombando por baixo.
            else pathBlocked[pathBlockedIndexBai]=false;
            if(relY<sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//players se trombando por cima.
            else pathBlocked[pathBlockedIndexCim]=false;
        }
        if(HitBox().intersects(new Rectangle()));//checagem com cenario e items.
    }
    /********************************************************************************************************************************/
    Player2(){
        descritor[ANDA][WIDTH2] = 124;
        descritor[ANDA][HEIGHT2] = 141;
        descritor[ANDA][COLS] = 7;
        descritor[ANDA][ROWS] = 3;
        descritor[ANDA][NUM] = 20;//ROWS x COLS
        descritor[PULA][WIDTH2] = 124;
        descritor[PULA][HEIGHT2] = 141;
        descritor[PULA][COLS] = 8;
        descritor[PULA][ROWS] = 7;
        descritor[PULA][NUM] = 51;
        /*Ainda não implementado
        descritor[CORRE][WIDTH] = 0;
        descritor[CORRE][HEIGHT] = 0;
        descritor[CORRE][COLS] = 0;
        descritor[CORRE][ROWS] = 0;
        descritor[CORRE][NUM] = 0;
        descritor[CAI][WIDTH] = 0;
        descritor[CAI][HEIGHT] = 0;
        descritor[CAI][COLS] = 0;
        descritor[CAI][ROWS] = 0;
        descritor[CAI][NUM] = 0;
        //*/
        sposX=posX-(descritor[ANDA][WIDTH2]-sizeX)/2+sdifX;
        sposY=posY-(descritor[ANDA][HEIGHT2]-sizeY)/2+sdifY;

        new CropSpriteSheet();
    }  
}