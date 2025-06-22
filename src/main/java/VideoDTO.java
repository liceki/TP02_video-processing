// DTO (Data Transfer Object) para encapsular os dados do vídeo e os parâmetros do processamento
// Facilita passar todas as informações necessárias entre os métodos e para as threads
public class VideoDTO {
    // Matriz 3D com o vídeo original, que servirá como fonte de leitura
    // É 'final' para garantir que não seja acidentalmente sobrescrito
    private final byte[][][] videoPreProcessamento;
    private byte[][][] videoPosSalPimenta; // resultado após a aplicação do primeiro filtro (Sal e Pimenta)
    private byte[][][] videoPosCorrecaoBorroes; // resultado final, após o segundo filtro (temporal/borrões)
    private int limiteInferior; // O índice do primeiro frame do chunk que a thread vai processar
    private int limiteSuperior;// O índice do último frame (não inclusivo) do chunk que a thread vai processar
    private int tamanhoLadoMascara; // Tamanho do lado da máscara (kernel) do filtro espacial, ex: 3 para uma máscara 3x3
    private TipoDeCalculo tipoDeCalculoSalPimenta; // define se o filtro de sal e pimenta usará Média ou Mediana
    private TipoDeCalculo tipoDeCalculoBorrao; // define o cálculo para o filtro temporal

    // Construtor principal, usado para criar o DTO inicial com o vídeo original
    public VideoDTO(byte[][][] videoPreProcessamento) {
        this.videoPreProcessamento = videoPreProcessamento;
    }

    // Construtor de cópia para as threads
    // Herda as referências dos buffers e parâmetros do DTO principal,
    // mas define limites (inferior/superior) específicos para o chunk daquela thread
    public VideoDTO(VideoDTO videoModelo, int limiteInferior, int limiteSuperior) {
        this.videoPreProcessamento = videoModelo.getVideoPreProcessamento();
        this.videoPosSalPimenta = videoModelo.getVideoPosSalPimenta();
        this.videoPosCorrecaoBorroes = videoModelo.getVideoPosCorrecaoBorroes();
        this.tamanhoLadoMascara = videoModelo.getTamanhoLadoMascara();
        this.tipoDeCalculoSalPimenta = videoModelo.getTipoDeCalculoSalPimenta();
        this.tipoDeCalculoBorrao = videoModelo.getTipoDeCalculoBorrao();
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
    }

    public byte[][][] getVideoPreProcessamento() {
        return videoPreProcessamento;
    }

    public byte[][][] getVideoPosSalPimenta() {
        return videoPosSalPimenta;
    }

    public void setVideoPosSalPimenta(byte[][][] videoPosSalPimenta) {
        this.videoPosSalPimenta = videoPosSalPimenta;
    }

    public byte[][][] getVideoPosCorrecaoBorroes() {
        return videoPosCorrecaoBorroes;
    }

    public void setVideoPosCorrecaoBorroes(byte[][][] videoPosCorrecaoBorroes) {
        this.videoPosCorrecaoBorroes = videoPosCorrecaoBorroes;
    }

    public int getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(int limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    public int getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(int limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public int getTamanhoLadoMascara() {
        return tamanhoLadoMascara;
    }

    public void setTamanhoLadoMascara(int tamanhoLadoMascara) {
        this.tamanhoLadoMascara = tamanhoLadoMascara;
    }

    public TipoDeCalculo getTipoDeCalculoSalPimenta() {
        return tipoDeCalculoSalPimenta;
    }

    public void setTipoDeCalculoSalPimenta(TipoDeCalculo tipoDeCalculoSalPimenta) {
        this.tipoDeCalculoSalPimenta = tipoDeCalculoSalPimenta;
    }

    public TipoDeCalculo getTipoDeCalculoBorrao() {
        return tipoDeCalculoBorrao;
    }

    public void setTipoDeCalculoBorrao(TipoDeCalculo tipoDeCalculoBorrao) {
        this.tipoDeCalculoBorrao = tipoDeCalculoBorrao;
    }
}
