import java.util.Arrays;

public class Piao extends Thread {


    byte[][][] frames;
    byte[][][] videoPosProcessamento;
    int limiteInferior;
    int limiteSuperior;
    int intensidadeSalPimenta;

    int larguraMascaraSalPimenta;
    int alturaMascaraSalPimenta;


    TipoDeCalculo tipoDeCalculoSalPimenta;
    TipoDeCalculo tipoDeCalculoBorrao;


    public Piao(byte[][][] frames, byte[][][] videoPosProcessamento, int limiteInferior, int limiteSuperior, int intensidadeSalPimenta, TipoDeCalculo tipoDeCalculoSalPimenta, TipoDeCalculo tipoDeCalculoBorrao) {
        this.frames = frames;
        this.videoPosProcessamento = videoPosProcessamento;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        this.intensidadeSalPimenta = intensidadeSalPimenta;
        this.tipoDeCalculoSalPimenta = tipoDeCalculoSalPimenta;
        this.tipoDeCalculoBorrao = tipoDeCalculoBorrao;
    }

    @Override
    public void run() {
        salPimenta();
        removerBorroesTempo();
    }


    private void salPimenta() {
        for (int i = 0; i < intensidadeSalPimenta; i++) {

            for (int indiceFrame = limiteInferior; indiceFrame < limiteSuperior; indiceFrame++) {
                byte[][] frame = frames[indiceFrame];



                switch (tipoDeCalculoSalPimenta) {
                    case MEDIA -> salPimentaMedia(frame, indiceFrame);
//                    case MEDIANA -> salPimentaMediana(frame, indiceFrame);
//                    case MEDIA_E_MEDIANA -> {
//                        salPimentaMediana(frame, indiceFrame);
//                        salPimentaMedia(frame, indiceFrame);
//                    }
                }
            }
        }
    }

    private void salPimentaMedia(byte[][] frame, int indiceFrame) {
        int media = 0, soma;

        larguraMascaraSalPimenta = 3;
        alturaMascaraSalPimenta = 3;
        // ex: mascara 5x5
        int metadeLargura = larguraMascaraSalPimenta/2; // -> 2
        int metadeAltura = alturaMascaraSalPimenta/2; // -> 2

        // passa por todos os pixels -> ex: mat[20][20]
        for (int i = metadeLargura; i < frame.length - metadeLargura; i++) {
            for (int j = metadeAltura; j < frame[i].length - metadeAltura; j++) {
                soma = 0;
                // ex: i = 10  j = 10
                for (int x = -metadeLargura; x <= metadeLargura; x++) {
                    for (int y = -metadeAltura; y <= metadeAltura; y++) {
                        soma += (frame[i + x][j + y] & 0xFF);
                    }
                }
                int totalPixels = (larguraMascaraSalPimenta * larguraMascaraSalPimenta) - 1;
                media = (soma - (frame[i][j] & 0xFF)) / totalPixels;

                videoPosProcessamento[indiceFrame][i][j] = (byte) media;
            }
        }
    }

    private void salPimentaMediana(byte[][] frame, int indiceFrame) {

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

                videoPosProcessamento[indiceFrame][j][k] = (byte) mediana;
            }
        }
    }

    private void removerBorroesTempo() {

        int janelaTemporalFrames = 1;

        for (int f = limiteInferior + janelaTemporalFrames; f < limiteSuperior - janelaTemporalFrames; f++) {

            byte [][] frameAtual = frames[f];
            int altura = frameAtual.length;
            int largura = frameAtual[0].length;

            for (int i = 0; i < largura; i++) {
                for (int j = 0; j < altura; j++) {

                    int[] valoresTemporais = new int[(2 * janelaTemporalFrames + 1)];
                    int index = 0;

                    for (int deslocamento = -janelaTemporalFrames; deslocamento <= janelaTemporalFrames; deslocamento++) {
                        valoresTemporais[index++] = frames[f + deslocamento][i][j] & 0xFF;
                    }

                    Arrays.sort(valoresTemporais);
                    int mediana = valoresTemporais[valoresTemporais.length/2];
                    videoPosProcessamento[f][i][j] = (byte) mediana;
                }
            }
        }
    }

    public int getQuantFrames() {
        return limiteSuperior - limiteInferior;
    }

}