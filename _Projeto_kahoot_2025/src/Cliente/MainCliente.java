package Cliente;


import GUI.Codigo_PINInicial_Frame;

public class MainCliente {
	
	
	public static void main(String[] args) {
		
		if (args.length == 5) {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            String pin = args[2];
            String equipa = args[3];
            String nome = args[4];

            System.out.printf("A ligar a %s:%d | Sala: %s | Equipa: %s | Jogador: %s%n",
                    host, port, pin, equipa, nome);

            Cliente cliente = new Cliente(host, port, pin, equipa, nome);
            cliente.ligar();
            return;
        }
     
        new Codigo_PINInicial_Frame();

    }
	
}
