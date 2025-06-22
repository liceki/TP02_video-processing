import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class VideoProcessing {

    /* Carrega a biblioteca nativa (via nu.pattern.OpenCV) assim que a classe é carregada na VM. */
    static {
        nu.pattern.OpenCV.loadLocally();
    }

    public static byte[][][] carregarVideo(String caminho) {

        VideoCapture captura = new VideoCapture(caminho);
        if (!captura.isOpened()) {
            System.out.println("Vídeo está sendo processado por outra aplicação");
        }

        //tamanho do frame
        int largura = (int) captura.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int altura = (int) captura.get(Videoio.CAP_PROP_FRAME_HEIGHT);

        //não conhecço a quantidade dos frames (melhorar com outra lib) :(
        List<byte[][]> frames = new ArrayList<>();

        //matriz RGB mesmo preto e branco?? - uso na leitura do frame
        Mat matrizRGB = new Mat();

        //criando uma matriz temporária em escala de cinza
        Mat escalaCinza = new Mat(altura, largura, CvType.CV_8UC1); //1 única escala
        byte linha[] = new byte[largura];

        while (captura.read(matrizRGB)) {//leitura até o último frames

            //convertemos o frame atual para escala de cinza
            Imgproc.cvtColor(matrizRGB, escalaCinza, Imgproc.COLOR_BGR2GRAY);

            //criamos uma matriz para armazenar o valor de cada pixel (int estouro de memória)
            byte video[][] = new byte[altura][largura];
            for (int y = 0; y < altura; y++) {
                escalaCinza.get(y, 0, linha);
                for (int x = 0; x < largura; x++) {
                    video[y][x] = (byte) (linha[x] & 0xFF); //shift de correção - unsig
                }
            }
            frames.add(video);
        }
        captura.release();

        /* converte o array de frames em matriz 3D */
        byte cuboPixels[][][] = new byte[frames.size()][][];
        for (int i = 0; i < frames.size(); i++) {
            cuboPixels[i] = frames.get(i);
        }

        return cuboPixels;
    }

    public static void gravarVideo(byte video[][][], String caminho, double fps) {

        int qFrames = video.length;
        int altura = video[0].length;
        int largura = video[0][0].length;

        int fourcc = VideoWriter.fourcc('a', 'v', 'c', '1');   // identificação codec .mp4
        VideoWriter escritor = new VideoWriter(
                caminho, fourcc, fps, new Size(largura, altura), true);

        if (!escritor.isOpened()) {
            System.err.println("Erro ao gravar vídeo no caminho sugerido");
        }

        Mat matrizRgb = new Mat(altura, largura, CvType.CV_8UC3); //voltamos a operar no RGB (limitação da lib)

        byte linha[] = new byte[largura * 3];                // BGR intercalado

        for (int f = 0; f < qFrames; f++) {
            for (int y = 0; y < altura; y++) {
                for (int x = 0; x < largura; x++) {
                    byte g = (byte) video[f][y][x];
                    int i = x * 3;
                    linha[i] = linha[i + 1] = linha[i + 2] = g;     // cinza → B,G,R
                }
                matrizRgb.put(y, 0, linha);
            }
            escritor.write(matrizRgb);
        }
        escritor.release(); //limpando o buffer 
    }


    public static void main(String[] args) {
        // Vendo o tamanho da heap que a JVM alocou, só pra ter uma ideia do consumo de memória
        System.out.println("Heap inicial (Xms): " + Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB");
        System.out.println("Heap máximo  (Xmx): " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");

        String caminhoVideo = "src/main/videos/video-cortado.mp4";
        String caminhoGravar = "src/main/videos/video-pos-borrao.mp4";
        double fps = 24.0;

        System.out.println("Carregando o vídeo do diretório: " + caminhoVideo);
        byte video[][][] = carregarVideo(caminhoVideo);

        System.out.printf("Nº de Frames: %d   Resolução: %d x %d \n",
                video.length, video[0][0].length, video[0].length);


        byte[][][] videoProcessado = null;
        // Definindo os parâmetros dos filtros -> 'final' pra garantir que ninguém mude sem querer
        final int tamanhoLadoMascara = 3;
        final TipoDeCalculo tipoDeCalculoSalPimenta = TipoDeCalculo.MEDIA;
        final TipoDeCalculo tipoDeCalculoBorrao = TipoDeCalculo.MEDIANA;

        // Usando um DTO pra não passar 500 parâmetros na chamada dos métodos de correção
        VideoDTO videoDTO = new VideoDTO(video);
        // Criamos os arrays de resultado aqui fora. Assim a gente passa eles como referência
        // e as threads os modificam. Evita criar um monte de array gigante dentro do loop.
        videoDTO.setVideoPosSalPimenta(new byte[video.length][video[0].length][video[0][0].length]);
        videoDTO.setVideoPosCorrecaoBorroes(new byte[video.length][video[0].length][video[0][0].length]);
        videoDTO.setTamanhoLadoMascara(tamanhoLadoMascara);
        videoDTO.setTipoDeCalculoSalPimenta(tipoDeCalculoSalPimenta);
        videoDTO.setTipoDeCalculoBorrao(tipoDeCalculoBorrao);


        System.out.println("\nIniciando Processamento!");
        // Loop principal pra testar a performance com 1, 2, 4, 8... threads
        // O numThreads dobra a cada iteracao, fazendo todos os testes requisitados
        for (int numThreads = 1; numThreads <= 32; numThreads *= 2) {
            System.out.println("Execução com " + numThreads + " thread(s)");
            // Pega o tempo antes...
            long inicio = System.currentTimeMillis();
            // Chamando a função que faz a magica acontecer
            videoProcessado = processarVideo(videoDTO, numThreads);
            // ...e depois, pra gente poder calcular o speedup
            long fim = System.currentTimeMillis();
            System.out.println("Tempo de execução com " + numThreads + " Thread(s): " + (fim - inicio));
            System.out.println("--------------------------\n");
        }

        // Se o video estiver nulo, ele nao sera salvo
        if (videoProcessado == null) return;

        System.out.println("Salvando...  " + caminhoGravar);
        gravarVideo(videoProcessado, caminhoGravar, fps);
        System.out.println("Término do processamento");
    }


    private static byte[][][] processarVideo(VideoDTO videoDTO, int numThreads) {
        // caso base: execução sequencial (sem criar threads)
        // -> é o nosso benchmark pra comparar o desempenho
        if (numThreads == 1) return processarSequencial(videoDTO);

        return processarParalelo(videoDTO, numThreads);
    }


    private static byte[][][] processarSequencial(VideoDTO videoDTO) {
        // Configura o DTO para processar o vídeo inteiro em um único "chunk"
        // Define o início do processamento no primeiro frame
        videoDTO.setLimiteInferior(0);
        // Define o fim do processamento no último frame
        videoDTO.setLimiteSuperior(videoDTO.getVideoPreProcessamento().length);
        // Invoca o metodo que contém a logica de aplicação dos filtros
        VideoProcessingMethods.processarVideo(videoDTO);

        // Retorna a referência para o buffer de vídeo com o resultado final
        return videoDTO.getVideoPosCorrecaoBorroes();
    }


    private static byte[][][] processarParalelo(VideoDTO videoDTO, int numThreads) {
        // Exemplo para que utilizamos para elaboraçao da logica de divisao de frames para as threads
        //video[247] 246 indices (do 0 ao 246)
        //247 frames para 4 threads
        //numFramesPorThread = 61
        //Resto = 3

        // Variáveis pra controlar a divisão do trabalho entre as threads.
        Thread thread;
        List<Thread> threads = new ArrayList<>();
        int numFramesPorThread = videoDTO.getVideoPreProcessamento().length / numThreads,
                resto = videoDTO.getVideoPreProcessamento().length % numThreads,
                limiteInferior = 0,
                limiteSuperior,
                numFramesAlocados;


        // Loop que cria e configura cada thread pra processar um "pedaço" do vídeo.
        for (int i = 0; i < numThreads; i++) {
            numFramesAlocados = numFramesPorThread;
            // Se a divisão não for exata, distribui o resto dos frames pras primeiras threads.
            // Cada uma pega um frame a mais até o resto acabar.
            if (resto > 0) {
                numFramesAlocados++;
                resto--;
            }
            limiteSuperior = limiteInferior + numFramesAlocados;

            // Cria a thread, passando o DTO com os frames que ela vai processar (do limiteInferior ao Superior).
            thread = new Thread(new VideoDTO(videoDTO, limiteInferior, limiteSuperior));
            // Atualiza o limite inferior pra próxima thread já começar do lugar certo.
            limiteInferior = limiteSuperior;

            // Inicia a thread. Agora ela tá rodando "em paralelo" com a main.
            thread.start();
            // Guarda a thread na lista para podermos aguardar ela terminar
            threads.add(thread);
        }

        // esperamos todas as threads finalizarem suas tasks
        for (Thread t : threads) {
            try {
                // O .join() faz a thread main 'pausar' aqui até a thread 't' terminar.
                // Sem isso, tentariamos retornar o vídeo antes de ele estar pronto.
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // Agora que todas as threads terminaram, o vídeo de resultado tá completo.
        return videoDTO.getVideoPosCorrecaoBorroes();
    }

}