package br.com.rdsv.picpaydesafiobackend.authorization;

public record Authorization(
        String message
) {
    public boolean isAuthorized(){
        return message.equals("Autorizado");
    }
}
