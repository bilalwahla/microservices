package com.df.organization.services;

import com.df.organization.events.source.SimpleSourceBean;
import com.df.organization.model.Organization;
import com.df.organization.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {
  private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

  private final OrganizationRepository orgRepository;

  private final Tracer tracer;

  private final SimpleSourceBean simpleSourceBean;

  @Autowired
  public OrganizationService(OrganizationRepository orgRepository, Tracer tracer, SimpleSourceBean simpleSourceBean) {
    this.orgRepository = orgRepository;
    this.tracer = tracer;
    this.simpleSourceBean = simpleSourceBean;
  }

  public Organization getOrg
      (String organizationId) {
    Span newSpan = tracer.createSpan("getOrgDBCall");

    logger.debug("In the organizationService.getOrg() call");
    try {
      return orgRepository.findById(organizationId);
    } finally {
      newSpan.tag("peer.service", "postgres");
      newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
      tracer.close(newSpan);
    }
  }

  public void saveOrg(Organization org) {
    org.setId(UUID.randomUUID().toString());

    orgRepository.save(org);
    simpleSourceBean.publishOrgChange("SAVE", org.getId());
  }

  public void updateOrg(Organization org) {
    orgRepository.save(org);
    simpleSourceBean.publishOrgChange("UPDATE", org.getId());

  }

  public void deleteOrg(String orgId) {
    orgRepository.delete(orgId);
    simpleSourceBean.publishOrgChange("DELETE", orgId);
  }
}
