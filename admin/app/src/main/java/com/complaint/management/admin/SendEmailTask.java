package com.complaint.management.admin;

import android.os.AsyncTask;
import android.widget.Toast;


import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailTask extends AsyncTask<String, Void, Void> {
    boolean success=false;
    private Context context;

    public SendEmailTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String subject = params[0];
        String message = params[1];
        String recipientEmail = params[2];
        String senderEmail = "amsproject00@gmail.com" ;

        String senderPassword = "aposdpoeglzztyjm";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(senderEmail));
            emailMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);
            Transport.send(emailMessage);
            success=true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Optional: show a confirmation message
        if(success){
            Toast.makeText(context, "Email sent successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Failed to sent email,Please re-try",Toast.LENGTH_SHORT).show();
        }
    }
}

