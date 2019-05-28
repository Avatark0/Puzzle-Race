import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//Classes: Objeto, Cenario, Player1, Player2.

public class BackObjeto extends JFrame{
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
class Cenario extends BackObjeto{
    //Cálculo da posição do sprite. É deslocada do centro da área de crop, e, por consequência, da posição da hitbox
    static int sdifX=0;//Diferença de posição do centro da hitBox com o centro do sprite
    static int sdifY=0;
    static int sposX=0;//Posição do sprite. Inicializado no construtor
    static int sposY=0;

    //Variáveis de controle do Cenario
    public int posX=0;//Posição da hitBox (0,0 = canto esquerdo superior)
    public int posY=0;
    public int sizeX=100;//Tamanho do Objeto
    public int sizeY=100;
    
    //Construtor
    Cenario(int tipo){
        switch(tipo){
            case 0:posX=  0;posY=150; break;
            case 1:posX=100;posY=150; break;
            case 2:posX=200;posY=250; break;
            case 3:posX=300;posY=150; break;
            case 4:posX=100;posY=250; break;
            case 5:posX=300;posY=250; break;
        }
    }

    //Retorna um retângulo com a hitBox do Objeto
    Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }

}

///////////////////////////////////////////////////////////////////////

//Classes Player 1 e Player 2 representam os personagens dos jogadores. São declarados como classes diferentes para facilitar aplicação de spriteSheets (e uma série de variáveis) diferentes.
class Player1 extends BackObjeto{
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
    //Executa as ações de cada frame, aplicando os inputs e colisões. Também atualiza o frame e estado do Objeto
    public static void ExecutaAcao(String input){
        Colisoes();//Checagem de colisões. Detecta quais direções estão bloqueadas
        ChecaOciosidade(input);
        SetEstado(input);
        SetFrame();
        SetPosition(input);
    }
    //Define a posição do Objeto
    static void SetPosition(String mov){
        if((mov.contains("a")||mov.contains("A")) && !pathBlocked[pathBlockedIndexEsq]){posX-=2;sposX-=2;}
        if((mov.contains("d")||mov.contains("D")) && !pathBlocked[pathBlockedIndexDir]){posX+=2;sposX+=2;}
        if(mov.contains("s")||mov.contains("S")){posY+=1;sposY+=1;}
        if(mov.contains("w")||mov.contains("W")){posY-=1;sposY-=1;}
        if(acelVert>0){
            if(!pathBlocked[pathBlockedIndexCim]){posY-=acelVert/3;sposY-=acelVert/3;}
            acelVert--;
        }
        else if(!pathBlocked[pathBlockedIndexBai]){
            posY-=acelVert/3;sposY-=acelVert/3;
            if(acelVert>-10)acelVert--;
        }
    }
    static void SetFrame(){
        //if(estadoAnterior!=estado&&frame%(descritor[estado][NUM]/3)==0)frame=0;
        if(estadoAnterior!=estado)frame=0;
        else if(frame==descritor[estado][NUM]-1&&(estado==CAI||estado==PULA));//Mantém o sprite no último frame da animação
        else if(frame>=descritor[estado][NUM]-1)frame=0;//Reseta o sprite da animação
        else frame++;//Avança o sprite da animação
    }
    //Define o estado (ação) do Objeto
    static void SetEstado(String input){
        estadoAnterior=estado;//Registra o estado anterior, antes do input atual. (Usado em SetFrame)
        if(acelVert>0)estado=PULA;//Caso esteja com aceleração vertical positiva, o estado é PULA
        else if(!pathBlocked[pathBlockedIndexBai])estado=CAI;//Caso não esteja pulando e não esteja sobre chão, o estado é CAI
        else if(input.contains(" ")){estado=PULA;acelVert=20;}//Caso não esteja pulando nem caindo, e receba o input de pular, pula
        else if(input.contains("w")||input.contains("W")||input.contains("s")||input.contains("S")){estado=ANDA;}
        else if(input.contains("a")||input.contains("A")||input.contains("d")||input.contains("D")){//Caso não esteja pulando nem caindo, e receba o input, anda
            estado=ANDA;
            if(input.contains("a")||input.contains("A")){
                direcao=ESQ;
                direcaoReajuste=0;    
            }
            if(input.contains("d")||input.contains("D")){
                direcao=DIR;
                direcaoReajuste=descritor[ANDA][LARGURA]-sdifX*2;
            }
        }
    }
    //Checa se o Player esta ocioso
    static void ChecaOciosidade(String input){
        System.out.println("input="+input);
        if(input.isEmpty())framePARADOIntervalCount++;//Conta os frames que Player não recebeu nenhum input
        else framePARADOIntervalCount=0;//Caso Player receba algum input, reseta a contagem de frames sem input
        if(framePARADOIntervalCount>=(1000/48)&&estado!=CAI)estado=PARADO;//Caso Player não tenha recebido nenhum input por 12 frames (0.5s), muda seu estado para PARADO
    }
    //Retorna um retângulo com a hitBox do Objeto
    static Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }

    //Checagem de colisões do Objeto. (Se sua hitbox está sobrebosta a alguma outra)
    //Cenário (em construção)
    public static int blocosNum=6;//Número de blocos do cenário
    public static Cenario[] cenario=new Cenario[blocosNum];//Vetor de blocos do cenário
    static void Colisoes(){
        pathBlocked[pathBlockedIndexEsq]=false;
        pathBlocked[pathBlockedIndexDir]=false;
        pathBlocked[pathBlockedIndexCim]=false;
        pathBlocked[pathBlockedIndexBai]=false;
        //NOTA: a sensibilidade do intersect é de ~2 pixels. (Intervalos menores não são reconhecidos)
        if(HitBox().intersects(Player2.HitBox())){
            float relX=posX-Player2.posX;
            float relY=posY-Player2.posY;
            if(relX>((float)sizeX)*0.89)pathBlocked[pathBlockedIndexEsq]=true;//Players se trombando pela direita
            if(relX<-((float)sizeX)*0.89)pathBlocked[pathBlockedIndexDir]=true;//Players se trombando pela Esquerda
            if(relY>(float)sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//Players se trombando por baixo
            if(relY<(float)sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//Players se trombando por cima
            //System.out.println("Player1: relX="+relX+" relY="+relY+" ((float)sizeX)*0.92="+((float)sizeX)*0.92)
        }
        if(HitBox().intersects(new Rectangle()));//Checagem com items.
        for(int i=0; i<blocosNum; i++){//Checagem com cenario. (Checa todos os blocos individualmente)
            if(HitBox().intersects(Sala.cenario[i].HitBox())){
                //System.out.println("Player1: Colisao com bloco "+i);
                if((float)(posX+sizeX)*0.98>Sala.cenario[i].posX && (float)(posX)*0.98<Sala.cenario[i].posX+Sala.cenario[i].sizeX){
                    if(posY>Sala.cenario[i].posY)pathBlocked[pathBlockedIndexCim]=true;
                    if(posY+sizeY>=Sala.cenario[i].posY)pathBlocked[pathBlockedIndexBai]=true;
                }
                if((float)(posY+sizeY)*0.98>Sala.cenario[i].posY && (float)(posY)*0.98<Sala.cenario[i].posY+Sala.cenario[i].sizeY){
                    if(posX<Sala.cenario[i].posX)pathBlocked[pathBlockedIndexDir]=true;
                    if(posX>Sala.cenario[i].posX)pathBlocked[pathBlockedIndexEsq]=true;
                }
            }
        }
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
    public static int posX = 350;//Posição da hitBox (0,0 = canto esquerdo superior)
    public static int posY = 50;
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
    //Executa as ações de cada frame, aplicando os inputs e colisões. Também atualiza o frame e estado do Objeto
    public static void ExecutaAcao(String input){
        Colisoes();//Checagem de colisões. Detecta quais direções estão bloqueadas
        ChecaOciosidade(input);
        SetEstado(input);
        SetFrame();
        SetPosition(input);
    }
    //Define a posição do Objeto
    static void SetPosition(String mov){
        if((mov.contains("a")||mov.contains("A")) && !pathBlocked[pathBlockedIndexEsq]){posX-=2;sposX-=2;}
        if((mov.contains("d")||mov.contains("D")) && !pathBlocked[pathBlockedIndexDir]){posX+=2;sposX+=2;}
        if(mov.contains("s")||mov.contains("S")){posY+=1;sposY+=1;}
        if(mov.contains("w")||mov.contains("W")){posY-=1;sposY-=1;}
        if(acelVert>0){
            if(!pathBlocked[pathBlockedIndexCim]){posY-=acelVert/3;sposY-=acelVert/3;}
            acelVert--;
        }
        else if(!pathBlocked[pathBlockedIndexBai]){
            posY-=acelVert/3;sposY-=acelVert/3;
            if(acelVert>-10)acelVert--;
        }
    }
    static void SetFrame(){
        //if(estadoAnterior!=estado&&frame%(descritor[estado][NUM]/3)==0)frame=0;
        if(estadoAnterior!=estado)frame=0;
        else if(frame==descritor[estado][NUM]-1&&(estado==CAI||estado==PULA));//Mantém o sprite no último frame da animação
        else if(frame>=descritor[estado][NUM]-1)frame=0;//Reseta o sprite da animação
        else frame++;//Avança o sprite da animação
    }
    //Define o estado (ação) do Objeto
    static void SetEstado(String input){
        estadoAnterior=estado;//Registra o estado anterior, antes do input atual. (Usado em SetFrame)
        if(acelVert>0)estado=PULA;//Caso esteja com aceleração vertical positiva, o estado é PULA
        else if(!pathBlocked[pathBlockedIndexBai])estado=CAI;//Caso não esteja pulando e não esteja sobre chão, o estado é CAI
        else if(input.contains(" ")){estado=PULA;acelVert=20;}//Caso não esteja pulando nem caindo, e receba o input de pular, pula
        else if(input.contains("w")||input.contains("W")||input.contains("s")||input.contains("S")){estado=ANDA;}
        else if(input.contains("a")||input.contains("A")||input.contains("d")||input.contains("D")){//Caso não esteja pulando nem caindo, e receba o input, anda
            estado=ANDA;
            if(input.contains("a")||input.contains("A")){
                direcao=ESQ;
                direcaoReajuste=0;    
            }
            if(input.contains("d")||input.contains("D")){
                direcao=DIR;
                direcaoReajuste=descritor[ANDA][LARGURA]-sdifX*2;
            }
        }
    }
    //Checa se o Player esta ocioso
    static void ChecaOciosidade(String input){
        System.out.println("input="+input);
        if(input.isEmpty())framePARADOIntervalCount++;//Conta os frames que Player não recebeu nenhum input
        else framePARADOIntervalCount=0;//Caso Player receba algum input, reseta a contagem de frames sem input
        if(framePARADOIntervalCount>=(1000/48)&&estado!=CAI)estado=PARADO;//Caso Player não tenha recebido nenhum input por 12 frames (0.5s), muda seu estado para PARADO
    }
    //Retorna um retângulo com a hitBox do Objeto
    static Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }
    //Checagem de colisões do Objeto. (Se sua hitbox está sobrebosta a alguma outra)
    //Cenário (em construção)
    public static int blocosNum=6;//Número de blocos do cenário
    public static Cenario[] cenario=new Cenario[blocosNum];//Vetor de blocos do cenário
    static void Colisoes(){
        pathBlocked[pathBlockedIndexEsq]=false;
        pathBlocked[pathBlockedIndexDir]=false;
        pathBlocked[pathBlockedIndexCim]=false;
        pathBlocked[pathBlockedIndexBai]=false;
        //NOTA: a sensibilidade do intersect é de ~2 pixels. (Intervalos menores não são reconhecidos)
        if(HitBox().intersects(Player1.HitBox())){
            float relX=posX-Player1.posX;
            float relY=posY-Player1.posY;
            if(relX>((float)sizeX)*0.89)pathBlocked[pathBlockedIndexEsq]=true;//Players se trombando pela direita
            if(relX<-((float)sizeX)*0.89)pathBlocked[pathBlockedIndexDir]=true;//Players se trombando pela Esquerda
            if(relY>(float)sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//Players se trombando por baixo
            if(relY<(float)sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//Players se trombando por cima
            //System.out.println("Player2: relX="+relX+" relY="+relY+" ((float)sizeX)*0.92="+((float)sizeX)*0.92)
        }
        if(HitBox().intersects(new Rectangle()));//Checagem com items.
        for(int i=0; i<blocosNum; i++){//Checagem com cenario. (Checa todos os blocos individualmente)
            if(HitBox().intersects(cenario[i].HitBox())){
                //System.out.println("Player1: Colisao com bloco "+i);
                if((float)(posX+sizeX)*0.98>cenario[i].posX && (float)(posX)*0.98<cenario[i].posX+cenario[i].sizeX){
                    if(posY>cenario[i].posY)pathBlocked[pathBlockedIndexCim]=true;
                    if(posY+sizeY>=cenario[i].posY)pathBlocked[pathBlockedIndexBai]=true;
                }
                if((float)(posY+sizeY)*0.98>cenario[i].posY && (float)(posY)*0.98<cenario[i].posY+cenario[i].sizeY){
                    if(posX<cenario[i].posX)pathBlocked[pathBlockedIndexDir]=true;
                    if(posX>cenario[i].posX)pathBlocked[pathBlockedIndexEsq]=true;
                }
            }
        }
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