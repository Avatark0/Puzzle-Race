import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//Classes: Objeto, Cenario, Player1, Player2.

public class FrontObjeto extends JFrame{
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
class Cenario extends FrontObjeto{
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
class Player1 extends FrontObjeto{
    //Decritores. Devem ser redeclarados para o Player2, caso contrário eles são compartilhados entre as classes. O mesmo ocorre com funções
    static int[][] descritor = new int[ESTADO_INDEX_SIZE][PROP_INDEX_SIZE];//Associa a spriteSheet do estado (ação) com as variáveis dela

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

    /********************************************************************************************************************************/
    //Executa as ações de cada frame, aplicando os inputs e colisões. Também atualiza o frame e estado do Objeto
    public void ExecutaAcao(String input){
        SetFrame();
        SetSpritePosition();
    }

    public static void SetInputsRecebidosDoServidor(String inputLine){
        String[] valoresplayers = new String[4];
        valoresplayers=inputLine.substring(inputLine.indexOf("0:")+2, inputLine.indexOf(".1:")).split(",");
        posX = Integer.parseInt(valoresplayers[0]);
        posY = Integer.parseInt(valoresplayers[1]);
        estado = Integer.parseInt(valoresplayers[2]);
        direcao = Integer.parseInt(valoresplayers[3]);
    }

    void SetSpritePosition(){
        //Determinar a posição do sprite baseada na posição do objeto
        sposX=posX-(descritor[ANDA][LARGURA]-sizeX)/2+sdifX;
        sposY=posY-(descritor[ANDA][ALTURA]-sizeY)/2+sdifY;
    }

    void SetFrame(){
        //if(estadoAnterior!=estado&&frame%(descritor[estado][NUM]/3)==0)frame=0;
        if(estadoAnterior!=estado)frame=0;
        else if(frame==descritor[estado][NUM]-1&&(estado==CAI||estado==PULA));//Mantém o sprite no último frame da animação
        else if(frame>=descritor[estado][NUM]-1)frame=0;//Reseta o sprite da animação
        else frame++;//Avança o sprite da animação
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
        descritor[PULA][NUM] = 10; //MUDOU, era 51
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

    /********************************************************************************************************************************/
    //Executa as ações de cada frame, aplicando os inputs e colisões. Também atualiza o frame e estado do Objeto
    public void ExecutaAcao(String input){ //OBS: Função gêmea de Player1, se tiver algum erro olhar na outra classe <3
        SetFrame();
        SetSpritePosition();
    }

    public static void SetInputsRecebidosDoServidor(String inputLine){
        String[] valoresplayers = new String[4];
        valoresplayers=inputLine.substring(inputLine.indexOf("1:")+2).split(",");
        posX = Integer.parseInt(valoresplayers[0]);
        posY = Integer.parseInt(valoresplayers[1]);
        estado = Integer.parseInt(valoresplayers[2]);
        direcao = Integer.parseInt(valoresplayers[3]);
    }

    void SetSpritePosition(){
        //Determinar a posição do sprite baseada na posição do objeto
        sposX=posX-(descritor[ANDA][LARGURA]-sizeX)/2+sdifX;
        sposY=posY-(descritor[ANDA][ALTURA]-sizeY)/2+sdifY;
    }

    void SetFrame(){
        //if(estadoAnterior!=estado&&frame%(descritor[estado][NUM]/3)==0)frame=0;
        if(estadoAnterior!=estado)frame=0;
        else if(frame==descritor[estado][NUM]-1&&(estado==CAI||estado==PULA));//Mantém o sprite no último frame da animação
        else if(frame>=descritor[estado][NUM]-1)frame=0;//Reseta o sprite da animação
        else frame++;//Avança o sprite da animação
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
        descritor[PULA][NUM] = 10; //MUDOU, era 51
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