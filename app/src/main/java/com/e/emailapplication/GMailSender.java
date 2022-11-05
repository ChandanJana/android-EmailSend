package com.e.emailapplication;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by Chandan on 02/07/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */

public class GMailSender {
    private String mailhost = "smtp.gmail.com";
    private Session session;
    private Multipart _multipart = null;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender() {

        Properties props = new Properties();
        //props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", mailhost);
        //props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        //props.put("mail.smtp.user", user);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        //props.put("mail.smtp.socketFactory.fallback", "false");
        //props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
            }
        });
        Log.e("SendMail", "Constructor called");
    }

    public void addAttachment(String filename,String subject) throws Exception {
        if (_multipart == null)
            _multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        _multipart.addBodyPart(messageBodyPart);

        BodyPart messageBodyPart2 = new MimeBodyPart();
        messageBodyPart2.setText(subject);

        _multipart.addBodyPart(messageBodyPart2);
    }


    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        try{
            Log.e("SendMail", "sendMail called start");
            //session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            //message.setSender(new InternetAddress(sender));
            message.setFrom(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
//            if (_multipart != null)
//                message.setContent(_multipart);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            /*Transport t = session.getTransport("smtp");
            t.connect("janachandan1@gmail.com", "your_password");
            t.sendMessage(message, message.getAllRecipients());
            t.close();*/
            Transport.send(message);
            Log.e("SendMail", "sendMail called end");
        }catch(Exception e){
            Log.e("SendMail", e.getMessage());

        }
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
