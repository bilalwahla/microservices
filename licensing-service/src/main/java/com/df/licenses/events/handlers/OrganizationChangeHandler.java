package com.df.licenses.events.handlers;

import com.df.licenses.events.CustomChannels;
import com.df.licenses.events.models.OrganizationChangeModel;
import com.df.licenses.repository.OrganizationRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;


@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler {
  private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

  private final OrganizationRedisRepository organizationRedisRepository;

  @Autowired
  public OrganizationChangeHandler(OrganizationRedisRepository organizationRedisRepository) {
    this.organizationRedisRepository = organizationRedisRepository;
  }

  @StreamListener("inboundOrgChanges")
  public void loggerSink(OrganizationChangeModel orgChange) {
    logger.debug("Received a message of type " + orgChange.getType());
    switch (orgChange.getAction()) {
      case "GET":
        logger.debug("Received a GET event from the organization service for organization id {}", orgChange.getOrganizationId());
        break;
      case "SAVE":
        logger.debug("Received a SAVE event from the organization service for organization id {}", orgChange.getOrganizationId());
        break;
      case "UPDATE":
        logger.debug("Received a UPDATE event from the organization service for organization id {}", orgChange.getOrganizationId());
        organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
        break;
      case "DELETE":
        logger.debug("Received a DELETE event from the organization service for organization id {}", orgChange.getOrganizationId());
        organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
        break;
      default:
        logger.error("Received an UNKNOWN event from the organization service of type {}", orgChange.getType());
        break;

    }
  }

}
