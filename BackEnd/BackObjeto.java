import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;

//Classes: Objeto, Vitoria, Cenario, Player1.

public class BackObjeto extends JFrame{
    final static int PROP_INDEX_SIZE = 5;//Número de propriedades para cropar a spriteSheet e acessar os sprites individuais
        final static int LARGURA = 0;//Index das propriedades do sprite na spriteSheet
        final static int ALTURA = 1;
        final static int COLS = 2;
        final static int ROWS = 3;
        final static int NUM = 4;
    final static int ESTADO_INDEX_SIZE = 5;//Número de estados do jogador
        final static int ANDA = 0;//Index dos estados do jogador
        final static int CORRE = 1;
        final static int PULA = 2;
        final static int CAI = 3;
        final static int PARADO = 4;
    //Index das direções do jogador
    final static int DIR=-1;
    final static int ESQ=1;
    //Index das direções bloqueadas
    final static int pathBlockedIndexDir=0;//DIREITA
    final static int pathBlockedIndexCim=1;//CIMA
    final static int pathBlockedIndexEsq=2;//ESQUERDA
    final static int pathBlockedIndexBai=3;//BAIXO
}

class Vitoria extends BackObjeto{
    public static int posX=21;//Posição da hitBox (0,0 = canto esquerdo superior)
    public static int posY=186;
    public static int sizeX=10;//Tamanho do Objeto
    public static int sizeY=14;
    //Retorna um retângulo com a hitBox do Objeto
    static Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }
}

//As instâncias desta classe compõe o cenário lógico do jogo
class Cenario extends BackObjeto{
    //Numero de blocos da fase
    public static int blocosNum=20;
    //Variáveis de controle do Cenario
    public int posX=0;//Posição da hitBox (0,0 = canto esquerdo superior)
    public int posY=0;
    public int sizeX=0;//Tamanho do Objeto
    public int sizeY=0;
    public int maxX=1098;//Tamanho da tela X (1080 + 18 do contorno(?))
    public int maxY=680;//Tamanho da tela Y (650 + 30 da barra  de opções da janela)
    //Construtor
    Cenario(int i){
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
            //*/
          }
    }
    //Retorna um retângulo com a hitBox do Objeto
    Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }
}

