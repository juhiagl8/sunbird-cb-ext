package org.sunbird.portal.department.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sunbird.common.model.SunbirdApiResp;
import org.sunbird.common.model.SunbirdApiRespContent;
import org.sunbird.common.model.SunbirdApiResultResponse;
import org.sunbird.common.service.OutboundRequestHandlerServiceImpl;
import org.sunbird.common.service.UserUtilityService;
import org.sunbird.common.util.CbExtServerProperties;
import org.sunbird.common.util.Constants;
import org.sunbird.core.logger.CbExtLogger;
import org.sunbird.core.producer.Producer;
import org.sunbird.portal.department.model.DeptPublicInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PortalServiceImpl implements PortalService {

	public static final String NO_RECORDS_EXIST_FOR_USER_ID = "No records exist for UserId: ";
	public static final String LIST_OF_USER_RECORDS = "List of User Records -> ";
	public static final String DEPT_IDS = ", DeptIds: ";
	public static final String DEPARTMENT_NAME = "departmentName";
	private CbExtLogger logger = new CbExtLogger(getClass().getName());
	private ObjectMapper mapper = new ObjectMapper();
	@Autowired
	UserUtilityService userUtilService;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	CbExtServerProperties serverConfig;

	@Autowired
	Producer producer;

	@Autowired
	OutboundRequestHandlerServiceImpl outboundRequestHandlerService;

	@Override
	public List<DeptPublicInfo> getAllDept() {
		List<DeptPublicInfo> deptPublicInfo = new ArrayList<>();
		try {
			Map<String, Object> tenantMap = new HashMap<>();
			tenantMap.put(Constants.IS_TENANT, Boolean.TRUE);
			Map<String, Object> tMap = new HashMap<>();
			tMap.put(Constants.FILTERS, tenantMap);
			tMap.put(Constants.FIELDS, new ArrayList<>(
					Arrays.asList(Constants.ID, Constants.ROOT_ORG_ID, Constants.ORG_NAME, Constants.DESCRIPTION)));
			tMap.put(Constants.LIMIT, 100);
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put(Constants.REQUEST, tMap);
			String serviceURL = serverConfig.getSbUrl() + serverConfig.getSbOrgSearchPath();
			logger.info(String.format("service Url : %s", serviceURL));
			SunbirdApiResp orgResponse = mapper.convertValue(outboundRequestHandlerService
					.fetchResultUsingPost("https://igot-dev.in/api/org/v1/search", requestMap), SunbirdApiResp.class);
			SunbirdApiResultResponse resultResp = orgResponse.getResult().getResponse();
			for (int j = 0; j < resultResp.getContent().size(); j++) {
				DeptPublicInfo dept = new DeptPublicInfo(resultResp.getContent().get(j).getId(),
						resultResp.getContent().get(j).getDescription(), resultResp.getContent().get(j).getRootOrgId(),
						resultResp.getContent().get(j).getOrgName());
				deptPublicInfo.add(dept);
			}
			return deptPublicInfo;
		} catch (Exception e) {
			logger.info("Exception occurred in getDeptNameList");
			logger.error(e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<String> getDeptNameList() {
		try {
			List<String> orgNames = new ArrayList<>();
			int count = 0;
			do {
				Map<String, Object> tenantMap = new HashMap<>();
				tenantMap.put(Constants.IS_TENANT, Boolean.TRUE);
				Map<String, Object> tMap = new HashMap<>();
				tMap.put(Constants.FILTERS, tenantMap);
				tMap.put(Constants.FIELDS, new ArrayList<>(Arrays.asList(Constants.CHANNEL)));
				tMap.put(Constants.LIMIT, 100);
				tMap.put(Constants.OFFSET, orgNames.size());
				Map<String, Object> requestMap = new HashMap<>();
				requestMap.put(Constants.REQUEST, tMap);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(serverConfig.getSbUrl());
				stringBuilder.append(serverConfig.getSbOrgSearchPath());
				String serviceURL = stringBuilder.toString();
				logger.info(String.format("service Url : %s", serviceURL));
				SunbirdApiResp orgResponse = mapper.convertValue(outboundRequestHandlerService.fetchResultUsingPost(
						"https://igot-dev.in/api/org/v1/search", requestMap), SunbirdApiResp.class);
				SunbirdApiResultResponse resultResp = orgResponse.getResult().getResponse();
				count = resultResp.getCount();
				orgNames.addAll(resultResp.getContent().stream().map(SunbirdApiRespContent::getChannel)
						.collect(Collectors.toList()));
			} while (count != orgNames.size());
			return orgNames;
		} catch (Exception e) {
			logger.info("Exception occurred in getDeptNameList");
			logger.error(e);
		}
		return Collections.emptyList();
	}

	@Override
	public DeptPublicInfo searchDept(String deptName) {
		return null;
	}

}