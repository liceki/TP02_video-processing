import java.util.Arrays;

public class Piao extends Thread {


    byte[][][] frames;
    byte[][][] videoPosProcessamento;
    int limiteInferior;
    int limiteSuperior;
    int intensidade;

    public Piao(byte[][][] frames, byte[][][] videoPosProcessamento, int limiteInferior, int limiteSuperior, int intensidade) {
        this.frames = frames;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        this.intensidade = intensidade;
        this.videoPosProcessamento = videoPosProcessamento;
    }



    @Override
    public void run() {
        for(int p=0; p<intensidade; p++) {

            for (int i = limiteInferior; i < limiteSuperior; i++) {
                byte[][] frame = frames[i];
                //byte[][] framePadding = gerarFramePadding(frame, 1);
                int media = 0, soma = 0;

                for (int j = 1; j < frame.length - 1; j++) {
                    for (int k = 1; k < frame[j].length - 1; k++) {
//                    media = (framePadding[j][k] + framePadding[j][k+1] + framePadding[j][k+2]
//                            + framePadding[j+1][k] + framePadding[j+1][k+2]
//                            + framePadding[j+2][k] + framePadding[j+2][k+1] + framePadding[j+2][k+2]) / 8;
                        soma = 0;
                        soma += (frame[j - 1][k - 1] & 0xFF);
                        soma += (frame[j - 1][k] & 0xFF);
                        soma += (frame[j - 1][k + 1] & 0xFF);
                        soma += (frame[j][k - 1] & 0xFF);
                        soma += (frame[j][k + 1] & 0xFF);
                        soma += (frame[j + 1][k - 1] & 0xFF);
                        soma += (frame[j + 1][k] & 0xFF);
                        soma += (frame[j + 1][k + 1] & 0xFF);

                        media = soma / 8;
                        videoPosProcessamento[i][j][k] = (byte) media;
                    }
                }
            }

            for (int i = limiteInferior; i < limiteSuperior; i++) {
                byte[][] frame = frames[i];

                for (int j = 1; j < frame.length - 1; j++) {
                    for (int k = 1; k < frame[j].length - 1; k++) {

                        int[] vizinhos = new int[9];
                        int index = 0;

                        for (int y = -1; y <= 1; y++) {
                            for (int x = -1; x <= 1; x++) {
                                vizinhos[index++] = frame[j + y][k + x] & 0xFF;
                            }
                        }

                        Arrays.sort(vizinhos);
                        int mediana = vizinhos[4]; // 13º valor após ordenar

                        videoPosProcessamento[i][j][k] = (byte) mediana;
                    }
                }
            }
        }

    }

    private static byte[][] gerarFramePadding(byte[][] frame, int pad) {
        int altura = frame.length;
        int largura = frame[0].length;

        byte[][] framePadding = new byte[altura + pad * 2][largura + pad * 2];

        for (int i = 0; i < framePadding.length; i++) {
            for (int j = 0; j < framePadding[i].length; j++) {
                int x = Math.min(Math.max(i - pad, 0), altura-1);
                int y = Math.min(Math.max(j - pad, 0), largura-1);
                framePadding[i][j] = frame[x][y];
            }
        }
        return framePadding;
    }



    public int getQuantFrames(){
        return limiteSuperior-limiteInferior;
    }

}