//Classe Player1 representa os personagens dos jogadores. São declarados como instâncias da mesma classe
class Player1 extends BackObjeto{
    public int slotNumber;
    //Posição do jogador (0,0 = canto esquerdo superior)
    public int posX = 900;
    public int posY = 550;
    /*************************************************************/
    //Idênticas na inicialização dos jogadores:
    //Controle de direções, registra quais estão bloqueadas
    public boolean[] pathBlocked=new boolean[4];
    //Controles de posição e estado
    public int sizeX = 20;//Tamanho do jogador
    public int sizeY = 75;
    public int estado = PARADO;//O estado (ação) do jogador
    public int estadoAnterior = PARADO;//O estado anterior do jogador
    public int frameIntervalCount=0;//Contagem de frames das ações
    public int framePARADOIntervalCount=0;//Controle especial do estado PARADO (checagem de frames de ociosidade)
    public int direcao=ESQ;//Direção do jogador
    //Controles de velocidade
    public int velX=0;
    public int velY=0;
    public int maxVelX=5;
    public int maxVelY=4;
    public int duracaoPulo=0;
    public boolean temPulo=true;
    //Executa as ações de cada frame, aplicando os inputs e colisões. Também atualiza o frame e estado do Objeto
    public void ExecutaAcao(String input){
        Colisoes();//Checagem de colisões. Detecta quais direções estão bloqueadas
        ChecaOciosidade(input);//Conta os frames ociosos (parados), e muda o estado para parado caso necessário
        SetEstado(input);
        SetVelocidade(input);
        SetPosition();
    }
    //Ajustes a posição, a partir da velocidade e colisões
    void SetPosition(){
        if(velX<0&&!pathBlocked[pathBlockedIndexEsq]){
            if(-velX<maxVelX)posX+=velX;
            else posX-=maxVelX;
        }
        if(velX>0&&!pathBlocked[pathBlockedIndexDir]){
            if(velX<maxVelX)posX+=velX;
            else posX+=maxVelX;
        }
        if(velY<0&&!pathBlocked[pathBlockedIndexCim]){
            if(-velY<maxVelY)posY+=velY;
            else posY-=maxVelY;
        }
        if(velY>0&&!pathBlocked[pathBlockedIndexBai]){
            if(velY<maxVelY)posY+=velY;
            else posY+=maxVelY;
        }
    }
    void SetVelocidade(String input){
        frameIntervalCount++;
        if(estadoAnterior!=estado){frameIntervalCount=0;System.out.println("Player"+slotNumber+": frameCount=0");}
        if(input.contains("d")||input.contains("D")){
            if(velX<0)velX=-1;
            if(velX<=maxVelX)velX++;
        }
        else if(input.contains("a")||input.contains("A")){
            if(velX>0)velX=1;
            if(velX>=-maxVelX)velX--;
        }
        else if(velX>0)velX--;
        else if(velX<0)velX++;
        if(estado==PULA){
            if(estadoAnterior!=estado){duracaoPulo=20;}
            velY=-duracaoPulo;
            duracaoPulo--;
        }
        else if(!pathBlocked[pathBlockedIndexBai])velY++;
        else if(velY>0)velY--;
        else if(velY<0)velY++;
        //if(input.contains("s")||input.contains("S")){posY+=1;}
        //if(input.contains("w")||input.contains("W")){posY-=1;}
    }
    //Define o estado (ação) do Objeto, a partir dos inputs e colisões
    void SetEstado(String input){
        estadoAnterior=estado;
        if(duracaoPulo==0&&!temPulo)estado=CAI;//Caso esteja na velocidade limite de queda o estado é CAI
        else if((input.contains(" ")&&temPulo)||(estado==PULA&&!temPulo)){estado=PULA;temPulo=false;}//Controle de pulo pela variável temPulo, resetada ao tocar no chão
        //else if(input.contains("w")||input.contains("W")||input.contains("s")||input.contains("S")){estado=ANDA;}//Caso escadas e cordas sejam adicionadas, este estado será referente a elas
        else if(input.contains("a")||input.contains("A")||input.contains("d")||input.contains("D")){//Caso não esteja pulando nem caindo, e receba o input, anda
            estado=ANDA;
            if(input.contains("a")||input.contains("A"))direcao=ESQ;
            if(input.contains("d")||input.contains("D"))direcao=DIR;
        }
    }
    //Checa se o Player esta ocioso
    void ChecaOciosidade(String input){
        if(input.isEmpty())framePARADOIntervalCount++;//Conta os frames que jogador não recebeu nenhum input
        else framePARADOIntervalCount=0;//Caso Player receba algum input, reseta a contagem de frames sem input
        if(framePARADOIntervalCount>=(12)&&estado!=CAI)estado=PARADO;//Caso jogador não tenha recebido nenhum input por 12 frames (0.5s), muda seu estado para PARADO
    }
    //Retorna um retângulo com a hitBox do Objeto
    Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }
    //Checagem de colisões do Objeto. (Se sua hitbox está sobrebosta a alguma outra)
    void Colisoes(){
        pathBlocked[pathBlockedIndexEsq]=false;
        pathBlocked[pathBlockedIndexDir]=false;
        pathBlocked[pathBlockedIndexCim]=false;
        pathBlocked[pathBlockedIndexBai]=false;
        //NOTA: a sensibilidade do intersect é de ~2 pixels. (Intervalos menores não são reconhecidos)
        if(HitBox().intersects(new Rectangle()));//Checagem com items.
        
        for(int i=0; i<Cenario.blocosNum; i++){//Checagem com cenario. (Checa todos os blocos individualmente)
            if(HitBox().intersects(Sala.cenario[i].HitBox())){
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
        if(pathBlocked[pathBlockedIndexBai])temPulo=true;//Reset da variável temPulo, utilizada no controle do estado PULA
        //referências cruzadas entre os Players 
        if(HitBox().intersects(Vitoria.HitBox())){Sala.EncerraPartida(slotNumber);}//0:P1, 1:P2, etc
        /*
        if(HitBox().intersects(Player2.HitBox())){
            float relX=posX-Player2.posX;
            float relY=posY-Player2.posY;
            if(relX>((float)sizeX)*0.89)pathBlocked[pathBlockedIndexEsq]=true;//Players se trombando pela direita
            if(relX<-((float)sizeX)*0.89)pathBlocked[pathBlockedIndexDir]=true;//Players se trombando pela Esquerda
            if(relY>(float)sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//Players se trombando por baixo
            if(relY<(float)sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//Players se trombando por cima
        }//*/
    }
}
/*LEGADO:
class Player2 extends Player1{
    //Posição do Player2 (0,0 = canto esquerdo superior)
    public static int posX = 600;
    public static int posY = 20;
    /*************************************************************
    //Idênticas na inicialização entre Players:
    //Controle de direções, registra quais estão bloqueadas
    static boolean[] pathBlocked=new boolean[4];
    static final int pathBlockedIndexDir=0;//DIREITA
    static final int pathBlockedIndexCim=1;//CIMA
    static final int pathBlockedIndexEsq=2;//ESQUERDA
    static final int pathBlockedIndexBai=3;//BAIXO
    //Controles de posição e estado
    public static int sizeX = 20;//Tamanho do Player
    public static int sizeY = 75;
    public static int estado = PARADO;//O estado (ação) do Player
    public static int estadoAnterior = PARADO;//O estado anterior do Player
    public static int frameIntervalCount=0;//Contagem de frames das ações
    public static int framePARADOIntervalCount=0;//Controle especial do estado PARADO (checagem de frames de ociosidade)
    public static int direcao=ESQ;//Direção do Player
    public static boolean vencedor=false;
    //Controles de velocidade
    public static int velX=0;
    public static int velY=0;
    public static int maxVelX=5;
    public static int maxVelY=4;
    public static int duracaoPulo=0;
    public static boolean temPulo=true;
    //Executa as ações de cada frame, aplicando os inputs e colisões. Também atualiza o frame e estado do Objeto
    public static void ExecutaAcao(String input){
        Colisoes();//Checagem de colisões. Detecta quais direções estão bloqueadas
        ChecaOciosidade(input);//Conta os frames ociosos (parados), e muda o estado para parado caso necessário
        SetEstado(input);
        SetVelocidade(input);
        SetPosition();
    }
    //Ajustes a posição, a partir da velocidade e colisões
    static void SetPosition(){
        if(velX<0&&!pathBlocked[pathBlockedIndexEsq]){posX+=velX;}
        if(velX>0&&!pathBlocked[pathBlockedIndexDir]){posX+=velX;}
        if(velY<0&&!pathBlocked[pathBlockedIndexCim]){posY+=velY;}
        if(velY>0&&!pathBlocked[pathBlockedIndexBai]){posY+=velY;}
    }
    static void SetVelocidade(String input){
        frameIntervalCount++;
        if(estadoAnterior!=estado){frameIntervalCount=0;System.out.println("Player1: frameCount=0");}
        if(input.contains("d")||input.contains("D")){
            if(velX<0)velX=-1;
            if(velX<maxVelX)velX++;
        }
        else if(velX>0)velX--;
        if(input.contains("a")||input.contains("A")){
            if(velX>0)velX=1;
            if(velX>-maxVelX)velX--;
        }
        else if(velX<0)velX++;
        if(estado==PULA){
            if(estadoAnterior!=estado){duracaoPulo=24;}
            if(duracaoPulo>0)velY=-(duracaoPulo/2)-2;
            duracaoPulo--;
        }
        if(velY<maxVelY&&!pathBlocked[pathBlockedIndexBai])velY++;
        else if(pathBlocked[pathBlockedIndexBai])velY=0;
        if(input.contains("s")||input.contains("S")){posY+=1;}
        if(input.contains("w")||input.contains("W")){posY-=1;}
    }
    //Define o estado (ação) do Objeto, a partir dos inputs e colisões
    static void SetEstado(String input){
        estadoAnterior=estado;
        if(velY==maxVelY)estado=CAI;//Caso esteja na velocidade limite de queda o estado é CAI
        else if((input.contains(" ")&&temPulo)||(estado==PULA&&!temPulo)){estado=PULA;temPulo=false;}//Controle de pulo pela variável temPulo, resetada ao tocar no chão
        else if(input.contains("w")||input.contains("W")||input.contains("s")||input.contains("S")){estado=ANDA;}//Caso escadas e cordas sejam adicionadas, este estado será referente a elas
        else if(input.contains("a")||input.contains("A")||input.contains("d")||input.contains("D")){//Caso não esteja pulando nem caindo, e receba o input, anda
            estado=ANDA;
            if(input.contains("a")||input.contains("A"))direcao=ESQ;
            if(input.contains("d")||input.contains("D"))direcao=DIR;
        }
    }
    //Checa se o Player esta ocioso
    static void ChecaOciosidade(String input){
        if(input.isEmpty())framePARADOIntervalCount++;//Conta os frames que Player não recebeu nenhum input
        else framePARADOIntervalCount=0;//Caso Player receba algum input, reseta a contagem de frames sem input
        if(framePARADOIntervalCount>=(12)&&estado!=CAI)estado=PARADO;//Caso Player não tenha recebido nenhum input por 12 frames (0.5s), muda seu estado para PARADO
    }
    //Retorna um retângulo com a hitBox do Objeto
    static Rectangle HitBox(){
        Rectangle hitBox=new Rectangle(posX,posY,sizeX,sizeY);
        return hitBox;
    }
    //Checagem de colisões do Objeto. (Se sua hitbox está sobrebosta a alguma outra)
    static void Colisoes(){
        pathBlocked[pathBlockedIndexEsq]=false;
        pathBlocked[pathBlockedIndexDir]=false;
        pathBlocked[pathBlockedIndexCim]=false;
        pathBlocked[pathBlockedIndexBai]=false;
        //NOTA: a sensibilidade do intersect é de ~2 pixels. (Intervalos menores não são reconhecidos)
        if(HitBox().intersects(new Rectangle()));//Checagem com items.
        for(int i=0; i<Cenario.blocosNum; i++){//Checagem com cenario. (Checa todos os blocos individualmente)
            if(HitBox().intersects(Sala.cenario[i].HitBox())){
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
        if(pathBlocked[pathBlockedIndexBai])temPulo=true;//Reset da variável temPulo, utilizada no controle do estado PULA
        /*referências cruzadas entre os Players/ 
        if(HitBox().intersects(Vitoria.HitBox())){Sala.EncerraPartida(1);}//1 equivale a este player (Player2)
        /*
        if(HitBox().intersects(Player1.HitBox())){
            float relX=posX-Player1.posX;
            float relY=posY-Player1.posY;
            if(relX>((float)sizeX)*0.89)pathBlocked[pathBlockedIndexEsq]=true;//Players se trombando pela direita
            if(relX<-((float)sizeX)*0.89)pathBlocked[pathBlockedIndexDir]=true;//Players se trombando pela Esquerda
            if(relY>(float)sizeY*0.96)pathBlocked[pathBlockedIndexCim]=true;//Players se trombando por baixo
            if(relY<(float)sizeY*0.96)pathBlocked[pathBlockedIndexBai]=true;//Players se trombando por cima
        }///
    }
}
*/