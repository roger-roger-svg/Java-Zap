package chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//`chatCliente` estende a classe `JFrame` para criar uma interface gráfica do usuário ( GUI )

class chatCliente extends JFrame{
	private static final long serialVersionUID = 1L;
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	chatCliente(String host){
		super("JAVAZAP messenger");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow),BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
		
	}
	//start running é o principal método que executa o aplicativo de bate-papo.
	
	// conecta ao servidor usando o método `connectToServer`, que cria um novo objeto `Socket` 
	//e estabelece uma conexão com o servidor
	
	//`setupStreams` cria objetos `ObjectInputStream` e `ObjectOutputStream` para enviar e receber mensagens de e para o servidor.
	//` whileChatting` é um loop que ouve as mensagens recebidas do servidor usando o objeto `input`
	//a mensagem aparece noJText area usando o metodo "showMessage"
	// o loop continua ate a mensagem SERVER end
	//O "showMessage" método usa o SwingUtilities.invokeLater método para atualizar a GUI no encadeamento de expedição de eventos
	
	
	void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException bleh){
			showMessage("\n Cliente Terminou a conexão");
		}catch(IOException blehbleh){
			blehbleh.printStackTrace();
		}finally{
			closeLixo();
		}
	}
	
	private void connectToServer() throws IOException{
		showMessage("Tentando se conectar ... \n");
		connection = new Socket(InetAddress.getByName(serverIP),6789);
		showMessage("Conectado ao: "+connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n As streams estao prontas \n");
	}
	//define a propriedade editável do JTextField como true ou false, se o cliente tem permissão para digitar no campo de texto.
	
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n "+message);
			}catch(ClassNotFoundException b){
				showMessage("\n nao pode esse tipo de mesnsagem! ");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	private void closeLixo(){
		showMessage("\n closing lixo!! ");
		ableToType(false);
		
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException fml){
			fml.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENTE -"+ message);
			output.flush();
			showMessage("\n CLIENTE - "+ message);
			
		}catch(IOException bunda){
			chatWindow.append("\n Algo confuso enviando mensagem");
		}
	}
	
	private void showMessage(final String m){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(m);
					}
				});
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
					}
				});
	}
}
	//O main método cria um novo chatCliente objeto, passando no endereço IP do servidor como parâmetro
	//ele define a operação de fechamento padrão para sair do programa quando a GUI é fechada e inicia o app
	//de bate-papo chamando o startRunning método.

public class Cliente{
	public static void main(String[] args) {
		chatCliente charlie = new chatCliente("127.0.0.1");
		charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		charlie.startRunning();
		
		
	}
}
