import java.util.Arrays;

public class VideoProcessingMethods {

    public static void processarVideo(VideoDTO videoASerProcessado) {
        removerSalPimenta(videoASerProcessado);
        removerBorroesTempo(videoASerProcessado);
    }

    private static void removerSalPimenta(VideoDTO videoASerProcessado) {
        for (int indiceFrame = videoASerProcessado.getLimiteInferior(); indiceFrame < videoASerProcessado.getLimiteSuperior(); indiceFrame++) {

            byte[][] frame = videoASerProcessado.getVideoPreProcessamento()[indiceFrame];
            int ladoMascara = videoASerProcessado.getTamanhoLadoMascara(),
                    metadeLado = ladoMascara/2,
                    numPixelsVizinhos = ladoMascara * ladoMascara-1,
                    pixelProcessado = 0;

            TipoDeCalculo tipoDeCalculo = videoASerProcessado.getTipoDeCalculoSalPimenta();

            // Passa por todos os pixels
            for (int i = metadeLado; i < frame.length - metadeLado; i++) {
                for (int j = metadeLado; j < frame[i].length - metadeLado; j++) {

                    int[] vizinhos = new int[numPixelsVizinhos];
                    int index = 0;

                    for (int x = -metadeLado; x <= metadeLado; x++) {
                        for (int y = -metadeLado; y <= metadeLado; y++) {
                            if (x != 0 || y != 0) vizinhos[index++] = frame[i + x][j + y] & 0xFF;
                        }
                    }

                    pixelProcessado = tipoDeCalculo.equals(TipoDeCalculo.MEDIA)
                            ? calcularMediaEntreVizinhos(vizinhos)
                            : pegarMedianaEntreVizinhos(vizinhos);
                    videoASerProcessado.getVideoPosSalPimenta()[indiceFrame][i][j] = (byte) pixelProcessado;
                }
            }


        }
    }



    private static void removerBorroesTempo(VideoDTO videoASerProcessado) {

        // if limiteInferior = 0 -> logica diferente -> PRIMEIRA THREAD
        // if limiteSuperior = frames.length-1 -> lógica diferente -> ULTIMA THREAD

        int janelaTemporalFrames = 2,
                limiteInferior = videoASerProcessado.getLimiteInferior(),
                limiteSuperior = videoASerProcessado.getLimiteSuperior();
        byte[][][] video = videoASerProcessado.getVideoPosSalPimenta(),
                videoPos = videoASerProcessado.getVideoPosCorrecaoBorroes();

        int primeiroFrame = limiteInferior != 0
                ? limiteInferior
                : limiteInferior + janelaTemporalFrames;
        int ultimoFrame = limiteSuperior <= video.length - janelaTemporalFrames
                ? limiteSuperior
                : limiteSuperior - janelaTemporalFrames;
        TipoDeCalculo tipoDeCalculo = videoASerProcessado.getTipoDeCalculoBorrao();

        for (int indiceFrame = primeiroFrame; indiceFrame < ultimoFrame; indiceFrame++) {

            byte[][] frameAtual = video[indiceFrame];
            int altura = frameAtual.length;      // 720
            int largura = frameAtual[0].length;  // 960

            for (int indicePixelAltura = 0; indicePixelAltura < altura; indicePixelAltura++) {   // 0 -> 720
                for (int indicePixelLargura = 0; indicePixelLargura < largura; indicePixelLargura++) { // 0 -> 960, 720 vezes

                    int[] valoresTemporais = new int[(2 * janelaTemporalFrames + 1)];
                    int index = 0;

                    for (int deslocamento = -janelaTemporalFrames; deslocamento <= janelaTemporalFrames; deslocamento++) {
                        valoresTemporais[index] = video[indiceFrame + deslocamento][indicePixelAltura][indicePixelLargura] & 0xFF;
                        index++;
                    }

                    int pixelProcessado = tipoDeCalculo.equals(TipoDeCalculo.MEDIA)
                            ? calcularMediaEntreVizinhos(valoresTemporais)
                            : pegarMedianaEntreVizinhos(valoresTemporais);
                    videoPos[indiceFrame][indicePixelAltura][indicePixelLargura] = (byte) pixelProcessado;


                }
            }
        }
    }


    private static int calcularMediaEntreVizinhos(int[] vizinhos) {
        int soma = 0;
        for (int valorPixelVizinho : vizinhos) {
            soma += valorPixelVizinho;
        }
        return soma / vizinhos.length;
    }

    private static int pegarMedianaEntreVizinhos(int[] vizinhos) {
        Arrays.sort(vizinhos);
        if (vizinhos.length % 2 == 0) {
            return (vizinhos[(vizinhos.length / 2) - 1] + vizinhos[(vizinhos.length / 2)]) / 2;
        }
        return vizinhos[vizinhos.length / 2];

    }
}
