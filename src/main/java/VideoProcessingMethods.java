import java.util.Arrays;

public class VideoProcessingMethods {

    public static void processarVideo(VideoDTO videoASerProcessado) {
        removerSalPimenta(videoASerProcessado);
        removerBorroesTempo(videoASerProcessado);
    }


    private static void removerSalPimenta(VideoDTO videoASerProcessado) {

        // Itera sobre cada frame que foi designado para esta thread
        for (int indiceFrame = videoASerProcessado.getLimiteInferior(); indiceFrame < videoASerProcessado.getLimiteSuperior();
             indiceFrame++) {

            // Pega o frame atual do vídeo de entrada para processar
            byte[][] frame = videoASerProcessado.getVideoPreProcessamento()[indiceFrame];

            // Define as dimensões da máscara (kernel) para o filtro, ex: 3x3
            int ladoMascara = videoASerProcessado.getTamanhoLadoMascara(),
                    // Calcula o deslocamento para acessar os valores vizinhos
                    metadeLado = ladoMascara / 2,
                    // Calcula a quantidade de vizinhos pra criar o array com o tamanho certo
                    numPixelsVizinhos = ladoMascara * ladoMascara - 1,
                    // Variável que vai guardar o novo valor do pixel pós-filtro
                    pixelProcessado = 0;

            // Obtém qual cálculo usar (Média ou Mediana) a partir dos parâmetros
            TipoDeCalculo tipoDeCalculo = videoASerProcessado.getTipoDeCalculoSalPimenta();

            // Itera sobre cada pixel do frame, ignorando as bordas
            for (int i = metadeLado; i < frame.length - metadeLado; i++) {
                for (int j = metadeLado; j < frame[i].length - metadeLado; j++) {

                    // Array pra guardar os valores dos pixels vizinhos
                    int[] vizinhos = new int[numPixelsVizinhos];
                    int index = 0;

                    // Monta a lista de vizinhos do pixel atual
                    for (int x = -metadeLado; x <= metadeLado; x++) {
                        for (int y = -metadeLado; y <= metadeLado; y++) {
                            // Pula o pixel do centro (x=0, y=0) pra pegar só os vizinhos
                            if (x != 0 || y != 0) {
                                // '& 0xFF' converte o byte (signed) pra um int "unsigned" (0 a 255)
                                vizinhos[index++] = frame[i + x][j + y] & 0xFF;
                            }
                        }
                    }

                    // Operador ternário pra escolher o cálculo de forma concisa
                    pixelProcessado = tipoDeCalculo.equals(TipoDeCalculo.MEDIA)
                            ? calcularMediaEntreVizinhos(vizinhos)
                            : pegarMedianaEntreVizinhos(vizinhos);

                    // Escreve o resultado no buffer de saída
                    videoASerProcessado.getVideoPosSalPimenta()[indiceFrame][i][j] = (byte) pixelProcessado;
                }
            }
        }
    }


    /*
     * Aplica um filtro temporal pra remover anomalias como flashes ou manchas
     * A função opera sobre um "chunk" de frames, definido no DTO
     * DTO carrega todos os dados de entrada, saída e parâmetros
     */
    private static void removerBorroesTempo(VideoDTO videoASerProcessado) {

        // Parâmetros de configuração do filtro temporal
        int janelaTemporalFrames = 2, // Raio da janela, 2 significa pegar 2 frames antes e 2 depois
                limiteInferior = videoASerProcessado.getLimiteInferior(), // Início do chunk da thread
                limiteSuperior = videoASerProcessado.getLimiteSuperior(); // Fim do chunk da thread

        // Buffers de entrada (leitura) e saída (escrita) pra fazer o processamento out-of-place
        byte[][][] video = videoASerProcessado.getVideoPosSalPimenta(),
                videoPos = videoASerProcessado.getVideoPosCorrecaoBorroes();

        // Ajustando os limites do loop pra não dar IndexOutOfBounds nas bordas do vídeo
        // A janela temporal não pode ser aplicada nos frames das extremidades
        int primeiroFrame = limiteInferior != 0
                ? limiteInferior
                : limiteInferior + janelaTemporalFrames;
        int ultimoFrame = limiteSuperior <= video.length - janelaTemporalFrames
                ? limiteSuperior
                : limiteSuperior - janelaTemporalFrames;

        TipoDeCalculo tipoDeCalculo = videoASerProcessado.getTipoDeCalculoBorrao();

        // Itera sobre os frames do meio do chunk, onde é seguro aplicar o filtro
        for (int indiceFrame = primeiroFrame; indiceFrame < ultimoFrame; indiceFrame++) {

            byte[][] frameAtual = video[indiceFrame];
            int altura = frameAtual.length;
            int largura = frameAtual[0].length;

            // Itera sobre cada pixel do frame atual
            for (int indicePixelAltura = 0; indicePixelAltura < altura; indicePixelAltura++) {
                for (int indicePixelLargura = 0; indicePixelLargura < largura; indicePixelLargura++) {

                    // Array pra guardar o valor de um mesmo pixel ao longo da janela temporal
                    int[] valoresTemporais = new int[(2 * janelaTemporalFrames + 1)];
                    int index = 0;

                    // Coleta os valores do pixel (na mesma posição x,y) dos frames vizinhos
                    for (int deslocamento = -janelaTemporalFrames; deslocamento <= janelaTemporalFrames; deslocamento++) {
                        // Lê do buffer de entrada e converte o byte pra um int "unsigned" (0-255)
                        valoresTemporais[index] = video[indiceFrame + deslocamento][indicePixelAltura][indicePixelLargura] & 0xFF;
                        index++;
                    }

                    // Seleciona e aplica o filtro (Média ou Mediana)
                    int pixelProcessado = tipoDeCalculo.equals(TipoDeCalculo.MEDIA)
                            ? calcularMediaEntreVizinhos(valoresTemporais)
                            : pegarMedianaEntreVizinhos(valoresTemporais);

                    // Armazena o novo valor do pixel no buffer de saída
                    videoPos[indiceFrame][indicePixelAltura][indicePixelLargura] = (byte) pixelProcessado;
                }
            }
        }
    }


    // Helper pra calcular a média simples de um array de pixels
    private static int calcularMediaEntreVizinhos(int[] vizinhos) {
        int soma = 0;
        // Acumula a soma de todos os valores
        for (int valorPixelVizinho : vizinhos) {
            soma += valorPixelVizinho;
        }
        // Retorna a divisão inteira
        return soma / vizinhos.length;
    }

    // Helper pra pegar a mediana, ATENÇÃO: o array de entrada é ordenado e modificado
    private static int pegarMedianaEntreVizinhos(int[] vizinhos) {
        // Ordena o array pra achar o valor do meio
        Arrays.sort(vizinhos);

        // Tratamento pro caso (improvável) de array com tamanho par
        if (vizinhos.length % 2 == 0) {
            return (vizinhos[(vizinhos.length / 2) - 1] + vizinhos[(vizinhos.length / 2)]) / 2;
        }

        // Se for ímpar, a mediana é só pegar o cara do meio
        return vizinhos[vizinhos.length / 2];
    }
}
