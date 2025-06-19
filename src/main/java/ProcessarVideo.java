import java.util.Arrays;

public class ProcessarVideo {

    public static byte[][][] processarVideo(VideoASerProcessadoDTO videoASerProcessado) {
        salPimenta(videoASerProcessado);
        return null;
    }


    private static void salPimenta(VideoASerProcessadoDTO videoASerProcessado) {
        for (int numVezesAplicado = 0; numVezesAplicado < videoASerProcessado.getIntensidadeSalPimenta(); numVezesAplicado++) {

            for (int indiceFrame = videoASerProcessado.getLimiteInferior(); indiceFrame < videoASerProcessado.getLimiteSuperior(); indiceFrame++) {
                byte[][] framePreProcessado = videoASerProcessado.getVideoPreProcessamento()[indiceFrame];

                int ladoMascara = videoASerProcessado.getTamanhoLadoMascara();
                int metadeLado = ladoMascara / 2;
                int numPixelsVizinhos = ladoMascara * ladoMascara - 1;
                int pixelProcessado = 0;


                // passa por todos os pixels -> ex: mat[20][20]
                for (int i = metadeLado; i < framePreProcessado.length - metadeLado; i++) {
                    for (int j = metadeLado; j < framePreProcessado[i].length - metadeLado; j++) {

                        int[] vizinhos = new int[numPixelsVizinhos];
                        int index = 0;
                        // ex: i = 10  j = 10

                        for (int x = -metadeLado; x <= metadeLado; x++) {
                            for (int y = -metadeLado; y <= metadeLado; y++) {
                                vizinhos[index++] = framePreProcessado[j + x][i + y] & 0xFF;
                            }
                        }

                        if (videoASerProcessado.getTipoDeCalculoSalPimenta().equals(TipoDeCalculo.MEDIA)) {
                            pixelProcessado = calcularMediaEntreVizinhos(vizinhos);
                        } else {
                            pixelProcessado = pegarMedianaEntreVizinhos(vizinhos);
                        }

                        videoASerProcessado.getVideoPosProcessamento()[indiceFrame][i][j] = (byte) pixelProcessado;
                    }
                }

            }
        }
    }

    private static int calcularMediaEntreVizinhos(int[] vizinhos) {
        int soma = 0;
        for(int valorPixelVizinho : vizinhos) {
            soma += valorPixelVizinho;
        }
        return soma/vizinhos.length;
    }

    private static int pegarMedianaEntreVizinhos(int[] vizinhos) {
        Arrays.sort(vizinhos);
        return vizinhos[vizinhos.length / 2];
    }


//    private static void removerBorroesTempo() {
//
//        // if limiteInferior = 0 -> logica diferente -> PRIMEIRA THREAD
//        // if limiteSuperior = frames.length-1 -> lógica diferente -> ULTIMA THREAD
//
//        // if(limiteInferior+deslocamento < limiteInfeior){
////            frames[limiteInferior+deslocamento]
////        }
//
//        int janelaTemporalFrames = 2;
//
//        int primeiroFrame = limiteInferior != 0 ? limiteInferior : limiteInferior + janelaTemporalFrames;
//        System.out.println(limiteSuperior);
//        int ultimoFrame = limiteSuperior <= frames.length - janelaTemporalFrames ? limiteSuperior : limiteSuperior - janelaTemporalFrames;
//
//        for (int indiceFrame = primeiroFrame; indiceFrame < ultimoFrame; indiceFrame++) {
//
//            byte[][] frameAtual = frames[indiceFrame];
//            int altura = frameAtual.length;      // 720
//            int largura = frameAtual[0].length;  // 960
//
//
//            for (int indicePixelAltura = 0; indicePixelAltura < altura; indicePixelAltura++) {   // 0 -> 720
//                for (int indicePixelLargura = 0; indicePixelLargura < largura; indicePixelLargura++) { // 0 -> 960, 720 vezes
//
//                    int[] valoresTemporais = new int[(2 * janelaTemporalFrames + 1)];
//                    int index = 0;
//
//                    for (int deslocamento = -janelaTemporalFrames; deslocamento <= janelaTemporalFrames; deslocamento++) {
//                        valoresTemporais[index] = frames[indiceFrame + deslocamento][indicePixelAltura][indicePixelLargura] & 0xFF;
//
//
//                        index++;
//                    }
//
//                    Arrays.sort(valoresTemporais);
//                    int mediana = valoresTemporais[valoresTemporais.length / 2];
//                    videoPosProcessamento[indiceFrame][indicePixelAltura][indicePixelLargura] = (byte) mediana;
//                    // indicePixelAltura -> indice da linha
//                    // indicePixelLargura -> indice da coluna
//                }
//            }
//        }
//    }
}
