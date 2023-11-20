package chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;

class chatServidor extends JFrame{
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	
	chatServidor(){
		super("JavaZap messenger!! ");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			sendMessage(event.getActionCommand());	
			userText.setText("");
			}
		});
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}
	
	//O método `startRunning ( )` é o ponto de entrada do programa. Ele configura o socker
	//do servidor e aguarda as conexões recebidas usando um loop while.
	//Quando o conexao e estabelecida ela chama `setupStreams ( )` para inicializar os fluxos de entrada e saída e inicia
	//o loop `whileChatting ( )` para fazer a comunicação entre o servidor e o cliente.
	
	public void startRunning(){
		try{
			server = new ServerSocket(6789,100);
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
					
				}catch(EOFException bleh){
					showMessage("\n Servidor terminou a conexão!!");
				}finally{
					closeCrap();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private void waitForConnection() throws IOException{
		showMessage("Esperando alguem se conectar.. \n");
		connection = server.accept();
		showMessage("Agora conectado ao " + connection.getInetAddress().getHostName());
		
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n AS streams estao prontas!! ");
	}
	
	private void whileChatting() throws IOException{
		String message = "Agora você está conectado!!";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n "+ message);
			}catch(ClassNotFoundException c){
				showMessage("\n ... ");
			}
			
		}while(!message.equals("CLIENTE - END"));
	}
	
	private void closeCrap(){
		showMessage("\n Fechando conexão.... \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException bruh){
			bruh.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			output.writeObject("SERVIDOR - "+message);
			output.flush();
			showMessage("\n SERVIDOR - "+ message);
		}catch(Exception a){
			chatWindow.append("\n ERROR: nda pra enviar a mensagem!!!!");
			
		}
	}
	
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(text);
					}
				}
				);
	}
	//O método `ableToType ( )` ativa ou desativa a entrada do usuário, definindo a propriedade `editable` do campo `userText`
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
						
					}
				}
					);
	}
}
	//O método `main ( )` cria uma instância da classe `chatServidor`, inicia a execução do 
	//servidor, define a operação de fechamento padrão para a GUI e inicia a execução do servidor novamente.
public class Servidor {
	public static void main(String[] args) {
		chatServidor sally = new chatServidor();
		sally.startRunning();
		sally.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sally.startRunning();
	}
}

