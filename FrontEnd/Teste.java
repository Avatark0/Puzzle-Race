class Teste{

    static void AplicaInputsRecebidosDoServidor(String inputLine){
        //Atualiza as posições dos Players de acordo com a inputLine
        /*formato de inputLine = 0:player1posX,player1posY,player1estado.1:player2posX,player2posY,player2estado*/
        //Extrai os inputs individuais para cada Player
        String[] valoresplayers = new String[3];
        int a;
        valoresplayers=inputLine.substring(inputLine.indexOf("0:")+2, inputLine.indexOf(".1:")).split(",");
        a = Integer.parseInt(valoresplayers[0]);
        System.out.println("1: "+a);
        a = Integer.parseInt(valoresplayers[1]);
        System.out.println("2: "+a);
        a = Integer.parseInt(valoresplayers[2]);
        System.out.println("3: "+a);
        valoresplayers=inputLine.substring(inputLine.indexOf("1:")+2).split(",");
        a = Integer.parseInt(valoresplayers[0]);
        System.out.println("4: "+a);
        a = Integer.parseInt(valoresplayers[1]);
        System.out.println("5: "+a);
        a = Integer.parseInt(valoresplayers[2]);
        System.out.println("6: "+a);
    }

    public static void main(String[] args){
        AplicaInputsRecebidosDoServidor("0:100,200,1.1:100,200,1");
      }
}