import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VideoASerProcessadoDTO {

    private byte[][][] videoPreProcessamento;
    private byte[][][] videoPosProcessamento;

    private int limiteInferior;
    private int limiteSuperior;

    private int intensidadeSalPimenta;
    private int tamanhoLadoMascara;

    private TipoDeCalculo tipoDeCalculoSalPimenta;
    private TipoDeCalculo tipoDeCalculoBorrao;
}
