package com.df.organization.repository;

import com.df.organization.model.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization, String> {
  Organization findById(String organizationId);
}
