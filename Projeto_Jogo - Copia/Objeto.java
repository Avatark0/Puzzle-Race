import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//Classes: Objeto, Cenario, Player1, Player2.

public class Objeto extends JFrame{
    final static int PROP_INDEX_SIZE = 5;//Número de propriedades para cropar a spriteSheet e acessar os sprites individuais
        final static int LARGURA = 0;//Index das propriedades do sprite na spriteSheet
        final static int ALTURA = 1;
        final static int COLS = 2;
        final static int ROWS = 3;
        final static int NUM = 4;
    final static int ESTADO_INDEX_SIZE = 5;//Número de estados do Player
        final static int ANDA = 0;//Index dos estados do Player
        final static int CORRE = 1;
        final static int PULA = 2;
        final static int CAI = 3;
        final static int PARADO = 4;
    final public static int DIR=-1;//Index das direções do Player
    final public static int ESQ=1;
}

//////////////////////////////////////////////////////////////////////
//Chão
class Cenario extends Objeto{
    //Cálculo da posição do sprite. É deslocada do centro da área de crop, e, por consequência, da posição da hitbox
    static int sdifX=0;//Diferença de posição do centro da hitBox com o centro do sprite
    static int sdifY=0;
    static int sposX=0;//Posição do sprite. Inicializado no construtor
    static int sposY=0;

    //Variáveis de controle do Cenario
    public static int posX = 80;//Posição da hitBox (0,0 = canto esquerdo superior)
    public static int posY = 80;
    public static int sizeX = 50;//Tamanho do Objeto
    public static int sizeY = 50;
    
    //Retorna um retângulo com a hitBox do Objeto
    static Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }

}

///////////////////////////////////////////////////////////////////////

//Classes Player 1 e Player 2 representam os personagens dos jogadores. São declarados como classes diferentes para facilitar aplicação de spriteSheets (e uma série de variáveis) diferentes.
class Player1 extends Objeto{
    //Decritores. Devem ser redeclarados para o Player2, caso contrário eles são compartilhados entre as classes. O mesmo ocorre com funções
    static int[][] descritor = new int[ESTADO_INDEX_SIZE][PROP_INDEX_SIZE];//Associa a spriteSheet do estado (ação) com as variáveis dela
    
    //Controle de direções, registra quais estão bloqueadas
    static boolean[] pathBlocked=new boolean[4];
    static final int pathBlockedIndexDir=0;//DIREITA
    static final int pathBlockedIndexCim=1;//CIMA
    static final int pathBlockedIndexEsq=2;//ESQUERDA
    static final int pathBlockedIndexBai=3;//BAIXO

    //Cálculo da posição do sprite. É deslocada do centro da área de crop, e, por consequência, da posição da hitbox
    static int sdifX=8;//Diferença de posição do centro da hitBox com o centro do sprite
    static int sdifY=-19;
    static int sposX=0;//Posição do sprite. Inicializado no construtor 
    static int sposY=0;

    //Variáveis de controle do Player1
    public static int posX = 50;//Posição do Player1 (0,0 = canto esquerdo superior)
    public static int posY = 20;
    public static int sizeX = 20;//Tamanho do Player1
    public static int sizeY = 75;
    public static int estado = PARADO;//O estado (ação) do Player1
    public static int estadoAnterior = 0;//Utilizado para checar mudança de estado
    public static int frame = 0;//O frame da animação do estado
    public static int framePARADOIntervalCount=0;//Controle especial do estado PARADO
    public static int direcao=ESQ;//Direção do Player1
    public static int direcaoReajuste=0;//Reajuste da posição do sprite invertido (*-1)
    public static int acelVert=0;//Controle da aceleração vertical do Player1. (Atualmente alterada apenas pelo estado PULA)

    /********************************************************************************************************************************/
    //Define o estado (ação) do Objeto
    static void SetEstado(int set){
        if(acelVert>0)estado=PULA;
        else if(!pathBlocked[pathBlockedIndexBai])estado=CAI;
        else if(set==PULA){estado=PULA;acelVert=10;}
        else if(set==ANDA)estado=ANDA;
        else estado=PARADO;
    }

    //Define a posição do Objeto
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

