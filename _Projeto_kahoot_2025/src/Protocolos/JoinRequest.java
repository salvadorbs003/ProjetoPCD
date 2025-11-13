package Protocolos;

public class JoinRequest {
    private String pinSala;
    private String jogadorNome;
    private String nomeEquipa;

    public JoinRequest(String pinSala, String jogadorNome, String nomeEquipa){
        this.pinSala = pinSala;
        this.jogadorNome = jogadorNome;
        this.nomeEquipa = nomeEquipa;
    }

    public String getPinSala() {
        return pinSala;
    }

    public String getJogadorNome() {
        return jogadorNome;
    }

    public String getNomeEquipa() {
        return nomeEquipa;
    }

    //turns the JoinRequest object back into the raw 
    //text line that the socket protocol expects
    public String serialize() {
    return "JOIN " + pinSala + " " + nomeEquipa + " " + jogadorNome;
    }


//Static helpers
    
    //checks if the JoinReq is a JoinReq
    public static boolean matches(String linha){
        return linha != null && linha.startsWith("JOIN ");
    }

    //Forms an object of type JoinReq
    public static JoinRequest formJoin(String linha){
        if (!matches(linha)) {
            throw new IllegalArgumentException("Mensagem não é JOIN");
        }

        String[] partes = linha.trim().split("\\s+", 4);
        if (partes.length != 4) {
            throw new IllegalArgumentException("Formato JOIN inválido");
        }

        return new JoinRequest(partes[1], partes[3], partes[2]);
    }

//ToString method
    @Override
    public String toString() {
        return "JoinRequest{" +
                "pinSala='" + pinSala + '\'' +
                ", nomeEquipa='" + nomeEquipa + '\'' +
                ", jogadorNome='" + jogadorNome + '\'' +
                '}';
    }

}