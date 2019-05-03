import java.util.*;
import java.io.*;

//Classes: GerenteIO, GerenteFPS.

public class Gerencia{}

//Padroniza o Input recebido pelos Clientes
class GerenteIO{
    static final int IO_INDEX_SIZE = 4;//Numero de valores a serem guardados no descritor IO 
        static final int JOGADOR1ACAO = 0;
        static final int JOGADOR1ITEM = 1;
        static final int JOGADOR2ACAO = 2;
        static final int JOGADOR2ITEM = 3;
        static int[] descritorIO = new int[IO_INDEX_SIZE];

    GerenteIO(String inputLine, int playerNum){
        descritorIO[playerNum*2]=0;
    }
}

//Controla a taxa de redesenho da Janela (FPS)
class GerenteFPS extends TimerTask{
    static long tempoInicio;
    static long tempoExecucao;
    static long tempoIntervalo;

    GerenteFPS(){}

    public void run(){
        Timer tempo=new Timer();
        tempoInicio=System.currentTimeMillis();
        tempoExecucao=tempoInicio-scheduledExecutionTime();
        tempoIntervalo=1000/24;//24 frames/s
        try{
            tempo.scheduleAtFixedRate(this,tempoInicio,tempoIntervalo);
            if(tempoExecucao>tempoIntervalo)System.out.println("GERENTE_FPS: execucao levou mais tempo que o frameRate. (como lidar com isso?)");
            //avisar aos clienteFrames para redesenharem a janela;
        }catch(Exception e){
            System.out.println("Timer.scheduleAtFixedRate");
        }
    }
}