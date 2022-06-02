package org.sunbird.user.registration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunbird.common.util.CbExtServerProperties;
import org.sunbird.common.util.Constants;
import org.sunbird.common.util.NotificationUtil;
import org.sunbird.common.util.PropertiesCache;
import org.sunbird.user.registration.model.UserRegistration;

@Service
public class UserRegistrationNotificationServiceImpl implements UserRegistrationNotificationService {

	@Autowired
	CbExtServerProperties serverProperties;
	
	@Autowired
	NotificationUtil notificationUtil;
	
	@Override
	public void sendNotification(UserRegistration userRegistration) {
		List<String> sendTo = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;

			{
				add(userRegistration.getEmail());
			}
		};

		Map<String, Object> notificationObj = new HashMap<>();
		notificationObj.put("mode", Constants.EMAIL);
		notificationObj.put("deliveryType", Constants.MESSAGE);
		notificationObj.put("config", new HashMap<String, Object>() {
			{
				put(Constants.SUBJECT, serverProperties.getUserRegistrationSubject());
			}
		});
		notificationObj.put("ids", sendTo);
		notificationObj.put(Constants.TEMPLATE,
				notificationMessage(userRegistration.getStatus(), userRegistration.getRegistrationCode()));

		if (notificationObj.get(Constants.TEMPLATE) != null) {
			notificationUtil.sendNotification(sendTo, notificationObj,
					PropertiesCache.getInstance().getProperty(Constants.SENDER_MAIL),
					PropertiesCache.getInstance().getProperty(Constants.NOTIFICATION_HOST)
							+ PropertiesCache.getInstance().getProperty(Constants.NOTIFICATION_ENDPOINT));
		}
	}
	
	private Map<String, Object> notificationMessage(String status, String regCode) {
		Map<String, Object> template = new HashMap<>();
		template.put(Constants.ID, Constants.USER_REGISTERATION_TEMPLATE);
		Map<String, Object> params = new HashMap<>();
		params.put(Constants.STATUS, serverProperties.getUserRegistrationStatus().replace("{status}", status));
		params.put(Constants.TITLE, serverProperties.getUserRegistrationTitle().replace("{status}", status));
		template.put("params", params);
		switch (status) {
		case "WF_INITIATED":
			params.put(Constants.TITLE, serverProperties.getUserRegistrationThankyouMessage());
			params.put(Constants.DESCRIPTION, serverProperties.getUserRegistrationInitiatedMessage()
					.replace("{regCode}", "<b>" + regCode + "</b>"));
			break;
		case "WF_APPROVED":
			params.put(Constants.DESCRIPTION, serverProperties.getUserRegistrationApprovedMessage());
			params.put("btn-url", serverProperties.getUserRegistrationDomainName());
			params.put("btn-name", serverProperties.getUserRegisterationButtonName());
			break;
		case "WF_DENIED":
			break;
		case "FAILED":
			params.put(Constants.STATUS, serverProperties.getUserRegistrationFailedMessage());
			break;

		default:
			template = null;
			break;
		}
		return template;
	}

}
