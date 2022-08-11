package bal.projects.services;

import javax.mail.MessagingException;

import bal.projects.entities.User;

public interface INotificationServices {

	public void sendMailWithCode(User user,Boolean withCode) throws MessagingException;
}
