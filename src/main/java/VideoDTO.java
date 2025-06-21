public class VideoDTO {

    private final byte[][][] videoPreProcessamento;
    private byte[][][] videoPosSalPimenta;
    private byte[][][] videoPosCorrecaoBorroes;

    private int limiteInferior;
    private int limiteSuperior;

    private int tamanhoLadoMascara;

    private TipoDeCalculo tipoDeCalculoSalPimenta;
    private TipoDeCalculo tipoDeCalculoBorrao;


    public VideoDTO(byte[][][] videoPreProcessamento) {
        this.videoPreProcessamento = videoPreProcessamento;
    }

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
