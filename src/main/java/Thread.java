

public class Thread extends java.lang.Thread {
    private final VideoDTO videoDTO;

    public Thread(VideoDTO videoDTO) {
        this.videoDTO = videoDTO;
    }

    @Override
    public void run() {
        VideoProcessingMethods.processarVideo(videoDTO);
    }





    public int getQuantFrames() {
        return videoDTO.getLimiteSuperior() - videoDTO.getLimiteInferior();
    }

}