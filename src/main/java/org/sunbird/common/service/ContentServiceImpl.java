package org.sunbird.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunbird.common.model.SunbirdApiHierarchyResultBatch;
import org.sunbird.common.model.SunbirdApiResp;
import org.sunbird.common.util.CbExtServerProperties;
import org.sunbird.core.logger.CbExtLogger;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ContentServiceImpl implements ContentService {

	private CbExtLogger logger = new CbExtLogger(getClass().getName());

	@Autowired
	private OutboundRequestHandlerServiceImpl outboundRequestHandlerService;

	@Autowired
	CbExtServerProperties serverConfig;

	@Autowired
	private ObjectMapper mapper;

	public SunbirdApiResp getHeirarchyResponse(String contentId) {
		StringBuilder url = new StringBuilder();
		url.append(serverConfig.getContentHost()).append(serverConfig.getHierarchyEndPoint()).append("/" + contentId)
				.append("?hierarchyType=detail");
		SunbirdApiResp response = mapper.convertValue(outboundRequestHandlerService.fetchResult(url.toString()),
				SunbirdApiResp.class);
		if (response.getResponseCode().equalsIgnoreCase("Ok")) {
			return response;
		}

		return null;
	}

	public List<String> getParticipantsList(List<String> batchIdList) {
		List<String> participantList = new ArrayList<String>();
		StringBuilder url = new StringBuilder();
		url.append(serverConfig.getCourseServiceHost()).append(serverConfig.getParticipantsEndPoint());

		Map<String, Object> requestBody = new HashMap<String, Object>();
		Map<String, Object> request = new HashMap<String, Object>();
		Map<String, Object> batch = new HashMap<String, Object>();
		batch.put("active", true);
		request.put("batch", batch);
		requestBody.put("request", request);

		for (String batchId : batchIdList) {
			try {
				batch.put("batchId", batchId);
				SunbirdApiResp response = mapper.convertValue(outboundRequestHandlerService.fetchResult(url.toString()),
						SunbirdApiResp.class);
				if (response.getResponseCode().equalsIgnoreCase("Ok")) {
					SunbirdApiHierarchyResultBatch batchResp = response.getResult().getBatch();
					if (batchResp != null && batchResp.getCount() > 0) {
						participantList.addAll(batchResp.getParticipants());
					}
				} else {
					logger.warn("Failed to get participants for BatchId - " + batchId);
					logger.warn("Error Response -> " + mapper.writeValueAsString(response));
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}

		return participantList;
	}
}