    //Executa as ações de cada frame, aplicando os inputs e colisões. Também atualiza o frame do Objeto e determina seu estado
    public void ExecutaAcao(String input){
        Colisoes();

        if(input.contains("a")||input.contains("A")){
            SetEstado(ANDA);
            direcao=ESQ;
            direcaoReajuste=0;
            framePARADOIntervalCount=0;
        }
        else if(input.contains("d")||input.contains("D")){
            SetEstado(ANDA);
            direcao=DIR;
            direcaoReajuste=descritor[ANDA][LARGURA]-sdifX*2;
            framePARADOIntervalCount=0;
        }
        else if(input.contains("w")||input.contains("W")){}
        else if(input.contains("s")||input.contains("S")){}
        else if(framePARADOIntervalCount>=10){SetEstado(PARADO);framePARADOIntervalCount=0;}
        else framePARADOIntervalCount++;
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

    //Checagem de colisões do Objeto. (Se sua hitbox está sobrebosta a alguma outra)
    void Colisoes(){
        pathBlocked[pathBlockedIndexEsq]=false;
        pathBlocked[pathBlockedIndexDir]=false;
        pathBlocked[pathBlockedIndexCim]=false;
        pathBlocked[pathBlockedIndexBai]=true;//Setado em true temporariamente para testes de movimentação
        //NOTA: a sensibilidade do intersect é de 2 pixels. (Intervalos menores não são reconhecidos)
        if(HitBox().intersects(Player2.HitBox())){
            float relX=posX-Player2.posX;
            float relY=posY-Player2.posY;
            if(relX>((float)sizeX)*0.89)pathBlocked[pathBlockedIndexEsq]=true;//Players se trombando pela direita
            if(relX<-((float)sizeX)*0.89)pathBlocked[pathBlockedIndexDir]=true;//Players se trombando pela Esquerda
            if(relY>(float)sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//Players se trombando por baixo
            if(relY<(float)sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//Players se trombando por cima
            //System.out.println("Player1: relX="+relX+" relY="+relY+" ((float)sizeX)*0.92="+((float)sizeX)*0.92)
        }
        if(HitBox().intersects(new Rectangle()));//Checagem com cenario e items. (Como checar com diversos objetos diferentes da mesma classe?)
    }

    /********************************************************************************************************************************/
    Player1(){
        //ANDA
        descritor[ANDA][LARGURA] = 164;//Largura de cada sprite na spriteSheet ANDA
        descritor[ANDA][ALTURA] = 155;//Altura de cada sprite na spriteSheet ANDA
        descritor[ANDA][COLS] = 5;//Total de colunas na spriteSheet ANDA
        descritor[ANDA][ROWS] = 4;//Total de linhas na spriteSheet ANDA
        descritor[ANDA][NUM] = 20;//Total de sprites na spriteSheet ANDA
        //PULA
        descritor[PULA][LARGURA] = 164;
        descritor[PULA][ALTURA] = 155;
        descritor[PULA][COLS] = 6;
        descritor[PULA][ROWS] = 9;
        descritor[PULA][NUM] = 51;
        //CAI
        descritor[CAI][LARGURA] = 164;
        descritor[CAI][ALTURA] = 155;
        descritor[CAI][COLS] = 16;
        descritor[CAI][ROWS] = 1;
        descritor[CAI][NUM] = 16;
        //PARADO
        descritor[PARADO][LARGURA] = 164;
        descritor[PARADO][ALTURA] = 155;
        descritor[PARADO][COLS] = 5;
        descritor[PARADO][ROWS] = 2;
        descritor[PARADO][NUM] = 9;
        /*Ainda não implementado
        descritor[CORRE][LARGURA] = 0;
        descritor[CORRE][ALTURA] = 0;
        descritor[CORRE][COLS] = 0;
        descritor[CORRE][ROWS] = 0;
        descritor[CORRE][NUM] = 0;
        //*/
        //Cálculo da posição dos sprites
        sposX=posX-(descritor[ANDA][LARGURA]-sizeX)/2+sdifX;
        sposY=posY-(descritor[ANDA][ALTURA]-sizeY)/2+sdifY;
    }  
}

class Player2 extends Player1{
    //Decritores. Devem ser redeclarados para o Player2, caso contrário eles são compartilhados entre as classes. O mesmo ocorre com funções
    static int[][] descritor = new int[ESTADO_INDEX_SIZE][PROP_INDEX_SIZE];//Associa a spriteSheet do estado (ação) com as variáveis dela
    
    //Controle de direções, registra quais estão bloqueadas
    static boolean[] pathBlocked=new boolean[4];
    static final int pathBlockedIndexDir=0;//DIREITA
    static final int pathBlockedIndexCim=1;//CIMA
    static final int pathBlockedIndexEsq=2;//ESQUERDA
    static final int pathBlockedIndexBai=3;//BAIXO

    //Cálculo da posição do sprite. É deslocada do centro da área de crop, e, por consequência, da posição da hitbox
    static int sdifX=11;//Diferença de posição do centro da hitBox com o centro do sprite
    static int sdifY=-12;
    static int sposX=0;//Posição do sprite. Inicializado no construtor
    static int sposY=0;

    //Variáveis de controle do Player2
    public static int posX = 150;//Posição da hitBox (0,0 = canto esquerdo superior)
    public static int posY = 100;
    public static int sizeX = 20;//Tamanho do Player2
    public static int sizeY = 75;
    public static int estado = PARADO;//O estado (ação) do Player2
    public static int estadoAnterior = 0;//Utilizado para checar mudança de estado
    public static int frame = 0;//O frame da animação do estado
    public static int framePARADOIntervalCount=0;//Controle especial do estado PARADO
    public static int direcao=ESQ;//Direção do Player2
    public static int direcaoReajuste=0;//Reajuste da posição do sprite invertido (*-1)
    public static int acelVert=0;//Controle da aceleração vertical do Player2. (Atualmente alterada apenas pelo estado PULA)

    /********************************************************************************************************************************/
    //Define o estado (ação) do Objeto
    static void SetEstado(int set){
        if(acelVert>0)estado=PULA;
        else if(!pathBlocked[pathBlockedIndexBai])estado=CAI;
        else if(set==PULA){estado=PULA;acelVert=10;}
        else if(set==ANDA)estado=ANDA;
        else estado=PARADO;
    }

    //Define a posição do Objeto
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

    //Executa as ações de cada frame, aplicando os inputs e colisões. Também atualiza o frame do Objeto e determina seu estado
    public void ExecutaAcao(String input){
        Colisoes();

        if(input.contains("a")||input.contains("A")){
            SetEstado(ANDA);
            direcao=ESQ;
            direcaoReajuste=0;
            framePARADOIntervalCount=0;
        }
        else if(input.contains("d")||input.contains("D")){
            SetEstado(ANDA);
            direcao=DIR;
            direcaoReajuste=descritor[ANDA][LARGURA]-sdifX*2;
            framePARADOIntervalCount=0;
        }
        else if(input.contains("w")||input.contains("W")){}
        else if(input.contains("s")||input.contains("S")){}
        else if(framePARADOIntervalCount>=10){SetEstado(PARADO);framePARADOIntervalCount=0;}
        else framePARADOIntervalCount++;
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

//Checagem de colisões do Objeto. (Se sua hitbox está sobrebosta a alguma outra)
void Colisoes(){
    pathBlocked[pathBlockedIndexEsq]=false;
    pathBlocked[pathBlockedIndexDir]=false;
    pathBlocked[pathBlockedIndexCim]=false;
    pathBlocked[pathBlockedIndexBai]=true;//Setado em true temporariamente
    //NOTA: a sensibilidade do intersect é de 2 pixels. (Intervalos menores não são reconhecidos)
    if(HitBox().intersects(Player1.HitBox())){
        float relX=posX-Player1.posX;
        float relY=posY-Player1.posY;
        if(relX>((float)sizeX)*0.89)pathBlocked[pathBlockedIndexEsq]=true;//Players se trombando pela direita
        if(relX<-((float)sizeX)*0.89)pathBlocked[pathBlockedIndexDir]=true;//Players se trombando pela Esquerda
        if(relY>(float)sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//Players se trombando por baixo
        if(relY<(float)sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//Players se trombando por cima
    }
    if(HitBox().intersects(new Rectangle()));//Checagem com cenario e items
}
    /********************************************************************************************************************************/
    Player2(){
        //ANDA
        descritor[ANDA][LARGURA] = 124;//Largura de cada sprite na spriteSheet ANDA
        descritor[ANDA][ALTURA] = 141;//Altura de cada sprite na spriteSheet ANDA
        descritor[ANDA][COLS] = 7;//Total de colunas na spriteSheet ANDA
        descritor[ANDA][ROWS] = 3;//Total de linhas na spriteSheet ANDA
        descritor[ANDA][NUM] = 20;//Total de sprites na spriteSheet ANDA
        //PULA
        descritor[PULA][LARGURA] = 124;
        descritor[PULA][ALTURA] = 141;
        descritor[PULA][COLS] = 8;
        descritor[PULA][ROWS] = 7;
        descritor[PULA][NUM] = 51;
        //CAI
        descritor[CAI][LARGURA] = 124;
        descritor[CAI][ALTURA] = 141;
        descritor[CAI][COLS] = 8;
        descritor[CAI][ROWS] = 2;
        descritor[CAI][NUM] = 16;
        //PARADO
        descritor[PARADO][LARGURA] = 124;
        descritor[PARADO][ALTURA] = 141;
        descritor[PARADO][COLS] = 5;
        descritor[PARADO][ROWS] = 2;
        descritor[PARADO][NUM] = 9;
        /*Ainda não implementado
        descritor[CORRE][LARGURA] = 0;
        descritor[CORRE][ALTURA] = 0;
        descritor[CORRE][COLS] = 0;
        descritor[CORRE][ROWS] = 0;
        descritor[CORRE][NUM] = 0;
        //*/
        //Cálculo da posição dos sprites
        sposX=posX-(descritor[ANDA][LARGURA]-sizeX)/2+sdifX;
        sposY=posY-(descritor[ANDA][ALTURA]-sizeY)/2+sdifY;
    }  
}