package com.e.emailapplication;


import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by Chandan on 02/07/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */

public class MailService {
    // public static final String MAIL_SERVER = "localhost";

    private String toList;
    private String ccList;
    private String bccList;
    private String subject;
    final private static String SMTP_SERVER = "smtp.gmail.com";
    private String from;
    private String txtBody;
    private String htmlBody;
    private String replyToList;
    private ArrayList<Attachment> attachments;
    private boolean authenticationRequired = false;

    public MailService(String from, String toList, String subject, String txtBody, String htmlBody,
                       Attachment attachment) {
        this.txtBody = txtBody;
        this.htmlBody = htmlBody;
        this.subject = subject;
        this.from = from;
        this.toList = toList;
        this.ccList = null;
        this.bccList = null;
        this.replyToList = null;
        this.authenticationRequired = true;
        Log.e("SendMail", "Constructor called");
        this.attachments = new ArrayList<Attachment>();
        if (attachment != null) {
            this.attachments.add(attachment);
        }
    }

    public MailService(String from, String toList, String subject, String txtBody, String htmlBody,
                       ArrayList<Attachment> attachments) {
        this.txtBody = txtBody;
        this.htmlBody = htmlBody;
        this.subject = subject;
        this.from = from;
        this.toList = toList;
        this.ccList = null;
        this.bccList = null;
        this.replyToList = null;
        this.authenticationRequired = true;
        this.attachments = attachments == null ? new ArrayList<Attachment>()
                : attachments;
    }

    public void sendAuthenticated() throws MessagingException {
        authenticationRequired = true;
        send();
    }

    /**
     * Send an e-mail
     *
     * @throws MessagingException
     * @throws AddressException
     */
    public void send() throws AddressException, MessagingException {
        Log.e("SendMail", "sendMail called start");
        Properties props = new Properties();

        // set the host smtp address
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.user", from);

        props.put("mail.smtp.starttls.enable", "true");  // needed for gmail
        props.put("mail.smtp.auth", "true"); // needed for gmail
        props.put("mail.smtp.port", "587");  // gmail smtp port
        props.put("mail.smtp.socketFactory.port", "587");

        /*Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mobile@mydomain.com", "mypassword");
            }
        };*/


        Session session;

        if (authenticationRequired) {
            Authenticator auth = new SMTPAuthenticator();
            props.put("mail.smtp.auth", "true");
            session = Session.getDefaultInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props, null);
        }

        // get the default session
        session.setDebug(true);

        // create message
        Message msg = new MimeMessage(session);

        // set from and to address
        try {
            msg.setFrom(new InternetAddress(from, from));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(from, from)});
        } catch (Exception e) {
            msg.setFrom(new InternetAddress(from));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(from)});
        }

        // set send date
        msg.setSentDate(Calendar.getInstance().getTime());

        // parse the recipients TO address
        StringTokenizer st = new StringTokenizer(toList, ",");
        int numberOfRecipients = st.countTokens();

        InternetAddress[] addressTo = new InternetAddress[numberOfRecipients];

        int i = 0;
        while (st.hasMoreTokens()) {
            addressTo[i++] = new InternetAddress(st
                    .nextToken());
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // parse the replyTo addresses
        if (replyToList != null && !"".equals(replyToList)) {
            st = new java.util.StringTokenizer(replyToList, ",");
            int numberOfReplyTos = st.countTokens();
            InternetAddress[] addressReplyTo = new InternetAddress[numberOfReplyTos];
            i = 0;
            while (st.hasMoreTokens()) {
                addressReplyTo[i++] = new InternetAddress(
                        st.nextToken());
            }
            msg.setReplyTo(addressReplyTo);
        }

        // parse the recipients CC address
        if (ccList != null && !"".equals(ccList)) {
            st = new java.util.StringTokenizer(ccList, ",");
            int numberOfCCRecipients = st.countTokens();

            InternetAddress[] addressCC = new InternetAddress[numberOfCCRecipients];

            i = 0;
            while (st.hasMoreTokens()) {
                addressCC[i++] = new InternetAddress(st
                        .nextToken());
            }

            msg.setRecipients(Message.RecipientType.CC, addressCC);
        }

        // parse the recipients BCC address
        if (bccList != null && !"".equals(bccList)) {
            st = new java.util.StringTokenizer(bccList, ",");
            int numberOfBCCRecipients = st.countTokens();

            InternetAddress[] addressBCC = new InternetAddress[numberOfBCCRecipients];

            i = 0;
            while (st.hasMoreTokens()) {
                addressBCC[i++] = new InternetAddress(st
                        .nextToken());
            }

            msg.setRecipients(Message.RecipientType.BCC, addressBCC);
        }

        // set header
        msg.addHeader("X-Mailer", "MyAppMailer");
        msg.addHeader("Precedence", "bulk");
        // setting the subject and content type
        msg.setSubject(subject);

        Multipart mp = new MimeMultipart("related");

        // set body message
        MimeBodyPart bodyMsg = new MimeBodyPart();
        bodyMsg.setText(txtBody, "iso-8859-1");
        if (attachments.size() > 0)
            htmlBody = htmlBody.replaceAll("#filename#", attachments.get(0).dataSource.getName());
        if (htmlBody.indexOf("#header#") >= 0)
            htmlBody = htmlBody.replaceAll("#header#", attachments.get(1).dataSource.getName());
        if (htmlBody.indexOf("#footer#") >= 0)
            htmlBody = htmlBody.replaceAll("#footer#", attachments.get(2).dataSource.getName());

        bodyMsg.setContent(htmlBody, "text/html");
        mp.addBodyPart(bodyMsg);

        // set attachements if any
        if (attachments != null && attachments.size() > 0) {
            for (i = 0; i < attachments.size(); i++) {
                Attachment a = attachments.get(i);
                BodyPart att = new MimeBodyPart();
                att.setDataHandler(new DataHandler(a.getDataSource()));
                att.setFileName(a.getDataSource().getName());
                att.setHeader("Content-ID", "<" + a.getDataSource().getName() + ">");
                mp.addBodyPart(att);
            }
        }
        msg.setContent(mp);

        // send it
        try {
            /*Transport t = session.getTransport("smtp");
            t.connect("janachandan1@gmail.com", "your_password");
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();*/
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SendMail", e.getMessage());
        }

    }

    /**
     * SimpleAuthenticator is used to do simple authentication when the SMTP
     * server requires it.
     */
    private static class SMTPAuthenticator extends javax.mail.Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            Log.e("SendMail", "getPasswordAuthentication called");
            String username = "your@gmail.com";
            String password = "";

            return new PasswordAuthentication(username, password);
        }
    }

    public String getToList() {
        return toList;
    }

    public void setToList(String toList) {
        this.toList = toList;
    }

    public String getCcList() {
        return ccList;
    }

    public void setCcList(String ccList) {
        this.ccList = ccList;
    }

    public String getBccList() {
        return bccList;
    }

    public void setBccList(String bccList) {
        this.bccList = bccList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTxtBody(String body) {
        this.txtBody = body;
    }

    public void setHtmlBody(String body) {
        this.htmlBody = body;
    }

    public String getReplyToList() {
        return replyToList;
    }

    public void setReplyToList(String replyToList) {
        this.replyToList = replyToList;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
    }

}
