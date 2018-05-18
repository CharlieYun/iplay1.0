package com.iminer.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

/**
 * 发送邮件工具类
 * @author Administrator
 *
 */
public class SendEmailUtil{
	/**
	 * 
	 * @param emailSubject 邮件标题
	 * @param emailContent 邮件内容
	 * @param destEmailUrl 邮件目标地址
	 */
	public static void sendEmail(String emailSubject,String emailContent,List<String> emailUrlList){
		 	Properties props = new Properties();  
	        props.setProperty("mail.smtp.auth", "true"); 
	        props.setProperty("mail.transport.protocol", "smtp");  
	        Session session = Session.getInstance(props);  
	        session.setDebug(true);  
	        Message msg = new MimeMessage(session);  
	        try {  
	            //邮件的内容  
	        	//给消息对象设置内容
	        	BodyPart mdp=new MimeBodyPart();//新建一个存放信件内容的BodyPart对象
	        	mdp.setContent(emailContent,"text/html;charset=gb2312");//给BodyPart对象设置内容和格式/编码方式
	        	Multipart mm=new MimeMultipart();//新建一个MimeMultipart对象用来存放BodyPart对象(事实上可以存放多个)
	        	mm.addBodyPart(mdp);//将BodyPart加入到MimeMultipart对象中(可以加入多个BodyPart)
	        	msg.setContent(mm);//把mm作为消息对象的内容
	            //邮件标题  
	            msg.setSubject(emailSubject);  
	            //发件人的地址  
	            msg.setFrom(new InternetAddress("testing@iminer.com"));  
	            //设置发送时间  
	            msg.setSentDate(new Date());  
	            //建立发送对象Transport  
	            Transport transport = session.getTransport();  
	            //使用——用户名和密码连接邮箱  
	            transport.connect("smtp.qiye.163.com",25,"testing@iminer.com", "iminer2@2test");
	            //发送对象的地址 
	            List<Address> addressList=new ArrayList<Address>();
	            for(String emailUrl:emailUrlList){
	            	if(!StringUtils.isEmpty(emailUrl)){
	            		addressList.add(new InternetAddress(emailUrl));
	            	}
	            }
	            if(addressList.size()>0){
	            	transport.sendMessage(msg, addressList.toArray(new Address[addressList.size()]));
	            }
	            transport.close();  
	        } catch (MessagingException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	}
	
}
