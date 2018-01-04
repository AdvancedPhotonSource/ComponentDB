#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from sqlalchemy.orm.exc import NoResultFound

from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.domain import Domain
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from cdb.common.db.entities.allowedEntityTypeDomain import AllowedEntityTypeDomain
from cdb.common.db.impl.entityTypeHandler import EntityTypeHandler

class DomainHandler(CdbDbEntityHandler):

    CATALOG_DOMAIN_NAME = "Catalog"
    INVENTORY_DOMAIN_NAME = "Inventory"
    LOCATION_DOMAIN_NAME = "Location"
    MAARC_DOMAIN_NAME = "MAARC"

    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.entityTypeHandler = EntityTypeHandler()

    def getDomains(self, session):
        return self._getAllDbObjects(session, Domain)

    def findDomainByName(self, session, name):
        return self._findDbObjByName(session, Domain, name)

    def addDomain(self, session, domainName, description):
        self._prepareAddUniqueNameObj(session, Domain, domainName)

        # Create Domain Handler
        dbDomain = Domain(name=domainName)
        if description:
            dbDomain.description = description

        session.add(dbDomain)
        session.flush()
        self.logger.debug('Inserted domain id %s' % dbDomain.id)
        return dbDomain

    def addAllowedEntityType(self, session, domainName, entityTypeName):
        domain = self.findDomainByName(session, domainName)
        entityType = self.entityTypeHandler.findEntityTypeByName(session, entityTypeName)

        dbAllowedEntityTypeDomain = AllowedEntityTypeDomain()

        dbAllowedEntityTypeDomain.domain = domain
        dbAllowedEntityTypeDomain.entityType = entityType


        session.add(dbAllowedEntityTypeDomain)
        session.flush()
        self.logger.debug('Inserted allowed entity type %s for domain handler %s' % (entityTypeName, domainName))

        return dbAllowedEntityTypeDomain

    def getAllowedEntityTypeDomain(self, session, domain_id):
        try:
            query = session.query(AllowedEntityTypeDomain).filter(AllowedEntityTypeDomain.domain_id == domain_id)
            dbAllowedEntityTypeDomain = query.all()
            return dbAllowedEntityTypeDomain
        except NoResultFound, ex:
            entityDisplayName = self._getEntityDisplayName()
            raise ObjectNotFound("No %s with domain id: %s was found." % (entityDisplayName, domain_id))
