import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Thread extends java.lang.Thread {
    private VideoASerProcessadoDTO videoASerProcessadoDTO;

    @Override
    public void run() {
        ProcessarVideo.processarVideo(videoASerProcessadoDTO);
    }



    public int getQuantFrames() {
        return videoASerProcessadoDTO.getLimiteSuperior() - videoASerProcessadoDTO.getLimiteInferior();
    }

}