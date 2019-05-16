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
    final static int ESTADO_INDEX_SIZE = 5;//Número de spriteSheets da classe
        final static int ANDA = 0;//Index do estado ANDA, etc
        final static int CORRE = 1;
        final static int PULA = 2;
        final static int CAI = 3;
        final static int STAND = 4;
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
    public static int frameStandIntervalCount=0;
    public static int direcao=ESQ;
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
    /********************************************************************************************************************************/
    //Define o estado (ação) do Objeto
    static void SetEstado(int set){
        if(acelVert>0)estado=PULA;
        else if(!pathBlocked[pathBlockedIndexBai])estado=CAI;
        else if(set==PULA){estado=PULA;acelVert=10;}
        else if(set==ANDA)estado=ANDA;
        else estado=STAND;
    }

    //Define a posição do objeto
    static void SetPosition(String mov){
        if((mov.contains("a")||mov.contains("A")) && !pathBlocked[pathBlockedIndexEsq]){posX-=2;sposX-=2;}
        else if((mov.contains("d")||mov.contains("D")) && !pathBlocked[pathBlockedIndexDir]){posX+=2;sposX+=2;}
        else if(mov.contains("s")||mov.contains("S")){posY+=1;sposY+=1;}
        else if(mov.contains("w")||mov.contains("W")){posY-=1;sposY-=1;}
        if(acelVert>0){
            if(!pathBlocked[pathBlockedIndexCim]){posY-=acelVert/3;sposY-=acelVert/3;}
            acelVert--;
        }
        else if(!pathBlocked[pathBlockedIndexBai]){
            posY-=acelVert/3;sposY-=acelVert/3;
            if(acelVert>-10)acelVert--;
        }
    }

    //Atualmente atualiza o frame do objeto
    public void ExecutaAcao(String input){
        Colisoes();

        if(input.contains("a")||input.contains("A")){
            SetEstado(ANDA);
            direcao=ESQ;
            direcaoReajuste=0;
            frameStandIntervalCount=0;
        }
        else if(input.contains("d")||input.contains("D")){
            SetEstado(ANDA);
            direcao=DIR;
            direcaoReajuste=descritor[ANDA][WIDTH2]-sdifX*2;
            frameStandIntervalCount=0;
        }
        else if(input.contains("w")||input.contains("W")){}
        else if(input.contains("s")||input.contains("S")){}
        else if(frameStandIntervalCount>=10){SetEstado(STAND);frameStandIntervalCount=0;}
        else frameStandIntervalCount++;
        if(input.contains(" ")){
            SetEstado(PULA);
        }
        if(estadoAnterior!=estado&&frame%(descritor[estado][NUM]/3)==0)frame=0;
        else if(frame==descritor[estado][NUM]-1&&(estado==CAI||estado==PULA));//mantém o sprite no último frame da animação
        else if(frame>=descritor[estado][NUM]-1)frame=0;
        else frame++;
        estadoAnterior=estado;
        SetPosition(input);
    }

    //Retorna um retângulo com a hitBox do Objeto
    static Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }

    void Colisoes(){
        pathBlocked[pathBlockedIndexEsq]=false;
        pathBlocked[pathBlockedIndexDir]=false;
        pathBlocked[pathBlockedIndexCim]=false;
        pathBlocked[pathBlockedIndexBai]=true;//Setado em true temporariamente
        if(HitBox().intersects(Player2.HitBox())){
            float relX=posX-Player2.posX;
            float relY=posY-Player2.posY;
            //System.out.println("Player1: relX="+relX+" relY="+relY+" ((float)sizeX)*0.92="+((float)sizeX)*0.92);
            //NOTA: a sensibilidade do intersect é de 2 pixels. (Intervalos menores não serão reconhecidos)
            if(relX>((float)sizeX)*0.89)pathBlocked[pathBlockedIndexEsq]=true;//players se trombando pela direita.
            if(relX<-((float)sizeX)*0.89)pathBlocked[pathBlockedIndexDir]=true;//players se trombando pela Esquerda.
            if(relY>(float)sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//players se trombando por baixo.
            if(relY<(float)sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//players se trombando por cima.
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
        descritor[CAI][WIDTH2] = 164;
        descritor[CAI][HEIGHT2] = 155;
        descritor[CAI][COLS] = 16;
        descritor[CAI][ROWS] = 1;
        descritor[CAI][NUM] = 16;
        descritor[STAND][WIDTH2] = 164;
        descritor[STAND][HEIGHT2] = 155;
        descritor[STAND][COLS] = 5;
        descritor[STAND][ROWS] = 2;
        descritor[STAND][NUM] = 9;
        /*Ainda não implementado
        descritor[CORRE][WIDTH2] = 0;
        descritor[CORRE][HEIGHT2] = 0;
        descritor[CORRE][COLS] = 0;
        descritor[CORRE][ROWS] = 0;
        descritor[CORRE][NUM] = 0;
        //*/
        sposX=posX-(descritor[ANDA][WIDTH2]-sizeX)/2+sdifX;
        sposY=posY-(descritor[ANDA][HEIGHT2]-sizeY)/2+sdifY;
    }  
}

class Player2 extends Player1{
    static int[][] descritor = new int[ESTADO_INDEX_SIZE][PROP_INDEX_SIZE];//Associa a spriteSheet do estado (ação) com as variáveis dela
    public static int posX = 150;//Posição da hitBox (0,0 = canto esquerdo superior)
    public static int posY = 100;
    public static int sizeX = 20;//Tamanho da hitBox
    public static int sizeY = 75;
    public static int estado = STAND;//O estado (acao) da classe
    public static int estadoAnterior = 0;//Utilizado para checar mudança de estado
    public static int frame = 0;//O frame da animação do estado
    public static int frameStandIntervalCount=0;
    public static int direcao=ESQ;
    public static int direcaoReajuste=0;
    public static int acelVert=0;

    static int sdifX=11;//Diferença de posição do centro da hitBox com o centro do sprite
    static int sdifY=-12;
    static int sposX=0;//Inicializado no construtor
    static int sposY=0;

    static boolean[] pathBlocked=new boolean[4];
    static final int pathBlockedIndexDir=0;//DIREITA
    static final int pathBlockedIndexCim=1;//CIMA
    static final int pathBlockedIndexEsq=2;//ESQUERDA
    static final int pathBlockedIndexBai=3;//BAIXO
    /********************************************************************************************************************************/
    //Define o estado (ação) do Objeto
    static void SetEstado(int set){
        if(acelVert>0)estado=PULA;
        else if(!pathBlocked[pathBlockedIndexBai])estado=CAI;
        else if(set==PULA){estado=PULA;acelVert=10;}
        else if(set==ANDA)estado=ANDA;
        else estado=STAND;
    }

    //Define a posição do objeto
    static void SetPosition(String mov){
        if((mov.contains("a")||mov.contains("A")) && !pathBlocked[pathBlockedIndexEsq]){posX-=2;sposX-=2;}
        else if((mov.contains("d")||mov.contains("D")) && !pathBlocked[pathBlockedIndexDir]){posX+=2;sposX+=2;}
        else if(mov.contains("s")||mov.contains("S")){posY+=1;sposY+=1;}
        else if(mov.contains("w")||mov.contains("W")){posY-=1;sposY-=1;}
        if(acelVert>0){
            if(!pathBlocked[pathBlockedIndexCim]){posY-=acelVert/3;sposY-=acelVert/3;}
            acelVert--;
        }
        else if(!pathBlocked[pathBlockedIndexBai]){
            posY-=acelVert/3;sposY-=acelVert/3;
            if(acelVert>-10)acelVert--;
        }
    }

    //Atualmente atualiza o frame do objeto
    public void ExecutaAcao(String input){
        Colisoes();

        if(input.contains("a")||input.contains("A")){
            SetEstado(ANDA);
            direcao=ESQ;
            direcaoReajuste=0;
            frameStandIntervalCount=0;
        }
        else if(input.contains("d")||input.contains("D")){
            SetEstado(ANDA);
            direcao=DIR;
            direcaoReajuste=descritor[ANDA][WIDTH2]-sdifX*2;
            frameStandIntervalCount=0;
        }
        else if(input.contains("w")||input.contains("W")){}
        else if(input.contains("s")||input.contains("S")){}
        else if(frameStandIntervalCount>=10){SetEstado(STAND);frameStandIntervalCount=0;}
        else frameStandIntervalCount++;
        if(input.contains(" ")){
            SetEstado(PULA);
        }
        if(estadoAnterior!=estado&&frame%(descritor[estado][NUM]/3)==0)frame=0;
        else if(frame==descritor[estado][NUM]-1&&(estado==CAI||estado==PULA));//mantém o sprite no último frame da animação
        else if(frame>=descritor[estado][NUM]-1)frame=0;
        else frame++;
        estadoAnterior=estado;
        SetPosition(input);
    }

//Retorna um retângulo com a hitBox do Objeto
static Rectangle HitBox(){
    Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
    return hitBox;
}

void Colisoes(){
    pathBlocked[pathBlockedIndexEsq]=false;
    pathBlocked[pathBlockedIndexDir]=false;
    pathBlocked[pathBlockedIndexCim]=false;
    pathBlocked[pathBlockedIndexBai]=true;//Setado em true temporariamente
    if(HitBox().intersects(Player1.HitBox())){
        float relX=posX-Player1.posX;
        float relY=posY-Player1.posY;
        //NOTA: a sensibilidade do intersect é de 2 pixels. (Intervalos menores não serão reconhecidos)
        if(relX>((float)sizeX)*0.89)pathBlocked[pathBlockedIndexEsq]=true;//players se trombando pela direita.
        if(relX<-((float)sizeX)*0.89)pathBlocked[pathBlockedIndexDir]=true;//players se trombando pela Esquerda.
        if(relY>(float)sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//players se trombando por baixo.
        if(relY<(float)sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//players se trombando por cima.
    }
    if(HitBox().intersects(new Rectangle()));//checagem com cenario e items.
}
    /********************************************************************************************************************************/
    Player2(){
        descritor[ANDA][WIDTH2] = 124;//Largura de cada sprite na spriteSheet ANDA
        descritor[ANDA][HEIGHT2] = 141;//Altura de cada sprite na spriteSheet ANDA
        descritor[ANDA][COLS] = 7;//Total de colunas na spriteSheet ANDA
        descritor[ANDA][ROWS] = 3;//Total de linhas na spriteSheet ANDA
        descritor[ANDA][NUM] = 20;//Total de sprites na spriteSheet ANDA
        descritor[PULA][WIDTH2] = 124;
        descritor[PULA][HEIGHT2] = 141;
        descritor[PULA][COLS] = 8;
        descritor[PULA][ROWS] = 7;
        descritor[PULA][NUM] = 51;
        descritor[CAI][WIDTH2] = 124;
        descritor[CAI][HEIGHT2] = 141;
        descritor[CAI][COLS] = 8;
        descritor[CAI][ROWS] = 2;
        descritor[CAI][NUM] = 16;
        descritor[STAND][WIDTH2] = 124;
        descritor[STAND][HEIGHT2] = 141;
        descritor[STAND][COLS] = 5;
        descritor[STAND][ROWS] = 2;
        descritor[STAND][NUM] = 9;
        /*Ainda não implementado
        descritor[CORRE][WIDTH2] = 0;
        descritor[CORRE][HEIGHT2] = 0;
        descritor[CORRE][COLS] = 0;
        descritor[CORRE][ROWS] = 0;
        descritor[CORRE][NUM] = 0;
        //*/
        sposX=posX-(descritor[ANDA][WIDTH2]-sizeX)/2+sdifX;
        sposY=posY-(descritor[ANDA][HEIGHT2]-sizeY)/2+sdifY;
    }  
}