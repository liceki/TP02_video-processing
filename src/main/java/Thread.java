// A nossa thread "worker", a classe que vai fazer o trabalho pesado em paralelo
public class Thread extends java.lang.Thread {

    // Cada thread guarda seu próprio DTO com as infos da tarefa dela
    private final VideoDTO videoDTO;

    // Construtor simples pra receber o DTO com o "pedaço" do vídeo que ela vai processar
    public Thread(VideoDTO videoDTO) {
        this.videoDTO = videoDTO;
    }

    // É aqui que a mágica acontece quando a gente dá um thread.start()
    // A única coisa que ela faz é chamar os métodos de processamento, passando o DTO dela
    @Override
    public void run() {
        VideoProcessingMethods.processarVideo(videoDTO);
    }

    // Um helperzinho pra gente saber quantos frames essa thread tá processando
    public int getQuantFrames() {
        return videoDTO.getLimiteSuperior() - videoDTO.getLimiteInferior();
    }
}