import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleChatClient {
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    public static void main(String[] args) {
        SimpleChatClient client = new SimpleChatClient();
        client.go();
    }

    private void go() {
        JFrame frame = new JFrame("Ludicrously Simple Chat Client");
        JPanel mainpanel = new JPanel();
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outgoing = new JTextField(20);
        JButton sendbutton = new JButton("Send");
        sendbutton.addActionListener(new SendButtonListener());
        mainpanel.add(qScroller);
        mainpanel.add(outgoing);
        mainpanel.add(sendbutton);
        setUpNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        frame.getContentPane().add(BorderLayout.CENTER, mainpanel);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setUpNetworking() {
        try{
            sock = new Socket("127.0.0.1", 5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Networking stablished");
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try{
                writer.println(outgoing.getText());
                writer.flush();
            } catch (Exception ex){
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    private class IncomingReader implements Runnable {
        public void run(){
            String message;
            try{
                while((message = reader.readLine()) != null){
                    System.out.println("read" + message);
                    incoming.append(message + "\n");
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}

